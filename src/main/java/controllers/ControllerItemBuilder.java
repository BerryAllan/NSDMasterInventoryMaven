package controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;


public class ControllerItemBuilder
{
	public static String initializationString = "";
	@FXML
	TextField itemName;
	@FXML
	TextField field0Text;
	@FXML
	TextField field1Text;
	@FXML
	TextField field2Text;
	@FXML
	TextField field3Text;
	@FXML
	TextField field4Text;
	@FXML
	TextField field5Text;
	@FXML
	TextField field6Text;
	@FXML
	TextField field7Text;
	@FXML
	TextField field8Text;
	@FXML
	TextField field9Text;
	@FXML
	TextField field10Text;
	@FXML
	TextField field11Text;
	@FXML
	TextField field12Text;
	@FXML
	TextField field13Text;
	@FXML
	TextField field14Text;
	@FXML
	TextField field15Text;
	@FXML
	TextField field16Text;
	@FXML
	TextField field17Text;
	@FXML
	TextField field18Text;

	@FXML
	ChoiceBox<String> field0Type;
	@FXML
	ChoiceBox<String> field1Type;
	@FXML
	ChoiceBox<String> field2Type;
	@FXML
	ChoiceBox<String> field3Type;
	@FXML
	ChoiceBox<String> field4Type;
	@FXML
	ChoiceBox<String> field5Type;
	@FXML
	ChoiceBox<String> field6Type;
	@FXML
	ChoiceBox<String> field7Type;
	@FXML
	ChoiceBox<String> field8Type;
	@FXML
	ChoiceBox<String> field9Type;
	@FXML
	ChoiceBox<String> field10Type;
	@FXML
	ChoiceBox<String> field11Type;
	@FXML
	ChoiceBox<String> field12Type;
	@FXML
	ChoiceBox<String> field13Type;
	@FXML
	ChoiceBox<String> field14Type;
	@FXML
	ChoiceBox<String> field15Type;
	@FXML
	ChoiceBox<String> field16Type;
	@FXML
	ChoiceBox<String> field17Type;
	@FXML
	ChoiceBox<String> field18Type;

	@FXML
	TextField field0SortBy;
	@FXML
	TextField field1SortBy;
	@FXML
	TextField field2SortBy;
	@FXML
	TextField field3SortBy;
	@FXML
	TextField field4SortBy;
	@FXML
	TextField field5SortBy;
	@FXML
	TextField field6SortBy;
	@FXML
	TextField field7SortBy;
	@FXML
	TextField field8SortBy;
	@FXML
	TextField field9SortBy;
	@FXML
	TextField field10SortBy;
	@FXML
	TextField field11SortBy;
	@FXML
	TextField field12SortBy;
	@FXML
	TextField field13SortBy;
	@FXML
	TextField field14SortBy;
	@FXML
	TextField field15SortBy;
	@FXML
	TextField field16SortBy;
	@FXML
	TextField field17SortBy;
	@FXML
	TextField field18SortBy;

	@FXML
	Button field0TypeEdit;
	@FXML
	Button field1TypeEdit;
	@FXML
	Button field2TypeEdit;
	@FXML
	Button field3TypeEdit;
	@FXML
	Button field4TypeEdit;
	@FXML
	Button field5TypeEdit;
	@FXML
	Button field6TypeEdit;
	@FXML
	Button field7TypeEdit;
	@FXML
	Button field8TypeEdit;
	@FXML
	Button field9TypeEdit;
	@FXML
	Button field10TypeEdit;
	@FXML
	Button field11TypeEdit;
	@FXML
	Button field12TypeEdit;
	@FXML
	Button field13TypeEdit;
	@FXML
	Button field14TypeEdit;
	@FXML
	Button field15TypeEdit;
	@FXML
	Button field16TypeEdit;
	@FXML
	Button field17TypeEdit;
	@FXML
	Button field18TypeEdit;
	@FXML
	Button saveItemPrefab;

	private ArrayList<TextField> texts = new ArrayList<>();
	private ArrayList<ChoiceBox<String>> types = new ArrayList<>();
	private ArrayList<TextField> sortBys = new ArrayList<>();
	private ArrayList<Button> editButtons = new ArrayList<>();

