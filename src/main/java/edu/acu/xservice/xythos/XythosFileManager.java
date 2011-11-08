/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.acu.xservice.xythos;

import edu.acu.xservice.api.FileManager;
import edu.acu.xservice.api.DirectoryEntry;
import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import com.xythos.common.api.VirtualServer;
import com.xythos.common.api.XythosException;
import com.xythos.security.api.Context;
import com.xythos.storageServer.api.FileSystem;
import com.xythos.storageServer.api.FileSystemDirectory;
import com.xythos.storageServer.api.FileSystemDirectory;
import com.xythos.storageServer.api.FileSystemEntry;
import com.xythos.storageServer.api.FileSystemFile;
import edu.acu.xservice.EntryException;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hgm02a
 */
@RequestScoped
public class XythosFileManager implements FileManager {

	private static final Logger logger = LoggerFactory.getLogger(XythosFileManager.class);
	@Inject
	private Context context;

	@Override
	public DirectoryEntry getDirectoryEntry(String path) throws EntryException {
		try {
			VirtualServer virtualServer = context.getContextUser().getHomeDirectoryVirtualServer();
			if (virtualServer == null) {
				logger.error("VirtualServer is null");
				throw new EntryException("Could not get VirtualServer");
			}
			logger.trace("VirtualServer - {}", virtualServer.getName());
			FileSystemEntry entry = FileSystem.findEntry(virtualServer, path, false, context);
			if (entry instanceof FileSystemFile) {
				return new XythosFile((FileSystemFile) entry, this);
			} else if (entry instanceof FileSystemDirectory) {
				return new XythosDirectory((FileSystemDirectory) entry, this);
			}
			logger.debug("weird");
			return null;
		} catch (XythosException ex) {
			throw new EntryException(ex);
		}
	}

	String getDisplayName(String principalID) throws EntryException {
		try {
			return XythosUtil.getPrincipalDisplayName(principalID, context.getContextUser());
		} catch (XythosException ex) {
			throw new EntryException(ex);
		}
	}

	@Override
	public String getEtag(DirectoryEntry entry) throws EntryException {
		if (entry instanceof com.xythos.fileSystem.File) {
			try {
				return ((com.xythos.fileSystem.File) entry).getLatestRevision().getETag();
			} catch (XythosException ex) {
			}
		}
		return null;
	}
}
