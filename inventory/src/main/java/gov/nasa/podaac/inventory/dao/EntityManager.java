// Copyright 2007, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.dao;

import gov.nasa.podaac.inventory.model.Collection;
import gov.nasa.podaac.inventory.model.CollectionContact;
import gov.nasa.podaac.inventory.model.CollectionDataset;
import gov.nasa.podaac.inventory.model.CollectionElementDD;
import gov.nasa.podaac.inventory.model.CollectionProduct;
import gov.nasa.podaac.inventory.model.Contact;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.DatasetCoverage;
import gov.nasa.podaac.inventory.model.DatasetElement;
import gov.nasa.podaac.inventory.model.DatasetPolicy;
import gov.nasa.podaac.inventory.model.DatasetReal;
import gov.nasa.podaac.inventory.model.DatasetSpatial;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.GranuleElement;
import gov.nasa.podaac.inventory.model.ElementDD;
import gov.nasa.podaac.inventory.model.GranuleMetaHistory;
import gov.nasa.podaac.inventory.model.MetadataManifest;
import gov.nasa.podaac.inventory.model.Provider;
import gov.nasa.podaac.inventory.model.Sensor;
import gov.nasa.podaac.inventory.model.Source;
import gov.nasa.podaac.inventory.model.Project;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author clwong
 * @version
 * $Id$
 */
public class EntityManager extends DAOFactory {

	private static Log log = LogFactory.getLog(EntityManager.class);

	private GenericManager instantiateDAO(Class daoClass) {
		try {
			log.debug("Instantiating DAO: " + daoClass);
			return (GenericManager)daoClass.newInstance();
		} catch (Exception ex) {
			throw new RuntimeException("Can not instantiate DAO: " + daoClass, ex);
		}
	}

	// enclosing interfaces
	public interface GranuleDAO extends GenericDAO<Granule, Integer> {	
		List<Granule> listByIngestTimeRange(Date startTime, Date endTime);
	}
	
	public interface ElementDDDAO
	extends GenericDAO<ElementDD, Integer> {
	}
	
	public interface DatasetSpatialDAO
	extends GenericDAO<DatasetSpatial, Integer> {
	}
	
	public interface DatasetRealDAO
	extends GenericDAO<DatasetReal, Integer>{
		
	}

	public interface MetadataManifestDAO
	extends GenericDAO<MetadataManifest, Integer> {
	}
	
	public interface GranuleElementDAO
	extends GenericDAO<GranuleElement, Integer> {
	}
	
	public interface DatasetElementDAO
	extends GenericDAO<DatasetElement, Integer> {
	}
	
	public interface GranuleMetaHistoryDAO
	extends GenericDAO<GranuleMetaHistory, Integer> {
	}

	public interface DatasetDAO
	extends GenericDAO<Dataset, Integer> {
	}
	
	public interface DatasetPolicyDAO
	extends GenericDAO<DatasetPolicy, Integer> {
	}
	
	public interface DatasetCoverageDAO
	extends GenericDAO<DatasetCoverage, Integer> {
	}
	
	public interface SourceDAO
	extends GenericDAO<Source, Integer> {
	}

	public interface SensorDAO
	extends GenericDAO<Sensor, Integer> {
	}

	public interface ProjectDAO
	extends GenericDAO<Project, Integer> {
	}
	
	public interface ProviderDAO
	extends GenericDAO<Provider, Integer> {
	}

	public interface ContactDAO
	extends GenericDAO<Contact, Integer> {
	}

	public interface CollectionDAO
	extends GenericDAO<Collection, Integer> {
	}
	
	public interface CollectionProductDAO 
		extends GenericDAO<CollectionProduct, Integer>{
	}

	public interface CollectionElementDDDAO
	extends GenericDAO<CollectionElementDD, Integer> {
	}

	public interface CollectionDatasetDAO
	extends GenericDAO<CollectionDataset, Integer> {
	}

	public interface CollectionContactDAO
	extends GenericDAO<CollectionContact, Integer> {
	}

	//--------------------------------------------------------------
	// create entity data objects
	//--------------------------------------------------------------
	public DatasetRealDAO getDatasetRealDAO(){
		return (DatasetRealDAO)instantiateDAO(DatasetRealManager.class);
	}
	
	public GranuleDAO getGranuleDAO() {
		return (GranuleDAO)instantiateDAO(GranuleManager.class);
	}

	public ElementDDDAO getElementDDDAO() {
		return (ElementDDDAO)instantiateDAO(ElementDDManager.class);
	}
	
	public MetadataManifestDAO getMetadataManifestDAO() {
		return (MetadataManifestDAO)instantiateDAO(MetadataManifestManager.class);
	}

	public GranuleElementDAO getGranuleElementDAO() {
		return (GranuleElementDAO)instantiateDAO(GranuleElementManager.class);
	}

	public DatasetElementDAO getDatasetElementDAO() {
		return (DatasetElementDAO)instantiateDAO(DatasetElementManager.class);
	}
	
