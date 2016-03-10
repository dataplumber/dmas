package gov.nasa.podaac.distribute.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gov.nasa.podaac.distribute.ems.EMSReport;
import gov.nasa.podaac.distribute.ems.EMSReport.EMSTuple;
import gov.nasa.podaac.inventory.api.Constant.GranuleStatus;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.DatasetSoftware;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.GranuleArchive;
import gov.nasa.podaac.inventory.model.GranuleMetaHistory;
import gov.nasa.podaac.inventory.model.Provider;

import org.junit.Test;


public class EMSTest {

	/*
	 * Variables Go Here
	 */
	public String GRANULENAME = "grName";
	
	
	/*
	 * Tests/Methods
	 */
	@Test
	public void testPrintRecords(){
		String result;
		boolean archive = false;
		
		//Null grnaule
		Granule g = null;
		EMSTuple t =((EMSReport.EMSTuple) EMSReport.formatPrint(g, archive, null));
		if(t == null)
			result=null;
		else
			result = t.entry;
		
		assert result == null;
		
		
		//Initilized, empty granule
		g  = new Granule();
		//test empty granule
		EMSReport.formatPrint(g,archive, null);
		assert result == null;
		
		//Correct Granule
		g = new Granule (GRANULENAME,new Date(), new Date((new Date()).getTime() - 10000), new Date(), new Date(), new Date(),
				new Date(), 1, "read", "GZIP", "GZIP", "MD5",GranuleStatus.OFFLINE, null, "/store/temp", "2009/334");
		

		g.setGranuleId(1001);
		
		Dataset d = new Dataset();
		d.setShortName("dsShortName");
		d.setDatasetId(2002);
		
		Provider p = new Provider();
		p.setShortName("dsProvider");
		d.setProvider(p);
		
		DatasetSoftware ds = new DatasetSoftware();
		ds.setSoftwareVersion("SoftwareVersion 2.0.1");
		
		Set<DatasetSoftware> softwareSet = new HashSet<DatasetSoftware>();
		softwareSet.add(ds);
		System.out.println(softwareSet.size());
		
		d.setSoftwareSet(softwareSet);
		g.setDataset(d);
		g.getDataset().getSoftwareSet();
		
		//create archive(s)
		GranuleArchive ga1 = new GranuleArchive();
		GranuleArchive ga2 = new GranuleArchive();
		
		ga1.setName("grData1");
		ga1.setType("DATA");
		ga1.setFileSize(10000L);
		ga1.setStatus("ONLINE");
		ga2.setName("grData2");
		ga2.setType("DATA");
		ga2.setFileSize(10000L);
		ga2.setStatus("ONLINE");
		
		//g.setArchiveTimeLong(g.getArchiveTimeLong() +10000);
		
		g.getGranuleArchiveSet().add(ga1);
		g.getGranuleArchiveSet().add(ga2);
		System.out.println("ArchiveSize: " + g.getGranuleArchiveSet().size());
		
		//create GMH
		GranuleMetaHistory gmh = new GranuleMetaHistory();
		gmh.setLastRevisionDate(new Date());
		g.getMetaHistorySet().add(gmh);
		
		result = ((EMSReport.EMSTuple)EMSReport.formatPrint(g,archive, null)).entry;
		System.out.println("result:\n\t" + result);
		assert result != null;
		
	}
	
	@Test
	public void testJSONReader(){
		
		List<Map<String,Object>> lst = EMSReport.parseJSON("/data/archive/reports/archive.2013-08-08.gsc");
		
		for(Map<String,Object> mapping:lst){
			for(String k: mapping.keySet()){
				System.out.println(String.format("%s:%s",k, mapping.get(k)));
			}
		}
		
		
	}
	
	@Test
	public void testDirReader(){
		Calendar c = Calendar.getInstance();
		System.out.println(c.getTime());
		List<Map<String,Object>> lst = EMSReport.readReportDirectory("/data/archive/reports",c );
		if(lst != null)
			System.out.println("Granules: " + lst.size());
		
		
		c.add(Calendar.DAY_OF_MONTH, -1);
		System.out.println(c.getTime());
		lst = EMSReport.readReportDirectory("/data/archive/reports",c );
		if(lst != null)
			System.out.println("Granules: " + lst.size());
	}
	
	@Test
	public void testSorting(){
		EMSReport ems = new EMSReport();
		EMSReport.EMSTuple t1 = ems.new EMSTuple();
		EMSReport.EMSTuple t2 = ems.new EMSTuple();
		EMSReport.EMSTuple t3 = ems.new EMSTuple();
		EMSReport.EMSTuple t4 = ems.new EMSTuple();
		EMSReport.EMSTuple t5 = ems.new EMSTuple();
		
		t1.entry="entry 3";
		t1.granuleId = 123;
		t1.time = 10003L;
		
		t2.entry="entry 2";
		t2.granuleId = 123;
		t2.time = 10002L;
		
		t3.entry="entry 1";
		t3.granuleId = 123;
		t3.time = 10001L;
		
		t4.entry="entry 0";
		t4.granuleId = 125;
		t4.time = 10000L;
		
		t5.entry="entry -1";
		t5.granuleId = 127;
		t5.time = 9999L;
		
		List<EMSTuple> tList = new ArrayList<EMSTuple>();
		
		tList.add(t1);
		tList.add(t2);
		tList.add(t3);
		tList.add(t4);
		tList.add(t5);
		
		System.out.println("*** PRE SORT ***");
		
		for(EMSTuple t: tList){
			System.out.println(t);
		}
		
		Collections.sort(tList);
		
		System.out.println("*** POST SORT ***");
		for(EMSTuple t: tList){
			System.out.println(t);
		}
		
		Set<EMSTuple> tSet = new HashSet<EMSTuple>();
	    //reverse the order first...
	    Collections.reverse(tList);
	    for(EMSTuple t: tList){
	    	//by doing this in order, we should replace
	    	tSet.add(t);
	    }
	    tList = new ArrayList<EMSTuple>();
	    
	    for(EMSTuple t:tSet){
	    	tList.add(t);
	    }
	    System.out.println("*** POST SET ***");
	    for(EMSTuple t:tList){
	    	System.out.println(t.entry);
	    }
	    
	    
	    System.out.println("*** POST SET SORT ***");
	    Collections.sort(tList);	    
	    for(EMSTuple t:tList){
	    	System.out.println(t.entry);
	    }
		
	}
	
}
