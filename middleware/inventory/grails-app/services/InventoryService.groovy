import gov.nasa.podaac.inventory.dao.EntityManager.ElementDDDAO;
import gov.nasa.podaac.inventory.model.ElementDD;

import gov.nasa.podaac.inventory.dao.EntityManager.CollectionProductDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.GranuleMetaHistoryDAO;
import gov.nasa.podaac.inventory.model.CollectionProduct;

import java.util.ArrayList;
import java.util.List;

import com.sun.org.apache.xalan.internal.xsltc.compiler.Import;


import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import gov.nasa.podaac.inventory.hibernate.HibernateSessionFactory;
import gov.nasa.podaac.inventory.model.CollectionDataset;
import gov.nasa.podaac.inventory.model.Dataset
import gov.nasa.podaac.inventory.model.Contact
import gov.nasa.podaac.inventory.model.Collection
import gov.nasa.podaac.inventory.model.DatasetCoverage
import gov.nasa.podaac.inventory.model.DatasetElement;
import gov.nasa.podaac.inventory.model.DatasetPolicy
import gov.nasa.podaac.inventory.model.Granule
import gov.nasa.podaac.inventory.model.GranuleArchive;
import gov.nasa.podaac.inventory.model.GranuleMetaHistory;
import gov.nasa.podaac.inventory.model.Provider
import gov.nasa.podaac.inventory.dao.DAOFactory;
import gov.nasa.podaac.inventory.api.DataManager;
import gov.nasa.podaac.inventory.api.DataManagerFactory;
import gov.nasa.podaac.inventory.api.Query;
import gov.nasa.podaac.inventory.api.Inventory;
import gov.nasa.podaac.inventory.api.InventoryFactory;
import gov.nasa.podaac.inventory.api.QueryFactory;
import gov.nasa.podaac.inventory.api.Constant.GranuleArchiveType;
import gov.nasa.podaac.inventory.exceptions.InventoryException;

import gov.nasa.podaac.common.api.metadatamanifest.MetadataManifest;
import gov.nasa.podaac.common.api.metadatamanifest.MetadataField;
import gov.nasa.podaac.common.api.util.StringUtility;

import org.hibernate.Session;

//import gov.nasa.podaac.inventory.hibernate.HibernateSessionFactory;
//import gov.nasa.podaac.inventory.model.Dataset
//import gov.nasa.podaac.inventory.model.DatasetCoverage
//import gov.nasa.podaac.inventory.model.DatasetPolicy
//import gov.nasa.podaac.inventory.model.Granule
//import gov.nasa.podaac.inventory.model.Provider
//import gov.nasa.podaac.inventory.api.Query;
//import gov.nasa.podaac.inventory.api.Inventory;
//import gov.nasa.podaac.inventory.api.InventoryFactory;
//import gov.nasa.podaac.inventory.api.QueryFactory;
//import gov.nasa.podaac.inventory.exceptions.InventoryException;
//import gov.nasa.podaac.common.api.metadatamanifest.MetadataManifest;
//import gov.nasa.podaac.common.api.metadatamanifest.MetadataField;
//import org.hibernate.Session;

        
class InventoryService {

	private static final int INCLAUSE_LIMIT = 1000;
	private DAOFactory daoFactory = DAOFactory.instance(DAOFactory.HIBERNATE);
	
	def sessionFactory
//    static transactional = true
	static transactional = false
	//xml = serviceProfile
	def ingestSip = { xml ->
		HibernateSessionFactory.sessionFactory = sessionFactory
		Inventory inventory = InventoryFactory.instance.createInventory()
      		log.debug("Store ServiceProfile")
      		inventory.storeServiceProfile(xml)
		log.debug("finished storing profile")
		return xml
	}

	
	//@Transactional(noRollbackFor=InventoryException.class)
	public MetadataManifest processManifest(manifest, user) throws InventoryException{ 
		HibernateSessionFactory.sessionFactory = sessionFactory
		try{
			Inventory inventory = InventoryFactory.instance.createInventory()
			  log.debug("Process Manifest")
	
			  MetadataManifest retMani = inventory.processManifest(manifest, user)
		log.debug("finished storing profile")
		return retMani
		}catch(InventoryException e){
			log.debug( "AHHHHHHHHH")
			throw new InventoryException(e.getMessage());
		}
         } 
	
