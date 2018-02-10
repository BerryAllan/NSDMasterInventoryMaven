package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ControllerItemManager
{
	@FXML
	ListView<String> listView;

	@FXML
	private void initialize()
	{
		refreshListView();
	}

	@FXML
	private void removeItem()
	{
		try
		{
			if (!listView.getSelectionModel().getSelectedItem().equals("Default"))
			{
				File file =
						new File(this.getClass().getResource("/prefabs").getPath() + "/" + listView.getSelectionModel().getSelectedItem());
				FileUtils.deleteDirectory(file);
				listView.getItems().remove(listView.getSelectionModel().getSelectedItem());
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@FXML
	private void showItemBuilderWindow()
	{
		try
		{
			ControllerItemBuilder.initializationString = "";
			Parent root = FXMLLoader.load(this.getClass().getResource("/scenes/ItemBuilder.fxml"));
			Stage stage = new Stage();
			stage.setTitle("Item Prefab Builder");
			Image img = new Image(this.getClass().getResourceAsStream("/images/icon.png"));
			stage.getIcons().add(img);
			stage.setResizable(false);
			stage.setScene(new Scene(root, 900, 550));
			stage.setOnHidden(event -> refreshListView());
			stage.show();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@FXML
	private void showEditorWindow()
	{
		try
		{
			ControllerItemBuilder.initializationString = listView.getSelectionModel().getSelectedItem();
			Parent root = FXMLLoader.load(this.getClass().getResource("/scenes/ItemBuilder.fxml"));
			Stage stage = new Stage();
			stage.setTitle("Item Prefab Builder");
			Image img = new Image(this.getClass().getResourceAsStream("/images/icon.png"));
			stage.getIcons().add(img);
			stage.setResizable(false);
			stage.setScene(new Scene(root, 900, 550));
			stage.setOnHidden(event -> refreshListView());
			stage.show();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void refreshListView()
	{
		listView.getItems().clear();

		for (File file : Objects.requireNonNull(new File(this.getClass().getResource("/prefabs").getPath()).listFiles()))
		{
			listView.getItems().add(FilenameUtils.getBaseName(file.getPath()));
		}
	}
}
