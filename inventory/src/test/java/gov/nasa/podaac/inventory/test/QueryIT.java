//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.podaac.inventory.test;


import gov.nasa.podaac.inventory.api.Query;
import gov.nasa.podaac.inventory.api.QueryFactory;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.DatasetLocationPolicy;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.GranuleArchive;
import gov.nasa.podaac.inventory.model.GranuleReal;

import java.util.List;
import java.util.Properties;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * This class tests queries from database.
 *
 * @author clwong
 * @version
 * $Id: QueryTest.java 6515 2010-12-16 19:20:53Z gangl $
 */
public class QueryIT extends TestCase {

	private static final String DATASETNAME = "dsShortName";
	
	
//	public void testFetchLatestGranuleIdByDatasetID(){
//		//fetchLatestGranuleIdByDatasetID(int datasetId)
//		Query q = QueryFactory.getInstance().createQuery();
//		System.out.println("fetchLatestGranuleIdByDatasetID");
//		int x = q.fetchLatestGranuleIdByDatasetID(14);
//							// properties 				for WHERE clause
//		System.out.println("\tLatest Granule id: "+x);
//		assertFalse(x == 0);
//		
//	}
	
	/**
	 * This is a unit test to fetch dataset using HQL method.
	 */
	@Test
	public void testFetchDatasetUsingHQL() {
		Query q = QueryFactory.getInstance().createQuery();
		System.out.println("testFetchDatasetUsingHQL");
		List<Dataset> dsList = q.fetchDatasetList(
				new String[] {"datasetPolicy", "locationPolicySet"}, "dataset.shortName='"+DATASETNAME+"'");
							// properties 				for WHERE clause
		assertFalse(dsList.isEmpty());
		//System.out.println(dsList.size());
		for (Dataset ds : dsList) {
			assertNotNull(ds);
			assertNotNull(ds.getDatasetPolicy());
			try {
				ds.getDatasetPolicy().getChecksumType();
			} catch (Exception e) {
				fail(e.getMessage());
			}
			Set<DatasetLocationPolicy> policySet = ds.getLocationPolicySet();
			//System.out.println(ds.getShortName()+" has " + policySet.size() + " locationPolicy");
			for (DatasetLocationPolicy policy : policySet) {
				System.out.println(policy.getBasePath());
			}
			assertNotNull(ds.getLocationPolicySet());
			assertFalse(ds.getLocationPolicySet().isEmpty());
		}
	}
	
	/**
	 * This is a unit test to fetch dataset using Criteria method.
	 */
	@Test
	public void testFetchDatasetUsingCriteria() {
		Query q = QueryFactory.getInstance().createQuery();
		System.out.println("testFetchDatasetUsingCriteria");
		Properties properties = new Properties();
		properties.put("shortName", DATASETNAME);
		//properties.put("datasetId", 19);
		List<Dataset> dsList = q.fetchDatasetList(properties, "datasetPolicy", "locationPolicySet");
		assertFalse(dsList.isEmpty());
		for (Dataset ds : dsList) {
			assertNotNull(ds);
			assertNotNull(ds.getDatasetPolicy());
			try {
				ds.getDatasetPolicy().getChecksumType();
			} catch (Exception e) {
				fail(e.getMessage());
			}
			Set<DatasetLocationPolicy> policySet = ds.getLocationPolicySet();
			for (DatasetLocationPolicy policy : policySet) {
				System.out.println(policy.getBasePath());
			}
			Dataset ds2 = q.fetchDatasetById(ds.getDatasetId(), "locationPolicySet");
			policySet = ds2.getLocationPolicySet();
			for (DatasetLocationPolicy policy : policySet) {
				System.out.println(policy.getBasePath());
			}
			assertNotNull(ds.getLocationPolicySet());
			assertFalse(ds.getLocationPolicySet().isEmpty());
		}
	}

	/**
	 * This is a unit test to fetch dataset using Example method.
	 */
	@Test
	public void testFetchDatasetUsingExample() {
		Query q = QueryFactory.getInstance().createQuery();
		System.out.println("testFetchDatasetUsingExample");
		Dataset dsInstance = new Dataset();
		dsInstance.setShortName(DATASETNAME);
		List<Dataset> dsList = q.fetchDatasetList(dsInstance, "datasetPolicy", "locationPolicySet");
		assertFalse(dsList.isEmpty());
		for (Dataset ds : dsList) {
			assertNotNull(ds);
			assertNotNull(ds.getDatasetPolicy());
			try {
				ds.getDatasetPolicy().getChecksumType();
			} catch (Exception e) {
				fail(e.getMessage());
			}
			Set<DatasetLocationPolicy> policySet = ds.getLocationPolicySet();
			for (DatasetLocationPolicy policy : policySet) {
				System.out.println(policy.getBasePath());
			}
			Dataset ds2 = q.fetchDatasetById(ds.getDatasetId(), "locationPolicySet");
			policySet = ds2.getLocationPolicySet();
			for (DatasetLocationPolicy policy : policySet) {
				System.out.println(policy.getBasePath());
			}
			assertNotNull(ds.getLocationPolicySet());
			assertFalse(ds.getLocationPolicySet().isEmpty());
		}
	}

