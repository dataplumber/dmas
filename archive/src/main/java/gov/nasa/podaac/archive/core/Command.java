//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.archive.core;

import gov.nasa.podaac.archive.exceptions.ArchiveException;
import gov.nasa.podaac.archive.exceptions.ArchiveException.ErrorType;
import gov.nasa.podaac.archive.external.ArchiveMetadata;
import gov.nasa.podaac.archive.external.FileUtil;
import gov.nasa.podaac.archive.external.InventoryFactory;
import gov.nasa.podaac.archive.external.ServiceProfile;
import gov.nasa.podaac.inventory.api.Constant.GranuleStatus;
import gov.nasa.podaac.inventory.api.Constant.LocationPolicyType;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.Granule;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class provides processes of requests coming into Archive Program Set.
 * 
 * @author clwong
 * @version $Id: Command.java 13614 2014-10-21 21:18:41Z gangl $
 *
 */
public class Command {
	private static Log log = LogFactory.getLog(Command.class);	
	private static final String ONLINE = GranuleStatus.ONLINE.toString();
	private static final String OFFLINE = GranuleStatus.OFFLINE.toString();
	private static String arguments = "";
	private static Calendar start= Calendar.getInstance();
	private static Calendar stop = Calendar.getInstance();
	private static boolean startIsDefined = false;
	private static boolean stopIsDefined = false;
	private static boolean verifyChecksum = false;
	
	public static void delete(Integer granuleId, boolean dataOnly, String baseLocation) {
		String msg = "\n\n===== "+new Date()+"  Granule Deleted Files [ID="+granuleId+"] =====";
		ArchiveMetadata metadata = InventoryFactory.getInstance().getQuery().getGranuleMetadata(granuleId);
		if (metadata==null) {
			log.info(msg+"\nNo metadata found!\n=====");
			return;
		}
		List<String> archivePathList = metadata.getArchivePathList();
		if (!archivePathList.isEmpty()) {
			String path = archivePathList.get(0);
			if (baseLocation==null) baseLocation = ArchiveData.getBaseLocation(path);
			else if (!path.startsWith(baseLocation)) {
				baseLocation = ArchiveData.getBaseLocation(path);		
			}
			log.debug("Archive Base Location: " + baseLocation);
			
			List<String> deleted = null;
			try {
				deleted = ArchiveData.delete(archivePathList, baseLocation);
				if (deleted==null || deleted.isEmpty()) {
					log.debug("No files delete, bypassing granule status change output.");
				}else{
					if(dataOnly)
						GranuleStatusChangeFactory.getInstance().notifyChange(GranuleStatusChange.DELETE, metadata, null);
					else
						GranuleStatusChangeFactory.getInstance().notifyChange(GranuleStatusChange.FULL_DELETE, metadata, null);
				}
				for(String s : archivePathList){
					log.info("path: " + s);
				}
				
				String defData = System.getProperty("default.data.path");
				if(defData != null){
					for(int i=1; i<=10; i++){
						String mDir = System.getProperty("mirror.directory" + i);
						if(mDir == null)
							break;
						log.info("Check mirror directory: " + mDir);
						List<String> mirroredPathList = new ArrayList<String>();
						for(String s : archivePathList){
							mirroredPathList.add(s.replace(defData, mDir));
						}
						try{
						deleted.addAll(ArchiveData.delete(mirroredPathList));
						}catch(ArchiveException ae){
							continue;
						}
					}
				}

			} catch (ArchiveException e) {
				msg+= "\n"+e.getMessage();
			}
			if (deleted==null || deleted.isEmpty()) {
				msg += "\nNo files deleted!";
			} else {
				for (String file : deleted) msg += "\n"+file;
			}
		} else {
			msg += "\nNo files deleted!";
		}
		InventoryFactory.getInstance().getAccess().deleteGranule(metadata.getGranule(), dataOnly);
		if (dataOnly) {
			// refresh metadata
			metadata = InventoryFactory.getInstance().getQuery().getGranuleMetadata(granuleId);
			msg += "\n\nGranule Status: "+metadata.getGranule().getStatus();
			msg += metadata.printGranuleArchives();
			msg += metadata.printGranuleReferences();
		} else {
			metadata = InventoryFactory.getInstance().getQuery().getGranuleMetadata(granuleId);
			if (metadata.getGranule()!=null) msg += "\nGranule metadata not deleted.";
			msg += "\nGranule metadata deleted.";
		}
		log.info(msg+"\n\n===== "+new Date());
	}

