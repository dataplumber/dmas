// Copyright 2007, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
 
package gov.nasa.podaac.inventory.test;

import gov.nasa.podaac.common.api.metadatamanifest.Constant;
import gov.nasa.podaac.common.api.metadatamanifest.MetadataField;
import gov.nasa.podaac.common.api.metadatamanifest.MetadataManifest;
import gov.nasa.podaac.common.api.metadatamanifest.MetadataManifestException;
import gov.nasa.podaac.common.api.serviceprofile.ArchiveFileInfo;
import gov.nasa.podaac.common.api.serviceprofile.ArchiveGranule;
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
import gov.nasa.podaac.inventory.core.DatasetMetadataImpl;
import gov.nasa.podaac.inventory.exceptions.InventoryException;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.DatasetCoverage;
import gov.nasa.podaac.inventory.model.DatasetElement;
import gov.nasa.podaac.inventory.model.DatasetLocationPolicy;
import gov.nasa.podaac.inventory.model.DatasetPolicy;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.GranuleArchive;
import gov.nasa.podaac.inventory.model.GranuleElement;
import gov.nasa.podaac.inventory.model.Provider;


import java.io.BufferedInputStream;
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
 * @version $Id: GranuleMetadataTest.java 4662 2010-03-25 22:33:38Z gangl $
 */
public class MetadataManifestIT extends TestCase {
	
	private List<Integer> dsIdList = new ArrayList<Integer>();
	private Integer objectId;
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
	
		
//	@Test
//	public void testListObjectTypes(){
//		//this tests to see if we can list the main types of objects to the web browser:
//		//dataset, granule, collection, source, sensor, provider, project, contact, element
//		
//		Inventory inventory = InventoryFactory.getInstance().createInventory();
//		try {
//			
//			String xml = "<dataset type=\"list\"></dataset>"; 
//			
//		MetadataManifest mf = null;
//		try {
//			mf = new MetadataManifest(xml);
//		} catch (MetadataManifestException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
////		mf = new MetadataManifest();
////		mf.setObject(Constant.ObjectType.DATASET);
////		mf.setType(Constant.ActionType.LIST);
//		
//		System.out.println("Object: "+mf.getObjectType()+" Action: "+mf.getActionType());
//		
//		MetadataManifest m2 = inventory.processManifest(mf);
//		System.out.println(m2.generateXml());
//
//		System.out.println("List size: " + m2.getFields().size());
//		
//		
//		//		
//		mf.setObject(Constant.ObjectType.SOURCE);
//		mf.setType(Constant.ActionType.LIST);
//		m2 = inventory.processManifest(mf);
//		System.out.println("Source List size: " + m2.getFields().size());
//		//System.out.println(m2.generateXml());
//		//		
//		mf.setObject(Constant.ObjectType.SENSOR);
//		mf.setType(Constant.ActionType.LIST);
//		m2 = inventory.processManifest(mf);
//		System.out.println("Sensor List size: " + m2.getFields().size());
//		//System.out.println(m2.generateXml());
////		
//		mf.setObject(Constant.ObjectType.PROVIDER);
//		mf.setType(Constant.ActionType.LIST);
//		m2 = inventory.processManifest(mf);
//		System.out.println("Provider List size: " + m2.getFields().size());
//		//System.out.println(m2.generateXml());
////		
//		mf.setObject(Constant.ObjectType.PROJECT);
//		mf.setType(Constant.ActionType.LIST);
//		m2 = inventory.processManifest(mf);
//		System.out.println(m2.generateXml());
////		
//		mf.setObject(Constant.ObjectType.CONTACT);
//		mf.setType(Constant.ActionType.LIST);
//		m2 = inventory.processManifest(mf);
//		System.out.println(m2.generateXml());
////		
//		mf.setObject(Constant.ObjectType.ELEMENT);
//		mf.setType(Constant.ActionType.LIST);
//		m2 = inventory.processManifest(mf);
//		System.out.println(m2.generateXml());
//		
//		mf.setObject(Constant.ObjectType.COLLECTION);
//		mf.setType(Constant.ActionType.LIST);
//		m2 = inventory.processManifest(mf);
//		System.out.println(m2.generateXml());
//		
//		} catch (InventoryException e) {
//			// TODO Auto-generated catch block
//			System.out.println(e.getMessage());
//			e.printStackTrace();
//		}
//	}
//	
	@Test
	public void testTemplates(){
//		Inventory inventory = InventoryFactory.getInstance().createInventory();
//		try {
//			String xml = "<dataset type=\"template\"></dataset>"; 
//			
//			MetadataManifest mf = new MetadataManifest(xml);
//			MetadataManifest m2;
//			
//			mf.setObject(Constant.ObjectType.DATASET);
//			mf.setType(Constant.ActionType.TEMPLATE);
//			m2 = inventory.processManifest(mf);
//			System.out.println(m2.generateXml());
//			
//			mf = new MetadataManifest();
//			mf.setObject(Constant.ObjectType.SOURCE);
//			mf.setType(Constant.ActionType.TEMPLATE);
//			m2 = inventory.processManifest(mf);
//			System.out.println(m2.generateXml());
//			
//			mf = new MetadataManifest();
//			mf.setObject(Constant.ObjectType.SENSOR);
//			mf.setType(Constant.ActionType.TEMPLATE);
//			m2 = inventory.processManifest(mf);
//			System.out.println(m2.generateXml());
//			
//			mf = new MetadataManifest();
//			mf.setObject(Constant.ObjectType.CONTACT);
//			mf.setType(Constant.ActionType.TEMPLATE);
//			m2 = inventory.processManifest(mf);
//			System.out.println(m2.generateXml());
//			
//			mf = new MetadataManifest();
//			mf.setObject(Constant.ObjectType.ELEMENT);
//			mf.setType(Constant.ActionType.TEMPLATE);
//			m2 = inventory.processManifest(mf);
//			System.out.println(m2.generateXml());
//			
//			mf = new MetadataManifest();
//			mf.setObject(Constant.ObjectType.PROJECT);
//			mf.setType(Constant.ActionType.TEMPLATE);
//			m2 = inventory.processManifest(mf);
//			System.out.println(m2.generateXml());
////			
//			mf = new MetadataManifest();
//			mf.setObject(Constant.ObjectType.PROVIDER);
//			mf.setType(Constant.ActionType.TEMPLATE);
//			m2 = inventory.processManifest(mf);
//			System.out.println(m2.generateXml());
//			
//			mf = new MetadataManifest();
//			mf.setObject(Constant.ObjectType.COLLECTION);
//			mf.setType(Constant.ActionType.TEMPLATE);
//			m2 = inventory.processManifest(mf);
//			System.out.println(m2.generateXml());
//			
//		}
//		catch(Exception e){
//			e.printStackTrace();
//			assert false;
//		}
	}
	
