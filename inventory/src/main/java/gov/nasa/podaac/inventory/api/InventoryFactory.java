package gov.nasa.podaac.inventory.api;

import gov.nasa.podaac.inventory.core.InventoryImpl;

/**
 * @author clwong
 * $Id: InventoryFactory.java 439 2007-12-03 22:42:41Z clwong $
 */
public class InventoryFactory {

	private static InventoryFactory inventoryFactory = new InventoryFactory();
	
	/**
	 * Default constructor.
	 */
	private InventoryFactory() {
	}

	/**
	 * Gets an instance of InventoryFactory object.
	 * 
	 * @return InventoryFactory object.
	 */
	 public static InventoryFactory getInstance() {
	    return inventoryFactory;
	 }

	 public Inventory createInventory() {
		return new InventoryImpl();
	 }
}
