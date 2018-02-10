package io;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.commons.io.FilenameUtils;
import primary.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

public class BarcodeGenerator
{
	public void createDMCodes(Map hintMap, int height, int width) throws IOException, WriterException
	{
		try
		{

			for (File file : Objects.requireNonNull((new File(Main.DATABASE_DIRECTORY)).listFiles()))
			{
				File directory = new File(Main.BARCODES_DIRECTORY + "/" + FilenameUtils.getBaseName(file.getAbsolutePath()) + "/");
				if (!directory.exists()) directory.mkdirs();

				for (int i = 0; i < Main.getStrings(file.getAbsolutePath()).size(); i++)
				{
					BitMatrix matrix = new MultiFormatWriter()
							.encode(Main.getStrings(file.getAbsolutePath()).get(i), BarcodeFormat.DATA_MATRIX, width, height, hintMap);

					//System.out.println(Main.getStrings(file.getAbsolutePath()).get(i));
					String newString = "";
					for (char c : Main.getStrings(file.getAbsolutePath()).get(i).toCharArray())
					{
						if (c == '/' || c == '\\')
						{
							c = '∕';
						}
						else if (c == ':')
						{
							c = '꞉';
						}

						newString += c;
					}

					//System.out.println(newString);

					Path path = FileSystems.getDefault().getPath(directory.getAbsolutePath() + "/" + newString + ".png");

					if (!newString.isEmpty()) MatrixToImageWriter.writeToPath(matrix, "PNG", path);
				}
			}
		} catch (NumberFormatException | FileNotFoundException e)
		{
			//e.printStackTrace();
		}
	}
}
