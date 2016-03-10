//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.podaac.archive.core;

import gov.nasa.podaac.archive.external.InventoryFactory;
import gov.nasa.podaac.inventory.api.Constant.GranuleStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class contains methods to process verify requests.
 *
 * @author clwong
 * $Id: Verify.java 13211 2014-04-25 18:22:07Z gangl $
 */
public class Verify {
	private static Log log = LogFactory.getLog(Verify.class);
	private static Map<String,Map<Integer, List<String>>> result = null;
	private static Map<Integer, Integer> onlineList = null;
					// result stored as [status, [id, <paths>]]
	private static final int MAX_GRANULE_REQUEST_ALLOW =1000;
	private static boolean doChecksum = true;
	

	public static void verifyByDataset(Integer datasetId, List<Integer>granuleIdList) {
		log.debug("verfiyByDataset: datasetId="+datasetId);
		long time = System.currentTimeMillis();
		Date startTime = new Date();

		Integer granuleSize = InventoryFactory.getInstance().getQuery().getGranuleSizeByDataset(datasetId);
		if (granuleSize>MAX_GRANULE_REQUEST_ALLOW || granuleSize > granuleIdList.size()) {
			verifyByGranule(granuleIdList);
			return;
		}
		List<AIP> archiveList = new ArrayList<AIP>();
		List<AIP> refList = new ArrayList<AIP>();
		int archiveVerifyCount = 0;
		int referenceVerifyCount = 0;
		String archiveResultFormat = "";
		String refResultFormat = "";

		if (granuleSize>0) {
			archiveList = InventoryFactory.getInstance().getQuery().getArchiveAIPByDataset(datasetId);
			if (archiveList.size()>0) {
				archiveVerifyCount = verifyArchive(archiveList);
				if (archiveVerifyCount<0) {
					archiveVerifyCount = 0;
					archiveResultFormat = "\n\nVerification skipped.\n";
				} else archiveResultFormat = "\n\n"+formatResult();
			}					

			refList = InventoryFactory.getInstance().getQuery().getReferenceAIPByDataset(datasetId);
			if (refList.size()>0) {
				referenceVerifyCount = verifyReference(refList);
				if (referenceVerifyCount<0) {
					referenceVerifyCount = 0;
					refResultFormat = "\n\nVerification skipped.\n";
				} else refResultFormat = "\n\n"+formatResult();
			}
		}

		time = System.currentTimeMillis() - time;

		log.info(
				"\n\n===== "+startTime+"   Archive Verification Summary =====\n"
				+"\nDataset Id\t:\t"+datasetId
				+"\nNo. Granules\t:\t"+granuleSize
				+"\nNo. Archives\t:\t"+archiveList.size()+"\tFailures:\t"+archiveVerifyCount
				+archiveResultFormat
				+"\nNo. References\t:\t"+refList.size()+"\tFailures:\t"+referenceVerifyCount
				+refResultFormat
				+"\nProcess Time\t:\t"+time/1000. + " seconds"
				+"\n\n===== "+new Date()+"   End of Summary ===================\n"
		);
	}
	
	public static void verifyByGranule(List<Integer> granuleIdList, boolean verifyChecksum){
		doChecksum = verifyChecksum;
		Verify.verifyByGranule(granuleIdList);
	}
	
