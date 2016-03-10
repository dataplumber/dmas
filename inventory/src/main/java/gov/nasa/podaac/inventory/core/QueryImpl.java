// Copyright 2007, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.core;

import gov.nasa.podaac.common.api.util.StringUtility;
import gov.nasa.podaac.inventory.api.Constant.VersionPolicy;
import gov.nasa.podaac.inventory.api.DataManager;
import gov.nasa.podaac.inventory.api.DataManagerFactory;
import gov.nasa.podaac.inventory.api.Query;
import gov.nasa.podaac.inventory.api.Constant.GranuleArchiveType;
import gov.nasa.podaac.inventory.api.Constant.GranuleStatus;
import gov.nasa.podaac.inventory.dao.DAOFactory;
import gov.nasa.podaac.inventory.dao.EntityManager.CollectionDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.CollectionDatasetDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.CollectionElementDDDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.CollectionProductDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.ContactDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.DatasetDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.DatasetCoverageDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.DatasetPolicyDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.GranuleDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.ElementDDDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.GranuleMetaHistoryDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.MetadataManifestDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.ProjectDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.ProviderDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.SensorDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.SourceDAO;
import gov.nasa.podaac.inventory.dao.GranuleManager;
import gov.nasa.podaac.inventory.exceptions.InventoryException;
import gov.nasa.podaac.inventory.hibernate.HibernateSessionFactory;
import gov.nasa.podaac.inventory.model.Collection;
import gov.nasa.podaac.inventory.model.CollectionDataset;
import gov.nasa.podaac.inventory.model.CollectionElementDD;
import gov.nasa.podaac.inventory.model.CollectionProduct;
import gov.nasa.podaac.inventory.model.Contact;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.DatasetCoverage;
import gov.nasa.podaac.inventory.model.DatasetElement;
import gov.nasa.podaac.inventory.model.DatasetPolicy;

import gov.nasa.podaac.inventory.model.DatasetLocationPolicy;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.GranuleArchive;
import gov.nasa.podaac.inventory.model.GranuleCharacter;
import gov.nasa.podaac.inventory.model.GranuleDateTime;
import gov.nasa.podaac.inventory.model.GranuleReal;
import gov.nasa.podaac.inventory.model.ElementDD;
import gov.nasa.podaac.inventory.model.GranuleMetaHistory;
import gov.nasa.podaac.inventory.model.GranuleReference;
import gov.nasa.podaac.inventory.model.GranuleSpatial;
import gov.nasa.podaac.inventory.model.MetadataManifest;
import gov.nasa.podaac.inventory.model.Project;
import gov.nasa.podaac.inventory.model.Provider;
import gov.nasa.podaac.inventory.model.Sensor;
import gov.nasa.podaac.inventory.model.Source;

import java.io.File;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import oracle.jdbc.OracleTypes;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;

/**
 * @author clwong
 * 
 * $Id: QueryImpl.java 13176 2014-04-03 18:56:47Z gangl $
 */
public class QueryImpl implements Query {

	private static Log log = LogFactory.getLog(QueryImpl.class);
	private DAOFactory daoFactory = DAOFactory.instance(DAOFactory.HIBERNATE);
	
	// constants
	private static final String GRANULE = Granule.class.getSimpleName().toLowerCase();
	private static final String DATASET = Dataset.class.getSimpleName().toLowerCase();

	// constants for pojo objects' properties' names used in query methods
	private static final String GRANULE_NAME = "name";
	private static final String SHORTNAME = "shortName";
	private static final String GRANULE_SET = "granuleSet";
	private static final String DATASET_ID = "datasetId";
	private static final String GRANULE_ARCHIVE_SET = "granuleArchiveSet";
	private static final String GRANULE_REF_SET = "granuleReferenceSet";

	public QueryImpl() {
		super();
	}

	public void evictObject(Object o){
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		HibernateSessionFactory.getInstance().getCurrentSession().flush();
		HibernateSessionFactory.getInstance().getCurrentSession().evict(o);
		HibernateSessionFactory.getInstance().getCurrentSession().clear();
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	}
	
	//METADATA MASNIFEST INTERFACES
	public void saveMetadataManifest(MetadataManifest mf) throws InventoryException{
		//log.debug("findGranuleById: id="+id);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		MetadataManifestDAO dao = daoFactory.getMetadataManifestDAO();
		
		try{
			dao.save(mf);
		}
		catch(HibernateException e){
			throw new InventoryException("Cannot save Metadata Manifest\n" + e.getMessage()); 
		}
		
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    //return result;
	}
	
	//-------------------  Granule Query Interfaces ------
	public Granule findGranuleById(Integer id) {
		log.debug("findGranuleById: id="+id);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		GranuleDAO dao = daoFactory.getGranuleDAO();
		Granule result = dao.findById(id);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}
	
	public Granule findGranuleByIdDeep(Integer id) {
		log.debug("findGranuleById: id="+id);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		//HibernateSessionFactory.getInstance().getCurrentSession().
		GranuleDAO dao = daoFactory.getGranuleDAO();
		Granule result = dao.findById(id);
		if(result == null)
			return null;
		result.getGranuleArchiveSet().size();
		result.getGranuleReferenceSet().size();
		for(GranuleMetaHistory mgh : result.getMetaHistorySet())
			mgh.getCreationDate();
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}
	
	public void addReal(int datasetId, int deId, double value, String units)
	{
		String sql= "insert into DATASET_REAL (DATASET_ID, DE_ID, VALUE, UNITS) values ("+datasetId+","+deId+","+value+",'"+units+"')"; 
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		HibernateSessionFactory.getInstance().getCurrentSession().createSQLQuery(sql).executeUpdate();  
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
		return;
	}
	
	public List<Dataset> fetchNonSpatialDatasets(){
		String sql= "select * from dataset where dataset_id not in (select dataset_id from dataset_spatial)"; 
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		List<Dataset> dl = HibernateSessionFactory.getInstance().getCurrentSession().createSQLQuery(sql).addEntity(Dataset.class).list();
		for(Dataset d: dl){
			d.getDatasetCoverage().getEastLon();
		}
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
		return dl;
	}
	
	public void runQuery(String sql){
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		HibernateSessionFactory.getInstance().getCurrentSession().createSQLQuery(sql).executeUpdate();  
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	}
	
	private Integer countDeIDUsage(String tableName, Integer deId){
		log.debug("Checking "+tableName+" for deId: " + deId);
		
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		String query = "select count(*) as cnt from " + tableName + " where de_id="+deId;
		log.debug("executing query: " + query);
		SQLQuery q = HibernateSessionFactory.getInstance().getCurrentSession().createSQLQuery(query);
		q.addScalar("cnt", Hibernate.INTEGER);
		
		@SuppressWarnings("unchecked")
		List<Object> objects = (List<Object>) q.list();
		if(!objects.isEmpty())
		{
			Integer i = (Integer) objects.get(0);
			return i;
		}
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
		return 0;
	}
	
	
	public boolean deiIdIsUsed(Integer did){
		String[] tables = {"granule_real","granule_integer","granule_character","granule_datetime","granule_spatial"}; 
		for(String t: tables){
			Integer cnt = countDeIDUsage(t,did);
			if(cnt > 0)
				return true;
		}
		log.debug("Never got count > 0 ");
		return false;
	}
	
