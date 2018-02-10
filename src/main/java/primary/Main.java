package primary;

import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import comparators.SortByField;
import io.BarcodeGenerator;
import io.ConfigUtil;
import io.DatabaseWriter;
import io.ExcelWriter;
import io.watchers.CheckSumWatcher;
import io.watchers.MD5CheckSum;
import item.Item;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/*
TO-DO:
- nicer gui ?
- make sure files aren't locked when attempting to move
- duplicate in item prefab manager
- sorting
- first line is the prefab for the database file; get prefab from excel first row when writing to database
- disable certain inventories based on prefab
*/

public class Main extends Application
{
	public static volatile String MAIN_DIRECTORY;

	public static volatile String DATABASE_DIRECTORY;
	public static volatile String BACKUPS_DIRECTORY;
	public static volatile String LOGS_DIRECTORY;
	public static volatile String BARCODES_DIRECTORY;

	public static volatile String INVENTORY_EXCEL_FILE;
	public static volatile String CONFIG_RESOURCE_PATH = "/config.properties";

	public static volatile boolean updatingSpreadsheetCurrently = false;
	public static volatile boolean updatingDatabaseCurrently = false;
	public static volatile boolean programFinished = false;

	public static volatile String checkSumExcel;
	public static volatile String checkSumDatabase;

	private static Timer timerExcel;
	private static Timer timerDatabase;

	public static void main(String[] args) throws InvalidFormatException, NoSuchAlgorithmException, IOException
	{
		generateWorkspace();
		checkForDatabaseChangesAndUpdateExcel();
		checkForExcelChangesAndUpdateDatabase();
		launch(args);
		programFinished = true;
		backupFiles();

		while (true)
		{
			if (!(updatingSpreadsheetCurrently || updatingDatabaseCurrently)) break; //or if already synced
			//wait
		}

		System.exit(0);
	}

	private static void checkForDatabaseChangesAndUpdateExcel()
	{
		TimerTask task = new CheckSumWatcher(DATABASE_DIRECTORY, Main.checkSumDatabase)
		{
			@Override
			protected void onChange(String path)
			{
				if (!updatingDatabaseCurrently && !updatingSpreadsheetCurrently)
				{
					try
					{
						updateExcel();
					} catch (Exception e)
					{
						e.printStackTrace();

					}
				}
			}
		};

		timerExcel = new Timer();
		timerExcel.schedule(task, new Date(), 15000);
	}

