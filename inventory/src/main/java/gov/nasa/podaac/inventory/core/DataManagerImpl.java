// Copyright 2007, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.core;

import gov.nasa.podaac.inventory.api.DataManager;
import gov.nasa.podaac.inventory.dao.DAOFactory;
import gov.nasa.podaac.inventory.dao.GenericDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.CollectionContactDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.CollectionDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.CollectionDatasetDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.CollectionElementDDDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.ContactDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.DatasetCoverageDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.DatasetDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.DatasetElementDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.DatasetPolicyDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.DatasetRealDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.GranuleDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.GranuleElementDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.ElementDDDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.GranuleMetaHistoryDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.ProjectDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.ProviderDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.SensorDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.SourceDAO;
import gov.nasa.podaac.inventory.hibernate.HibernateSessionFactory;
import gov.nasa.podaac.inventory.model.Collection;
import gov.nasa.podaac.inventory.model.CollectionContact;
import gov.nasa.podaac.inventory.model.CollectionDataset;
import gov.nasa.podaac.inventory.model.CollectionElementDD;
import gov.nasa.podaac.inventory.model.Contact;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.DatasetCoverage;
import gov.nasa.podaac.inventory.model.DatasetElement;
import gov.nasa.podaac.inventory.model.DatasetPolicy;
import gov.nasa.podaac.inventory.model.DatasetReal;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.GranuleElement;
import gov.nasa.podaac.inventory.model.ElementDD;
import gov.nasa.podaac.inventory.model.GranuleMetaHistory;
import gov.nasa.podaac.inventory.model.Project;
import gov.nasa.podaac.inventory.model.Provider;
import gov.nasa.podaac.inventory.model.Sensor;
import gov.nasa.podaac.inventory.model.Source;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author clwong
 * $Id: DataManagerImpl.java 5242 2010-07-09 23:19:15Z gangl $
 */
public class DataManagerImpl implements DataManager {

	private static Log log = LogFactory.getLog(DataManagerImpl.class);
	private DAOFactory daoFactory = DAOFactory.instance(DAOFactory.HIBERNATE);	
	
	public DataManagerImpl() {
		super();
	}
	
	//-------------------  Manage Granule Data ------
	/**
	 * This method adds a granule into the database.
	 * @param granule
	 * @return Granule
	 */
	public Granule addGranule(Granule granule) {
	   
		log.debug("addGranule....");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		HibernateSessionFactory.getInstance().getCurrentSession().clear();
	        HibernateSessionFactory.getInstance().getCurrentSession().flush();
		GranuleDAO dao = daoFactory.getGranuleDAO();
		Granule result = dao.save(granule);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}
	
	public void updateGranule(Granule granule) {
		log.debug("updateGranule....");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		GranuleDAO dao = daoFactory.getGranuleDAO();
		dao.update(granule);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return;
	}
	
	/**
	 * This method deletes a granule from the database.
	 * @param granule
	 */
	public void deleteGranule(Granule granule) {
		log.debug("deleteGranule....");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		GranuleDAO dao = daoFactory.getGranuleDAO();
		dao.delete(granule);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	}

	//-------------------  Manage Granule Element Dictionary ------
	/**
	 * This method adds a granule element definition into database.
	 * @param element
	 * @return ElementDD
	 */
	public ElementDD addElementDD(ElementDD element) {
		log.debug("addElementDD....");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		ElementDDDAO dao = daoFactory.getElementDDDAO();
		ElementDD result = dao.save(element);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}

	/**
	 * This method deletes a granule  element definition from the database.
	 * @param element
	 */
	public void deleteElementDD(ElementDD element) {
		log.debug("deleteElementDD....");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		ElementDDDAO dao = daoFactory.getElementDDDAO();
		dao.delete(element);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	}

	//-------------------  Manage Granule Metadata History ------
	/**
	 * This method adds a granule metadata history into database.
	 * @param metaHistory
	 * @return GranuleMetaHistory
	 */
	public GranuleMetaHistory addGranuleMetaHistory(GranuleMetaHistory metaHistory) {
		log.debug("addGranuleMetaHistory....");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		GranuleMetaHistoryDAO dao = daoFactory.getGranuleMetaHistoryDAO();
		GranuleMetaHistory result = dao.save(metaHistory);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}

	//-------------------  Manage Granule Element ------
	/**
	 * This method adds a granule element into database.
	 * @param granuleElement
	 * @return GranuleElement
	 */
	public GranuleElement addGranuleElement(GranuleElement granuleElement) {
		log.debug("addGranuleElement....");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		GranuleElementDAO dao = daoFactory.getGranuleElementDAO();
		GranuleElement result = dao.save(granuleElement);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}

	//-------------------  Manage Dataset ------
	public Dataset addDataset(Dataset dataset) {
		log.debug("addDataset....");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		HibernateSessionFactory.getInstance().getCurrentSession().clear();
        HibernateSessionFactory.getInstance().getCurrentSession().flush();
		DatasetDAO dao = daoFactory.getDatasetDAO();
		Dataset result = dao.save(dataset);
			result.getDatasetElementSet().size();
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}
	