	/*  <field name=\"collection_collectionId\" type=\"int\" required=\"true\">4</field>
	 * <collection type=\"template\">	<field name=\"collectionDataset_granuleFlag1\" type=\"char\" required=\"true\">A</field>	<field name=\"collectionDataset_granuleRange3601\" type=\"char\" required=\"true\">N</field>	<field name=\"collectionDataset_startGranuleId1\" type=\"int\" required=\"false\"></field>	<field name=\"collectionLegacyProduct_legacyProductId\" type=\"char\" required=\"true\">999</field>	<field name=\"collectionProduct_visibileFlag\" type=\"char\" required=\"true\">N</field>	<field name=\"collection_type\" type=\"char\" required=\"true\">PRODUCT</field>	<field name=\"collectionContact_contactId1\" type=\"char\" required=\"true\">16</field>	<field name=\"collectionDataset_stopGranuleId1\" type=\"int\" required=\"false\"></field>	<field name=\"collection_longName\" type=\"char\" required=\"true\">NEW GHRSST L4 Foundation Sea Surface Temperature analysis for the North Sea and Baltic Sea</field>	<field name=\"collection_description\" type=\"char\" required=\"true\">NEW TBD</field>	<field name=\"collection_shortName\" type=\"char\" required=\"true\">NEW GHRSST Level 4 Denmark DMI North Sea Baltic SST</field>	<field name=\"collectionDataset_datasetId1\" type=\"int\" required=\"true\">88</field>	<field name=\"collection_fullDescription\" type=\"char\" required=\"true\">NEW</field>	<field name=\"collectionProduct_productId\" type=\"char\" required=\"true\">999</field></collection>

	 */
	
