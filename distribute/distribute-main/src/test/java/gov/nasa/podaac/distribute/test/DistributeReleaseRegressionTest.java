package gov.nasa.podaac.distribute.test;

import static org.junit.Assert.*;

import java.util.*;

import gov.nasa.podaac.distribute.echo.ECHOCollectionFile;
import gov.nasa.podaac.distribute.echo.ECHOGranuleFile;
import gov.nasa.podaac.distribute.echo.jaxb.granule.BoundingRectangle;
import gov.nasa.podaac.distribute.echo.jaxb.granule.Geometry;
import gov.nasa.podaac.distribute.ems.EMSReport;
import gov.nasa.podaac.distribute.ems.EMSReport.EMSTuple;
import gov.nasa.podaac.inventory.model.*;
import gov.nasa.podaac.inventory.api.Constant.*;


import org.junit.Test;

import com.mchange.v1.util.StringTokenizerUtils;



public class DistributeReleaseRegressionTest {

	@Test
	public void testNormal(){
		double northernmostLatitude        = 90;     
		double southernmostLatitude        = -90;     
		double westernmostLongitude        = -180;     
		double easternmostLongitude        = 180d;  
		
		System.out.print(String.format("Testing [east: %f, west: %f, north: %f, south: %f ]",easternmostLongitude, westernmostLongitude, northernmostLatitude, southernmostLatitude));
		Geometry g = ECHOGranuleFile.genBoundingBox(false, easternmostLongitude, westernmostLongitude, northernmostLatitude, southernmostLatitude);
		List<Object> bbs = g.getPointOrBoundingRectangleOrGPolygon();
		assertEquals(bbs.size(), 1);
		for(Object b:bbs){	
			BoundingRectangle bb = (BoundingRectangle)b;
			//System.out.println(String.format("BBox: [east: %f, west: %f, north: %f, south: %f ]", bb.getEastBoundingCoordinate(),bb.getWestBoundingCoordinate(), bb.getNorthBoundingCoordinate(), bb.getSouthBoundingCoordinate()));
		}
		System.out.println("...PASSED");
	}
	
	@Test
	public void testNormal180(){
	
		double northernmostLatitude        = 90;     
		double southernmostLatitude        = -90;     
		double westernmostLongitude        = 50;     
		double easternmostLongitude        = -180d;  
		
		System.out.print(String.format("Testing [east: %f, west: %f, north: %f, south: %f ]",easternmostLongitude, westernmostLongitude, northernmostLatitude, southernmostLatitude));
	
		Geometry g = ECHOGranuleFile.genBoundingBox(false, easternmostLongitude, westernmostLongitude, northernmostLatitude, southernmostLatitude);
		List<Object> bbs = g.getPointOrBoundingRectangleOrGPolygon();
		assertEquals(bbs.size(), 1);
		for(Object b:bbs){
			BoundingRectangle bb = (BoundingRectangle)b;
			//System.out.println(String.format("BBox: [east: %f, west: %f, north: %f, south: %f ]", bb.getEastBoundingCoordinate(),bb.getWestBoundingCoordinate(), bb.getNorthBoundingCoordinate(), bb.getSouthBoundingCoordinate()));
			//bb.toString();
		}
		System.out.println("...PASSED");
	}
	
	@Test
	public void testNormalSplit(){
	
		double northernmostLatitude        = 90;     
		double southernmostLatitude        = -90;     
		double westernmostLongitude        = 50;     
		double easternmostLongitude        = -150d;  
		
		System.out.print(String.format("Testing [east: %f, west: %f, north: %f, south: %f ]",easternmostLongitude, westernmostLongitude, northernmostLatitude, southernmostLatitude));
	
		Geometry g = ECHOGranuleFile.genBoundingBox(false, easternmostLongitude, westernmostLongitude, northernmostLatitude, southernmostLatitude);
		List<Object> bbs = g.getPointOrBoundingRectangleOrGPolygon();
		assertEquals(bbs.size(), 2);
		for(Object b:bbs){
			BoundingRectangle bb = (BoundingRectangle)b;
			//System.out.println(String.format("BBox: [east: %f, west: %f, north: %f, south: %f ]", bb.getEastBoundingCoordinate(),bb.getWestBoundingCoordinate(), bb.getNorthBoundingCoordinate(), bb.getSouthBoundingCoordinate()));
			//bb.toString();
		}
		System.out.println("...PASSED");
	}
	
