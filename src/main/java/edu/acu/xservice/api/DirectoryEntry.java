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
public interface DirectoryEntry {

	public String getPath();

	public String getType();

	public String getOwner() throws EntryException;

	public String getCreator() throws EntryException;

	public Date getCreated() throws EntryException;

	public String getDescription() throws EntryException;

	public String getLastUpdater() throws EntryException;

	public Date getLastUpdated() throws EntryException;
}
