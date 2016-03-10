// Copyright 2007, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.api;

import gov.nasa.podaac.inventory.api.Constant.GranuleStatus;
import gov.nasa.podaac.inventory.exceptions.InventoryException;
import gov.nasa.podaac.inventory.model.Collection;
import gov.nasa.podaac.inventory.model.CollectionDataset;
import gov.nasa.podaac.inventory.model.CollectionElementDD;
import gov.nasa.podaac.inventory.model.CollectionProduct;
import gov.nasa.podaac.inventory.model.Contact;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.DatasetCoverage;
import gov.nasa.podaac.inventory.model.DatasetPolicy;
import gov.nasa.podaac.inventory.model.DatasetReal;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.GranuleArchive;
import gov.nasa.podaac.inventory.model.ElementDD;
import gov.nasa.podaac.inventory.model.GranuleMetaHistory;
import gov.nasa.podaac.inventory.model.GranuleReference;
import gov.nasa.podaac.inventory.model.GranuleSpatial;
import gov.nasa.podaac.inventory.model.MetadataManifest;
import gov.nasa.podaac.inventory.model.Project;
import gov.nasa.podaac.inventory.model.Provider;
import gov.nasa.podaac.inventory.model.Sensor;
import gov.nasa.podaac.inventory.model.Source;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author clwong
 * $Id: Query.java 13176 2014-04-03 18:56:47Z gangl $
 */
public interface Query {

	//-------WSM implementables
	
	public void evictObject(Object d);
	//delete a granule based on ID
	public void deleteGranule(Integer granule ) throws InventoryException;
	public void updateAIPArchiveStatus (Integer granule, String type, String status, String name) throws InventoryException;
	public void updateAIPGranuleArchiveStatus (Integer granule, String status, String name) throws InventoryException;
	public void updateAIPGranuleReferenceStatus (Integer granule, String status, String path) throws InventoryException;
	public void updateGranuleArchiveChecksum(Integer granuleId,String name, String sum) throws InventoryException;
	public void updateGranuleArchiveSize (Integer granuleId,String name, Long Size) throws InventoryException;
	public void updateGranuleArchiveStatus (Integer granuleId, String status) throws InventoryException;
	public void updateGranuleStatus (Integer granule, String status) throws InventoryException;
	public void updateGranuleLocation (Integer granule, String baseLocation) throws InventoryException;
	//remove reference to a granule
	public void removeReference(Integer granule, String type) throws InventoryException;
	//add reference to a granule
    public void addReference(Integer granule, String type, String status, String Description, String path) throws InventoryException;
    public void updateVerifyGranuleStatus (Integer granule, String status) throws InventoryException;
	
	
	//Other
	public void saveMetadataManifest(MetadataManifest mf) throws InventoryException;
	//---------------- Granule Query Interfaces ------------------
	public Granule findGranuleById(Integer id);
	public void addReal(int datasetId, int deId, double value, String units);
	
	public Granule findGranuleByIdDeep(Integer id);
	
	public List<BigDecimal>listLatestGranuleIdsByDatasetID(int datasetId);
	
	public int fetchLatestGranuleIdByDatasetID(int datasetId);

	public Granule fetchGranuleByIdDeep(Integer id, String... dependencies); 	
	public Granule fetchGranuleById(Integer id, String... dependencies);
	
	public Granule findGranuleByName(String name);
	
	public List<Integer> listGranuleId(String...whereClause);
	
	public List<Integer> listGranuleIdByProperty (String propertyName, Object value);

	public List<Granule> listGranule();
	
	public Dataset findDatasetByShortNameDeep(String shortName);
	
	
	
	public Dataset findDatasetByPersistentId(String persistentId);
	public Integer findDatasetIdByPersistentId(String persistentId);
	public String findPersistentIdByDatasetId(Integer datasetId);
	
	
	public List<Granule> listGranuleByDateRange(long start, long stop, String timeField);
	public List<Granule> listGranuleByDateRange(long start, long stop, String timeField, int dsId);
	public List<Granule> listGranuleByDateRangeWithDelete(long start, long stop, String timeField);

	public List<Granule> listGranuleByProperty(String propertyName, Object value);
	
	public List<Granule> listGranuleByIngestTime(Date startTime, Date endTime);
	
	public List<Granule> fetchGranuleList(String[]joinProperties, String...whereClause);
	
	public List<Granule> fetchGranuleList(Granule example, String...joinProperties);
	
	public List<Granule> fetchGranuleList(Properties properties, String...joinProperties);
	
	public void mapGranuleToXML(String filename);
	
	public void updateGranuleStatusByID(Integer id, GranuleStatus gs);
	
	public void updateGranuleEchoSubmitTime(GranuleMetaHistory granule);

	//---------------- Granule Element Dictionary Query Interfaces  ------------------
	public Map<String, ElementDD> getGranuleElementShortNames();
	
	public void setGranuleElementShortNames(
			Map<String, ElementDD> granuleElementShortNames);
	
	public void initGranuleElementShortNames();
	
	public void resetGranuleElementShortNames();
	
	public ElementDD findElementDDByShortName(String name);
	
	public ElementDD findElementDDById(Integer id);
	
	public ElementDD fetchElementDDById(Integer id, String... dependencies);
	
	public List<ElementDD> listElementDD();
	
	public List<ElementDD> listElementDDByProperty(String propertyName, Object value);
	
	public List<ElementDD> fetchElementDDList(ElementDD example, String...joinProperties);
	
	public void mapElementDDToXML(String filename);
	
	//---------------- Dataset Query Interfaces  ------------------
	public Dataset findDatasetById(Integer id);
	
