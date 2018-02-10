package controllers;

import io.DatabaseWriter;
import io.ExcelWriter;
import item.Item;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import primary.Main;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;

public class ControllerMain
{
	@FXML
	ChoiceBox<String> itemType;

	@FXML
	Button updateSpreadsheet;
	@FXML
	Button generateDMCodes;
	@FXML
	Button generateText;
	@FXML
	TextField generatedText;
	@FXML
	Button inventoryItem;
	@FXML
	TextField itemPlainString;
	@FXML
	Button updateDatabase;

	@FXML
	MenuItem openConsole;
	@FXML
	MenuItem aboutWindow;
	@FXML
	Button changeDirectory;
	@FXML
	TextField mainDirectoryText;
	@FXML
	MenuItem saveBackup;
	@FXML
	Button browseDirectory;
	@FXML
	MenuBar menuBar;
	@FXML
	MenuItem viewTable;

	@FXML
	FlowPane fieldsFlowPane;

	@FXML
	ComboBox<String> barcodeInventoryCB;

	private ComboBox<String> inventoryComboBox;

	// Add a public no-args constructor
	public ControllerMain()
	{
	}

	@FXML
	private void initialize() throws IOException
	{
		Properties prop = new Properties();
		prop.load(this.getClass().getResourceAsStream(Main.CONFIG_RESOURCE_PATH));
		mainDirectoryText.setText(prop.getProperty("mainDirectory"));

		refreshItemTypes();

		for (File file : Objects.requireNonNull(new File(Main.DATABASE_DIRECTORY).listFiles()))
		{
			barcodeInventoryCB.getItems().add(FilenameUtils.getBaseName(file.getPath()));
		}
		barcodeInventoryCB.getEditor().setFont(Font.font("System", FontWeight.BOLD, 15));
		barcodeInventoryCB.setPromptText("Inventory");
	}

