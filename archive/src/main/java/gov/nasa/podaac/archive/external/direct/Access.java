//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.podaac.archive.external.direct;

import gov.nasa.podaac.archive.core.AIP;
import gov.nasa.podaac.archive.core.ArchiveData;
import gov.nasa.podaac.archive.external.InventoryAccess;
import gov.nasa.podaac.inventory.api.Constant;
import gov.nasa.podaac.inventory.api.Constant.GranuleArchiveStatus;
import gov.nasa.podaac.inventory.api.Constant.GranuleArchiveType;
import gov.nasa.podaac.inventory.api.Constant.GranuleStatus;
import gov.nasa.podaac.inventory.api.Constant.LocationPolicyType;
import gov.nasa.podaac.inventory.api.DataManager;
import gov.nasa.podaac.inventory.api.DataManagerFactory;
import gov.nasa.podaac.inventory.hibernate.HibernateSessionFactory;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.DatasetElement;
import gov.nasa.podaac.inventory.model.DatasetLocationPolicy;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.GranuleReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

/**
 * This class contains methods to access database.
 *
 * @author clwong
 * @version
 * $Id: InventoryAccess.java 5507 2010-08-05 21:45:24Z gangl $
 */
class Access implements InventoryAccess
{	
	private static Log log = LogFactory.getLog(Access.class);
	private static final int INCLAUSE_LIMIT = 1000;
	
	public static void delete (Granule granule) {
		DataManager manager = DataManagerFactory.getInstance().createDataManager();
		Session session = HibernateSessionFactory.getInstance().getCurrentSession();
		session.beginTransaction();
		if (session.createSQLQuery(
				"DELETE FROM granule_real WHERE "
				+" granule_id="+granule.getGranuleId()
				)
				.executeUpdate()==0)
			log.debug("granuleId="+granule.getGranuleId()+"-->real not deleted");
		session.getTransaction().commit(); // commit also closes session
		
		manager.deleteGranule(granule);
	}
	
	public void updateGranuleInfo(Granule g){
		DataManager manager = DataManagerFactory.getInstance().createDataManager();
		manager.updateGranule(g);
	}

	public static void delete (Integer granuleId) {
		delete(Query.getGranule(granuleId));
	}


    public void deleteGranule(Granule granule, boolean dataOnly) { Access.deleteGranule_(granule, dataOnly); }
    private static void deleteGranule_(Granule granule, boolean dataOnly) {
		
    	
    	if (dataOnly) {
			// if request not to delete metadata
			log.debug("delete archived data-only;");
			setGranuleArchiveStatus(granule, GranuleArchiveStatus.DELETED.toString());
			removeLocalReference(granule);
			Set<GranuleReference> refSet = granule.getGranuleReferenceSet();
			if (refSet==null || refSet.isEmpty()) {
				granule.setStatus(GranuleStatus.OFFLINE);
			}
			update(granule);
		} else {
			// Delete Inventory metadata
			if(granule != null){
				if(granule.getGranuleRealSet() != null){
					granule.getGranuleRealSet().clear();
				}
			}
			delete(granule);
			log.debug("Metadata: deleted.");
		}
	}
	
	public static List<String> deleteGranule(Integer granuleId, boolean dataOnly) {
		Granule granule = Query.fetchGranule(granuleId, "granuleArchiveSet", "granuleReferenceSet");
		deleteGranule_(granule, dataOnly);
		return Query.getArchivePathList(granule);
	}
	
	public static void deleteLocalReference (Granule granule) {
		removeLocalReference(granule);
		update(granule);
	}
	
	public static void deleteRemoteReference (Granule granule) {
		removeRemoteReference(granule);
		update(granule);
	}
	
	public static void deleteLocalReference (Integer granuleId) {
		deleteReference(granuleId, LocationPolicyType.LOCAL.toString());
	}

	public static void deleteReference (Integer granuleId, String type) {
		Session session = HibernateSessionFactory.getInstance().getCurrentSession();
		session.beginTransaction();
		if (session.createSQLQuery(
				"DELETE FROM granule_reference WHERE "
				+" granule_id="+granuleId
				+" AND (type LIKE '" + type +"%')"
				)
				.executeUpdate()==0)
			log.debug("granuleId="+granuleId+"-->reference not deleted");
		session.getTransaction().commit(); // commit also closes session
	}

