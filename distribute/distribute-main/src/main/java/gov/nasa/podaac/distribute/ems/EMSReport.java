package gov.nasa.podaac.distribute.ems;

import gov.nasa.podaac.distribute.common.DistributeProperty;
import gov.nasa.podaac.inventory.api.Query;
import gov.nasa.podaac.inventory.api.QueryFactory;
import gov.nasa.podaac.inventory.api.Constant.GranuleStatus;
import gov.nasa.podaac.inventory.model.Collection;
import gov.nasa.podaac.inventory.model.CollectionDataset;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.DatasetSoftware;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.GranuleArchive;
import gov.nasa.podaac.inventory.model.GranuleMetaHistory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


/*
 * EMSReport tool is run against the archive or ingest*** databases and exports
 * information in the EMS format show below. The default behavior of the program
 * is to run the report for "yesterday." An optional parameter -d "dd/mm/yyyy"
 * can have the report run for any particular day. Future editions will Allow
 * the reporting tool to filter on datasets or granule id ranges.
 * 
 * ***ingest EMS is still run against the ORACLE database, only it looks at ingest times, not archive times
 */

public class EMSReport {

	private static Query q = QueryFactory.getInstance().createQuery();
	private static String SEPARATOR = "|&|";
	private static String sep = SEPARATOR;
	
	
	private static int archiveCount = 0;
	private static int ingestCount = 0;
	private static List<String> archReport = new ArrayList<String>();
	private static List<String> ingReport = new ArrayList<String>();
	
	private static Log log = null;
	private static Options options = init();
	private static boolean ingest=false, archive=false, usePID=false, useLegacyId=false;
	private static String date = null;
	private static String endDate = null;
	private static String outputPath = null;
	private static String granuleStatusPath = null;
	
	private static EMSReport ems;
	
	
	private static Calendar begin = Calendar.getInstance();
	private static Calendar end = Calendar.getInstance();
	
	private static String usage = "Print usage here";
	
	public static void main(String[] args){
	
		ems = new EMSReport();
		Calendar start = Calendar.getInstance();
		processCmdLine(args);
		//loop here
		while(!begin.equals(end))
		{
			log.info("Processing report for date: " + printDate(begin));	
			if(ingest)
				ingReport.add(processIngest());
			if(archive)
				archReport.add(processArchive());
			begin.add(Calendar.DATE, 1);
		}
		
		//end program execution
		
		Calendar stop = Calendar.getInstance();
		long durationInSeconds = stop.getTimeInMillis() - start.getTimeInMillis();
		durationInSeconds = durationInSeconds / 1000;
		
		
		logReport(durationInSeconds);

	}

