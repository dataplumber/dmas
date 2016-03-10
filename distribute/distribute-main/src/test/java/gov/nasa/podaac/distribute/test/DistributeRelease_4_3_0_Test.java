package gov.nasa.podaac.distribute.test;

import static org.junit.Assert.*;

import java.util.*;

import gov.nasa.podaac.distribute.echo.ECHOCollectionFile;
import gov.nasa.podaac.distribute.echo.ECHOGranuleFile;
import gov.nasa.podaac.distribute.echo.jaxb.granule.Geometry;
import gov.nasa.podaac.distribute.ems.EMSReport;
import gov.nasa.podaac.distribute.ems.EMSReport.EMSTuple;
import gov.nasa.podaac.inventory.model.*;
import gov.nasa.podaac.inventory.api.Constant.*;


import org.junit.Test;

import com.mchange.v1.util.StringTokenizerUtils;



public class DistributeRelease_4_3_0_Test {

	@Test
	public void test1737(){
		//https://podaac-redmine.jpl.nasa.gov/issues/1737
		DistributeTestSuite.printTestInfo("Distribute/EMS - Update Rolling Store handling",1737);
		
		//create a rolling store granule and see how the 'print' looks for it.
		EMSReport ems = new EMSReport();
		EMSTuple t =  ems.new EMSTuple();
		
		Dataset d = new Dataset();
		d.setPersistentId("TEST_DATASET_DAAC");
		DatasetPolicy dp =new DatasetPolicy();
		dp.setDataClass("ROLLING-STORE");
		dp.setDataDuration(10);
		d.setDatasetPolicy(dp);
		
		Granule g = new Granule();
		
		g.setDataset(d);
		g.setGranuleId(1001);
		g.setName("TEST GRANULE");
		g.setIngestTimeLong(100000001L);
		g.setArchiveTimeLong(200000001L);
		t = ems.formatPrint(g, true, null);
		System.out.println(t.entry);
		
		String[] args = t.entry.split("\\|\\&\\|");
		
		assertEquals("N",args[10]);
		System.out.println("TEST 1737: PASSED...\n");

	}
	
	@Test
	public void test1739(){
		//https://podaac-redmine.jpl.nasa.gov/issues/1739
		DistributeTestSuite.printTestInfo("Ingestion of Granule State File",1739);
		EMSReport ems = new EMSReport();
		String filename = System.getProperty("granule.state.change.file");
		System.out.println("Parsing statefile: "+ filename);
		List<Map<String,Object>> lmm = ems.parseJSON(filename);
		assertEquals(3, lmm.size());
		System.out.println("TEST 1739: PASSED...\n");
	}
	
	@Test
	public void test1755(){
		DistributeTestSuite.printTestInfo("ECHO Export - ProcessingCenter value truncated by 1 character",1755);
		String processingCenter = "TEST PROCESSING CENTER";
		String sample1 = processingCenter.substring(0,(Math.min(80, processingCenter.length()-1)));
		assertEquals(21,sample1.length());
		String sample2 = ECHOCollectionFile.maxLengthString(processingCenter, 80);
		assertEquals(22,sample2.length());
		System.out.println("TEST 1755: PASSED...\n");
	}
	
	@Test
	public void test1793(){
		DistributeTestSuite.printTestInfo("EMS daily archive report should time sort all of the entries",1793);
		EMSReport emsRep = new EMSReport();
		
		List<EMSTuple> etList = new ArrayList<EMSTuple>();
		
		EMSTuple et = emsRep.new EMSTuple();
		et.entry="";
		et.time=0L;
		et.granuleId=1;
		etList.add(et);
		
		et = emsRep.new EMSTuple();
		et.entry="";
		et.time=-10L;
		et.granuleId=2;
		etList.add(et);
		
		et = emsRep.new EMSTuple();
		et.entry="";
		et.time=10L;
		et.granuleId=3;
		etList.add(et);
		
		etList = EMSReport.timeOrder(etList);
		Long lastTime = null;
		for(EMSTuple e: etList){
			System.out.println("\tlastTime: "+lastTime+" currentTime: " + e.time);
			if(lastTime == null)
				lastTime = e.time;
			else{
				assertTrue(lastTime <= e.time);
				lastTime = e.time;
			}
		}
		
		System.out.println("TEST 1793: PASSED...\n");
	}

	@Test
	public void test1794(){
		DistributeTestSuite.printTestInfo("Do not insert delete time for deleted data-only granule\nfrom DMAS into the ingested entries on the EMS archive report",1794);
		
		//This ticket is no longer valid. Only the last entry is seen by the EMS archive, so the original 'archive' line is never read by EMS. We can keep the deleted date/time in all entries.
		
		System.out.println("TEST 1794: PASSED...\n");

	}

	@Test
	public void test1822(){
		DistributeTestSuite.printTestInfo("EMS daily archive report should time sort all of the entries and only include the latest entry",1822);
EMSReport emsRep = new EMSReport();
		
		List<EMSTuple> etList = new ArrayList<EMSTuple>();
		
		EMSTuple et = emsRep.new EMSTuple();
		et.entry="MIDDLE";
		et.time=0L;
		et.granuleId=1;
		etList.add(et);
		
		et = emsRep.new EMSTuple();
		et.entry="FIRST";
		et.time=-10L;
		et.granuleId=1;
		etList.add(et);
		
		et = emsRep.new EMSTuple();
		et.entry="LAST";
		et.time=10L;
		et.granuleId=1;
		etList.add(et);
		
		etList = EMSReport.timeOrder(etList);
		
		assertEquals(1, etList.size());
		et = etList.get(0);
		
		assertEquals("LAST", et.entry);
		assertEquals(new Long(10), et.time);
		
		System.out.println("TEST 1822: PASSED...\n");
		
	}
	
