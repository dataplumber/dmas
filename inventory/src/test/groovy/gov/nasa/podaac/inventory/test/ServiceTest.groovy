package gov.nasa.podaac.inventory.test

import gov.nasa.podaac.common.api.serviceprofile.ServiceProfile;
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfileFactory;

import gov.nasa.podaac.inventory.core.ServiceImpl;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.Collection;
import gov.nasa.podaac.inventory.model.Contact;
import gov.nasa.podaac.inventory.model.ElementDD;
import gov.nasa.podaac.inventory.model.Provider;
import gov.nasa.podaac.inventory.model.GranuleReference;

public class ServiceTest extends GroovyTestCase{

	def HOST = "https://localhost";
	def PORT = 9192
	def DATASET = "219"
	def GRANULE = 4372024
	
	
	public void testGetGranuleIdsTest(){
		
		ServiceImpl service = new ServiceImpl(HOST, 2181);
//		service.setAuthInfo("gangl","1234\$");
//		
//			Object o = new Object();
//			service.ingestSip(o );
			
//		List<Integer> ids = service.getGranuleIdListByDateRange(1325613290790L,1325613590795L, 'ARCHIVE');
//		ids.each {
//			println "returned id: $it"
//			 }
//		
//		service.getGranuleIdListByDateRange(1325613290790L,1325613590795L, 'ARCHIVE',656);
//		ids.each {
//			println "returned id: $it"
//			 }
	}
//	
//	public void testGetContact(){
//		ServiceImpl service = new ServiceImpl(HOST);
//		Contact c = service.getContactByID(13)
//		println "${c.getFirstName()} ${c.getLastName()} - ${c.getProvider().getLongName()}" 	
//	}
//	
//	public void testGetElementId(){
//		ServiceImpl service = new ServiceImpl(HOST);
//		ElementDD d = service.getElementDDById(13)
//		println "${d.getShortName()} ${d.getElementId()} - ${d.getDescription()}"
//		println "Number of DEIDS: " + d.getDatasetElementSet().size()
//	}
//	
//	public void testGetElementName(){
//		ServiceImpl service = new ServiceImpl(HOST);
//		ElementDD d = service.getElementDDByShortName("minAltitude")
//		println "${d.getShortName()} ${d.getElementId()} - ${d.getDescription()}"
//		println "Number of DEIDS: " + d.getDatasetElementSet().size()
//	}
//	
//	public void testGetCollection(){
//		ServiceImpl service = new ServiceImpl(HOST);
//		Collection c = service.getCollectionById(3);
//		println "Collection info"
//		println c.getCollectionId()
//		println c.getShortName()
//		println c.getLongName()
//		
//		c.getCollectionLegacyProductSet().each { 
//			println "LegacyId: " + it.getLegacyProductId()
//		}
//		
//		c.getCollectionDatasetSet().each {
//			println "granFlag: " + it.getGranuleFlag()
//			println "granRange: " + it.getGranuleRange360()
//		}
//		
//		println "Echo time " + c.getCollectionProduct().getEchoSubmitDateLong()
//		println "visibleFlag " + c.getCollectionProduct().getVisibleFlag()
//		println "productId " + c.getCollectionProduct().getProductId()
//		
//		c.getCollectionContactSet().each{
//			println "Contact first: " + it.getFirstName()
//			println "Contact Prvdr: " + it.getProvider().getLongName()
//		}
//		
//	}
//	
//	public void testCollectionList(){
//		ServiceImpl service = new ServiceImpl(HOST);
//		def cList = service.listCollections()
//		cList.each { 
//			println "${it.getCollectionId()} - ${it.getShortName()}"
//			 }
//	}
	
	
	
//	public void testToStringTest(){
//		println "Skipping service tests..."
//	}
//	
//	public void testGetGranuleIdsTest(){
//		ServiceImpl service = new ServiceImpl(HOST);
//		List<Integer> ids = service.findGranuleList([3734433,3734432,3734431,3734038]);
//		ids.each { 
//			println "returned id: $it"
//			 }
//	}
//	
//	public void testUpdateArchiveCheck(){
//		ServiceImpl service = new ServiceImpl(HOST);
//		service.setAuthInfo("thuang", "password");
//		Integer i = new Integer(3716822)
//		service.updateGranuleArchiveChecksum(i,"ascat_20110714_215103_metopa_24567_eps_o_250_1019_ovw.l2.nc.gz","aidjalskd")
//
//	}
//	
//	public void testUpdateArchiveSize(){
//		ServiceImpl service = new ServiceImpl(HOST);
//		service.setAuthInfo("thuang", "password");
//		Integer i = new Integer(3716822)
//
//		service.updateGranuleArchiveSize(i,"ascat_20110714_215103_metopa_24567_eps_o_250_1019_ovw.l2.nc.gz",11111111L)
//
//	}
	
//	public void testUpdateArchiveStatus(){
//		ServiceImpl service = new ServiceImpl(HOST);
//		service.setAuthInfo("thuang", "password");
//		Integer i = new Integer(3716822)
//
//		service.updateGranuleArchiveStatus(i,"OFFLINE")
//	}
	
//	public void testUpdateGranAndVerify(){
//		ServiceImpl service = new ServiceImpl(HOST);
//		service.setAuthInfo("thuang", "password");
//		Integer i = new Integer(3716822)
//
//		service.updateStatusAndVerify(i,"OFFLINE")
//	}
	
//	public void testUpdateGranuleRootPathTest(){
//		ServiceImpl service = new ServiceImpl(HOST);
//		service.setAuthInfo("thuang", "password");
//		Integer i = new Integer(3716822)
//		//file:///store/ascat/preview/L2/25km
//		service.updateGranuleRootPath(i,"file:///store/ascat/preview/L2/25km/NEWROOT")
//	}
	
//	public void testGranuleDeleteLocalReferenceTest(){
//		ServiceImpl service = new ServiceImpl(HOST);
//		service.setAuthInfo("thuang", "password");
//		Integer i = new Integer(3716822)
//
//		service.deleteGranuleLocalReference(i,"LOCAL");
//	}
	
	
//	public void testGranuleAddReferenceTest(){
//		ServiceImpl service = new ServiceImpl(HOST);
//		service.setAuthInfo("thuang", "password");
//		Integer i = new Integer(3716822)
//		service.addGranuleReference(i,"ftp://podaac-ftp/file/goes/here.data","ONLINE","LOCAL-FTP","Newly added reference");
//	}
//	
//	public void testGranuleUpdateReferenceStatusTest(){
//		ServiceImpl service = new ServiceImpl(HOST);
//		service.setAuthInfo("thuang", "password");
//		Integer i = new Integer(3716822)
//		service.updateGranuleReferenceStatus(i,"ftp://podaac-ftp/file/goes/here.data","OFFLINE");
//	}
	
