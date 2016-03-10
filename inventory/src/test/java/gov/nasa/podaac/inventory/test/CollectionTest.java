// Copyright 2007, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import gov.nasa.podaac.inventory.api.DataManager;
import gov.nasa.podaac.inventory.api.DataManagerFactory;
import gov.nasa.podaac.inventory.api.Query;
import gov.nasa.podaac.inventory.api.QueryFactory;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.DatasetCoverage;
import gov.nasa.podaac.inventory.model.DatasetElement;
import gov.nasa.podaac.inventory.model.DatasetInteger;
import gov.nasa.podaac.inventory.model.DatasetLocationPolicy;
import gov.nasa.podaac.inventory.model.DatasetPolicy;
import gov.nasa.podaac.inventory.model.ElementDD;
import gov.nasa.podaac.inventory.model.GranuleElement;
import gov.nasa.podaac.inventory.model.Provider;

import gov.nasa.podaac.inventory.model.Collection;
import gov.nasa.podaac.inventory.model.CollectionLegacyProduct;

import java.util.Date;
import java.util.List;

import org.junit.Test;

/**
 * This class tests interfaces for the tables associated with the Dataset
 * portion of the data model.
 * 
 * @author clwong
 * @version $Id: DatasetTest.java 2363 2008-12-04 19:30:14Z clwong $
 */
public class CollectionTest {
	
	private static final String DATASETNAME = "dsShortName";

	/**
	 * This is a unit test to add a minimal dataset record into database.
	 */
	@Test
	public void testFetchCollection() {

		Query q = QueryFactory.getInstance().createQuery();
		DataManager manager = DataManagerFactory.getInstance()
								.createDataManager();
	        //System.out.println("Deleting old Dataset");	
		// delete before create new one
		Collection col = q.fetchCollectionById(55);
		for(CollectionLegacyProduct clp : col.getCollectionLegacyProductSet())
		{
			System.out.println("Legacy id: " + clp.getLegacyProductId());
		}
	      
		
		//System.out.println("Collection ID: " + col.getCollectionProduct().getProductId());
		System.out.println("Collection full desc:" + col.getFullDescription());
		System.out.println("Collection Aggregate:" + col.getAggregate());
		
		//assertNull(q.findDatasetByShortName(DATASETNAME));	
		
		
		/*assertNotNull(result.getDatasetId());
		assertEquals(result.getShortName(), ds.getShortName());
		assertEquals(result.getLongName(), ds.getLongName());
		assertEquals(result.getProcessingLevel(), ds.getProcessingLevel());*/
		//assert result.getProvider() instanceof Provider;
	}
}