	/**
	 * This is a unit test to fetch granuleArchive using HQL method.
	 */
	@Test
	public void testFetchGranuleArchiveUsingHQL() {
		Query q = QueryFactory.getInstance().createQuery();
		System.out.println("testFetchGranuleArchiveUsingHQL");
		List<Granule> granuleList = q.fetchGranuleList(
//				new String[]{ "granuleRealSet", "granuleArchiveSet" },
				new String[]{ "granuleArchiveSet" },
				"granule.dataset.shortName='"+DATASETNAME+"'");
		assertFalse(granuleList.isEmpty());
		System.out.println("granule size="+granuleList.size());
		for (Granule g : granuleList) {
			for (GranuleArchive ga : g.getGranuleArchiveSet()) {
				ga.setGranuleId(g.getGranuleId());
				System.out.println(ga.toString());
			}
//			for (GranuleReal ga : g.getGranuleRealSet()) {
//				System.out.println(ga.toString());
//			}
		}
	}
	
	/**
	 * This is a unit test to fetch granuleArchive using Criteria method.
	 */
	@Test
	public void testFetchGranuleArchiveUsingCriteria() {
		Query q = QueryFactory.getInstance().createQuery();
		System.out.println("testFetchGranuleArchiveUsingCriteria");
		Properties properties = new Properties();
		properties.put("dataset", q.findDatasetByShortName(DATASETNAME));
		List<Granule> granuleList = q.fetchGranuleList(properties,"granuleRealSet", "granuleArchiveSet");

		assertFalse(granuleList.isEmpty());
		System.out.println("granule size="+granuleList.size());
		for (Granule g : granuleList) {
			for (GranuleArchive ga : g.getGranuleArchiveSet()) {
				ga.setGranuleId(g.getGranuleId());
				System.out.println(ga.toString());
			}
			for (GranuleReal ga : g.getGranuleRealSet()) {
				System.out.println(ga.toString());
			}
		}
	}
	
	/**
	 * This is a unit test to fetch granuleArchive using Example method.
	 */
	/*@Test
	public void testFetchGranuleArchiveUsingExample() {
		Query q = QueryFactory.getInstance().createQuery();
		System.out.println("testFetchGranuleArchiveUsingExample");
		Granule granuleInstance = new Granule();
		granuleInstance.setDataset(q.findDatasetByShortName(DATASETNAME));
		List<Granule> granuleList = q.fetchGranuleList(granuleInstance, 
				"granuleRealSet", "granuleArchiveSet");
		assertFalse(granuleList.isEmpty());
		System.out.println("granule size="+granuleList.size());
		for (Granule g : granuleList) {
			for (GranuleArchive ga : g.getGranuleArchiveSet()) {
				ga.setGranuleId(g.getGranuleId());
				System.out.println(ga.toString());
			}
			for (GranuleReal ga : g.getGranuleRealSet()) {
				System.out.println(ga.toString());
			}
		}
	}
	*/
	/**
	 * This is a unit test to fetch dataset using Criteria method.
	 */
	@Test
	public void testFetchGranuleByDatasetUsingHQL() {
		Query q = QueryFactory.getInstance().createQuery();
		System.out.println("testFetchGranuleByDatasetUsingHQL");
		Dataset ds = q.findDatasetByShortName(DATASETNAME);
		List<Dataset> dsList = q.fetchDatasetList(
				new String[]{ "granuleSet" }, "dataset.id="+ds.getDatasetId());
		assertFalse(dsList.isEmpty());
		System.out.println("dataset size="+dsList.size());
		Set<Granule> granuleSet = dsList.get(0).getGranuleSet();
		Integer gSize = q.sizeOfGranuleByDatasetId(ds.getDatasetId());
		System.out.println("granule size="+granuleSet.size()+";"+gSize);
		assertEquals(granuleSet.size(), gSize.intValue());
		for (Granule g : granuleSet) {
			System.out.println(g.getGranuleId()+":"+g.getName());
		}
	}
	
