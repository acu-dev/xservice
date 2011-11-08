/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.acu.xservice.api;

import edu.acu.xservice.EntryException;
import java.util.Date;

/**
 *
 * @author hgm02a
 */
public interface File extends DirectoryEntry {

	public long getSize() throws EntryException;

	public String getUrl() throws EntryException;
}
