//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.podaac.archive.external;

import gov.nasa.podaac.archive.core.AIP;
import gov.nasa.podaac.archive.core.ArchiveData;
import gov.nasa.podaac.archive.exceptions.ArchiveException;
import gov.nasa.podaac.common.api.serviceprofile.ArchiveFileInfo;
import gov.nasa.podaac.common.api.serviceprofile.ArchiveGranule;
import gov.nasa.podaac.common.api.serviceprofile.ArchiveProfile;
import gov.nasa.podaac.common.api.serviceprofile.IngestDetails;
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfileException;
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfileFactory;
import gov.nasa.podaac.common.api.serviceprofile.Common.ChecksumAlgorithm;
import gov.nasa.podaac.inventory.api.Constant.GranuleStatus;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * This class contains interfaces to ServiceProfile.
 *
 * @author clwong
 * $Id: ServiceProfile.java 5629 2010-08-18 17:52:37Z niessner $
 */
public class ServiceProfile {
	
	private static Log log = LogFactory.getLog(ServiceProfile.class);
	private static gov.nasa.podaac.common.api.serviceprofile.ServiceProfile serviceProfile;
	
	public static Map<Integer, List<AIP>> createAIPById () throws ArchiveException
	{
		log.debug("createAIPById...");	
		ArchiveProfile ap = getArchiveProfile();
		if (ap==null) {
			log.debug("Missing ArchiveProfile");
			throw new ArchiveException("Missing ArchiveProfile");
		}
		List<ArchiveGranule> archiveGranules = ap.getGranules();
		Map<Integer, List<AIP>> aipById = new TreeMap<Integer, List<AIP>>();
		for (ArchiveGranule archiveGranule : archiveGranules) {			
			// create AIP mapped by granuleId
			List<ArchiveFileInfo> archiveFiles = archiveGranule.getFiles();
			List<AIP> aipList = new ArrayList<AIP>(archiveFiles.size());
			for (ArchiveFileInfo archiveFile : archiveFiles) {
				// verify if localStaging exists
				IngestDetails ingestDetails = archiveFile.getIngestDetails();
				
				AIP aip = new AIP();
				aip.setGranuleId((Integer) archiveGranule.getGranuleId().intValue());
				URI localURI = ingestDetails.getLocalStaging();
				if (!(new File(localURI)).exists()) {				
					aip.setStaging(ingestDetails.getRemoteStaging());
				} else {
					aip.setStaging(ingestDetails.getLocalStaging());
				}				
				aip.setDestination(archiveFile.getDestination());
				aip.setChecksum(ingestDetails.getChecksum());
				aip.setAlgorithm(toString(ingestDetails.getChecksumAlgorithm()));
				aip.setFileSize(ingestDetails.getSize());
				aip.setType(archiveFile.getType().toString());
				aipList.add(aip);
				log.debug(aip.getStaging());
			}
			aipById.put((Integer) archiveGranule.getGranuleId().intValue(), aipList);
		}
		
		return aipById;
	};

	public static void createServiceProfile(String message) throws ArchiveException
	{
		log.debug("createServiceProfile...");
		try {
			serviceProfile = ServiceProfileFactory.getInstance()
				.createServiceProfileFromMessage(message);
		} catch (ServiceProfileException e) {
			log.debug(message);
			throw new ArchiveException(e.getMessage(), e.getCause());
		}
	}

