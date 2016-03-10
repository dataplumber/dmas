/**
 * 
 */
package gov.nasa.podaac.inventory.api;

import gov.nasa.podaac.inventory.core.DataManagerImpl;

/**
 * @author clwong
 * $Id: DataManagerFactory.java 291 2007-10-05 21:53:21Z clwong $
 */
public class DataManagerFactory {

	private static DataManagerFactory DataManagerFactory = new DataManagerFactory();

	/**
	 * Default constructor.
	 */
	private DataManagerFactory() {
	}

	/**
	 * Gets an instance of DataManagerFactory object.
	 * 
	 * @return DataManagerFactory object.
	 */
	 public static DataManagerFactory getInstance() {
	    return DataManagerFactory;
	 }

	 public DataManager createDataManager() {
		return new DataManagerImpl();
	 }

}
