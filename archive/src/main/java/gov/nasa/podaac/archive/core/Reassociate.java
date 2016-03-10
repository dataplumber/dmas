package gov.nasa.podaac.archive.core;

import gov.nasa.podaac.archive.external.ArchiveMetadata;
import gov.nasa.podaac.archive.external.InventoryAccess;
import gov.nasa.podaac.archive.external.InventoryFactory;
import gov.nasa.podaac.archive.external.InventoryQuery;
import gov.nasa.podaac.inventory.api.Constant.GranuleStatus;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.DatasetElement;
import gov.nasa.podaac.inventory.model.DatasetLocationPolicy;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.GranuleArchive;
import gov.nasa.podaac.inventory.model.GranuleReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Reassociate {
	private static Log log = LogFactory.getLog(Reassociate.class);
	
	private List<String> errorList = new ArrayList<String>();
	private List<String> msgList = new ArrayList<String>();
	private Map<String,String> locMap = new HashMap<String,String>();
	private Map<String,String> toMap = new HashMap<String,String>();
	
	InventoryQuery iq = null;
	InventoryAccess ia = null;
	GranuleStatusChangeNotification gsfcn = null;
	
	private boolean testMode = true;
	private boolean interactive = true;
	
	//the default, overwrite files
	private boolean updateMetaOnMissing = false;
	private boolean overwrite = true;
	
	
	public void setTestMode(boolean val){
		this.testMode = val;
	}
	
	/*
	 * Default will use the factory methods.
	 */
	public Reassociate(){
		this.iq = InventoryFactory.getInstance().getQuery();
		this.ia = InventoryFactory.getInstance().getAccess();	
		this.gsfcn = GranuleStatusChangeFactory.getInstance();
	}
	
	/*
	 * Have these be setable for testing....
	 */
	public Reassociate(InventoryQuery i_iq, InventoryAccess i_ia, GranuleStatusChangeNotification i_gsfcn){
		this.iq = i_iq;
		this.ia = i_ia;
		this.gsfcn = i_gsfcn;
	}
	
	public Map<String,Object> granuleReassociate(Integer granuleId, String toDataset){
		interactive = false;
		Granule g = iq.getGranuleMetadata(granuleId).getGranule();
		String fromLoc = g.getRootPath() + File.separator + g.getRelPath();

		log.debug("From Dataset:" +g.getDataset().getDatasetId());
		Dataset fromD = iq.fetchDatasetByPersistentId(g.getDataset().getDatasetId().toString());
		
		
		log.debug("To Dataset:" +toDataset);

		Dataset toD = iq.fetchDatasetByPersistentId(toDataset);
		String toBasePath = null;
		
		//make sure locMaps are defined (location policies).
		for(DatasetLocationPolicy dlp : fromD.getLocationPolicySet()){
			locMap.put(dlp.getType(), dlp.getBasePath());
		}
		//fetch toBasePath
		for(DatasetLocationPolicy dlp : toD.getLocationPolicySet()){
			if(dlp.getType().contains("ARCHIVE"))
				toBasePath = dlp.getBasePath();
			toMap.put(dlp.getType(),dlp.getBasePath());
		}
		
		HashMap<String,Object> ret = new HashMap<String,Object>();
		
		if(toBasePath == null){
			log.info("No toBasePath Found. Exiting.");
			errorList.add("No 'to basepath' found for dataset ["+toDataset+"]. Failed to reassociate granule.");
			ret.put("type", "failure");
			ret.put("msgs", errorList);
			return ret;
		}
		
		log.info("Reassociating Granule ["+g.getGranuleId()+":"+g.getName()+"]");
		reassociateGranule(g,toBasePath,toD, fromD);
		/*
		 * Add Granule Status change entry
		 */
		if(!this.testMode){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("oldDataset", fromD.getDatasetId());
			map.put("oldLocation", fromLoc);
			//Container for sending the granule...
			ArchiveMetadata am = new ArchiveMetadata();
			am.setGranule(g);
			gsfcn.notifyChange(GranuleStatusChange.REASSOCIATE, am, map);
		}
		
		if(errorList.size() > 0 ){
			log.debug("Errors processing reassociate.");
			ret.put("type", "failure");
			ret.put("msgs", errorList);
			return ret;
		}
		else{
			log.debug("Successfully processed granule");
			ret.put("type", "success");
			ret.put("msgs", msgList);
			return ret;
		}
		
	}
	
	public void reassociateGranules(Dataset fromD, Dataset toD,
			String gnp, boolean moveAll) {
		
		
		String toBasePath = null;
		List<Integer> granuleIds;
		
		//get granules by name/pattern
		if(moveAll){
			granuleIds = iq.getGranuleIdList(fromD.getDatasetId());
		}
		else{
			//find the granules by pattern...
			granuleIds = new ArrayList<Integer>();
			List<Granule> gList = iq.locateGranules(fromD.getDatasetId(), gnp, null, null);
			for(Granule g: gList){
				granuleIds.add(g.getGranuleId());
			}
		}
		log.info("Number of granules to reassociate: " + granuleIds.size());
		
		//fetch fromBasePaths
		for(DatasetLocationPolicy dlp : fromD.getLocationPolicySet()){
			locMap.put(dlp.getType(), dlp.getBasePath());
		}
		//fetch toBasePath
		for(DatasetLocationPolicy dlp : toD.getLocationPolicySet()){
			if(dlp.getType().contains("ARCHIVE"))
				toBasePath = dlp.getBasePath();
			toMap.put(dlp.getType(),dlp.getBasePath());
		}
		if(toBasePath == null){
			log.info("No toBasePath Found. Exiting.");
			System.exit(99);
		}
		int count = 1;
		for(Integer i : granuleIds){
			
			Granule g  = iq.getGranuleMetadata(i).getGranule();
			String fromLoc = g.getRootPath() + File.separator + g.getRelPath();
			System.out.println("Processing granule "+count+" of " + granuleIds.size() + " ["+g.getGranuleId()+":"+g.getName()+"]");
			log.info("Processing granule "+count+" of " + granuleIds.size() + " ["+g.getGranuleId()+":"+g.getName()+"]");
			reassociateGranule(g, toBasePath, toD,fromD);
			/*
			 * Add Granule Status change entry
			 */
			if(!this.testMode){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("oldDataset", fromD.getDatasetId());
				map.put("oldLocation", fromLoc);
				//Container for sending the granule...
				ArchiveMetadata am = new ArchiveMetadata();
				am.setGranule(g);
				GranuleStatusChangeFactory.getInstance().notifyChange(GranuleStatusChange.REASSOCIATE, am, map);
			}
			++count;
			
		}
		
		if(errorList.size() > 0){
		
			System.out.println("---------------------------------");
			System.out.println("Processing completed with errors:");
			System.out.println("---------------------------------");
			for(String s : errorList){
				System.out.println("\t" + s);
			}
			
			log.debug("---------------------------------");
			log.debug("Processing completed with errors:");
			log.debug("---------------------------------");
			for(String s : errorList){
				log.debug("\t" + s);
			}
		}
		else{
			System.out.println("-----------------------------------");
			System.out.println("Processing completed without errors");
			System.out.println("-----------------------------------");
			
			log.debug("-----------------------------------");
			log.debug("Processing completed without errors");
			log.debug("-----------------------------------");
		}
	}

	private void reassociateGranule(Granule g, String toBasePath, Dataset toD, Dataset fromD){
		
		if(this.testMode){
			String fromLoc=null, toLoc=null;
			for(GranuleArchive ga : g.getGranuleArchiveSet()){
				//create old,new file locations
				fromLoc = g.getRootPath() + File.separator + g.getRelPath() + File.separator + ga.getName();
				if(interactive)
					System.out.println("\tFrom: " + g.getRootPath() + File.separator + g.getRelPath() + File.separator + ga.getName());
				
				log.info("\tFrom: " + g.getRootPath() + File.separator + g.getRelPath() + File.separator + ga.getName());
				msgList.add("["+g.getGranuleId()+"] From: " + g.getRootPath() + File.separator + g.getRelPath() + File.separator + ga.getName());
				toLoc = toBasePath + File.separator + g.getRelPath() + File.separator + ga.getName();
				
				if(interactive)
					System.out.println("\tto: " + toBasePath + File.separator + g.getRelPath() + File.separator + ga.getName());
				
				log.info("\tto: " + toBasePath + File.separator + g.getRelPath() + File.separator + ga.getName());
				msgList.add("["+g.getGranuleId()+"] to: " + toBasePath + File.separator + g.getRelPath() + File.separator + ga.getName());
			}
			return;
		}
		
		
		//offlineGranule
		//ia.updateGranuleArchiveStatus(g.getGranuleId(), "OFFLINE");
		
		//move files
		if(!moveFiles(g,toBasePath)){
			//ERROR OCCURED, should we skip the rest?
			log.warn("Error moving files. Granule id ["+g.getGranuleId()+"] will abort processing (metadata has not been changed.");
			errorList.add("Error moving files. Granule id ["+g.getGranuleId()+"] will abort processing (metadata has not been changed.)");
			
			return;
		}	
		log.debug("Set granule root, dataset to ["+toBasePath+"," + toD.getDatasetId()+"]");
		g.setRootPath(toBasePath);
		g.setDataset(toD);
		String at = null;
		try{
			at = toD.getDatasetPolicy().getAccessType();
		}catch(Exception e){
			log.debug("error getting dataset info.");
			at = iq.getDatasetMetadata(toD.getDatasetId()).getDataset().getDatasetPolicy().getAccessType();
		}
		log.debug("Setting granule access type to \"ToDataset's\" AccessType: " + toD.getDatasetPolicy().getAccessType());
		g.setAccessType(toD.getDatasetPolicy().getAccessType());
		//see if there exists a granule for the toDataset already
		log.debug("Delete Checks");
		List<Integer> gIds =  iq.fetchGranulesByDatasetAndPatter(toD.getDatasetId(), g.getName());
		if(gIds != null){
			log.debug("Listing is not null.");
			if(!gIds.isEmpty()){
				log.debug("GIds are not empt. Removing granule["+gIds.get(0)+"]");
				//an existing entry with this name exists.
				Integer i = gIds.get(0);
				Granule replace = iq.fetchGranule(i);
				//delete the replace metadata,
				if(replace != null)
					ia.deleteGranule(replace, false);
			}
		}
		
		
		ia.updateGranuleInfo(g);  //UNCOMMENT THIS 
		
		//update granuleRefs
		log.debug("Changing local granule reference paths");
		reReference(g,toMap, locMap);
		
		
		//TODO
		//update granule_real, date, int, char, spatial
		//Granuleelements will need to be mapped to the new DEIDS and updated in the granule_* tables
		log.debug("Chagning granule elements to use new dataset IDs");
		reElement(g, toD, fromD);
	
		//set granule to online
		//ia.updateGranuleArchiveStatus(g.getGranuleId(), "ONLINE");
	}
	
	private void reElement(Granule g, Dataset toD, Dataset fromD) {
		ia.reElement(g,toD,fromD);
	}

	private void reReference(Granule g, Map<String, String> toMap,
			Map<String, String> locMap) {
		for(GranuleReference ref : g.getGranuleReferenceSet()){
			if(ref.getType().contains("LOCAL")){
				log.debug("from: " + ref.getPath());
				//check to make sure the *Map.gets are not null
				String replaceWith = toMap.get(ref.getType());
				if(replaceWith == null)
				{
					errorList.add("No entry in \"toDataset\" location policy for:" + ref.getType());
					continue;
				}
				String replace = locMap.get(ref.getType());
				if(replace == null){
					errorList.add("No entry in \"fromDataset\" location policy for:" + ref.getType());
					//this should never happen
					continue;
				}

				String newRef = ref.getPath().replaceAll(replace, replaceWith);
				log.debug("newRef: " + newRef);
				//make sure newRef is different that oldRef
				if(newRef.equals(ref.getPath())){
					log.debug("newRef same as oldRef.");
					errorList.add("granule ["+g.getGranuleId()+"] newReference same as old reference.");
					continue;
				}
				ia.updateGranuleReferencePath(g.getGranuleId(), ref.getPath(), newRef);
			}
		}
		
	}

	private boolean moveFiles(Granule g, String toBasePath) {
		
		log.debug("Granule name: " + g.getName());
		String fromLoc=null, toLoc=null;
		boolean noErrors = true;
		for(GranuleArchive ga : g.getGranuleArchiveSet()){
			//create old,new file locations
			fromLoc = g.getRootPath() + File.separator + g.getRelPath() + File.separator + ga.getName();
			log.debug("From: " + g.getRootPath() + File.separator + g.getRelPath() + File.separator + ga.getName());
			toLoc = toBasePath + File.separator + g.getRelPath() + File.separator + ga.getName();
			log.debug("to: " + toBasePath + File.separator + g.getRelPath() + File.separator + ga.getName());
			if(!ArchiveData.rename(fromLoc.substring(6), toLoc.substring(6),overwrite)){
				errorList.add("Error moving granule file ["+fromLoc+"] to ["+toLoc+"]. Run with debug mode for more details.");
				noErrors = false;
			}else
				log.info("Successfully moved granule file ["+fromLoc+"] to ["+toLoc+"].");
		}
		//return true if we had an error.
		return noErrors;
		
	}
}
