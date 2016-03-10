// Copyright 2007, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
 
package gov.nasa.podaac.inventory.test;

import gov.nasa.podaac.common.api.serviceprofile.ArchiveFileInfo;
import gov.nasa.podaac.common.api.serviceprofile.ArchiveGranule;
import gov.nasa.podaac.common.api.util.StringUtility;
import gov.nasa.podaac.common.api.serviceprofile.ArchiveProfile;
import gov.nasa.podaac.common.api.serviceprofile.GranuleHistory;
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfile;
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfileFactory;
import gov.nasa.podaac.inventory.api.DataManager;
import gov.nasa.podaac.inventory.api.DataManagerFactory;
import gov.nasa.podaac.inventory.api.Inventory;
import gov.nasa.podaac.inventory.api.InventoryFactory;
import gov.nasa.podaac.inventory.api.Query;
import gov.nasa.podaac.inventory.api.QueryFactory;
import gov.nasa.podaac.inventory.api.Constant.AccessType;
import gov.nasa.podaac.inventory.api.Constant.AppendBasePathType;
import gov.nasa.podaac.inventory.api.Constant.LocationPolicyType;
import gov.nasa.podaac.inventory.exceptions.InventoryException;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.DatasetCoverage;
import gov.nasa.podaac.inventory.model.DatasetElement;
import gov.nasa.podaac.inventory.model.DatasetLocationPolicy;
import gov.nasa.podaac.inventory.model.DatasetPolicy;
import gov.nasa.podaac.inventory.model.DatasetRegion;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.GranuleArchive;
import gov.nasa.podaac.inventory.model.GranuleElement;
import gov.nasa.podaac.inventory.model.Provider;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This class tests the interfaces between Ingest and Inventory Program Sets.
 *
 * @author clwong
 * @version $Id: GranuleMetadataTest.java 13177 2014-04-03 19:11:08Z gangl $
 */
public class GranuleMetadataIT extends TestCase {
	
	private List<Integer> dsIdList = new ArrayList<Integer>();
	