	public static List<String> deletes() {
		ArchiveProfile ap = getArchiveProfile();
		if (ap==null) return null;
		log.debug("processDeletes...");
		List<ArchiveGranule> archiveGranules = ap.getGranules();
		Map<String, String> dsBaseLocation = new TreeMap<String, String>();
		List<String> savedList = new ArrayList<String>();
		for (ArchiveGranule archiveGranule : archiveGranules) {
			if (archiveGranule.getDeleteName()==null) continue;
			List<String> deletes = archiveGranule.getDeletes();
			if (deletes==null) continue;
			if (deletes.isEmpty()) continue;
			
			if (archiveGranule.getGranuleId()==null) {
				log.warn("Cannot find this granule!");
				continue;
			}
			Integer granuleVersion = InventoryFactory.getInstance().getQuery().getGranuleVersion(
									archiveGranule.getGranuleId().intValue());
			if (granuleVersion==null) {
				log.warn("Set Version=0; Cannot find version for this granule!");
				granuleVersion=1;
			}
			String trashBasePath = 
				System.getProperty("archive.trash")+"/"
				+archiveGranule.getGranuleId()+"/"
				+archiveGranule.getDeleteName()+"/"
				+(granuleVersion-1)+"/";
			
			String datasetName = archiveGranule.getDatasetName();
			String baseLocation = null;
			if (dsBaseLocation.containsKey(datasetName))
				baseLocation = dsBaseLocation.get(datasetName);
			else {
				baseLocation = InventoryFactory.getInstance().getQuery().getArchiveBaseLocation(datasetName);
				dsBaseLocation.put(datasetName, baseLocation);
			}
			List<String> backupList = ArchiveData.delete(
					archiveGranule.getDeletes(), baseLocation, trashBasePath);
			if (backupList==null) continue;
			String msg = backupList.isEmpty() ? "" : "\nFiles are saved up to: ";
			for (String backup : backupList) {
				msg += "\n"+backup;
				savedList.add("file://"+backup);
			}
			if (!msg.equals("")) log.info("\n"+msg+"\n");

			// clear the deletes in the section
			archiveGranule.clearDeletes();
		} // for archiveGranules
		return savedList;
	}
	
    public static ArchiveProfile getArchiveProfile() {
		ArchiveProfile ap = serviceProfile.getProductProfile().getArchiveProfile();
		if (ap==null) log.warn("ServiceProfile has Null ArchiveProfile!");
		return ap;
	}
    
    public static gov.nasa.podaac.common.api.serviceprofile.ServiceProfile getServiceProfile() {
		return serviceProfile;
	}
	
