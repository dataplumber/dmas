//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.podaac.archive.external.direct;

import gov.nasa.podaac.archive.core.AIP;
import gov.nasa.podaac.archive.external.ArchiveMetadata;
import gov.nasa.podaac.archive.external.InventoryQuery;
import gov.nasa.podaac.common.api.util.StringUtility;
import gov.nasa.podaac.inventory.api.Constant.AccessType;
import gov.nasa.podaac.inventory.api.Constant.GranuleArchiveStatus;
import gov.nasa.podaac.inventory.api.Constant.GranuleArchiveType;
import gov.nasa.podaac.inventory.api.Constant.LocationPolicyType;
import gov.nasa.podaac.inventory.api.QueryFactory;
import gov.nasa.podaac.inventory.hibernate.HibernateSessionFactory;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.DatasetLocationPolicy;
import gov.nasa.podaac.inventory.model.DatasetPolicy;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.GranuleArchive;
import gov.nasa.podaac.inventory.model.GranuleReference;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;


/**
 * This class contains query methods that use inventory api to access database.
 *
 * @author clwong
 *
 * @version
 * $Id: Query.java 5361 2010-07-23 17:34:44Z gangl $
 */
class Query implements InventoryQuery
{	
	private static Log log = LogFactory.getLog(Query.class);
	private static final int INCLAUSE_LIMIT = 1000;
	
    public Dataset fetchDataset (Integer id, String...properties) { return Query.fetchDataset_(id, properties); }
    static Dataset fetchDataset_ (Integer id, String...properties) {
		gov.nasa.podaac.inventory.api.Query q = QueryFactory.getInstance().createQuery();
		Dataset dataset = q.fetchDatasetById(id, properties);
		return dataset;		 
	}
	
    public Dataset fetchDatasetByPersistentId(String perId){
    	gov.nasa.podaac.inventory.api.Query q = QueryFactory.getInstance().createQuery();
		Dataset dsInstance = new Dataset();
		dsInstance = q.findDatasetByPersistentId(perId);
		return dsInstance;		 
    }
    
    public Granule fetchGranule(Integer granuleId) {
    	gov.nasa.podaac.inventory.api.Query q = QueryFactory.getInstance().createQuery();
		return q.fetchGranuleById(granuleId);
	}
    
    public List<Granule> locateGranules(Integer Id, String pattern, Long startTime, Long stopTime){
    	
    	gov.nasa.podaac.inventory.api.Query q = QueryFactory.getInstance().createQuery();
    	
    	ArrayList<String> al = new ArrayList<String>();
    	al.add("granule.dataset="+Id.toString());
    	if(startTime != null){
    		al.add("archive_time_long >= "+startTime.toString());
    	}
    	if(stopTime != null){
    		al.add("archive_time_long <= "+stopTime.toString());
    	}
    	if(pattern != null){
    		pattern = pattern.replace('#', '%');
    		log.info("searching for granule names like: " + pattern);
    		al.add("name like '"+pattern+"'");
    	}
    	
    	String wheres [] = (String []) al.toArray (new String [al.size ()]);
    	String[] joins = {};
    	return q.fetchGranuleList(joins, wheres );
    }
    
	public static Dataset fetchDataset (String datasetName, String...properties) {
		gov.nasa.podaac.inventory.api.Query q = QueryFactory.getInstance().createQuery();
		Dataset dsInstance = new Dataset();
		dsInstance.setShortName(datasetName);
		List<Dataset> dsList = q.fetchDatasetList(dsInstance, properties);
		if ((dsList == null) || dsList.isEmpty())  return null;
		if (dsList.size()>1) {
			log.warn("Mulitiple datasets with same short name ["+datasetName+"]");
		}
		return dsList.get(0);		 
	}
	
	public static Granule fetchGranule(Integer id, String...properties) {
		gov.nasa.podaac.inventory.api.Query q = QueryFactory.getInstance().createQuery();
		Granule granule = q.fetchGranuleByIdDeep(id, properties);
		//Granule granule = q.fetchGranuleById(id, properties); //changed due to lazy init error
		return granule;
	}