	public List<Integer> getDatasetIds(Integer dId, String dids){
		
		String didQuery = "1=1";
		List<Integer> resList = new ArrayList<Integer>();
		if(dids == null){
			//no dids supplied, means all DEs are deleted.
		}else{
			didQuery = "de_id not in ("+dids+")";
		}
		String sql= "select de_id from dataset_element where DATASET_ID="+ dId +" AND " + didQuery;
		log.debug("running sql: " + sql );
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		List<java.math.BigDecimal> resultList = HibernateSessionFactory.getInstance().getCurrentSession().createSQLQuery(sql).list();
		for(java.math.BigDecimal bd : resultList){
			resList.add(bd.intValue());
		}
		//int maxID = ( (Integer)list.get(0) ).intValue(); 
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
		//return maxID;
		return resList;
	}
	
	
	public void addChars(Integer datasetId, Integer deId, String value)
	{
		String sql= "insert into DATASET_CHARACTER (DATASET_ID, DE_ID, VALUE) values ("+datasetId+","+deId+",'"+value+"')"; 
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		HibernateSessionFactory.getInstance().getCurrentSession().createSQLQuery(sql).executeUpdate();  
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
		return;
	}
	public void addInteger(Integer datasetId, Integer deId, Integer value,String units)
	{
		String sql= "insert into DATASET_INTEGER (DATASET_ID, DE_ID, VALUE, UNITS) values ("+datasetId+","+deId+","+value+",'"+units+"')"; 
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		HibernateSessionFactory.getInstance().getCurrentSession().createSQLQuery(sql).executeUpdate();  
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
		return;
	}
	public void addDateTime(Integer datasetId, Integer deId, Long value)
	{
		String sql= "insert into DATASET_DATETIME (DATASET_ID, DE_ID, VALUE_LONG) values ("+datasetId+","+deId+","+value.toString()+")"; 
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		HibernateSessionFactory.getInstance().getCurrentSession().createSQLQuery(sql).executeUpdate();  
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
		return;
	}
	
	public List<BigDecimal>listLatestGranuleIdsByDatasetID(int datasetId)
	{
		String sql= "select GRANULE_ID from GRANULE where DATASET_ID="+ datasetId +" ORDER BY ARCHIVE_TIME_LONG desc"; 
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		List<BigDecimal> resultList = HibernateSessionFactory.getInstance().getCurrentSession().createSQLQuery(sql).list(); 
		
		//int maxID = ( (Integer)list.get(0) ).intValue(); 
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
		//return maxID;
		return resultList;
	}
	
	public List<Granule> listGranuleByDateRange(long start, long stop, String timeField){
		String sql= "select * from GRANULE where "+timeField+" >= "+start+" AND "+timeField+" < "+stop+" ORDER BY "+timeField+" ASC"; 
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		List<Granule> resultList = HibernateSessionFactory.getInstance().getCurrentSession().createSQLQuery(sql).addEntity(Granule.class).list(); 
		for(Granule g : resultList){
			g.getGranuleArchiveSet().size();
			g.getMetaHistorySet().size();
			g.getDataset().getSoftwareSet().size();
			g.getDataset().getProvider().getShortName();
			g.getDataset().getDatasetPolicy().getDataClass();
			for(CollectionDataset c: g.getDataset().getCollectionDatasetSet()){
				if(c.getCollectionDatasetPK().getCollection().getCollectionDatasetSet().size()<2)
				{
					c.getCollectionDatasetPK().getCollection().getCollectionProduct().getProductId();
				}
				
			}
		}
		//int maxID = ( (Integer)list.get(0) ).intValue(); 
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
		//return maxID;
		return resultList;
		
		
	}
	
	public List<Granule> listGranuleByDateRange(long start, long stop, String timeField, int dsId){
		String sql= "select * from GRANULE where "+timeField+" >= "+start+" AND "+timeField+" < "+stop+" AND dataset_id="+dsId+" ORDER BY "+timeField+" ASC"; 
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		List<Granule> resultList = HibernateSessionFactory.getInstance().getCurrentSession().createSQLQuery(sql).addEntity(Granule.class).list(); 
		for(Granule g : resultList){
			g.getGranuleArchiveSet().size();
			g.getMetaHistorySet().size();
			g.getDataset().getSoftwareSet().size();
			g.getDataset().getProvider().getShortName();
			g.getDataset().getDatasetPolicy().getDataClass();
			g.getGranuleReferenceSet().size();
			g.getGranuleSpatialSet().size();
			g.getGranuleRealSet().size();
			for(CollectionDataset c: g.getDataset().getCollectionDatasetSet()){
				if(c.getCollectionDatasetPK().getCollection().getCollectionDatasetSet().size()<2)
				{
					c.getCollectionDatasetPK().getCollection().getCollectionProduct().getProductId();
				}
				
			}
		}
		//int maxID = ( (Integer)list.get(0) ).intValue(); 
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
		//return maxID;
		return resultList;
	}
	
	
	public List<Granule> listGranuleByDateRangeWithDelete(long start, long stop, String timeField){
		String sql= "select * from GRANULE where "+timeField+" >= "+start+" AND "+timeField+" < "+stop+" OR granule_id in (select granule_id from granule_meta_history where LAST_REVISION_DATE_LONG >= "+start+" AND LAST_REVISION_DATE_LONG < "+stop+") ORDER BY "+timeField+" ASC"; 
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		List<Granule> resultList = HibernateSessionFactory.getInstance().getCurrentSession().createSQLQuery(sql).addEntity(Granule.class).list(); 
		for(Granule g : resultList){
			g.getGranuleArchiveSet().size();
			g.getMetaHistorySet().size();
			g.getDataset().getSoftwareSet().size();
			g.getDataset().getProvider().getShortName();
			g.getDataset().getDatasetPolicy().getDataClass();
			for(CollectionDataset c: g.getDataset().getCollectionDatasetSet()){
				if(c.getCollectionDatasetPK().getCollection().getCollectionDatasetSet().size()<2)
				{
					c.getCollectionDatasetPK().getCollection().getCollectionProduct().getProductId();
				}
				
			}
		}
		//int maxID = ( (Integer)list.get(0) ).intValue(); 
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
		//return maxID;
		return resultList;
		
		
	}
	public int fetchLatestGranuleIdByDatasetID(int datasetId)
	{
		String sql = "select INGEST_TIME_LONG, GRANULE_ID from GRANULE where DATASET_ID="+ datasetId +" AND INGEST_TIME_LONG=(select max(ingest_time_long) from granule where DATASET_ID="+ datasetId +")";
		//String sql= "select INGEST_TIME_LONG, GRANULE_ID from GRANULE where DATASET_ID="+ datasetId +" ORDER BY INGEST_TIME_LONG desc"; 
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		List list = HibernateSessionFactory.getInstance().getCurrentSession().createSQLQuery(sql).list(); 
		Iterator i =list.iterator();
		if(i.hasNext())
		{
			Object x[] =(Object[]) i.next();
			//System.out.println("Value at object 1: " + x[1].toString());
			HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
			return new Integer( ((BigDecimal)x[1]).intValue());
		}
		//int maxID = ( (Integer)list.get(0) ).intValue(); 
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
		//return maxID;
		return 0;
	}
	public Granule fetchGranuleById(Integer id, String... dependencies) {
		log.debug("fetchGranuleById: id="+id);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		GranuleDAO dao = daoFactory.getGranuleDAO();
		
//		for(String s : dependencies){
//			System.out.println("Dependencies :" + s);
//		}
		Granule result = dao.fetchById(id, dependencies);
		
		
//		result.getGranuleSpatialSet().size();
//		for(GranuleSpatial element :result.getGranuleSpatialSet())
//        {
//                element.getDatasetElement().getElementDD().getShortName();
//        }
		
//		System.out.println("Spatials: "+result.getGranuleSpatialSet().size());
//		System.out.println("chars: "+result.getGranuleCharacterSet().size());
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}
	
	public Granule fetchGranuleByIdDeep(Integer id, String... dependencies) {
                log.debug("fetchGranuleById: id="+id);
                HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
                GranuleDAO dao = daoFactory.getGranuleDAO();
                Granule result = dao.fetchById(id, dependencies);
                
                if(result == null)
                		return null;
                
                for(GranuleCharacter element :result.getGranuleCharacterSet())
                {
                        element.getDatasetElement().getElementDD().getShortName();
                }
                for(GranuleReal element :result.getGranuleRealSet())
                {
                        element.getDatasetElement().getElementDD().getShortName();
                }
                for(GranuleDateTime element :result.getGranuleDateTimeSet())
                {
                        element.getDatasetElement().getElementDD().getShortName();
                }
                for(GranuleSpatial element :result.getGranuleSpatialSet())
                {
                        element.getDatasetElement().getElementDD().getShortName();
                }
                result.getGranuleArchiveSet().size();
                result.getGranuleReferenceSet().size();
                result.getMetaHistorySet().size();
            HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
            return result;
        }	
	public Granule findGranuleByName(String name) {
		log.debug("findGranuleByName: "+GRANULE_NAME+"="+name);
		List<Granule> result = listGranuleByProperty(GRANULE_NAME, name);
	    if (result.size() > 0) return result.get(0);
	    else return null;
	}
	
