/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.acu.xservice.xythos;

import com.xythos.common.api.XythosException;
import edu.acu.xservice.api.Directory;
import edu.acu.xservice.api.DirectoryEntry;
import edu.acu.xservice.xythos.XythosFile;
import com.xythos.storageServer.api.FileSystemDirectory;
import com.xythos.storageServer.api.FileSystemEntry;
import com.xythos.storageServer.api.FileSystemFile;
import edu.acu.xservice.EntryException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hgm02a
 */
public class XythosDirectory extends XythosDirectoryEntry implements Directory {
	
	private final XythosFileManager manager;
	private final FileSystemDirectory directory;

	public XythosDirectory(FileSystemDirectory directory, XythosFileManager manager) throws EntryException {
		super(directory, manager);
		this.directory = directory;
		this.manager = manager;
	}

	public List<DirectoryEntry> getContents() throws EntryException {
		List<DirectoryEntry> contents = new ArrayList<DirectoryEntry>();
		try {
			FileSystemEntry[] entries = directory.getReadableDirectoryContents(false);
			for (FileSystemEntry e : entries) {
				if (e instanceof FileSystemFile) {
					contents.add(new XythosFile((FileSystemFile) e, manager));
				} else if (e instanceof FileSystemDirectory) {
					contents.add(new XythosDirectory((FileSystemDirectory) e, manager));
				}
			}
			return contents;
		} catch (XythosException ex) {
			throw new EntryException(ex);
		} 
	}

	@Override
	public String getType() {
		return "folder";
	}
	
}