	public static void deleteRemoteReference (Integer granuleId) {
		deleteReference(granuleId, LocationPolicyType.REMOTE.toString());
	}
	
	public static List<Integer> getGranuleIdList(List<AIP> aipList) {
		Iterator<AIP> it = aipList.iterator();
		List<Integer> granuleIdList = new ArrayList<Integer>();
		while (it.hasNext()) {
			Integer id = it.next().getGranuleId();
			if (!granuleIdList.contains(id)) granuleIdList.add(id);
		}
		return granuleIdList;
	}
	
	public void remoteGranule (Granule granule) { Access.remoteGranule_(granule); }
	private static void remoteGranule_ (Granule granule) {
		if (granule==null) return;
		removeLocalReference(granule);
		setGranuleArchiveStatus(granule, GranuleArchiveStatus.DELETED.toString());
		update(granule);
	}
	
	public static void removeLocalReference(Granule granule) {
		removeReference(granule, LocationPolicyType.LOCAL.toString());
	}
	
	public static void removeReference(Granule granule, String type) {
		Set<GranuleReference> newSet = new HashSet<GranuleReference>(
				granule.getGranuleReferenceSet()
				);
		Set<GranuleReference> currentSet = granule.getGranuleReferenceSet();	
		for (GranuleReference ref : currentSet) {
			if (ref.getType().startsWith(type)) newSet.remove(ref);
		}
		granule.setGranuleReferenceSet(newSet);
	}
	
	public static void removeRemoteReference(Granule granule) {
		removeReference(granule, LocationPolicyType.REMOTE.toString());
	}
	
	public static void setGranuleArchiveStatus (Granule granule, String status) {
		/*Set<GranuleArchive> archiveSet = granule.getGranuleArchiveSet();
		for (GranuleArchive archive : archiveSet) {
			archive.setStatus(status);
		}*/
		updateGranuleArchiveStatus_(granule.getGranuleId(), "DELETED");
	}
	