	public Granule fetchGranuleByName(String name) {
		log.debug("fetchGranuleByName: "+GRANULE_NAME+"="+name);
		List<Granule> granuleList = this.fetchGranuleList(
				new String[]{  }, 
				GRANULE+".name='"+name+"'");
		
	    if (granuleList.size() > 0) 
	    	{
	    		
	    		return granuleList.get(0);
	    	}
	    else return null;
	}
	
	
	public List<Integer> listGranuleId(String...whereClause) {
		log.debug("listGranuleId...");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		GranuleDAO dao = daoFactory.getGranuleDAO();
		List<Integer> resultList = dao.listId(whereClause);
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return resultList;
	}
	
	public List<Integer> listGranuleIdByProperty (String propertyName, Object value) {
		log.debug("listGranuleIdByProperty: "+propertyName+":"+value);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		GranuleDAO dao = daoFactory.getGranuleDAO();
		List<Integer> resultList = dao.listId(GRANULE+"."+propertyName+"="+value);
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return resultList;
	}

	public List<Granule> listGranule() {
		log.debug("listGranule: ");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		GranuleDAO dao = daoFactory.getGranuleDAO();
		List<Granule> resultList = dao.findAll();
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return resultList;
	}
	
	public List<Granule> listGranuleByProperty(String propertyName, Object value) {
		log.debug("listGranuleByProperty: "+propertyName+"="+value);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		GranuleDAO dao = daoFactory.getGranuleDAO();
		List<Granule> result = dao.findByPropertyName(propertyName, value);
		
		if (result.size() > 0){
			result.get(0).getGranuleSpatialSet().size();
		}
		
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}
	
	public List<Granule> listGranuleByIngestTime(Date startTime, Date endTime) {
		log.debug("listGranuleByIngestTime: startTime="+startTime+" endTime="+endTime);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		GranuleDAO dao = daoFactory.getGranuleDAO();
		List<Granule> resultList = dao.listByIngestTimeRange(startTime, endTime);
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return resultList;
	}
	
	public List<Granule> fetchGranuleList(String[]joinProperties, String...whereClause) {
		log.debug("fetchGranuleList:");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		GranuleDAO dao = daoFactory.getGranuleDAO();
		List<Granule> resultList = dao.fetch(joinProperties, whereClause);
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return resultList;
	}

	public List<Granule> fetchGranuleList(Granule example, String...joinProperties) {
		log.debug("fetchGranuleList:");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		GranuleDAO dao = daoFactory.getGranuleDAO();
		List<Granule> resultList = dao.findByExample(example, joinProperties);
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return resultList;
	}

	public List<Granule> fetchGranuleList(Properties properties, String...joinProperties) {
		log.debug("fetchGranuleList:");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		GranuleDAO dao = daoFactory.getGranuleDAO();
		List<Granule> resultList = dao.fetchByProperties(properties, joinProperties);
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return resultList;
	}

	public void mapGranuleToXML(String filename) {
		log.debug("mapGranuleToXml: ");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		GranuleDAO dao = daoFactory.getGranuleDAO();
		dao.toXMLFile(dao.toXML(), filename);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	}
	
	//-------------------  ElementDD Query Interfaces ------
	private static Map<String, ElementDD> granuleElementShortNames = new TreeMap();
	
	public Map<String, ElementDD> getGranuleElementShortNames() {
		return granuleElementShortNames;
	}

	public void setGranuleElementShortNames(
			Map<String, ElementDD> granuleElementShortNames) {
		QueryImpl.granuleElementShortNames = granuleElementShortNames;
	}
	
	public void initGranuleElementShortNames() {
		if (granuleElementShortNames.isEmpty())
			resetGranuleElementShortNames();
	}
	
	public void resetGranuleElementShortNames() {
		List<ElementDD> granuleElementDDList = listElementDD();
		for (ElementDD element : granuleElementDDList) {
			granuleElementShortNames.put(element.getShortName(), element);
		}
	}
	
	public ElementDD findElementDDByShortName(String shortName) {
		log.debug("findElementDDByShortName..."+shortName);
		if (granuleElementShortNames.containsKey(shortName)) {
			return granuleElementShortNames.get(shortName);
		} else {
			List<ElementDD> result = listElementDDByProperty(SHORTNAME, shortName);
			if (result.size() > 0) {
				granuleElementShortNames.put(shortName, result.get(0));
				return result.get(0);
			}
			else return null;
		}
	}
	
	public ElementDD findElementDDById(Integer id) {
		log.debug("findElementDDById: "+id);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		ElementDDDAO dao = daoFactory.getElementDDDAO();
		ElementDD result = dao.findById(id);
		result.getDatasetElementSet().size();
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
		return result;
	}

	public ElementDD fetchElementDDById(Integer id, String... dependencies) {
		log.debug("fetchElementDD: id="+id);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		ElementDDDAO dao = daoFactory.getElementDDDAO();
		ElementDD result = dao.fetchById(id, dependencies);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}
	
	public List<ElementDD> listElementDD() {
		log.debug("listElementDD...");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		ElementDDDAO dao = daoFactory.getElementDDDAO();
		List<ElementDD> resultList = dao.findAll();
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return resultList;
	}

	public List<ElementDD> listElementDDByProperty(String propertyName, Object value) {
		log.debug("listElementDDByProperty: "+propertyName+"="+value);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		ElementDDDAO dao = daoFactory.getElementDDDAO();
		List<ElementDD> result = dao.findByPropertyName(propertyName, value);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}

	public List<ElementDD> fetchElementDDList(ElementDD example, String...joinProperties) {
		log.debug("fetchElementDDList:");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		ElementDDDAO dao = daoFactory.getElementDDDAO();
		List<ElementDD> resultList = dao.fetch(example, joinProperties);
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return resultList;
	}
	
	public void mapElementDDToXML(String filename) {
		log.debug("mapElementDDToXml: ");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		ElementDDDAO dao = daoFactory.getElementDDDAO();
		dao.toXMLFile(dao.toXML(), filename);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	}
	//-------------------  Dataset Query Interfaces ------
	public Dataset findDatasetById(Integer id) {
		log.debug("findDatasetById..."+id);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		DatasetDAO dao = daoFactory.getDatasetDAO();
		Dataset result = dao.findById(id);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}

	public Dataset fetchDatasetById(Integer id, String... dependencies) {
		log.debug("fetchDatasetById: id="+id);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		DatasetDAO dao = daoFactory.getDatasetDAO();
		Dataset result;
		
		if (dependencies.length > 0) result = dao.fetchById(id, dependencies);
		else result = dao.excludeFetchById(id, "granuleSet", "granuleElementSet", "DatasetCharacterSet", "DatasetIntegerSet","DatasetRealSet","DatasetDateTimeSet","citationSet","datasetElementSet");
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    
	    return result;
	}
	
	public Dataset fetchDatasetByIdFull(int id, String ...dependencies) {
		log.debug("fetchDatasetById: id="+id);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		DatasetDAO dao = daoFactory.getDatasetDAO();
		Dataset result;
		if (dependencies.length > 0) {
	
			result = dao.fetchById(id, dependencies);
			try{
			result.getDatasetElementSet().size();
			result.getDatasetRealSet().size();
			result.getCitationSet().size();
			for(DatasetElement dd : result.getDatasetElementSet()){
				dd.getElementDD().getElementId();
				dd.getElementDD().getLongName();
			}
			}catch(NullPointerException npe){
			}
		}
		else result = dao.excludeFetchById(id, "granuleSet", "granuleElementSet", "DatasetCharacterSet", "DatasetIntegerSet","DatasetRealSet","DatasetCitationSet","DatasetDateTimeSet","citationSet","datasetElementSet");
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}