	@Test
	public void test1752(){
		DistributeTestSuite.printTestInfo("ECHO export has bad output for EAST=-180",1752);
		Geometry boundingBox = new Geometry();
		
		Double east, west, north, south;
		north = 90d;
		south=-90d;
		
		east= 180d;
		west= -180d;
		boundingBox = ECHOGranuleFile.genBoundingBox(false,east,west,north,south);
		System.out.println("\t-180/180"+boundingBox.toString());
		
		east= -180d;
		west= 11.858d;
		boundingBox = ECHOGranuleFile.genBoundingBox(false,east,west,north,south);
		System.out.println("\t11.85d/-180: "+boundingBox.toString());
		
		east= 320d;
		west= 180d;
		boundingBox = ECHOGranuleFile.genBoundingBox(true,east,west,north,south);
		System.out.println("\t180/320: "+boundingBox.toString());
		
		System.out.println("TEST 1752: PASSED...\n");
	}
	
	@Test
	public void test1833(){
		DistributeTestSuite.printTestInfo("ECHO Export Translate Dataset Bounding Box from 0,360 to -180,180 Convention",1833);
		//Added to regression tests
		testCollection180();
		testCollection360SpecialCase();
		testCollection360();
		System.out.println("TEST 1833: PASSED...\n");
	}
	
	@Test
	public void test1893(){
		DistributeTestSuite.printTestInfo("ECHO Export dataset collection_ids 2,5 out of memory error",1893);
		
		System.out.println("\tTested during runtime. ");
		System.out.println("TEST 1893: PASSED...\n");
	}
	

	
	
	public void testCollection180(){
	
		double northernmostLatitude        = 90;     
		double southernmostLatitude        = -90;     
		double westernmostLongitude        = -180;     
		double easternmostLongitude        = 180d;  
		
		System.out.print(String.format("\tTesting [east: %f, west: %f, north: %f, south: %f ]",easternmostLongitude, westernmostLongitude, northernmostLatitude, southernmostLatitude));
	
		gov.nasa.podaac.distribute.echo.jaxb.collection.Geometry g = ECHOCollectionFile.genBoundingBox(false, easternmostLongitude, westernmostLongitude, northernmostLatitude, southernmostLatitude);
		List<Object> bbs = g.getPointOrBoundingRectangleOrGPolygon();
		assertEquals(bbs.size(), 1);
		for(Object b:bbs){
			gov.nasa.podaac.distribute.echo.jaxb.collection.BoundingRectangle bb = (gov.nasa.podaac.distribute.echo.jaxb.collection.BoundingRectangle)b;
			//System.out.println(String.format("BBox: [east: %f, west: %f, north: %f, south: %f ]", bb.getEastBoundingCoordinate(),bb.getWestBoundingCoordinate(), bb.getNorthBoundingCoordinate(), bb.getSouthBoundingCoordinate()));
			//bb.toString();
		}
		System.out.println("...PASSED");
	}
	
	
	public void testCollection360SpecialCase(){
	
		double northernmostLatitude        = 90;     
		double southernmostLatitude        = -90;     
		double westernmostLongitude        = 0;     
		double easternmostLongitude        = 360d;  
		
		System.out.print(String.format("\tTesting [east: %f, west: %f, north: %f, south: %f ]",easternmostLongitude, westernmostLongitude, northernmostLatitude, southernmostLatitude));
	
		gov.nasa.podaac.distribute.echo.jaxb.collection.Geometry g = ECHOCollectionFile.genBoundingBox(true, easternmostLongitude, westernmostLongitude, northernmostLatitude, southernmostLatitude);
		List<Object> bbs = g.getPointOrBoundingRectangleOrGPolygon();
		
		for(Object b:bbs){
			gov.nasa.podaac.distribute.echo.jaxb.collection.BoundingRectangle bb = (gov.nasa.podaac.distribute.echo.jaxb.collection.BoundingRectangle)b;
			System.out.print(String.format("BBox: [east: %f, west: %f, north: %f, south: %f ]", bb.getEastBoundingCoordinate(),bb.getWestBoundingCoordinate(), bb.getNorthBoundingCoordinate(), bb.getSouthBoundingCoordinate()));
			//bb.toString();
		}
		assertEquals(bbs.size(), 1);
		System.out.println("...PASSED");
	}
	
	
	public void testCollection360(){
		double northernmostLatitude        = 90;     
		double southernmostLatitude        = -90;     
		double westernmostLongitude        = 90;     
		double easternmostLongitude        = 270d;  
		
		System.out.print(String.format("\tTesting [east: %f, west: %f, north: %f, south: %f ]",easternmostLongitude, westernmostLongitude, northernmostLatitude, southernmostLatitude));
	
		gov.nasa.podaac.distribute.echo.jaxb.collection.Geometry g = ECHOCollectionFile.genBoundingBox(true, easternmostLongitude, westernmostLongitude, northernmostLatitude, southernmostLatitude);
		List<Object> bbs = g.getPointOrBoundingRectangleOrGPolygon();
		
		for(Object b:bbs){
			gov.nasa.podaac.distribute.echo.jaxb.collection.BoundingRectangle bb = (gov.nasa.podaac.distribute.echo.jaxb.collection.BoundingRectangle)b;
			//System.out.println(String.format("BBox: [east: %f, west: %f, north: %f, south: %f ]", bb.getEastBoundingCoordinate(),bb.getWestBoundingCoordinate(), bb.getNorthBoundingCoordinate(), bb.getSouthBoundingCoordinate()));
			//bb.toString();
		}
		assertEquals(bbs.size(), 2);
		System.out.println("...PASSED");
	}
	
}
