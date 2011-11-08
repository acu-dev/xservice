
package edu.acu.xservice;

/**
 *
 * @author hgm02a
 */
public class EntryException extends Exception {

	public EntryException(Throwable thrwbl) {
		super(thrwbl);
	}

	public EntryException(String string, Throwable thrwbl) {
		super(string, thrwbl);
	}

	public EntryException(String string) {
		super(string);
	}

	public EntryException() {
	}
	
}
