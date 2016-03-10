package gov.nasa.podaac.inventory.api;


import gov.nasa.podaac.common.api.metadatamanifest.MetadataManifest;
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfile;
import gov.nasa.podaac.inventory.exceptions.InventoryException;
import gov.nasa.podaac.inventory.model.Collection;
import gov.nasa.podaac.inventory.model.Contact;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.DatasetCoverage;
import gov.nasa.podaac.inventory.model.DatasetPolicy;
import gov.nasa.podaac.inventory.model.ElementDD;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.GranuleReference;
import gov.nasa.podaac.inventory.model.GranuleSpatial;
import gov.nasa.podaac.inventory.model.Provider;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Calendar;
import java.util.Map;
import java.util.List;

public interface Service {

	//Dataset
	public Dataset getDataset(String id) throws InventoryException;
	public Dataset getDataset(Integer dId)throws InventoryException;
	
	public DatasetCoverage getDatasetCoverage(String id) throws InventoryException;
	public DatasetCoverage getDatasetCoverage(Integer id) throws InventoryException;
	
	public DatasetPolicy getDatasetPolicy(String id) throws InventoryException;
	public DatasetPolicy getDatasetPolicy(Integer id) throws InventoryException;
	
	public List<Map<String,String>> getDatasets() throws InventoryException; 
		
	public Integer getLatestGranuleIdByDataset(Integer id) throws InventoryException;
	public Integer getLatestGranuleIdByDataset(String id) throws InventoryException;
	
	//Provider
	public Provider getProvider(Integer id) throws InventoryException;
	public Provider getProvider(String id) throws InventoryException;
	public List<Map<String,String>> getProviders() throws InventoryException;
	
	//Granule
	public Granule getGranuleById(int id) throws InventoryException;
	
	public Granule getGranuleByNameAndDataset(String id, int datasetId) throws InventoryException;
	public Granule getGranuleByNameAndDataset(String id, String datasetId) throws InventoryException;

	public boolean updateGranuleEcho(Granule g, long time ) throws InventoryException;
 	public boolean updateGranuleEcho(Granule g, long time, String user, String pass ) throws InventoryException;

	
	public String getGranuleArchivePath(Integer id) throws InventoryException;
	public String getGranuleArchivePath(String id) throws InventoryException;
	
	public Granule getGranuleByName(String id) throws InventoryException;
	
	public boolean updateGranule(Granule g) throws InventoryException;
	public boolean updateGranule(Granule g, String userName, String pass) throws InventoryException;
	
	public boolean updateGranuleStatus(int granuleId, String status) throws InventoryException;
	public boolean updateGranuleStatus(int granuleId, String status, String userName, String password) throws InventoryException;
	
	//Service Profile
	public ServiceProfile ingestSip(ServiceProfile sip) throws InventoryException;
	public ServiceProfile ingestSip(ServiceProfile sip, String user, String pass) throws InventoryException;
	
	//Manifest
	public MetadataManifest processManifest(MetadataManifest mf) throws InventoryException;
	public MetadataManifest processManifest(MetadataManifest mf, String user, String pass) throws InventoryException;
	
	public boolean isOnline();
	public void setAuthInfo(String u, String pass);
	
	public List<Integer> getGranulesByDatasetAndPattern(Integer datasetId,String gnp);
	public List<Integer> getGranulesByDatasetAndPattern(String datasetId,String gnp);
	
	public Map<String,Object> getGranuleReferences(Integer datasetId, Integer page) throws InventoryException;
	public Map<String,Object> getGranuleReferences(String datasetId, Integer page) throws InventoryException;
	
	public List<GranuleReference> getGranuleAllReferences(String datasetId) throws InventoryException;
	public List<GranuleReference> getGranuleAllReferences(Integer datasetId) throws InventoryException;
	
	public List<Integer> findGranuleList(List<Integer> idList) throws InventoryException;
	public List<Integer> getGranuleIdListAll(Integer datasetId, Calendar start,
			Calendar stop,String pattern) throws InventoryException;
	public Map<String,Object> getGranuleIdList(Integer datasetId, Calendar start,
			Calendar stop, String pattern, Integer page) throws InventoryException;
	public Integer getGranuleSize(Integer datasetId) throws InventoryException;
	
	public boolean deleteGranule(Granule granule, boolean dataOnly) throws InventoryException;
	public boolean updateGranuleAIPReference(Integer granuleId, String type,
			String destination, String name, String status);
	public boolean updateGranuleAIPArchive(Integer granuleId, String type,
			String destination, String name, String status);
	
	//tested
	public boolean updateGranuleArchiveChecksum(Integer gId, String name, String cSum) throws InventoryException;
	public boolean updateGranuleArchiveStatus(Integer gId, String status) throws InventoryException;
	public boolean updateGranuleArchiveSize(Integer gId, String name, Long size) throws InventoryException;
	public boolean updateStatusAndVerify(Integer gId, String status) throws InventoryException;
	public boolean updateGranuleRootPath(Integer gId, String path) throws InventoryException;
	public boolean deleteGranuleLocalReference(Integer gId, String type) throws InventoryException;
	public boolean updateGranuleReferenceStatus(Integer gId, String path, String status) throws InventoryException;
	public boolean addGranuleReference(Integer gId, String path, String status, String type, String desc) throws InventoryException;
	public boolean updateGranuleReferencePath(Integer gId, String path, String newPath) throws InventoryException;
	
	public boolean updateGranuleReassociate(Integer gId, String rootpath, Integer dataset, String accessType) throws InventoryException;
	public boolean updateGranuleReassociate(Integer gId, String rootpath, String dataset, String accessType ) throws InventoryException;
	
	public boolean updateGranuleReassociateElement(Integer gId, Integer fromD, Integer toD) throws InventoryException;
	
	//New Additions for 4.0.0 distribute
	public Collection getCollectionById(Integer id) throws InventoryException;
	public Collection getCollectionById(String id) throws InventoryException;
	public List<Collection> listCollections() throws InventoryException;
	public List<Integer> getGranuleIdListByDateRange(long begin, long stop,
			String timefield) throws InventoryException;
	public List<Integer>getGranuleIdListByDateRange(long begin, long stop,
			String timefield, Integer dsId) throws InventoryException;
	public Contact getContactByID(Integer contactId) throws InventoryException;
	public ElementDD getElementDDById(Integer elementId) throws InventoryException;
	public ElementDD getElementDDByShortName(String elementId) throws InventoryException;
	public GranuleSpatial getSpatial(Integer id) throws InventoryException;
	public Collection getCollectionByDataset(Integer datasetId) throws InventoryException;
	public List<BigDecimal> getEchoGranulesByDataset(Integer datasetId) throws InventoryException;
	public List<BigDecimal> getEchoGranulesByDataset(String datasetId) throws InventoryException;
	
}