	def fetchContactById = { cId ->
		log.debug("Fetch contact by contactID $cId")
		HibernateSessionFactory.sessionFactory = sessionFactory
		Query query = QueryFactory.getInstance().createQuery()
		String[] depends = {"provider"};	
		Contact c = query.fetchContactById(cId, depends);
		return c;
		
	}
	
	def fetchCollectionById = { collectionId ->
		log.debug("Fetch collection by collectionID $collectionId")
		HibernateSessionFactory.sessionFactory = sessionFactory
		Query query = QueryFactory.getInstance().createQuery()
		Collection c = query.fetchCollectionById(collectionId);
		return c;
	}
	
	def fetchCollectionByShortName = { collectionId ->
		log.debug("Fetch collection by collectionShortName $collectionId")
		
		Query query = QueryFactory.getInstance().createQuery()
		gov.nasa.podaac.inventory.model.Collection c = query.fetchCollectionByShortName(collectionId);
		return c;
	}
	
	def fetchCollectionList = {
		log.debug("Fetch collection list")
		HibernateSessionFactory.sessionFactory = sessionFactory
		Query query = QueryFactory.getInstance().createQuery()
		def c = query.listCollection();
		return c;
	}
	
	def updateCollectionProduct= { collection ->
		log.debug("Entering updateCollection.");
		HibernateSessionFactory.sessionFactory = sessionFactory
		CollectionProductDAO dao = daoFactory.getCollectionProductDAO();
		dao.update(collection);
		return;
	}
	
	def updateGranuleEchoTime = { gmh ->
		log.debug("Entering updateGranuleEchoTime.");
		HibernateSessionFactory.sessionFactory = sessionFactory
		GranuleMetaHistoryDAO dao = daoFactory.getGranuleMetaHistoryDAO();
		dao.update(gmh);
	}
	
	def listGranuleByDateRange= { start, stop, timeField, dsId ->
		String sql= "select * from GRANULE where "+timeField+" >= "+start+" AND "+timeField+" < "+stop+" ORDER BY "+timeField+" ASC";
		if(dsId != null)
			sql= "select * from GRANULE where "+timeField+" >= "+start+" AND "+timeField+" < "+stop+" AND dataset_id="+dsId+" ORDER BY "+timeField+" ASC";
		
		HibernateSessionFactory.sessionFactory = sessionFactory
		
				List<Granule> resultList = HibernateSessionFactory.getInstance().getCurrentSession().createSQLQuery(sql).addEntity(Granule.class).list();
//		for(Granule g : resultList){
//			g.getGranuleArchiveSet().size();
//			g.getMetaHistorySet().size();
//			g.getDataset().getSoftwareSet().size();
//			g.getDataset().getProvider().getShortName();
//			g.getDataset().getDatasetPolicy().getDataClass();
//			for(CollectionDataset c: g.getDataset().getCollectionDatasetSet()){
//				if(c.getCollectionDatasetPK().getCollection().getCollectionDatasetSet().size()<2)
//				{
//					c.getCollectionDatasetPK().getCollection().getCollectionProduct().getProductId();
//				}
//				
//			}
//		}
		return resultList;
	}
	
    def serviceMethod()  {

     }

		def findElementDDByShortName= {String shortName ->
			log.debug("findElementDDByShortName..."+shortName);
			//"shortName" is the field of elementDD we are searching.
			List<ElementDD> result = listElementDDByProperty("shortName", shortName);
			if (result.size() > 0) {
				return result.get(0);
			}
				return null;
		}
		
		def listElementDDByProperty(String propertyName, Object value) {
			log.debug("listElementDDByProperty: "+propertyName+"="+value);
			HibernateSessionFactory.sessionFactory = sessionFactory
			ElementDDDAO dao = daoFactory.getElementDDDAO();
			List<ElementDD> result = dao.findByPropertyName(propertyName, value);
			return result;
		}
	
		def findElementDDById = {id ->
			log.debug("findElementDDById: "+id);
			HibernateSessionFactory.sessionFactory = sessionFactory
			ElementDDDAO dao = daoFactory.getElementDDDAO();
			ElementDD result = dao.findById(id);
			result.getDatasetElementSet().size();
			return result;
		}
	
	
        def updateGranuleStatus = {gId, status ->
		log.debug("updateGranuleStatus, granule: $gId, status: $status")
     	        HibernateSessionFactory.sessionFactory = sessionFactory
                Query query = QueryFactory.getInstance().createQuery()
		query.updateGranuleStatusByID(gId,status)
		return
	}

