package io.watchers;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.TimerTask;

public abstract class CheckSumWatcher extends TimerTask
{
	private String path;
	private String originalCheckSum;

	public CheckSumWatcher(String path, String originalCheckSum)
	{
		this.path = path;
		this.originalCheckSum = originalCheckSum;
	}

	@Override
	public void run()
	{
		String newCheckSum = null;

		try
		{
			newCheckSum = MD5CheckSum.getMD5Checksum(path);
		} catch (NoSuchAlgorithmException | IOException e)
		{
			e.printStackTrace();
		}

		//System.out.println("o " + originalCheckSum);
		//System.out.println("c " + Main.checkSumDatabase);
		//System.out.println("n " + newCheckSum);

		if (!originalCheckSum.equals(newCheckSum))
		{
			onChange(path);
			originalCheckSum = newCheckSum;
		}
	}

	protected abstract void onChange(String path);
}