	private EventHandler eventHandlerEditButton = event ->
	{
		try
		{
			Button button = (Button) event.getSource();
			String str = button.getId();
			str = str.replaceAll("\\D+", "");
			int fieldNo = Integer.valueOf(str);
			ChoiceBox<String> choiceBox = types.get(fieldNo);

			if (choiceBox.getValue().equals("ComboBox") && !itemName.getText().isEmpty())
			{
				ControllerComboBoxBuilder.initString = itemName.getText();
				ControllerComboBoxBuilder.initTypeNoString = "comboBox" + fieldNo;

				Parent root = FXMLLoader.load(this.getClass().getResource("/scenes/ComboBoxBuilder.fxml"));
				Stage stage = new Stage();
				stage.setTitle("Combo Box " + (fieldNo + 1) + " Builder");
				Image img = new Image(this.getClass().getResourceAsStream("/images/icon.png"));
				stage.getIcons().add(img);
				stage.setResizable(false);
				stage.setScene(new Scene(root, 600, 400));
				stage.show();
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	};

	private EventHandler eventHandleTypeChange = event ->
	{
		ChoiceBox choiceBox = (ChoiceBox) event.getSource();
		setEdits(choiceBox);
	};

	@FXML
	private void initialize()
	{
		addToLists();
		try
		{
			if (!initializationString.isEmpty())
			{
				setFieldNamesInit();
				setFieldTypesInit();
				setFieldSortBysInit();
			}

			for (int i = 0; i < types.size(); i++)
			{
				types.get(i).addEventHandler(ActionEvent.ANY, eventHandleTypeChange);
				setEdits(types.get(i));
			}

			for (int i = 0; i < editButtons.size(); i++)
			{
				editButtons.get(i).addEventHandler(ActionEvent.ANY, eventHandlerEditButton);
			}
		} catch (NullPointerException | IOException e)
		{
		}
	}

	@FXML
	private void onSaveButtonClick()
	{
		if (!itemName.getText().isEmpty() && itemName.getText() != null)
		{
			storeFieldNames();
			storeFieldTypes();
			storeFieldSortBys();
		}

		Stage stage = (Stage) itemName.getScene().getWindow();
		stage.close();
	}

	private void setFieldNamesInit() throws IOException
	{
		InputStream inputStream = this.getClass()
				.getResourceAsStream("/prefabs/" + initializationString + "/" + initializationString + "Fields" + ".properties");
		Properties properties = new Properties();
		properties.load(inputStream);

		itemName.setText(initializationString);

		for (int i = 0; i < texts.size(); i++)
		{
			texts.get(i).setText(properties.getProperty("field" + i));
		}

		inputStream.close();
	}

	private void setFieldTypesInit() throws IOException
	{
		InputStream inputStream = this.getClass()
				.getResourceAsStream("/prefabs/" + initializationString + "/" + initializationString + "FieldTypes" + ".properties");
		Properties properties = new Properties();
		properties.load(inputStream);

		itemName.setText(initializationString);

		for (int i = 0; i < types.size(); i++)
		{
			types.get(i).setValue(properties.getProperty("field" + i));
		}

		inputStream.close();
	}

	private void setFieldSortBysInit() throws IOException
	{
		InputStream inputStream = this.getClass()
				.getResourceAsStream("/prefabs/" + initializationString + "/" + initializationString + "FieldSortBys" + ".properties");
		Properties properties = new Properties();
		properties.load(inputStream);

		itemName.setText(initializationString);

		for (int i = 0; i < sortBys.size(); i++)
		{
			sortBys.get(i).setText(properties.getProperty("field" + i));
		}

		inputStream.close();
	}

	private void storeFieldNames()
	{
		Properties prop = new Properties();
		for (int i = 0; i < texts.size(); i++)
		{
			prop.setProperty("field" + i, texts.get(i).getText());
		}

		try
		{
			File directory = new File(this.getClass().getResource("/prefabs").getPath() + "/" + itemName.getText());
			if (!directory.exists()) directory.mkdirs();

			OutputStream os = new FileOutputStream(
					this.getClass().getResource("/prefabs").getPath() + "/" + itemName.getText() + "/" + itemName
							.getText() + "Fields" + ".properties");
			prop.store(os, null);
			os.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void storeFieldTypes()
	{
		Properties propFieldType = new Properties();
		for (int i = 0; i < types.size(); i++)
		{
			propFieldType.setProperty("field" + i, types.get(i).getValue() != null ? types.get(i).getValue() : "TextField");
		}

		try
		{
			File directory = new File(this.getClass().getResource("/prefabs").getPath() + "/" + itemName.getText());
			if (!directory.exists()) directory.mkdirs();

			OutputStream os = new FileOutputStream(
					this.getClass().getResource("/prefabs").getPath() + "/" + itemName.getText() + "/" + itemName
							.getText() + "FieldTypes" + ".properties");
			propFieldType.store(os, null);
			os.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	private void storeFieldSortBys()
	{
		Properties prop = new Properties();
		for (int i = 0; i < sortBys.size(); i++)
		{
			prop.setProperty("field" + i,
					sortBys.get(i).getText() == null || sortBys.get(i).getText().isEmpty() ? "0" : sortBys.get(i).getText());
		}

		try
		{
			File directory = new File(this.getClass().getResource("/prefabs").getPath() + "/" + itemName.getText());
			if (!directory.exists()) directory.mkdirs();

			OutputStream os = new FileOutputStream(
					this.getClass().getResource("/prefabs").getPath() + "/" + itemName.getText() + "/" + itemName
							.getText() + "FieldSortBys" + ".properties");
			prop.store(os, null);
			os.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void setEdits(ChoiceBox choiceBox)
	{
		if (choiceBox.getValue().equals("ComboBox"))
		{
			String str = choiceBox.getId();
			str = str.replaceAll("\\D+", "");
			int fieldNo = Integer.valueOf(str);
			editButtons.get(fieldNo).setDisable(false);
		}
		else
		{
			String str = choiceBox.getId();
			str = str.replaceAll("\\D+", "");
			int fieldNo = Integer.valueOf(str);
			editButtons.get(fieldNo).setDisable(true);
		}
	}

	private void addToLists()
	{
		texts.add(field0Text);
		texts.add(field1Text);
		texts.add(field2Text);
		texts.add(field3Text);
		texts.add(field4Text);
		texts.add(field5Text);
		texts.add(field6Text);
		texts.add(field7Text);
		texts.add(field8Text);
		texts.add(field9Text);
		texts.add(field10Text);
		texts.add(field11Text);
		texts.add(field12Text);
		texts.add(field13Text);
		texts.add(field14Text);
		texts.add(field15Text);
		texts.add(field16Text);
		texts.add(field17Text);
		texts.add(field18Text);

		types.add(field0Type);
		types.add(field1Type);
		types.add(field2Type);
		types.add(field3Type);
		types.add(field4Type);
		types.add(field5Type);
		types.add(field6Type);
		types.add(field7Type);
		types.add(field8Type);
		types.add(field9Type);
		types.add(field10Type);
		types.add(field11Type);
		types.add(field12Type);
		types.add(field13Type);
		types.add(field14Type);
		types.add(field15Type);
		types.add(field16Type);
		types.add(field17Type);
		types.add(field18Type);

		sortBys.add(field0SortBy);
		sortBys.add(field1SortBy);
		sortBys.add(field2SortBy);
		sortBys.add(field3SortBy);
		sortBys.add(field4SortBy);
		sortBys.add(field5SortBy);
		sortBys.add(field6SortBy);
		sortBys.add(field7SortBy);
		sortBys.add(field8SortBy);
		sortBys.add(field9SortBy);
		sortBys.add(field10SortBy);
		sortBys.add(field11SortBy);
		sortBys.add(field12SortBy);
		sortBys.add(field13SortBy);
		sortBys.add(field14SortBy);
		sortBys.add(field15SortBy);
		sortBys.add(field16SortBy);
		sortBys.add(field17SortBy);
		sortBys.add(field18SortBy);

		editButtons.add(field0TypeEdit);
		editButtons.add(field1TypeEdit);
		editButtons.add(field2TypeEdit);
		editButtons.add(field3TypeEdit);
		editButtons.add(field4TypeEdit);
		editButtons.add(field5TypeEdit);
		editButtons.add(field6TypeEdit);
		editButtons.add(field7TypeEdit);
		editButtons.add(field8TypeEdit);
		editButtons.add(field9TypeEdit);
		editButtons.add(field10TypeEdit);
		editButtons.add(field11TypeEdit);
		editButtons.add(field12TypeEdit);
		editButtons.add(field13TypeEdit);
		editButtons.add(field14TypeEdit);
		editButtons.add(field15TypeEdit);
		editButtons.add(field16TypeEdit);
		editButtons.add(field17TypeEdit);
		editButtons.add(field18TypeEdit);
	}
}