		def deleteDataOnly = { Granule g ->
			//update refs, archives, gtable
			//don't need this-- unless we want to improve performance
			}
		
		def deleteDataAndMetadata = { Granule g ->
			HibernateSessionFactory.sessionFactory = sessionFactory
			Session session = HibernateSessionFactory.getInstance().getCurrentSession();
			DataManager manager = DataManagerFactory.getInstance().createDataManager();
			if (session.createSQLQuery(
					"DELETE FROM granule_real WHERE "
					+" granule_id="+g.getGranuleId()
					)
					.executeUpdate()==0)
				log.debug("granuleId="+g.getGranuleId()+"-->real not deleted");
			manager.deleteGranule(g);
			}
		
		def updateGranuleAIPArch = { status,dest,name,id,type ->
			log.debug("updating AIP Arch");
			HibernateSessionFactory.sessionFactory = sessionFactory
			Session session = HibernateSessionFactory.getInstance().getCurrentSession();
			session.beginTransaction();
			
			String qUery = "update granule_archive set status='$status' where granule_id=$id and name LIKE '$name'"; 
			
			log.debug("running: " + qUery);
						
				if (session.createSQLQuery(qUery)
						.executeUpdate()==0)
					log.debug("granuleId="+id+"-->archive status not updated");
					if (session.createSQLQuery(
						"UPDATE granule SET verify_time_long=" + new Date().getTime() + ""
						+" WHERE "
						+" granule_id = "+id)
						.executeUpdate()==0)
					log.debug("granuleId="+id+"-->granule verify time not updated");
				
			session.getTransaction().commit(); // commit also closes session
		}
		
		def updateGranuleAIPRef = { status,name,dest,id,type ->
			log.debug("updating AIP Ref");
			HibernateSessionFactory.sessionFactory = sessionFactory
			Session session = HibernateSessionFactory.getInstance().getCurrentSession();
			session.beginTransaction();
				//if (type.equals(GranuleArchiveType.DATA.toString())) {
					if (session.createSQLQuery(
						"update granule_reference set status='"+ status+"'"
						+" where "
						+" granule_id="+id
						+" and path LIKE '%"+name+"%'")
						.executeUpdate()==0)
					log.debug("granuleId="+id+"-->reference status not updated");
//				}else{
//					log.debug("Non Data Type.. skipping.");
//				}
			session.getTransaction().commit(); // commit also closes session
		}
		
		def updateGranuleArchiveSize = { Granule g, String name, Long fsize ->
			log.debug("updateGranuleArchiveSize "+g.getGranuleId());
			HibernateSessionFactory.sessionFactory = sessionFactory
			Session session = HibernateSessionFactory.getInstance().getCurrentSession();
			session.beginTransaction();
			if (session.createSQLQuery(
					"UPDATE granule_archive SET FILE_SIZE="+fsize+""
					+" WHERE "
					+" granule_id="+g.getGranuleId()
					+" AND "
					+" name='"+name+"'"
					)
					.executeUpdate()==0)
				log.debug("granuleId="+g.getGranuleId()+"-->archive size not updated");
			session.getTransaction().commit(); // commit also closes session
			}
		
		def updateGranuleArchiveStatus = { Granule g, String status ->
			log.debug("updateGranuleArchiveStatus "+g.getGranuleId());
			HibernateSessionFactory.sessionFactory = sessionFactory
			Session session = HibernateSessionFactory.getInstance().getCurrentSession();
			session.beginTransaction();
			if (session.createSQLQuery(
				"UPDATE granule_archive SET status='"+status+"'"
				+" WHERE "
				+" granule_id="+g.getGranuleId()
				)
				.executeUpdate()==0)
			log.debug("granuleId="+g.getGranuleId()+"-->archive status not updated");
			session.getTransaction().commit(); // commit also closes session
		}
		