	@FXML
	private void changeDirectory()
	{
		try
		{
			Properties prop = new Properties();
			prop.setProperty("mainDirectory", mainDirectoryText.getText());
			PrintWriter writer = new PrintWriter(this.getClass().getResource(Main.CONFIG_RESOURCE_PATH).getPath());
			prop.store(writer, null);

			//System.out.println(prop.getProperty("mainDirectory") + "\\Inventories");

			Main.changeWorkspace(Main.MAIN_DIRECTORY, prop.getProperty("mainDirectory") + "\\Inventories");
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		System.out.println("Changed main directory to: " + Main.MAIN_DIRECTORY);
	}

	@FXML
	private void showConsoleWindow()
	{
		try
		{
			Parent root = FXMLLoader.load(this.getClass().getResource("/scenes/Console.fxml"));
			Stage stage = new Stage();
			stage.setTitle("Console");
			Image img = new Image(this.getClass().getResourceAsStream("/images/icon.png"));
			stage.getIcons().add(img);
			stage.setResizable(false);
			stage.setScene(new Scene(root));
			/*stage.setOnHiding(event1 -> Platform.runLater(() -> {
				try
				{
					LogWriter.writeLog();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
				System.exit(0);
			}));*/
			stage.show();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@FXML
	private void showAboutWindow()
	{
		try
		{
			Parent root = FXMLLoader.load(this.getClass().getResource("/scenes/About.fxml"));
			Stage stage = new Stage();
			stage.setTitle("About");
			Image img = new Image(this.getClass().getResourceAsStream("/images/icon.png"));
			stage.getIcons().add(img);
			stage.setResizable(false);
			stage.setScene(new Scene(root, 600, 400));
			stage.show();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@FXML
	private void showItemManagerWindow()
	{
		try
		{
			File prefabDir = new File(this.getClass().getResource("/").getPath() + "/prefabs");
			if (!prefabDir.exists()) prefabDir.mkdirs();

			Parent root = FXMLLoader.load(this.getClass().getResource("/scenes/ItemManager.fxml"));
			Stage stage = new Stage();
			stage.setTitle("Item Prefab Manager");
			Image img = new Image(this.getClass().getResourceAsStream("/images/icon.png"));
			stage.getIcons().add(img);
			stage.setResizable(false);
			stage.setScene(new Scene(root, 900, 600));
			stage.setOnHidden(event -> refreshItemTypes());
			stage.show();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@FXML
	private void inventoryFromFields()
	{
		try
		{
			String theString = "";
			for (int i = 0; i < fieldsFlowPane.getChildren().size(); i++)
			{
				if (fieldsFlowPane.getChildren().get(i) instanceof DatePicker)
				{
					DatePicker fieldDP = (DatePicker) fieldsFlowPane.getChildren().get(i);
					theString += fieldDP.getEditor().getText() + ",";
				}
				else if (fieldsFlowPane.getChildren().get(i) instanceof ComboBox)
				{
					ComboBox fieldCB = (ComboBox) fieldsFlowPane.getChildren().get(i);
					theString += fieldCB.getEditor().getText() + ",";
				}
				else if (fieldsFlowPane.getChildren().get(i) instanceof TextField)
				{
					TextField fieldTF = (TextField) fieldsFlowPane.getChildren().get(i);
					theString += fieldTF.getText() + ",";
				}
			}

			theString += "TRUE," + inventoryComboBox.getEditor().getText();

			generatedText.setText(theString);

			File file = new File(Main.DATABASE_DIRECTORY + inventoryComboBox.getEditor().getText() + ".csv");
			if (!file.exists()) file.createNewFile();

			Main.updatingDatabaseCurrently = true;
			FileWriter fileWriter = new FileWriter(file, true);
			PrintWriter writer = new PrintWriter(fileWriter);

			if (!Main.searchUsingScanner(file, generatedText.getText()))
			{
				writer.print(generatedText.getText() + "\n");
				System.out.println("Appended \"" + generatedText.getText() + "\" to Database.");
			}
			else
			{
				Main.markInventoried(file, generatedText.getText() + "," + "FALSE");
				System.out.println("Marked \"" + generatedText.getText() + "\" as inventoried in Database.");
			}

			writer.close();
			Main.updatingDatabaseCurrently = false;
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@FXML
	private void generateCodes()
	{
		Thread t1 = new Thread(() ->
		{
			System.out.println("Generating Data Matrix barcodes...");
			Main.generateBarcodesFromInventory();
			System.out.println("Successfully generated Data Matrix barcodes.");
		});

		t1.setDaemon(true);
		t1.start();
	}

	@FXML
	private void writeToSpreadsheet()
	{
		Thread t1 = new Thread(() ->
		{
			try
			{
				ExcelWriter writer = new ExcelWriter();

				System.out.println("Updating Spreadsheet... DO NOT ATTEMPT TO OPEN...");
				writer.writeExcelAll(Main.INVENTORY_EXCEL_FILE);
				System.out.println("Successfully updated Spreadsheet.");
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		});
		t1.setDaemon(true);
		t1.start();
	}

	@FXML
	private void writeToDatabase()
	{
		Thread t1 = new Thread(() ->
		{
			try
			{
				DatabaseWriter writer = new DatabaseWriter();

				System.out.println("Updating Database...");
				writer.readExcelAllWriteToCSV(Main.DATABASE_DIRECTORY);
				System.out.println("Successfully updated Database.");
			} catch (IOException | InvalidFormatException | NoSuchAlgorithmException e)
			{
				e.printStackTrace();
			}
		});
		t1.setDaemon(true);
		t1.start();
	}

	@FXML
	private void backupFiles()
	{
		try
		{
			System.out.println("Backing up Database and Inventory...");
			Main.backupFiles();
			System.out.println("Successfully backed up Database and Inventory.");
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@FXML
	private void inventoryItemFromBarcode()
	{
		try
		{
			Main.updatingDatabaseCurrently = true;
			File file = new File(Main.DATABASE_DIRECTORY + barcodeInventoryCB + ".csv");
			FileWriter fileWriter = new FileWriter(file, true);
			PrintWriter writer = new PrintWriter(fileWriter);

			if (!Main.searchUsingScanner(file, itemPlainString.getText()))
			{
				ArrayList<String> stringArrayList = Item.parser(itemPlainString.getText());
				if (stringArrayList.get(19).toLowerCase().startsWith("f"))
				{
					for (int i = 0; i < stringArrayList.size(); i++)
					{
						if (i != stringArrayList.size() - 1) writer.print(stringArrayList.get(i) + ",");
						else writer.print("TRUE");
					}
					writer.print("\n");
				}
				else
				{
					writer.print(itemPlainString.getText() + "\n");
				}
				System.out.println("Appended \"" + itemPlainString.getText() + "\" to Database.");
			}
			else
			{
				Main.markInventoried(file, itemPlainString.getText());
				System.out.println("Marked \"" + itemPlainString.getText() + "\" as inventoried in Database.");
			}
			itemPlainString.setText("");

			writer.close();
			Main.updatingDatabaseCurrently = false;
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@FXML
	private void browseDirectory()
	{
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select Directory");
		directoryChooser.setInitialDirectory(new File(Main.MAIN_DIRECTORY));
		Stage stage = new Stage();
		stage.setResizable(false);
		File selectedDirectory = directoryChooser.showDialog(stage);
		mainDirectoryText.setText(selectedDirectory.getAbsolutePath());
	}

	@FXML
	private void onChangeItemType()
	{
		try
		{
			InputStream inputStream = this.getClass()
					.getResourceAsStream("/prefabs/" + itemType.getValue() + "/" + itemType.getValue() + "Fields" + ".properties");
			Properties properties = new Properties();
			properties.load(inputStream);

			setFieldTypes();

			for (int i = 0; i < fieldsFlowPane.getChildren().size(); i++)
			{
				if (fieldsFlowPane.getChildren().get(i) instanceof DatePicker)
				{
					DatePicker fieldDP = (DatePicker) fieldsFlowPane.getChildren().get(i);
					fieldDP.setPrefWidth(125);
					fieldDP.setPromptText(properties.getProperty("field" + i));
				}
				else if (fieldsFlowPane.getChildren().get(i) instanceof ComboBox)
				{
					ComboBox<String> fieldCB = (ComboBox<String>) fieldsFlowPane.getChildren().get(i);

					try
					{
						InputStream is = this.getClass()
								.getResourceAsStream("/prefabs/" + itemType.getValue() + "/comboBoxes/comboBox" + i + ".properties");

						Properties propsCBs = new Properties();
						propsCBs.load(is);
						for (int j = 0; j < propsCBs.size(); j++)
						{
							fieldCB.getItems().add(propsCBs.getProperty("comboChoice" + j));
						}
						is.close();
					} catch (NullPointerException ignored)
					{
					}

					fieldCB.setEditable(true);
					fieldCB.setPrefWidth(125);
					fieldCB.setPromptText(properties.getProperty("field" + i));
				}
				else if (fieldsFlowPane.getChildren().get(i) instanceof TextField)
				{
					TextField fieldTF = (TextField) fieldsFlowPane.getChildren().get(i);
					fieldTF.setPrefWidth(125);
					fieldTF.setPromptText(properties.getProperty("field" + i));
				}
			}
			inputStream.close();
		} catch (NullPointerException | IOException e)
		{
			for (int i = 0; i < fieldsFlowPane.getChildren().size(); i++)
			{
				if (fieldsFlowPane.getChildren().get(i) instanceof DatePicker)
				{
					DatePicker fieldDP = (DatePicker) fieldsFlowPane.getChildren().get(i);
					fieldDP.setPrefWidth(125);
					fieldDP.setPromptText("");
				}
				else if (fieldsFlowPane.getChildren().get(i) instanceof ComboBox)
				{
					ComboBox fieldCB = (ComboBox) fieldsFlowPane.getChildren().get(i);
					fieldCB.setPrefWidth(125);
					fieldCB.setEditable(true);
					fieldCB.setPromptText("");
				}
				else if (fieldsFlowPane.getChildren().get(i) instanceof TextField)
				{
					TextField fieldTF = (TextField) fieldsFlowPane.getChildren().get(i);
					fieldTF.setPrefWidth(125);
					fieldTF.setPromptText("");
				}
			}
		}
		inventoryComboBox.setPromptText("Inventory");
	}

	private void refreshItemTypes()
	{
		itemType.getItems().clear();

		File superDirectory = new File(this.getClass().getResource("/").getPath() + "prefabs");
		if (!superDirectory.exists()) superDirectory.mkdirs();

		for (File file : Objects.requireNonNull(new File(this.getClass().getResource("/prefabs").getPath()).listFiles()))
		{
			itemType.getItems().add(FilenameUtils.getBaseName(file.getPath()));
		}

		itemType.setValue(itemType.getItems().get(0));
		onChangeItemType();
	}

	private void setFieldTypes() throws IOException
	{
		InputStream inputStream = this.getClass()
				.getResourceAsStream("/prefabs/" + itemType.getValue() + "/" + itemType.getValue() + "FieldTypes" + ".properties");
		Properties prop = new Properties();
		prop.load(inputStream);

		fieldsFlowPane.getChildren().clear();

		for (int i = 0; i < prop.size(); i++)
		{
			if (prop.getProperty("field" + i).equalsIgnoreCase("datepicker")) fieldsFlowPane.getChildren().add(new DatePicker());
			else if (prop.getProperty("field" + i).equalsIgnoreCase("combobox")) fieldsFlowPane.getChildren().add(new ComboBox<>());
			else fieldsFlowPane.getChildren().add(new TextField());
		}

		inventoryComboBox = new ComboBox<>();
		for (File file : Objects.requireNonNull(new File(Main.DATABASE_DIRECTORY).listFiles()))
		{
			if (Main.getPrefabFromDatabase(file).equals(itemType.getValue()))
				inventoryComboBox.getItems().add(FilenameUtils.getBaseName(file.getPath()));
		}
		inventoryComboBox.setPrefWidth(100);
		inventoryComboBox.getEditor().setFont(Font.font("System", FontWeight.BOLD, 15));
		fieldsFlowPane.getChildren().add(inventoryComboBox);

		inputStream.close();
	}

	@FXML
	private void viewTheTable()
	{
		try
		{

			Parent root = FXMLLoader.load(this.getClass().getResource("/scenes/TableView.fxml"));
			Stage stage = new Stage();
			stage.setTitle("Table View");
			Image img = new Image(this.getClass().getResourceAsStream("/images/icon.png"));
			stage.getIcons().add(img);
			stage.setResizable(true);
			stage.setScene(new Scene(root, 1600, 800));
			stage.show();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
