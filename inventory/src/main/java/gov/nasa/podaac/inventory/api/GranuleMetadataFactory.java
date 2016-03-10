/**
 * 
 */
package gov.nasa.podaac.inventory.api;

import gov.nasa.podaac.inventory.core.GranuleMetadataImpl;


/**
 * @author clwong
 * $Id: GranuleMetadataFactory.java 249 2007-10-02 22:59:41Z clwong $
 */
public class GranuleMetadataFactory {
	
	private static GranuleMetadataFactory granuleMetadataFactory = new GranuleMetadataFactory();

	/**
	 * Default constructor.
	 */
	private GranuleMetadataFactory() {
	}

	/**
	 * Gets an instance of GranuleMetadataFactory object.
	 * 
	 * @return GranuleMetadataFactory object.
	 */
	 public static GranuleMetadataFactory getInstance() {
	    return granuleMetadataFactory;
	 }

	 public GranuleMetadata createGranuleMetadata() {
		return new GranuleMetadataImpl();
	 }
}
