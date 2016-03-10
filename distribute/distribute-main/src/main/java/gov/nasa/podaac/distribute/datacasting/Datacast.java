package gov.nasa.podaac.distribute.datacasting;

import gov.nasa.podaac.distribute.common.DistributeProperty;
import gov.nasa.podaac.distribute.common.Query;
import gov.nasa.podaac.distribute.common.QueryFactory;

import gov.nasa.podaac.inventory.api.Constant.GranuleStatus;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.DatasetElement;
import gov.nasa.podaac.inventory.model.DatasetSoftware;
import gov.nasa.podaac.inventory.model.ElementDD;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.GranuleArchive;
import gov.nasa.podaac.inventory.model.GranuleMetaHistory;
import gov.nasa.podaac.inventory.model.GranuleReal;
import gov.nasa.podaac.inventory.model.GranuleReference;
import gov.nasa.podaac.inventory.model.GranuleSpatial;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;


/*
 * EMSReport tool is run against the archive or ingest*** databases and exports
 * information in the EMS format show below. The default behavior of the program
 * is to run the report for "yesterday." An optional parameter -d "dd/mm/yyyy"
 * can have the report run for any particular day. Future editions will Allow
 * the reporting tool to filter on datasets or granule id ranges.
 * 
 * ***ingest EMS is still run against the ORACLE database, only it looks at ingest times, not archive times
 */

public class Datacast {
	
	
	private static String SEPARATOR = "|&|";
	private static String sep = SEPARATOR;
	
	private static String dsShortName = null;
	
	private static boolean useShortName = false;
	private static Log log = null;
	private static Options options = init();
	private static String date = null;
	private static String endDate = null;
	private static String outputPath = null;
	private static Dataset d = null;
	
	
	private static Calendar begin = Calendar.getInstance();
	private static Calendar end = Calendar.getInstance();
	
	private static String usage = "Print usage here";
	
	public static void main(String[] args){
		Query q = QueryFactory.getInstance().createQuery();
		Calendar start = Calendar.getInstance();
		processCmdLine(args);
		
		/*
		 * process dataset
		 * 
		 * Basically: get the dataset Id from shortname,
		 * use dsId to get the granules within the specified begin/end range
		 * for the granules, create the ingest files in output path 
		 */
		Integer dsId = getDatasetId(dsShortName);
		if(dsId == null){
			System.out.println("Dataset does not exist, check the name and try again.");
			System.exit(-1);
		}
		
		List<Granule> gList = fetchGranules(dsId);
		log.info("total number of granules to process: " + gList.size());
		for(Granule g : gList){
			//Granule g = q.fetchGranuleByIdDeep(i);
			writeGranuleFile(g);
		}
		//end program execution
		
		Calendar stop = Calendar.getInstance();
		long durationInSeconds = stop.getTimeInMillis() - start.getTimeInMillis();
		durationInSeconds = durationInSeconds / 1000;

		logReport(durationInSeconds);

	}
	
