package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.util.Properties;

public class ControllerComboBoxBuilder
{
	public static String initTypeNoString = "";
	public static String initString;
	boolean closeStage = true;

	@FXML
	Button saveButton;
	@FXML
	ListView<String> listView;

	@FXML
	TextField textField;
	@FXML
	Button addButton;
	@FXML
	Button editButton;
	@FXML
	Button removeButton;


	@FXML
	private void initialize()
	{
		try
		{
			File file = new File(this.getClass().getResource("/prefabs").getPath() + "/" + initString + "/comboBoxes/");
			if (!file.exists()) file.mkdirs();

			File file1 = new File(this.getClass().getResource("/prefabs/")
					.getPath() + "/" + initString + "/comboBoxes/" + initTypeNoString + "" + ".properties");
			if (!file1.exists()) file1.createNewFile();

			InputStream inputStream =
					this.getClass().getResourceAsStream("/prefabs/" + initString + "/comboBoxes/" + initTypeNoString + ".properties");
			Properties properties = new Properties();
			properties.load(inputStream);
			listView.getItems().clear();

			for (int i = 0; i < properties.size(); i++)
			{
				listView.getItems().add(properties.getProperty("comboChoice" + i));
			}

			inputStream.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@FXML
	private void saveButtonAction()
	{
		try
		{
			File file = new File(this.getClass().getResource("/prefabs").getPath() + "/" + initString + "/comboBoxes/");
			if (!file.exists()) file.mkdirs();

			Properties properties = new Properties();
			for (int i = 0; i < listView.getItems().size(); i++)
			{
				properties.setProperty("comboChoice" + i, listView.getItems().get(i));
			}

			OutputStream os = new FileOutputStream(this.getClass().getResource("/prefabs")
					.getPath() + "/" + initString + "/comboBoxes/" + initTypeNoString + ".properties");
			properties.store(os, null);
			os.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		if (closeStage)
		{
			Stage stage = (Stage) saveButton.getScene().getWindow();
			stage.close();
		}
	}

	@FXML
	private void onAddComboBoxItem()
	{
		listView.getItems().add(textField.getText());
		textField.setText("");
	}

	@FXML
	private void onEditComboBoxItem()
	{
		textField.setText(listView.getSelectionModel().getSelectedItem());
	}

	@FXML
	private void onRemoveComboBoxItem()
	{
		if (listView.getSelectionModel().getSelectedItem() != null)
		{
			listView.getItems().remove(listView.getSelectionModel().getSelectedItem());
			closeStage = false;
			saveButtonAction();
			closeStage = true;
		}
	}
}