	@Test
	public void testNormalOn180(){
		//System.out.println("testNormalOn180...");
		double northernmostLatitude        = 90;     
		double southernmostLatitude        = -90;     
		double westernmostLongitude        = 180;     
		double easternmostLongitude        = -30d;  
		
		System.out.print(String.format("Testing [east: %f, west: %f, north: %f, south: %f ]",easternmostLongitude, westernmostLongitude, northernmostLatitude, southernmostLatitude));
	
		Geometry g = ECHOGranuleFile.genBoundingBox(false, easternmostLongitude, westernmostLongitude, northernmostLatitude, southernmostLatitude);
		List<Object> bbs = g.getPointOrBoundingRectangleOrGPolygon();
		
		for(Object b:bbs){
			BoundingRectangle bb = (BoundingRectangle)b;
			//System.out.println(String.format("BBox: [east: %f, west: %f, north: %f, south: %f ]", bb.getEastBoundingCoordinate(),bb.getWestBoundingCoordinate(), bb.getNorthBoundingCoordinate(), bb.getSouthBoundingCoordinate()));
			//bb.toString();
		}
		assertEquals(bbs.size(), 1);
		System.out.println("...PASSED");
	}
	
	@Test
	public void testNormalOnNegative180(){
	
		double northernmostLatitude        = 90;     
		double southernmostLatitude        = -90;     
		double westernmostLongitude        = -180;     
		double easternmostLongitude        = -30d;  
		
		System.out.print(String.format("Testing [east: %f, west: %f, north: %f, south: %f ]",easternmostLongitude, westernmostLongitude, northernmostLatitude, southernmostLatitude));
	
		Geometry g = ECHOGranuleFile.genBoundingBox(false, easternmostLongitude, westernmostLongitude, northernmostLatitude, southernmostLatitude);
		List<Object> bbs = g.getPointOrBoundingRectangleOrGPolygon();
		assertEquals(bbs.size(), 1);
		for(Object b:bbs){
			BoundingRectangle bb = (BoundingRectangle)b;
			//System.out.println(String.format("BBox: [east: %f, west: %f, north: %f, south: %f ]", bb.getEastBoundingCoordinate(),bb.getWestBoundingCoordinate(), bb.getNorthBoundingCoordinate(), bb.getSouthBoundingCoordinate()));
			//bb.toString();
		}
		
		System.out.println("...PASSED");
	}
	
	
	@Test 
	public void testPrintout(){
		System.out.println("-----------------------------");
		System.out.println("Running 0-360 tests..");
		System.out.println("-----------------------------");
		
	}
	
	
	
	
	/*
	 * TESTING 0-360 range
	 */
	
	@Test
	public void testPositive(){
		double northernmostLatitude        = 90;     
		double southernmostLatitude        = -90;     
		double westernmostLongitude        = 50;     
		double easternmostLongitude        = 80d;  
		
		System.out.print(String.format("Testing [east: %f, west: %f, north: %f, south: %f ]",easternmostLongitude, westernmostLongitude, northernmostLatitude, southernmostLatitude));
		Geometry g = ECHOGranuleFile.genBoundingBox(true, easternmostLongitude, westernmostLongitude, northernmostLatitude, southernmostLatitude);
		List<Object> bbs = g.getPointOrBoundingRectangleOrGPolygon();
		assertEquals(bbs.size(), 1);
		for(Object b:bbs){	
			BoundingRectangle bb = (BoundingRectangle)b;
			//System.out.println(String.format("BBox: [east: %f, west: %f, north: %f, south: %f ]", bb.getEastBoundingCoordinate(),bb.getWestBoundingCoordinate(), bb.getNorthBoundingCoordinate(), bb.getSouthBoundingCoordinate()));
		}
		System.out.println("...PASSED");
	}
	