	public static void verifyByGranule(List<Integer> granuleIdList) {
		int granuleSize = granuleIdList.size();
		log.debug("verifyByGranule:"+granuleSize);
		if (granuleSize>MAX_GRANULE_REQUEST_ALLOW) {
			log.warn("Number of granules["+granuleSize
					+"] requested exceeds limit ["+MAX_GRANULE_REQUEST_ALLOW+"]!");		
			for (int i=0; i<granuleSize; i+=MAX_GRANULE_REQUEST_ALLOW) {
				verifyByGranule(
						new ArrayList<Integer>(granuleIdList.subList(i,
						i+MAX_GRANULE_REQUEST_ALLOW-1<granuleSize?i+MAX_GRANULE_REQUEST_ALLOW:granuleSize))
						);
			}
			return;
		}
		long time = System.currentTimeMillis();
		Date startTime = new Date();
		
		List<AIP> archiveList = new ArrayList<AIP>();
		List<AIP> refList = new ArrayList<AIP>();
		int archiveVerifyCount = 0;
		int referenceVerifyCount = 0;
		String archiveResultFormat = "";
		String refResultFormat = "";
		if ((granuleIdList != null) && (!granuleIdList.isEmpty())) {
			
			/*
			 * Get archive list...
			 */
			
			try {
				archiveList = InventoryFactory.getInstance().getQuery().getArchiveAIPByGranule(granuleIdList);
			} catch (Exception e) { 
				e.printStackTrace(); 
			}
			
			int onlineArchive = 0;
			int onlineReference = 0;
			
			/*
			 * Verify the Archives
			 */
			if ((archiveList != null) && (!archiveList.isEmpty())) {
				archiveVerifyCount = verifyArchive(archiveList,doChecksum);
				if (archiveVerifyCount<0) {
					archiveVerifyCount = 0;
					archiveResultFormat = "\n\nVerification skipped.\n";
				} else archiveResultFormat = "\n\n"+formatResult();
				
				for(AIP aip: archiveList){
					if(new String("ONLINE").equals(aip.getStatus()))
						onlineArchive++;
				}
				
			}		
			
			/*
			 * Verify the references
			 */
			refList = InventoryFactory.getInstance().getQuery().getReferenceAIPByGranule(granuleIdList);
			if ((refList != null) && (!refList.isEmpty())) {
				referenceVerifyCount = verifyReference(refList);
				if (referenceVerifyCount<0) {
					referenceVerifyCount = 0;
					refResultFormat = "\n\nVerification skipped.\n";
				} else refResultFormat = "\n\n"+formatResult();
				
				for(AIP aip: refList){
					if(new String("ONLINE").equals(aip.getStatus()))
						onlineReference++;
				}
				
			}
			
			
			
		}
		time = System.currentTimeMillis() - time;
		log.info(
				"\n\n===== "+startTime+"   Archive Verification Summary =====\n"
				+"\nNo. Granules\t:\t"+granuleSize
				+"\nNo. Archives\t:\t"+archiveList.size()+"\tFailures:\t"+archiveVerifyCount
				+archiveResultFormat
				+"\nNo. References\t:\t"+refList.size()+"\tFailures:\t"+referenceVerifyCount
				+refResultFormat
				+"\nProcess Time\t:\t"+time/1000. + " seconds"
				+"\n\n===== "+new Date()+"   End of Summary ===================\n"
		);
	}
	
	private static int verifyArchive(List<AIP> archiveList, boolean checksum) {
		doChecksum = checksum;
		return verifyArchive(archiveList);
	}
	
	private static int verifyArchive(List<AIP> archiveList) {
		//1 = online
		//2 = no online
		onlineList = new HashMap<Integer,Integer>();
		
		
		log.debug("verifyArchive:"+archiveList.size());
		int failCount = 0;
		result = new TreeMap<String,Map<Integer, List<String>>>();
		List<Integer> failGranuleIdList = new ArrayList<Integer>();
		List<AIP> verifyList = new ArrayList<AIP>();
		for (AIP archive : archiveList) {
			if (ArchiveData.isInProcess(archive)) {
				resultAdd(archive);
			} else {
				verifyList.add(archive);
				log.debug("setting checksum to " + doChecksum);
				if (!ArchiveData.verify(archive, doChecksum)) {
					failCount++;
					if (!failGranuleIdList.contains(archive.getGranuleId()))
						failGranuleIdList.add(archive.getGranuleId());
					resultAdd(archive);
				}
				else{
					log.debug("Archive verified.");
					log.debug("***Status: " + archive.getStatus());
					if(!archive.getStatus().equals("ONLINE") && !archive.getStatus().equals("DELETED")){
						log.debug("Granule is not online, update status.");
						archive.setStatus("ONLINE");
					}
				}
				if(archive.getStatus().equals("ONLINE"))
					onlineList.put(archive.getGranuleId(), 1);
				else{
					if(onlineList.get(archive.getGranuleId()) == null || onlineList.get(archive.getGranuleId()) != 1 )
						onlineList.put(archive.getGranuleId(), 2);
				}
			}
		}
		InventoryFactory.getInstance().getAccess().updateAIPGranuleArchiveStatus(verifyList);
		InventoryFactory.getInstance().getAccess().updateVerifyGranuleStatus(failGranuleIdList, 
				GranuleStatus.ONLINE.toString());
		
		List<Integer> list = new ArrayList<Integer>();
		for(Integer k : onlineList.keySet()){
			if(onlineList.get(k).equals(2)){
				list.add(k);
			}
		}
		InventoryFactory.getInstance().getAccess().updateVerifyGranuleStatus(list, GranuleStatus.OFFLINE.toString());
		list = new ArrayList<Integer>();
		for(Integer k : onlineList.keySet()){
			if(onlineList.get(k).equals(1)){
				list.add(k);
			}
		}
		InventoryFactory.getInstance().getAccess().updateVerifyGranuleStatus(list, GranuleStatus.ONLINE.toString());
		return failCount;
	}
	