	/*public static List<GranuleReference> getGranuleReferenceByID(Integer ID)
	{
		log.debug("getGranuleReferenceByID: ID="+ID);
		Session session = HibernateSessionFactory.getInstance().getCurrentSession();
		session.beginTransaction();	
	    long time = System.currentTimeMillis();
	    Iterator result = session.createSQLQuery(
				"select granule_id, Path, Type, Status, Description from granule_reference where granule_ID=" +ID).list().iterator();
		
	   
	    
	    return null;
		
		
	}*/
	
	public List<String> findDatasetByProductId (int id) { return Query.findDatasetByProductId_(id); }
	
	private static List<String> findDatasetByProductId_ (int id) {
		log.debug("findDatasetByProductId: id="+id);
		Session session = HibernateSessionFactory.getInstance().getCurrentSession();
		session.beginTransaction();	
		String query = "Select short_name from dataset where dataset_id in (Select dataset_id from collection_dataset where collection_id in (Select collection_id from collection_product where product_id='"+id+"'))";
		log.debug("Query: " + query);
		List<String> result = session.createSQLQuery(query).list();
	    session.getTransaction().commit(); // commit also closes session		
		return result;
	};
	
	
	public List<Integer> findGranuleList (List<Integer> idList) { return Query.findGranuleList_(idList); }
	private static List<Integer> findGranuleList_ (List<Integer> idList) {
		log.debug("findGranuleList: idList.size="+idList.size());
		Session session = HibernateSessionFactory.getInstance().getCurrentSession();
		session.beginTransaction();	
		int size = idList.size();
		List<Integer> foundList = new ArrayList<Integer>();
		for (int i=0; i<size; i+=INCLAUSE_LIMIT) {
			String inClause = "("+idList.get(i);
			int nextSize = Math.min(i+INCLAUSE_LIMIT, size);
		    for (int j=i+1; j<nextSize; j++) inClause += ", " + idList.get(j);
		    inClause += ")";
		    log.debug("select granule_id from granule where "
					+" granule_id IN "+inClause);
		    long time = System.currentTimeMillis();
		    Iterator result = session.createSQLQuery(
					"select granule_id from granule where "
					+" granule_id IN "+inClause)
					.list().iterator();
		    while (result.hasNext()) foundList.add(new Integer(result.next().toString()));
		    log.debug("time="+(System.currentTimeMillis()-time));
		}
	    session.getTransaction().commit(); // commit also closes session		
		return foundList;
	};
	
	public static List<AIP> getAIP(Granule granule) {
		Set<GranuleArchive> archiveSet = granule.getGranuleArchiveSet();
		List<AIP> aipList = new ArrayList<AIP>();
		for (GranuleArchive archive : archiveSet) {
			try {
				aipList.add(
						new AIP(granule.getGranuleId(), new URI(StringUtility.cleanPaths(granule.getRootPath(), granule.getRelPath(),archive.getName())), 
						archive.getChecksum(), granule.getChecksumType(), 
						archive.getFileSize(),
						archive.getType(), archive.getStatus())
				);
			} catch (Exception e) {
			}		
		}
		return aipList;
	}
	
