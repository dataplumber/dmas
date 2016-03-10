/**
 * 
 */
package gov.nasa.podaac.inventory.exceptions;

/**
 * @author clwong
 * @version
 * $Id: InventoryException.java 346 2007-10-18 15:25:59Z clwong $ 
 */
@SuppressWarnings("serial")
public class InventoryException extends Exception {

	/**
	 * 
	 */
	public InventoryException() {
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InventoryException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public InventoryException(final String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public InventoryException(final Throwable cause) {
		super(cause);
	}

}