	public Dataset fetchDatasetById(Integer id, String... dependencies);
	
	public Dataset findDatasetByShortName(String name);
	public List<Dataset> listDatasets();
	public List<Integer> listDatasetId(String...whereClause);
	
	public Dataset fetchDepthDatasetById(Integer id);
		
	public List<Integer> listDatasetIdByProperty(String propertyName, Object value);
	
	public List<Dataset> listDataset();
	
	public List<Dataset> listDatasetByProperty(String propertyName, Object value);
	
	public List<Dataset> fetchDatasetList(String[]joinProperties, String...whereClause);
	
	public List<Dataset> fetchDatasetList(Dataset example, String...joinProperties);
	
	public List<Dataset> fetchDatasetList(Properties properties, String...joinProperties);
	
	public void mapDatasetToXML(String filename);
	public List<Dataset> fetchNonSpatialDatasets();
	
	//------------------- Source Query Interfaces  ------
	public Source findSourceById(Integer id);
	
	public Source fetchSourceById(Integer id, String... dependencies);
	
	public List<Source> listSource();
	
	public List<Source> fetchSourceList(Source example, String...joinProperties);
	
	public void mapSourceToXML(String filename);

	//----------------- Sensor Query Interfaces  ------
	public Sensor findSensorById(Integer id);
	
	public Sensor fetchSensorById(Integer id, String... dependencies);
	
	public List<Sensor> listSensor();
	
	public List<Sensor> fetchSensorList(Sensor example, String...joinProperties);
	
	public void mapSensorToXML(String filename);
	
	//----------------- Project Query Interfaces   ------
	public List<Project> listProject();
	
	//----------------- Provider Query Interfaces  ------
	public Provider findProviderById(Integer id);

	public Provider fetchProviderById(Integer id, String... dependencies);
	
	public Provider findProviderByShortName(String name);
	
	public List<Integer> listProviderId(String...whereClause);
		
	public List<Integer> listProviderIdByProperty(String propertyName, Object value);

	public List<Provider> listProvider();

	public List<Provider> listProviderByProperty(String propertyName, Object value);
	
	public List<Provider> fetchProviderList(String[]joinProperties, String...whereClause);
	
	public List<Provider> fetchProviderList(Provider example, String...joinProperties);
	
	public List<Provider> fetchProviderList(Properties properties, String...joinProperties);
		
	public void mapProviderToXML(String filename);
	
	//----------------- Contact Query Interfaces  ------
	public Contact findContactById(Integer id);
	
	public Contact fetchContactById(Integer id, String... dependencies);
	
	public List<Contact> listContact();
	
	public List<Contact> fetchContactList(Contact example, String...joinProperties);
	
	public void mapContactToXML(String filename);

	//----------------- Collection Query Interfaces  ------
	public Collection findCollectionById(Integer id);
	
	public CollectionProduct findCollectionProductById(Integer id);
	
	public Collection fetchCollectionById(Integer id, String... dependencies);
	
	public List<Collection> listCollection();
	
	public List<Collection> fetchCollectionList(Collection example, String...joinProperties);
	
	public void mapCollectionToXML(String filename);
	
	public List<CollectionDataset> listCollectionDataset();
	
	public void updateCollectionProduct(CollectionProduct collection);
	
	//----------------- Collection Element Dictionary Query Interfaces  ------
	public CollectionElementDD findCollectionElementDDById(Integer id);
	
	public CollectionElementDD fetchCollectionElementDDById(Integer id, String... dependencies);
		
	public List<CollectionElementDD> listCollectionElementDD();
	
	public List<CollectionElementDD> fetchCollectionElementDDList(CollectionElementDD example, String...joinProperties);

	public void mapCollectionElementDDToXML(String filename);
	
	//----------------- Native SQL query --------------------
	public List list (String tableName, String...conditions);
	
	public List getResultList(String sql);
	
	public void runQuery(String sql);

	//----------------- Derived Query ------------------------
	public Integer sizeOfGranuleByDatasetId(Integer datasetId);
	
	public Integer sizeOfArchiveByDatasetId(Integer datasetId);
	
	public Integer sizeOfReferenceByDatasetId(Integer datasetId);
	
	public String fetchArchivePathByGranuleId(Integer granuleId);
	public String fetchArchivePathByGranuleName(String granuleName);
	
	public List<GranuleArchive> fetchArchiveByDatasetId(Integer datasetId);	

	public List<GranuleReference> fetchReferenceByDatasetId(Integer datasetId);
	
	public Granule findGranule (String granuleName, Dataset dataset);
	
	//------------------ Dataset Policy query ----------------
	public DatasetPolicy fetchDatasetPolicyById(Integer id, String... dependencies);
	
	//------------------ Dataset Coverage query ----------------
	public DatasetCoverage fetchDatasetCoverageById(Integer id, String... dependencies);
	public Dataset fetchDatasetByIdFull(int id, String ...dependencies);
	public void addInteger(Integer datasetId, Integer deId, Integer value,
			String units);
	public void addChars(Integer datasetId, Integer deId, String value);
	public void addDateTime(Integer datasetId, Integer deId, Long long1);
	public Project fetchProjectById(int id);
	public  List<Integer> searchGranuleSpatial(int datasetId, double lat1, double lon1, double lat2, double lon2);
//	public List<String> updateGranule(Granule original, Granule incoming);
//	public List<String> updateGranule(Granule g);
	public List<Integer> getDatasetIds(Integer dId, String dids);
	public boolean deiIdIsUsed(Integer did);

}