	public List<AIP> getArchiveAIPByDataset (Integer datasetId) { return Query.getArchiveAIPByDataset_(datasetId); }
	private static List<AIP> getArchiveAIPByDataset_ (Integer datasetId) {
		gov.nasa.podaac.inventory.api.Query q = QueryFactory.getInstance().createQuery();
		String checksumType = "";
		try {
			checksumType = q.fetchDatasetById(datasetId, "datasetPolicy")
								.getDatasetPolicy().getChecksumType();
			log.debug("datasetId="+datasetId+":checksumType="+checksumType);
		} catch (Exception e) {
			log.error("getArchiveAIPByDataset: error getting datasetPolicy "+e.getMessage());
			return new ArrayList<AIP>();
		}
		try {
			long time = System.currentTimeMillis();
			List<GranuleArchive> archiveList = q.fetchArchiveByDatasetId(datasetId);
			log.debug("Time="+(System.currentTimeMillis() - time));			
			log.debug("getArchiveAIPByDataset: sizeOfResult="+archiveList.size());
			
			
			List<AIP> metadataList = new ArrayList<AIP>();
			for (GranuleArchive archive : archiveList) {
				Granule g = q.fetchGranuleById(archive.getGranuleId());
				
				metadataList.add(
						new AIP(archive.getGranuleId(), new URI(StringUtility.cleanPaths(g.getRootPath(), g.getRelPath(),archive.getName())), 
						archive.getChecksum(), checksumType, archive.getFileSize(),
						archive.getType(), archive.getStatus())
				);			
			}
			return metadataList;
		} catch (Exception e) {
			log.error("getArchiveAIPByDataset: error getting archive info "+e.getMessage());
			return new ArrayList<AIP>();
		}
	}

	public static List<AIP> getArchiveAIPByGranule(Integer... ids) {
		List<Integer> idList = new ArrayList<Integer>();
		for (Integer id : ids) idList.add(id);
		return getArchiveAIPByGranule_(idList);
	}

	public List<AIP> getArchiveAIPByGranule(List<Integer> idList) { return Query.getArchiveAIPByGranule_(idList); }
	private static List<AIP> getArchiveAIPByGranule_(List<Integer> idList) {
		gov.nasa.podaac.inventory.api.Query q = QueryFactory.getInstance().createQuery();
		try {
			List<AIP> metadataList = new ArrayList<AIP>();
			for (Integer granuleId : idList) {
				Granule granule = q.fetchGranuleById(granuleId, "granuleArchiveSet");
				if (granule==null) {
					log.warn("GranuleId="+granuleId+" not found!");
					continue;
				}
				metadataList.addAll(getAIP(granule));
			}
			log.debug("getArchiveAIPByGranule: sizeOfResult="+metadataList.size());
			return metadataList;
		} catch (Exception e) {
			log.error("getArchiveAIPByGranule: error getting archive info "+e.getMessage());
			return new ArrayList<AIP>();
		}
	}
	
	public static String getArchiveBaseLocation(Dataset dataset) {
		if (dataset==null) return null;
		Set<DatasetLocationPolicy> locationPolicySet = dataset.getLocationPolicySet();
		if (locationPolicySet == null || locationPolicySet.isEmpty()) return "";
		LocationPolicyType type = getArchiveLocationType(dataset.getDatasetPolicy().getAccessType());
		for (DatasetLocationPolicy locationPolicy : locationPolicySet) {
			if (locationPolicy.getType().startsWith(type.toString())) 
				return locationPolicy.getBasePath();				
		}
		log.error("Unable to find "+type+" base location!!!");
		return null;
	}
	
	public String getArchiveBaseLocation(Integer datasetId) { return Query.getArchiveBaseLocation_(datasetId); }
	private static String getArchiveBaseLocation_(Integer datasetId) {
		Dataset dataset = fetchDataset_(datasetId, "datasetPolicy", "locationPolicySet");
		return getArchiveBaseLocation(dataset);
	}
	
	public Map<Integer, String> getArchiveBaseLocation(List<Integer> granuleIdList) { return Query.getArchiveBaseLocation_(granuleIdList); }
	private static Map<Integer, String> getArchiveBaseLocation_(List<Integer> granuleIdList) {
		// <granuleId, baseLocation>
		Map<Integer,String> granuleBaseLocation = new TreeMap<Integer,String>();
		Map<String,String> dsBaseLocation = new TreeMap<String,String>();
		//<dsShortname, baseLocation>
		gov.nasa.podaac.inventory.api.Query q = QueryFactory.getInstance().createQuery();
		for (Integer granuleId : granuleIdList) {
			Granule granule = q.fetchGranuleById(granuleId, "dataset");
			String datasetName = granule.getDataset().getShortName();
			String baseLocation = null;
			if (dsBaseLocation.containsKey(datasetName)) {
				baseLocation = dsBaseLocation.get(datasetName);			
			} else {
				baseLocation = getArchiveBaseLocation_(datasetName);
				dsBaseLocation.put(datasetName, baseLocation);
			}
			granuleBaseLocation.put(granuleId, baseLocation);
		}
		return granuleBaseLocation;
	}

