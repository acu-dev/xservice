/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.acu.xservice.api;

import edu.acu.xservice.EntryException;

/**
 *
 * @author hgm02a
 */
public interface FileManager {
	
	public DirectoryEntry getDirectoryEntry(String path) throws EntryException;

	public String getEtag(DirectoryEntry entry) throws EntryException;
	
}
