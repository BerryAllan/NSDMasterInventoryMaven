package io;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BarcodeReader //unused as of now
{
	public static String readQRCode(File filePath, Map hintMap) throws IOException, NotFoundException
	{
		BinaryBitmap binaryBitmap =
				new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(ImageIO.read(new FileInputStream(filePath)))));
		Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap, hintMap);
		return qrCodeResult.getText();
	}

	public String decodeBarCode(File codeImage) throws IOException, NotFoundException, FormatException, ChecksumException
	{

		BufferedImage bufferedImage = ImageIO.read(codeImage);
		LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

		HashMap<DecodeHintType, Object> decodeHintMap = new HashMap<>();
		decodeHintMap.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);

		try
		{
			Result result = new MultiFormatReader().decode(bitmap);
			return result.getText();
		} catch (NotFoundException e)
		{
			System.out.println("There is no code in the image: " + codeImage.getAbsolutePath());
			return null;
		}
	}
}