	@Test
	public void testPositive180(){
	
		double northernmostLatitude        = 90;     
		double southernmostLatitude        = -90;     
		double westernmostLongitude        = 50;     
		double easternmostLongitude        = 180d;  
		
		System.out.print(String.format("Testing [east: %f, west: %f, north: %f, south: %f ]",easternmostLongitude, westernmostLongitude, northernmostLatitude, southernmostLatitude));
	
		Geometry g = ECHOGranuleFile.genBoundingBox(true, easternmostLongitude, westernmostLongitude, northernmostLatitude, southernmostLatitude);
		List<Object> bbs = g.getPointOrBoundingRectangleOrGPolygon();
		assertEquals(bbs.size(), 1);
		for(Object b:bbs){
			BoundingRectangle bb = (BoundingRectangle)b;
			//System.out.println(String.format("BBox: [east: %f, west: %f, north: %f, south: %f ]", bb.getEastBoundingCoordinate(),bb.getWestBoundingCoordinate(), bb.getNorthBoundingCoordinate(), bb.getSouthBoundingCoordinate()));
			//bb.toString();
		}
		
		System.out.println("...PASSED");
	}
	
	@Test
	public void testPositiveSplit(){
	
		double northernmostLatitude        = 90;     
		double southernmostLatitude        = -90;     
		double westernmostLongitude        = 50;     
		double easternmostLongitude        = 320d;  
		
		System.out.print(String.format("Testing [east: %f, west: %f, north: %f, south: %f ]",easternmostLongitude, westernmostLongitude, northernmostLatitude, southernmostLatitude));
	
		Geometry g = ECHOGranuleFile.genBoundingBox(true, easternmostLongitude, westernmostLongitude, northernmostLatitude, southernmostLatitude);
		List<Object> bbs = g.getPointOrBoundingRectangleOrGPolygon();
		assertEquals(bbs.size(), 2);
		for(Object b:bbs){
			BoundingRectangle bb = (BoundingRectangle)b;
			//System.out.println(String.format("BBox: [east: %f, west: %f, north: %f, south: %f ]", bb.getEastBoundingCoordinate(),bb.getWestBoundingCoordinate(), bb.getNorthBoundingCoordinate(), bb.getSouthBoundingCoordinate()));
			//bb.toString();
		}
		System.out.println("...PASSED");
	}
	
	@Test
	public void testPositiveSplitMerid(){
	
		double northernmostLatitude        = 90;     
		double southernmostLatitude        = -90;     
		double westernmostLongitude        = 180;     
		double easternmostLongitude        = 320d;  
		
		System.out.print(String.format("Testing [east: %f, west: %f, north: %f, south: %f ]",easternmostLongitude, westernmostLongitude, northernmostLatitude, southernmostLatitude));
	
		Geometry g = ECHOGranuleFile.genBoundingBox(true, easternmostLongitude, westernmostLongitude, northernmostLatitude, southernmostLatitude);
		List<Object> bbs = g.getPointOrBoundingRectangleOrGPolygon();
		assertEquals(bbs.size(), 1);
		for(Object b:bbs){
			BoundingRectangle bb = (BoundingRectangle)b;
			//System.out.println(String.format("BBox: [east: %f, west: %f, north: %f, south: %f ]", bb.getEastBoundingCoordinate(),bb.getWestBoundingCoordinate(), bb.getNorthBoundingCoordinate(), bb.getSouthBoundingCoordinate()));
			//bb.toString();
		}
		System.out.println("...PASSED");
	}
	
	@Test
	public void testPositiveCrossMerid(){
	
		double northernmostLatitude        = 90;     
		double southernmostLatitude        = -90;     
		double westernmostLongitude        = 0;     
		double easternmostLongitude        = 190d;  
		
		System.out.print(String.format("Testing [east: %f, west: %f, north: %f, south: %f ]",easternmostLongitude, westernmostLongitude, northernmostLatitude, southernmostLatitude));
	
		Geometry g = ECHOGranuleFile.genBoundingBox(true, easternmostLongitude, westernmostLongitude, northernmostLatitude, southernmostLatitude);
		List<Object> bbs = g.getPointOrBoundingRectangleOrGPolygon();
		assertEquals(bbs.size(), 2);
		for(Object b:bbs){
			BoundingRectangle bb = (BoundingRectangle)b;
			//System.out.println(String.format("BBox: [east: %f, west: %f, north: %f, south: %f ]", bb.getEastBoundingCoordinate(),bb.getWestBoundingCoordinate(), bb.getNorthBoundingCoordinate(), bb.getSouthBoundingCoordinate()));
			//bb.toString();
		}
		System.out.println("...PASSED");
	}
	
