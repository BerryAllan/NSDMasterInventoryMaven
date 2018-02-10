package io.watchers;

import java.io.File;
import java.io.FileFilter;

public class DirFilterWatcher implements FileFilter
{
	private String filter;

	public DirFilterWatcher()
	{
		this.filter = "";
	}

	public DirFilterWatcher(String filter)
	{
		this.filter = filter;
	}

	public boolean accept(File file)
	{
		return "".equals(filter) || (file.getName().endsWith(filter));
	}
}