	// include granules; this can return large result set
	public Dataset fetchDepthDatasetById(Integer id) {
		log.debug("fetchDatasetById: id="+id);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		DatasetDAO dao = daoFactory.getDatasetDAO();
		Dataset result = dao.fetchById(id);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}
		
	public Dataset findDatasetByShortName(String shortName) {
		log.debug("findDatasetByShortName..."+shortName);
		List<Dataset> result = listDatasetByProperty(SHORTNAME, shortName);
	    if (result.size() > 0) {
//	    	Hibernate.initialize(result.get(0));
//	    	Hibernate.initialize(result.get(0).getDatasetPolicy());
//	    	result.get(0).getDatasetPolicy().getDataFormat();
//	    	result.get(0).getDatasetElementSet().size();
	    	return result.get(0);
	  
	    }
	    else return null;
	}
	
	public Dataset findDatasetByShortNameDeep(String shortName) {
		log.debug("findDatasetByShortName..."+shortName);
		List<Dataset> result = listDatasetByPropertydeep(SHORTNAME, shortName);
	    if (result.size() > 0) {
//	    	Hibernate.initialize(result.get(0));
//	    	Hibernate.initialize(result.get(0).getDatasetPolicy());
	    	result.get(0).getDatasetPolicy().getDataFormat();
	    	result.get(0).getDatasetElementSet().size();
	    	return result.get(0);
	  
	    }
	    else return null;
	}

	public List<Integer> listDatasetId(String...whereClause) {
		log.debug("listDatasetId...");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		DatasetDAO dao = daoFactory.getDatasetDAO();
		List<Integer> resultList = dao.listId(whereClause);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return resultList;
	}
	
	public List<Integer> listDatasetIdByProperty(String propertyName, Object value) {
		log.debug("listDatasetId...");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		DatasetDAO dao = daoFactory.getDatasetDAO();
		List<Integer> resultList = dao.listId(propertyName+"="+value);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return resultList;
	}

	public List<Dataset> listDatasets(){
		log.debug("listDataset...");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		String sql = "select dataset_Id, short_Name, persistent_Id from Dataset";
		List<Object[]> it = 
			 HibernateSessionFactory.getInstance().getCurrentSession()
			.createSQLQuery(sql)
			.addScalar("dataset_id", Hibernate.INTEGER)
			.addScalar("short_name", Hibernate.STRING)
			.addScalar("persistent_id", Hibernate.STRING)
			.list();
		 HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
		 List<Dataset> dl = new ArrayList<Dataset>();
		 for(Object o[] : it ){
			 Dataset d = new Dataset();
			 d.setDatasetId((Integer)o[0]);
			 d.setShortName((String)o[1]);
			 d.setPersistentId((String)o[2]);
			 dl.add(d);
		 }
		 return dl;
	}
	
	public List<Dataset> listDataset() {
		log.debug("listDataset...");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		DatasetDAO dao = daoFactory.getDatasetDAO();
		List<Dataset> resultList = dao.findAll();
		 HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
		return resultList;
	}
	
	public List<Dataset> listDatasetByProperty(String propertyName, Object value) {
		log.debug("listDatasetByProperty: "+propertyName+"="+value);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		DatasetDAO dao = daoFactory.getDatasetDAO();
		List<Dataset> result = dao.findByPropertyName(propertyName, value);
		 if (result.size() > 0) {
			 	
		    	Hibernate.initialize(result.get(0));
		    	//Hibernate.initialize(result.get(0).getDatasetPolicy());
		    	//result.get(0).getDatasetPolicy().getDataFormat();
		    	result.get(0).getDatasetElementSet().size();
		    }
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}
	
	private List<Dataset> listDatasetByPropertydeep(String propertyName, Object value) {
		log.debug("listDatasetByProperty: "+propertyName+"="+value);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		DatasetDAO dao = daoFactory.getDatasetDAO();
		List<Dataset> result = dao.findByPropertyName(propertyName, value);
		 if (result.size() > 0) {
			 	
		    	Hibernate.initialize(result.get(0));
		    	//Hibernate.initialize(result.get(0).getDatasetPolicy());
		    	result.get(0).getDatasetPolicy().getDataFormat();
		    	result.get(0).getDatasetElementSet().size();
		    }
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}
	public List<Dataset> fetchDatasetList(String[]joinProperties, String...whereClause) {

		log.debug("fetchDatasetList:");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		DatasetDAO dao = daoFactory.getDatasetDAO();
		List<Dataset> resultList = dao.fetch(joinProperties, whereClause);
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return resultList;
	}
	
	public List<Dataset> fetchDatasetList(Dataset example, String...joinProperties) {
		log.debug("fetchDatasetList:");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		DatasetDAO dao = daoFactory.getDatasetDAO();
		List<Dataset> resultList = dao.fetch(example, joinProperties);
		for(Dataset d : resultList){
			for( DatasetElement de : d.getDatasetElementSet())
				de.getElementDD().getElementId();
		}
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return resultList;
	}

	public List<Dataset> fetchDatasetList(Properties properties, String...joinProperties) {
		log.debug("fetchDatasetList:");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		DatasetDAO dao = daoFactory.getDatasetDAO();
		List<Dataset> resultList = dao.fetchByProperties(properties, joinProperties);
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return resultList;
	}
	
	public void mapDatasetToXML(String filename) {
		log.debug("mapDatasetToXml: ");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		DatasetDAO dao = daoFactory.getDatasetDAO();
		dao.toXMLFile(dao.toXML(), filename);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	}
	
	//------------------- Source Query Interfaces  ------
	public Source findSourceById(Integer id) {
		log.debug("findSourceById: "+id);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		SourceDAO dao = daoFactory.getSourceDAO();
		Source result = dao.findById(id);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}
	public Source fetchSourceById(Integer id, String... dependencies) {
		log.debug("fetchSourceById: id="+id);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		SourceDAO dao = daoFactory.getSourceDAO();
		Source result = dao.fetchById(id, dependencies);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}	
	public List<Source> listSource() {
		log.debug("listSource...");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		SourceDAO dao = daoFactory.getSourceDAO();
		List<Source> resultList = dao.findAll();
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return resultList;
	}

	public List<Source> fetchSourceList(Source example,
			String... joinProperties) {
		log.debug("fetchSourceList:");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		SourceDAO dao = daoFactory.getSourceDAO();
		List<Source> resultList = dao.fetch(example, joinProperties);
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return resultList;
	}

	public void mapSourceToXML(String filename) {
		log.debug("mapSourceToXml: ");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		SourceDAO dao = daoFactory.getSourceDAO();
		dao.toXMLFile(dao.toXML(), filename);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	}
	
	//----------------- Sensor Query Interfaces  ------
	public Sensor findSensorById(Integer id) {
		log.debug("findSensorById: "+id);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		SensorDAO dao = daoFactory.getSensorDAO();
		Sensor result = dao.findById(id);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}

	public Sensor fetchSensorById(Integer id, String... dependencies) {
		log.debug("fetchSensorById: id="+id);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		SensorDAO dao = daoFactory.getSensorDAO();
		Sensor result = dao.fetchById(id, dependencies);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}
	
	public List<Sensor> listSensor() {
		log.debug("listSensor...");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		SensorDAO dao = daoFactory.getSensorDAO();
		List<Sensor> resultList = dao.findAll();
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return resultList;
	}

	public List<Sensor> fetchSensorList(Sensor example,
			String... joinProperties) {
		log.debug("fetchSensorList:");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		SensorDAO dao = daoFactory.getSensorDAO();
		List<Sensor> resultList = dao.fetch(example, joinProperties);
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return resultList;
	}

	public void mapSensorToXML(String filename) {
		log.debug("mapSensorToXml: ");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		SensorDAO dao = daoFactory.getSensorDAO();
		dao.toXMLFile(dao.toXML(), filename);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	}
	
	//----------------- Provider Query Interfaces  ------
	public Provider findProviderById(Integer id) {
		log.debug("findProviderById: "+id);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		ProviderDAO dao = daoFactory.getProviderDAO();
		Provider result = dao.findById(id);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}
	