	public GranuleMetaHistoryDAO getGranuleMetaHistoryDAO() {
		return (GranuleMetaHistoryDAO)instantiateDAO(GranuleMetaHistoryManager.class);
	}

	public DatasetDAO getDatasetDAO() {
		return (DatasetDAO)instantiateDAO(DatasetManager.class);
	}
	
	public DatasetSpatialDAO getDatasetSpatialDAO() {
		return (DatasetSpatialDAO)instantiateDAO(DatasetSpatialManager.class);
	}

	public DatasetPolicyDAO getDatasetPolicyDAO() {
		return (DatasetPolicyDAO)instantiateDAO(DatasetPolicyManager.class);
	}

	public DatasetCoverageDAO getDatasetCoverageDAO() {
		return (DatasetCoverageDAO)instantiateDAO(DatasetCoverageManager.class);
	}

	public SourceDAO getSourceDAO() {
		return (SourceDAO)instantiateDAO(SourceManager.class);
	}

	public SensorDAO getSensorDAO() {
		return (SensorDAO)instantiateDAO(SensorManager.class);
	}
	
	public ProjectDAO getProjectDAO() {
		return (ProjectDAO)instantiateDAO(ProjectManager.class);
	}

	public ProviderDAO getProviderDAO() {
		return (ProviderDAO)instantiateDAO(ProviderManager.class);
	}

	public ContactDAO getContactDAO() {
		return (ContactDAO)instantiateDAO(ContactManager.class);
	}

	public CollectionDAO getCollectionDAO() {
		return (CollectionDAO)instantiateDAO(CollectionManager.class);
	}
	
	public CollectionProductDAO getCollectionProductDAO() {
		return (CollectionProductDAO)instantiateDAO(CollectionProductManager.class);
	}
	
	public CollectionElementDDDAO getCollectionElementDDDAO() {
		return (CollectionElementDDDAO)instantiateDAO(CollectionElementDDManager.class);
	}
	
	public CollectionDatasetDAO getCollectionDatasetDAO() {
		return (CollectionDatasetDAO)instantiateDAO(CollectionDatasetManager.class);
	}
	
	public CollectionContactDAO getCollectionContactDAO() {
		return (CollectionContactDAO)instantiateDAO(CollectionContactManager.class);
	}
	//--------------------------------------------------------------
	// Inline concrete DAO implementations
	//--------------------------------------------------------------    
	public static class DatasetRealManager
	extends GenericManager<DatasetReal, Integer>
	implements DatasetRealDAO {
	}
	
	public static class ElementDDManager
	extends GenericManager<ElementDD, Integer>
	implements ElementDDDAO {
	}
	
	public static class MetadataManifestManager
	extends GenericManager<MetadataManifest, Integer>
	implements MetadataManifestDAO {
	}

	public static class GranuleMetaHistoryManager
	extends GenericManager<GranuleMetaHistory, Integer>
	implements GranuleMetaHistoryDAO {
	}

	public static class GranuleElementManager
	extends GenericManager<GranuleElement, Integer>
	implements GranuleElementDAO {
	}

	public static class DatasetElementManager
	extends GenericManager<DatasetElement, Integer>
	implements DatasetElementDAO {
	}
	
	public static class DatasetManager
	extends GenericManager<Dataset, Integer>
	implements DatasetDAO {
	}
	
	public static class DatasetSpatialManager
	extends GenericManager<DatasetSpatial, Integer>
	implements DatasetSpatialDAO {
	}
	
	public static class DatasetPolicyManager
	extends GenericManager<DatasetPolicy, Integer>
	implements DatasetPolicyDAO {
	}
	
	public static class DatasetCoverageManager
	extends GenericManager<DatasetCoverage, Integer>
	implements DatasetCoverageDAO {
	}
	
	public static class SourceManager
	extends GenericManager<Source, Integer>
	implements SourceDAO {
	}

	public static class SensorManager
	extends GenericManager<Sensor, Integer>
	implements SensorDAO {
	}

	public static class ProjectManager
	extends GenericManager<Project, Integer>
	implements ProjectDAO {
	}

	public static class ProviderManager
	extends GenericManager<Provider, Integer>
	implements ProviderDAO {
	}

	public static class ContactManager
	extends GenericManager<Contact, Integer>
	implements ContactDAO {
	}

	public static class CollectionManager
	extends GenericManager<Collection, Integer>
	implements CollectionDAO {
	}
	
	public static class CollectionProductManager
	extends GenericManager<CollectionProduct, Integer>
	implements CollectionProductDAO {
	}

	public static class CollectionElementDDManager
	extends GenericManager<CollectionElementDD, Integer>
	implements CollectionElementDDDAO {
	}

	public static class CollectionDatasetManager
	extends GenericManager<CollectionDataset, Integer>
	implements CollectionDatasetDAO {
	}

	public static class CollectionContactManager
	extends GenericManager<CollectionContact, Integer>
	implements CollectionContactDAO {
	}

}