	@Test
	public void testCollectionInstance(){
//		Inventory inventory = InventoryFactory.getInstance().createInventory();
//		try {
//		MetadataManifest mf = new MetadataManifest();
//		mf.setObject(Constant.ObjectType.COLLECTION);
//		mf.setType(Constant.ActionType.LIST);
//		
//		MetadataField field = new MetadataField();
//		field.setName("object_id");
//		field.setValue("181"); //ascat aggregate
//		//field.setValue("182"); //ascat single
//		mf.getFields().add(field);
//		MetadataManifest m2 = inventory.processManifest(mf);
//		System.out.println(m2.generateXml());
//		}
//		catch(Exception e){
//			e.printStackTrace();
//			System.out.println(e.getMessage());
//			assert false;
//		}
	}
	
	
//	@Test
//	public void testUpdateCollectionMetadataManifest() {
//		Inventory inventory = null;
//		//test-manifest-source-add.xml
//		try {
//				 inventory = InventoryFactory.getInstance().createInventory();
//				//<field name=\"project_projectId\" type=\"int\" required=\"true\"></field> 
//				//String xml= "<collection type=\"update\"><field name=\"collection_collectionId\" type=\"int\" required=\"true\">88</field>	<field name=\"collectionDataset_granuleFlag1\" type=\"char\" required=\"true\">A</field>	<field name=\"collectionDataset_granuleRange3601\" type=\"char\" required=\"true\">N</field>	<field name=\"collectionDataset_startGranuleId1\" type=\"int\" required=\"false\"></field>	<field name=\"collectionLegacyProduct_legacyProductId\" type=\"char\" required=\"true\">9</field>	<field name=\"collectionProduct_visibleFlag\" type=\"char\" required=\"true\">N</field>	<field name=\"collection_type\" type=\"char\" required=\"true\">PRODUCT</field>	<field name=\"collectionContact_contactId1\" type=\"char\" required=\"true\">15</field>	<field name=\"collectionDataset_stopGranuleId1\" type=\"int\" required=\"false\"></field>	<field name=\"collection_longName\" type=\"char\" required=\"true\">NEW GHRSST L4 Foundation Sea Surface Temperature analysis for the North Sea and Baltic Sea</field>	<field name=\"collection_description\" type=\"char\" required=\"true\">NEW TBD</field>	<field name=\"collection_shortName\" type=\"char\" required=\"true\">NEW GHRSST Level 4 Denmark DMI North Sea Baltic SST</field>	<field name=\"collectionDataset_datasetId1\" type=\"int\" required=\"true\">22</field>	<field name=\"collection_fullDescription\" type=\"char\" required=\"true\">NEW</field>	<field name=\"collectionProduct_productId\" type=\"char\" required=\"true\">9</field></collection>";
//				String xml = "<collection type=\"update\"><field name=\"collectionProduct_productId\" type=\"char\" required=\"true\"></field>        <field name=\"collection_type\" type=\"char\" required=\"true\">PRODUCT</field>        <field name=\"collection_fullDescription\" type=\"char\" required=\"true\">null</field>        <field name=\"collectionDataset_stopGranuleId1\" type=\"int\" required=\"false\"></field>        <field name=\"collectionDataset_granuleFlag1\" type=\"char\" required=\"true\">A</field>        <field name=\"collectionDataset_datasetId1\" type=\"int\" required=\"true\">330</field><field name=\"collectionLegacyProduct_legacyProductId\" type=\"int\" required=\"true\">9</field>        <field name=\"collection_longName\" type=\"char\" required=\"true\">SeaWinds on QuikSCAT Level 3 Sigma-0 Polar-Stereographic Browse Maps (Reduced) of Antarctica</field>        <field name=\"collectionProduct_visibleFlag\" type=\"char\" required=\"true\">Y</field>        <field name=\"collectionContact_contactId1\" type=\"char\" required=\"true\">1</field>        <field name=\"collection_description\" type=\"char\" required=\"true\">null</field>        <field name=\"collectionDataset_startGranuleId1\" type=\"int\" required=\"false\"></field>        <field name=\"collection_shortName\" type=\"char\" required=\"true\">QSCAT_BYU_L3_OW_SIGMA0_ANTARCTIC_POLAR-STEREOGRAPHIC_BROWSE_MAPS_LITE</field>        <field name=\"collection_collectionId\" type=\"int\" required=\"true\">293</field>        <field name=\"collectionDataset_granuleRange3601\" type=\"char\" required=\"true\">N</field></collection>";		
//				MetadataManifest mf = new MetadataManifest(xml);
//				//mf = inventory.processManifest(mf);
//				
//		} 
////		catch (InventoryException iex) {
////			System.out.println(iex.getMessage());
////			fail("InventoryException while processManifest!!");
////		}
//		catch (Exception exception) {
//			System.out.println(exception.getMessage());
//			exception.printStackTrace();
//			fail("Not passed");
//		}
//		finally{
//			inventory.closeSession();
//		}
//	}
//	
	@Test
	public void testAddCollectionMetadataManifest() {
//		Inventory inventory = null;
//		//test-manifest-source-add.xml
//		try {
//				 inventory = InventoryFactory.getInstance().createInventory();
//				//<field name=\"project_projectId\" type=\"int\" required=\"true\"></field> 
//				//String xml= "<collection type=\"create\">	<field name=\"collectionDataset_granuleFlag1\" type=\"char\" required=\"true\">A</field>	<field name=\"collectionDataset_granuleRange3601\" type=\"char\" required=\"true\">N</field>	<field name=\"collectionDataset_startGranuleId1\" type=\"int\" required=\"false\"></field>	<field name=\"collectionLegacyProduct_legacyProductId\" type=\"char\" required=\"true\">999</field>	<field name=\"collectionProduct_visibleFlag\" type=\"char\" required=\"true\">N</field>	<field name=\"collection_type\" type=\"char\" required=\"true\">PRODUCT</field>	<field name=\"collectionContact_contactId1\" type=\"char\" required=\"true\">16</field>	<field name=\"collectionDataset_stopGranuleId1\" type=\"int\" required=\"false\"></field>	<field name=\"collection_longName\" type=\"char\" required=\"true\">NEW GHRSST L4 Foundation Sea Surface Temperature analysis for the North Sea and Baltic Sea</field>	<field name=\"collection_description\" type=\"char\" required=\"true\">NEW TBD</field>	<field name=\"collection_shortName\" type=\"char\" required=\"true\">NEW GHRSST Level 4 Denmark DMI North Sea Baltic SST</field>	<field name=\"collectionDataset_datasetId1\" type=\"int\" required=\"true\">88</field>	<field name=\"collection_fullDescription\" type=\"char\" required=\"true\">NEW</field>	<field name=\"collectionProduct_productId\" type=\"char\" required=\"true\">999</field></collection>";
//				 String xml= "<collection type=\"create\">" +
//				 		//"<field name=\"collectionLegacyProduct_legacyProductId\" type=\"char\" required=\"true\">999</field>" +
//				 	"<field name=\"collectionDataset_collectionId\" type=\"char\" required=\"true\"></field>" +
//				 		"<field name=\"collectionProduct_visibleFlag\" type=\"char\" required=\"true\">Y</field>" +
//				 		"<field name=\"collection_type\" type=\"char\" required=\"true\">PRODUCT</field>" +
//				 		"<field name=\"collectionContact_contactId1\" type=\"char\" required=\"true\">16</field>" +
//				 		"<field name=\"collection_longName\" type=\"char\" required=\"true\">NEW GHRSST L4 Foundation Sea Surface Temperature analysis for the North Sea and Baltic Sea</field>	" +
//				 		"<field name=\"collection_description\" type=\"char\" required=\"true\">NEW TBD</field>	" +
//				 		"<field name=\"collection_shortName\" type=\"char\" required=\"true\">NEW GHRSST Level 4 Denmark DMI North Sea Baltic SST</field>	" +
//				 		"<field name=\"collection_fullDescription\" type=\"char\" required=\"true\">NEW</field>	" +
//				 		"<field name=\"collectionProduct_productId\" type=\"char\" required=\"true\">999</field>" +
//				 		"</collection>";
//
//				 
//				 MetadataManifest mf = new MetadataManifest(xml);
//				mf = inventory.processManifest(mf);
//
//		} 
//				catch (InventoryException iex) {
//			System.out.println(iex.getMessage());
//			fail("InventoryException while processManifest!!");
//		}
//		catch (Exception exception) {
//			System.out.println(exception.getMessage());
//			exception.printStackTrace();
//			fail("Not passed");
//		}
//		finally{
//			inventory.closeSession();
//		}
	}
	
