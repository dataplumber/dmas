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
import gov.nasa.podaac.inventory.model.DatasetLocationPolicy;
import gov.nasa.podaac.inventory.model.DatasetPolicy;
import gov.nasa.podaac.inventory.model.DatasetRegion;
import gov.nasa.podaac.inventory.model.Provider;


import java.util.Date;
import java.util.List;

import org.junit.Test;

/**
 * This class tests interfaces for the tables associated with the Dataset
 * portion of the data model.
 * 
 * @author clwong
 * @version $Id: DatasetTest.java 13471 2014-08-15 18:47:27Z gangl $
 */
public class DatasetTest {
	
	private static final String DATASETNAME = "dsShortName";

	/**
	 * This is a unit test to add a minimal dataset record into database.
	 */
	@Test
	public void testAddDataset() {

		Query q = QueryFactory.getInstance().createQuery();
		DataManager manager = DataManagerFactory.getInstance()
								.createDataManager();
	       
		// delete before create new one
		Dataset ds = q.findDatasetByShortName(DATASETNAME);
		if (ds != null) 
		{	
			//System.out.println("Deleting old Dataset");	
			System.out.println("deleting dataset with Shortname: "+ ds.getShortName() + " and ID: " +ds.getDatasetId());
			manager.deleteDataset(ds);
		
		}
		assertNull(q.findDatasetByShortName(DATASETNAME));	
		
		ds = new Dataset();
		ds.setShortName(DATASETNAME);
		ds.setLongName("dsLongName");
		ds.setProcessingLevel("dsLevel");
		ds.setRemoteDataset("C");
		
		
		ds.setTemporalResolution("dsTemporalResolution");
		ds.setLatitudeResolution(77.7);
		ds.setLongitudeResolution(135.75);

		DatasetRegion dr = new DatasetRegion();
		
		dr.setRegion("added Region");
		dr.setRegionDetail("added region detail");
		ds.getRegionSet().add(dr);
		
		
		Provider pr = q.findProviderByShortName("CLWONG");
		if (pr==null) {
			pr = new Provider();
			pr.setShortName("CLWONG");
			pr.setLongName("Cynthia L. Wong");
			pr.setType("TEST");
		}
		ds.setProvider(pr);

		DatasetLocationPolicy locationPolicy = new DatasetLocationPolicy();
		locationPolicy.setBasePath("file:///tmp/");
		locationPolicy.setType("ARCHIVE");
		ds.add(locationPolicy);

		locationPolicy = new DatasetLocationPolicy();
		locationPolicy.setBasePath("ftp://localhost");
		locationPolicy.setType("FTP");
		ds.add(locationPolicy);

		locationPolicy = new DatasetLocationPolicy();
		locationPolicy.setBasePath("http://localhost");
		locationPolicy.setType("OPENDAP");
		ds.add(locationPolicy);
		
		manager = DataManagerFactory.getInstance()
				.createDataManager();
		manager.addProvider(pr);
		Dataset result = manager.addDataset(ds);
		
		
		
		Integer[] elementIds = {2, 7, 14, 22, 24, 27};
		for (Integer elementId : elementIds) {
			
			DatasetElement DatasetElement = new DatasetElement();
			DatasetElement.setDataset(ds);
			DatasetElement.setElementDD(q.findElementDDById(elementId));
			DatasetElement.setScope("BOTH");
			DatasetElement.setObligationFlag('O');
			manager.addDatasetElement(DatasetElement);
		}
		
		System.out.println("Resultant DatasetElementSet" + result.getDatasetElementSet().size());
		
		manager.updateDataset(ds);
		
		assertNotNull(result.getDatasetId());
		assertEquals(result.getShortName(), ds.getShortName());
		assertEquals(result.getLongName(), ds.getLongName());
		assertEquals(result.getProcessingLevel(), ds.getProcessingLevel());
		assert result.getProvider() instanceof Provider;
	}