	private static void checkForExcelChangesAndUpdateDatabase()
	{
		TimerTask task = new CheckSumWatcher(INVENTORY_EXCEL_FILE, Main.checkSumExcel)
		{
			@Override
			protected void onChange(String path)
			{
				//System.out.println(INVENTORY_EXCEL_FILE);
				if (!updatingSpreadsheetCurrently && !updatingDatabaseCurrently)
				{
					try
					{
						updateDatabase();
					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		};

		timerDatabase = new Timer();
		timerDatabase.schedule(task, new Date(), 1000);
	}

	public static void changeWorkspace(String originalPath, String newDirectory) throws IOException
	{
		timerExcel.cancel();
		timerDatabase.cancel();

		File originalDirectory = new File(originalPath);

		for (File file : originalDirectory.listFiles())
		{
			if (file.isDirectory())
			{
				if (!isFileLock(file))
				{
					FileUtils.moveDirectoryToDirectory(file, new File(newDirectory), true);
				}
				else
				{
					while (isFileLock(file))
					{
						try
						{
							FileUtils.moveDirectoryToDirectory(file, new File(newDirectory), true);
						} catch (IOException e)
						{
							System.out.println("Trying to move Inventory, please wait.");
						}
					}
				}
			}
			else
			{
				if (!isFileLock(file))
				{
					FileUtils.moveFileToDirectory(file, new File(newDirectory), true);
				}
				else
				{
					while (isFileLock(file))
					{
						try
						{
							FileUtils.moveFileToDirectory(file, new File(newDirectory), true);
						} catch (IOException e)
						{
							System.out.println("Trying to move Inventory, please wait.");
						}
					}
				}
			}
		}

		originalDirectory.delete();

		ConfigUtil configUtil = new ConfigUtil();
		configUtil.getAndSetWorkspaceDirectory();

		checkForDatabaseChangesAndUpdateExcel();
		checkForExcelChangesAndUpdateDatabase();
	}

	public static boolean isFileLock(File file) throws IOException
	{
		boolean isLock = true;
		if (file.exists())
		{
			RandomAccessFile rf = new RandomAccessFile(file, "rw");
			FileChannel fileChannel = rf.getChannel();
			FileLock lock = null;
			try
			{
				// let us try to get a lock. If file already has an exclusive lock by another process
				//LOGGER.info("Trying to acquire lock");
				lock = fileChannel.tryLock();
				if (lock != null)
				{
					isLock = false;
				}
			} catch (Exception ex)
			{
				//LOGGER.error("Error when checkFileLock: " + ex);
			} finally
			{
				if (lock != null)
				{
					lock.release();
				}

				if (fileChannel != null)
				{
					fileChannel.close();
				}

				if (rf != null)
				{
					rf.close();
				}
			}
		}
		else
		{
			//LOGGER.warn("File is not exist");
		}
		return isLock;
	}

	public static void generateWorkspace() throws IOException, InvalidFormatException, NoSuchAlgorithmException
	{
		ConfigUtil configUtil = new ConfigUtil();
		configUtil.getAndSetWorkspaceDirectory();

		File file;
		if (!(file = new File(MAIN_DIRECTORY)).exists()) file.mkdirs();

		if (!(file = new File(DATABASE_DIRECTORY)).exists()) file.mkdirs();

		if (!(file = new File(BACKUPS_DIRECTORY)).exists()) file.mkdirs();

		if (!(file = new File(LOGS_DIRECTORY)).exists()) file.mkdirs();

		if (!(file = new File(BARCODES_DIRECTORY)).exists()) file.mkdirs();

		if (!(file = new File(INVENTORY_EXCEL_FILE)).exists())
		{
			file.createNewFile();
		}
		else
		{/*
			File xssfFile = new File(INVENTORY_EXCEL_FILE);
			if (xssfFile.length() > 0)
			{
				try
				{
					XSSFWorkbook xssfWorkbook = new XSSFWorkbook(xssfFile);
					if (xssfWorkbook.getNumberOfSheets() < 1) xssfWorkbook.createSheet();
					else
					{
						for (Sheet sheet : xssfWorkbook)
						{
							if (!(file = new File(DATABASE_DIRECTORY + sheet.getSheetName() + ".csv")).exists()) file.createNewFile();
						}
					}

					DatabaseWriter databaseWriter = new DatabaseWriter();
					//databaseWriter.readExcelAllWriteToCSV(Main.DATABASE_DIRECTORY);
				} catch (POIXMLException | IOException e)
				{
					e.printStackTrace();
				}
			}*/
		}

		checkSumExcel = MD5CheckSum.getMD5Checksum(INVENTORY_EXCEL_FILE);
		checkSumDatabase = MD5CheckSum.getMD5Checksum(DATABASE_DIRECTORY);
	}

	public static void backupFiles() throws IOException
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy/HH꞉mm꞉ss");
		File file = new File(Main.BACKUPS_DIRECTORY + LocalDate.now().atTime(LocalTime.now()).format(formatter));
		if (!file.exists()) file.mkdirs();

		FileUtils.copyDirectoryToDirectory(new File(DATABASE_DIRECTORY), file);
		FileUtils.copyFileToDirectory(new File(INVENTORY_EXCEL_FILE), file);
	}

	public static void generateBarcodesFromInventory()
	{
		try
		{
			FileUtils.cleanDirectory(new File(BARCODES_DIRECTORY));
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		try
		{
			BarcodeGenerator qrCodeGenerator = new BarcodeGenerator();
			//String charset = "UTF-8"; // or "ISO-8859-1"
			Map hintMap = new HashMap();
			hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
			qrCodeGenerator.createDMCodes(hintMap, 200, 200);
		} catch (WriterException e)
		{
			System.out.println("Could not generate Barcode, WriterException :: " + e.getMessage());
		} catch (IOException e)
		{
			System.out.println("Could not generate Barcode, IOException :: " + e.getMessage());
		}
	}

	public static String getPrefabFromDatabase(File file) throws IOException
	{
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

		return bufferedReader.readLine();
	}

	public static ArrayList<String> getStrings(String path) throws FileNotFoundException
	{
		File file = new File(path);
		Scanner scanner = new Scanner(file);
		ArrayList<String> strings = new ArrayList<>();

		int counter = 0;
		while (scanner.hasNextLine())
		{
			if (counter == 0)
			{
				scanner.nextLine();
			}
			else
			{
				strings.add(scanner.nextLine());
			}

			counter++;
		}

		return strings;
	}

	//only checks asset tag and serial number; if neither it just checks the whole string
	public static boolean searchUsingScanner(File filePath, String searchQuery) throws FileNotFoundException
	{
		ArrayList<String> list = new ArrayList<>();
		Scanner scanner = new Scanner(filePath);

		ArrayList<String> stringsForNewSearchQuery = Item.parser(searchQuery);
		String assetTagSearchQuery = stringsForNewSearchQuery.get(0);
		String serialNumberSearchQuery = stringsForNewSearchQuery.get(14);

		while (scanner.hasNextLine())
		{
			list.add(scanner.nextLine());
		}

		String total = "";
		for (String aList : list)
		{
			total += aList;
		}

		if (!assetTagSearchQuery.isEmpty() && !serialNumberSearchQuery.isEmpty())
		{
			return total.toLowerCase().contains(assetTagSearchQuery.toLowerCase()) && total.toLowerCase()
					.contains(serialNumberSearchQuery.toLowerCase());
		}
		else
		{
			return total.toLowerCase().contains(searchQuery.toLowerCase());
		}
	}

	public static void markInventoried(File fileName, String textToReplace)
	{
		try
		{
			BufferedReader file = new BufferedReader(new FileReader(fileName));
			String line;
			String input = "";

			while ((line = file.readLine()) != null)
			{
				if (line.toLowerCase().contains(textToReplace.toLowerCase()))
				{
					line = "";
					ArrayList<String> itemStrings = Item.parser(textToReplace);
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
					itemStrings.set(12, LocalDate.now().format(formatter));
					itemStrings.set(itemStrings.size() - 1, "TRUE");
					for (int j = 0; j < itemStrings.size(); j++)
					{
						if (j == 0) input += itemStrings.get(j);
						else input += "," + itemStrings.get(j);
					}
				}
				input += line + '\n';
			}
			removeEmptyLines(fileName);

			FileOutputStream File = new FileOutputStream(fileName);
			File.write(input.getBytes());
			file.close();
			File.close();
		} catch (Exception e)
		{
			System.out.println("Problem reading file.");
		}
	}

	public static void removeEmptyLines(File file)
	{
		try (BufferedReader inputFile = new BufferedReader(new FileReader(file)); PrintWriter outputFile = new PrintWriter(
				new FileWriter(file)))
		{
			String lineOfText;
			while ((lineOfText = inputFile.readLine()) != null)
			{
				lineOfText = lineOfText.trim();
				if (!lineOfText.isEmpty())
				{
					outputFile.println(lineOfText);
				}
			}

			inputFile.close();
			outputFile.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void updateDatabase()
	{
		try
		{
			DatabaseWriter databaseWriter = new DatabaseWriter();

			if (!updatingSpreadsheetCurrently && !programFinished)
			{
				System.out.println("Updating Database...");
				databaseWriter.readExcelAllWriteToCSV(Main.DATABASE_DIRECTORY);
				System.out.println("Successfully updated Database.");
			}
		} catch (NoSuchAlgorithmException | IOException | InvalidFormatException e)
		{
			updatingDatabaseCurrently = false;
			System.out.println("Errors encountered updating Database. Did not update.");
			//e.printStackTrace();
		}
	}

	public static void updateExcel() throws Exception
	{
		try
		{
			ExcelWriter excelWriter = new ExcelWriter();

			if (!updatingDatabaseCurrently && !programFinished)
			{
				System.out.println("Updating Spreadsheet... DO NOT ATTEMPT TO OPEN...");
				excelWriter.writeExcelAll(INVENTORY_EXCEL_FILE);
				System.out.println("Successfully updated Spreadsheet.");
			}
		} catch (IOException | InvalidFormatException e)
		{
			updatingSpreadsheetCurrently = false;
			System.out.println(
					"Errors encountered updating Spreadsheet. Did not update. You likely have the spreadsheet currently open. If so, ignore this error. Everything is working.");
			//e.printStackTrace();
		}
	}

	public static ArrayList<Item> sortList(ArrayList<Item> items, ArrayList<Integer> sorts)
	{
		//USE RECURSION

		if (sorts.size() == 0)
		{
			return items;
		}
		else
		{
			for (int i = 0; i < sorts.size(); i++)
			{

				for (int j = 0; j < items.size(); j++)
				{
					if (j != 0)
					{
						if (items.get(j).getFields().get(sorts.get(i)).equals(items.get(j - 1).getFields().get(sorts.get(i))))
						{
							items.subList(0, j).sort(new SortByField(sorts.get(i)));
						}
					}
				}
			}
			return items;
		}
	}

	public ArrayList<Item> getItems(File file, String prefab) throws IOException, NullPointerException
	{
		ArrayList<Item> items = new ArrayList<>();

		//System.out.println(fileEntry.getAbsolutePath());
		for (String string : Main.getStrings(file.getAbsolutePath()))
		{
			//System.out.println(StringUtil.countMatches(string, ','));
			items.add(new Item(Item.parser(string)));
		}

		InputStream inputStream = this.getClass().getResourceAsStream("/prefabs/" + prefab + "/" + prefab + "FieldSortBys" + ".properties");
		//System.out.println("/prefabs/" + prefab + "/" + prefab + "FieldSortBys.properties");
		Properties prop = new Properties();
		prop.load(inputStream);

		TreeMap<Integer, Integer> treeMap = new TreeMap<>();
		ArrayList<Integer> sorts = new ArrayList<>();

		for (int i = 0; i < prop.size(); i++)
		{
			if (!prop.getProperty("field" + i).isEmpty() && !prop.getProperty("field" + i).equals("0"))
			{
				treeMap.put(Integer.valueOf(prop.getProperty("field" + i)), i);
			}
		}

		Collection<Integer> keySets = treeMap.keySet();

		for (int i : keySets)
		{
			sorts.add(treeMap.get(i));
		}
		//Collections.sort(sorts, Collections.reverseOrder());
/*
		for (Integer integer : sorts)
		{
			System.out.println(integer);
		}*/

		return sortList(items, sorts);
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		Parent root = FXMLLoader.load(this.getClass().getResource("/scenes/NSDMasterInventory.fxml"));
		primaryStage.setTitle("NSD Master Inventory");
		Image img = new Image(this.getClass().getResourceAsStream("/images/icon.png"));
		primaryStage.getIcons().add(img);
		primaryStage.setScene(new Scene(root, 1400, 800));
		primaryStage.show();
	}
}
