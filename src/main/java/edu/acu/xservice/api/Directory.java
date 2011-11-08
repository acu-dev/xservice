/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.acu.xservice.api;

import edu.acu.xservice.EntryException;
import java.util.List;

/**
 *
 * @author hgm02a
 */
public interface Directory extends DirectoryEntry {
	
	public List<DirectoryEntry> getContents() throws EntryException;
	
}