	public static String prettyPrint(String xmlString){
		log.debug("prettyPrint...");	
		Document document = null;
		try {
			document = DocumentHelper.parseText(xmlString);
			OutputFormat format = OutputFormat.createPrettyPrint();
			StringWriter stringWriter = new StringWriter();
			XMLWriter writer = new XMLWriter(stringWriter, format);
	        writer.write( document );
	        return stringWriter.toString();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;		
	}

	public static String printArchiveProfile()
	{ 
		log.debug("printArchiveProfile...");
		//return prettyPrint(getArchiveProfile().toString());
		
		String statusMsg = "";
		ArchiveProfile ap = getArchiveProfile();
		if (ap==null) {
			statusMsg += "Missing ArchiveProfile";
			log.debug(statusMsg);
			return statusMsg;
		}
		List<ArchiveGranule> archiveGranules = ap.getGranules();
		for (ArchiveGranule archiveGranule : archiveGranules) {
			List<ArchiveFileInfo> archiveFiles = archiveGranule.getFiles();
			statusMsg +="\n\n***** ARCHIVE STARTS @ "+archiveGranule.getArchiveStartTime()+" *****\n"
				+"\nGranule ID: "+ archiveGranule.getGranuleId()
				+"   Name: "+archiveGranule.getName()
				+"\nDataset: "+archiveGranule.getDatasetName()	
				+"   ProductType: "+ archiveGranule.getProductType()
				+"\n\nNo. Files: "+archiveFiles.size();
			for (ArchiveFileInfo archiveFile : archiveFiles) {
				statusMsg += "\n"+ archiveFile.getDestination();				
			}		
			statusMsg+= "\n\nNote: "+archiveGranule.getArchiveNote();
			statusMsg+="\n\n***** ARCHIVE ENDS @ "+archiveGranule.getArchiveStopTime()+" *****\n";
		}
		return statusMsg;
	}

	public static String printArchiveProfile(String inServiceProfile)
	{ 
		log.debug("printArchiveProfile...");
		try {
			serviceProfile = ServiceProfileFactory.getInstance()
							.createServiceProfileFromMessage(inServiceProfile);
		} catch (ServiceProfileException e) {
			e.printStackTrace();
		}
		return printArchiveProfile();
	}
	
	public static String printServiceProfile()
	{ 
		log.debug("printServiceProfile...");	
		return prettyPrint(serviceProfile.toString());	
	}
	
	public static void setServiceProfile(
			gov.nasa.podaac.common.api.serviceprofile.ServiceProfile serviceProfile) {
		ServiceProfile.serviceProfile = serviceProfile;
	}

	public static ChecksumAlgorithm toChecksumAlgorithm(String checksumType) {
        if (checksumType.equalsIgnoreCase("MD2"))
           return ChecksumAlgorithm.MD2;
        if (checksumType.equalsIgnoreCase("MD5"))
           return ChecksumAlgorithm.MD5;
        if (checksumType.equalsIgnoreCase("SHA1"))
           return ChecksumAlgorithm.SHA1;
        if (checksumType.equalsIgnoreCase("SHA-256"))
           return ChecksumAlgorithm.SHA256;
        if (checksumType.equalsIgnoreCase("SHA-384"))
           return ChecksumAlgorithm.SHA384;
        if (checksumType.equalsIgnoreCase("SHA-512"))
           return ChecksumAlgorithm.SHA512;

        return null;
    }
	
	public static String toString(ChecksumAlgorithm checksumAlgorithm) {
        if (checksumAlgorithm == ChecksumAlgorithm.MD2)
           return "MD2" ;
        if (checksumAlgorithm == ChecksumAlgorithm.MD5)
           return "MD5";
        if (checksumAlgorithm == ChecksumAlgorithm.SHA1)
            return "SHA1";
        if (checksumAlgorithm == ChecksumAlgorithm.SHA256)
            return "SHA-256";
        if (checksumAlgorithm == ChecksumAlgorithm.SHA384)
            return "SHA-384";
        if (checksumAlgorithm == ChecksumAlgorithm.SHA512)
            return "SHA-512";
        return null;
    }
	
	public static void updateArchiveGranule(Map<Integer, List<AIP>> aipById) {
		ArchiveProfile ap = getArchiveProfile();
		if (ap==null) return;
		log.debug("updateArchiveGranule...");
		List<ArchiveGranule> archiveGranules = ap.getGranules();
		for (ArchiveGranule archiveGranule : archiveGranules) {
			log.debug("granuleId="+archiveGranule.getGranuleId());
			Integer id = (Integer) archiveGranule.getGranuleId().intValue();
			List<AIP> aipList = aipById.get(id);
			archiveGranule.setArchiveStartTime(aipList.get(0).getArchiveGranuleStartDate());
			archiveGranule.setArchiveStopTime(aipList.get(0).getArchiveGranuleEndDate());
			archiveGranule.setArchiveSuccess(true);
			archiveGranule.setArchiveNote("");
			List<ArchiveFileInfo> archiveGranuleFiles = archiveGranule.getFiles();
			for (ArchiveFileInfo archiveGranuleFile : archiveGranuleFiles) {
				URI destination = archiveGranuleFile.getDestination();
				AIP aipInstance = new AIP();
				aipInstance.setDestination(destination);
				aipInstance.setGranuleId(id);
				AIP aip = aipList.get(aipList.indexOf(aipInstance));
				archiveGranuleFile.setArchiveStartTime(aip.getArchiveFileStartDate());
				archiveGranuleFile.setArchiveStopTime(aip.getArchiveFileEndDate());
				if (!aip.getStatus().equals(GranuleStatus.ONLINE.toString())) {
					archiveGranule.setArchiveSuccess(false);
					archiveGranule.setArchiveNote(archiveGranule.getArchiveNote()
						+ aip.getNote() + "\n");
				}
			}
			if (archiveGranule.isArchiveSuccess())
				archiveGranule.setArchiveNote(GranuleStatus.ONLINE.toString());
		}
	}
	
	protected ServiceProfile () {} 
}