	@Test
	public void testPositiveCrossZero(){
	
		double northernmostLatitude        = 90;     
		double southernmostLatitude        = -90;     
		double westernmostLongitude        = 320;     
		double easternmostLongitude        = 30d;  
		
		System.out.print(String.format("Testing [east: %f, west: %f, north: %f, south: %f ]",easternmostLongitude, westernmostLongitude, northernmostLatitude, southernmostLatitude));
	
		Geometry g = ECHOGranuleFile.genBoundingBox(true, easternmostLongitude, westernmostLongitude, northernmostLatitude, southernmostLatitude);
		List<Object> bbs = g.getPointOrBoundingRectangleOrGPolygon();
		assertEquals(bbs.size(), 1);
		for(Object b:bbs){
			BoundingRectangle bb = (BoundingRectangle)b;
			//System.out.println(String.format("BBox: [east: %f, west: %f, north: %f, south: %f ]", bb.getEastBoundingCoordinate(),bb.getWestBoundingCoordinate(), bb.getNorthBoundingCoordinate(), bb.getSouthBoundingCoordinate()));
			//bb.toString();
		}
		System.out.println("...PASSED");
	}
	
	@Test 
	public void testCollectionBBoxTest(){
		System.out.println("-----------------------------");
		System.out.println("Running Collection 0-360 tests...");
		System.out.println("-----------------------------");
	}
	
	@Test
	public void testCollection180(){
	
		double northernmostLatitude        = 90;     
		double southernmostLatitude        = -90;     
		double westernmostLongitude        = -180;     
		double easternmostLongitude        = 180d;  
		
		System.out.print(String.format("Testing [east: %f, west: %f, north: %f, south: %f ]",easternmostLongitude, westernmostLongitude, northernmostLatitude, southernmostLatitude));
	
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
	
	@Test
	public void testCollection360SpecialCase(){
	
		double northernmostLatitude        = 90;     
		double southernmostLatitude        = -90;     
		double westernmostLongitude        = 0;     
		double easternmostLongitude        = 360d;  
		
		System.out.print(String.format("Testing [east: %f, west: %f, north: %f, south: %f ]",easternmostLongitude, westernmostLongitude, northernmostLatitude, southernmostLatitude));
	
		gov.nasa.podaac.distribute.echo.jaxb.collection.Geometry g = ECHOCollectionFile.genBoundingBox(true, easternmostLongitude, westernmostLongitude, northernmostLatitude, southernmostLatitude);
		List<Object> bbs = g.getPointOrBoundingRectangleOrGPolygon();
		
		for(Object b:bbs){
			gov.nasa.podaac.distribute.echo.jaxb.collection.BoundingRectangle bb = (gov.nasa.podaac.distribute.echo.jaxb.collection.BoundingRectangle)b;
			System.out.println(String.format("BBox: [east: %f, west: %f, north: %f, south: %f ]", bb.getEastBoundingCoordinate(),bb.getWestBoundingCoordinate(), bb.getNorthBoundingCoordinate(), bb.getSouthBoundingCoordinate()));
			//bb.toString();
		}
		assertEquals(bbs.size(), 1);
		System.out.println("...PASSED");
	}
	
	@Test
	public void testCollection360(){
		double northernmostLatitude        = 90;     
		double southernmostLatitude        = -90;     
		double westernmostLongitude        = 90;     
		double easternmostLongitude        = 270d;  
		
		System.out.print(String.format("Testing [east: %f, west: %f, north: %f, south: %f ]",easternmostLongitude, westernmostLongitude, northernmostLatitude, southernmostLatitude));
	
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
