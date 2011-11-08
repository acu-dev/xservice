/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.acu.xservice.xythos;

import com.xythos.common.api.XythosException;
import com.xythos.storageServer.api.InvalidRequestException;
import edu.acu.xservice.api.File;
import com.xythos.storageServer.api.FileSystemFile;
import edu.acu.xservice.EntryException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hgm02a
 */
public class XythosFile extends XythosDirectoryEntry implements File {

	private final FileSystemFile file;

	public XythosFile(FileSystemFile file, XythosFileManager manager) throws EntryException {
		super(file, manager);
		this.file = file;
	}

	public long getSize() {
		return this.file.getEntrySize();
	}

	public String getUrl() {
		return "";
	}
	
}