		def updateGranuleArchiveChecksum = {Granule g, String name, String csum ->
			log.debug("updateGranuleArchiveCsum "+g.getGranuleId());
			HibernateSessionFactory.sessionFactory = sessionFactory
			Session session = HibernateSessionFactory.getInstance().getCurrentSession();
			session.beginTransaction();
			if (session.createSQLQuery(
				"UPDATE granule_archive SET checksum='"+csum+"'"
				+" WHERE "
				+" granule_id="+g.getGranuleId()
				+" AND "
				+" name='"+name+"'"
				)
				.executeUpdate()==0)
			log.debug("granuleId="+g.getGranuleId()+"-->archive checksum not updated");
			session.getTransaction().commit(); // commit also closes session
		}
			
		def updateGranuleRootPath = { Granule g, String path ->
			DataManager manager = DataManagerFactory.getInstance().createDataManager();
			HibernateSessionFactory.sessionFactory = sessionFactory
			g.setRootPath(path);
			manager.updateGranule(g);
		}
		
		def updateGranuleReferencePath = { gId, String path, String newPath ->
			HibernateSessionFactory.sessionFactory = sessionFactory
			log.debug("Updating granule reference ["+gId+"]");
			Session session = HibernateSessionFactory.getInstance().getCurrentSession();
			session.beginTransaction();
			if(session.createSQLQuery(
						"UPDATE granule_reference SET path='" + newPath + "'"
						+" WHERE "
						+" granule_id="+gId
						+" AND path='"+path+"'")
						.executeUpdate()==0){
				log.debug("No references Updated.");
			}
			else
				log.debug("References Updated.");
			session.getTransaction().commit(); // commit also closes session
		}
		
		def reassociateGranuleElement_db = {Integer gId, Integer from, Integer to, String type ->
			HibernateSessionFactory.sessionFactory = sessionFactory
			//log.debug("Updating granule reference ["+gId+"]");
			Session session = HibernateSessionFactory.getInstance().getCurrentSession();
			session.beginTransaction();
			if(session.createSQLQuery(
						"UPDATE granule_"+type+" SET de_id=" + to + ""
						+" WHERE "
						+" granule_id="+gId
						+" AND de_Id="+from+"")
						.executeUpdate()==0){
				//log.debug("No Element Updated.");
			}
			else
				log.debug("Elements Updated.");
			session.getTransaction().commit(); // commit also closes session
		}
		
		def reassociateGranuleElement = { Granule g, Dataset toD, Dataset fromD ->
			HibernateSessionFactory.sessionFactory = sessionFactory
			log.debug("in reassocGranuleElement");
			Map<Integer,String> deIDmapping = new HashMap<Integer,String>();
			for(DatasetElement toDE : toD.getDatasetElementSet()){
				for(DatasetElement fromDE : fromD.getDatasetElementSet()){
					if(toDE.getElementDD().equals(fromDE.getElementDD()))
					{
						//log.debug("adding " + toDE.getElementDD().getType())
						deIDmapping.put(fromDE.getDeId(), fromDE.getElementDD().getType()+","+toDE.getDeId());
					}
				}
			}
			log.debug("DEID Mappings");
			for(Entry<Integer,String> me : deIDmapping.entrySet()){
				String[] ary = me.getValue().split(",");
				String type = ary[0].trim();
				Integer deId = Integer.valueOf(ary[1].trim());
				if(type.equals("time"))
					type = "DATETIME";
				
				//log.debug("map "+ me.getKey() + " to " + deId + "["+type+"]");
				this.reassociateGranuleElement_db(g.getGranuleId(),me.getKey(),deId,type);
			}
		}
		
		//modifications to granule before getting here.
		def updateGranule = { Granule g ->
			DataManager manager = DataManagerFactory.getInstance().createDataManager();
			HibernateSessionFactory.sessionFactory = sessionFactory
			manager.updateGranule(g);
		}
		
		def updateGranuleReferenceStatus = { gId, String status, String path ->
			HibernateSessionFactory.sessionFactory = sessionFactory
			Query q = QueryFactory.getInstance().createQuery();
			q.updateAIPGranuleReferenceStatus(gId, status, path);
		}
		
		def addGranuleReference = { Integer gId, String type,String status,String path,String desc ->
			HibernateSessionFactory.sessionFactory = sessionFactory
			Query q = QueryFactory.getInstance().createQuery();
			q.addReference(gId, type, status, desc, path)
		}
		
		def deleteGranuleReference ={ Granule g, String type ->
			HibernateSessionFactory.sessionFactory = sessionFactory
			Query q = QueryFactory.getInstance().createQuery();
			q.removeReference(g.getGranuleId(), type);
		}
		