	public Provider fetchProviderById(Integer id, String... dependencies) {
		log.debug("fetchProviderById: id="+id);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		ProviderDAO dao = daoFactory.getProviderDAO();
		Provider result = dao.fetchById(id, dependencies);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}
		
	public Provider findProviderByShortName(String shortName) {
		log.debug("findProviderByShortName..."+shortName);
		List<Provider> result = listProviderByProperty(SHORTNAME, shortName);
	    if (result.size() > 0) {
	    	return result.get(0);
	    }
	    else return null;
	}

	public List<Integer> listProviderId(String...whereClause) {
		log.debug("listProviderId...");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		ProviderDAO dao = daoFactory.getProviderDAO();
		List<Integer> resultList = dao.listId(whereClause);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return resultList;
	}
	
	public List<Integer> listProviderIdByProperty(String propertyName, Object value) {
		log.debug("listProviderId...");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		ProviderDAO dao = daoFactory.getProviderDAO();
		List<Integer> resultList = dao.listId(propertyName+"="+value);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return resultList;
	}
	public List<Project> listProject() {
		log.debug("listProject...");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		ProjectDAO dao = daoFactory.getProjectDAO();
		List<Project> resultList = dao.findAll();
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return resultList;
	}

	public List<Provider> listProvider() {
		log.debug("listProvider...");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		ProviderDAO dao = daoFactory.getProviderDAO();
		List<Provider> resultList = dao.findAll();
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return resultList;
	}
	
	public List<Provider> listProviderByProperty(String propertyName, Object value) {
		log.debug("listProviderByProperty: "+propertyName+"="+value);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		ProviderDAO dao = daoFactory.getProviderDAO();
		List<Provider> result = dao.findByPropertyName(propertyName, value);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}

	public List<Provider> fetchProviderList(String[]joinProperties, String...whereClause) {
		log.debug("fetchProviderList:");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		ProviderDAO dao = daoFactory.getProviderDAO();
		List<Provider> resultList = dao.fetch(joinProperties, whereClause);
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return resultList;
	}
	
	public List<Provider> fetchProviderList(Provider example, String...joinProperties) {
		log.debug("fetchProviderList:");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		ProviderDAO dao = daoFactory.getProviderDAO();
		List<Provider> resultList = dao.fetch(example, joinProperties);
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return resultList;
	}

	public List<Provider> fetchProviderList(Properties properties, String...joinProperties) {
		log.debug("fetchProviderList:");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		ProviderDAO dao = daoFactory.getProviderDAO();
		List<Provider> resultList = dao.fetchByProperties(properties, joinProperties);
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return resultList;
	}
	
	public void mapProviderToXML(String filename) {
		log.debug("mapProviderToXml: ");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		ProviderDAO dao = daoFactory.getProviderDAO();
		dao.toXMLFile(dao.toXML(), filename);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	}

	//----------------- Provider Contact Query Interfaces  ------
	public Contact findContactById(Integer id) {
		log.debug("findContactById: "+id);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		ContactDAO dao = daoFactory.getContactDAO();
		Contact result = dao.findById(id);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}

	public Contact fetchContactById(Integer id, String... dependencies) {
		log.debug("fetchContactById: id="+id);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		ContactDAO dao = daoFactory.getContactDAO();
		Contact result = dao.fetchById(id, dependencies);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}
	
	public List<Contact> listContact() {
		log.debug("listContact...");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		ContactDAO dao = daoFactory.getContactDAO();
		List<Contact> resultList = dao.findAll();
		for(Contact c : resultList){
			if(c.getProvider()!= null)
				c.getProvider().getShortName();
		}
		
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return resultList;
	}

	public List<Contact> fetchContactList(Contact example,
			String... joinProperties) {
		log.debug("fetchContactList:");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		ContactDAO dao = daoFactory.getContactDAO();
		List<Contact> resultList = dao.fetch(example, joinProperties);
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return resultList;
	}
		
	public void mapContactToXML(String filename) {
		log.debug("mapContactToXml: ");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		ContactDAO dao = daoFactory.getContactDAO();
		dao.toXMLFile(dao.toXML(), filename);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	}


	//----------------- Collection Query Interfaces  ------
	public Collection findCollectionById(Integer id) {
		log.debug("findCollectionById: "+id);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		CollectionDAO dao = daoFactory.getCollectionDAO();
		Collection result = dao.findById(id);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}
	
	public List<Collection> listCollection() {
		log.debug("listCollection...");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		CollectionDAO dao = daoFactory.getCollectionDAO();
		List<Collection> resultList = dao.findAll();
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return resultList;
	}
	
	public CollectionProduct findCollectionProductById(Integer id) {
		log.debug("findCollectionById: "+id);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		CollectionProductDAO dao = daoFactory.getCollectionProductDAO();
		CollectionProduct result = dao.findById(id);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}

	public List<Collection> fetchCollectionList(Collection example,
			String... joinProperties) {
		log.debug("fetchCollectionList:");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		CollectionDAO dao = daoFactory.getCollectionDAO();
		List<Collection> resultList = dao.fetch(example, joinProperties);
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return resultList;
	}

	public Collection fetchCollectionById(Integer id, String... dependencies) {
		log.debug("fetchCollectionById: id="+id);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		CollectionDAO dao = daoFactory.getCollectionDAO();
		Collection result = dao.fetchById(id, dependencies);
		//result.getCollectionProduct().getVisibleFlag();
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}
		
	public void mapCollectionToXML(String filename) {
		log.debug("mapCollectionToXml: ");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		CollectionDAO dao = daoFactory.getCollectionDAO();
		dao.toXMLFile(dao.toXML(), filename);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	}

	public List<CollectionDataset> listCollectionDataset() {
		log.debug("listCollectionDataset: ");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		CollectionDatasetDAO dao = daoFactory.getCollectionDatasetDAO();
		List<CollectionDataset> resultList = dao.findAll();
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return resultList;
	}
	
	//----------------- Collection Element Dictionary Query Interfaces  ------
	public CollectionElementDD findCollectionElementDDById(Integer id) {
		log.debug("findCollectionElementDDById: "+id);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		CollectionElementDDDAO dao = daoFactory.getCollectionElementDDDAO();
		CollectionElementDD result = dao.findById(id);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}

	public CollectionElementDD fetchCollectionElementDDById(Integer id,
			String... dependencies) {
		log.debug("fetchCollectionElementDDById: id="+id);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		CollectionElementDDDAO dao = daoFactory.getCollectionElementDDDAO();
		CollectionElementDD result = dao.fetchById(id, dependencies);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}
	
	public List<CollectionElementDD> listCollectionElementDD() {
		log.debug("listCollectionElementDD: ");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		CollectionElementDDDAO dao = daoFactory.getCollectionElementDDDAO();
		List<CollectionElementDD> resultList = dao.findAll();
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return resultList;
	}

	public List<CollectionElementDD> fetchCollectionElementDDList(
			CollectionElementDD example, String... joinProperties) {
		log.debug("fetchCollectionElementDDList:");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		CollectionElementDDDAO dao = daoFactory.getCollectionElementDDDAO();
		List<CollectionElementDD> resultList = dao.fetch(example, joinProperties);
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return resultList;	}
	
	public void mapCollectionElementDDToXML(String filename) {
		log.debug("mapCollectionElementDDToXml: ");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		CollectionElementDDDAO dao = daoFactory.getCollectionElementDDDAO();
		dao.toXMLFile(dao.toXML(), filename);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	}
	
	//----------------- Native SQL query --------------------
	public List list (String tableName, String... conditions) {
		log.debug("list: "+tableName);
		String sql = "SELECT * FROM " + tableName;
		for (String condition : conditions) sql += condition;
	    return getResultList(sql);		
	}

	public List getResultList (String sql) {
		log.debug("getResultList: "+sql);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		List result = HibernateSessionFactory.getInstance().getCurrentSession()
				.createSQLQuery(sql)
				.list();
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}
	
