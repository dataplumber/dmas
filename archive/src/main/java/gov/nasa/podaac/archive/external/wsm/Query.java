package gov.nasa.podaac.archive.external.wsm;

import gov.nasa.podaac.archive.core.AIP;
import gov.nasa.podaac.archive.core.ArchiveProperty;
import gov.nasa.podaac.archive.external.ArchiveMetadata;
import gov.nasa.podaac.archive.external.InventoryQuery;
//import gov.nasa.podaac.archive.xml.Packet;
import gov.nasa.podaac.common.api.util.StringUtility;
import gov.nasa.podaac.inventory.api.Constant.GranuleArchiveStatus;
import gov.nasa.podaac.inventory.api.Constant.GranuleArchiveType;
import gov.nasa.podaac.inventory.api.Constant.LocationPolicyType;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.DatasetLocationPolicy;
import gov.nasa.podaac.inventory.model.DatasetPolicy;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.GranuleArchive;
import gov.nasa.podaac.inventory.model.GranuleReference;

import gov.nasa.podaac.inventory.api.Service;
import gov.nasa.podaac.inventory.api.ServiceFactory;
import gov.nasa.podaac.inventory.exceptions.InventoryException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

class Query implements InventoryQuery
{
    final private Log log = LogFactory.getLog(Query.class);
//    final private Utilities utils;
    final private Service service; 
    private String user = null;
    private String pass = null;
    
    Query (String URI, int PORT)
    {
        service = ServiceFactory.getInstance().createService(URI,PORT);
        
        user=ArchiveProperty.getInstance().getProperty(ArchiveProperty.INVENTORY_WS_USER);
        pass=ArchiveProperty.getInstance().getProperty(ArchiveProperty.INVENTORY_WS_PASSWORD);
        service.setAuthInfo(user, pass);
    }
    
    public Granule fetchGranule(Integer id){
    	try {
			return service.getGranuleById(id);
		} catch (InventoryException e) {
			e.printStackTrace();
			log.error("Unable to connect to Inventory Service");
			log.debug("error:", e);
		}
		return null;
    }
    
    public Dataset fetchDatasetByPersistentId(String perId) {
    	try {
			return service.getDataset(perId);
		} catch (InventoryException e) {
			e.printStackTrace();
			log.error("Unable to connect to Inventory Service");
			log.debug("error:", e);
		}
		return null;
    }
    
    public Dataset fetchDataset (Integer id, String... properties)
    {
        List<Integer> ids = new ArrayList<Integer>(1);
        ids.add(id);
        log.debug("Returned Dataset Size: " + ids.size());
//        if(ids.size() < 1){
//        	log.info("No datasets returned.");
//        	return null;
//        }
        return this.fetchDatasets (ids).get(0);
    }
    
    private Dataset fetchDataset (String shortName)
    {
//        return this.utils.extractDataset (this.request ("dataset?datasetShortName=" + shortName)).get (0);
        try {
			return service.getDataset(shortName);
		} catch (InventoryException e) {
			log.error("Unable to fetch Dataset from inventory WS: " + e.getMessage());
			log.debug("error:", e);
		}
        return null;
        
    }
    
    private List<Dataset> fetchDatasets (List<Integer> ids)
    {
       List<Dataset> dl = new ArrayList<Dataset>();
       
       for(Integer id: ids){
    	   try {
			Dataset d = service.getDataset(id);
			dl.add(d);
		} catch (InventoryException e) {
			log.error("Unable to fetch Dataset from inventory WS: " + e.getMessage());
			log.debug("error:", e);
		}
       }
       return dl;
    }
	
    public List<Integer> fetchGranulesByDatasetAndPatter(Integer datasetId, String gnp){
		return service.getGranulesByDatasetAndPattern(datasetId,gnp);
	}