	public static List<String> move(Integer granuleId, String fromBaseLocation, String toBaseLocation, boolean force) {
		String msg = "\n\n===== "+new Date()+"  Granule relocated Files [ID="+granuleId+"] =====";
		ArchiveMetadata metadata = InventoryFactory.getInstance().getQuery().getGranuleMetadata(granuleId);
		if (metadata==null) {
			log.info(msg+"\nNo metadata found!\n=====");
			return null;
		}
		List<String> archivePathList = metadata.getArchivePathList();
		List<String> movedList = new ArrayList<String>();
		if (!archivePathList.isEmpty()) {
			
			if (fromBaseLocation==null) {
				log.info("fromBaseLocation is null");
				if (archivePathList.isEmpty()) return null;
				fromBaseLocation = ArchiveData.getBaseLocation(archivePathList.get(0));
			}
			try {
				String toPathBase = null;
				if(metadata.getGranule().getRelPath() != null){
					toPathBase = toBaseLocation + File.separator + metadata.getGranule().getRelPath();
				}
				else{
					toPathBase = toBaseLocation;
				}
				movedList = ArchiveData.move(archivePathList, fromBaseLocation, toPathBase, force);
			} catch (ArchiveException e) {
				log.info("exception thrown");
				msg += e.getMessage();
			}
			
			if (movedList==null || movedList.isEmpty()) {
				msg += "\nNo files relocated!";
			} else {
				InventoryFactory.getInstance().getAccess().updateGranuleLocation(metadata.getGranule(), fromBaseLocation, toBaseLocation);
				for (String file : movedList) msg += "\n"+file;
				// refresh metadata
				metadata = InventoryFactory.getInstance().getQuery().getGranuleMetadata(granuleId);
				msg += "\n\nGranule Status: "+metadata.getGranule().getStatus();
				msg += metadata.printGranuleArchives();
				msg += metadata.printGranuleReferences();
				/*
				 * insert REASSOCIATE granuleUpdateStatus...
				 */
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("oldLocation", fromBaseLocation + File.pathSeparator + metadata.getGranule().getRelPath());
				GranuleStatusChangeFactory.getInstance().notifyChange(GranuleStatusChange.RELOCATE, metadata, map);
			}		
		} else {
			msg += "\nNo files relocated!";
		}
		log.info(msg+"\n\n===== "+new Date());
		return movedList;
	}

	public static String processAIP (String msg) throws ArchiveException  {
		log.info("processAIP...");
		long time = System.currentTimeMillis();
		Date startTime = new Date();

		String statusMsg = "";
		Map<Integer, List<AIP>> aipById = new TreeMap<Integer, List<AIP>>();
		int archiveCount = 0;  // number of archive files attempted
		int passCount = 0; // number of archive files succeeded
		long totalFileSize = 0l;

		// Buffers to track success or failure archives
		// Integer for GranuleId
		// String for "Destination<Status>"
		Map<Integer,List<String>> online = new TreeMap<Integer,List<String>>();
		Map<Integer,List<String>> offline = new TreeMap<Integer,List<String>>();   		
		try {
			ServiceProfile.createServiceProfile(msg);
		} catch (ArchiveException e) {
			throw new ArchiveException(
					ErrorType.SERVICE_PROFILE, e.getMessage());
		}                                  
		// process deletion
		List<String> savedList = ServiceProfile.deletes();

		try {
			aipById = ServiceProfile.createAIPById();
		} catch (ArchiveException e) {
			throw new ArchiveException (
					ErrorType.SERVICE_PROFILE, e.getMessage());
		}
		log.debug("Number of Granules="+aipById.size());

		Set<Integer> ids = aipById.keySet();
		for (Integer id : ids) {
			Date archiveTime = new Date();
			List<AIP> aipList = aipById.get(id);
			// process files per granule
			for (AIP aip : aipList) {
				aip.setArchiveGranuleStartDate(archiveTime);
				aip.setArchiveFileStartDate(new Date());

				long fz;
				try {
					fz = ArchiveData.add(aip);
				} catch (ArchiveException e) {
					throw new ArchiveException (
							ErrorType.COPY_FILE, e.getMessage());
				} 
				if (fz >= 0) {
					totalFileSize +=  fz;
					archiveCount++; // increment number of attempts to archive
					// Verfify after archive completes
					// Sort out success/failure attempts into online/offline buffers
					if (ArchiveData.verify(aip)) {  // verified archived file
						if (!offline.containsKey(id)) {
							// this granule is not black-listed yet
							if (!online.containsKey(id))
								// first entry for this granule; a good one
								online.put(id, new ArrayList<String>());
							online.get(id).add(aip.getDestination().toString()+"<"+aip.getStatus()+">");
						} else { 
							// other archived file not verified even that this file passes
							offline.get(id).add(aip.getDestination().toString()+"<"+aip.getStatus()+">");
						}
						log.debug("granuleId="+id+":"+aip.getDestination().toString()
								+"<"+aip.getStatus()+">");
						passCount++;
					} else {
						// this archived file failed verification
						if (!offline.containsKey(id)) {
							if (!online.containsKey(id)) {
								// first entry for this granule; a bad one
								offline.put(id, new ArrayList<String>());
							} else {
								// first failed archived file so move all other verified to offline
								offline.put(id, online.get(id));
								online.remove(id);
							}            			
						}
						offline.get(id).add(aip.getDestination().toString()
								+"<"+aip.getStatus()+">");        		
						try {
							log.error(
									"granuleId="+id+":"+aip.getDestination().toString()
									+"<"+aip.getStatus()+">"
									+new String(FileUtil.removeFile(aip.getDestination()) ? " REMOVED! " : "")
							);
						} catch (ArchiveException e) {
							throw new ArchiveException(ErrorType.ARCHIVE, e.getMessage());
						}
					}
					aip.setArchiveFileEndDate(new Date());
				} // test for fz
			} // for aip
			InventoryFactory.getInstance().getAccess().updateAIPArchiveStatus(aipList);
			InventoryFactory.getInstance().getAccess().updateVerifyGranuleStatus(new ArrayList<Integer>(online.keySet()), ONLINE);
			archiveTime = new Date();
			for (AIP aip : aipList) aip.setArchiveGranuleEndDate(archiveTime);
		} // for granule id
		ServiceProfile.updateArchiveGranule(aipById);
		time = System.currentTimeMillis() - time;
		log.info("Process Time:"+time/1000.+" seconds"+"\tBytes: "+totalFileSize);
		if (archiveCount>0) {
			statusMsg ="\n\n===== "+startTime+"   Archive Request Summary =====\n"	
			+"\nNo. Requested Files: "+aipById.size()+"\tArchived: "+archiveCount+"\tVerified: "+passCount
			+"\nNo. Granules: "+(online.size()+offline.size())
			+"\t\t"+ONLINE+": "+online.size()+"\t"+OFFLINE+": "+offline.size()
			+ new String((online.size()>0) ? "\n\n"+ONLINE+":\t{GranuleId=[Path<Status>]}\n\t"+online.toString() : "")
			+ new String((offline.size()>0) ? "\n\n"+OFFLINE+":\t{GranuleId=[Path<Status>]}\n\t"+offline.toString() : "")
			+"\n\nProcess Time: "+time/1000.+" seconds"+"\tBytes: "+totalFileSize
			+"\n\n===== "+new Date()+"   End of Summary ==============\n"
			;
		}
		log.info("Cleaned up saved files.");
		ArchiveData.delete(savedList, System.getProperty("archive.trash"));
		return statusMsg ;
	}