	private static String processArchive() {
		//determine file Name
		File f = getFileName("Arch");
		//get data
		
		
		List<Granule> glist = q.listGranuleByDateRange(begin.getTimeInMillis(), begin.getTimeInMillis()+ 86400000, "ARCHIVE_TIME_LONG");
		log.info("# of granules: " + glist.size());
		int grans = glist.size();
		//go through data and write file		
		BufferedWriter out = null;
		try {
		    out = new BufferedWriter(new FileWriter(f));
			List<EMSTuple> tList = new ArrayList<EMSTuple>();
		    for(Granule g: glist){
		    	tList.add(formatPrint(g, true, null));
		    }
		    
		    /*
		     * Process STATUS Files...
		     */
		    List<Map<String,Object>> grMap = readReportDirectory(granuleStatusPath, begin);
		    //System.out.println(grMap.size());
		    
		    for(Map<String,Object> m : grMap){
		    	
		    	log.debug("Processing report operation: "+m.get("op"));
		    	if(m.get("op").equals("DELETE") || m.get("op").equals("FULL_DELETE") || m.get("op").equals("REASSOCIATE") || m.get("op").equals("ROLLING_STORE")){
		    		log.debug("Processing granule " + m.get("granuleId"));
		    		
		    		String[] args = new String[0];
		    		//fetch the granule and the dataset...
		    		Granule g;
		    		Dataset d;
		    		
		    		if(!m.get("op").equals("FULL_DELETE")){
		    			g = q.fetchGranuleByIdDeep(((Long)m.get("granuleId")).intValue(), args);
		    			if(g == null){
		    				log.error("Could not find granule with id: " + ((Long)m.get("granuleId")).intValue() + ". Skipping granule status entry.");
		    				System.out.println("Could not find granule with id: " + ((Long)m.get("granuleId")).intValue() + ". Skipping granule status entry.");
		    				continue;
		    			}
		    			
		    			d = q.fetchDatasetByIdFull(g.getDataset().getDatasetId(), args);
		    		}else{
		    			g = new Granule();
		    			g.setName(((String)m.get("granuleName")));
		    			g.setGranuleId(((Long)m.get("granuleId")).intValue());
		    			g.setCreateTimeLong(((Long)m.get("createTime")));
		    			g.setStartTimeLong(((Long)m.get("startTime")));
		    			g.setStopTimeLong(((Long)m.get("stopTime")));
		    			g.setArchiveTimeLong(((Long)m.get("archiveTime")));
		    			
		    			Integer dId = ((Long) m.get("datasetId")).intValue();
		    			d = q.fetchDatasetByIdFull(dId, args);
		    		}
		    		g.setDataset(d);
		    		tList.add(formatPrint(g, true, m));
		    		
		    		archiveCount++;
		    	}
		    }
		    
		    tList = timeOrder(tList);
		    
//		    /*Need to create a set of these...*/
//		    Collections.sort(tList);
//		    Set<EMSTuple> tSet = new HashSet<EMSTuple>();
//		    //reverse the order first...
//		    Collections.reverse(tList);
//		    for(EMSTuple t: tList){
//		    	//by doing this in order, we should replace
//		    	tSet.add(t);
//		    }
//		    tList = new ArrayList<EMSTuple>();
//		    
//		    for(EMSTuple t:tSet){
//		    	tList.add(t);
//		    }
//		    //probably not needed since the hashcode is based on the int anyway
//
//		    Collections.sort(tList);
		    for(EMSTuple t:tList){
		    	out.write( t.entry + "\n");
		    }

		} catch (IOException e) {
			log.error("Error writing to file...", e);
			System.out.println("Error writing to file...");
		}finally{
			if(out!= null)
				try {
					out.close();
				} catch (IOException e) {
					//uh oh...
				}
		}
		archiveCount += grans;
		return archiveCount + " granules processed to archive file " + f.getAbsolutePath();
	}

	

	public static List<EMSTuple> timeOrder(List<EMSTuple> tList) {
		 /*Need to create a set of these...*/
	    Collections.sort(tList);
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
	    //probably not needed since the hashcode is based on the int anyway

	    Collections.sort(tList);
	    return tList;
	}

	@SuppressWarnings("unchecked")
	private static String processIngest() {
		//determine file Name
		File f = getFileName("Ing");
		
		//setup for data (including determine file name)
			
		//get data
		List<Granule> glist = q.listGranuleByDateRange(begin.getTimeInMillis(), begin.getTimeInMillis()+ 86400000, "INGEST_TIME_LONG");
		List<EMSTuple> tList = new ArrayList<EMSTuple>();
		log.info("#of granules: " + glist.size());
		int grans = glist.size();
		//go through data and write file		
		BufferedWriter out = null;
		try {
		    out = new BufferedWriter(new FileWriter(f));
		    
		    for(Granule g: glist){
		    	tList.add(formatPrint(g, false, null));
		    	//out.write(formatPrint(g, false, null) + "\n");
		    }
		    
		    //sort tList
		    Collections.sort(tList);
		    
		    //writeout
		    for(EMSTuple t : tList){
		    	out.write(t.entry + "\n");
		    }
		    
		    
		} catch (IOException e) {
			log.error("Error writing to file...",e);
			System.out.println("Error writing to file...");
		}finally{
			if(out!= null)
				try {
					out.close();
				} catch (IOException e) {
					//uh oh...
				}
		}
		ingestCount += grans;
		return  grans + " granules processed to ingest file " + f.getAbsolutePath();
	}
	
