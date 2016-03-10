//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.podaac.archive.core;

import gov.nasa.podaac.archive.exceptions.ArchiveException;
import gov.nasa.podaac.archive.external.FileUtil;
import gov.nasa.podaac.inventory.api.Constant.GranuleArchiveStatus;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class contains implementation of interface DataStore.
 * It store files locally.
 *
 * @author clwong
 *
 * @version
 * $Id: LocalDataStore.java 5358 2010-07-23 17:18:20Z gangl $
 */
class LocalDataStore implements DataStore {
	
	private static Log log = LogFactory.getLog(LocalDataStore.class);
	private static boolean doChecksum = true;
	
	public void setChecksumOption(boolean checksum){
		doChecksum=checksum;
	}
	
	public boolean verify(URL url) throws ArchiveException {
		File file = null;
		try {
			file = new File(url.toURI());
		} catch (URISyntaxException e) {
			throw new ArchiveException(
					GranuleArchiveStatus.MISSING.toString()+": "
					+"URL "+url.toString()+" URISyntaxException!"
					+"\n"+e.getMessage());
		}
		if (!file.exists()) {
			throw new ArchiveException(
					GranuleArchiveStatus.MISSING,
					GranuleArchiveStatus.MISSING.toString()+": "
					+file.getPath() + ": not existed");
		}
		
		if (!file.isFile()) {
			throw new ArchiveException(
					GranuleArchiveStatus.MISSING,
					GranuleArchiveStatus.MISSING.toString()+": "
					+file.getPath() + ": not a file");
		}
		return true;
	}
	
	@SuppressWarnings("static-access")
	public boolean verify(URI uri, long fileSize, String checksum, String checksumType) 
								throws ArchiveException {
		log.debug("verify: "+uri);	
		try {
			verify(uri.toURL());	
		} catch (ArchiveException ex) {
			throw new ArchiveException(ex.getGranuleStatus(),
					ex.getMessage());
		} catch (MalformedURLException ex) {
			throw new ArchiveException(GranuleArchiveStatus.MISSING,
					GranuleArchiveStatus.MISSING.toString()+": "
					+"URI "+uri.toString()+" MalformedURLException!"
					+"\n"+ex.getMessage());
		}
		File file = new File(uri);
		// verify checksum
		if(doChecksum){
			if (!FileUtil.validateChecksum(file, checksum, checksumType)) {
				throw new ArchiveException(GranuleArchiveStatus.CORRUPTED, 
						GranuleArchiveStatus.CORRUPTED.toString()+": "
						+file.getPath() + ": checksum not matched");
			}
		}
		else{
			log.debug("Skipping checksum option");
		}
		
		// verify file size
		if (file.length() != fileSize) {
			throw new ArchiveException(GranuleArchiveStatus.CORRUPTED, 
					GranuleArchiveStatus.CORRUPTED.toString()+": "
					+file.getPath() + ": file size unmatched");
		}

				
		return true;
	}
}
