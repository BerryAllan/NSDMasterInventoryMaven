package io;

import primary.Main;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil
{
	public String getAndSetWorkspaceDirectory() throws IOException
	{
		InputStream inputStream = this.getClass().getResourceAsStream(Main.CONFIG_RESOURCE_PATH);
		Properties prop = new Properties();
		prop.load(inputStream);

		Main.MAIN_DIRECTORY = prop.getProperty("mainDirectory") + "\\Inventories";

		Main.DATABASE_DIRECTORY = Main.MAIN_DIRECTORY + "\\Database\\";
		Main.BACKUPS_DIRECTORY = Main.MAIN_DIRECTORY + "\\Backups\\";
		Main.LOGS_DIRECTORY = Main.MAIN_DIRECTORY + "\\Logs\\";
		Main.BARCODES_DIRECTORY = Main.MAIN_DIRECTORY + "\\Barcodes\\";

		Main.INVENTORY_EXCEL_FILE = Main.MAIN_DIRECTORY + "\\Inventories.xlsx";

		inputStream.close();
		return Main.MAIN_DIRECTORY;
	}
}