	private static File getFileName(String type) {
		// YYYYMMDD_PODAAC_Ing_DMAS.flt    for ingest file
		// YYYYMMDD_PODAAC_Arch_DMAS.flt   for archive file
		String fname = null;
		File f = new File(outputPath);
		if(!f.exists()){
			System.out.println("Specified output path \""+outputPath+"\" does not exist. Please enter a valid path.");
			System.exit(404);
		}
		
		String datePortion = null;
		String pattern = "%1$tY%1$tm%1$td";
		datePortion =  String.format(pattern, begin);
		
		fname = datePortion + "_PODAAC_" + type+"_DMAS.flt";
		log.debug("start filename: " + fname);
		
		f = new File(outputPath + File.separator +fname);
		if(f.exists()){
			int i = 1;
			while(true){
				String fileRev = outputPath + File.separator + fname + ".rev" + i;
				f = new File(fileRev);
				if(!f.exists())
					break;
				
				i++;
			}
		}
		log.debug("File path: " + f.getAbsoluteFile());
		return f;
		
		
		//return null;
	}

	private static void processCmdLine(String[] args) {
		if(args.length==0)
		{
			printUsage();
			System.exit(0);
		}
		
		CommandLineParser parser = new BasicParser();
		try {
			CommandLine cmd = parser.parse(options, args);
			if (cmd.hasOption("help")) {
				printUsage();
				System.exit(0);
			}
			
			if (cmd.hasOption("ingest")) {
				ingest = true;
			}
			if (cmd.hasOption("pid")) {
				usePID = true;
			}
			if (cmd.hasOption("legacyId")){
				System.out.println("Warning: Using dataset's numeric ID for reports.");
				useLegacyId = true;
			}
			if (cmd.hasOption("archive")) {
				archive = true;
			}
			if (cmd.hasOption("report-end-date")) {
				//use the date supplied by the caller
				endDate = cmd.getOptionValue("report-end-date");
				String pattern = "MM/dd/yyyy";
			    SimpleDateFormat format = new SimpleDateFormat(pattern);
			    Date d = null;
			    try {
					d = format.parse(endDate);
				} catch (java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Calendar tempCal = Calendar.getInstance();
				tempCal.setTime(d);
				TimeZone gmt = TimeZone.getTimeZone("GMT");
				end.setTimeZone(gmt);
				end.set(tempCal.get(Calendar.YEAR), tempCal.get(Calendar.MONTH), tempCal.get(Calendar.DATE) , 0,0,0);
				end.set(Calendar.MILLISECOND, 0);
				
			}
			if (cmd.hasOption("report-date")) {
				//use the date supplied by the caller
				date = cmd.getOptionValue("report-date");
				String pattern = "MM/dd/yyyy";
			    SimpleDateFormat format = new SimpleDateFormat(pattern);
			    Date d = null;
			    try {
					d = format.parse(date);
				} catch (java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}
				Calendar tempCal = Calendar.getInstance();
				tempCal.setTime(d);
				TimeZone gmt = TimeZone.getTimeZone("GMT");
				begin.setTimeZone(gmt);
				begin.set(tempCal.get(Calendar.YEAR), tempCal.get(Calendar.MONTH), tempCal.get(Calendar.DATE) , 0,0,0);
				begin.set(Calendar.MILLISECOND, 0);
				
			}
			else
			{
				Calendar tempCal = Calendar.getInstance();
				tempCal.add(Calendar.DATE, -1);
				TimeZone gmt = TimeZone.getTimeZone("GMT");
				begin.setTimeZone(gmt);
				begin.set(tempCal.get(Calendar.YEAR), tempCal.get(Calendar.MONTH), tempCal.get(Calendar.DATE) , 0,0,0);
				begin.set(Calendar.MILLISECOND, 0);
				
			}
			if (cmd.hasOption("output-path")) {
				outputPath = cmd.getOptionValue("output-path");
			}
			if (cmd.hasOption("gsr")) {
				granuleStatusPath= cmd.getOptionValue("gsr");
			}
		}catch(ParseException pe)
		{
			System.out.println(pe.getMessage());
			printUsage();
			System.out.println("Exiting...");
			System.exit(-1);
		}
		/*
		 * Rules of the program here, including syntax errors
		 */
		
		//if report-end-date is specified but report-date isn't, throw an error
		if(date == null && endDate != null)
		{
			System.out.println("ERROR: An report-end-date cannot be specified if no corresponding report-date is specified");
			printUsage();
			System.exit(-1);
		}
		
		//if report-end-date is before report-date, throw an error
		if(endDate != null && (end.before(begin) || end.equals(begin)))
		{
			System.out.println("ERROR: report-end-date cannot be before or the same as the report-time");
			printUsage();
			System.exit(-1);
		}
		//if niether are spcified, error out and print usage
		//note that both can be specified, and we will run through both reports.
		if(!archive && !ingest )
		{
			System.out.println("ERROR: The program must be run with the ingest and/or archive option.");
			printUsage();
			System.exit(-1);
		}
		
		if(outputPath == null || outputPath.equals("")){
			System.out.println("ERROR: The program must be run with an output path specified (-o).");
			printUsage();
			System.exit(-1);
		}
		
		//if no "end date is specified, set it equal to being+1 day
		if(endDate == null)
		{
			log.info("End date is null setting to begin + 1");
			
			end.setTimeInMillis(begin.getTimeInMillis());
			end.setTimeZone(TimeZone.getTimeZone("GMT"));
			end.add(Calendar.DATE, 1);
			
			log.info("BEGIN: " + begin.getTimeInMillis());
			log.info("END: " + end.getTimeInMillis());
			
		}
		
			
		//should now have all defaults and values necessary, print out the info used to run the program:
		log.info("Running EMS Report with the following parameters:");
		log.info("Archive: " + archive);
		log.info("Ingest: " + ingest);
		log.info("Output Path: " + outputPath);
		log.info("Beginning date range: " + printDate(begin));
		log.info("End date range: " + printDate(end));
		log.info("Granule Status Report directory: " + granuleStatusPath);
	}

	/*
	 * DateTime format is YYYY-MM-DD HH-MMAMorPM
	 */
	private static String printDate(Calendar c){
		if(c == null)
			return "Calendar is null";
		String pattern = "%1$tY-%1$tm-%1$td %1$tl-%1$tM";
		String amPm = "%1$tp";
		return String.format(pattern, c) + String.format(amPm, c).toUpperCase();
	}
	
	private static String printDateFromLong(Long longtime){
		if(longtime == null)
			return "";
		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT"));
		c.setTimeInMillis(longtime);
		String pattern = "%1$tY-%1$tm-%1$td %1$tl-%1$tM";
		String amPm = "%1$tp";
		return String.format(pattern, c) + String.format(amPm, c).toUpperCase();
	}
	
	private static void printUsage() {
		//System.out.println(usage);
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("EMSReport", options, true );
		System.exit(0);
	}

	//this will rpovide a report in the log of the entries in the EMS
	private static void logReport(long time){
		
		int hours, minutes, seconds;
		String h_zero="0",m_zero="0",s_zero="0";
		//calculate time it took to run
		{
			hours = (int)(time /(3600));
			minutes = (int)((time /(60))%60);
			seconds = (int)(time%60);
			if(hours >= 10)	h_zero = "";
			if(minutes >= 10) m_zero = "";
			if(seconds >= 10) s_zero = "";
		}
		log.info("========================================");
		log.info("EMS Report Summary");
		log.info("========================================");
		log.info("Total runtime: "+h_zero+hours+":"+m_zero+minutes+":"+s_zero+seconds);
		log.info("Reported Ingest Granules: " + ingestCount);
		log.info("Reported Archive Granules: " + archiveCount );
		log.info("");
		
		log.info("INGEST DETAILS");
		log.info("========================================");
		for(String s : ingReport){
			log.info(s);
		}
		log.info("");
		log.info("ARCHIVE DETAILS");
		log.info("========================================");
		for(String s : archReport){
			log.info(s);
		}
		log.info("");
		log.info("END REPORT");
		log.info("========================================");
		
	}
	
	@SuppressWarnings("static-access")
	private static Options init(){
		
		DistributeProperty.getInstance();
		log = LogFactory.getLog(EMSReport.class);
		log.info("EMSReport log initialized");
		Options options = new Options();
		options.addOption(OptionBuilder.withArgName("help")
                .hasArg(false)
                .withLongOpt("help")
                .withDescription("Display help and usage information" )
                .create("h") );
		options.addOption(OptionBuilder.withArgName("date")
                .hasArg()
                .withLongOpt("report-date")
                .withDescription("The date on which to run the report. If not specified, yesterday's date is used. Date is in the format MM/DD/YYYY." )
                .create("d") );
		options.addOption(OptionBuilder.withArgName("endDate")
                .hasArg()
                .withLongOpt("report-end-date")
                .withDescription("The end date with which to run the reporting tool. The report-date option is required if this option is used. Date is in the format MM/DD/YYYY. A report will not be generated for the the date entered here (non-inclusive)." )
                .create("ed") );
		options.addOption(OptionBuilder.withArgName("output")
                .hasArg()
                .withLongOpt("output-path")
                .withDescription("The path where the output file(s) will be written." )
                .create("o") );
		options.addOption(OptionBuilder.withArgName("ingest")
                .hasArg(false)
                .withLongOpt("ingest")
                .withDescription("Create the ingest report" )
                .create("ingest") );
		options.addOption(OptionBuilder.withArgName("legacyId")
                .hasArg(false)
                .withLongOpt("use-legacyId")
                .withDescription("Flag to use the legacy dataset id (numeric) when creating reports." )
                .create("legacyId") );
		options.addOption(OptionBuilder.withArgName("pid")
                .hasArg(false)
                .withLongOpt("productID")
                .withDescription("Use the product ID for identification instead of dataset ID." )
                .create("pid") );
		options.addOption(OptionBuilder.withArgName("archive")
                .hasArg(false)
                .withLongOpt("archive")
                .withDescription("Create the archhive report" )
                .create("archive") );
		options.addOption(OptionBuilder.withArgName("gsr")
                .hasArg(true)
                .withLongOpt("granule-status-report")
                .withDescription("The directory containing granule status reports" )
                .create("gsr") );
		return options;
		
	}
	
	public static EMSTuple formatPrint(Granule g, boolean forArchive, Map<String,Object> map){
		if(ems == null)
			ems = new EMSReport();
		EMSTuple t =  ems.new EMSTuple();
		
		if(g == null)
			return null;
		
		if(map == null){
			if(g.getGranuleId() == null)
				return null;
			t.granuleId = g.getGranuleId();
			
		}
		else
			t.granuleId = ((Long)map.get("granuleId")).intValue();
			
		String result = null;
		
		//All of these need to be calculated
		long productVolume = 0;
		int totalFiles = 0;
		String software = null;
		String lastUpdate = null;
		String parentId = null;
		if(g.getDataset() == null)
			return null;
		
		try{
		
			if(useLegacyId)
				parentId = g.getDataset().getDatasetId().toString();
			else
				parentId = g.getDataset().getPersistentId();
		
		}catch(NullPointerException npe){
			return null;
		}
		if(usePID){
			for(CollectionDataset c : g.getDataset().getCollectionDatasetSet()){
				
				if(c.getCollectionDatasetPK().getCollection().getCollectionDatasetSet().size()<2)
				{
					parentId = c.getCollectionDatasetPK().getCollection().getCollectionProduct().getProductId();
				}
				
				
			}
		}
		
		
		
		for(DatasetSoftware ds : g.getDataset().getSoftwareSet())
		{
			software = ds.getSoftwareVersion();
		}
		
		if(software == null)
			software= "";
		String deleteDate = "";
		
		
		if(map != null && map.get("op").equals("FULL_DELETE")){
				t.time = (Long)map.get("time");
				lastUpdate = printDateFromLong(((Long)map.get("time")));
				productVolume = ((Long)map.get("productVolume"));
				totalFiles = ((Long)map.get("productFileCount")).intValue();
		}
		else{
			for(GranuleMetaHistory mh : g.getMetaHistorySet())
			{
				if(map != null){
					lastUpdate = printDateFromLong((Long)map.get("time"));
					t.time = (Long)map.get("time");
				}else{
					lastUpdate = printDateFromLong(mh.getLastRevisionDateLong());
					//t.time =mh.getLastRevisionDateLong();
					t.time = g.getArchiveTimeLong();
				}
				
			}
			for(GranuleArchive ga : g.getGranuleArchiveSet())
			{
				if(ga.getType().equals("DATA"))
				{
					totalFiles++;
					productVolume+=ga.getFileSize();
				}
			/*
			 * Comment this out. DOn't set a delete date even if the granule IS deleted. Get this from the GSR.
			 */
//				if(ga.getStatus().equals("DELETED"))
//				{
//					deleteDate = lastUpdate;
//				}
				
			}
		}
		
		
		if(forArchive)
		{
			/*
			 * format: http://podaac-cm.jpl.nasa.gov:8080/display/Dev/EMS+Report+Generation
			 */
			char Del = 'N';
			
			//calculate if a granule is to be deleted
			
//			if(deleteDate.equals("") && g.getDataset().getDatasetPolicy().getDataClass().equals("ROLLING-STORE"))
//			{
//				Del = 'Y';
//				log.debug("Granule, Dataset: " + g.getGranuleId() + "," + g.getDataset().getDatasetId());
//				g.getDataset();
//				g.getDataset().getDatasetPolicy();
//				Integer diff = g.getDataset().getDatasetPolicy().getDataDuration();
//				if(diff == null){
//					log.info("GranuleID ["+g.getGranuleId()+"] has no duration even though it is listed as a ROLLING-STORE. Continuing without marking for deletion.");
//					Del = 'N';
//				}
//				else{
//					Calendar c = Calendar.getInstance();
//					c.setTimeZone(TimeZone.getTimeZone("GMT"));
//					c.setTimeInMillis(g.getArchiveTimeLong());
//					c.add(Calendar.DAY_OF_YEAR, diff);
//					deleteDate = printDate(c);
//				}
//			}
			if(map != null){
				if(map.get("op").equals("DELETE") || map.get("op").equals("FULL_DELETE") ||  map.get("op").equals("ROLLING_STORE")){
					//delete
					Del = 'Y';
					Calendar c = Calendar.getInstance();
					c.setTimeZone(TimeZone.getTimeZone("GMT"));
					c.setTimeInMillis((Long) map.get("time"));
					deleteDate = printDate(c);
				}
			}
			
			result = g.getGranuleId()
				+ sep + parentId
				+ sep + productVolume 
				+ sep + totalFiles 
				+ sep + printDateFromLong(g.getArchiveTimeLong())  
				+ sep + printDateFromLong(g.getStartTimeLong()) 
				+ sep + printDateFromLong(g.getStopTimeLong())
				+ sep + printDateFromLong(g.getCreateTimeLong())
				+ sep + g.getName()
				+ sep + software
				+ sep + Del
				+ sep + deleteDate
				+ sep + lastUpdate;
			
			
		}
		else{
			String timeToArchive = null;
			String timeToProcess = null;
			String timeToXfer = null;
			String gStatus = "UNSUCCESSFUL";
			
			if(g.getStatus() == GranuleStatus.ONLINE)
				gStatus = "SUCCESSFUL";
			
			try{
				timeToArchive = Long.toString(((g.getArchiveTimeLong() - g.getIngestTimeLong()) / 1000));
				if(g.getArchiveTimeLong() - g.getIngestTimeLong() < 0 || timeToArchive == null)
					timeToArchive = "";
			}
			catch(NullPointerException npe){timeToArchive = "";}
			try{
				//Not sure how to calculate this yet
				timeToProcess = "";
			}
			catch(NullPointerException npe){}
			
			try{
				timeToXfer = Long.toString(((g.getAcquiredTimeLong() - g.getRequestedTimeLong()) / 1000));
				if(g.getAcquiredTimeLong() - g.getRequestedTimeLong() < 0 || timeToXfer.equals("null"))
					timeToXfer="";
			}
			catch(NullPointerException npe){
				timeToXfer="";
			}
			
			String proStartTime = printDateFromLong(g.getAcquiredTimeLong()) ;
			if(proStartTime == null || proStartTime.equals("")){
				log.debug("Acquired time is null, using ingest start time.");
				proStartTime = printDateFromLong(g.getIngestTimeLong());
			}
			
			t.time =g.getIngestTimeLong();
			result = g.getGranuleId()
			+ sep + parentId 
			+ sep + productVolume 
			+ sep + gStatus 
			+ sep + g.getDataset().getProvider().getShortName()
			+ sep + proStartTime 
			+ sep + printDateFromLong(g.getIngestTimeLong())
			+ sep + timeToArchive
			+ sep + timeToProcess
			+ sep + timeToXfer;
			
		}
		log.debug("RESULT: " + result);
		
		
		t.entry = result;
		return t;
	}
	
	public static List<Map<String, Object>> readReportDirectory(String dir, final Calendar cal) {
		List<Map<String,Object>> lst = new ArrayList<Map<String,Object>>();
		if(dir == null)
			return lst;
		
		File d = new File(dir);
		if(!d.exists()){
			System.out.println("Could not find directory: " + dir +". Skipping granule status change reports.");
			return lst;
		}
		
		FilenameFilter fnf = new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		    	if(cal == null){
					return false;
				}
		    	String datePortion = null;
				String pattern = "%1$tY-%1$tm-%1$td";
				datePortion =  String.format(pattern, cal);
		        return name.contains(datePortion);
		    }
		};
		
