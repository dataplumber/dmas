// Copyright 2007, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.dao;

import gov.nasa.podaac.inventory.dao.EntityManager.DatasetElementDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.CollectionContactDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.CollectionDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.CollectionDatasetDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.CollectionElementDDDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.CollectionProductDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.ContactDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.DatasetCoverageDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.DatasetDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.DatasetPolicyDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.DatasetRealDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.GranuleDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.GranuleElementDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.ElementDDDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.GranuleMetaHistoryDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.MetadataManifestDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.ProjectDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.ProviderDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.SensorDAO;
import gov.nasa.podaac.inventory.dao.EntityManager.SourceDAO;

/**
 * @author clwong
 * @version Sep 7, 2007
 * $Id: DAOFactory.java 4739 2010-04-16 16:15:39Z gangl $
 */
public abstract class DAOFactory {
	
    public static final Class HIBERNATE = EntityManager.class;

    /**
     * Factory method for instantiation of concrete factories.
     */
    public static DAOFactory instance(Class factory) {
        try {
            return (DAOFactory)factory.newInstance();
        } catch (Exception ex) {
        	ex.printStackTrace();
            throw new RuntimeException("Couldn't create DAOFactory: " + factory);
        }
    }
    
    // Add your DAO interfaces here
    public abstract GranuleDAO getGranuleDAO();
    public abstract ElementDDDAO getElementDDDAO();
    public abstract MetadataManifestDAO getMetadataManifestDAO();
    public abstract DatasetDAO getDatasetDAO();
    public abstract DatasetPolicyDAO getDatasetPolicyDAO();
    public abstract DatasetCoverageDAO getDatasetCoverageDAO();
    public abstract SourceDAO getSourceDAO();
    public abstract SensorDAO getSensorDAO();
    public abstract DatasetRealDAO getDatasetRealDAO();
    public abstract ProjectDAO getProjectDAO();
    public abstract ProviderDAO getProviderDAO();
    public abstract ContactDAO getContactDAO();
    public abstract CollectionDAO getCollectionDAO();
    public abstract CollectionProductDAO getCollectionProductDAO();
    public abstract CollectionElementDDDAO getCollectionElementDDDAO();
    public abstract CollectionContactDAO getCollectionContactDAO();
    // Add your PK DAO interfaces here

    public abstract GranuleMetaHistoryDAO getGranuleMetaHistoryDAO();
    public abstract GranuleElementDAO getGranuleElementDAO();
    public abstract DatasetElementDAO getDatasetElementDAO();
    public abstract CollectionDatasetDAO getCollectionDatasetDAO();

}