	//-------------- Derived Query --------------------------
	
	public Integer sizeOfGranuleByDatasetId(Integer datasetId) {
		log.debug("sizeOfGranuleByDatasetId: "+datasetId);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		DatasetDAO dao = daoFactory.getDatasetDAO();
		Integer result = dao.sizeOfJoin(new String[]{ GRANULE_SET }, 
							DATASET+"."+DATASET_ID+"="+datasetId);
		/*
		GranuleDAO dao = daoFactory.getGranuleDAO();
		Integer result = dao.size(new String[]{"granule.dataset.datasetId="+datasetId});
		*/
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}
	
	public Integer sizeOfArchiveByDatasetId(Integer datasetId) {
		log.debug("sizeOfArchiveByDatasetId:"+datasetId);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		GranuleDAO dao = daoFactory.getGranuleDAO();
		Integer result = dao.sizeOfJoin(
									new String[]{ GRANULE_ARCHIVE_SET }, 
									GRANULE+"."+DATASET+"."+DATASET_ID+"="+datasetId);
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;		
	}

	public Integer sizeOfReferenceByDatasetId(Integer datasetId) {
		log.debug("sizeOfReferenceByDatasetId:"+datasetId);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		GranuleDAO dao = daoFactory.getGranuleDAO();
		Integer result = dao.sizeOfJoin(
									new String[]{ GRANULE_REF_SET }, 
									GRANULE+"."+DATASET+"."+DATASET_ID+"="+datasetId);
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;		
	}

	public String fetchArchivePathByGranuleId(Integer granuleID)
	{
		
		String path = null;
		Granule g = this.fetchGranuleById(granuleID);
		if(g == null)
			return null;
		Set<GranuleArchive> gas = g.getGranuleArchiveSet();
		for( GranuleArchive ga : gas)
		{
			path = StringUtility.cleanPaths(g.getRootPath(), g.getRelPath(), ga.getName());
			if(!path.equals(null) || !path.equals(""))
				break;
		}
		path = path.substring(0, path.lastIndexOf("/"));
		return path;
	}
	
	public String fetchArchivePathByGranuleName(String granuleName)
	{
		String path = null;
		Granule g = this.fetchGranuleByName(granuleName);
		path = fetchArchivePathByGranuleId(g.getGranuleId());
		
		//Set<GranuleArchive> gas = g.getGranuleArchiveSet();
		//for( GranuleArchive ga : gas)
		//{
		//	path = ga.getPath();
		//	if(!path.equals(null) || !path.equals(""))
		//		break;
		//}
		//path = path.substring(0, path.lastIndexOf("/"));
		return path;
		
	}
	
	
	public List<GranuleArchive> fetchArchiveByDatasetId(Integer datasetId) {
		log.debug("fetchArchiveByDatasetId: "+datasetId);
		List<GranuleArchive> resultList = new ArrayList<GranuleArchive>();

		/* fetch using example instance (one more query comparing to HQL)
		Granule granuleInstance = new Granule();
		granuleInstance.setDataset(this.findDatasetById(datasetId));
		List<Granule> granuleList = this.fetchGranuleList(granuleInstance, GRANULE_ARCHIVE_SET);
		*/
		/* Fetch using Criteria (one more query comparing to HQL)
		Properties props = new Properties();
		props.put(DATASET, this.findDatasetById(datasetId));
		List<Granule> granuleList = this.fetchGranuleList(props, GRANULE_ARCHIVE_SET);	
		*/
		/* fetch using HQL method */	
		List<Granule> granuleList = this.fetchGranuleList(
								new String[]{ GRANULE_ARCHIVE_SET }, 
								GRANULE+"."+DATASET+"."+DATASET_ID+"="+datasetId);
		
		log.debug("fetchGranuleArchive: sizeOfJoinedResult="+granuleList.size());
		for (Granule g : granuleList) {
			for (GranuleArchive archive : g.getGranuleArchiveSet()) {
				archive.setGranuleId(g.getGranuleId());
				resultList.add(archive);
			}
		}
		
		/* Native SQL Query (note that will have to construct columns into object)
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		String sql = "select arc.* from GRANULE g, GRANULE_ARCHIVE arc where "+
					"(g.Granule_ID=arc.GRANULE_ID) and (g.DATASET_ID="+datasetId+")";
		List it = 
				 HibernateSessionFactory.getInstance().getCurrentSession()
				.createSQLQuery(sql)
				.list();
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
		*/	
	    return resultList;
	}
	
	public List<GranuleReference> fetchReferenceByDatasetId(Integer datasetId) {
		log.debug("fetchReferenceByDatasetId: "+datasetId);
		
		/* Native query 
		long time = System.currentTimeMillis();
		
		//String sql = "SELECT gr.granule_id, gr.path, gr.type, gr.status "
		/*
		String sql = "SELECT * "
			+" FROM granule g inner join granule_reference gr on "
			+ " g.granule_id=gr.granule_id "
			+ " where g.dataset_id="+datasetId
			;//+" and (select count(gr.granule_id) from granule_reference gr2 "
			//+" WHERE g.granule_id=gr2.granule_id)>0";
		Iterator results = getResultList(sql).iterator();
		List<GranuleReference> refList = new ArrayList<GranuleReference>();
		
		while ( results.hasNext() ) { 
			Object[] row = (Object[]) results.next();
			GranuleReference ref = new GranuleReference();
			for (int i=0; i<row.length; i++) {
				ref.setGranuleId(((BigDecimal)(row[0])).intValue());
				ref.setPath((String) row[1]);
				ref.setType((String) row[2]);
				ref.setStatus((String) row[3]);
			}
			refList.add(ref);
		}		
		log.debug("Time="+(System.currentTimeMillis() - time));			
		log.debug("sizeOfResult="+refList.size());
		*/
		
		// fetch using HQL method
		List<GranuleReference> resultList = new ArrayList<GranuleReference>();
		long time = System.currentTimeMillis();		
		
		String sql = "Select * from granule_reference where granule_id in (select granule_id from granule where dataset_id="+datasetId+") order by granule_id desc";
//		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
//		log.debug("new method...");
//		resultList = (List<GranuleReference>) HibernateSessionFactory.getInstance().getCurrentSession().createSQLQuery(sql).addEntity(GranuleReference.class).list();
//		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
		Iterator results = getResultList(sql).iterator();
		while ( results.hasNext() ) { 
			Object[] row = (Object[]) results.next();
			GranuleReference ref = new GranuleReference();
			for (int i=0; i<row.length; i++) {
				ref.setGranuleId(((BigDecimal)(row[0])).intValue());
				ref.setPath((String) row[1]);
				ref.setType((String) row[2]);
				ref.setStatus((String) row[3]);
			}
			resultList.add(ref);
		}
//		List<Granule> granuleList = this.fetchGranuleList(
//								new String[]{ GRANULE_REF_SET },
//								GRANULE+"."+DATASET+"."+DATASET_ID+"="+datasetId);
//
		log.debug("Time="+(System.currentTimeMillis() - time));
//		log.debug("fetchGranuleReference: sizeOfGranule="+granuleList.size());
//		int count = 0;
//		for (Granule g : granuleList) {
//			Set<GranuleReference> refSet = g.getGranuleReferenceSet();
//			count += refSet.size();
//			for (GranuleReference ref : refSet) {
//				ref.setGranuleId(g.getGranuleId());
//				resultList.add(ref);
//			}
//		}
		log.debug("fetchGranuleReference: "+resultList.size()+":sizeOfJoinedResult="+resultList.size());
	    return resultList;
	}