	public String getArchiveBaseLocation(String datasetName) { return Query.getArchiveBaseLocation_(datasetName); }
	private static String getArchiveBaseLocation_ (String datasetName)
	{
		Dataset dataset = fetchDataset(datasetName, "datasetPolicy", "locationPolicySet");
		return getArchiveBaseLocation(dataset);
	}
	
	public static String getArchiveDataFilePath(Granule granule) {
		String dataFilename = null;
		if (granule != null) {
			Set<GranuleArchive> granuleArchiveSet = granule.getGranuleArchiveSet();
			for (GranuleArchive archive : granuleArchiveSet) {
				if (archive.getType().equals(GranuleArchiveType.DATA.toString()))
					dataFilename = StringUtility.cleanPaths(granule.getRootPath(), granule.getRelPath(),archive.getName());
			}
		}
		return dataFilename;
	}

	public static String getArchiveDataFilePath(Integer granuleId) {
		Granule granule = Query.fetchGranule(granuleId, "granuleArchiveSet");
		return getArchiveDataFilePath(granule);
	}
	
	public static LocationPolicyType getArchiveLocationType (String accessType) {
		if (accessType.equals(AccessType.PRIVATE.toString())) {
			return LocationPolicyType.ARCHIVE_PRIVATE;
		}
		else if (accessType.equals(AccessType.OPEN.toString())) {
			return  LocationPolicyType.ARCHIVE_OPEN;
		}
//		else if (accessType.equals(AccessType.SIMULATED.toString())) {
//			return  LocationPolicyType.ARCHIVE_SIMULATED;
//		}
		else if (accessType.equals(AccessType.PREVIEW.toString())) {
			return  LocationPolicyType.ARCHIVE_PREVIEW;
		}
		else if (accessType.equals(AccessType.RETIRED.toString())) {
			return  LocationPolicyType.ARCHIVE_RETIRED;
		}
		else if (accessType.equals(AccessType.REDUCED.toString())) {
			return  LocationPolicyType.ARCHIVE_REDUCED;
		}
		else if (accessType.equals(AccessType.SHARED.toString())) {
			return  LocationPolicyType.ARCHIVE_SHARED;
		}
		else if (accessType.equals(AccessType.CONTROLLED.toString())) {
			return  LocationPolicyType.ARCHIVE_CONTROLLED;
		}
		else if (accessType.equals(AccessType.PUBLIC.toString())) {
			return  LocationPolicyType.ARCHIVE_PUBLIC;
		}
		else if (accessType.equals(AccessType.RESTRICTED.toString())) {
			return  LocationPolicyType.ARCHIVE_RESTRICTED;
		}
		else
			{
			System.out.println("Accesstype not found: " + accessType);
			log.info("Accesstype not found: " + accessType);
				return null;
			}
	}
	
	public static List<String> getArchivePathList(Granule granule) {
		Set<GranuleArchive> archiveSet = granule.getGranuleArchiveSet();
		List<String> archiveList = new ArrayList<String>();
		for (GranuleArchive archive : archiveSet) {
			archiveList.add(StringUtility.cleanPaths(granule.getRootPath(), granule.getRelPath(),archive.getName()));
		}
		return archiveList;
	}
	
	public static List<String> getArchivePathList(Integer granuleId) {
		return Query.getArchivePathList(fetchGranule(granuleId, "granuleArchiveSet"));
	}
	
	public static Integer getArchiveSizeByDataset(Integer datasetId) {
		gov.nasa.podaac.inventory.api.Query q = 
			QueryFactory.getInstance().createQuery();
		return q.sizeOfArchiveByDatasetId(datasetId);
	}
	
