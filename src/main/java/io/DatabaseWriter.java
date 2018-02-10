package io;

import io.watchers.MD5CheckSum;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import primary.Main;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;

public class DatabaseWriter
{
	public void readExcelAllWriteToCSV(String directory) throws IOException, InvalidFormatException, NoSuchAlgorithmException
	{
		Main.updatingDatabaseCurrently = true;
		FileInputStream inputStream = new FileInputStream(new File(Main.INVENTORY_EXCEL_FILE));
		Workbook workbook = WorkbookFactory.create(inputStream);

		for (Sheet sheet : workbook)
		{
			File file = new File(directory + sheet.getSheetName() + ".csv");
			if (!file.exists()) file.createNewFile();
			PrintWriter writer = new PrintWriter(file);

			//System.out.println(file.getAbsolutePath());
			//System.out.println(sheet.getLastRowNum());

			writer.println(prefabString(sheet));

			for (int i = 2; i <= sheet.getLastRowNum(); i++)
			{
				Row row = sheet.getRow(i);
				if (row == null || checkIfRowIsEmpty(row))
				{
					continue;
				}

				Cell cell = row.getCell(0);
				ArrayList<String> data = new ArrayList<>();

				for (int j = 0; j < row.getLastCellNum(); j++)
				{
					if (row.getCell(j) != null)
					{
						if (j == 0)
						{
							data.add(row.getCell(j).getCellTypeEnum() == CellType.NUMERIC ?
							         String.valueOf((int) row.getCell(j).getNumericCellValue()) : row.getCell(j).getStringCellValue());
						}
						else
						{
							data.add("," + (row.getCell(j).getCellTypeEnum() == CellType.NUMERIC ?
							                String.valueOf((int) row.getCell(j).getNumericCellValue()) :
							                row.getCell(j).getStringCellValue()));
						}
					}
					else
					{
						data.add(",");
					}
				}

				if (cell != null)
				{
					if (cell.getCellStyle().getFillForegroundColor() == IndexedColors.LIGHT_GREEN.getIndex())
					{
						data.add(",TRUE");
					}
					else
					{
						data.add(",FALSE");
					}
				}

				String theString = "";
				for (String string : data)
				{
					theString = string;
					//System.out.print(string);
					if (!string.trim().isEmpty()) writer.print(string);
				}
				//System.out.println();
				if (!theString.trim().isEmpty()) writer.print("\n");
			}
			writer.close();
		}

		ArrayList<String> sheetArrayList = new ArrayList<>();
		for (Sheet sheet : workbook)
		{
			sheetArrayList.add(sheet.getSheetName());
		}

		for (File file : (new File(directory).listFiles()))
		{
			if (!sheetArrayList.contains(FilenameUtils.getBaseName(file.getAbsolutePath()))) file.delete();
		}

		workbook.close();

		Main.updatingDatabaseCurrently = false;
		Main.checkSumExcel = MD5CheckSum.getMD5Checksum(Main.INVENTORY_EXCEL_FILE);
	}

	private boolean checkIfRowIsEmpty(Row row)
	{
		if (row == null)
		{
			return true;
		}
		if (row.getLastCellNum() <= 0)
		{
			return true;
		}
		for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++)
		{
			Cell cell = row.getCell(cellNum);
			if (cell != null && cell.getCellTypeEnum() != CellType.BLANK && StringUtils.isNotBlank(cell.toString()))
			{
				return false;
			}
		}
		return true;
	}

	public String prefabString(Sheet sheet) throws IOException
	{
		Row row0 = sheet.getRow(sheet.getFirstRowNum());
		File prefabsDir = new File(this.getClass().getResource("/prefabs").getPath());

		int counter = 0;
		for (File prefabDir : Objects.requireNonNull(prefabsDir.listFiles()))
		{
			InputStream is = this.getClass().getResourceAsStream(
					"/prefabs/" + "/" + FilenameUtils.getBaseName(prefabDir.getPath()) + "/" + FilenameUtils
							.getBaseName(prefabDir.getPath()) + "Fields.properties");
			//System.out.println("/prefabs/" + "/" + FilenameUtils.getBaseName(prefabDir.getPath()) + "/" + FilenameUtils
			//		.getBaseName(prefabDir.getPath()) + "Fields.properties");
			Properties properties = new Properties();
			properties.load(is);

			String prefabString = "";
			for (int i = 0; i < properties.size(); i++)
			{
				prefabString += properties.getProperty("field" + i);
			}
			String excelString = "";
			for (Cell cell : row0)
			{
				excelString += cell.getStringCellValue();
			}

			//System.out.println(prefabString);
			//System.out.println(excelString);
			if (prefabString.equals(excelString))
			{
				return FilenameUtils.getBaseName(prefabDir.getPath());
				//break;
			}
			else if (counter == Objects.requireNonNull(prefabsDir.listFiles()).length - 1)
			{
				return "Default";
				//break;
			}
			counter++;
		}

		return "Did Not Work";
	}
}
