package gov.nasa.podaac.inventory.test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gov.nasa.podaac.common.api.metadatamanifest.MetadataField;
import gov.nasa.podaac.common.api.metadatamanifest.MetadataManifest;
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfile;
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfileException;
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfileFactory;
import gov.nasa.podaac.inventory.api.Constant;
import gov.nasa.podaac.inventory.api.DataManager;
import gov.nasa.podaac.inventory.api.DataManagerFactory;
import gov.nasa.podaac.inventory.api.Inventory;
import gov.nasa.podaac.inventory.api.InventoryFactory;
import gov.nasa.podaac.inventory.api.Query;
import gov.nasa.podaac.inventory.api.QueryFactory;
import gov.nasa.podaac.inventory.exceptions.InventoryException;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.DatasetParameter;
import gov.nasa.podaac.inventory.model.Granule;
import junit.framework.TestCase;

public class InventoryUnitTests300 extends TestCase {

	private static Integer oId = null;
	private static final String DATASETNAME = "dsShortName";
	
	
//	public void testUpdateStatus(){
//		System.out.println("UPDATE GRANULE Test");
//		Query q = QueryFactory.getInstance().createQuery();
//		q.updateGranuleStatusByID(3339693, Constant.GranuleStatus.ONLINE);
//		
//		
//		List<Dataset> datasets = q.listDataset();
//	
//		List<Granule> gl = q.listGranuleByDateRange(1295626062000L, 1395626062000L, "archive_time_long", 218);
//		for(Granule eg : gl){
//			System.out.println("Size of spatial: " + eg.getGranuleSpatialSet().size());
//		}
//		
//		
//	}

	public void testReadDatasetMetatag(){
		
		System.out.println("ReadDatasetMetata Test");
		Query q = QueryFactory.getInstance().createQuery();
		
		Dataset d = q.findDatasetByShortName(DATASETNAME);
		assertNull(d.getMetadata());
		System.out.println("\t"+DATASETNAME + " metadata: null");
		
	}
	
	public void testAddDatasetMetaTag(){
		System.out.println("ReadDatasetMetata Test");
		Query q = QueryFactory.getInstance().createQuery();
		DataManager manager = DataManagerFactory.getInstance().createDataManager();
		
		Dataset d = q.findDatasetByShortName(DATASETNAME);
		assertNull(d.getMetadata());
		d.setMetadata("metadata test");
		manager.updateDataset(d);
		
		d = q.findDatasetByShortName(DATASETNAME);
		assertEquals("metadata test",d.getMetadata());
		System.out.println("\t"+DATASETNAME + " metadata: " + "metadata test");

	}
	
//	public void testAddDatasetManifest(){
//		//This is a catch all test for testing the new manifest fields. We'll want to list what we're testing in each version
//		//3.0.0: added manifest field to Dataset
//		
//		System.out.println("Testing AddDatasetManifest [dataset_metadata]");
//		
//		Query q = QueryFactory.getInstance().createQuery();
//		DataManager manager = DataManagerFactory.getInstance().createDataManager();
//		Inventory inventory = InventoryFactory.getInstance().createInventory();
//		
//		Integer objectId = null;
//		try{
//				System.out.println("ingest.metadata.manifest.xml");
//				String filename = System.getProperty("ingest.metadata.manifest.xml.patch.add");	
//				System.out.println("Use File: "+filename);
//				
//				 byte[] buffer = new byte[(int) new File(filename).length()];
//				    BufferedInputStream f = new BufferedInputStream(new FileInputStream(filename));
//				    f.read(buffer);
//				  String xml = new String(buffer);
//				
//				//create manifest
//				MetadataManifest mf = new MetadataManifest(xml);
//				
//				System.out.println("Number of XML Elements: "+mf.getFields().size());
//				
//				
//				//System.out.println("Starting storeServiceProfile...");
//				long time = System.currentTimeMillis();
//				
//				MetadataManifest m2 = inventory.processManifest(mf);
//				
//				System.out.println("Inventory Process Manifest took "+(System.currentTimeMillis()-time)/1000.);
//				for(MetadataField mef : m2.getFields()){
//					if(mef.getName().equals("object_id"))
//						oId = Integer.valueOf(mef.getValue());
//				}
//				System.out.println("ObjectId: " + oId);
//								
//		} catch (InventoryException iex) {
//			System.out.println(iex.getMessage());
//			fail("InventoryException while processManifest!!");
//		} catch (Exception exception) {
//			System.out.println(exception.getMessage());
//			exception.printStackTrace();
//			fail("Not passed");
//		}
//		finally{
//			inventory.closeSession();
//		}
//		
//		Dataset d = q.findDatasetById(oId);
//		assertNotNull(d.getMetadata());
//	}
	
//	public void testUpdateManifest(){
//		System.out.println("Testing UpdateManifest [metadata]");
//		System.out.println("ObjectId: " + oId);
//		Query q = QueryFactory.getInstance().createQuery();
//		DataManager manager = DataManagerFactory.getInstance().createDataManager();
//		Inventory inventory = InventoryFactory.getInstance().createInventory();
//		
//		try{
//				String filename = System.getProperty("ingest.metadata.manifest.xml.patch.update");	
//				System.out.println("Use File: "+filename);
//				
//				 byte[] buffer = new byte[(int) new File(filename).length()];
//				    BufferedInputStream f = new BufferedInputStream(new FileInputStream(filename));
//				    f.read(buffer);
//				  String xml = new String(buffer);
//				
//				//create manifest
//				MetadataManifest mf = new MetadataManifest(xml);
//				MetadataField m = new MetadataField();
//				m.setName("dataset_datasetId");
//				Integer id = new Integer(oId);
//				m.setValue(id);
//				m.setRequired(true);
//				m.setType("int");
//				mf.getFields().add(m);
//				
//				System.out.println("Number of XML Elements: "+mf.getFields().size());
//				
//				inventory.processManifest(mf);								
//		} catch (InventoryException iex) {
//			System.out.println(iex.getMessage());
//			fail("InventoryException while processManifest!!");
//		} catch (Exception exception) {
//			System.out.println(exception.getMessage());
//			exception.printStackTrace();
//			fail("Not passed");
//		}
//		finally{
//			inventory.closeSession();
//		}
//		
//		Dataset d = q.findDatasetById(oId);
//		assertNull(d.getMetadata());
//		
//		System.out.println("Deleting test dataset...");
//		manager.deleteDataset(d);
//		System.out.println("Deleted...");
//	}
	