	/**
	 * This is a unit test to add a dataset policy record into database.
	 */
    @Test
	public void testAddDatasetPolicy() {

    	System.out.println("TEST ADD DSP");
    	
		DatasetPolicy policy = new DatasetPolicy();
		policy.setAccessType("PRIVATE");
		policy.setBasePathAppendType("YEAR-DOY");
		policy.setChecksumType("MD5");
		policy.setDataClass("public");
		policy.setDataFormat("Data");
		policy.setCompressType("GZIP");
		policy.setSpatialType("NONE");	// ORACLE, BACKTRACK, or NONE
		policy.setAccessConstraint("read");
		policy.setUseConstraint("read");
		policy.setBasePathAppendType("YEAR-DOY");
		policy.setDataLatency(2);
		policy.setViewOnline("N");

		Query q = QueryFactory.getInstance().createQuery();
		Dataset ds = null;
		try{
		
		ds = q.findDatasetByShortName(DATASETNAME);
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("Got here...");
		
		if(ds.getDatasetId() == null)
			System.out.println("****Dataset Does Not Exist.");
		
		policy.setDataset(ds);
		ds.setDatasetPolicy(policy);

		System.out.println("adding policy...");
		DataManager manager = DataManagerFactory.getInstance()
		.createDataManager();
		
		DatasetPolicy result = manager.addDatasetPolicy(policy);
		
		
		assertNotNull(result.getDatasetId());
		assertEquals(result.getAccessType(), policy.getAccessType());
		assertEquals(result.getChecksumType(), policy.getChecksumType());
		assertEquals(result.getCompressType(), policy.getCompressType());
		assertEquals(result.getDataClass(), policy.getDataClass());
		assertEquals(result.getDataFormat(), policy.getDataFormat());
    }

	/**
	 * This is a unit test to add a dataset coverage record into database.
	 */
    @Test
	public void testAddDatasetCoverage() {

    	/*
		Query q = QueryFactory.getInstance().createQuery();
		Dataset ds = q.findDatasetByShortName(DATASETNAME);
		final SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");
		String startTime = formatter.format(new Date());
		String sql = "INSERT INTO dataset_coverage "
			+ "	(dataset_id, start_time) VALUES " + " ("
			+ ds.getDatasetId() + ", to_timestamp('" + startTime
			+ "', 'YYYY-MM-DD HH24:MI:SS.FF3'))";

		Session session = HibernateSessionFactory.getInstance()
		.getCurrentSession();
		session.beginTransaction();
		session.createSQLQuery(sql).executeUpdate();
		session.getTransaction().commit();

		Dataset result = q.fetchDatasetList(
				new String[] { "datasetCoverage" },
		"dataset.shortName='"+DATASETNAME+"'").get(0);
		DatasetCoverage coverage = result.getDatasetCoverage();
		assertEquals(ds.getDatasetId(), coverage.getDataset()
				.getDatasetId());
	     */
		
		 DatasetCoverage coverage = new DatasetCoverage();
		 coverage.setEastLon(180.);
		 coverage.setWestLon(-180.);
		 coverage.setNorthLat(90.);
		 coverage.setSouthLat(-90.);
		 coverage.setStartTime(new Date());
		 
		 Query q = QueryFactory.getInstance().createQuery(); 
		 Dataset ds = q.findDatasetByShortName(DATASETNAME); 
		 coverage.setDataset(ds);
		 ds.setDatasetCoverage(coverage);
		  
		 DataManager manager =
		 DataManagerFactory.getInstance().createDataManager(); 
		 DatasetCoverage result = manager.addDatasetCoverage(coverage);
		
		 assertNotNull(result.getDatasetId());
		 assertEquals(result.getEastLon(), coverage.getEastLon());
		 assertEquals(result.getNorthLat(), coverage.getNorthLat());
		 assertEquals(result.getWestLon(), coverage.getWestLon());
		 assertEquals(result.getSouthLat(), coverage.getSouthLat());
	}

	/**
	 * This is a unit test to list all dataset records.
	 */
    @Test
	public void testListDataset() {
		Query q = QueryFactory.getInstance().createQuery();
		List<Dataset> resultList = q.listDataset();
		for (Dataset result : resultList) {
			assert result instanceof Dataset;
			assert result.getProvider() instanceof Provider;
		}
	}

	/**
	 * This is a unit test to update the dataset policy.
	 */
    @Test
	public void testUpdateDatasetPolicy() {
		Query q = QueryFactory.getInstance().createQuery();
		Dataset ds = (q.fetchDatasetList(new String[] { "datasetPolicy" },
				"dataset.shortName='"+DATASETNAME+"'")).get(0);
		DatasetPolicy policy = ds.getDatasetPolicy();
		if (policy != null) {
			policy.setAccessType("PUBLIC");
			DataManager manager = DataManagerFactory.getInstance()
					.createDataManager();
			DatasetPolicy result = manager.addDatasetPolicy(policy);
			assertEquals(result.getAccessType(), "PUBLIC");
		}
	}

	/**
	 * This is a unit test to query a dataset by its shortname.
	 */
    @Test
	public void testQueryDataset() {
		Query q = QueryFactory.getInstance().createQuery();
		Dataset ds = (q.fetchDatasetList(new String[] { "datasetPolicy" },
				"dataset.shortName='"+DATASETNAME+"'")).get(0);
		
		for(DatasetRegion dr : ds.getRegionSet()){
		
			System.out.println("dataset: " +dr.getRegion()+":" + dr.getRegionDetail());
		
		}
		
		assertEquals(ds.getShortName(), DATASETNAME);
		assertEquals(ds.getDatasetPolicy().getAccessType(), "PUBLIC");
		assertEquals(ds.getDatasetPolicy().getViewOnline(), "N");
	}
}