    public List<Granule> locateGranules(Integer Id, String pattern, Long startTime, Long stopTime){
    	//not implemented
    	try {
    		
    		Calendar start = Calendar.getInstance();
    		if(startTime != null)
    			start.setTimeInMillis(startTime);
    		else start = null;
    		
    		Calendar stop = Calendar.getInstance();
    		if(stopTime != null)
    			stop.setTimeInMillis(stopTime);
    		else
    			stop = null;
    		
			List<Integer> ll =  service.getGranuleIdListAll(Id,start,stop,pattern);
			List<Granule> gs = new ArrayList(ll.size());
			for(Integer xi : ll){
				gs.add(service.getGranuleById(xi));
			}
			return gs;
			
		} catch (InventoryException e) {
			log.error("Error fetching granule list:" + e.getLocalizedMessage());
			return null;
		}
		
    	
    	
    }
    
    private List<GranuleReference> fetchGranuleReferences (Integer datasetId)
    {
    	List<GranuleReference> list = null;
		try {
			list = service.getGranuleAllReferences(datasetId);
		} catch (InventoryException e) {
			log.error("Error fetching Granule List: " + e.getMessage());
			System.out.println("Error fetching Granule List: " + e.getMessage());
			log.debug("error:", e);
			return null;
		} 
    	return list; //should be done?
    }
    
    private List<Granule> fetchGranules (List<Integer> ids)
    {
    	List<Granule> gl = new ArrayList<Granule>();
        for(Integer i : ids){
        	try {
				gl.add(service.getGranuleById(i));
			} catch (InventoryException e) {
				log.debug("Error querying service: " + e.getMessage());
				//log.debug(e.getStackTrace().toString());
			}
        	
        }
        return gl;
    }
    
    public List<String> findDatasetByProductId (int id)
    {
        List<Dataset> datasets;
        List<Integer> ids = new ArrayList<Integer>(1);
        List<String> result;
        
        datasets = this.fetchDatasets (ids);
        result = new ArrayList<String>(datasets.size());
        for (Dataset ds : datasets) result.add (ds.getShortName());
        return result;
    }
    
    public List<Integer> findGranuleList (List<Integer> idList)
    {
    	try {
			return service.findGranuleList(idList);
		} catch (InventoryException e) {
			// TODO Auto-generated catch block
			log.error("Error fetching granule Id List: " + e.getMessage());
			return null;
		}
        //return this.request ("granuleVerify?" + this.toString ("granuleId=", idList)).getIdList().getIds();
    }
    
    public List<AIP> getArchiveAIPByDataset (Integer datasetId)
    {
        return this.getArchiveAIPByGranule (this.getGranuleIdList(datasetId));
    }
    
    public List<AIP> getArchiveAIPByGranule (List<Integer> idList)
    {
        List<AIP> result = new ArrayList<AIP>(idList.size());
        List<Granule> granules = new ArrayList<Granule>(idList.size());
        for(Integer i: idList){
        	Granule g = null;
			try {
				g = service.getGranuleById(i);
			} catch (InventoryException e) {
				log.error("Error fetching Granules");
				return null;
			}
        	granules.add(g);
        }
        
        
        for (Granule g : granules)
        {
            for (GranuleArchive ga : g.getGranuleArchiveSet())
            {
                try
                {
                    result.add (new AIP(g.getGranuleId(),
                                new URI(StringUtility.cleanPaths(g.getRootPath(), g.getRelPath(), ga.getName())),
                                ga.getChecksum(), g.getChecksumType(),
                                ga.getFileSize(), ga.getType(), ga.getStatus()));
                }
                catch (URISyntaxException urise)
                {
                    this.log.error ("getArchiveAIPByDataset: error getting archive info " + urise.getMessage());
                }
            }
        }
        
        return result;
    }
    
    private String getArchiveBaseLocation (Dataset dataset)
    {
        Set<DatasetLocationPolicy> locationPolicySet = dataset.getLocationPolicySet();
        
        if (locationPolicySet == null || locationPolicySet.isEmpty()) return "";
        
        LocationPolicyType type = getArchiveLocationType(dataset.getDatasetPolicy().getAccessType());
        
        for (DatasetLocationPolicy locationPolicy : locationPolicySet)
        {
            if (locationPolicy.getType().startsWith(type.toString())){
            	log.debug("lp.basepath: "+locationPolicy.getBasePath());
                return locationPolicy.getBasePath();                
            }
        }
        
        this.log.error("Unable to find " + type + " base location!!!");
        return null;
    }

