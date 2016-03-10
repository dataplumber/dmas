// Copyright 2008, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//
// $Id: CleanupTest.java 1955 2008-09-20 21:41:06Z shardman $

package gov.nasa.podaac.inventory.test;

import static org.junit.Assert.assertNull;
import gov.nasa.podaac.inventory.api.DataManager;
import gov.nasa.podaac.inventory.api.DataManagerFactory;
import gov.nasa.podaac.inventory.api.Query;
import gov.nasa.podaac.inventory.api.QueryFactory;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.Provider;

import java.util.List;
import java.util.Set;

import org.junit.Test;

/**
 * This class contains test cases to clean up database objects.
 *
 * @author clwong
 *
 * @version $Revision: 1955 $
 */
public class CleanupTest {
	private static final String PROVIDER = "CLWONG";

	/**
	 * This is a unit test to delete datasets belonging to PROVIDER.
	 */
	@Test
	public void testCleanupDataset() {
		Query q = QueryFactory.getInstance().createQuery();
		Provider providerInstance = new Provider();
		providerInstance.setShortName(PROVIDER);
		List<Provider> providerList = q.fetchProviderList(providerInstance, "datasetSet");
		DataManager manager = DataManagerFactory.getInstance().createDataManager();		
		for (Provider provider : providerList) {
			if (provider.getShortName().equals(PROVIDER)) {
				Set<Dataset> dsSet = provider.getDatasetSet();
				for (Dataset ds : dsSet) {
					manager.deleteDataset(ds);
					ds = q.findDatasetById(ds.getDatasetId());
					assertNull(ds);
				}
			}
		}		
	}
	
	/**
	 * This is a unit test to delete providers with PROVIDER name.
	 */
	@Test
	public void testCleanupProvider() {
		Query q = QueryFactory.getInstance().createQuery();
		Provider providerInstance = new Provider();
		providerInstance.setShortName(PROVIDER);
		List<Provider> providerList = q.listProviderByProperty("shortName", PROVIDER);
		DataManager manager = DataManagerFactory.getInstance().createDataManager();		
		for (Provider provider : providerList) {
			manager.deleteProvider(provider);
		}		
	}
}