		def updateGranuleStatusAndVerify = { Integer id, status ->
			HibernateSessionFactory.sessionFactory = sessionFactory
			Session session = HibernateSessionFactory.getInstance().getCurrentSession();
			session.beginTransaction();
			if (session.createSQLQuery(
				"UPDATE granule SET status='" + status + "'"
				+", verify_time_long=" + new Date().getTime() + ""
				+" WHERE "
				+" granule_id=$id")
				.executeUpdate()==0)
				log.debug("granule status not updated");
			else log.debug("granule status set "+status);
			session.getTransaction().commit(); // commit also closes session
		}
		
		def findDatasetByProductId  = { int id -> 
			log.debug("findDatasetByProductId, datasetId = $id")
			HibernateSessionFactory.sessionFactory = sessionFactory
			Session session = HibernateSessionFactory.getInstance().getCurrentSession();
			session.beginTransaction();
			String query = "Select short_name from dataset where dataset_id in (Select dataset_id from collection_dataset where collection_id in (Select collection_id from collection_product where product_id='"+id+"'))";
			log.debug("Query: " + query);
			List<String> result = session.createSQLQuery(query).list();
			session.getTransaction().commit(); // commit also closes session
			return result;
			
		}
		
	def findGranuleList = { idList ->
		log.debug("findGranuleList: idList.size="+idList.size());
		HibernateSessionFactory.sessionFactory = sessionFactory
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
	}
	
	def getArchiveAIPByGranuleList = { gList ->
		HibernateSessionFactory.sessionFactory = sessionFactory
		gov.nasa.podaac.inventory.api.Query q = QueryFactory.getInstance().createQuery();
		try {
			//List<AIP> metadataList = new ArrayList<AIP>();
			def metadataList = []
			for (Integer granuleId : gList) {
				Granule granule = q.fetchGranuleById(Integer.valueOf(granuleId), "granuleArchiveSet");
				if (granule==null) {
					log.warn("GranuleId="+granuleId+" not found!");
					continue;
				}
				def amap = [:]
				for (GranuleArchive archive : granule.getGranuleArchiveSet()) {
					try {
						amap.granuleId=granule.getGranuleId();
						amap.URI = StringUtility.cleanPaths(granule.getRootPath(), granule.getRelPath(),archive.getName());
						amap.checksum = archive.getChecksum()
						amap.checksumType = granule.getChecksumType()
						amap.fileSize = archive.getFileSize();
						amap.type = archive.getType();
						amap.status = archive.getStatus()
					} catch (Exception e) {
					}
				}
				metadataList.add(amap);
			}
			log.debug("getArchiveAIPByGranule: sizeOfResult="+metadataList.size());
			return metadataList;
		} catch (Exception e) {
			log.error("getArchiveAIPByGranule: error getting archive info "+e.getMessage());
			return null;
		}
	}
	
	def getArchiveAIPByDataset = { datasetId ->
		gov.nasa.podaac.inventory.api.Query q = QueryFactory.getInstance().createQuery();
		String checksumType = "";
		try {
			checksumType = q.fetchDatasetById(datasetId, "datasetPolicy")
								.getDatasetPolicy().getChecksumType();
			log.debug("datasetId="+datasetId+":checksumType="+checksumType);
		} catch (Exception e) {
			log.error("getArchiveAIPByDataset: error getting datasetPolicy "+e.getMessage());
//			return new ArrayList<AIP>();
		}
		try {
			long time = System.currentTimeMillis();
			List<GranuleArchive> archiveList = q.fetchArchiveByDatasetId(datasetId);
			log.debug("Time="+(System.currentTimeMillis() - time));
			log.debug("getArchiveAIPByDataset: sizeOfResult="+archiveList.size());
			
			
			def metadataList = [];
			for (GranuleArchive archive : archiveList) {
				Granule g = q.fetchGranuleById(archive.getGranuleId());
				def amap = [:];
				amap.granuleId=archive.getGranuleId();
				amap.URI = StringUtility.cleanPaths(g.getRootPath(), g.getRelPath(),archive.getName());
				amap.checksum = archive.getChecksum()
				amap.checksumType = checksumType
				amap.fileSize = archive.getFileSize();
				amap.type = archive.getType();
				amap.status = archive.getStatus()
				metadataList.add(amap);
//				metadataList.add(
//						new AIP(archive.getGranuleId(), new URI(StringUtility.cleanPaths(g.getRootPath(), g.getRelPath(),archive.getName())),
//						archive.getChecksum(), checksumType, archive.getFileSize(),
//						archive.getType(), archive.getStatus())
//				);
			}
			return metadataList;
		} catch (Exception e) {
			log.error("getArchiveAIPByDataset: error getting archive info "+e.getMessage());
//			return new ArrayList<AIP>();
		}
	}
	
		
	def getGranuleReferences = { dId ->
		log.debug("fetchGranuleReferences, datasetId = $dId")
		HibernateSessionFactory.sessionFactory = sessionFactory
		Query query = QueryFactory.getInstance().createQuery()
		def list = query.fetchReferenceByDatasetId(dId)
		return list;
	}
	