    public String getArchiveBaseLocation (Integer datasetId)
    {
        List<Integer> ids = new ArrayList<Integer>(1);
        
        ids.add (datasetId);
        return this.getArchiveBaseLocation (this.fetchDatasets (ids).get (0));
    }

    public Map<Integer, String> getArchiveBaseLocation (List<Integer> granuleIdList)
    {
        List<Granule> granules = new ArrayList<Granule>(granuleIdList.size());
        Map<Integer,String> result = new TreeMap<Integer,String>();
        Map<String,String> dsBaseLocation = new TreeMap<String,String>();

        for (Granule g : granules)
        {
            String datasetName = g.getDataset().getShortName();
            String baseLocation = null;
            
            if (dsBaseLocation.containsKey(datasetName))
            {
                baseLocation = dsBaseLocation.get (datasetName);         
            }
            else
            {
                baseLocation = this.getArchiveBaseLocation (datasetName);
                dsBaseLocation.put (datasetName, baseLocation);
            }
            log.debug("adding: " + baseLocation);
            result.put (g.getGranuleId(), baseLocation);
        }
        log.debug("result" + result.size());
        return result;
    }

    public String getArchiveBaseLocation (String datasetName)
    {
        return this.getArchiveBaseLocation (this.fetchDataset (datasetName));
    }

    private LocationPolicyType getArchiveLocationType (String accessType)
    {
        LocationPolicyType result = null;
        
        try { 
        	log.debug("searching for: " + accessType);
        	log.debug("converting to: "+"ARCHIVE_"+accessType);
        	result = LocationPolicyType.valueOf("ARCHIVE_"+accessType);
        	
        }
        catch (IllegalArgumentException iae)
        {
            log.debug("Accesstype not found: " + accessType);
        }
        catch (NullPointerException npe)
        {
            log.debug("Accesstype is null.");
        }
        
        return result;
    }

    private List<String> getArchivePathList(Granule granule)
    {
        Set<GranuleArchive> archiveSet = granule.getGranuleArchiveSet();
        List<String> archiveList = new ArrayList<String>(archiveSet.size());
        
        for (GranuleArchive archive : archiveSet)
        {
            archiveList.add (StringUtility.cleanPaths(granule.getRootPath(), granule.getRelPath(),archive.getName()));
        }
        return archiveList;
    }

    private ArchiveMetadata getDatasetMetadata (Dataset dataset)
    {
    	DatasetPolicy dp = null;
    	//get policy
    	try {
			dp = service.getDatasetPolicy(dataset.getDatasetId());
			dataset.setDatasetPolicy(dp);
			
		} catch (InventoryException e) {
			// TODO Auto-generated catch block
			log.debug("stack trace: ",e);
		}
    	
        DatasetPolicy policy = dataset.getDatasetPolicy();
        ArchiveMetadata result = new ArchiveMetadata();
        
        result.setDataClass (policy.getDataClass());
        result.setDataDuration (policy.getDataDuration());
        result.setRemoteBaseLocation (this.getRemoteBaseLocation (dataset));
        result.setLocalBaseLocation (this.getLocalBaseLocation (dataset));
        return result;
    }

    public ArchiveMetadata getDatasetMetadata (Integer datasetId)
    {
        ArchiveMetadata result;
        List<Dataset> datasets;
        List<Integer> ids = new ArrayList<Integer>(1);

        ids.add (datasetId);
        datasets = this.fetchDatasets (ids);
        result = new ArchiveMetadata();
        result.setDataset(datasets.get (0));
        result.setDataClass (datasets.get (0).getDatasetPolicy().getDataClass());
        result.setDataDuration (datasets.get (0).getDatasetPolicy().getDataDuration());

        result.setLocalBaseLocation(this.getLocalBaseLocation(datasets.get(0))); 
	log.debug("Datasetlocations: " + datasets.get(0).getLocationPolicySet().size());
        result.setRemoteBaseLocation (this.getRemoteBaseLocation (datasets.get (0)));
        log.debug("Got baselocations: " + result.getRemoteBaseLocation().size() );
        return result;
    }