	public static void processDelete(CommandLine cmdLine) {
		boolean deleteMetadata = false;
		log.info("processDelete: Arguments: "+arguments);
		Map<Integer, List<Integer>> processIdList = processIdList(cmdLine);
		if (processIdList==null || processIdList.isEmpty()) return;
		deleteMetadata = cmdLine.hasOption("delete-metadata");
		
		if(deleteMetadata)
			System.out.println("You have chosen to delete data and DMAS Inventory metadata.");
		
		List<Integer> granuleIdList = processIdList.get(-1);
		if (granuleIdList==null) {
			Set<Integer> datasetIdList = processIdList.keySet();
			for (Integer datasetId : datasetIdList) {
				granuleIdList = processIdList.get(datasetId);
				String baseLocation = InventoryFactory.getInstance().getQuery().getArchiveBaseLocation(datasetId);
				log.debug("Baselocation: "+baseLocation);
				for (Integer granuleId : granuleIdList) {
					delete(granuleId, !deleteMetadata, baseLocation);
				}
			}
		} else {
			Map<Integer, String> dsBaseLocation = InventoryFactory.getInstance().getQuery().getArchiveBaseLocation(granuleIdList);	
			for (Integer granuleId : granuleIdList) {
				log.debug("BASELOC"+dsBaseLocation.get(granuleId));
				delete(granuleId, !deleteMetadata, dsBaseLocation.get(granuleId));
			}		
		}
	}
	private static int checkParam(String param,String option)
	{
		try{
			int temp = Integer.parseInt(param);
			
			if(temp < 1)
			{
				System.out.println("Error! "+ option + " option value: " + param + " cannot be a zero or negative value. Exiting.");
				System.exit(1);
			}
			
			return temp;
			
		}catch(NumberFormatException nfe)
			{
				System.out.println("Error! "+ option + " option value: " + param + " cannot be converted to an integer. Exiting.");
				System.exit(1);
			}
		catch(Exception e)
		{
			System.out.println("Unknown error converting command line parameter to integer. Exiting.");
			System.exit(-2);
		}
		return -1;
	}
	