	@Test
	public void testDatasetInstance(){
		Inventory inventory = InventoryFactory.getInstance().createInventory();
		try {
		MetadataManifest mf = new MetadataManifest();
		mf.setObject(Constant.ObjectType.DATASET);
		mf.setType(Constant.ActionType.LIST);
		
		MetadataField field = new MetadataField();
		field.setName("object_id");
		field.setValue("912");
		mf.getFields().add(field);
		MetadataManifest m2 = inventory.processManifest(mf);
		System.out.println(m2.generateXml());
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
			assert false;
		}
	}
	
	
	//PROVIDER
	
//	@Test
//	public void testProviderInstance(){
//		Inventory inventory = InventoryFactory.getInstance().createInventory();
//		try {
//		MetadataManifest mf = new MetadataManifest();
//		mf.setObject(Constant.ObjectType.PROVIDER);
//		mf.setType(Constant.ActionType.LIST);
//		
//		MetadataField field = new MetadataField();
//		field.setName("object_id");
//		field.setValue("3");
//		mf.getFields().add(field);
//		MetadataManifest m2 = inventory.processManifest(mf);
//		System.out.println(m2.generateXml());
//		}
//		catch(Exception e){
//			e.printStackTrace();
//			System.out.println(e.getMessage());
//			assert false;
//		}
//	
//	}
	/*<field name=\"provider_providerId\" type=\"int\" required=\"true\">3</field>
	 * <provider type=\"template\">	<field name=\"provider_type\" type=\"char\" required=\"true\">UP DATA-PROVIDER</field>	<field name=\"provider_longName\" type=\"char\" required=\"true\">UP Australian Burea of Meteorology</field>	<field name=\"provider_path1\" type=\"char\" required=\"true\">UP http://www.bom.gov.au</field>	<field name=\"provider_shortName\" type=\"char\" required=\"true\">UP AU/BOM</field></provider>
	 */
//	@Test
//	public void testAddProviderMetadataManifest() {
//		Inventory inventory = null;
//		//test-manifest-source-add.xml
//		try {
//				 inventory = InventoryFactory.getInstance().createInventory();
//				//<field name=\"project_projectId\" type=\"int\" required=\"true\"></field> 
//				String xml= "<provider type=\"create\">	<field name=\"provider_type\" type=\"char\" required=\"true\">UP DATA-PROVIDER</field>	<field name=\"provider_longName\" type=\"char\" required=\"true\">UP Australian Burea of Meteorology</field>	<field name=\"provider_path1\" type=\"char\" required=\"true\">UP http://www.bom.gov.au</field>	<field name=\"provider_shortName\" type=\"char\" required=\"true\">UP AU/BOM</field></provider>";
//				
//				MetadataManifest mf = new MetadataManifest(xml);
//				mf = inventory.processManifest(mf);
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
//	}
//	@Test
//	public void testUpdateProviderMetadataManifest() {
//		Inventory inventory = null;
//		//test-manifest-source-add.xml
//		try {
//				 inventory = InventoryFactory.getInstance().createInventory();
//				//<field name=\"project_projectId\" type=\"int\" required=\"true\"></field> 
//				String xml= "<provider type=\"update\"><field name=\"provider_providerId\" type=\"int\" required=\"true\">24</field>	<field name=\"provider_type\" type=\"char\" required=\"true\">UPDATED PROVIDER</field>	<field name=\"provider_longName\" type=\"char\" required=\"true\">UPDATED Australian Burea of Meteorology</field><field name=\"provider_path2\" type=\"char\" required=\"true\">UPDATED AND NEW http://www.bom.gov.au</field>	<field name=\"provider_path1\" type=\"char\" required=\"true\">UPDATED http://www.bom.gov.au</field><field name=\"provider_path3\" type=\"char\" required=\"true\">UPDATED XYZ http://www.bom.gov.au</field>	<field name=\"provider_shortName\" type=\"char\" required=\"true\">UPDATED AU/BOM</field></provider>";
//				
//				MetadataManifest mf = new MetadataManifest(xml);
//				mf = inventory.processManifest(mf);
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
//	}
	
	
	//END PROVIDER
	
	
	
//	@Test
//	public void testProjectInstance(){
//		Inventory inventory = InventoryFactory.getInstance().createInventory();
//		try {
//		MetadataManifest mf = new MetadataManifest();
//		mf.setObject(Constant.ObjectType.PROJECT);
//		mf.setType(Constant.ActionType.LIST);
//		
//		MetadataField field = new MetadataField();
//		field.setName("object_id");
//		field.setValue("3");
//		mf.getFields().add(field);
//		MetadataManifest m2 = inventory.processManifest(mf);
//		System.out.println(m2.generateXml());
//		}
//		catch(Exception e){
//			e.printStackTrace();
//			System.out.println(e.getMessage());
//			assert false;
//		}
//	
//	}
	
//	@Test
//	public void testAddProjectMetadataManifest() {
//		Inventory inventory = null;
//		//test-manifest-source-add.xml
//		try {
//				 inventory = InventoryFactory.getInstance().createInventory();
//				//<field name=\"project_projectId\" type=\"int\" required=\"true\"></field> 
//				String xml= "<project type=\"create\">	<field name=\"project_shortName\" type=\"char\" required=\"true\">NEW PROJECT</field>	<field name=\"project_longName\" type=\"char\" required=\"true\">NEW PROJECT LONG</field></project>";
//				
//				MetadataManifest mf = new MetadataManifest(xml);
//				mf = inventory.processManifest(mf);
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
//	}
//	@Test
//	public void testUpdateProjectMetadataManifest() {
//		Inventory inventory = null;
//		//test-manifest-source-add.xml
//		try {
//				 inventory = InventoryFactory.getInstance().createInventory();
//				//<field name=\"project_projectId\" type=\"int\" required=\"true\"></field> 
//				String xml= "<project type=\"update\">	<field name=\"project_projectId\" type=\"int\" required=\"true\">7</field><field name=\"project_shortName\" type=\"char\" required=\"true\">UPDATED PROJECT</field>	<field name=\"project_longName\" type=\"char\" required=\"true\">UPDATED PROJECT LONG</field></project>";
//				
//				MetadataManifest mf = new MetadataManifest(xml);
//				mf = inventory.processManifest(mf);
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
//	}
	
//	@Test
//	public void testElementInstance(){
//		Inventory inventory = InventoryFactory.getInstance().createInventory();
//		try {
//		MetadataManifest mf = new MetadataManifest();
//		mf.setObject(Constant.ObjectType.ELEMENT);
//		mf.setType(Constant.ActionType.LIST);
//		
//		MetadataField field = new MetadataField();
//		field.setName("object_id");
//		field.setValue("3");
//		mf.getFields().add(field);
//		MetadataManifest m2 = inventory.processManifest(mf);
//		System.out.println(m2.generateXml());
//		}
//		catch(Exception e){
//			e.printStackTrace();
//			System.out.println(e.getMessage());
//			assert false;
//		}
//	}
//	
	/*
	 * <field name=\"element_elementId\" type=\"int\" required=\"true\">3</field>
	 * <element type=\"template\">
	<field name=\"element_description\" type=\"char\" required=\"true\">A comment pertaining to the granule.</field>
	<field name=\"element_maxLength\" type=\"int\" required=\"true\"></field>
	<field name=\"element_longName\" type=\"char\" required=\"true\">Comment</field>
	<field name=\"element_scope\" type=\"char\" required=\"true\">null</field>
	<field name=\"element_shortName\" type=\"char\" required=\"true\">comment</field>
	
	<field name=\"element_type\" type=\"char\" required=\"true\">character</field>
</element>

	 */
	