    public Map<Integer, ArchiveMetadata> getDatasetMetadata (List<Integer> granuleIdList)
    {
        ArchiveMetadata metadata = null;            
        List<Granule> granules = this.fetchGranules (granuleIdList);
        Map<Integer, ArchiveMetadata> result = new TreeMap<Integer, ArchiveMetadata>();
        Map<Integer, ArchiveMetadata> datasetCache = new TreeMap<Integer,ArchiveMetadata>();

        for (Granule g : granules)
        {
            Integer datasetId = g.getDataset().getDatasetId();
            
            metadata = null;
            
            if (datasetCache.containsKey (datasetId)) metadata = datasetCache.get( datasetId);         
            else
            {
                metadata = this.getDatasetMetadata (datasetId);
                if (metadata != null) datasetCache.put (datasetId, metadata);
            }
            
            if (metadata != null) result.put (g.getGranuleId(), metadata);
        }
        
        return result;
    }

    public List<Integer> getGranuleIdList (Integer datasetId)
    {
        return getGranuleIdList(datasetId,null,null);
    }
    public List<Integer> getGranuleIdList (Integer datasetId, Calendar start, Calendar stop)
    {
    	
    	try {
			return service.getGranuleIdListAll(datasetId,start,stop,null);
		} catch (InventoryException e) {
			log.error("Error fetching granule list:" + e.getLocalizedMessage());
		}
		return null;
        
    	
    	//return this.request ("granuleIds?datasetId=" + datasetId + "?startTime=" + Long.toString (start.getTimeInMillis()) + "?stopTime=" + Long.toString (stop.getTimeInMillis())).getIdList().getIds();
    }
    public ArchiveMetadata getGranuleMetadata (Integer granuleId)
    {
        ArchiveMetadata result = new ArchiveMetadata();
        Granule g;
        List<Integer> ids = new ArrayList<Integer>(1);
        
        ids.add (granuleId);
        List<Granule> gl = this.fetchGranules(ids);

        if(gl== null){
        	return result;
        }
        if(gl.isEmpty()){
        	return result;
        }
        
        g=gl.get(0);
        
        if (g != null)
        {
            result.setArchivePathList (getArchivePathList (g));
            result.setGranule (g);
	    log.debug("REFS: "+g.getGranuleReferenceSet().size());
        }
        return result;
    }

    public Integer getGranuleSizeByDataset (Integer datasetId)
    {
    	try {
			return service.getGranuleSize(datasetId);
		} catch (InventoryException e) {
			log.error("Error fetching granule size: " + e.getMessage());
			return null;
		}
    }

    public Integer getGranuleVersion (Integer id)
    {
        List<Integer> ids = new ArrayList<Integer>(1);
        
        ids.add (id);
        return this.fetchGranules (ids).get (0).getVersion();
    }

    private Map<String, String> getLocalBaseLocation(Dataset dataset)
    {
        Map<String, String> result = new TreeMap<String, String>();
        Set<DatasetLocationPolicy> locationPolicySet = dataset.getLocationPolicySet();          

        for (DatasetLocationPolicy locationPolicy : locationPolicySet)
        {
            String policyType = locationPolicy.getType();
            
            log.debug("type: "+locationPolicy.getType());
            log.debug("base: "+locationPolicy.getBasePath());
            log.debug("dsId: "+locationPolicy.getDatasetId());
            
            if (!policyType.startsWith(LocationPolicyType.ARCHIVE.toString()) &&
                 policyType.startsWith(LocationPolicyType.LOCAL.toString()))
            {
                result.put(policyType, locationPolicy.getBasePath());
            }
            // ARCHIVE-{AccessType} location
            else if (policyType.equals(this.getArchiveLocationType(dataset.getDatasetPolicy().getAccessType()).toString()))
            {
                result.put(LocationPolicyType.ARCHIVE.toString(), locationPolicy.getBasePath());
            }
        }
        return result;
    }

