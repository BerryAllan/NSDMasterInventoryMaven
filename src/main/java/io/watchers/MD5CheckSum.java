package io.watchers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class MD5CheckSum
{
	public static byte[] createChecksum(String filename) throws IOException, NoSuchAlgorithmException
	{
		File file = new File(filename);
		//System.out.println(file.getAbsolutePath());

		if (file.isDirectory())
		{
			ArrayList<byte[]> bytes = new ArrayList<>();


			for (File f : file.listFiles())
			{
				InputStream fis = new FileInputStream(f);

				byte[] buffer = new byte[1024];
				MessageDigest complete = MessageDigest.getInstance("MD5");
				int numRead;
				do
				{
					numRead = fis.read(buffer);
					if (numRead > 0)
					{
						complete.update(buffer, 0, numRead);
					}
				} while (numRead != -1);
				fis.close();
				bytes.add(complete.digest());
			}

			ArrayList<Byte> bytes1 = new ArrayList<>();
			for (byte[] bs : bytes)
			{
				for (byte b : bs)
				{
					bytes1.add(b);
				}
			}

			byte[] bytePrims = new byte[bytes1.size()];
			Byte[] byteObjs = bytes1.toArray(new Byte[bytes1.size()]);

			for (int i = 0; i < byteObjs.length; i++)
				bytePrims[i] = byteObjs[i];

			return bytePrims;
		}
		else
		{
			InputStream fis = new FileInputStream(file);

			byte[] buffer = new byte[1024];
			MessageDigest complete = MessageDigest.getInstance("MD5");
			int numRead;
			do
			{
				numRead = fis.read(buffer);
				if (numRead > 0)
				{
					complete.update(buffer, 0, numRead);
				}
			} while (numRead != -1);
			fis.close();
			return complete.digest();
		}
	}

	// a byte array to a HEX string
	public static String getMD5Checksum(String filename) throws IOException, NoSuchAlgorithmException
	{
		byte[] b = createChecksum(filename);
		String result = "";
		for (int i = 0; i < b.length; i++)
		{
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}


}