	private static int verifyReference(List<AIP> refList) {
		log.debug("verifyReference: "+refList.size());
		int failCount = 0;
		result = new TreeMap<String,Map<Integer, List<String>>>();
		List<AIP> verifyList = new ArrayList<AIP>();
		for (AIP ref : refList) {
			if (ArchiveData.isInProcess(ref)) {
				resultAdd(ref);
			} else {
				verifyList.add(ref);
				if (!ArchiveData.verify(ref)) {
					failCount++;
					//List<AIP> tempList = new ArrayList<AIP>();
					//tempList.add(ref);
					//log.debug("updating reference...");
					//InventoryFactory.getInstance().getAccess().updateAIPGranuleReferenceStatus(tempList);
					resultAdd(ref);
				}
			}
			
			if(ref.getStatus().equals("ONLINE"))
				onlineList.put(ref.getGranuleId(), 1);
			else{
				if(onlineList.get(ref.getGranuleId()) == null || onlineList.get(ref.getGranuleId()) != 1 )
					onlineList.put(ref.getGranuleId(), 2);
			}
			
		}
		InventoryFactory.getInstance().getAccess().updateAIPGranuleReferenceStatus(verifyList);
		List<Integer> list = new ArrayList<Integer>();
		for(Integer k : onlineList.keySet()){
			if(onlineList.get(k).equals(2)){
				list.add(k);
			}
		}
		InventoryFactory.getInstance().getAccess().updateVerifyGranuleStatus(list, GranuleStatus.OFFLINE.toString());
		list = new ArrayList<Integer>();
		for(Integer k : onlineList.keySet()){
			if(onlineList.get(k).equals(1)){
				list.add(k);
			}
		}
		InventoryFactory.getInstance().getAccess().updateVerifyGranuleStatus(list, GranuleStatus.ONLINE.toString());
		
		return failCount;
	}
	
	private static void resultAdd(AIP aip) {
		if (aip==null) return;
		if (!result.containsKey(aip.getStatus())) {
			result.put(aip.getStatus(), new TreeMap<Integer, List<String>>());
		}
		Map<Integer,List<String>> statusMap = result.get(aip.getStatus());
		if (!statusMap.containsKey(aip.getGranuleId())){
			statusMap.put(aip.getGranuleId(), new ArrayList<String>());
		}
		statusMap.get(aip.getGranuleId()).add(aip.getDestination().toString());
		if(aip.getNote() != null){
			if(aip.getNote().contains("ANOMALY")){
				statusMap.get(aip.getGranuleId()).add(aip.getNote().toString());
			}
		}
	}
	
	private static String formatResult() {
		Iterator<Entry<String,Map<Integer, List<String>>>> 
			resultIterator = result.entrySet().iterator();
		String report = "";
		StringBuffer buf = new StringBuffer();
		while (resultIterator.hasNext()) {
			Entry<String, Map<Integer, List<String>>> result = resultIterator.next();
			// <staus, [id, <paths>]>
			buf.append(result.getKey()+":\t{GranuleId=[Paths]}\n\t"
					+result.getValue().toString()+"\n");
			/*report +=result.getKey()+":\t{GranuleId=[Paths]}\n\t"
					+result.getValue().toString()+"\n";*/			
		}
		report = buf.toString();
		return report;
	}
}
