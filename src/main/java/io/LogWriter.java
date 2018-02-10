package io;

import controllers.ControllerConsole;
import primary.Main;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LogWriter
{
	public static void writeLog() throws IOException
	{
		ControllerConsole controllerConsole = new ControllerConsole();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy_HH-mm-ss");
		File file = new File(Main.LOGS_DIRECTORY + LocalDate.now().atTime(LocalTime.now()).format(formatter) + ".txt");
		if (!file.exists())
		{
			file.createNewFile();
		}
		PrintWriter writer = new PrintWriter(file);
		writer.print(controllerConsole.getConsoleText());
		writer.close();
	}
}