	public void testDataParameterList(){
		 //This test is done to make sure two identical parameters, except for the detail, can both be added to the parameter list.
		
		 Set<DatasetParameter> pSet = new HashSet<DatasetParameter>();
		 DatasetParameter dp = new DatasetParameter();
		 dp.setCategory("Category");
		 dp.setDatasetId(1);
		 dp.setTerm("Term");
		 dp.setTopic("Topic");
		 dp.setVariable("Variable");
		 dp.setVariableDetail("Variable Detail");
		 
		 DatasetParameter dp2 = new DatasetParameter();
		 dp2.setCategory("Category");
		 dp2.setDatasetId(1);
		 dp2.setTerm("Term");
		 dp2.setTopic("Topic");
		 dp2.setVariable("Variable");
		 dp2.setVariableDetail("Variable Detail 2");
		 
		 DatasetParameter dp3 = new DatasetParameter();
		 dp3.setCategory("Category");
		 dp3.setDatasetId(1);
		 dp3.setTerm("Term");
		 dp3.setTopic("Topic");
		 dp3.setVariable("Variable");
		 
		 
		 pSet.add(dp);
		 pSet.add(dp2);
		 pSet.add(dp3);
		 
		 assertTrue(pSet.size() == 3);

	}
	
	
	public void testSipGranuleSpatial(){
		Query q = QueryFactory.getInstance().createQuery();
		Inventory inventory = InventoryFactory.getInstance().createInventory();
		String filename = System.getProperty("ingest.sip.test.polygon.filename");	
		System.out.println("Use File: "+filename);
		ServiceProfile serviceProfile = null;
		try {
			serviceProfile = ServiceProfileFactory.getInstance().
			createServiceProfileFromMessage(new File(filename));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("IOException reading Sip File");
		} catch (ServiceProfileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Exception reading SIP (format)");
		}		
		Set<gov.nasa.podaac.common.api.serviceprofile.Granule>
			sipGranuleSet = serviceProfile.getProductProfile()
							.getIngestProfile().getCompleteContent().getGranules();
		System.out.println("#granules found: "+sipGranuleSet.size());
		for (gov.nasa.podaac.common.api.serviceprofile.Granule sipGranule : sipGranuleSet) {
			System.out.println("Starting storeServiceProfile...");
			long time = System.currentTimeMillis();
			Dataset ds =  q.findDatasetByShortName(sipGranule.getDatasetName());
			Granule g = q.findGranule(sipGranule.getName(), ds);
			if (g != null) {
				sipGranule.setReplace(sipGranule.getName());
				sipGranule.setName(sipGranule.getName());
			}
			try {
				inventory.storeServiceProfile(serviceProfile);
			} catch (InventoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail("Failed to store service profile in Inventory.");
			}
			System.out.println("storeServiceProfile took "+(System.currentTimeMillis()-time)/1000.);
		}
		
		
	}
	
}