	def getGranulesByDataset = { dId,start,stop,pattern ->
		log.debug("fetchGranuleReferences, datasetId = $dId")
		HibernateSessionFactory.sessionFactory = sessionFactory
		Query query = QueryFactory.getInstance().createQuery()
		def hasPattern = false;
		
		String whereClauses = "";
		if(start != null){
			whereClauses += " AND ARCHIVE_TIME_LONG > " +start; 	
		}
		if(stop != null){
			whereClauses += " AND ARCHIVE_TIME_LONG < " +stop;
		}
		if(pattern != null && pattern != ''){
			pattern = pattern.replace('#', '%');
			log.debug("pattern: $pattern");
			whereClauses += " AND name like '"+pattern+"'";
		}


		def q = "select granule_id from granule where dataset_id="+dId+" $whereClauses";
		log.debug("runnin query: " + q);
		List<java.math.BigDecimal> granules_big = (List<java.math.BigDecimal>) query.getResultList(q);
		List<Integer> granules = new ArrayList<Integer>();
		
		for(java.math.BigDecimal bd : granules_big){
			granules.add(bd.intValue());
		}

		return granules;

	}
		
	def fetchArchivePathByGranuleId = { gId ->
 	    log.debug("fetchArchvePathByGID, granuleId = $gId")
            HibernateSessionFactory.sessionFactory = sessionFactory
            Query query = QueryFactory.getInstance().createQuery()
            String str_path = query.fetchArchivePathByGranuleId(gId)
            return str_path;
	}

	def fetchSources = {
            log.debug("fetchSourceList")
            HibernateSessionFactory.sessionFactory = sessionFactory
            Query query = QueryFactory.getInstance().createQuery()
            def sources = query.listSource()
            return sources;
	}

        def fetchSensors = {
            log.debug("fetchSensorsList")
            HibernateSessionFactory.sessionFactory = sessionFactory
            Query query = QueryFactory.getInstance().createQuery()
            def sensors = query.listSensor()
            return sensors;
        }

	def fetchProviders = {
   	    log.debug("fetchProviderList")
            HibernateSessionFactory.sessionFactory = sessionFactory
            Query query = QueryFactory.getInstance().createQuery()
  	    def providers = query.listProvider()
	    return providers;
	}

	def fetchProviderByName = { pId ->
            log.debug("fetchProvider by Name, pId: $pId")
            HibernateSessionFactory.sessionFactory = sessionFactory
            Query query = QueryFactory.getInstance().createQuery()
            Provider provider = query.findProviderByShortName(pId)
            return provider
	}

	def fetchProviderById = {pId ->
 	    log.debug("fetchProvider by Id, pId: $pId")
	    HibernateSessionFactory.sessionFactory = sessionFactory
            Query query = QueryFactory.getInstance().createQuery()
	    Provider provider = query.findProviderById(pId)
	    return provider
	}


	def fetchGranuleById = {gId ->
	    log.debug("fetchGranuleById gid: $gId")
	    HibernateSessionFactory.sessionFactory = sessionFactory
            Query query = QueryFactory.getInstance().createQuery()
            Granule val = query.findGranuleByIdDeep(gId)
			val?.getGranuleSpatialSet()?.size();
			//fetchGranuleByIdDeep
			//Granule val2 = query.fetchGranuleByIdDeep(gId)
			//val.setGranuleSpatialSet(val2.getGranuleSpatialSet())
			
		 //val.getGranuleArchiveSet().each {
		//log.debug("flag: "+it.getCompressFlag());
		//}
	    return val;
        }