	public static Set<DatasetLocationPolicy> getDatasetLocationPolicy (String datasetName) {
		Dataset ds = fetchDataset(datasetName, "locationPolicySet");
		if (ds==null) return new HashSet<DatasetLocationPolicy>();
		return ds.getLocationPolicySet();
	}
	
	public static Map<Integer, ArchiveMetadata> getDatasetMetadata() {
		gov.nasa.podaac.inventory.api.Query q = QueryFactory.getInstance().createQuery();
		List<Dataset> datasetList = q.fetchDatasetList(
				new String[] {"datasetPolicy", "locationPolicySet" });
		Map<Integer, ArchiveMetadata> datasetMetadata = new TreeMap<Integer, ArchiveMetadata>();
		for (Dataset dataset : datasetList) {
			datasetMetadata.put(dataset.getDatasetId(), Query.getDatasetMetadata(dataset));
		}
		return datasetMetadata;	
	}
	
	public static ArchiveMetadata getDatasetMetadata(Dataset dataset) {
		DatasetPolicy policy = dataset.getDatasetPolicy();
		ArchiveMetadata metadata = new ArchiveMetadata();
		metadata.setDataClass(policy.getDataClass());
		metadata.setDataDuration(policy.getDataDuration());
		metadata.setRemoteBaseLocation(Query.getRemoteBaseLocation(dataset));
		metadata.setLocalBaseLocation(Query.getLocalBaseLocation(dataset));
		metadata.setDataset(dataset);
		return metadata;
	}
	
    public ArchiveMetadata getDatasetMetadata(Integer datasetId) { return Query.getDatasetMetadata_(datasetId); }
    private static ArchiveMetadata getDatasetMetadata_(Integer datasetId) {
		Dataset dataset = Query.fetchDataset_(datasetId, "datasetPolicy", "locationPolicySet");
		return getDatasetMetadata(dataset);
	}

	public Map<Integer, ArchiveMetadata> getDatasetMetadata(List<Integer> granuleIdList) { return Query.getDatasetMetadata_(granuleIdList); }	    	
	private static Map<Integer, ArchiveMetadata> getDatasetMetadata_(List<Integer> granuleIdList) {       
		Map<Integer, ArchiveMetadata> datasetMetadata = new TreeMap<Integer, ArchiveMetadata>();
		Map<Integer, ArchiveMetadata> datasetCache = new TreeMap<Integer,ArchiveMetadata>();
		gov.nasa.podaac.inventory.api.Query q = QueryFactory.getInstance().createQuery();
		for (Integer granuleId : granuleIdList) {
			Granule granule = q.fetchGranuleById(granuleId, "dataset");
			Integer datasetId = granule.getDataset().getDatasetId();
			ArchiveMetadata metadata = null;			
			if (datasetCache.containsKey(datasetId)) {
				metadata = datasetCache.get(datasetId);			
			} else {
				metadata = getDatasetMetadata_(datasetId);
				if (metadata != null) datasetCache.put(datasetId, metadata);
			}
			if (metadata != null) datasetMetadata.put(granuleId, metadata);
		}
		return datasetMetadata;	
	}

	public static Granule getGranule(Integer id) {
		gov.nasa.podaac.inventory.api.Query q = QueryFactory.getInstance().createQuery();
		Granule granule = q.findGranuleById(id);
		return granule;
	}
	
    public List<Integer> getGranuleIdList(Integer datasetId) { return Query.getGranuleIdList_(datasetId); }
    private static List<Integer> getGranuleIdList_(Integer datasetId) {
		gov.nasa.podaac.inventory.api.Query q = QueryFactory.getInstance().createQuery();
		return q.listGranuleIdByProperty("dataset.datasetId", datasetId);
	}
	
