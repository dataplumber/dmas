/**
 * 
 */
package gov.nasa.podaac.inventory.api;

import gov.nasa.podaac.common.api.metadatamanifest.MetadataManifest;
import gov.nasa.podaac.common.api.serviceprofile.IngestProfile;
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfile;
import gov.nasa.podaac.inventory.exceptions.InventoryException;

/**
 * @author clwong
 * $Id: Inventory.java 9507 2012-02-09 00:54:36Z gangl $
 */
public interface Inventory {

	public MetadataManifest processManifest(MetadataManifest metadataManifest)
							throws InventoryException;
	
	public MetadataManifest processManifest(MetadataManifest metadataManifest, String user)	throws InventoryException;
	
	public void storeServiceProfile(ServiceProfile serviceProfile) 
																throws InventoryException;
	
	public void storeIngestProfile(IngestProfile ingestProfile) throws InventoryException;
	
	public void closeSession();
	
}
