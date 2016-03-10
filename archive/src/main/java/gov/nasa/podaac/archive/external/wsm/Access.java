package gov.nasa.podaac.archive.external.wsm;

import gov.nasa.podaac.archive.core.AIP;
import gov.nasa.podaac.archive.core.ArchiveData;
import gov.nasa.podaac.archive.core.ArchiveProperty;
import gov.nasa.podaac.archive.external.InventoryAccess;
import gov.nasa.podaac.archive.external.InventoryFactory;
import gov.nasa.podaac.archive.xml.Packet;
import gov.nasa.podaac.inventory.api.DataManager;
import gov.nasa.podaac.inventory.api.DataManagerFactory;
import gov.nasa.podaac.inventory.api.Inventory;
import gov.nasa.podaac.inventory.api.Service;
import gov.nasa.podaac.inventory.api.ServiceFactory;
import gov.nasa.podaac.inventory.api.Constant.GranuleArchiveStatus;
import gov.nasa.podaac.inventory.api.Constant.GranuleArchiveType;
import gov.nasa.podaac.inventory.api.Constant.GranuleStatus;
import gov.nasa.podaac.inventory.api.Constant.LocationPolicyType;
import gov.nasa.podaac.inventory.exceptions.InventoryException;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.GranuleReference;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.xml.sax.SAXException;

public class Access implements InventoryAccess
{	
	public class Subpath {
		public String path = null;
		public Boolean OutsideBP = false;
	}
	
	
	
	final private static Log log = LogFactory.getLog(Access.class);
	//  final private Utilities utils;
	final private Service service; 
	private String user = null;
	private String pass = null;
	
	private static String ALL_DATA = "/allData/"; 
	private static String STORE = "/store/";
	
	public Access(String URI, int PORT) {
		service = ServiceFactory.getInstance().createService(URI, PORT);
        user=ArchiveProperty.getInstance().getProperty(ArchiveProperty.INVENTORY_WS_USER);
        pass=ArchiveProperty.getInstance().getProperty(ArchiveProperty.INVENTORY_WS_PASSWORD);
        service.setAuthInfo(user, pass);
	}
	
	//Constructor use for Test.
	public Access(Service service){
		this.service = service;
	}

//	public Acess(String URI){
//
//	}
	public boolean delete (Granule granule) {
		try {
			service.deleteGranule(granule, false);
			return true;
		} catch (InventoryException e) {
			log.error("unable to delete granule: " + granule.getGranuleId());
			return false;
		}
	}
    public void deleteGranule (Granule granule, boolean dataOnly)
    {
    	//this.request ("granule/delete?granuleId=" + granule.getGranuleId()+"&dataOnly="+dataOnly);
    	if (dataOnly) {
			// if request not to delete metadata
			log.debug("delete archived data-only;");
			setGranuleArchiveStatus(granule, GranuleArchiveStatus.DELETED.toString()); //will work
			removeLocalReference(granule); //will work
			Set<GranuleReference> refSet = granule.getGranuleReferenceSet();
			if (refSet==null || refSet.isEmpty()) {
				try {
					service.updateGranuleStatus(granule.getGranuleId(), "OFFLINE");
				} catch (InventoryException e) {
					log.error("Unable to update granule status.");
				}
			}
		} else {
			if(delete(granule))
				log.debug("Metadata: deleted.");
		}
    }

    public void remoteGranule (Granule granule)
    {
        //this.request ("granule/remote?granuleId=" + granule.getGranuleId());
        removeLocalReference(granule);
        List<Integer> gids = new ArrayList<Integer>();
        gids.add(granule.getGranuleId());
        //update the status to whatever it is set to...
        updateVerifyGranuleStatus(gids,granule.getStatus().toString());
        setGranuleArchiveStatus(granule, GranuleArchiveStatus.DELETED.toString());
    }

    public void updateAIPArchiveStatus (List<AIP> aipList)
    {
    	log.debug("updateAIPArchiveStatus: aipsize="+aipList.size());
		for (AIP aip : aipList) {
			//set granuleArchive 
			//status, gId,name (dest substring), destination
			updateAIPGranuleArchiveStatus(aip);
			if (aip.getType().equals(GranuleArchiveType.DATA.toString())) {
				updateAIPGranuleReferenceStatus(aip);
			}
		}	
    }