    public List<Integer> getGranuleIdList(Integer datasetId, Calendar start, Calendar stop) { return Query.getGranuleIdList_(datasetId, start.getTimeInMillis(), stop.getTimeInMillis()); }
    private static List<Integer> getGranuleIdList_(Integer datasetId, Long start, Long stop) {
		gov.nasa.podaac.inventory.api.Query q = QueryFactory.getInstance().createQuery();
		List<java.math.BigDecimal> granules_big = (List<java.math.BigDecimal>) q.getResultList("select granule_id from granule where dataset_id="+datasetId+" AND ARCHIVE_TIME_LONG > " +start+ " AND ARCHIVE_TIME_LONG < " +stop);
		List<Integer> granules = new ArrayList<Integer>();
		
		for(java.math.BigDecimal bd : granules_big){
			granules.add(bd.intValue());
		}

		return granules;
	}
	
	public ArchiveMetadata getGranuleMetadata(Integer granuleId) { return Query.getGranuleMetadata_(granuleId); }
	private static ArchiveMetadata getGranuleMetadata_(Integer granuleId) {
		Granule granule = Query.fetchGranule(granuleId, "granuleArchiveSet", "granuleReferenceSet");
		ArchiveMetadata metadata = new ArchiveMetadata();
		if (granule!=null) {
			metadata.setArchivePathList(Query.getArchivePathList(granule));
			metadata.setGranule(granule);
		}
		return metadata;
	}
	
	public Integer getGranuleSizeByDataset(Integer datasetId) { return Query.getGranuleSizeByDataset_(datasetId); }
	private static Integer getGranuleSizeByDataset_(Integer datasetId) {
		gov.nasa.podaac.inventory.api.Query q = 
			QueryFactory.getInstance().createQuery();
		return q.sizeOfGranuleByDatasetId(datasetId);
	}
	
	public Integer getGranuleVersion(Integer id) {
		log.debug("getGranuleVersion: "+id);
		Granule granule = getGranule(id);
		if (granule==null) return null;
		return granule.getVersion();
	}

	public static Date getIngestTime (Integer granuleId) {
		Granule granule = Query.getGranule(granuleId);
		if (granule == null) return null;
		return granule.getIngestTime();
	}

	public static Map<String, String> getLocalBaseLocation(Dataset dataset) {
		// Map<type, baselocation>
		// get local base location Policy (ARCHIVE+ and not REMOTE+)
		if (dataset==null) return null;
		Map<String, String> localBaseLocation = new TreeMap<String, String>();
		Set<DatasetLocationPolicy> locationPolicySet = dataset.getLocationPolicySet();			
		for (DatasetLocationPolicy locationPolicy : locationPolicySet) {
			String policyType = locationPolicy.getType();
			//log.debug("getLocalLocationPolicy:"+datasetId+":"+policyType);
			// non-Archive location such as FTP/OPENDAP
			if (!policyType.startsWith(LocationPolicyType.ARCHIVE.toString()) &&
					policyType.startsWith(LocationPolicyType.LOCAL.toString())) {
				localBaseLocation.put(policyType, locationPolicy.getBasePath());
			}
			// ARCHIVE-{AccessType} location
			else if (policyType.equals(
					Query.getArchiveLocationType(dataset.getDatasetPolicy().getAccessType()).toString()
					)) {
				localBaseLocation.put(LocationPolicyType.ARCHIVE.toString(), locationPolicy.getBasePath());
			}
		}
		//log.debug(localBaseLocation);
		return localBaseLocation;
	}

	public static Map<String, String> getLocalBaseLocation(Integer datasetId) {
		return(getLocalBaseLocation(
				Query.fetchDataset_(datasetId, "datasetPolicy", "locationPolicySet")));
	}
	
	public String getOnlineDataFilePath(Granule granule) { return Query.getOnlineDataFilePath_(granule); }
	static String getOnlineDataFilePath_(Granule granule) {
		String dataFilename = null;
		Set<GranuleArchive> granuleArchiveSet = granule.getGranuleArchiveSet();
		for (GranuleArchive archive : granuleArchiveSet) {
			if (archive.getType().equals(GranuleArchiveType.DATA.toString()) &&
					archive.getStatus().equals(GranuleArchiveStatus.ONLINE.toString())) 
				dataFilename = StringUtility.cleanPaths(granule.getRootPath(), granule.getRelPath(),archive.getName());
		}
		return dataFilename;
	}
	
