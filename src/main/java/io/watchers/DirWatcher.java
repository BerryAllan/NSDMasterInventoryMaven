package io.watchers;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TimerTask;

public abstract class DirWatcher extends TimerTask
{
	private String path;
	private File filesArray[];
	private HashMap<File, Long> dir = new HashMap<>();
	private DirFilterWatcher dfw;

	public DirWatcher(String path)
	{
		this(path, "");
	}

	public DirWatcher(String path, String filter)
	{
		this.path = path;
		dfw = new DirFilterWatcher(filter);
		filesArray = new File(path).listFiles(dfw);

		// transfer to the hashmap be used a reference and keep the
		// lastModfied value
		for (File aFilesArray : filesArray)
		{
			dir.put(aFilesArray, aFilesArray.lastModified());
		}
	}

	public final void run()
	{
		HashSet<File> checkedFiles = new HashSet<>();
		filesArray = new File(path).listFiles(dfw);

		// scan the files and check for modification/addition
		for (File file : filesArray)
		{
			Long current = dir.get(file);
			checkedFiles.add(file);
			if (current == null)
			{
				// new file
				dir.put(file, file.lastModified());
				onChange(file, "add");
			}
			else if (current != file.lastModified())
			{
				// modified file
				dir.put(file, file.lastModified());
				onChange(file, "modify");
			}
		}

		// now check for deleted files
		Set ref = ((HashMap) dir.clone()).keySet();
		ref.removeAll(checkedFiles);
		for (Object aRef : ref)
		{
			File deletedFile = (File) aRef;
			dir.remove(deletedFile);
			onChange(deletedFile, "delete");
		}
	}

	protected abstract void onChange(File file, String action);
}