	@BeforeClass
    public void setUp() {
		//System.getProperties().list(System.out);
		//System.out.println("setup: basedir="+System.getProperty("basedir"));
		Properties p = new Properties();
		try {
			p.load(new FileInputStream(System.getProperty("test.dir")+"/test.properties" ));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Enumeration propNames = p.propertyNames();
		while (propNames.hasMoreElements()) {
			String name = propNames.nextElement().toString();
			System.setProperty(name, p.getProperty(name));
		}
    }
	
	 /**
	 * This is a unit test to translate SIP metadata into a granule entry in database.
	 * It sets up system properties required by Ingest, locate the SIP file, 
	 * utilizes ingest-api package to retrieve IngestProfile, and uses Inventory
	 * API to translate the metadata in SIP into Granule data model and store
	 * the metadata into the database.
	 * 
	 * The API also return in ServiceProfile to include the Archive Profile information.
	 * 
	 * The property file, <i>test.properties</i>, is required to provide all the 
	 * necessary properties to run this test.
	 * 
	 * <p>
	 * It loops through property "number.sip.files" to perform the same test
	 * on each sip file. 
	 * <p>
	 * Example code to use to translate SIP metadata into granule database inventory
	 * is shown as below.
	 * <p>
	 * Input: SIP file
	 * <pre>
	 * ServiceProfile serviceProfile = 
	 *				ServiceProfileFactory.getInstance().
	 *				createServiceProfileFromMessage(new File(filename));
	 * try {
	 * 	  Inventory inventory = InventoryFactory.getInstance().createInventory();
	 *    inventory.storeServiceProfile(serviceProfile);
	 * } catch InventoryException e {
	 *	  System.out.println(e.getMessage());
	 * }
	 * </pre>
	 * Output: Verify if the granule metadata is in database;
	 * The returned ServiceProfile shall contain information for ArchiveProfile.
	 */
	@Test
	public void testIngestSIP() {
		try {
			Dataset ds = null;
			Integer count = new Integer(System.getProperty("number.sip.files"));	
			Inventory inventory = InventoryFactory.getInstance().createInventory();
			 for(int i=0; i<count; i++) {
				System.out.println("ingest.sip.test.filename."+(i+1));
				String filename = System.getProperty("ingest.sip.test.filename."+(i+1));	
				System.out.println("Use File: "+filename);
				ServiceProfile serviceProfile = 
					ServiceProfileFactory.getInstance().
					createServiceProfileFromMessage(new File(filename));		
				Set<gov.nasa.podaac.common.api.serviceprofile.Granule>
					sipGranuleSet = serviceProfile.getProductProfile()
									.getIngestProfile().getCompleteContent().getGranules();
				System.out.println("#granules found: "+sipGranuleSet.size());
				for (gov.nasa.podaac.common.api.serviceprofile.Granule sipGranule : sipGranuleSet) {			
					String dsShortName = sipGranule.getDatasetName();	
					System.out.println("name: " + dsShortName);
					if (dsShortName == null) { // if granule from sip does not have datasetname
						System.out.println("Using suuplied dsName");
						dsShortName = "dsShortName";
						sipGranule.setDatasetName(dsShortName);
					}
					// test dataset's existence
					Query q = QueryFactory.getInstance().createQuery();
					System.out.println("DatasetName: "+dsShortName);
					 ds = q.findDatasetByShortName(dsShortName);
					if (ds == null) { 
						System.out.println("Creating Dataset: " + dsShortName);
						// no such dataset and create one with test data
						// for other fields
						ds = new Dataset();
						ds.setShortName(dsShortName);
						ds.setLongName(dsShortName);
						ds.setProcessingLevel("dsLevel");
						ds.setRemoteDataset("R");
						DatasetRegion dr = new DatasetRegion();
						
						dr.setRegion("added Region");
						dr.setRegionDetail("added region detail");
						ds.getRegionSet().add(dr);
						ds.setTemporalResolution("dsTemporalResolution");
						ds.setLatitudeResolution(180.0);
						ds.setLongitudeResolution(90.0);				
					} else {
						
						ds = q.fetchDatasetById(ds.getDatasetId(), "provider", "locationPolicySet");
						System.out.println("Fetched Short Name " + ds.getShortName());
						System.out.println("Ds id: " + ds.getDatasetId());
						
					}
					Provider pr = ds.getProvider();
					if (pr == null || pr.getShortName()==null) {
						pr = q.findProviderByShortName("CLWONG");
						if (pr==null) {
							pr = new Provider();
							pr.setShortName("CLWONG");
							pr.setLongName("Cynthia L. Wong");
							pr.setType("TEST");
						}												
						ds.setProvider(pr);
					}
					Set<DatasetLocationPolicy> locationPolicySet = ds.getLocationPolicySet();
					if (locationPolicySet == null || locationPolicySet.isEmpty()) {
						System.out.println("Creating Location Policies");
						DatasetLocationPolicy locationPolicy = new DatasetLocationPolicy();
//					    locationPolicy.setBasePath("file://"+System.getProperty("archive.base.path"));
//					    locationPolicy.setType(LocationPolicyType.ARCHIVE_SIMULATED.toString());
//					    ds.add(locationPolicy);
						//locationPolicy = new DatasetLocationPolicy();
					    locationPolicy.setBasePath("ftp://localhost"+System.getProperty("archive.base.path"));
					    locationPolicy.setType(LocationPolicyType.REMOTE_FTP.toString());
					    ds.add(locationPolicy);
						locationPolicy = new DatasetLocationPolicy();
					    locationPolicy.setBasePath("http://localhost"+System.getProperty("archive.base.path"));
					    locationPolicy.setType(LocationPolicyType.REMOTE_OPENDAP.toString());
					    ds.add(locationPolicy);
					}					

					DatasetPolicy policy = null;
					Dataset dsPolicy = q.fetchDatasetById(ds.getDatasetId(), "datasetPolicy");
					if (dsPolicy != null) policy = dsPolicy.getDatasetPolicy();
					else {
						if (policy==null) policy = new DatasetPolicy();
						policy.setAccessType(AccessType.OPEN.toString());
						policy.setBasePathAppendType(AppendBasePathType.NONE.toString());
						policy.setChecksumType("MD5");
						policy.setDataClass("PUBLIC");
						policy.setDataFormat("DATA");
						policy.setCompressType("GZIP");
						policy.setSpatialType("NONE");
						policy.setAccessConstraint("READ");
						policy.setUseConstraint("READ");
						policy.setDataset(ds);
						ds.setDatasetPolicy(policy);
					}
					
					DatasetCoverage coverage = null;
					Dataset dsCoverage = q.fetchDatasetById(ds.getDatasetId(), "datasetCoverage");
					if (dsCoverage != null) coverage = dsCoverage.getDatasetCoverage();
					else {
						if (coverage==null) coverage = new DatasetCoverage();
						coverage.setEastLon(180.);
						coverage.setWestLon(-180.);
						coverage.setNorthLat(90.);
						coverage.setSouthLat(-90.);
						coverage.setStartTime(new Date());
						coverage.setDataset(ds);
						ds.setDatasetCoverage(coverage);
					}
					
					
					
					DataManager manager = DataManagerFactory.getInstance().createDataManager();
					manager.addProvider(pr);
					manager.addDatasetPolicy(policy);
					manager.addDatasetCoverage(coverage);
					ds = manager.addDataset(ds);			
					dsIdList.add(ds.getDatasetId());
					
					Integer[] elementIds = {2, 8, 15, 22, 23, 28};
					
					/*if(ds.getDatasetElementSet().isEmpty()){
					for (Integer elementId : elementIds) {
						
						DatasetElement DatasetElement = new DatasetElement();
						DatasetElement.setDataset(ds);
						DatasetElement.setElementDD(q.findElementDDById(elementId));
						DatasetElement.setScope("BOTH");
						DatasetElement.setObligationFlag('O');
						manager.addDatasetElement(DatasetElement);
					}
					}*/
//					Integer[] elementIds = {2, 7, 14, 22, 24, 27};
//					for (Integer elementId : elementIds) {
//						GranuleElement granuleElement = new GranuleElement();
//						GranuleElementPK pk = new GranuleElementPK();
//						pk.setDataset(ds);
//						pk.setElementDD(q.findElementDDById(elementId));
//						granuleElement.setGranuleElementPK(pk);
//						granuleElement.setObligationFlag('O');
//						manager.addGranuleElement(granuleElement);
//					}

					System.out.println("granule name: " + sipGranule.getName());
					// test replacement
					Granule g = q.findGranule(sipGranule.getName(), ds);
					if (g != null) {
						System.out.println("Granule is not null");
					//	if(sipGranule.getReplace() != null){
							sipGranule.setReplace(sipGranule.getName());
							sipGranule.setName(sipGranule.getName());
					//	}
					}
					
					// test data
					
					GranuleHistory history = sipGranule.createGranuleHistory();
					if(sipGranule.getGranuleHistory()!= null){
						history.setVersion(sipGranule.getGranuleHistory().getVersion());
						history.setCreateDate(new Date());
						history.setLastRevisionDate(new Date());
						history.setRevisionHistory("revision history");
						sipGranule.setGranuleHistory(history);
					}
					
					

				} // end for

				System.out.println("Starting storeServiceProfile...");
				long time = System.currentTimeMillis();
				
				inventory.storeServiceProfile(serviceProfile);
				
				
				System.out.println("storeServiceProfile took "+(System.currentTimeMillis()-time)/1000.);
				System.out.println(filename+".aip.xml");
				serviceProfile.toFile(filename+".aip.xml");
				
				if(ds.isRemote()){
					return;
				}
				
				ArchiveProfile archiveProfile = serviceProfile.getProductProfile().getArchiveProfile();
				List<ArchiveGranule> archiveGranules = archiveProfile.getGranules();
				for (ArchiveGranule archiveGranule : archiveGranules) {
					Integer id = new Integer(archiveGranule.getGranuleId().toString());
					Query q = QueryFactory.getInstance().createQuery();
					Granule g = q.fetchGranuleById(id, "granuleArchiveSet");					
					Set<GranuleArchive> granuleArchiveSet = g.getGranuleArchiveSet();
					List<ArchiveFileInfo> files = archiveGranule.getFiles();
					for (ArchiveFileInfo file : files) {
						boolean found = false;
						for (GranuleArchive archive : granuleArchiveSet) {
							System.out.println("Inventory File: "+ StringUtility.cleanPaths(g.getRootPath(), g.getRelPath(), archive.getName()));
							System.out.println("Archive File  : "+ file.getDestination().toString());
							if ((StringUtility.cleanPaths(g.getRootPath(), g.getRelPath(), archive.getName())).equalsIgnoreCase(file.getDestination().toString()))
								{
									found = true;
									System.out.println("Found file: " + StringUtility.cleanPaths(g.getRootPath(), g.getRelPath(), archive.getName()));
								}
						}
						assertTrue(found);
					}
				}
				
				
				
			}
		} catch (InventoryException iex) {
			System.out.println(iex.getMessage());
			iex.printStackTrace();
			fail("InventoryException while storeServiceProfile!!");
		} catch (Exception exception) {
			exception.printStackTrace();
			fail("Not passed");
		}
	}
	
	@Test
	public void testCloseInventory() {
		Inventory session = InventoryFactory.getInstance().createInventory();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		session.closeSession();
	}
}