	public List<AIP> getReferenceAIPByDataset (Integer datasetId) { return Query.getReferenceAIPByDataset_(datasetId); }
	private static List<AIP> getReferenceAIPByDataset_ (Integer datasetId) {
		gov.nasa.podaac.inventory.api.Query q = QueryFactory.getInstance().createQuery();
		try {
			long time = System.currentTimeMillis();
			List<GranuleReference> refList = q.fetchReferenceByDatasetId(datasetId);
			log.debug("Time="+(System.currentTimeMillis() - time));			
			log.debug("getReferenceAIPByDataset: sizeOfResult="+refList.size());
			List<AIP> metadataList = new ArrayList<AIP>();
			for (GranuleReference ref : refList) {
				metadataList.add(
						new AIP(ref.getGranuleId(), new URI(ref.getPath()),
						ref.getType(), ref.getStatus())
				);			
			}			
			return metadataList;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("getReferenceAIPByDataset: error getting archive info "
					+e.getMessage());
			return new ArrayList<AIP>();
		}
	}
	
	public static List<AIP> getReferenceAIPByGranule(Integer... ids) {
		List<Integer> idList = new ArrayList<Integer>();
		for (Integer id : ids) idList.add(id);
		return getReferenceAIPByGranule_(idList);
	}
	
	public List<AIP> getReferenceAIPByGranule(List<Integer> idList) { return Query.getReferenceAIPByGranule_(idList); }
	private static List<AIP> getReferenceAIPByGranule_(List<Integer> idList) {
		gov.nasa.podaac.inventory.api.Query q = QueryFactory.getInstance().createQuery();
		try {
			List<AIP> metadataList = new ArrayList<AIP>();
			for (Integer granuleId : idList) {
				Granule granule = q.fetchGranuleById(granuleId, "granuleReferenceSet");
				if (granule==null) {
					log.warn("GranuleId="+granuleId+" not found!");
					continue;
				}
				Set<GranuleReference> refSet = granule.getGranuleReferenceSet();
				for (GranuleReference ref : refSet) {
					metadataList.add(
							new AIP(granuleId, new URI(ref.getPath()),
							ref.getType(), ref.getStatus())
					);		
				}
			}
			log.debug("getReferenceAIPByGranule: sizeOfResult="+metadataList.size());
			return metadataList;
		} catch (Exception e) {
			log.error("getReferenceAIPByGranule: error getting reference info "
						+e.getMessage());
			return new ArrayList<AIP>();
		}
	}

	public static List<String> getReferenceList(Integer id) {
		Set<GranuleReference> refSet = 
			fetchGranule(id,"granuleReferenceSet").getGranuleReferenceSet();
		List<String> refList = new ArrayList<String>();
		for (GranuleReference ref : refSet) {
			refList.add(ref.getPath());
		}
		return refList;
	}
	
	public Set<GranuleReference> getReferenceSet(Integer id) { return Query.getReferenceSet_(id); }
	private static Set<GranuleReference> getReferenceSet_(Integer id) {
		Set<GranuleReference> refSet = 
			fetchGranule(id,"granuleReferenceSet").getGranuleReferenceSet();
		return refSet;
	}
	
	public static List<String> getReferencePathList(Granule granule) {
		Set<GranuleReference> refSet = granule.getGranuleReferenceSet();
		List<String> refList = new ArrayList<String>();
		for (GranuleReference ref : refSet) {
			refList.add(ref.getPath());
		}
		return refList;
	}
	
	public static List<String> getReferencePathList(Integer granuleId) {
		return Query.getReferencePathList(fetchGranule(granuleId, "granuleReferenceSet"));
	}
	