	public static void writeGranuleFile(String gName, String dName, String extentsInfo, String outDirectory){
		Query q = QueryFactory.getInstance().createQuery();
		System.out.println("Processing Granule: " + gName);
		
		FileWriter fstream = null;
		 BufferedWriter out = null;
		try {
			fstream = new FileWriter(outDirectory + File.separator + gName + ".item");
			out = new BufferedWriter(fstream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Granule g = q.findGranuleByName(gName);
		g = q.fetchGranuleByIdDeep(g.getGranuleId());
		Dataset d = q.findDatasetByShortNameDeep(dName);
		
		
		//can write to 'fstream'
		//System.out.println("Granule Name: " + g.getName());
		String productName = g.getName();
		String title = d.getLongName();
		String description = d.getDescription();
		//String pubdate = printDate(Calendar.getInstance(TimeZone.getTimeZone("GMT")));
		String pubdate = printDateFromLong(g.getArchiveTimeLong());
		String enclosure= null;
		String guid = null;
		
		String url = null;
		for(GranuleReference gr : g.getGranuleReferenceSet())
		{
			if(gr.getType().equals("LOCAL-FTP"))
			{
				 //enclosure = url + ", " + ga.getFileSize() +", application/x-" + d.getDatasetPolicy().getDataFormat().toLowerCase();
				 url = gr.getPath();
				 guid = gr.getPath();
			}
		}
		
		for(GranuleArchive ga : g.getGranuleArchiveSet())
		{
			if(ga.getType().equals("DATA"))
			{
				 enclosure = url + ", " + ga.getFileSize() +", application/x-" + d.getDatasetPolicy().getDataFormat().toLowerCase(); 
			}
		}
		if(enclosure == null)
			enclosure = url + ",0, application/x-" + d.getDatasetPolicy().getDataFormat().toLowerCase();
		

		
		Calendar start =  Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		start.setTimeInMillis(g.getStartTimeLong());
		
		Calendar stop =  Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		stop.setTimeInMillis(g.getStopTimeLong());
		try {
			out.write("title=" +productName );
//			out.write("\nproductName=" + productName ); //not sure if this is needed anymore.
			out.write("\ndescription=" + productName);
			out.write("\npubDate=" + pubdate);
			out.write("\nenclosure=" + enclosure );
			out.write("\nguid=" + guid );
			out.write("\nacquisitionStartDate=" + printDateFromLong(g.getStartTimeLong()) );
			out.write("\nacquisitionEndDate=" + printDateFromLong(g.getStopTimeLong()));
			out.write("\nextentsPoly=" + extentsInfo);
//			out.write("\ncustomElement=starttime," +printXMLDate(start));
//			out.write("\ncustomElement=stoptime," +printXMLDate(stop));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
private static void writeGranuleFile(Granule g) {
	
	Query q = QueryFactory.getInstance().createQuery();
	System.out.println("Processing Granule: " + g.getName());
	String extents = null;
	FileWriter fstream = null;
	 BufferedWriter out = null;
	try {
		fstream = new FileWriter(outputPath + File.separator + g.getName() + ".item");
		out = new BufferedWriter(fstream);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    
	if(out == null)
	{
		log.debug("Error outputing datacast information for file " + outputPath + File.separator + g.getName() + ".xml" );
		return;
	}
	
		//System.out.println("Granule Name: " + g.getName());
		String productName = g.getName();
		String title = d.getLongName();
		String description = d.getDescription();
		String pubdate = printDate(Calendar.getInstance(TimeZone.getTimeZone("GMT")));
		String enclosure= null;
		String guid = null;
		
		String url = null;
		for(GranuleReference gr : g.getGranuleReferenceSet())
		{
			log.debug("refType: "+gr.getType().toString());
			if(gr.getType().equals("LOCAL-FTP"))
			{
				 //enclosure = url + ", " + ga.getFileSize() +", application/x-" + d.getDatasetPolicy().getDataFormat().toLowerCase();
				 url = gr.getPath();
				 guid = gr.getPath();
			}
		}
		
		for(GranuleArchive ga : g.getGranuleArchiveSet())
		{
			if(ga.getType().equals("DATA"))
			{
				 enclosure = url + ", " + ga.getFileSize() +", application/x-" + d.getDatasetPolicy().getDataFormat().toLowerCase(); 
			}
		}
		if(enclosure == null)
			enclosure = url + ",0, application/x-" + d.getDatasetPolicy().getDataFormat().toLowerCase();

		/*
		 * EXTENTS work area
		 */
		String[] list = {"westernmostLongitude","easternmostLongitude", "northernmostLatitude", "southernmostLatitude"};
		Double east = 0.0, west=0.0, north=0.0, south=0.0;
		for(String eName : list){
		
			
			ElementDD element = q.findElementDDByShortName(eName);
			element = q.fetchElementDDById(element.getElementId());
			Set<DatasetElement> x = element.getDatasetElementSet();

			x.retainAll(d.getDatasetElementSet());
			log.debug("After Retain: "+ x.size());
			
			DatasetElement de = null;
			if(x.iterator().hasNext())
			{	
				de = x.iterator().next();
			}
			if(de == null)
				continue;

			//System.out.println("Performing match, looking for " + de.getDeId());
			for(GranuleReal gr :g.getGranuleRealSet()){
				
				if(gr.getDatasetElement().getDeId().equals(de.getDeId()))
				{
					if(eName.equals("westernmostLongitude")){
						west = gr.getValue();
						break;
					}
					else if(eName.equals("easternmostLongitude")){
						east = gr.getValue();
						break;
					}
					else if(eName.equals("northernmostLatitude")){
						north = gr.getValue();
						break;
					}
					else if(eName.equals("southernmostLatitude")){
						south = gr.getValue();
						break;
					}
				}
				else{
					log.debug("nothing found gr.getDatasetEle.DEID: " +gr.getDatasetElement().getDeId());
					log.debug("nothing found de.DEID: " +de.getDeId());
				}
			}
		}
		
//		ElementDD element = q.findElementDDByShortName("spatialGeometry");
//		element = q.fetchElementDDById(element.getElementId());
//		Set<DatasetElement> x = element.getDatasetElementSet();
//		x.retainAll(d.getDatasetElementSet());
		
//		log.debug("After Retain: "+ x.size());
		if(g.getGranuleSpatialSet().size() > 0){
			log.debug("Has Spatial. go get it.");
			GranuleSpatial gs = null;
			
			for(GranuleSpatial gspat : g.getGranuleSpatialSet()){
				gs = gspat;
				break;
			}
			
			if(gs != null){
				Polygon polygon = gs.getValue();
				StringBuilder sb = new StringBuilder();
				boolean first = true;
				for(Coordinate c:polygon.getCoordinates()){
					if(first){
						sb.append(c.y + ", " + c.x);
						first = false;
					}
					sb.append("," + c.y + ", " + c.x);
				}
				extents = sb.toString();
			}
			
		}
		
		Calendar start =  Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		start.setTimeInMillis(g.getStartTimeLong());
		
		Calendar stop =  Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		stop.setTimeInMillis(g.getStopTimeLong());
		try {
			out.write("title=" +productName );
//			out.write("\nproductName=" + productName ); //not sure if this is needed anymore.
			out.write("\ndescription=" + productName);
			out.write("\npubDate=" + pubdate);
			out.write("\nenclosure=" + enclosure );
			out.write("\nguid=" + guid );
			out.write("\nacquisitionStartDate=" + printDateFromLong(g.getStartTimeLong()) );
			out.write("\nacquisitionEndDate=" + printDateFromLong(g.getStopTimeLong()));
			if(extents == null)
				out.write("\nextents=" + south + ", " +west + ", "+north + ", "+east);
			else
				out.write("\nextentsPoly=" + extents);
//			out.write("\ncustomElement=starttime," +printXMLDate(start));
//			out.write("\ncustomElement=stoptime," +printXMLDate(stop));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
private static String printXMLDate(Calendar c){
	if(c == null)
		return "Calendar is null";
	String pattern = "%1$tY-%1$tm-%1$tdT%1$tH:%1$tM:%1$tS.%1$tL";
	//String amPm = "%1$tp";
	return String.format(pattern, c);
}

	private static List<Granule> fetchGranules(int dsId) {
	Query q = QueryFactory.getInstance().createQuery();
		List<Granule> gl = null;
		gl = q.listGranuleByDateRange(begin.getTimeInMillis(), end.getTimeInMillis(), "archive_time_long", dsId);
	
		//do checks here?
		
		return gl;
	}

	private static Integer getDatasetId(String dsShortName2) {
		Query q = QueryFactory.getInstance().createQuery();
		if(useShortName){
			d = q.findDatasetByShortNameDeep(dsShortName2);
		}
		else{
			log.debug("pID: " + dsShortName2);
			d = q.findDatasetByPersistentId(dsShortName2);
			
			log.debug("shortName" + d.getShortName());
			d = q.findDatasetByShortNameDeep(d.getShortName());
		}
		if(d == null){
			return null;
		}
		Integer dId = d.getDatasetId();
		return dId;
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
			
			
			if (cmd.hasOption("dataset")) {
				dsShortName = cmd.getOptionValue("dataset");
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
			if(cmd.hasOption("shortname")){
				useShortName = true;
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
		if(dsShortName == null )
		{
			System.out.println("ERROR: The program must be run with a dataset short name.");
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
		if(outputPath == null)
		{
			System.out.println("ERROR: an output path must be specified using the -o option.");
			printUsage();
			System.exit(-1);
		}
		
			
		//should now have all defaults and values necessary, print out the info used to run the program:
		log.info("Running Datacast with the following parameters:");
		//log.info("Archive: " + archive);
		//log.info("Ingest: " + ingest);
		log.info("Dataset to process: " + dsShortName);
		log.info("Output Path: " + outputPath);
		log.info("Beginning date range: " + printDate(begin));
		log.info("End date range: " + printDate(end));
		
	}

	/*
	 * DateTime format is YYYY-MM-DD HH-MMAMorPM
	 */
	private static String printDate(Calendar c){
		if(c == null)
			return "Calendar is null";
		String pattern = "%1$tY:%1$tm:%1$td:%1$tH:%1$tM:%1$tS";
		//String amPm = "%1$tp";
		return String.format(pattern, c);
	}
	

	//MAKE CHANGES HERE
	private static String printDateFromLong(Long longtime){
		/*
		 * TO-DO
		 */
		//if(c == null)
		//	return "Calendar is null";
		
		if(longtime == null)
			return "";
		
		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT"));
		c.setTimeInMillis(longtime);
		String pattern = "%1$tY:%1$tm:%1$td:%1$tH:%1$tM:%1$tS";
		return String.format(pattern, c);
	}
	
	private static void printUsage() {
		//System.out.println(usage);
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("Datacast", options, true );
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
		log.info("Datacast Report Summary");
		log.info("========================================");
		log.info("Total runtime: "+h_zero+hours+":"+m_zero+minutes+":"+s_zero+seconds);
		
	}
	
	@SuppressWarnings("static-access")
	private static Options init(){
		
		DistributeProperty.getInstance();
		log = LogFactory.getLog(Datacast.class);
		log.info("Datacast log initialized");
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
                .isRequired()
                .withLongOpt("output-path")
                .withDescription("The path where the output files will be written." )
                .create("o") );
		options.addOption(OptionBuilder.withArgName("shortname")
                .hasArg(false)
                .withLongOpt("use-shortname")
                .withDescription("Flag to use the legacy shortname to find and create products" )
                .create("shortname") );
		options.addOption(OptionBuilder.withArgName("dataset")
                .hasArg()
                .isRequired()
                .withLongOpt("dataset")
                .withDescription("The dataset, persistent id on which to run datacasting" )
                .create("dataset") );
		return options;
		
	}

}
