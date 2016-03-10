// Copyright 2007, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.nasa.podaac.inventory.api.DataManager;
import gov.nasa.podaac.inventory.api.DataManagerFactory;
import gov.nasa.podaac.inventory.api.Query;
import gov.nasa.podaac.inventory.api.QueryFactory;
import gov.nasa.podaac.inventory.api.Constant.GranuleStatus;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.DatasetElement;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.GranuleArchive;
import gov.nasa.podaac.inventory.model.GranuleCharacter;
import gov.nasa.podaac.inventory.model.GranuleDateTime;
import gov.nasa.podaac.inventory.model.ElementDD;
import gov.nasa.podaac.inventory.model.GranuleInteger;
import gov.nasa.podaac.inventory.model.GranuleMetaHistory;
import gov.nasa.podaac.inventory.model.GranuleReal;
import gov.nasa.podaac.inventory.model.GranuleSIP;
import gov.nasa.podaac.inventory.model.GranuleSpatial;
import gov.nasa.podaac.inventory.model.GranuleMetaHistory.GranuleMetaHistoryPK;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;

/**
 * This class tests interfaces for the tables associated with the Granule
 * portion of the data model.
 * 
 * @author clwong
 * $Id: GranuleTest.java 6682 2011-01-26 19:01:02Z gangl $
 */
public class GranuleIT {

	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private static final String DATASETNAME = "dsShortName";
	private static final String GRANULENAME = "grName";

	/**
	 * This is a unit test to add a minimal granule record into database.
	 */
	@Test
	public void testAddGranule() throws ParseException {
		System.out.println("add granule test");
		Query q = QueryFactory.getInstance().createQuery();
		DataManager manager = DataManagerFactory.getInstance().createDataManager();
		
		Dataset ds = q.findDatasetByShortName(DATASETNAME);
		if(ds == null)
		{
			System.out.println("Dataset is null. This isn't good.");
		}
		Granule g = q.findGranule(GRANULENAME, ds);
		if (g != null){ 
			manager.deleteGranule(g);
			System.out.println("Deleting Granule");
			assertNull(q.findGranule(GRANULENAME, ds));
		}
		
		g = new Granule (GRANULENAME,new Date(), new Date(), new Date(), new Date(), new Date(),
				new Date(), 1, "read", "GZIP", "GZIP", "MD5",GranuleStatus.OFFLINE, new Date(), "/store/temp", "2009/334");
		
		Integer[] elementIds = {2, 7, 14, 22, 24, 27};
		for(Integer id : elementIds){
	
			ElementDD element = q.fetchElementDDById(id, "datasetElementSet");
			Set<DatasetElement> x = element.getDatasetElementSet();
			x.retainAll(ds.getDatasetElementSet());
			System.out.println("Size after 'retain all' operation: " + x.size());
			DatasetElement de = null;
			if(x.iterator().hasNext())
			{
				de = x.iterator().next();
			}
	
			String type = element.getType();
			if(g == null)
				System.out.println("granule is null");
			if(de == null)
				{
					System.out.println("de is null");
					continue;
				}
			
			if (type.equals("character")) 
				g.add(new GranuleCharacter(de, "TEST CHARACTER"));
			else if (type.equals("integer"))
				g.add(new GranuleInteger(de, new Integer(777)));
			else if (type.equals("real"))
				g.add(new GranuleReal(de, Double.valueOf(777)));
			else if (type.equals("date"))
				g.add(new GranuleDateTime(de, new Date()));
			else if (type.equals("time"))
				g.add(new GranuleDateTime(de, new Date()));
			else if (type.equals("spatial"))
			{
				System.out.println("Processing Spatial");
				WKTReader rdr = new WKTReader();
			    Geometry geometry = null;
			    try {
					geometry = rdr.read("POLYGON ((189 98, 83 187, 185 221, 325 168, 189 98))");
					geometry.setSRID(8307);
					GranuleSpatial gs = new GranuleSpatial(de, (Polygon)geometry);
					System.out.println("DE: "+gs.toString());
					g.add(gs);
				} catch (com.vividsolutions.jts.io.ParseException e) {
					e.printStackTrace();
					assertEquals(true,false);
				}
//				if (geometry.getGeometryType().equals("Polygon")) {
//					System.out.println("AddingPolygon");
//					//g.add(new GranuleSpatial(de, (Polygon)geometry));
//				}
				
			}
			
		}		
		
		g.setDataset(ds);
		assertEquals(GRANULENAME, g.getName());
		assertEquals(DATASETNAME, g.getDataset().getShortName());

		manager = DataManagerFactory.getInstance()
				.createDataManager();
		//GranuleSIP gsip = new GranuleSIP();
		GranuleSIP gsip = new GranuleSIP();
		gsip.setGranule(g);
		gsip.setSip("test");
		g.add(gsip);

		GranuleArchive ga = new GranuleArchive();
		ga.setName("grName");
		ga.setStatus("ONLINE");
		ga.setType("DATA");
		ga.setChecksum("2314124");
		ga.setCompressFlag('N');
		ga.setFileSize(0L);
		g.add(ga);
		
		Granule result =  manager.addGranule(g);
		
		// test adding metadata history
		GranuleMetaHistory metaHistory = new GranuleMetaHistory();
		GranuleMetaHistoryPK pk = new GranuleMetaHistoryPK();
		
		
		
		pk.setGranule(result);
		pk.setVersionId(1);
		metaHistory.setMetaHistoryPK(pk);
		metaHistory.setCreationDate(new Date());
		metaHistory.setEchoSubmitDate(new Date());
		metaHistory.setLastRevisionDate(new Date());
		metaHistory.setRevisionHistory("new history revision");
		manager.addGranuleMetaHistory(metaHistory);
		
		
		result.getMetaHistorySet().add(metaHistory);
		result = manager.addGranule(result);
		
		assertEquals(result.getGranuleId(), g.getGranuleId());
		Set<GranuleCharacter> characterSet = result.getGranuleCharacterSet();
		//assert characterSet.size() == 1;
//		for (GranuleCharacter gc : characterSet) {
//			assert gc instanceof GranuleCharacter;
//			assertNotNull(gc.getDatasetElement().getElementDD());
//		}
	}
	