	public static Map<String, String> getRemoteBaseLocation(Dataset dataset) {
		// Map<type, baselocation>
		// get remote base location Policy REMOTE+

		Map<String, String> remoteBaseLocation = new TreeMap<String, String>();
		Set<DatasetLocationPolicy> locationPolicySet = dataset.getLocationPolicySet();			
		for (DatasetLocationPolicy locationPolicy : locationPolicySet) {
			String policyType = locationPolicy.getType();
			//log.debug("getLocalLocationPolicy:"+datasetId+":"+policyType);
			// REMOTE location such as FTP/OPENDAP
			if (policyType.startsWith(LocationPolicyType.REMOTE.toString())) 
				remoteBaseLocation.put(policyType, locationPolicy.getBasePath());
		}
		return remoteBaseLocation;
	}
	
	public Map<Integer, ArchiveMetadata> getRollingStore() { return Query.getRollingStore_(); }
	private static Map<Integer, ArchiveMetadata> getRollingStore_() {
		Map<Integer, ArchiveMetadata> datasetMetadata = Query.getDatasetMetadata();
		Set<Integer> datasetIdList = datasetMetadata.keySet();
		Map<Integer, ArchiveMetadata> rollingStore = new TreeMap<Integer, ArchiveMetadata>();
		log.debug("getRolloingStore():"+datasetIdList.size()+" datasets.");
		for (Integer datasetId : datasetIdList) {
			ArchiveMetadata metadata = datasetMetadata.get(datasetId);
			if (metadata.getDataClass().equals("ROLLING-STORE")) {
				rollingStore.put(datasetId, metadata);
			}
		}
		log.debug("Number of ROLLING-STORE dataset="+rollingStore.size());
		return rollingStore;	
	}
	
	public static ArchiveMetadata getRollingStore(Dataset dataset) {
		DatasetPolicy policy = dataset.getDatasetPolicy();
		if (policy.getDataClass().equals("ROLLING-STORE")) return getDatasetMetadata(dataset);
		return null;
	}

    public ArchiveMetadata getRollingStore(Integer datasetId) { return Query.getRollingStore_(datasetId); }
    private static ArchiveMetadata getRollingStore_(Integer datasetId) {
		Dataset dataset = Query.fetchDataset_(datasetId, "datasetPolicy", "locationPolicySet");
		return getRollingStore(dataset);
	}
	
	public Map<Integer, ArchiveMetadata> getRollingStore(List<Integer> granuleIdList) { return Query.getRollingStore_(granuleIdList); }
	private static Map<Integer, ArchiveMetadata> getRollingStore_(List<Integer> granuleIdList) {      
		Map<Integer, ArchiveMetadata> datasetMetadata = new TreeMap<Integer, ArchiveMetadata>();
		Map<Integer, ArchiveMetadata> datasetCache = new TreeMap<Integer,ArchiveMetadata>();
		gov.nasa.podaac.inventory.api.Query q = QueryFactory.getInstance().createQuery();
		for (Integer granuleId : granuleIdList) {
			Granule granule = q.fetchGranuleById(granuleId, "dataset");
			Integer datasetId = granule.getDataset().getDatasetId();
			ArchiveMetadata metadata = null;			
			if (datasetCache.containsKey(datasetId)) {
				metadata = datasetCache.get(datasetId);			
			} else {
				metadata = getRollingStore_(datasetId);
				if (metadata != null) datasetCache.put(datasetId, metadata);
			}
			if (metadata!=null) datasetMetadata.put(granuleId, metadata);
		}
		return datasetMetadata;	
	}
	
	public List<Integer> fetchGranulesByDatasetAndPatter(Integer datasetId, String gnp){
		
		List<Integer> ll = new ArrayList<Integer>();
		gov.nasa.podaac.inventory.api.Query q = QueryFactory.getInstance().createQuery();
		log.debug("findGranuleByName: name="+gnp);
		List<Granule> result = q.listGranuleByProperty("name", gnp);
	    for(Granule g : result){
	    	if(g.getDataset().getDatasetId().equals(datasetId)){
	    		ll.add(g.getGranuleId());
	    	}
	    }
		return ll;
	}

	
}
