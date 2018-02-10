package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import primary.Main;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ControllerConsole extends OutputStream implements Initializable
{
	@FXML
	TextArea consoleArea;

	@FXML
	Button saveButton;

	public void appendText(String str)
	{
		Platform.runLater(() -> consoleArea.appendText(str));
	}

	@Override
	public void write(int b)
	{

	}

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		OutputStream out = new OutputStream()
		{
			@Override
			public void write(int b)
			{
				appendText(String.valueOf((char) b));
			}
		};
		System.setOut(new PrintStream(out, true));
	}

	public String getConsoleText()
	{
		if (consoleArea.getText() == null) return "";
		else return consoleArea.getText();
	}

	@FXML
	private void onSave() throws IOException
	{
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH꞉mm꞉ss");
		File dir = new File(Main.LOGS_DIRECTORY + LocalDate.now().format(dateFormatter));
		File txt = new File(dir.getAbsolutePath() + "/" + LocalTime.now().format(timeFormatter) + ".txt");
		if (!dir.exists()) dir.mkdirs();
		if (!txt.exists()) txt.createNewFile();

		PrintWriter writer = new PrintWriter(txt);
		writer.print(consoleArea.getText());
		writer.close();
		System.out.println("Saved log to: " + txt.getAbsolutePath());
	}
}