	@Test
	public void testAddElementMetadataManifest() {
//		Inventory inventory = null;
//		//test-manifest-source-add.xml
//		try {
//				 inventory = InventoryFactory.getInstance().createInventory();
//				 
//				String xml= "<element type=\"create\">	<field name=\"element_description\" type=\"char\" required=\"true\">A comment pertaining to the granule.</field>	<field name=\"element_maxLength\" type=\"int\" required=\"true\">66</field>	<field name=\"element_longName\" type=\"char\" required=\"true\">Comment</field>	<field name=\"element_scope\" type=\"char\" required=\"true\">NEW ALL</field>	<field name=\"element_shortName\" type=\"char\" required=\"true\">NEW comment</field>		<field name=\"element_type\" type=\"char\" required=\"true\">character</field></element>";
//				
//				MetadataManifest mf = new MetadataManifest(xml);
//				mf = inventory.processManifest(mf);
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
	}
//	@Test
//	public void testUpdateElementMetadataManifest() {
//		Inventory inventory = null;
//		//test-manifest-source-add.xml
//		try {
//				 inventory = InventoryFactory.getInstance().createInventory();
//				 
//				String xml= "<element type=\"update\"><field name=\"element_elementId\" type=\"int\" required=\"true\">62</field>	<field name=\"element_description\" type=\"char\" required=\"true\">A comment pertaining to the granule.</field>	<field name=\"element_maxLength\" type=\"int\" required=\"true\">111111</field>	<field name=\"element_longName\" type=\"char\" required=\"true\">Comment</field>	<field name=\"element_scope\" type=\"char\" required=\"true\">UPDATED ALL</field>	<field name=\"element_shortName\" type=\"char\" required=\"true\">UPDATED comment</field>		<field name=\"element_type\" type=\"char\" required=\"true\">character</field></element>";
//				
//				MetadataManifest mf = new MetadataManifest(xml);
//				mf = inventory.processManifest(mf);
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
//	}
	
//	@Test
//	public void testContactInstance(){
//		Inventory inventory = InventoryFactory.getInstance().createInventory();
//		try {
//		MetadataManifest mf = new MetadataManifest();
//		mf.setObject(Constant.ObjectType.CONTACT);
//		mf.setType(Constant.ActionType.LIST);
//		
//		MetadataField field = new MetadataField();
//		field.setName("object_id");
//		field.setValue("3");
//		mf.getFields().add(field);
//		MetadataManifest m2 = inventory.processManifest(mf);
//		System.out.println(m2.generateXml());
//		}
//		catch(Exception e){
//			e.printStackTrace();
//			System.out.println(e.getMessage());
//			assert false;
//		}
//	}
//	@Test
//	public void testAddContactMetadataManifest() {
//		Inventory inventory = null;
//		//test-manifest-source-add.xml
//		try {
//				 inventory = InventoryFactory.getInstance().createInventory();
//				 //<field name=\"contact_contactId\" type=\"int\" required=\"true\">3</field>
//				String xml= "<contact type=\"create\">	<field name=\"contact_providerId\" type=\"int\" required=\"true\">1</field>	<field name=\"contact_role\" type=\"char\" required=\"true\">Technical Contact</field>	<field name=\"contac_notifyType\" type=\"char\" required=\"true\">null</field>	<field name=\"contact_phone\" type=\"char\" required=\"true\">818-354-4588</field><field name=\"contact_lastName\" type=\"char\" required=\"true\">Hausman</field>	<field name=\"contact_address\" type=\"char\" required=\"true\">4800 Oak Grove Drive Pasadena, CA 91109-8099</field>	<field name=\"contact_fax\" type=\"char\" required=\"true\">818-393-2718</field>	<field name=\"contact_middleName\" type=\"char\" required=\"true\">null</field>	<field name=\"contact_firstName\" type=\"char\" required=\"true\">NEW NEW Jessica</field>	<field name=\"contact_email\" type=\"char\" required=\"true\">Jessica.K.Hausman@jpl.nasa.gov</field>	</contact>";
//				
//				MetadataManifest mf = new MetadataManifest(xml);
//				mf = inventory.processManifest(mf);
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
//	}
	
//	@Test
//	public void testUpdateContactMetadataManifest() {
//		Inventory inventory = null;
//		
//		try {
//				 inventory = InventoryFactory.getInstance().createInventory();
//				 //<field name=\"contact_contactId\" type=\"int\" required=\"true\">52</field>
//				String xml= "<contact type=\"update\"><field name=\"contact_contactId\" type=\"int\" required=\"true\">52</field><field name=\"contact_providerId\" type=\"int\" required=\"true\">1</field>	<field name=\"contact_role\" type=\"char\" required=\"true\">Technical Contact</field>	<field name=\"contac_notifyType\" type=\"char\" required=\"true\">null</field>	<field name=\"contact_phone\" type=\"char\" required=\"true\">818-354-4588</field><field name=\"contact_lastName\" type=\"char\" required=\"true\">Hausman</field>	<field name=\"contact_address\" type=\"char\" required=\"true\">4800 Oak Grove Drive Pasadena, CA 91109-8099</field>	<field name=\"contact_fax\" type=\"char\" required=\"true\">818-393-2718</field>	<field name=\"contact_middleName\" type=\"char\" required=\"true\">null</field>	<field name=\"contact_firstName\" type=\"char\" required=\"true\">UPDATE Jessica</field>	<field name=\"contact_email\" type=\"char\" required=\"true\">Jessica.K.Hausman@jpl.nasa.gov</field>	</contact>";
//				MetadataManifest mf = new MetadataManifest(xml);
//				mf = inventory.processManifest(mf);
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
//	}
//	
	
//	@Test
//	public void testSourceInstance(){
//		Inventory inventory = InventoryFactory.getInstance().createInventory();
//		try {
//		MetadataManifest mf = new MetadataManifest();
//		mf.setObject(Constant.ObjectType.SOURCE);
//		mf.setType(Constant.ActionType.LIST);
//		
//		MetadataField field = new MetadataField();
//		field.setName("object_id");
//		field.setValue("3");
//		mf.getFields().add(field);
//		MetadataManifest m2 = inventory.processManifest(mf);
//		System.out.println(m2.generateXml());
//		}
//		catch(Exception e){
//			e.printStackTrace();
//			System.out.println(e.getMessage());
//			assert false;
//		}
//	}

//	@Test
//	public void testUpdateSourceMetadataManifest() {
//		Inventory inventory = null;
//		
//		try {
//				 inventory = InventoryFactory.getInstance().createInventory();
//			
//				 String xml= "<source type=\"update\"><field name=\"source_sourceId\" type=\"int\" required=\"true\">22</field><field name=\"source_type\" type=\"char\" required=\"true\">UPDATED SPACECRAFT</field><field name=\"source_inclinationAngle\" type=\"float\" required=\"true\">99.9</field><field name=\"source_description\" type=\"char\" required=\"true\">UPDATED The GOES system maintains a continuous data stream from a two-GOES system in support of the National Weather Service requirements. These satellites send weather data and pictures that cover various sections of the United States.</field><field name=\"source_longName\" type=\"char\" required=\"true\">UPDATED Geostationary Operational Environmental Satellite 11</field><field name=\"source_orbitPeriod\" type=\"float\" required=\"true\">9999</field><field name=\"source_shortName\" type=\"char\" required=\"true\">UPDATED GOES-11</field></source>";
//				MetadataManifest mf = new MetadataManifest(xml);
//				mf = inventory.processManifest(mf);
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
//	}
	
//	@Test
//	public void testAddSourceMetadataManifest() {
//		Inventory inventory = null;
//		//test-manifest-source-add.xml
//		try {
//				 inventory = InventoryFactory.getInstance().createInventory();
//			
//				String xml= "<source type=\"create\"><field name=\"source_type\" type=\"char\" required=\"true\">NEW SPACECRAFT</field><field name=\"source_inclinationAngle\" type=\"float\" required=\"true\">99.9</field><field name=\"source_description\" type=\"char\" required=\"true\">NEW The GOES system maintains a continuous data stream from a two-GOES system in support of the National Weather Service requirements. These satellites send weather data and pictures that cover various sections of the United States.</field><field name=\"source_longName\" type=\"char\" required=\"true\">NEW Geostationary Operational Environmental Satellite 11</field><field name=\"source_orbitPeriod\" type=\"float\" required=\"true\">9999</field><field name=\"source_shortName\" type=\"char\" required=\"true\">NEW GOES-11</field></source>";
//				
//				MetadataManifest mf = new MetadataManifest(xml);
//				mf = inventory.processManifest(mf);
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
//	}
	
	
//	@Test
//	public void testSensorInstance(){
//		Inventory inventory = InventoryFactory.getInstance().createInventory();
//		try {
//		MetadataManifest mf = new MetadataManifest();
//		mf.setObject(Constant.ObjectType.SENSOR);
//		mf.setType(Constant.ActionType.LIST);
//		
//		MetadataField field = new MetadataField();
//		field.setName("object_id");
//		field.setValue("3");
//		mf.getFields().add(field);
//		MetadataManifest m2 = inventory.processManifest(mf);
//		System.out.println(m2.generateXml());
//		}
//		catch(Exception e){
//			e.printStackTrace();
//			System.out.println(e.getMessage());
//			assert false;
//		}
//	}
//
	