    public String getOnlineDataFilePath (Granule granule)
    {
        Set<GranuleArchive> granuleArchiveSet = granule.getGranuleArchiveSet();
        String result = null;

        for (GranuleArchive archive : granuleArchiveSet)
        {
            if (archive.getType().equals(GranuleArchiveType.DATA.toString()) &&
                archive.getStatus().equals(GranuleArchiveStatus.ONLINE.toString())) 
                result = StringUtility.cleanPaths(granule.getRootPath(), granule.getRelPath(),archive.getName());
        }
        
        return result;
    }

    public List<AIP> getReferenceAIPByDataset (Integer datasetId)
    {
        List<GranuleReference> refList = this.fetchGranuleReferences(datasetId);
        List<AIP> result = new ArrayList<AIP>();

        for (GranuleReference ref : refList)
        {
            try
            {
                result.add (new AIP(ref.getGranuleId(), new URI(ref.getPath()),
                            ref.getType(), ref.getStatus()));
            }
            catch (URISyntaxException urise)
            {
            	
                log.error("getReferenceAIPByDataset: error getting archive info "
                        + urise.getMessage());
                log.debug("error:", urise);
            }
        }           

        return result;
    }

    public List<AIP> getReferenceAIPByGranule (List<Integer> idList)
    {
        List<AIP> result = new ArrayList<AIP>();
        List<Granule> granules = this.fetchGranules (idList);
        
        try
        {
            for (Granule g : granules)
            {
                for (GranuleReference ref : g.getGranuleReferenceSet())
                {
                    result.add (new AIP(g.getGranuleId(), new URI(ref.getPath()),
                               ref.getType(), ref.getStatus()));      
                }
            }
         }
        catch (URISyntaxException urise)
        {
            this.log.error ("getReferenceAIPByGranule: error getting reference info "
                            + urise.getMessage());
        }

        return result;
    }

    public Set<GranuleReference> getReferenceSet (Integer id)
    {
        List<Integer> ids = new ArrayList<Integer>(1);
        return this.fetchGranules (ids).get (0).getGranuleReferenceSet();
    }

    private Map<String, String> getRemoteBaseLocation(Dataset dataset)
    {
        Map<String, String> remoteBaseLocation = new TreeMap<String, String>();
        Set<DatasetLocationPolicy> locationPolicySet = dataset.getLocationPolicySet();          

        for (DatasetLocationPolicy locationPolicy : locationPolicySet)
        {
            String policyType = locationPolicy.getType();

            if (policyType.startsWith(LocationPolicyType.REMOTE.toString())) 
                remoteBaseLocation.put(policyType, locationPolicy.getBasePath());
        }
  
        return remoteBaseLocation;
    }

    public Map<Integer, ArchiveMetadata> getRollingStore ()
    {
        List<Integer> ids = new ArrayList<Integer>();
        Map<Integer, ArchiveMetadata> result = new HashMap<Integer, ArchiveMetadata>(ids.size());
        
        for (Integer id : ids) result.put (id, this.getRollingStore (id)); 
        return result;
    }

    public ArchiveMetadata getRollingStore (Integer datasetId)
    {
        List<Integer> ids = new ArrayList<Integer>(1);
        
        ids.add (datasetId);
        List<Dataset> dl = this.fetchDatasets(ids);
        if(dl  == null){
        	return new ArchiveMetadata();
        }
        if(dl.isEmpty()){
        	return new ArchiveMetadata();
        }
        return this.getDatasetMetadata (dl.get(0));
    }