	//
//	public void testGranuleUpdateReferencePathTest(){
//		ServiceImpl service = new ServiceImpl(HOST);
//		service.setAuthInfo("thuang", "password");
//		Integer i = new Integer(3716822)
//		service.updateGranuleReferencePath(i,"ftp://podaac-ftp/file/goes/here.data","ftp://podaac-ftp/file/goes/update/here.md5");
//	}
	
//	public void testGranuleUpdateReassociateTest(){
//		ServiceImpl service = new ServiceImpl(HOST);
//		service.setAuthInfo("thuang", "password");
//		Integer i = new Integer(3716822)
//		service.updateGranuleReassociate(i,"file://this/is/a/new/root/path",218);
//	}
	
//	public void testGranuleUpdateReassociateElementTest(){
//		ServiceImpl service = new ServiceImpl(HOST);
//		service.setAuthInfo("thuang", "password");
//		Integer i = new Integer(3729603)
//		service.updateGranuleReassociateElement(i,219,218);
//	}
	
//	public void testGetGranulesTestAll(){
//		ServiceImpl service = new ServiceImpl(HOST);
//		List<Integer> grList = service.getGranuleIdListAll(219,null,null);
//		println "testGranRefsTest";
//		println "return size: " + grList.size
//	}
	
//	public void testGetRefsTest(){
//		ServiceImpl service = new ServiceImpl(HOST);
//		def mp = service.getGranuleReferences(219,1);
//		
//		println "page:" + mp.getAt("page");
//		println "numpage: " + mp.getAt("numPages");
//		println "return size: " + mp.getAt("refs").size();
//	}
	
//	public void testGetRefsAllTest(){
//		ServiceImpl service = new ServiceImpl(HOST);
//		def ref = service.getGranuleAllReferences(219);
//		
//		println ref.size();
//		
//	}
	
//	public void testDeleteGranuleTest(){
//		
//		ServiceImpl service = new ServiceImpl(HOST);
//		service.setAuthInfo("axt", "axt388");
//		Granule g = new Granule();
//		g.setGranuleId(3732436);
//		if(service.deleteGranule(g, false))
//			println("Success!");
//		else
//			println("fail!");
//		
//		if(service.deleteGranule(g, true))
//			println("Success!");
//		else
//			println("fail!");
//		
//	}
//	
//	public void testGetGranuleIdsByDatasetTest(){
//		ServiceImpl service = new ServiceImpl(HOST);
//		Calendar start = Calendar.getInstance();
//		Calendar stop = Calendar.getInstance();
//		start.setTimeInMillis(1311792984626)
//		stop.setTimeInMillis(1311793028628)
//		List<Integer> ids = service.getGranuleIdList(218,start,stop);
//		println "Size of returned id list: "+ids.size()
//		println "test with no vars"
//		ids = service.getGranuleIdList(218,null,null);
//		println "Size of returned id list: "+ids.size()
//		
//	}
	
//		public void testUpdateAIPGranuleTest(){
//	
//			ServiceImpl service = new ServiceImpl(HOST);
//			service.setAuthInfo("thuang", "password");
//			Granule g = new Granule();
//			g.setGranuleId(3732436);
//			
//			service.updateGranuleAIPArchive(3732436,"TYPE",
//			"destination", "name", "status");
//			
//			service.updateGranuleAIPReference(3732436,"TYPE",
//			"destination", "name", "status");
//			
//		}
	
	
//	public void testGetProviderTest(){
//		println "Testing get Provider"
//		ServiceImpl service = new ServiceImpl(HOST);
//		try{
//			Provider p = service.getProvider("asdasda");
//			println "Provider Shortname: " + p.getShortName();
//			println "Provider Longname: " + p.getLongName();
//			println "Provider ID: " + p.getProviderId();
//			
//			p.getProviderResourceSet().each {
//				println "ResourcePath " + it.getResourcePath()
//			}
//		}catch(InventoryException e){
//			println "Exception: " + e.getMessage();
//		}
//	}
//	
//	public void testGetDatasetTest(){
//		println "Testing get Dataset"
//		ServiceImpl service = new ServiceImpl(HOST);
//		try{
//			Dataset d = service.getDataset(DATASET)
//			if(d==null)
//			{
//				println "Dataset is null"
//				return
//			}
//			
//			println "Datset shortname, id: " + d.getShortName() + ", " + d.getDatasetId()
//			println "DLPs ["+d.getLocationPolicySet().size()+"]"
//			d.getLocationPolicySet().each { 
//			println "\tBasepath: " + it.getBasePath()	
//		 }
//		}catch(InventoryException e){
//			println "Exception: " + e.getMessage();
//		}
//	}
//	
//	public void testGetDatasetCoverageTest(){
//		println "Testing get DatasetCoverage"
//		ServiceImpl service = new ServiceImpl(HOST);
//		try{
//			DatasetCoverage dc = service.getDatasetCoverage(DATASET);
//			if(dc==null)
//			{
//				println "Dataset is null"
//				return
//			}
//			println "Start/stop times: ["+dc.getStartTimeLong()+", "+dc.getStopTimeLong() +"]"
//			println "BB: ${dc.getNorthLat()},${dc.getSouthLat()},${dc.getEastLon()},${dc.getWestLon()}"
//		}catch(InventoryException e){
//			println "Exception: " + e.getMessage();
//		}
//	}
//	
//	public void testGetDatasetPolicyTest(){
//		println "Testing get DatasetPolicy"
//		ServiceImpl service = new ServiceImpl(HOST);
//		try{
//			DatasetPolicy dp = service.getDatasetPolicy(DATASET);
//			if(dp==null)
//			{
//				println "Dataset is null"
//				return
//			}
//			println "Class/Format times: [${dp.getDataClass()},${dp.getDataFormat()}]"
//			//println "versioned/versionPolicy times: [${dp.getVersioned()},${dp.getVersionPolicy()}]"
//		}catch(InventoryException e){
//			println "Exception: " + e.getMessage();
//		}
//	}
//
//	
	public void testGetGranuleTest(){
//		println "Testing get granule"
//		ServiceImpl service = new ServiceImpl(HOST);
//		try{
//			Granule g = service.getGranuleById(GRANULE)
//			
//			g.getGranuleSIPSet().each {  
//				println "Sip: ${it.getSip()}"	
//			}
//			
//			g.getGranuleArchiveSet().each { 
//				println "name/size: ${it.getName()}:${it.getFileSize()}"
//			}
//			
//			g.getGranuleReferenceSet().each {
//				println "path/status: ${it.getPath()}:${it.getStatus()}"
//			}
//			
//			g.getMetaHistorySet().each{
//				println "GMH LAST REV TIME: ${it.getLastRevisionDateLong()}"
//			}
//			
//			g.getGranuleSpatialSet().each { 
//				println "SPATIAL: " + it.getValue();
//				 }
//			
//			
//		}catch(Exception e){
//			println "Exception: " + e.getMessage();
//		}
	}
//	
	public void testSipIngestTest(){
//		println "Testing SIP processing"
//		ServiceImpl service = new ServiceImpl(HOST, PORT);
//		System.out.println("test-sip.xml");
//		String filename = System.getProperty("test-sip.xml");
//		System.out.println("Use File: "+filename);
//
//		 byte[] buffer = new byte[(int) new File(filename).length()];
//			BufferedInputStream f = new BufferedInputStream(new FileInputStream(filename));
//			f.read(buffer);
//		  String xml = new String(buffer);
//		  
//		  ServiceProfile profile = ServiceProfileFactory.getInstance().
//			createServiceProfileFromMessage(xml);
//			
//			service.setAuthInfo("gangl", "1234$test");
//			ServiceProfile p2 = service.ingestSip(profile);
//			if(p2 == null)
//			{
//				println "ServiceProfile is null"
//				return	
//			}
//			else
//				"ServiceProfile is not null."
//			println ServiceProfile.toString();
//		  
	}
//	
//	public void testManifestTest(){
//		println "Testing manifest processing"
//		ServiceImpl service = new ServiceImpl(HOST);
//		
//		System.out.println("ingest.metadata.manifest.xml");
//		String filename = System.getProperty("ingest.metadata.manifest.update.xml");
//		System.out.println("Use File: "+filename);
//
//		 byte[] buffer = new byte[(int) new File(filename).length()];
//		    BufferedInputStream f = new BufferedInputStream(new FileInputStream(filename));
//		    f.read(buffer);
//		  String xml = new String(buffer);
//
//		  
//		  
//		//create manifest
//		MetadataManifest mf = new MetadataManifest(xml);
//
//		try{
//			MetadataManifest ret = service.processManifest(mf, "axt", "axt388")
//			println "Returned Action Type: " + ret.getActionType()
//		}catch(InventoryException e){
//			println "Exception: " + e.getMessage();
//		}
//	}
//	
//	public void testGetGranuleByDatasetTest(){
//		println "Testing get granule"
//		ServiceImpl service = new ServiceImpl(HOST);
//		try{
//			println "LatestGranule: " + service.getLatestGranuleIdByDataset(DATASET);
//		}catch(InventoryException e){
//			println "Exception: " + e.getMessage();
//		}	
//	}
//	
//	public void testGetGranuleByNameAndDatasetTest(){
//		println "getting granule by name/dataset"
//		ServiceImpl service = new ServiceImpl(HOST);
//		try{
//				Granule g = service.getGranuleByNameAndDataset("A1_PLTM_P_1280_2009_01_02_23_53_56", 592)
//			if(g == null)
//				println "Granule is null"
//			else
//				println "GranuleId: "+g.getGranuleId()
//		}catch(InventoryException e){
//			println "Exception: " + e.getMessage();
//		}
//	}
//	
//	public void testGetGranArchPathTest(){
//		println "Testing get granule"
//		ServiceImpl service = new ServiceImpl(HOST);
//		try{
//			println "ArchivePath: " + service.getGranuleArchivePath(GRANULE);
//		}catch(InventoryException e){
//			println "Exception: " + e.getMessage();
//		}
//			
//	}
//	public void testisOnlineTest(){
//		println "Testing get inventoryOnline"
//		ServiceImpl service = new ServiceImpl(HOST);
//		if(service.isOnline())
//			println "Inventory Service is online. True returned."
//		else
//			println "Service is offline. False returned."
//	}
}