	//-------------------  Manage Dataset Element ------
	/**
	 * This method adds a granule element into database.
	 * @param granuleElement
	 * @return DatasetElement
	 */
	public DatasetElement addDatasetElement(DatasetElement granuleElement) {
		log.debug("addDatasetElement....");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		DatasetElementDAO dao = daoFactory.getDatasetElementDAO();
		DatasetElement result = dao.save(granuleElement);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}
	
	public void updateDataset(Dataset dataset) {
		log.debug("updateDataset....");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		DatasetDAO dao = daoFactory.getDatasetDAO();
		dao.update(dataset);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return;
	}
	
	public void updateDatasetDirty(Dataset dataset) {
		log.debug("updateDataset....");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		HibernateSessionFactory.getInstance().getCurrentSession().clear();
		HibernateSessionFactory.getInstance().getCurrentSession().flush();
		DatasetDAO dao = daoFactory.getDatasetDAO();
		
		dao.update(dataset);
		
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return;
	}
	
	public DatasetPolicy addDatasetPolicy(DatasetPolicy datasetPolicy) {
		log.debug("addDatasetPolicy....");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		DatasetPolicyDAO dao = daoFactory.getDatasetPolicyDAO();
		DatasetPolicy result = dao.save(datasetPolicy);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}
	
	public DatasetCoverage addDatasetCoverage(DatasetCoverage datasetCoverage) {
		log.debug("addDatasetCoverage....");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		DatasetCoverageDAO dao = daoFactory.getDatasetCoverageDAO();
		DatasetCoverage result = dao.save(datasetCoverage);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}

	public void deleteDataset(Dataset dataset) {
		log.debug("deleteDataset....");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		DatasetDAO dao = daoFactory.getDatasetDAO();
		dao.delete(dataset);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	}	
	public void deleteCollectionDataset(CollectionDataset cd){
		log.debug("deleteCollectionDataset....");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		CollectionDatasetDAO dao = daoFactory.getCollectionDatasetDAO();
		dao.delete(cd);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	}
	

	//-------------------  Manage Source ------
	public Source addSource(Source source) {
		log.debug("addSource....");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		SourceDAO dao = daoFactory.getSourceDAO();
		Source result = dao.save(source);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}
	
	//-------------------  Manage Sensor ------
	public Sensor addSensor(Sensor sensor) {
		log.debug("addSensor....");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		SensorDAO dao = daoFactory.getSensorDAO();
		Sensor result = dao.save(sensor);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}
	
	//-------------------  Manage Provider ------
	public Provider addProvider(Provider provider) {
		log.debug("addProvider....");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		ProviderDAO dao = daoFactory.getProviderDAO();
		Provider result = dao.save(provider);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}
	
	/**
	 * This method deletes a provider from the database.
	 * @param element
	 */
	public void deleteProvider(Provider provider) {
		log.debug("deleteProvider....");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		ProviderDAO dao = daoFactory.getProviderDAO();
		dao.delete(provider);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	}

	//-------------------  Manage Contact ------
	public Contact addContact(Contact contact) {
		log.debug("addContact....");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		ContactDAO dao = daoFactory.getContactDAO();
		Contact result = dao.save(contact);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}
	
	//-------------------  Manage Collection ------
	public Collection addCollection(Collection collection) {
		log.debug("addCollection....");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		CollectionDAO dao = daoFactory.getCollectionDAO();
		Collection result = dao.save(collection);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}
	
	//-------------------  Manage Collection Element Dictionary ------
	public CollectionElementDD addCollectionElementDD (
					CollectionElementDD collectionElementDD) {
		log.debug("addCollectionElementDD....");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		CollectionElementDDDAO dao = daoFactory.getCollectionElementDDDAO();
		CollectionElementDD result = dao.save(collectionElementDD);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}

	//-------------------  Manage Collection Dataset ------
	/**
	 * This method adds a collection dataset joined element into database.
	 */
	public CollectionDataset addCollectionDataset(CollectionDataset collectionDataset) {
		log.debug("addCollectionDataset....");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		
		HibernateSessionFactory.getInstance().getCurrentSession().clear();
		HibernateSessionFactory.getInstance().getCurrentSession().flush();
		
		CollectionDatasetDAO dao = daoFactory.getCollectionDatasetDAO();
		CollectionDataset result = dao.save(collectionDataset);
	    
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}

	//-------------------  Manage Collection Contact ------
	/**
	 * This method adds a collection contact joined element into database.
	 */
	public CollectionContact addCollectionContact(CollectionContact collectionContact) {
		log.debug("addCollectionContact....");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		CollectionContactDAO dao = daoFactory.getCollectionContactDAO();
		CollectionContact result = dao.save(collectionContact);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}

	public DatasetReal addDatasetReal(DatasetReal dr) {
		log.debug("addCollectionContact....");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		DatasetRealDAO dao = daoFactory.getDatasetRealDAO();
		DatasetReal result = (DatasetReal) dao.save(dr);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}

	public Project addProject(Project s) {
		log.debug("addProject....");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		ProjectDAO dao = daoFactory.getProjectDAO();
		Project result = dao.save(s);
	    HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();
	    return result;
	}
}