    public Map<Integer, ArchiveMetadata> getRollingStore (List<Integer> granuleIdList)
    {
        ArchiveMetadata metadata = null;            
        Integer datasetId;
        List<Granule> granules = this.fetchGranules (granuleIdList);
        Map<Integer, ArchiveMetadata> result = new HashMap<Integer, ArchiveMetadata>(granuleIdList.size());
        Map<Integer, ArchiveMetadata> datasetCache = new TreeMap<Integer,ArchiveMetadata>();

        for (Granule g : granules)
        {
            metadata = null;
            datasetId = g.getDataset().getDatasetId();
            if (datasetCache.containsKey (datasetId)) metadata = datasetCache.get(datasetId);         
            else
            {
                metadata = this.getDatasetMetadata (g.getDataset());
                if (metadata != null) datasetCache.put(datasetId, metadata);
            }
            if (metadata != null) result.put (g.getGranuleId(), metadata);
        }

        return result;
    }

//    private void request (String query)
//    {
//        Packet current = new Packet(), result = new Packet();
//        String puri;
//        StringBuilder buri = new StringBuilder();
//        
//        buri.append (ArchiveProperty.getInstance().getProperty("gov.nasa.podaac.archive.external.wsm.protocol", "http"));
//        buri.append ("://");
//        buri.append (ArchiveProperty.getInstance().getProperty("gov.nasa.podaac.archive.external.wsm.host", "lanina"));
//        buri.append (":");
//        buri.append (ArchiveProperty.getInstance().getProperty("gov.nasa.podaac.archive.external.wsm.port", "8090"));
//        buri.append ("/manager/archive/");
//        buri.append (query);
//        puri = buri.toString();
//        log.debug("Sending connection information to uri: " + puri);
//        try
//        {
//            HttpURLConnection connection;
//            URL url;
//            
//            do
//            {
//                url = new URL(puri);
//                connection = (HttpURLConnection)url.openConnection();
//                connection.connect();
//                current = Utilities.getInstance().read (connection.getInputStream());
//                
//                if (current.getPage() == 1) result = current;
//                else
//                {
//                    if (current.getDatasetList() != null) result.getDatasetList().getEncodedDataset().addAll (current.getDatasetList().getEncodedDataset());
//                    if (current.getGranuleList() != null) result.getGranuleList().getEncodedGranule().addAll (current.getGranuleList().getEncodedGranule());
//                    if (current.getGranuleReferencetList() != null) result.getGranuleReferencetList().getEncodedGranuleRef().addAll (current.getGranuleReferencetList().getEncodedGranuleRef());
//                    if (current.getIdList() != null) result.getIdList().getIds().addAll (current.getIdList().getIds());
//                    result.setPage (current.getPage());
//                    puri = buri.toString() + "?page=" + Integer.toString (current.getPage() + 1) + "?of=" + Integer.toString (current.getOf());
//                }
//            } while (current.getError() != null && current.getPage() != current.getOf());
//         }
//        catch (MalformedURLException murle)
//        {
//            murle.printStackTrace();
//            System.exit (-4);
//        }
//        catch (IOException ioe)
//        {
//            ioe.printStackTrace();
//            System.exit (-5);
//        }
//        catch (JAXBException jaxbe)
//        {
//            jaxbe.printStackTrace();
//            System.exit (-6);
//        }
//        catch (SAXException saxe)
//        {
//            saxe.printStackTrace();
//            System.exit (-7);
//        }
//        catch (URISyntaxException urise)
//        {
//            urise.printStackTrace();
//            System.exit (-8);
//        }
//        
//        if (current.getError() != null)
//        {
//            System.err.println ("The server had the following error:");
//            System.err.println ("   " + current.getError().getMessage());
//            
//            if (current.getError().getStackTrace() != null) System.err.println (current.getError().getStackTrace());
//            System.exit (-9);
//        }
//        
//        return result;
//    }
    
    private String toString (String id, List<Integer> ids)
    {
        StringBuilder list = new StringBuilder();
        
        for (Integer i : ids)
        {
            list.append (id);
            list.append (i.toString());
            list.append ("&");
        }
        list.setLength (list.length() - 1);

        return list.toString();
    }
}
