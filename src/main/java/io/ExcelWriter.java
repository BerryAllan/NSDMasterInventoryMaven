package io;

import io.watchers.MD5CheckSum;
import item.Item;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import primary.Main;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * A very simple program that writes some data to an Excel file
 * using the Apache POI library.
 *
 * @author www.codejava.net
 */
public class ExcelWriter
{
	public void writeExcelAll(String excelFilePath) throws IOException, InvalidFormatException, NoSuchAlgorithmException
	{
		Main.updatingSpreadsheetCurrently = true;
		clearExcelDocs(excelFilePath);
		File dataBaseDir = new File(Main.DATABASE_DIRECTORY);

		FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
		Workbook workbook = WorkbookFactory.create(inputStream);

		Sheet recycledSheet;
		try
		{
			recycledSheet = workbook.createSheet("RECYLED");
		} catch (IllegalArgumentException e)
		{
			recycledSheet = workbook.getSheet("RECYLED");
		}

		for (File file : Objects.requireNonNull(dataBaseDir.listFiles()))
		{
			//System.out.println(file.getAbsolutePath());
			//System.out.println(Main.getItems(file).size());
			String theDatabaseName = FilenameUtils.getBaseName(file.getAbsolutePath());
			Main main = new Main();
			List<Item> items = main.getItems(file, Main.getPrefabFromDatabase(file));

			Sheet sheet = workbook.getSheet(theDatabaseName);
			if (sheet == null) sheet = workbook.createSheet(theDatabaseName);

			int rowCount = 1;
			int recycledRowCount = 1;

			Row row0 = sheet.createRow(sheet.getFirstRowNum());
			writePrefabToTopRow(Main.getPrefabFromDatabase(file), row0);

			for (int i = 0; i < items.size(); i++)
			{
				if (!items.get(i).getFields().get(13).isEmpty())
				{/*
					if (i != 0)
					{
						if (!items.get(i).getField5().equalsIgnoreCase(items.get(i - 1).getField5()))
						{
							sheet.createRow(++recycledRowCount);
						}
					}*/

					Row row = recycledSheet.createRow(++recycledRowCount);
					writeItem(items.get(i), row);
				}
				else
				{/*
					if (i != 0)
					{
						if (!items.get(i).getField5().equalsIgnoreCase(items.get(i - 1).getField5()))
						{
							sheet.createRow(++rowCount);
						}
					}*/

					Row row = sheet.createRow(++rowCount);
					writeItem(items.get(i), row);
				}
			}

			//System.out.println(sheet.getLastRowNum());

			int counter = 0;
			for (int i = 2; i <= sheet.getLastRowNum(); i++)
			{
				Row row = sheet.getRow(i);
				Cell cell = row.getCell(0);

				if (cell != null)
				{
					CellStyle style = workbook.createCellStyle();

					if (items.get(counter).isInventoried())
					{
						style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
					}
					else if (!items.get(counter).getFields().get(13).isEmpty())
					{
						style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
					}
					else
					{
						style.setFillForegroundColor(IndexedColors.ROSE.getIndex());
					}

					style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					//System.out.println(itemSchools.get(i - counter).isInventoried());
					cell.setCellStyle(style);
					counter++;
				}
			}
		}

		FileOutputStream outputStream = new FileOutputStream(excelFilePath);
		workbook.write(outputStream);
		outputStream.close();
		workbook.close();

		Main.checkSumDatabase = MD5CheckSum.getMD5Checksum(Main.DATABASE_DIRECTORY);

		Main.updatingSpreadsheetCurrently = false;
		//Main.excelAndDatabaseSynced = true;
	}

	private void writePrefabToTopRow(String prefabFromDatabase, Row row) throws IOException
	{
		try
		{

			InputStream inputStream =
					this.getClass().getResourceAsStream("/prefabs/" + prefabFromDatabase + "/" + prefabFromDatabase + "Fields.properties");
			//System.out.println("/prefabs/" + prefabFromDatabase + "/" + prefabFromDatabase + "Fields.properties");
			Properties properties = new Properties();
			properties.load(inputStream);

			for (int i = 0; i < properties.size(); i++)
			{
				Cell cell = row.createCell(i);
				cell.setCellValue(properties.getProperty("field" + i));
				Font font = cell.getRow().getSheet().getWorkbook().createFont();
				font.setFontName("Calibri");
				font.setBold(true);
				CellStyle cellStyle = cell.getRow().getSheet().getWorkbook().createCellStyle();
				cellStyle.setFont(font);
				cell.setCellStyle(cellStyle);
			}

			inputStream.close();
		} catch (IOException e)
		{
			InputStream inputStream = this.getClass().getResourceAsStream("/prefabs/Default/DefaultFields.properties");
			Properties properties = new Properties();
			properties.load(inputStream);

			for (int i = 0; i < properties.size(); i++)
			{
				Cell cell = row.createCell(i);
				cell.setCellValue(properties.getProperty("field" + i));
				Font font = cell.getRow().getSheet().getWorkbook().createFont();
				font.setFontName("Calibri");
				font.setBold(true);
				CellStyle cellStyle = cell.getRow().getSheet().getWorkbook().createCellStyle();
				cellStyle.setFont(font);
				cell.setCellStyle(cellStyle);
			}

			inputStream.close();
		}
	}

	private void writeItem(Item item, Row row)
	{
		Cell cell;
		for (int i = 0; i < item.getFields().size(); i++)
		{
			cell = row.createCell(i);
			cell.setCellValue(item.getFields().get(i).equalsIgnoreCase("true") || item.getFields().get(i).equalsIgnoreCase("false") ? "" :
			                  item.getFields().get(i));
		}
	}

	public void clearExcelDocs(String excelFilePath) throws IOException, InvalidFormatException
	{
		File excelFile = new File(excelFilePath);
		FileInputStream inputStream = new FileInputStream(excelFile);
		Workbook workbook = WorkbookFactory.create(inputStream);
		ArrayList<Sheet> sheets = new ArrayList<>();
		File dataBaseDir = new File(Main.DATABASE_DIRECTORY);

		for (File file : Objects.requireNonNull(dataBaseDir.listFiles()))
		{
			if (workbook.getSheet(FilenameUtils.getBaseName(file.getAbsolutePath())) != null)
				sheets.add(workbook.getSheet(FilenameUtils.getBaseName(file.getAbsolutePath())));
			else
			{
				workbook.createSheet(FilenameUtils.getBaseName(file.getAbsolutePath()));
				sheets.add(workbook.getSheet(FilenameUtils.getBaseName(file.getAbsolutePath())));
			}
		}

		for (Sheet sheet : sheets)
		{
			for (int i = 2; i <= sheet.getLastRowNum(); i++)
			{
				if (sheet.getRow(i) != null)
				{
					sheet.removeRow(sheet.getRow(i));
				}
			}
		}

		FileOutputStream outputStream = new FileOutputStream(excelFilePath);
		workbook.write(outputStream);
		outputStream.close();
		workbook.close();
	}
}