	private static Map<Integer, List<Integer>> processIdList(CommandLine cmdLine) {
		log.info("processIdList: ");
		List<Integer> idList = new ArrayList<Integer>();	
			if (cmdLine.hasOption("d")) {
				idList.add(checkParam(cmdLine.getOptionValue("d"),"-d"));
			} else if (cmdLine.hasOption("g")) {
					idList.add(checkParam(cmdLine.getOptionValue("g"),"-g"));
						
			} else if (cmdLine.hasOption("dl")) {
				String[] values = cmdLine.getOptionValues("dl");
				for (String val : values) idList.add(checkParam(val,"-dl"));
			} else if (cmdLine.hasOption("gl")) {
				String[] values = cmdLine.getOptionValues("gl");
				for (String val : values) idList.add(checkParam(val,"-gl"));
			} else if (cmdLine.hasOption("dr") || cmdLine.hasOption("gr")) {
				String[] values = null;
				if (cmdLine.hasOption("dr")) values = cmdLine.getOptionValues("dr");
				else if (cmdLine.hasOption("gr")) values = cmdLine.getOptionValues("gr");
				int beginVal = checkParam(values[0],"-gr or -dr begin value");
				int endVal = checkParam(values[1],"-gr or -er end value");
				int startId = Math.min(beginVal, endVal);
				int endId = Math.max(beginVal, endVal);
				for (int i=startId; i<=endId; i++) idList.add(i);
			}
		
		Map<Integer, List<Integer>> processIdList = new TreeMap<Integer, List<Integer>>();
		// datasetId, granuleIds
		if (!idList.isEmpty()) {
			if (cmdLine.hasOption("d") || cmdLine.hasOption("dl") || cmdLine.hasOption("dr")) {
				String[] indexes = null;
				if (cmdLine.hasOption("i")) indexes = cmdLine.getOptionValues("i");
				for (Integer datasetId : idList) {
					log.debug("Fetching info for datasetId: " + datasetId);
					List<Integer> granuleIdList;
					if(startIsDefined){
						 granuleIdList = InventoryFactory.getInstance().getQuery().getGranuleIdList(datasetId, start, stop);
					}
					else{
						 granuleIdList = InventoryFactory.getInstance().getQuery().getGranuleIdList(datasetId);
					}
					log.debug("Number of granules for " + datasetId + ": " + granuleIdList.size());
					if (indexes!=null) {
						int beginVal = Integer.parseInt(indexes[0])-1;
						int endVal = Integer.parseInt(indexes[1]);
						granuleIdList = granuleIdList.subList(Math.min(beginVal, endVal), Math.max(beginVal, endVal));
					}
					if (granuleIdList.isEmpty()) {
						log.info("Granules not found!");
					} else {
						processIdList.put(datasetId, granuleIdList);
					}
				}					
			} else if (cmdLine.hasOption("g") || cmdLine.hasOption("gl") || cmdLine.hasOption("gr")) {
				List<Integer> granuleIdList = InventoryFactory.getInstance().getQuery().findGranuleList(idList);
				if (granuleIdList.isEmpty()) {
					log.info("Granules not found!");
				} else {
					if(idList.removeAll(granuleIdList))
					{
						for(Integer ix : idList){
							log.error("Granule " +ix+ " not found in the database. Skipping verify for this granule.");
						}
						
					}
					
					processIdList.put(-1, granuleIdList);
				}
			}
			if (processIdList.isEmpty()) {
				log.info("\n\n===== Cannot find any granule/dataset per request! =====");
				return null;
			}
		}
		return processIdList;
	}

	public static void processRelocate(CommandLine cmdLine) {
		log.info("processRelocate: Arguments: "+arguments);
		Map<Integer, List<Integer>> processIdList = processIdList(cmdLine);
		if (processIdList==null || processIdList.isEmpty()) return;
		String basepath = cmdLine.getOptionValue("bp");
		Boolean force = cmdLine.hasOption("f");
		
		System.out.println("force option set to: " + force.toString());
		
		List<Integer> granuleIdList = processIdList.get(-1);
		if (granuleIdList==null) {
			Set<Integer> datasetIdList = processIdList.keySet();
			for (Integer datasetId : datasetIdList) {
				granuleIdList = processIdList.get(datasetId);
				String baseLocation = InventoryFactory.getInstance().getQuery().getArchiveBaseLocation(datasetId);
				
				log.info("Dataset BaseLocation: " + baseLocation);
				
				for (Integer granuleId : granuleIdList) {
					move(granuleId, baseLocation, basepath, force);
				}
			}
		} else {
			Map<Integer, String>dsBaseLocation = InventoryFactory.getInstance().getQuery().getArchiveBaseLocation(granuleIdList);	
			for (Integer granuleId : granuleIdList) {
				
				log.info("Dataset BaseLocation: " + dsBaseLocation.get(granuleId));
				move(granuleId, dsBaseLocation.get(granuleId), basepath, force);
			}	
		}
	}