	public void setGranuleReference(Granule granule, 
											Map<String, String> baseLocationPolicy, 
											String subpath) { Access.setGranuleReference_(granule, baseLocationPolicy, subpath); }
	private static void setGranuleReference_(Granule granule, 
                Map<String, String> baseLocationPolicy, 
                String subpath) {
		Set<GranuleReference> newSet = new HashSet<GranuleReference>(
				granule.getGranuleReferenceSet()
				);
		Set<String> baseLocationTypes = baseLocationPolicy.keySet();
		for (String baseLocationType : baseLocationTypes) {
			GranuleReference ref = new GranuleReference();
			ref.setType(baseLocationType);
			
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
				}
			}
			if(!found)
				newSet.add(ref);
		}
		log.debug("Setting granule references with following number of remote references: " + newSet.size());
		for(GranuleReference r : newSet)
		{
			log.debug("Reference path to add: " +r.getPath());
		}
		granule.setGranuleReferenceSet(newSet);
	}

	public static void update(Granule granule) {
		DataManager manager = DataManagerFactory.getInstance().createDataManager();
		manager.addGranule(granule);
	}
	
    public void updateAIPArchiveStatus (List<AIP> aipList) { Access.updateAIPArchiveStatus_(aipList); }
    private static void updateAIPArchiveStatus_ (List<AIP> aipList) {
		log.debug("updateAIPArchiveStatus: aipsize="+aipList.size());
		Session session = HibernateSessionFactory.getInstance().getCurrentSession();
		session.beginTransaction();
		for (AIP aip : aipList) {
			if (session.createSQLQuery(
					"update granule_archive set status='"+ aip.getStatus()+"'"
					+" where "
					+" granule_id="+aip.getGranuleId()
					+" and name LIKE '"+aip.getDestination().toString().substring(aip.getDestination().toString().lastIndexOf(File.separator)+1)+"'"
					)
					.executeUpdate()==0)
				log.debug("granuleId="+aip.getGranuleId()+"-->archive status not updated");
			if (aip.getType().equals(GranuleArchiveType.DATA.toString())) {
				if (session.createSQLQuery(
					"update granule_reference set status='"+ aip.getStatus()+"'"
					+" where "
					+" granule_id="+aip.getGranuleId()
					+" and path LIKE '%"+
					ArchiveData.getFilename(aip.getDestination().toString())+"%'")
					.executeUpdate()==0)
				log.debug("granuleId="+aip.getGranuleId()+"-->reference status not updated");
			}
		}	
	    session.getTransaction().commit(); // commit also closes session
	}	
	
	public void updateAIPGranuleArchiveStatus (List<AIP> aipList) { Access.updateAIPGranuleArchiveStatus_(aipList); }
	private static void updateAIPGranuleArchiveStatus_ (List<AIP> aipList) {
		log.debug("updateAIPGranuleArchiveStatus: aipsize="+aipList.size());
		// Use direct native sql instead;
		Session session = HibernateSessionFactory.getInstance().getCurrentSession();
		session.beginTransaction();
		for (AIP aip : aipList) {
			if (session.createSQLQuery(
					"UPDATE granule_archive SET status='"+ aip.getStatus()+"'"
					+" WHERE "
					+" granule_id="+aip.getGranuleId()
					+" AND name LIKE '"+aip.getDestination().toString().substring(aip.getDestination().toString().lastIndexOf(File.separator)+1)+"'"
					)
					.executeUpdate()==0)
				log.debug("granuleId="+aip.getGranuleId()+"-->archive status not updated");
			if (session.createSQLQuery(
					"UPDATE granule SET verify_time_long=" + new Date().getTime() + ""
					+" WHERE "
					+" granule_id = "+aip.getGranuleId())
					.executeUpdate()==0)
				log.debug("granuleId="+aip.getGranuleId()+"-->granule verify time not updated");
		
		}	
	    session.getTransaction().commit(); // commit also closes session
	}
	
	public void updateAIPGranuleReferenceStatus (List<AIP> aipList) { Access.updateAIPGranuleReferenceStatus_(aipList); }
	private static void updateAIPGranuleReferenceStatus_ (List<AIP> aipList) {
		log.debug("updateAIPGranuleReferenceStatus: aipsize="+aipList.size());
		// Use direct native sql instead;
		Session session = HibernateSessionFactory.getInstance().getCurrentSession();
		session.beginTransaction();
		for (AIP aip : aipList) {
			if (aip.getType().equals(GranuleArchiveType.DATA.toString())) {
				if (session.createSQLQuery(
					"UPDATE granule_reference SET status='"+ aip.getStatus()+"'"
					+" WHERE "
					+" granule_id="+aip.getGranuleId()
					+" AND path LIKE '%"+
					ArchiveData.getFilename(aip.getDestination().toString())+"%'"
					)
					.executeUpdate()==0)
				log.debug("granuleId="+aip.getGranuleId()+"-->reference status not updated");
			}
		}	
	    session.getTransaction().commit(); // commit also closes session
	}
	
	public static void updateDatasetLocationPolicy(Integer datasetId, String basePath) {
		if (basePath==null) {
			log.debug("updateDatasetLocationPolicy: basePath null!");
			return;
		}
		Dataset dataset = Query.fetchDataset_(datasetId, "locationPolicySet");
		if (dataset==null) {
			log.warn("updateDatasetLocationPolicy: datasetId="+datasetId+" not found!");
			return;
		} else 	log.debug("updateDatasetLocationPolicy:"+datasetId);

		Set<DatasetLocationPolicy> locationPolicySet = dataset.getLocationPolicySet();
		for (DatasetLocationPolicy locationPolicy : locationPolicySet) {
			if (locationPolicy.getType().startsWith(LocationPolicyType.ARCHIVE.toString())) {
				//log.debug("set basepath for "+locationPolicy.getType()+" to "+basePath);
				locationPolicy.setBasePath(basePath); 
			}
		}
		DataManager manager = DataManagerFactory.getInstance().createDataManager();
		manager.addDataset(dataset);
	}

	public static void updateGranuleArchiveStatus (Granule granule, String status) {
		log.debug("updateGranuleArchiveStatus :");
		setGranuleArchiveStatus(granule, status);
		update(granule);
	}
	
	public void updateGranuleArchiveStatus (Integer granuleId, String status) { Access.updateGranuleArchiveStatus_(granuleId, status); }
	private static void updateGranuleArchiveStatus_ (Integer granuleId, String status) {
		log.debug("updateGranuleArchiveStatus "+granuleId);
		Session session = HibernateSessionFactory.getInstance().getCurrentSession();
		session.beginTransaction();
		if (session.createSQLQuery(
				"UPDATE granule_archive SET status='"+status+"'"
				+" WHERE "
				+" granule_id="+granuleId
				)
				.executeUpdate()==0)
			log.debug("granuleId="+granuleId+"-->archive status not updated");
		session.getTransaction().commit(); // commit also closes session
	}
	
	public void updateGranuleArchiveChecksum (Integer granuleId,String name, String Sum) { Access.updateGranuleArchiveChecksum_(granuleId, name, Sum); }
	private static void updateGranuleArchiveChecksum_ (Integer granuleId,String name, String Sum) {
		log.debug("updateGranuleArchiveChecksum "+granuleId);
		Session session = HibernateSessionFactory.getInstance().getCurrentSession();
		session.beginTransaction();
		if (session.createSQLQuery(
				"UPDATE granule_archive SET checksum='"+Sum+"'"
				+" WHERE "
				+" granule_id="+granuleId
				+" AND "
				+" name='"+name+"'"
				)
				.executeUpdate()==0)
			log.debug("granuleId="+granuleId+"-->archive status not updated");
		session.getTransaction().commit(); // commit also closes session
	}
	
	public void updateGranuleArchiveSize (Integer granuleId,String name, Long Size) { Access.updateGranuleArchiveSize_(granuleId, name, Size); }
	private static void updateGranuleArchiveSize_ (Integer granuleId,String name, Long Size) {
		log.debug("updateGranuleArchiveSize "+granuleId);
		Session session = HibernateSessionFactory.getInstance().getCurrentSession();
		session.beginTransaction();
		if (session.createSQLQuery(
				"UPDATE granule_archive SET FILE_SIZE="+Size+""
				+" WHERE "
				+" granule_id="+granuleId
				+" AND "
				+" name='"+name+"'"
				)
				.executeUpdate()==0)
			log.debug("granuleId="+granuleId+"-->archive status not updated");
		session.getTransaction().commit(); // commit also closes session
	}
	
    public void updateGranuleLocation (Granule granule, String archiveBaseLocation, String baseLocation) { Access.updateGranuleLocation_(granule, archiveBaseLocation, baseLocation); }
	private static void updateGranuleLocation_ (Granule granule, String archiveBaseLocation, String baseLocation) {
		log.debug("updateGranuleLocation:");
		String basepath = ArchiveData.absolutePath(baseLocation);

		//Only update the granule root_path
		if(!basepath.startsWith("file://"))
			basepath = "file://" + basepath;
		granule.setRootPath(basepath);
		removeLocalReference(granule);
		update(granule);
	}

	public static List<String> updateGranuleLocation (Integer granuleId, String archiveBaseLocation, String baseLocation) {
		log.debug("updateGranuleLocation:");
		Granule granule = Query.fetchGranule(granuleId, "granuleArchiveSet", "granuleReferenceSet");
		updateGranuleLocation_(granule, archiveBaseLocation, baseLocation);		
		return Query.getArchivePathList(granule);
	}
	
	public static void updateGranuleStatus(Granule granule, String status) {

		
		granule.setStatus(Constant.GranuleStatus.ValueOf(status));
		update(granule);
	}

	public static void updateGranuleStatus(Integer id, String status) {
		Session session = HibernateSessionFactory.getInstance().getCurrentSession();
		session.beginTransaction();
		if (session.createSQLQuery(
				"UPDATE granule SET status='"
				+status+"'"
				+" WHERE "
				+" granule_id="+id)
				.executeUpdate()==0)
			log.debug("granule status not updated");
		session.getTransaction().commit(); // commit also closes session	
	}

	public boolean updateLocalReference(Granule granule, Map<String, String> localBaseLocationPolicy) { return Access.updateLocalReference_(granule, localBaseLocationPolicy); }
	private static boolean updateLocalReference_(Granule granule, Map<String, String> localBaseLocationPolicy) {
		log.debug("updateLocalReference:");
		if (granule==null) return false;
		if (localBaseLocationPolicy==null) return false;
		String dataFilename = Query.getOnlineDataFilePath_(granule);
		if (dataFilename != null) {
			String archiveBaseLocation =
				localBaseLocationPolicy.remove(LocationPolicyType.ARCHIVE.toString());
			if (archiveBaseLocation==null) {
				archiveBaseLocation =  ArchiveData.getBaseLocation(dataFilename);
			}
			log.debug("ArchiveBaseLocation: " + archiveBaseLocation);
			String subpath = dataFilename.replaceFirst(archiveBaseLocation, "");
			log.debug("dataFileName: ");
			log.debug("Subpath: " + subpath);
			if(dataFilename.equals(subpath)){
				subpath = granule.getRelPath() + dataFilename.substring(dataFilename.lastIndexOf("/"));
				log.debug("Fixed Subpath: " + subpath);
			}
			
			removeLocalReference(granule);
			setGranuleReference_(granule, localBaseLocationPolicy, subpath);
			update(granule);
			return true;
		} else {
			log.debug("GranuleId="+granule.getGranuleId()+": no ONLINE-DATA for references creation!");
			return false;
		}
	}

    public void updateVerifyGranuleStatus (List<Integer> granuleIdList, String status) { Access.updateVerifyGranuleStatus_(granuleIdList, status); }
   
    
    private static void updateVerifyGranuleStatus_ (List<Integer> granuleIdList, String status) {
		log.debug("updateVerifyGranuleStatus: granuleIdList.size="+granuleIdList.size());
		Session session = HibernateSessionFactory.getInstance().getCurrentSession();
		session.beginTransaction();
		int size = granuleIdList.size();
		//String verifyTime = formatter.format(new Date());
		for (int i=0; i<size; i+=INCLAUSE_LIMIT) {
			String inClause = "("+granuleIdList.get(i);
			int nextSize = Math.min(i+INCLAUSE_LIMIT, size);
		    for (int j=i+1; j<nextSize; j++) inClause += ", " + granuleIdList.get(j);
		    inClause += ")";
			if (session.createSQLQuery(
					"UPDATE granule SET status='" + status + "'"
					+", verify_time_long=" + new Date().getTime() + ""
					+" WHERE "
					+" granule_id IN "+inClause)
					.executeUpdate()==0)
				log.debug("granule status not updated");
			else log.debug("granule status set "+status);
		}	
	    session.getTransaction().commit(); // commit also closes session		
	}

    
	public void reassociateGranuleElement(Integer gId, Integer from, Integer to, String type){
		log.debug("Updating granule reference ["+gId+"]");
		Session session = HibernateSessionFactory.getInstance().getCurrentSession();
		session.beginTransaction();
		if(session.createSQLQuery(
					"UPDATE granule_"+type+" SET de_id=" + to + ""
					+" WHERE "
					+" granule_id="+gId
					+" AND de_Id="+from+"")
					.executeUpdate()==0){
			log.debug("No Elements Updated.");
		}
		else
			log.debug("Elements Updated.");
	    session.getTransaction().commit(); // commit also closes session		
	}
    
	@Override
	public void updateGranuleReferencePath(Integer granuleId, String path,
			String newRef) {

		log.debug("Updating granule reference ["+granuleId+"]");
		Session session = HibernateSessionFactory.getInstance().getCurrentSession();
		session.beginTransaction();
		if(session.createSQLQuery(
					"UPDATE granule_reference SET path='" + newRef + "'"
					+" WHERE "
					+" granule_id="+granuleId
					+" AND path='"+path+"'")
					.executeUpdate()==0){
			log.debug("No references Updated.");
		}
		else
			log.debug("References Updated.");
	    session.getTransaction().commit(); // commit also closes session		
	}

	@Override
	public void reElement(Granule g, Dataset toD, Dataset fromD) {
		Map<Integer,String> deIDmapping = new HashMap<Integer,String>();
		for(DatasetElement toDE : toD.getDatasetElementSet()){
			
			for(DatasetElement fromDE : fromD.getDatasetElementSet()){
				if(toDE.getElementDD().equals(fromDE.getElementDD()))
					deIDmapping.put(fromDE.getDeId(), fromDE.getElementDD().getType()+","+toDE.getDeId());
			}	
		}
		log.debug("DEID Mappings");
		for(Entry<Integer,String> me : deIDmapping.entrySet()){
			String[] ary = me.getValue().split(","); 
			String type = ary[0].trim();
			Integer deId = Integer.valueOf(ary[1].trim());
			if(type.equals("time"))
				type = "DATETIME";
			
			log.debug("map "+ me.getKey() + " to " + deId + "["+type+"]");
			this.reassociateGranuleElement(g.getGranuleId(),me.getKey(),deId,type);
		}
		
	}
}