    public void updateAIPGranuleArchiveStatus (List<AIP> aipList)
    {
    	log.debug("updateAIPGranuleArchiveStatus: aipsize="+aipList.size());
		// Use direct native sql instead;
		
		for (AIP aip : aipList) {
			updateAIPGranuleArchiveStatus(aip);
		
		}	
	   
    }
    private void updateAIPGranuleArchiveStatus (AIP aip){
    	service.updateGranuleAIPArchive(aip.getGranuleId(),aip.getType(),aip.getDestination().toString(),aip.getDestination().toString().substring(aip.getDestination().toString().lastIndexOf(File.separator)+1),aip.getStatus());
    }
    
    private void updateAIPGranuleReferenceStatus (AIP aip){
    	service.updateGranuleAIPReference(aip.getGranuleId(),aip.getType(),aip.getDestination().toString(),aip.getDestination().toString().substring(aip.getDestination().toString().lastIndexOf(File.separator)+1),aip.getStatus());
    }
    
    public void updateAIPGranuleReferenceStatus (List<AIP> aipList)
    {
    	log.debug("updateAIPGranuleReferenceStatus: aipsize="+aipList.size());
    	for (AIP aip : aipList) {
    		log.debug("type: " + aip.getType());
    		//if(aip.getType().equals(GranuleArchiveType.DATA.toString())){
    			updateAIPGranuleReferenceStatus(aip);
    		//}
		}	
    	
    }

    public void updateGranuleArchiveChecksum (Integer granuleId, String name, String sum)
    {
		log.debug("updateGranuleArchiveChecksum "+granuleId);
		try {
			service.updateGranuleArchiveChecksum(granuleId, name, sum);
		} catch (InventoryException e) {
			log.error("error updating information: "+ e.getMessage());
		}
    }

    public void updateGranuleArchiveSize (Integer granuleId, String name, Long Size)
    {
    	log.debug("updateGranuleArchiveSize "+granuleId);
    	try {
			service.updateGranuleArchiveSize(granuleId, name, Size);
		} catch (InventoryException e) {
			log.error("error updating information: "+ e.getMessage());
		}
    	
    }

    public void updateGranuleArchiveStatus (Integer granuleId, String status)
    {
    	log.debug("updateGranuleArchiveSize "+granuleId);
    	try {
			service.updateGranuleArchiveStatus(granuleId, status);
		} catch (InventoryException e) {
			log.error("error updating information: "+ e.getMessage());
		}
    }

    public void updateGranuleLocation (Granule granule, String archiveBaseLocation, String baseLocation)
    {
    	log.debug("UpdateGranuleLocation");
    	String basepath = ArchiveData.absolutePath(baseLocation);

		//Only update the granule root_path
		if(!basepath.startsWith("file://"))
			basepath = "file://" + basepath;
		
		//call to update root_path
		try {
			service.updateGranuleRootPath(granule.getGranuleId(), basepath);
		} catch (InventoryException e) {
			log.error("Error updating granule basepath: "+e.getMessage());
			log.error("Will not remove local granule references.");
			return;
		}
		
		removeLocalReference(granule);
    }
    