		for(File f: d.listFiles(fnf)){
			System.out.println("Processing: " + f.getName());
			log.info("Processing: " + f.getName());
			lst.addAll(parseJSON(f.getPath()));
		}
		return lst;
	}
	
	public static List<Map<String,Object>> parseJSON(String filename){
		ArrayList<Map<String,Object>> lst = new ArrayList<Map<String,Object>>();
		try{
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
			JSONParser parser = new JSONParser();
			
			
			while ((line = br.readLine()) != null) {
				Map<String,Object> mapping = new HashMap<String,Object>();
				Object obj = parser.parse(line);
				JSONObject jsonObject = (JSONObject) obj;
				
				JSONObject jsonObject2 = (JSONObject) jsonObject.get("attributes");
				mapping.put("op", jsonObject.get("op"));
				
				for(Object k :jsonObject2.keySet()){
					String key = (String) k;
					mapping.put(key, jsonObject2.get(key));
				}
				lst.add(mapping);
			}
			br.close();
		}catch(FileNotFoundException e){
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (org.json.simple.parser.ParseException e) {
			System.out.println("Error parsing " + filename);
		}
		return lst;
	 }
	
	public class EMSTuple implements Comparable{
		public String entry=null;
		public Long time = null; 
		public int granuleId;
		
		public String toString(){
			return entry;
		}

		public int compareTo(Object arg0) {
			EMSTuple t1 = (EMSTuple) arg0;
			return new Long(this.time - t1.time).intValue();
		}
		
		public int hashCode() {
			return granuleId;
		}
		
		public boolean equals(Object o){
			if(o == null)
				return false;
			EMSTuple t = (EMSTuple) o;	
			if(t.granuleId == this.granuleId)	
				return true;
			else
				return false;
			
		}
	}	
	
	
}