	public Granule findGranule(String granuleName, Dataset dataset) {
		log.debug("findGranule: granuleName="+granuleName+" datasetId="+dataset.getDatasetId());
		//Granule granuleInstance = new Granule();
		//granuleInstance.setName(granuleName);
//		List<Granule> resultList = this.fetchGranuleList(new String[]{ GRANULE_ARCHIVE_SET },GRANULE+"."+GRANULE_NAME+"='"+granuleName+"'", 
//				GRANULE+"."+DATASET+"."+DATASET_ID+"="+dataset.getDatasetId());
		
		
		List<Granule> resultList = this.fetchGranuleList(new String[] {},GRANULE+"."+GRANULE_NAME+"='"+granuleName+"'", 
				GRANULE+"."+DATASET+"."+DATASET_ID+"="+dataset.getDatasetId());
		if((resultList==null) || resultList.isEmpty() || resultList.size()<=0) {
			return null;
		}
		if (resultList.size()>1)
			log.debug("Granule name="+granuleName
					+" is not unique within its dataset (id="+dataset.getDatasetId()+")");
		return resultList.get(0);
	}
	
	/*
	 * Updates granule status to value supplied for Granule, Granule_Archive, and Granule_Reference tables
	 * (non-Javadoc)
	 * @see gov.nasa.podaac.inventory.api.Query#updateGranuleStatusByID(java.lang.Integer, gov.nasa.podaac.inventory.api.Constant.GranuleStatus)
	 */
	public void updateGranuleStatusByID(Integer id, GranuleStatus gs) {
		log.debug("updateGranuleStatusByID: granuleId="+id+" GranuleStatus="+gs.toString());
		Granule g = this.fetchGranuleById(id);

		g.setStatus(gs);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
			GranuleDAO dao = daoFactory.getGranuleDAO();
			
			Set<GranuleArchive> GAS = g.getGranuleArchiveSet();
			Set<GranuleReference> GAR = g.getGranuleReferenceSet();
			
			Set<GranuleReference> granref = new HashSet<GranuleReference>();
			Set<GranuleArchive> granarch = new HashSet<GranuleArchive>();
			
			for(GranuleArchive ga : GAS)
			{   
				ga.setStatus(gs.getID());
				granarch.add(ga);
			}
			
			for(GranuleReference gr : GAR)
			{
				gr.setStatus(gs.getID());
				granref.add(gr);
			}
			
//			for(GranuleSpatial gr : g.getGranuleSpatialSet())
//			{
//				System.out.println("Granule Spaital");
//			}
			g.setGranuleReferenceSet(granref);
			g.setGranuleArchiveSet(granarch);
			dao.update(g);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	
//		Session session = HibernateSessionFactory.getInstance().getCurrentSession();
//		session.beginTransaction();
//		if (session.createSQLQuery(
//				"UPDATE granule_archive SET status='"
//				+gs.toString()+"'"
//				+" WHERE "
//				+" granule_id="+id)
//				.executeUpdate()==0)
//			log.debug("granule status not updated");
//		if (session.createSQLQuery(
//				"UPDATE granule_reference SET status='"
//				+gs.toString()+"'"
//				+" WHERE "
//				+" granule_id="+id)
//				.executeUpdate()==0)
//			log.debug("granule status not updated");
//		
//		session.getTransaction().commit(); // commit also closes session	
	}
	
	/*
	 * Fetches a Dataset Policy by the ID
	 * 
	 * 
	 */
	public DatasetPolicy fetchDatasetPolicyById(Integer id, String... dependencies) {
		log.debug("fetchDatasetPolicyById: id="+id);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		DatasetPolicyDAO dao = daoFactory.getDatasetPolicyDAO();
		DatasetPolicy result = dao.fetchById(id, dependencies);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}
	
	/**
	 * Fetches a Dataset Coverage by the ID
	 */
	public DatasetCoverage fetchDatasetCoverageById(Integer id, String... dependencies) {
		log.debug("fetchDatasetCoverageById: id="+id);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		DatasetCoverageDAO dao = daoFactory.getDatasetCoverageDAO();
		DatasetCoverage result = dao.fetchById(id, dependencies);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}

	public void updateGranuleEchoSubmitTime(GranuleMetaHistory gmh) {
		log.debug("Entering updateGranuleEchoSubmitTime.");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		
		gmh.setEchoSubmitDateLong(new Date().getTime());
		GranuleMetaHistoryDAO dao = daoFactory.getGranuleMetaHistoryDAO();
		dao.update(gmh);
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
		return;
	}

	public void updateCollectionProduct(CollectionProduct collection) {
		log.debug("Entering updateCollection.");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		CollectionProductDAO dao = daoFactory.getCollectionProductDAO();
		dao.update(collection);
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
		return;
		
	}

	public Project fetchProjectById(int id) {
		log.debug("fetchProviderById: id="+id);
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		ProjectDAO dao = daoFactory.getProjectDAO();
		Project result = dao.fetchById(id);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}

	public void addReference(Integer granule, String type, String status,
			String Description, String path) throws InventoryException {
		Granule g = this.findGranuleByIdDeep(granule);
		boolean found = false;
		for(GranuleReference gr : g.getGranuleReferenceSet()){
			
			if(gr.getPath().equals(path)){
				gr.setStatus("ONLINE");
				found = true;
			}
		}
		if(!found){
			GranuleReference ref = new GranuleReference();
			ref.setType(type);
			ref.setDescription(Description);
			ref.setGranuleId(granule);
			ref.setPath(path);
			ref.setStatus(status);
			g.getGranuleReferenceSet().add(ref);
		}
		//update granule
		
		DataManager manager = DataManagerFactory.getInstance().createDataManager();
		manager.addGranule(g);
		
	}

	public void deleteGranule(Integer granule)
			throws InventoryException {
		//this is onyl done when the metadata is to be deleted.
		log.debug("Deleting granule["+granule+"] from Inventory.");
		Granule g = this.fetchGranuleByIdDeep(granule);
		DataManager manager = DataManagerFactory.getInstance().createDataManager();
		manager.deleteGranule(g);

	}

	public void removeReference(Integer granule, String type)
			throws InventoryException {
		
		Granule g = this.fetchGranuleByIdDeep(granule);
		Set<GranuleReference> newSet = new HashSet<GranuleReference>(
				g.getGranuleReferenceSet()
				);
		Set<GranuleReference> currentSet = g.getGranuleReferenceSet();	
		for (GranuleReference ref : currentSet) {
			if (ref.getType().startsWith(type)) newSet.remove(ref);
		}
		g.setGranuleReferenceSet(newSet);
		
		DataManager manager = DataManagerFactory.getInstance().createDataManager();
		manager.addGranule(g);
		
	}

	public void updateAIPArchiveStatus(Integer granule, String type,
			String status, String name) throws InventoryException {
		
		Session session = HibernateSessionFactory.getInstance().getCurrentSession();
		session.beginTransaction();
		
			if (session.createSQLQuery(
					"update granule_archive set status='"+ status+"'"
					+" where "
					+" granule_id="+granule
					+" and name LIKE '"+name+"'"
					)
					.executeUpdate()==0)
				log.debug("granuleId="+granule+"-->archive status not updated");
			if (type.equals(GranuleArchiveType.DATA.toString())) {
				if (session.createSQLQuery(
					"update granule_reference set status='"+ status+"'"
					+" where "
					+" granule_id="+granule
					+" and path LIKE '%"+ name +"%'")
					.executeUpdate()==0)
				log.debug("granuleId="+granule+"-->reference status not updated");
			}
	    session.getTransaction().commit(); // commit also closes session
	}

	public void updateAIPGranuleArchiveStatus(Integer granule, String status,
			String name) throws InventoryException {
		Session session = HibernateSessionFactory.getInstance().getCurrentSession();
		session.beginTransaction();
			if (session.createSQLQuery(
					"UPDATE granule_archive SET status='"+ status+"'"
					+" WHERE "
					+" granule_id="+granule
					+" AND name LIKE '"+name+"'"
					)
					.executeUpdate()==0)
				log.debug("granuleId="+granule+"-->archive status not updated");
			if (session.createSQLQuery(
					"UPDATE granule SET verify_time_long=" + new Date().getTime() + ""
					+" WHERE "
					+" granule_id = "+granule)
					.executeUpdate()==0)
				log.debug("granuleId="+granule+"-->granule verify time not updated");
	    session.getTransaction().commit(); // commit also closes session
	}