	public static Calendar parseDate(String in){
		Calendar cal = Calendar.getInstance();
		String pattern = "MM/dd/yyyy";
	    SimpleDateFormat format = new SimpleDateFormat(pattern);
	    Date d = null;
	    try {
			d = format.parse(in);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			System.out.println("Error parsing date \""+in+"\". Dates must be in format MM/dd/yyyy, such as 01/01/2010");
			System.exit(-1);
		}
		Calendar tempCal = Calendar.getInstance();
		tempCal.setTime(d);
		TimeZone gmt = TimeZone.getTimeZone("GMT");
		cal.setTimeZone(gmt);
		cal.set(tempCal.get(Calendar.YEAR), tempCal.get(Calendar.MONTH), tempCal.get(Calendar.DATE) , 0,0,0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}
	
	public static void processRollingStore(CommandLine cmdLine) {
		log.info("processRollingStore: Arguments: "+arguments);
		
		if (cmdLine.hasOption("start")) {
			//use the date supplied by the caller
			String date = cmdLine.getOptionValue("start");
			start = parseDate(date);
			startIsDefined=true;
			}
		if (cmdLine.hasOption("stop")) {
			//use the date supplied by the caller
			String date = cmdLine.getOptionValue("stop");
			stop= parseDate(date);
			stopIsDefined=true;
		}
		
		if((startIsDefined && !stopIsDefined) || (stopIsDefined && !startIsDefined)){
			System.out.println("You cannot specifiy a start time without also specifying a stop time (or vice versa).");
			System.exit(-1);
		}
		
		Map<Integer, List<Integer>> processIdList = processIdList(cmdLine);
		if (processIdList==null) return;
		if (processIdList.isEmpty()) { // Scan all rolling-store datasets
			Map<Integer, ArchiveMetadata> datasetMetadata = InventoryFactory.getInstance().getQuery().getRollingStore();
			log.debug("processRollingStore: size="+datasetMetadata.size());
			Set<Integer> datasetIds = datasetMetadata.keySet();
			for (Integer datasetId : datasetIds) {
				List<Integer> granuleIdList = InventoryFactory.getInstance().getQuery().getGranuleIdList(datasetId);
				log.debug("datasetId="+datasetId+":granuleIdList size="+granuleIdList.size());
				log.debug(granuleIdList.get(0)+"--"+granuleIdList.get(granuleIdList.size()-1));
				ArchiveMetadata metadata = datasetMetadata.get(datasetId);
				for (Integer granuleId : granuleIdList) {
					rollingStore(granuleId, metadata);
				}			
			}
		} else {
			List<Integer> granuleIdList = processIdList.get(-1);
			if (granuleIdList==null) {
				Set<Integer> datasetIdList = processIdList.keySet();
				for (Integer datasetId : datasetIdList) {
					granuleIdList = processIdList.get(datasetId);
					ArchiveMetadata metadata = InventoryFactory.getInstance().getQuery().getRollingStore(datasetId);
					
					for (Integer granuleId : granuleIdList) {
						rollingStore(granuleId, metadata);
					}
				}
			} else {
				Map<Integer, ArchiveMetadata> granuleMetadata = InventoryFactory.getInstance().getQuery().getRollingStore(granuleIdList);
				for (Integer granuleId : granuleIdList) {
					rollingStore(granuleId, granuleMetadata.get(granuleId));
				}
			}
		}
	}

	public static void processVerify(CommandLine cmdLine) {
		log.info("processVerify: Arguments: "+arguments);		
		Map<Integer, List<Integer>> processIdList = processIdList(cmdLine);
		if (processIdList==null || processIdList.isEmpty()) return;
		List<Integer> granuleIdList = processIdList.get(-1);
		
		
		if (cmdLine.hasOption("cs")) {
			verifyChecksum = true;
		}
		
		// validate references against location policy
		if (cmdLine.hasOption("lp")) {
			if (granuleIdList==null) {
				Set<Integer> datasetIdList = processIdList.keySet();
				for (Integer datasetId : datasetIdList) {
					granuleIdList = processIdList.get(datasetId);
					ArchiveMetadata metadata = InventoryFactory.getInstance().getQuery().getDatasetMetadata(datasetId);
					for (Integer granuleId : granuleIdList) {
						Map<String,String> allRefs = metadata.getLocalBaseLocation();
						allRefs.putAll(metadata.getRemoteBaseLocation());
						validateReference( granuleId, allRefs);
					}
				}
			} else {
				Map<Integer, ArchiveMetadata> metadata = InventoryFactory.getInstance().getQuery().getDatasetMetadata(granuleIdList);
				for (Integer granuleId : granuleIdList) {
					Map<String,String> allRefs = metadata.get(granuleId).getLocalBaseLocation();	
                                        allRefs.putAll(metadata.get(granuleId).getRemoteBaseLocation());
					
                                        validateReference( granuleId, allRefs);
				}
			}
		} else {
			if (granuleIdList==null) {
				Set<Integer> datasetIdList = processIdList.keySet();
				for (Integer datasetId : datasetIdList) {
					granuleIdList = processIdList.get(datasetId);
					Verify.verifyByDataset(datasetId, granuleIdList);
				}
			} else {
				log.debug("setting checksum to " + verifyChecksum);
				Verify.verifyByGranule(granuleIdList, verifyChecksum);
			}
		}
	}
	
	public static void validateReference (Integer granuleId, Map<String,String>localBaseLocationPolicy) {
		log.debug("validateReference: granuleId="+granuleId);
		log.debug("Size of location policies: "+localBaseLocationPolicy.size());
		ArchiveMetadata metadata = InventoryFactory.getInstance().getQuery().getGranuleMetadata(granuleId);
		boolean updated = 
			InventoryFactory.getInstance().getAccess().updateLocalReference(metadata.getGranule(), localBaseLocationPolicy);			
		if (updated) {
			metadata = InventoryFactory.getInstance().getQuery().getGranuleMetadata(granuleId);
			log.info(
					"\n\nGranule Status: "+metadata.getGranule().getStatus()
					+metadata.printGranuleArchives()
					+metadata.printGranuleReferences()
					+"\n\n===== ");
		} else {
			log.info("validateReference: granuleId="+granuleId+" not updated!");
		}
	}
	
	public static void rollingStore(Integer granuleId, ArchiveMetadata datasetMetadata) {
		
		boolean remoteData = true;
		
		String msg = "\n\n===== "+new Date()+"  Rolling-Store Granule [ID="+granuleId+"] =====";
		if (datasetMetadata==null) 
			{
			log.info("Granule dataset metadata not retreived. Granule [ID="+granuleId+"] likely not a rolling-store granule.");
				return;
			}
		Map<String, String> localBaseLocationPolicy = datasetMetadata.getLocalBaseLocation();
		Map<String, String> remoteBaseLocationPolicy = datasetMetadata.getRemoteBaseLocation();
		if (localBaseLocationPolicy == null || remoteBaseLocationPolicy == null) 
			{
				log.info("Base Location or Rolling Store location not set, skipping file.");
				return;		
			}
		String archiveBaseLocation = localBaseLocationPolicy.get(LocationPolicyType.ARCHIVE_OPEN.toString());
		//if it's null, try preview
		if(archiveBaseLocation == null)
			 archiveBaseLocation = localBaseLocationPolicy.get(LocationPolicyType.ARCHIVE_PREVIEW.toString());
		//still null? Try simulated
		//if(archiveBaseLocation == null)
			// archiveBaseLocation = localBaseLocationPolicy.get(LocationPolicyType.ARCHIVE_SIMULATED.toString());
		// cache granule
		ArchiveMetadata granuleMetadata = InventoryFactory.getInstance().getQuery().getGranuleMetadata(granuleId);
		if (granuleMetadata==null) return;
		if (granuleMetadata.getGranule()==null) return;
		
		// check expiration day
		Date ingestTime = granuleMetadata.getGranule().getIngestTime();
		Integer dataDuration = datasetMetadata.getDataDuration();
		if (ingestTime==null || dataDuration==null) 
			{
				log.info("IngestTime or dataDuration not set, skipping file.");
				return;
			}
		long days = (new Date().getTime() - ingestTime.getTime())/(24L*60L*60L*1000L) - dataDuration;
		if (days<=0) {
			log.info(msg+"\nData not expired yet ["+days+" days].");
			return;
		}
		log.debug("granuleId="+granuleId+" expired ["+days+" days].");
				
		// get the archived path for DATA file
		List<String> archivePathList = granuleMetadata.getArchivePathList();
		if ((archivePathList==null) || archivePathList.isEmpty()) {
			log.info(msg+"\nUnable to determine archived data path!");
			return;
		}
		String dataFilePath = InventoryFactory.getInstance().getQuery().getOnlineDataFilePath(granuleMetadata.getGranule());
		if (dataFilePath==null) {
			log.info(msg+"\nUnable to find local archived online data!");
			return;
		}
		if (archiveBaseLocation==null) {
			archiveBaseLocation = ArchiveData.getBaseLocation(dataFilePath);
			localBaseLocationPolicy.put(LocationPolicyType.ARCHIVE.toString(), archiveBaseLocation);
		}
		String subpath = dataFilePath.replaceFirst(archiveBaseLocation, "");
		log.debug("subpath: " + subpath);
		log.debug("archiveBaseLocation: " + archiveBaseLocation);
		
		// Determine & copy archived files to ftp/sftp remote path
		/*String ftpRemotePath = 
							remoteBaseLocationPolicy.get(LocationPolicyType.REMOTE_FTP.toString())
							+ (subpath.startsWith("/") ? subpath : "/"+subpath);
		boolean existed = false;
		log.debug("ftpRemotePath: " + ftpRemotePath);
		
		
		if (ftpRemotePath != null) {
			try {
			
				if (ArchiveData.verify(ftpRemotePath)) existed = true;
			} catch (ArchiveException e) {};
		}
		if (!existed) {
			String ftpDir = ftpRemotePath.replaceFirst(ArchiveData.getFilename(dataFilePath), "");
			for (String archivePath : archivePathList) {
				// copy dataFilePath to ftpRemoteLocation	
				try {
					FileUtil.getInstance();
					FileUtil.copyFile(archivePath, ftpDir+ArchiveData.getFilename(archivePath));
					existed = true;
				} catch (ArchiveException e) {
					log.info(e.getMessage());
					
				}
				FileUtil.release();
			}
		}
		if (!existed) {
			log.info(msg+"\nRemote files not found!");
			return;
		}
		 */
		// verify the remote references for their existency
		Set<String> remoteLocationTypes = remoteBaseLocationPolicy.keySet();
		if (remoteLocationTypes==null || remoteLocationTypes.isEmpty()) {
			remoteData = false;
			log.info(msg+"\nNo remote base location policy found!");
			//return
		}
		Map<String, String>remoteReferences = new TreeMap<String, String>();
		
		if(remoteData){
			//log.
			for (String remoteLocationType : remoteLocationTypes) {
				String remotePath = remoteBaseLocationPolicy.get(remoteLocationType) 
									+ (subpath.startsWith("/") ? subpath : "/"+subpath);
				log.debug("RemotePath reference: " + remotePath);
				
				try {
					if (ArchiveData.verify(remotePath)) {
						remoteReferences.put(remoteLocationType,remoteBaseLocationPolicy.get(remoteLocationType));
						log.info("Verified: " + remotePath);
					}
				} catch (ArchiveException e) {
					log.info("Exception occurred verifying remote path: " + remotePath);
					
				};
			}
			if (remoteReferences.isEmpty()) {
				log.info(msg+"\n No remote references found.");
				return;
			}
		}
		List<String> dels = null;
		try {
			//DELETE DATA
			dels =ArchiveData.delete(granuleMetadata.getArchivePathList(), archiveBaseLocation);
			/*
			 * Add granule status update here
			 */
			GranuleStatusChangeFactory.getInstance().notifyChange(GranuleStatusChange.ROLLING_STORE, granuleMetadata, null);
			/*
			 * ADD MIRROR DELETE CODE HERE
			 * 
			 */
		
			String defData = System.getProperty("default.data.path");
			if(defData != null){
				for(int i=1; i<=10; i++){
					String mDir = System.getProperty("mirror.directory" + i);
					if(mDir == null)
						break;
					log.info("Check mirror directory: " + mDir);
					List<String> mirroredPathList = new ArrayList<String>();
					for(String s : archivePathList){
						mirroredPathList.add(s.replace(defData, mDir));
					}
					try{
						dels.addAll(ArchiveData.delete(mirroredPathList));
					}catch(ArchiveException ae){
						continue;
					}
				}
			}
			else
				log.info("No Default Data Path listed...");
			
			
				log.info("archiveBaseLocation: " + archiveBaseLocation);
				for(String s : granuleMetadata.getArchivePathList()){
					log.info("path: " + s);
					
				}
			
			
			
		} catch (ArchiveException e) {
			msg += "\n"+e.getMessage();
		}
		if(remoteData){
			log.debug("list size: "+ remoteReferences.size());
			InventoryFactory.getInstance().getAccess().setGranuleReference(granuleMetadata.getGranule(), remoteReferences, subpath);
		}
		else{
//			//If no remoteData, we should set the granule as OFFLINE.
//			List<Integer> gids = new ArrayList<Integer>();
//			gids.add(granuleMetadata.getGranule().getGranuleId());
//			InventoryFactory.getInstance().getAccess().updateVerifyGranuleStatus(gids, "OFFLINE");

			/*
			 * Set the granule Status to offline if there is no remoteData.
			 */
			//When we 'remote' the granule, it should update the status.
			granuleMetadata.getGranule().setStatus(GranuleStatus.OFFLINE);
			
		}
		//DELETE local references, set to deleted
		InventoryFactory.getInstance().getAccess().remoteGranule(granuleMetadata.getGranule());

		// refresh query
		granuleMetadata = InventoryFactory.getInstance().getQuery().getGranuleMetadata(granuleId);
		msg += "\n\nGranule Status: "+granuleMetadata.getGranule().getStatus()
			+granuleMetadata.printGranuleArchives()
			+granuleMetadata.printGranuleReferences()
			+"\n\n===== ";
		log.info(msg);
		
		
		
	}

	protected Command() {}

	public static String getArguments() {
		return arguments;
	}

	public static void setArguments(String[] args) {
		arguments = "";
		for (int i=0; i<args.length; i++) arguments+=" "+args[i];
	}

	public static void processReassociate(CommandLine cmdLine) {
		
		//fd,td, pattern
		String fromDataset = null;
		String toDataset = null;
		String gnp = null; // granuleNamePattern
		boolean testOnly = false;
		
		fromDataset= cmdLine.getOptionValue("fd");
		toDataset = cmdLine.getOptionValue("td");
		gnp = cmdLine.getOptionValue("pattern");
		testOnly = cmdLine.hasOption("test");
		
		log.debug("From Dataset: " + fromDataset);
		log.debug("To Dataset  : " + toDataset);
		log.debug("Name Pattern: " + gnp);
		log.debug("Test mode is set to: " + testOnly);
		
		if(fromDataset==null || toDataset == null){
			System.out.println("From and To dataset parameters must be valid.");
			System.exit(10);
		}
		
		//get From Dataset, (basepaths)
		Dataset fromD = InventoryFactory.getInstance().getQuery().fetchDatasetByPersistentId(fromDataset);
		//get To Dataset (basepaths)
		Dataset toD = InventoryFactory.getInstance().getQuery().fetchDatasetByPersistentId(toDataset);
		if(fromD == null){
			System.out.println("From Dataset does not exist. Check the persistent ID ["+fromDataset+"] supplied and try again.");
			log.info("From Dataset does not exist. Check the persistent ID ["+fromDataset+"] supplied and try again.");
			System.exit(10);
		}
		if(toD == null){
			System.out.println("To Dataset does not exist. Check the persistent ID ["+toDataset+"] supplied and try again.");
			log.info("To Dataset does not exist. Check the persistent ID ["+toDataset+"] supplied and try again.");
			System.exit(10);
		}
		if(toD.getDatasetId().equals(fromD.getDatasetId())){
			System.out.println("To and From datasets are the same ["+toDataset+"]");
			log.info("To and From datasets are the same ["+toDataset+"]");
			System.exit(11);
		}
		log.info("Processing from dataset ["+fromD.getPersistentId()+":"+fromD.getDatasetId()+"] to dataset ["+toD.getPersistentId()+":"+toD.getDatasetId()+"]");
		
		boolean moveAll = false;
		//if no pattern exists, we'll move all granules. confirm this action.
		if(gnp == null){
			moveAll = true;
		}
		else if (gnp.equals("")){
			moveAll = true;
		}
		
		String answer = "";
		InputStreamReader istream = new InputStreamReader(System.in) ;
        BufferedReader bufRead = new BufferedReader(istream) ;
        boolean proper = false;
		if(moveAll && !testOnly){
			do{
				System.out.println("\nNo Granule Pattern Name was specified. This will move all granules in dataset["+fromD.getPersistentId()+"] to dataset["+toD.getPersistentId()+"]. Do you want to continue? [y/n]");
				log.info("Confirming move all granules:");
				try {
		            	answer = bufRead.readLine();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(answer.equalsIgnoreCase("y"))
					{
						System.out.println("Received \"yes\" signal. Proceeding with reassociations of all granules");
						log.info("Received \"yes\" signal. Proceeding with reassociations of all granules");
						proper=true;
					}
					else if(answer.equalsIgnoreCase("n"))
					{
						System.out.println("Received \"no\" signal. Skipping reassociations");
						log.info("Received \"no\" signal. Skipping relocation of granules");
						System.exit(500);
					}
					if(!proper)
						System.out.println("Please enter a 'y'es or 'n'o response. You entered: \""+answer+"\"");
			}while(!proper);
			log.debug("Continuing with reassociate after moveAll check.");
		}
		Reassociate r = new Reassociate();
		r.setTestMode(testOnly);
		r.reassociateGranules(fromD, toD, gnp, moveAll);
		
		//get granules by name/pattern
		
		//create old,new file locations
		
		//confirm the move
		
		//move file from A to B
		
		//Update the following tables:
		//GranuleArchive
		//GranuleReference
		//Granule (dataset, root, rel?)
		//GranuleMetaHistory
		//Granuleelements will need to be mapped to the new DEIDS and updated in the granule_* tables
	}
	
	public static void processLocate(CommandLine cmdLine) {

		Integer dId = null;
		Long startTime=null, stopTime = null;
		try{
		 dId = Integer.valueOf(cmdLine.getOptionValue("dataset"));
		}catch(Exception E){
			System.out.println("Error parsing Dataset ID. A numeric Dataset Id must be entered to run the Locate Tool.");
			log.info("Error parsing Dataset ID. A numeric Dataset Id must be entered to run the Locate Tool.");
		}
		String pattern = null; 
		if(cmdLine.hasOption("pattern")){
			pattern = cmdLine.getOptionValue("pattern");
		}
		else
			pattern=null;
		String outFile = null;
		if(cmdLine.hasOption("output")){
			outFile = cmdLine.getOptionValue("output");
		}
		
		if (cmdLine.hasOption("start")) {
			//use the date supplied by the caller
			String date = cmdLine.getOptionValue("start");
			start = parseDate(date);
			startTime = start.getTimeInMillis();
			startIsDefined=true;
			}
		else 
			start = null;
		
		if (cmdLine.hasOption("stop")) {
			//use the date supplied by the caller
			String date = cmdLine.getOptionValue("stop");
			stop= parseDate(date);
			stopTime = stop.getTimeInMillis();
			stopIsDefined=true;
		}
		else
			stop = null;
		
		System.out.println("Dataset: " + dId);
		System.out.println("Pattern: " + pattern);
		
		log.info("Dataset: " + dId);
		log.info("Pattern: " + pattern);
		
		if(startIsDefined){
			System.out.println("start: " + start.getTime());
			log.info("start: " + start.getTime());
		}
		if(stopIsDefined){
			System.out.println("stop: " + stop.getTime());
			log.info("stop: " + stop.getTime());
		}
		
		//do the search now
		List<Granule> gl = InventoryFactory.getInstance().getQuery().locateGranules(dId, pattern, startTime, stopTime);
		if(gl == null){
			log.error("Error fetching granules.");
			return;
		}
		System.out.println("returned size: " + gl.size());
		System.out.println("[granule_id] granule_name");
		System.out.println("--------------\n");
		log.info("returned size: " + gl.size());
		log.info("[granule_id] granule_name");
		log.info("--------------\n");
		
		for(Granule g: gl){
			System.out.println("["+g.getGranuleId()+"] "+g.getName());
			log.info("["+g.getGranuleId()+"] "+g.getName());
			if(outFile != null){
			//output to file here	
			}
		}
	}	
}