	@Test
	public void testUpdateSensorMetadataManifest() {
//		Inventory inventory = null;
//		System.out.println("Updating Sensor");
//		try {
//				 inventory = InventoryFactory.getInstance().createInventory();
//			
//				String xml = "<sensor type=\"update\"><field name=\"sensor_sampleFrequency\" type=\"float\" required=\"false\">5.5</field><field name=\"sensor_sensorId\" type=\"int\" required=\"true\">21</field><field name=\"sensor_swathWidth\" type=\"float\" required=\"true\">111.0</field><field name=\"sensor_longName\" type=\"char\" required=\"true\">UPDATED Advanced Along-Track Scanning Radiometer</field><field name=\"sensor_shortName\" type=\"char\" required=\"true\">UPDATED AATSR</field><field name=\"sensor_description\" type=\"char\" required=\"true\">Advanced Along-Track Scanning Radiometer (AATSR) is one of the Announcement of Opportunity (AO) instruments on board the European Space Agency (ESA) satellite ENVISAT. It is the most recent in a series of instruments designed primarily to measure Sea Surface Temperature (SST), following on from ATSR-1 and ATSR-2 on board ERS-1 and ERS-2.</field></sensor>";
//				
//				MetadataManifest mf = new MetadataManifest(xml);
//				mf = inventory.processManifest(mf);
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
	}
	
//	@Test
//	public void testAddSensorMetadataManifest() {
//		Inventory inventory = null;
//		
//		try {
//				 inventory = InventoryFactory.getInstance().createInventory();
//			
//				String xml = "<sensor type=\"create\"> <field name=\"sensor_swathWidth\" type=\"float\" required=\"true\">111.0</field><field name=\"sensor_longName\" type=\"char\" required=\"true\">NEW Advanced Along-Track Scanning Radiometer</field><field name=\"sensor_shortName\" type=\"char\" required=\"true\">NEW AATSR</field><field name=\"sensor_description\" type=\"char\" required=\"true\">Advanced Along-Track Scanning Radiometer (AATSR) is one of the Announcement of Opportunity (AO) instruments on board the European Space Agency (ESA) satellite ENVISAT. It is the most recent in a series of instruments designed primarily to measure Sea Surface Temperature (SST), following on from ATSR-1 and ATSR-2 on board ERS-1 and ERS-2.</field></sensor>";
//				
//				MetadataManifest mf = new MetadataManifest(xml);
//				mf = inventory.processManifest(mf);
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
//	}
	
	
//	@Test
//	public void testAddManifestToDB(){
//		Query q = QueryFactory.getInstance().createQuery();
//		gov.nasa.podaac.inventory.model.MetadataManifest mf = new gov.nasa.podaac.inventory.model.MetadataManifest();
//
//		mf.setItemId(1);
//		mf.setManifest("MANIFEST");
//		mf.setType("DATASET");
//		mf.setSubmissionDate(new Date().getTime());
//		mf.setUser("dbUser");
//		
//		try {
//			q.saveMetadataManifest(mf);
//		} catch (InventoryException e) {
//
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			assert false;
//		}
//		
//		
//	}
//	
//	@Test
//	public void testdatasetInstance(){
//		Inventory inventory = InventoryFactory.getInstance().createInventory();
//		try {
//		
//		for(int i = 399; i<=399; i++){	
//			MetadataManifest mf = new MetadataManifest();
//			mf.setObject(Constant.ObjectType.DATASET);
//			mf.setType(Constant.ActionType.LIST);
//			
//			MetadataField field = new MetadataField();
//			field.setName("object_id");
//			field.setValue(i);
//			mf.getFields().add(field);
//			MetadataManifest m2 = inventory.processManifest(mf);
//			System.out.println(m2.generateXml());
//			
//			}
//		}
//		catch(Exception e){
//			e.printStackTrace();
//			System.out.println(e.getMessage());
//			assert false;
//		}
//	}
	