	@Test
	public void testUpdateGranuleStatusByID(){
		Query q = QueryFactory.getInstance().createQuery();
		
		
		//Granule g = q.findGranuleById(1053260);
		Granule g = q.findGranuleByName(GRANULENAME);
		System.out.println("GranuleStatus: " + g.getStatus().toString());
		System.out.println("Setting Status to ONLINE");
		q.updateGranuleStatusByID(g.getGranuleId(),GranuleStatus.ONLINE);
		//g = q.findGranuleById(1053260);
		g = q.findGranuleByName(GRANULENAME);
		System.out.println("New GranuleStatus: " + g.getStatus().toString());
		System.out.println("Setting Status to OFFLINE");
		q.updateGranuleStatusByID(g.getGranuleId(),GranuleStatus.OFFLINE);
		
	}
			
	/**
	 * This is a unit test to list the granules by the test dataset.
	 * Note that this will cause out of memory problem for hugh granule inventory.
	 */
	@Test
	public void testListGranuleByDataset() {
		Query q = QueryFactory.getInstance().createQuery();
		List<Granule> granuleList = q.listGranuleByProperty("dataset", 
									q.findDatasetByShortName(DATASETNAME));
		for (Granule result : granuleList) {
			assert result instanceof Granule;
		}
	}

	/**
	 * This is a unit test to query a granule by a granule name, GRANULENAME.
	 */
	@Test
	public void testFindGranuleByName() {
		System.out.println("Test FindGranuleByName");
		Query q = QueryFactory.getInstance().createQuery();
		System.out.println("Finding gran by name...");
		Granule g = q.findGranuleByName(GRANULENAME);
		
		assert g instanceof Granule;
		assertEquals(g.getName(), GRANULENAME);
		System.out.println("GranuleStatus " + g.getStatus() );
		System.out.println("EnumStatus " + GranuleStatus.OFFLINE );
		
		for(GranuleSpatial gs : g.getGranuleSpatialSet()){
			for(Coordinate c :gs.getValue().getCoordinates()){
				System.out.println("Coordinate: ["+c.x+","+c.y+" ]");
			}
		}
		
		
//		System.out.println("SIP: " + g.getGranuleSIPSet()getSip());
		assertEquals(g.getStatus(), GranuleStatus.OFFLINE);
	}
	
