package gov.nasa.podaac.distribute.subscriber.plugins;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import gov.nasa.podaac.common.api.file.FileProduct;
import gov.nasa.podaac.common.api.util.FileProductUtility;
import gov.nasa.podaac.common.api.util.Hostmap;
import gov.nasa.podaac.common.api.util.Hostmap.HostInfo;
import gov.nasa.podaac.distribute.subscriber.api.DataRetriever;
import gov.nasa.podaac.distribute.subscriber.model.Granule;
import gov.nasa.podaac.distribute.subscriber.model.GranuleFile;
import gov.nasa.podaac.distribute.subscriber.model.GranuleReference;


public class BasicDataRetrieverNotifier implements DataRetriever {
	
	static Log _logger = LogFactory.getLog(BasicDataRetrieverNotifier.class);
	static Hostmap hm; 
	
        // Additional function to look and fetch from local file system first.

	
	public List<String> get(Granule granule, String outputDir) throws IOException {
	
		
		List<String> returned = new ArrayList<String>();
		
		if(granule == null)
		{
			_logger.info("Granule is null. Returning.");
			return returned;
		}
		
		if(granule.getFileList().isEmpty())
		{
			_logger.info("No files exist in granule["+granule.getName()+"]. Returning.");
			return returned;
		}
		
		for(GranuleFile g : granule.getFileList())
		{
			
			_logger.info("Attempting to fetch file " + g.getName());	
			_logger.info("GranulePath: " + g.getPath());
			
            String fullPathInputName   = "";
            if (g.getPath().startsWith("file")) {
                 // Get the name, ignoring "file://" token.
                fullPathInputName  = g.getPath().substring(7) + File.separator + g.getName();
            } else {
                fullPathInputName  = g.getPath()              + File.separator + g.getName();
            }
            returned.add(fullPathInputName.trim());
                       
			
			//try local FTP if we can't get the file locally
			String localFtpPath = null;
			boolean foundRef = false;
			for(GranuleReference ref: granule.getReferenceList())
			{
				if(ref.getType().equals("LOCAL-FTP"))
				{
					localFtpPath = ref.getPath();
					localFtpPath = localFtpPath.substring(0, localFtpPath.lastIndexOf("/"));					
					//System.out.println("Path to look for files in: " + localFtpPath);
					foundRef = true;
				}
				else
					continue;
				
				String user, auth = null;
				_logger.info("fileName: "+ g.getName());
				_logger.info("Geting file: " + localFtpPath +"/"+g.getName());
				String file =  localFtpPath +"/"+g.getName();
				returned.add(file.trim());
				
//				
			}
			// TODO Auto-generated catch block
			//e.printStackTrace();	
		}
			
				//System.out.println("Cleaning up readers & writers");
		return returned;

	}

}