	@Test
	public void testCompareDatasetMetadataManifest() {
	
//		System.out.println("ingest.metadata.manifest.xml");
//		String filename = System.getProperty("ingest.metadata.manifest.update.xml");	
//		System.out.println("Use File: "+filename);
//		MetadataManifest mf = null;
//		try {
//		 byte[] buffer = new byte[(int) new File(filename).length()];
//		    BufferedInputStream f = new BufferedInputStream(new FileInputStream(filename));
//		    f.read(buffer);
//		  String xml = new String(buffer);
//		
//		//create manifest
//		 mf = new MetadataManifest(xml);
//		}
//		catch(Exception e){
//			e.printStackTrace();
//		}
//		
//		DatasetMetadataImpl dmi = new DatasetMetadataImpl();
//		Dataset d = new Dataset();
//		try {
//			d = dmi.mapElementsToDateset(mf, d);
//		} catch (InventoryException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		//219
//		QueryFactory.getInstance().createQuery().fetchDatasetByIdFull(219,"citationSet");
//		Dataset original = QueryFactory.getInstance().createQuery().fetchDatasetByIdFull(219,"DatasetIntegerSet", "sourceSet","DatasetDateTimeSet","DatasetCharacterSet","softwareSet","resourceSet","collectionDatasetSet","datasetPolicy","CollectionDataset","locationPolicySet","versionSet","datasetCoverage","contactSet","parameterSet", "citationSet","DatasetSoftware", "DatasetSource", "DatasetProvider","DatasetProject", "projectSet");
//		String diff = dmi.Compare(original, d);
//		
//		System.out.println("Diff: \n\n" + diff + "\n");
		
	}
	
	
	@Test
	public void testAddDatasetMetadataManifest() {
//		Inventory inventory = null;
//		
//		try {
//				 inventory = InventoryFactory.getInstance().createInventory();
//			
//				System.out.println("ingest.metadata.manifest.errors.xml");
//				String filename = System.getProperty("ingest.metadata.manifest.errors.xml");	
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
//				MetadataManifest m2 = inventory.processManifest(mf,"gangl");
//				
//				System.out.println("Inventory Process Manifest took "+(System.currentTimeMillis()-time)/1000.);
//				
//				
//				//get user
//				for(MetadataField mef : m2.getFields()){
//					if(mef.getName().equals("object_id"))
//						objectId = Integer.valueOf(mef.getValue());
//				}
//		
//				System.out.println("ObjectId: " + objectId);
//				
//				//testUpdateDatasetMetadataManifest(objectId);
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
	}
	
