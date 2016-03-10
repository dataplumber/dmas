package gov.nasa.podaac.archive;

import gov.nasa.podaac.archive.core.AIP;
import gov.nasa.podaac.archive.core.ArchiveProperty;
import gov.nasa.podaac.archive.external.InventoryAccess;
import gov.nasa.podaac.archive.external.InventoryFactory;
import gov.nasa.podaac.archive.external.wsm.Access;
import gov.nasa.podaac.archive.external.wsm.Access.Subpath;
import gov.nasa.podaac.archive.external.InventoryQuery;
//import gov.nasa.podaac.archive.external.direct.Query;








import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;




import java.util.Set;

import gov.nasa.podaac.inventory.api.Constant;
import gov.nasa.podaac.inventory.api.Constant.GranuleStatus;
import gov.nasa.podaac.inventory.api.Service;
import gov.nasa.podaac.inventory.model.*;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class InventoryAccessTest extends TestCase {

	@Before
    public void setUp() {
        ArchiveProperty.getInstance();
//		Properties p = new Properties();
//		try {
//			p.load(new FileInputStream("test.properties" ));
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Enumeration propNames = p.propertyNames();
//		while (propNames.hasMoreElements()) {
//			String name = propNames.nextElement().toString();
//			System.setProperty(name, p.getProperty(name));
//		}
    }
	
	
	public void testSubpath() throws gov.nasa.podaac.inventory.exceptions.InventoryException{
		System.out.println("Testing subpath from file locations (--location-policy)");
		
		
		//set up the mock..
		Service mockService = mock(Service.class);
		when(mockService.addGranuleReference(anyInt(), anyString(), anyString(), anyString(), anyString())).thenReturn(true);
		Access a = new Access(mockService);
		
		
		//test subpath functions
		Subpath sp = a.getSubpathFromLocation("file:///base/location/of/filename1", "file:///base/location");
		assertEquals(sp.OutsideBP, new Boolean(false));
		assertEquals(sp.path, "of/filename1");
		
		sp = a.getSubpathFromLocation("file:///store/base2/location/of/filename1", "file:///store/base1/location");
		assertEquals(sp.OutsideBP, new Boolean(true));
		assertEquals(sp.path, "base2/location/of/filename1");
	}
	
	public void testIWSRemotReferences() throws gov.nasa.podaac.inventory.exceptions.InventoryException{
		System.out.println("Testing IWS Remote Reference Updates (--location-policy)");
		
		
		//set up the mock..
		Service mockService = mock(Service.class);
		when(mockService.addGranuleReference(anyInt(), anyString(), anyString(), anyString(), anyString())).thenReturn(true);
		Access a = new Access(mockService);
		//test mock service
		assertTrue(mockService.addGranuleReference(555, "", "", "", ""));
		
		
		//setup objects
		//Granule granule, Map<String, String> baseLocationPolicy, String subpath, boolean outsideBP
		Map<String,String> blps = new HashMap<String,String>();
		
		blps.put("REMOTE-FTP", "ftp://datastore.ftp.remote.org/allData/dataset1");
		blps.put("REMOTE-OPENDAP", "http://datastore.opednap.remote.org/allData/dataset1");
		blps.put("LOCAL-FTP", "ftp://datastore.ftp.local.org/allData/dataset1");
		blps.put("LOCAL-OPENDAP", "http://datastore.opendap.local.org/allData/dataset1");
		
		Granule g = new Granule();
		g.setName("DATAFILE1");
		g.setRelPath("2014/001");
		g.setGranuleId(8675309);
		g.setStatus(GranuleStatus.ONLINE);
		
		
		//first run, no references
		Set<GranuleReference> grSet = a.setGranuleReference(g, blps, "2014/001/DATAFILE1", false);
		
		assertTrue(!grSet.isEmpty());
		assertEquals(grSet.size(),4);
		
		
		
		for(GranuleReference grCan : grSet){
			if(grCan.getType().equals("LOCAL-FTP")){
				assertTrue(grCan.getPath().equals("ftp://datastore.ftp.local.org/allData/dataset1/2014/001/DATAFILE1"));
			}
			else if(grCan.getType().equals("LOCAL-OPENDAP")){
				assertTrue(grCan.getPath().equals("http://datastore.opendap.local.org/allData/dataset1/2014/001/DATAFILE1.html"));
			}
		}

		System.out.println("Test 2...");
		GranuleReference gr = new GranuleReference();
		gr.setGranuleId(8675309);
		gr.setDescription("ORIGINAL DESC 123");
		gr.setType("REMOTE-FTP");
		gr.setPath("ftp://datastore.ftp.remote.org/allData/dataset1/2014/001/DATAFILE1");
		
		g.getGranuleReferenceSet().add(gr);
		grSet = a.setGranuleReference(g, blps, "2014/001/DATAFILE1", false);
		
		assertTrue(!grSet.isEmpty());
		assertEquals(grSet.size(),4);
		
		for(GranuleReference grCan: grSet){
			//ORIGINAL DESC 123
			if(grCan.getType().equals("REMOTE-FTP")){
				assertTrue(grCan.getDescription().equals("ORIGINAL DESC 123"));
			}
			else{
				assertTrue(grCan.getDescription().equals("Verifiy created description"));
			}
		}

		
		System.out.println("TEST 3");
		//OUT OF BASEPATH
		grSet = a.setGranuleReference(g, blps, "dataset2/2014/001/DATAFILE1", true);
		assertTrue(!grSet.isEmpty());
		assertEquals(grSet.size(),4);
				
		for(GranuleReference grCan : grSet){
			if(grCan.getType().equals("LOCAL-FTP")){
				assertTrue(grCan.getPath().equals("ftp://datastore.ftp.local.org/allData/dataset2/2014/001/DATAFILE1"));
			}
			else if(grCan.getType().equals("LOCAL-OPENDAP")){
				assertTrue(grCan.getPath().equals("http://datastore.opendap.local.org/allData/dataset2/2014/001/DATAFILE1.html"));
			}
		}

		
		when(mockService.addGranuleReference(anyInt(), anyString(), anyString(), anyString(), anyString())).thenThrow(new gov.nasa.podaac.inventory.exceptions.InventoryException("MockedError"));
		a = new Access(mockService);
		g.getGranuleReferenceSet().clear();
		grSet = a.setGranuleReference(g, blps, "2014/001/DATAFILE1", false);
		
		assertTrue(grSet.isEmpty());
		
		
	}
	
//	/**
//	 * This is a unit test to setting status in GranuleArchive.
//	 */
//	@Test
//	public void testUpdateArchiveStatus() {
//		List<Integer> iList = new ArrayList();
//		iList.add(5);
//		List<AIP>archiveList =  InventoryFactory.getInstance().getQuery().getArchiveAIPByGranule(iList);
//		for (AIP archive : archiveList) archive.setStatus("ONLINE");
//		InventoryFactory.getInstance().getAccess().updateAIPArchiveStatus(archiveList);
//	}
//	
//	/**
//	 * This is a unit test to setting status in Granule.
//	 */
//	@SuppressWarnings("static-access")
//	@Test
//	public void testUpdateGranuleStatus() {
//		List<Integer>idList = new ArrayList<Integer>();
//		for (int i=1; i<=1; i++) idList.add(i);
//		InventoryFactory.getInstance().getAccess().updateVerifyGranuleStatus(idList, GranuleStatus.ONLINE.toString() );
//	}

}