	/**
	 * This is a unit test caching the dictionary elements.
	 */
	@Test
	public void testGetElementDD() {		
		long time = System.currentTimeMillis();
		Query q = QueryFactory.getInstance().createQuery();
		System.out.println("testGetElementDD");
		assertEquals(q.findElementDDByShortName("cycle").getShortName(), "cycle");
		System.out.println("testGetElementDD took "+(System.currentTimeMillis()-time));
		time = System.currentTimeMillis();
		assertEquals(q.findElementDDByShortName("cycle").getShortName(), "cycle");
		System.out.println("testGetElementDD took "+(System.currentTimeMillis()-time));
	}
	
	/**
	 * This is a unit test list number of granules per dataset.
	 */	
	@Test
	public void testFetchDatasetGranules() {
		Query q = QueryFactory.getInstance().createQuery();
		List<Integer> datasetIdList = q.listDatasetId();
		for (Integer datasetId : datasetIdList) {
			int size = q.sizeOfGranuleByDatasetId(datasetId);
			System.out.println("datasetId:"+datasetId
					+":"+q.findDatasetById(datasetId).getShortName()
					+":"+size+":granules");
			List<Integer> granuleIdList = q.listGranuleId("dataset_id="+datasetId);
			assertEquals(granuleIdList.size(), size);
			System.out.println(granuleIdList.size());
		}
			
	}
	
	/**
	 * This is a unit test perform a set of similar queries to support echo metadata mapping.
	 
	@Test
	public void testGetCollection() {
		Query q = QueryFactory.getInstance().createQuery();
		CollectionProduct example = new CollectionProduct();
		example.setVisibleFlag('Y');
		List<CollectionProduct> productList = q.fetchProductList(example, "collection");
		for (CollectionProduct product : productList) {
			Collection collection = null;
			Set<CollectionDataset> collectionDatasetSet = null;
			try {
				collectionDatasetSet = collection.getCollectionDatasetSet();
			} catch (Exception e) {
				System.out.println("Fetch Collection...");
				collection = q.fetchCollectionById(product.getCollectionId());
				collectionDatasetSet = collection.getCollectionDatasetSet();
			}
			System.out.println(
				collection.getCollectionId()+":"+collection.getShortName()+" size="+collectionDatasetSet.size());
			for (CollectionDataset cd : collectionDatasetSet) {
				Dataset dataset = q.fetchDatasetById(
						cd.getCollectionDatasetPK().getDataset().getDatasetId());
				Set<DatasetVersion> versionSet = dataset.getVersionSet();
				System.out.println("Dataset Version size="+versionSet.size());
				for(DatasetVersion version : versionSet) {
					System.out.println(version.getVersion());
				}
				Set<DatasetMetaHistory> historySet = dataset.getMetaHistorySet();
				System.out.println("Dataset History size="+historySet.size());
				List<Date> createDates = new ArrayList<Date>();
				List<Date> revisionDates = new ArrayList<Date>();
				for(DatasetMetaHistory history : historySet) {
					createDates.add(history.getCreationDate());
					revisionDates.add(history.getLastRevisionDate());
				}
				System.out.println(Collections.min(createDates));
				System.out.println(Collections.max(revisionDates));
				System.out.println(dataset.getDatasetPolicy().getBasePathAppendType());
				
				Set<CollectionContact> collectionContactSet = collection.getCollectionContactSet();
				System.out.println("collectionContactSet size="+collectionContactSet.size());
				for (CollectionContact collectionContact : collectionContactSet) {
					Contact contact = collectionContact.getCollectionContactPK().getContact();
					String providerLongname = null;
					try {
						providerLongname = contact.getProvider().getLongName();
					} catch (Exception e) {
						System.out.println("Fetch Contact...");
						//e.printStackTrace();
						contact = q.fetchContactById(contact.getContactId());
						providerLongname = contact.getProvider().getLongName();
					}
					System.out.println("provider longname="+providerLongname);
				}
				Set<DatasetSource> datasetSourceSet = dataset.getSourceSet();
				for (DatasetSource datasetSource : datasetSourceSet) {
					Source source = datasetSource.getDatasetSourcePK().getSource();
					Sensor sensor = datasetSource.getDatasetSourcePK().getSensor();
					System.out.println("source longname, shortname, type="
							+source.getSourceLongName()+":"
							+source.getSourceShortName()+":"
							+source.getSourceType()+"\n"
							+"sensor longname, shortname="
							+sensor.getSensorLongName()+":"
							+sensor.getSensorShortName());
				}
				
				Set<DatasetResource> datasetResourceSet = dataset.getResourceSet();
				System.out.println("datasetResourceSet size="+datasetResourceSet.size());
				for (DatasetResource datasetResource : datasetResourceSet) {
					System.out.println(datasetResource.getResourcePath());
				}

			}
		}
		
	}*/
}
