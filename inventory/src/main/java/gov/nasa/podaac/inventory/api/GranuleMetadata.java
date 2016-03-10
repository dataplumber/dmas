package gov.nasa.podaac.inventory.api;

import java.util.Date;

import gov.nasa.podaac.inventory.exceptions.InventoryException;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.api.Constant;

/**
 * @author clwong
 * $Id: GranuleMetadata.java 5666 2010-08-25 19:26:33Z gangl $
 */
public interface GranuleMetadata {

	public Granule getGranule();

	public void setGranule(Granule granule);
	
	public gov.nasa.podaac.common.api.serviceprofile.Granule getMetadata();
	
	public void setMetadata(gov.nasa.podaac.common.api.serviceprofile.Granule metadata);
	
	public String getArchiveBasePath();

	public void setArchiveBasePath(String archiveBasePath);

	public String getArchiveSubDir();

	public void setArchiveSubDir(String archiveSubDir);

	public void mapToGranule() throws InventoryException;
	
	public void addGranuleArchive(String checksum, Character compressFlag, Long fileSize,
			String path, String type);
	
	public void addGranuleReference(String filename);
	
	public void addGranuleHistory() throws InventoryException;
	
	public void addGranule();
	
	public String appendArchiveSubDir(String basePathAppendType, Date startTime, Integer cycle ) throws InventoryException;
	
	public void updateGranule();
	
	public void setGranuleRootPath(String rootPath);
	
	public void setGranuleRelPath(String relPath);
	
	public void updateGranuleStatus(Constant.GranuleStatus status);
	
	public Constant.GranuleStatus getGranuleStatus();

}