	public void updateAIPGranuleReferenceStatus(Integer granule, String status,
			String path) throws InventoryException {
		Session session = HibernateSessionFactory.getInstance().getCurrentSession();
		session.beginTransaction();
				if (session.createSQLQuery(
					"UPDATE granule_reference SET status='"+ status+"'"
					+" WHERE "
					+" granule_id="+granule
					+" AND path LIKE '%"+
					path + "%'"
					)
					.executeUpdate()==0)
				log.debug("granuleId="+granule+"-->reference status not updated");
	    session.getTransaction().commit(); // commit also closes session
		
	}

	
	public List<String> updateGranule(Granule incoming){
		Granule original = this.fetchGranuleByIdDeep(incoming.getGranuleId(),new String[] {});
		return updateGranule(original, incoming);
	}
	
	public List<String> updateGranule(Granule original, Granule incoming){
		List<String> ll = original.updateGranule(incoming);
		DataManager dm = new DataManagerImpl();
		dm.updateGranule(original);
		return ll;
	}
	
	public void updateGranuleArchiveChecksum(Integer granuleId, String name,
			String sum) throws InventoryException {
		Session session = HibernateSessionFactory.getInstance().getCurrentSession();
		session.beginTransaction();
		if (session.createSQLQuery(
				"UPDATE granule_archive SET checksum='"+sum+"'"
				+" WHERE "
				+" granule_id="+granuleId
				+" AND "
				+" name='"+name+"'"
				)
				.executeUpdate()==0)
			log.debug("granuleId="+granuleId+"-->archive status not updated");
		session.getTransaction().commit(); // commit also closes session
		
	}

	public void updateGranuleArchiveSize(Integer granuleId, String name,
			Long Size) throws InventoryException {
		log.debug("updateGranuleArchiveStatus "+granuleId);
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

	public void updateGranuleArchiveStatus(Integer granuleId, String status)
			throws InventoryException {
		log.debug("updateGranuleArchiveStatus "+granuleId);
		Granule g = this.fetchGranuleById(granuleId);
		Dataset d = this.fetchDatasetById(g.getDataset().getDatasetId(),"datasetPolicy");
		
		
		Session session = HibernateSessionFactory.getInstance().getCurrentSession();
		session.beginTransaction();
		
		if(status.equals("ONLINE")){
			//get dataset based on granule_id
//			if(g.getOfficialName() != null){
//				log.debug("Versioned granule...");
//				if(d.getDatasetPolicy().getVersionPolicy().equals(VersionPolicy.LATEST.toString())){
//					log.debug("VersionPolicy is  LATEST -- offlining previous granules.");
//					if (session.createSQLQuery(
//							"UPDATE granule SET status='OFFLINE'"
//							+" WHERE "
//							+" official_name='"+g.getOfficialName()+"' and granule_Id !=" +granuleId
//							)
//							.executeUpdate()==0){
//						log.debug("Could not update offical name status");
//					}
//					if (session.createSQLQuery(
//							"UPDATE granule_archive SET status='OFFLINE'"
//							+" WHERE granule_id in ( select granule_id from granule where "
//							+" official_name='"+g.getOfficialName()+"' and granule_Id !=" +granuleId +")"
//							)
//							.executeUpdate()==0){
//						log.debug("Could not update offical name status");
//					}
//				}
//				else{
//					log.debug("VersionPolicy is  ALL -- no action taken for previous granules.");
//				}
//			}
		}
		if (session.createSQLQuery(
				"UPDATE granule_archive SET status='"+status+"'"
				+" WHERE "
				+" granule_id="+granuleId
				)
				.executeUpdate()==0)
			log.debug("granuleId="+granuleId+"--> status not updated");
		session.getTransaction().commit(); // commit also closes session
		
	}


	public void updateVerifyGranuleStatus(Integer granule,
			String status) throws InventoryException {
		Session session = HibernateSessionFactory.getInstance().getCurrentSession();
		session.beginTransaction();
		
		//String verifyTime = formatter.format(new Date());
		
			if (session.createSQLQuery(
					"UPDATE granule SET status='" + status + "'"
					+", verify_time_long=" + new Date().getTime() + ""
					+" WHERE "
					+" granule_id = "+granule)
					.executeUpdate()==0)
				log.debug("granule status not updated");
			else log.debug("granule status set "+status);	
	    session.getTransaction().commit(); // commit also closes session		
		
	}

	public void updateGranuleLocation(Integer granule,
			 String baseLocation)
			throws InventoryException {
		Granule g = this.fetchGranuleByIdDeep(granule);
		g.setRootPath(baseLocation);
		DataManager manager = DataManagerFactory.getInstance().createDataManager();
		manager.addGranule(g);
		
	}

	public void updateGranuleStatus(Integer granule,
			String status)
			throws InventoryException {
		Session session = HibernateSessionFactory.getInstance().getCurrentSession();
		session.beginTransaction();
		if (session.createSQLQuery(
				"UPDATE granule SET status='"
				+status+"'"
				+" WHERE "
				+" granule_id="+granule)
				.executeUpdate()==0)
			log.debug("granule status not updated");
		session.getTransaction().commit(); // commit also closes session	
		
	}

	public Dataset findDatasetByPersistentId(String persistentId) {
		log.debug("Entering findDatasetByPersistentId.");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		DatasetDAO dao = daoFactory.getDatasetDAO();
		List<Dataset> dList = dao.findByPropertyName("persistentId", persistentId);
		for(Dataset d: dList){
			d.getDatasetPolicy().getAccessType();
			for(DatasetLocationPolicy dlp: d.getLocationPolicySet()){
				dlp.getBasePath();
			}
			for(DatasetElement de : d.getDatasetElementSet()){
				de.getElementDD().getElementId();
			}
		}
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
		if(dList.size() == 0)
			return null;
		else
			return dList.get(0);
	}

	public Integer findDatasetIdByPersistentId(String persistentId) {
		log.debug("Entering findDatasetIdByPersistentId.");
		Dataset d = findDatasetByPersistentId(persistentId);
		if(d == null)
			return null;
		else
			return d.getDatasetId();
	}

	public String findPersistentIdByDatasetId(Integer datasetId) {
		Dataset d = this.findDatasetById(datasetId);
		if(d==null)
			return null;
		else
			return d.getPersistentId();
	}
	
	@SuppressWarnings("unchecked")
	public  List<Integer> searchGranuleSpatial(final int datasetId,final double lat1,final double lon1,final double lat2,final double lon2){
        //return (List) getHibernateTemplate().execute(new HibernateCallback() {
        //      public Object doInHibernate(Session session) throws HibernateException, SQLException
        //      {
				Session session = HibernateSessionFactory.getInstance().getCurrentSession();
				session.beginTransaction();
//                  org.hibernate.Query q = session.getNamedQuery("searchGranuleSpatial_SP");
//                  q.setInteger("datasetId", datasetId);
//                  q.setDouble("lat1", lat1 );
//                  q.setDouble("lon1", lon1 );
//                  q.setDouble("lat2", lat2 );
//                  q.setDouble("lon2", lon2 );
//                  
//                  List<GranuleSpatial> l = q.list(); 
//                  for(GranuleSpatial gs:l){
//                	  gs.getGranule().getGranuleId();
//                  }
//                
				final List<Integer> list = new LinkedList<Integer>();
				session.doWork(new Work() {
					  public void execute(Connection connection) throws SQLException {
					    CallableStatement call = connection.prepareCall(" begin INVENTORY.searchGranuleSpatial(?,?,?,?,?,?); end;");
					    
					    call.setInt(1, datasetId);
					    call.setDouble(2, lat1);
					    call.setDouble(3, lon1);
					    call.setDouble(4, lat2);
					    call.setDouble(5, lon2);
					    call.registerOutParameter( 6, OracleTypes.CURSOR ); // or whatever it is
					    call.execute();
					   //call.getResultSet()
					    ResultSet rs = (ResultSet)call.getObject(6);
					    while (rs.next()) {
					    	list.add(rs.getInt(1));
					    }
					    rs.close();
					  }
					});
				
				session.getTransaction().commit();
				
                  return list;
         //     }
         // });
      }

	
	
}