	def fetchGranuleByNameAndDataset = {gId, dId ->
            log.debug("fetchGranuleByNameAndDataset gid: $gId, dataset: ${dId.getShortName()}")
            HibernateSessionFactory.sessionFactory = sessionFactory
            Query query = QueryFactory.getInstance().createQuery()
            Granule val = query.findGranule(gId,dId)
            return val;	
	}

	def fetchGranuleByName = {gId ->
            log.debug("fetchGranuleByName gid: $gId")
            HibernateSessionFactory.sessionFactory = sessionFactory
            Query query = QueryFactory.getInstance().createQuery()
            Granule val = query.findGranuleByName(gId)
            return val;

	}


	def fetchLatestGranule = {dId ->
	    log.debug("fetchLatestGranule, dataset: $dId");
            HibernateSessionFactory.sessionFactory = sessionFactory
            Query query = QueryFactory.getInstance().createQuery()
            Integer val = query.fetchLatestGranuleIdByDatasetID(dId)
            return val;

	}

	def fetchDatasetPolicy = {dId ->
            log.debug("fetchDatasetPolicy, dataset: $dId");
            HibernateSessionFactory.sessionFactory = sessionFactory
            Query query = QueryFactory.getInstance().createQuery()
            DatasetPolicy val = query.fetchDatasetPolicyById(dId)
            return val;
	}

	def fetchDatasetCoverage = {dId ->
	    log.debug("fetchDatasetCoverage, dataset: $dId");
            HibernateSessionFactory.sessionFactory = sessionFactory
            Query query = QueryFactory.getInstance().createQuery()
            DatasetCoverage val = query.fetchDatasetCoverageById(dId)
            return val;
	}

	def sizeOfGranule = { dId ->
            log.debug("Size of Granule, dataset: $dId");
	    HibernateSessionFactory.sessionFactory = sessionFactory
            Query query = QueryFactory.getInstance().createQuery()	   
	    Integer val = query.sizeOfGranuleByDatasetId(dId)
	    return val;
	}

	def fetchDatasetList = {
            log.debug("fetchDatasetList")
	    HibernateSessionFactory.sessionFactory = sessionFactory
            Query query = QueryFactory.getInstance().createQuery()
            def list = query.listDatasets();
            return list;
	}
	
	def fetchDatasetById = { dId ->
	    log.debug("fetchDatasetById, dataset: $dId")
	    HibernateSessionFactory.sessionFactory = sessionFactory
	    Query query = QueryFactory.getInstance().createQuery()
		//Dataset d = query.fetchDatasetByIdFull(dId);
		Dataset d = query.fetchDatasetByIdFull(dId, "provider", "locationPolicySet", "datasetPolicy")
		if(d == null)
			return null
		d.getDatasetPolicy().getDatasetId();
		return d;
	}

	def fetchDatasetByShortName = {dId ->
            log.debug("fetchDatasetByShortname, dataset: $dId");
	    HibernateSessionFactory.sessionFactory = sessionFactory
            Query query = QueryFactory.getInstance().createQuery()
            Dataset d = query.findDatasetByShortNameDeep(dId);
            return d;

	}
	
	def fetchDatasetByPersistentId = { dId ->
            log.debug("fetchDatasetByPersistentId, dataset: $dId");
 	    HibernateSessionFactory.sessionFactory = sessionFactory
            Query query = QueryFactory.getInstance().createQuery()
                Dataset d = query.findDatasetByPersistentId(dId);
                return d;
	}
	
	def fetchEchoGranules = { dId ->
		log.debug("Fetching Echo Granules")
		HibernateSessionFactory.sessionFactory = sessionFactory
		Query q = QueryFactory.getInstance().createQuery()
		def listing = q.getResultList("select granule_id from (select granule_id,version_id from (select granule_id, version_id, max(version_id) over (partition by granule_id) max_version from   granule_meta_history where (LAST_REVISION_DATE_LONG > ECHO_SUBMIT_DATE_LONG OR ECHO_SUBMIT_DATE_LONG is null)) where version_id = max_version AND granule_id in (select granule_id from granule where DATASET_ID="+dId +"))");
		return listing
	}
	

}