    //Updates to make this section testable.
    public boolean updateLocalReference (Granule granule, Map<String, String> localBaseLocationPolicy)
    {
    	log.debug("updateLocalReference:");
		
    	if (granule==null) return false;
		
    	if (localBaseLocationPolicy==null) return false;
		
    	String dataFilename = InventoryFactory.getInstance().getQuery().getOnlineDataFilePath(granule);
    	
		if (dataFilename != null) {
			String archiveBaseLocation =
				localBaseLocationPolicy.remove(LocationPolicyType.ARCHIVE.toString());
			if (archiveBaseLocation==null) {
				archiveBaseLocation =  ArchiveData.getBaseLocation(dataFilename);
			}
			
			
			//TEST ME
			Subpath sp = getSubpathFromLocation(dataFilename, archiveBaseLocation);
			String subpath = sp.path;
			boolean outsideBP = sp.OutsideBP;
			
			/*
			 * UPDATES HERE for non-creation of references
			 */
			removeLocalReference(granule); //add db call for delete
			log.debug("Granule refs deleted");
			
			try {
				granule = service.getGranuleById(granule.getGranuleId());
			} catch (InventoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//TEST
			setGranuleReference(granule, localBaseLocationPolicy, subpath, outsideBP); //add db call for delete
			return true;
			
		} else {
			log.debug("GranuleId="+granule.getGranuleId()+": no ONLINE-DATA for references creation!");
			return false;
		}
    
    }
    
    public Subpath getSubpathFromLocation(String dataFilename, String archiveBaseLocation){
    	String subpath = dataFilename.replaceFirst(archiveBaseLocation+"/", "");
		boolean outsideBP = false;
		if(subpath.equals(dataFilename)){
			log.debug("relocating file outside of its basepath...");
			subpath = subpath.substring(subpath.indexOf(STORE) + 7);
			outsideBP = true;
		}
		log.debug("****SUBPATH: " + subpath);
		Subpath sp = this.new Subpath();
		sp.OutsideBP = outsideBP;
		sp.path = subpath;
		return sp;
    }

    
    
    
    public Set<GranuleReference> setGranuleReference (Granule granule, Map<String, String> baseLocationPolicy, String subpath, boolean outsideBP){
    	
    	String fname = subpath.substring(subpath.lastIndexOf("/") + 1);
    	log.debug("Got filename: " + fname);
    	Set<GranuleReference> newSet = new HashSet<GranuleReference>();
    	
    	for(String type: baseLocationPolicy.keySet()){
    		GranuleReference gr = new GranuleReference();
    		String path = null;
    		log.debug("Processing: " + type);
    		//REMOTE
    		if(type.startsWith("REMOTE")){
    			path = baseLocationPolicy.get(type) + "/" + granule.getRelPath() + "/" + fname;  
    			log.debug("Creating remote reference: " + path);
    			
    		}
    		else
    		{ //LOCAL*
    			if(outsideBP){
    				String bp = baseLocationPolicy.get(type);
    				path = bp.substring(0 , bp.indexOf(ALL_DATA) + 8) +"/"  + subpath;
    			}else{
    				path = baseLocationPolicy.get(type) + "/" + subpath;
    			}
    			log.debug("Creating remote reference: " + path);
    		}
    		
    		gr.setDescription("Verifiy created description");
			gr.setGranuleId(granule.getGranuleId());
			if (type.trim().endsWith("OPENDAP")) {
				gr.setPath(path + ".html");
			} else
				gr.setPath(path);
			
			gr.setStatus(granule.getStatus().toString());
			gr.setType(type);
    		
			boolean found = false;
			if(type.startsWith("REMOTE")){
				for(GranuleReference grOriginal : granule.getGranuleReferenceSet()){
					//already exists...
					if(grOriginal.getPath().equals(gr.getPath())){
						newSet.add(grOriginal);	
						found = true;
					}
				}
			}
			
			if(!found){
				try {
					log.debug("Adding granule reference: " + gr.getPath());
					service.addGranuleReference(granule.getGranuleId(), gr.getPath(), gr.getStatus(), gr.getType(), gr.getDescription());
					newSet.add(gr);
					log.debug(newSet.size());
					}catch(InventoryException e) {
						log.error("Error for granule reference (id, path): ["+gr.getGranuleId()+","+gr.getPath()+"]");
						log.error("Error updating granule reference status: " + e.getMessage());	
					}
			}
    	}
    	
    	return newSet;
    	//granule.setGranuleReferenceSet(newSet);
    }
    
    //no actual updates done here, jsut setting info for the granule object for the "update" call.
    public void setGranuleReference (Granule granule, Map<String, String> baseLocationPolicy, String subpath)
    {
    	
    	Set<GranuleReference> newSet = new HashSet<GranuleReference>(
				granule.getGranuleReferenceSet()
    	);
    	
    	log.debug("Existing refs: " + newSet.size());
    	for(GranuleReference gr : granule.getGranuleReferenceSet()){
    		log.debug("*-*" + gr.getType() +":"+ gr.getPath() + "*-*");	
    	}

    	log.debug("Size: " + baseLocationPolicy.keySet().size());
		Set<String> baseLocationTypes = baseLocationPolicy.keySet();
		
		
		
		
		
		for (String baseLocationType : baseLocationTypes) {
			log.debug("Processing blt: " +baseLocationType);
			
			GranuleReference ref = new GranuleReference();
			ref.setType(baseLocationType);
			ref.setDescription("");
			
			String path = baseLocationPolicy.get(baseLocationType)
						+ (subpath.startsWith("/") ? subpath : "/"+subpath);
			log.info("new path: " + path);
			if (baseLocationType.trim().endsWith("OPENDAP")) {
				ref.setPath(path + ".html");
			} else
				ref.setPath(path);
			ref.setStatus(GranuleArchiveStatus.ONLINE.toString());
			boolean found = false;
			for(GranuleReference r: newSet)
			{
				if(ref.getPath().equals(r.getPath()))
				{
					log.debug("Remote reference \""+ref.getPath()+"\" already exists. Not adding new reference.");
					found = true;
					r.setStatus("ONLINE");
					try {
						service.updateGranuleReferenceStatus(r.getGranuleId(), r.getPath(), "ONLINE");
					} catch (InventoryException e) {
						log.error("Error for granule reference (id, path): ["+r.getGranuleId()+","+r.getPath()+"]");
						log.error("Error updating granule reference status: " + e.getMessage());
					}
				}
			}
			if(!found){
				log.debug("Not Found! Adding... " + ref.getPath());
				if(ref.getStatus() == null)
					ref.setStatus("ONLINE");
				
				try {
					service.addGranuleReference(granule.getGranuleId(), ref.getPath(), ref.getStatus(), ref.getType(), ref.getDescription());
					newSet.add(ref);
					break;
				} catch (InventoryException e) {
					log.error("Error for granule reference (id, path): ["+ref.getGranuleId()+","+ref.getPath()+"]");
					log.error("Error adding granule reference status: " + e.getMessage());
				}	
			}
		}
		log.debug("Setting granule references with following number of remote references: " + newSet.size());
		for(GranuleReference r : newSet)
		{
			log.debug("Reference path to add: " +r.getPath());
		}
		granule.setGranuleReferenceSet(newSet);
    	
    }
    
    public  void removeLocalReference(Granule granule) {
		removeReference(granule, LocationPolicyType.LOCAL.toString());
	}
    
    public  void removeReference(Granule granule, String type) {
		Set<GranuleReference> newSet = new HashSet<GranuleReference>(
				granule.getGranuleReferenceSet()
				);
		Set<GranuleReference> currentSet = granule.getGranuleReferenceSet();	
		
		
		//granuleId and 
		try {
			service.deleteGranuleLocalReference(granule.getGranuleId(), type);
			granule = service.getGranuleById(granule.getGranuleId());
		} catch (InventoryException e) {
			// TODO Auto-generated catch block
			log.error("Error deleting granule references.");
			return;
		}
	}
    
   
    
    public  void setGranuleArchiveStatus (Granule granule, String status) {
    	/*Set<GranuleArchive> archiveSet = granule.getGranuleArchiveSet();
		for (GranuleArchive archive : archiveSet) {
			archive.setStatus(status);
		}*/
		updateGranuleArchiveStatus(granule.getGranuleId(), "DELETED");
	}
    
    public void updateVerifyGranuleStatus (List<Integer> granuleIdList, String status)
    {
		log.debug("updateVerifyGranuleStatus: granuleIdList.size="+granuleIdList.size());
		
		//String verifyTime = formatter.format(new Date());
		for (Integer gId : granuleIdList) {
			//String inClause = "("+granuleIdList.get(i);
			//int nextSize = Math.min(i+INCLAUSE_LIMIT, size);
		    //for (int j=i+1; j<nextSize; j++) inClause += ", " + granuleIdList.get(j);
		    //inClause += ")";
			try {
				service.updateStatusAndVerify(gId, status);
			} catch (InventoryException e) {
				log.error("Error updating granule["+gId+"] status to '"+status+"'... " + e.getMessage());
			}
		}	
    }
    
    
	public void updateGranuleInfo(Granule g){
		//only changes dataset_id, AccessType and basepath.
		try {
			
			service.updateGranuleReassociate(g.getGranuleId(), g.getRootPath(), g.getDataset().getDatasetId(), g.getAccessType());
		} catch (InventoryException e) {
			log.error("unable to reassociate granule["+g.getGranuleId()+"]: " + e.getMessage());
		}
	
	}

	public void reassociateGranuleElement(Integer gId,Integer key, Integer deId, String type){
		//don't need to do this...
		//rework this in future release.
		//this.request ("granule/verifyUpdate?granuleId=");
	}
	
	@Override
	public void updateGranuleReferencePath(Integer granuleId, String path,
			String newRef) {
		
		try {
			service.updateGranuleReferencePath(granuleId, path, newRef);
		} catch (InventoryException e) {
				log.error("Error updating granule reference Path [id, path]: ["+granuleId+","+path+"]");
				log.error("Error message: " + e.getMessage());
		}
	}
	
	@Override
	public void reElement(Granule g, Dataset toD, Dataset fromD) {
		try {
			service.updateGranuleReassociateElement(g.getGranuleId(), fromD.getDatasetId(), toD.getDatasetId());
		} catch (InventoryException e) {
			log.error("Could not reasociate granule elements: " + e.getMessage());
		}
	}
}