	/*
	@Test
	public void testUpdateDatasetMetadataManifest() {
		Inventory inventory = null;
		
		try {
				 inventory = InventoryFactory.getInstance().createInventory();
			
				 //ingest.metadata.manifest.update.xml
				System.out.println("ingest.metadata.manifest.update.xml");
				String filename = System.getProperty("ingest.metadata.manifest.update.xml");	
				System.out.println("Use File: "+filename);
				
				
				
				 byte[] buffer = new byte[(int) new File(filename).length()];
				    BufferedInputStream f = new BufferedInputStream(new FileInputStream(filename));
				    f.read(buffer);
				  String xml = new String(buffer);
				
				//create manifest
				MetadataManifest mf = new MetadataManifest(xml);
				
				System.out.println("Number of XML Elements: "+mf.getFields().size());
				
				//System.out.println("ObjectId: " + objectId);
				
				MetadataField x = null;
				
				if(!mf.hasField("dataset_datasetId")){
					System.out.println("adding datasetId");
					x = new MetadataField();
					x.setName("dataset_datasetId");
					x.setValue(objectId);
					mf.getFields().add(x);
				}
				
				//System.out.println("Starting storeServiceProfile...");
				long time = System.currentTimeMillis();
				
				MetadataManifest m2 = inventory.processManifest(mf);
				System.out.println(m2.generateXml());
				
				System.out.println("Inventory Process Manifest took "+(System.currentTimeMillis()-time)/1000.);
				
		} catch (InventoryException iex) {
			System.out.println(iex.getMessage());
			fail("InventoryException while processManifest!!");
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
			exception.printStackTrace();
			fail("Not passed");
		}
		finally{
			//inventory.closeSession();
		}
	}
	*/
//	public void testUpdateDatasetMetadataManifest(Integer o) {
//		Inventory inventory = null;
//		System.out.println("Updating dataset.");
//		try {
//				 inventory = InventoryFactory.getInstance().createInventory();
//			
//				System.out.println("ingest.metadata.manifest.xml");
//				String filename = System.getProperty("ingest.metadata.manifest.update.xml");	
//				System.out.println("Use File: "+filename);
//				
//				
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
//				System.out.println("ObjectId: " + objectId);
//				
//				MetadataField x = null;
//				
//				if(!mf.hasField("dataset_datasetId")){
//					System.out.println("adding datasetId");
//					x = new MetadataField();
//					x.setName("dataset_datasetId");
//					x.setValue(o);
//					mf.getFields().add(x);
//				}
//				
//				//System.out.println("Starting storeServiceProfile...");
//				long time = System.currentTimeMillis();
//				
//				inventory.processManifest(mf);
//				
//				System.out.println("Inventory Process Manifest took "+(System.currentTimeMillis()-time)/1000.);
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
//			//inventory.closeSession();
//		}
//	}
	
	
	@Test
	public void testCloseInventory() {
//		Inventory session = InventoryFactory.getInstance().createInventory();
//		try {
//			Thread.sleep(100);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		session.closeSession();
	}
}