	/**
	 * This is a unit test to query a granule by an area.
	 */
	@Test
	public void testFindGranuleByArea() {
		Query q = QueryFactory.getInstance().createQuery();
		Dataset ds = q.findDatasetByShortName(DATASETNAME);
		List<Granule> glist = q.listGranuleByProperty("dataset", ds);
		Granule selected = glist.get(0);
		Granule g = q.fetchGranuleById(selected.getGranuleId(), "granuleSpatialSet");
		assert g instanceof Granule;
		assertEquals(g.getName(), selected.getName());
		assertEquals(g.getStatus(), selected.getStatus());
		
		String wktFilter = "POLYGON ((0 0, 360 0, 360 360, 0 360, 0 0))";
        WKTReader fromText = new WKTReader();
        Geometry geometry = null;
        try{
                geometry = fromText.read(wktFilter);
                geometry.setSRID(8307);
        } catch (com.vividsolutions.jts.io.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Set<GranuleSpatial> spatialSet = g.getGranuleSpatialSet();
		if (spatialSet != null)
			for (GranuleSpatial gr : spatialSet) {
				assert gr instanceof GranuleSpatial;
				System.out.println("testFindGranuleByArea: "+g.getGranuleId()+":"+gr.getValue());
				Polygon polygon = gr.getValue();
				if (polygon.contains(geometry)) System.out.println("Contains:"+geometry);
				if (polygon.within(geometry)) System.out.println("Within:"+geometry);
			}
	}

	/**
	 * This is a unit test to query a granule by a granule sequence Id.
	 * In this case, it uses the sequence Id of the first granule returned
	 * from the list query of all granules.
	 */
	@Test
	public void testFindGranuleById() {
		Query q = QueryFactory.getInstance().createQuery();
		Dataset ds = q.findDatasetByShortName(DATASETNAME);
		List<Granule> glist = q.listGranuleByProperty("dataset", ds);
		Granule selected = glist.get(0);
		Granule g = q.findGranuleById(selected.getGranuleId());
		assert g instanceof Granule;
		//System.out.println("GranuleStatus: " + g.getStatus().toString());
		assertEquals(g.getName(), selected.getName());
		assertEquals(g.getStatus(), selected.getStatus());
	}

	/**
	 * This is a unit test to query a granule by a granule sequence Id.
	 * This returned granule object has other associations attached to it.
	 */
	@Test
	public void testFetchGranuleById() {
		System.out.println("fetch granuleByID test");
		
		Query q = QueryFactory.getInstance().createQuery();
		Dataset ds = q.findDatasetByShortName(DATASETNAME);
		List<Granule> glist = q.listGranuleByProperty("dataset", ds);
		Granule selected = glist.get(0);
		Granule g = q.fetchGranuleById(selected.getGranuleId(), 
				"granuleCharacterSet", "granuleArchiveSet", "granuleSpatialSet", "metaHistorySet");
		assert g instanceof Granule;
		assertEquals(g.getName(), selected.getName());
		assertEquals(g.getStatus(), selected.getStatus());
		Set<GranuleCharacter> characterSet = g.getGranuleCharacterSet();
		for (GranuleCharacter gc : characterSet)
			assert gc instanceof GranuleCharacter;
		Set<GranuleArchive> archiveSet = g.getGranuleArchiveSet();
		if (archiveSet != null)
			for (GranuleArchive gr : archiveSet)
				assert gr instanceof GranuleArchive;
		Set<GranuleSpatial> spatialSet = g.getGranuleSpatialSet();
		if (spatialSet != null)
			for (GranuleSpatial gr : spatialSet) {
				assert gr instanceof GranuleSpatial;
				System.out.println("testFetchGranuleById: "+g.getGranuleId()+":"+gr.getValue());
			}
		assert g.getMetaHistorySet().iterator().next() instanceof GranuleMetaHistory;
		assertTrue(g.getMetaHistorySet().size()>0);
		System.out.println("id: " + g.getGranuleId());
		
		assertTrue(g.getMetaHistorySet().iterator().next().getRevisionHistory().contains("history"));
	}

	/**
	 * This is a unit test to replace the metadata for a granule.
	 */
//	@Test
//	public void testReplaceGranule() {
//		Query q = QueryFactory.getInstance().createQuery();
//		DataManager manager = DataManagerFactory.getInstance()
//										.createDataManager();
//		
//		Granule g = q.findGranuleByName(GRANULENAME);
//		Integer id = g.getGranuleId();
//		Integer version = g.getVersion()+1;
//		g = new Granule (GRANULENAME,new Date(), new Date(), new Date(), new Date(), new Date(),
//				new Date(), version, "read", "GZIP", "GZIP", "MD5",GranuleStatus.OFFLINE, new Date(), "/store/temp", "2009/334");
//		
//		ElementDD element = q.findElementDDByShortName("comment");
//		GranuleCharacter gchar = new GranuleCharacter(element, "grComment");
//		g.add(gchar);
//		element = q.findElementDDByShortName("versionDate");
//		g.add(new GranuleDateTime(element, new Date()));
//
//	    WKTReader rdr = new WKTReader();
//	    Geometry geometry = null;
//	    try {
//			geometry = rdr.read("POLYGON ((88 98, 83 187, 185 221, 325 168, 88 98))");
//			geometry.setSRID(8307);
//		} catch (com.vividsolutions.jts.io.ParseException e) {
//		}
//		if (geometry.getGeometryType().equals("Polygon")) {
//			element = q.findElementDDByShortName("spatialGeometry");
//			g.add(new GranuleSpatial(element, (Polygon)geometry));
//		}
//		g.setGranuleId(id);
//		g.setDataset(q.findDatasetByShortName(DATASETNAME));
//		GranuleSIP gsip = new GranuleSIP();
//		gsip.setId(id);
//		gsip.setGranule(g);
//		gsip.setSip("test");
//		g.setGranuleSIP(gsip);
//		
//		manager.addGranule(g);
//		
//		// test adding metadata history
//		GranuleMetaHistory metaHistory = new GranuleMetaHistory();
//		GranuleMetaHistoryPK pk = new GranuleMetaHistoryPK();
//		pk.setGranule(g);
//		pk.setVersionId(version);
//		metaHistory.setMetaHistoryPK(pk);
//		metaHistory.setCreationDate(new Date());
//		metaHistory.setEchoSubmitDate(new Date());
//		metaHistory.setLastRevisionDate(new Date());
//		metaHistory.setRevisionHistory("history "+version);		
//		manager.addGranuleMetaHistory(metaHistory);
//		
//	}
	
	@Test
	public void testSetGranuleArchiveTime(){
		System.out.println("Running setGranuleArchiveTime");
		Query q = QueryFactory.getInstance().createQuery();

		Granule g = q.findGranuleByName(GRANULENAME);
		g.setArchiveTime(new Date());
		
		DataManager manager = DataManagerFactory.getInstance().createDataManager();
		
		//g.setGranuleSIP("sip");
		manager.updateGranule(g);
		//g = q.findGranuleById(1053260);
		//g = q.findGranuleByName(GRANULENAME);
		//System.out.println("New GranuleStatus: " + g.getStatus().toString());
		//System.out.println("Setting Status to OFFLINE");
		//q.updateGranuleStatusByID(g.getGranuleId(),GranuleStatus.ONLINE);
		System.out.println("Finished setting granule archive time.");
	}

}
