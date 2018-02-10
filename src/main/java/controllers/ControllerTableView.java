package controllers;

import item.Item;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.commons.io.FilenameUtils;
import primary.Main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public class ControllerTableView
{
	@FXML
	TabPane tabPane;
	@FXML
	private TextField searchField;

	private ObservableList<Item> masterData;

	/**
	 * Just add some sample data in the constructor.
	 */
	public ControllerTableView()
	{
	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 * <p>
	 * Initializes the table columns and sets up sorting and filtering.
	 */
	@FXML
	private void initialize()
	{
		for (File file : Objects.requireNonNull(new File(Main.DATABASE_DIRECTORY).listFiles()))
		{
			Tab tab = new Tab(FilenameUtils.getBaseName(file.getPath()));
			tabPane.getTabs().add(tab);
			writeToTable(file, tab);
		}
	}

	private void writeToTable(File file, Tab tab)
	{
		try
		{
			Main main = new Main();
			String prefab = Main.getPrefabFromDatabase(file);
			masterData = FXCollections.observableArrayList(main.getItems(file, prefab));

			TableView<Item> theTable = new TableView<>();

			theTable.getColumns().clear();

			InputStream inputStream = this.getClass().getResourceAsStream("/prefabs/" + prefab + "/" + prefab + "Fields.properties");
			Properties properties = new Properties();
			properties.load(inputStream);

			TableColumn<Item, String> indexColumn = new TableColumn<>("#");
			indexColumn.setSortable(false);
			indexColumn.setCellValueFactory(
					column -> new ReadOnlyObjectWrapper<>(String.valueOf(theTable.getItems().indexOf(column.getValue()) + 1)));
			theTable.getColumns().add(indexColumn);

			for (int i = 0; i < properties.size(); i++)
			{
				if (!properties.getProperty("field" + i).isEmpty()) ;
				{
					TableColumn<Item, String> tableColumn = new TableColumn<>(properties.getProperty("field" + i));
					theTable.getColumns().add(tableColumn);

					int finalI = i;
					tableColumn.setCellValueFactory(item -> item.getValue().getPropFields().get(finalI));
				}
			}

			// 1. Wrap the ObservableList in a FilteredList (initially display all data).
			FilteredList<Item> filteredData = new FilteredList<>(masterData, p -> true);

			// 2. Set the filter Predicate whenever the filter changes.
			searchField.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(item ->
			{
				// If filter text is empty, display all persons.
				if (newValue == null || newValue.isEmpty())
				{
					return true;
				}

				// Compare first name and last name of every person with filter text.
				String lowerCaseFilter = newValue.toLowerCase();

				for (int i = 0; i < item.getFields().size(); i++)
				{
					if (item.getFields().get(i).toLowerCase().contains(lowerCaseFilter))
					{
						return true; // Filter matches first name.
					}
				}
				return false; // Does not match.
			}));

			// 3. Wrap the FilteredList in a SortedList.
			SortedList<Item> sortedData = new SortedList<>(filteredData);

			// 4. Bind the SortedList comparator to the TableView comparator.
			sortedData.comparatorProperty().bind(theTable.comparatorProperty());

			// 5. Add sorted (and filtered) data to the table.
			theTable.setItems(sortedData);

			tab.setContent(theTable);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}

