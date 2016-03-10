package gov.nasa.podaac.inventory.api;

import gov.nasa.podaac.common.api.metadatamanifest.MetadataManifest;
import gov.nasa.podaac.inventory.exceptions.InventoryException;


public interface MetadataObject {

	public MetadataManifest getTemplate() throws InventoryException;
	public MetadataManifest update(MetadataManifest mf) throws InventoryException;
	public MetadataManifest addNew(MetadataManifest mf) throws InventoryException;
	public MetadataManifest delete(MetadataManifest mf) throws InventoryException;
	public MetadataManifest getInstance(MetadataManifest mf) throws InventoryException;
	
}
