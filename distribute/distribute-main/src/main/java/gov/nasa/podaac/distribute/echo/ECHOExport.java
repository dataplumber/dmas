//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.podaac.distribute.echo;

import gov.nasa.podaac.distribute.common.DistributeProperty;
import gov.nasa.podaac.distribute.common.Query;
import gov.nasa.podaac.distribute.common.QueryFactory;
import gov.nasa.podaac.inventory.model.Collection;
import gov.nasa.podaac.inventory.model.CollectionDataset;
import gov.nasa.podaac.inventory.model.CollectionProduct;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.DatasetMetaHistory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is a command line tool to export/upload (via ftp) ECHO metadata files.
 *
 * @author clwong
 * @version
 * $Id: ECHOExport.java 14484 2015-11-25 00:18:25Z gangl $
 */
public class ECHOExport {
    
	private static Log log = null;
	private static Options options = init();
	private static boolean forceBB = false;
	private static boolean testOnly = false;
	private static boolean toCMR = true;
	private static ECHORestClient erc  = null;
	private static Integer limit = -1;
	private static boolean outputXml;	
	private Query q = QueryFactory.getInstance().createQuery();

	public static Options getOptions() {
		return options;
	}
	public  void setOptions(Options options) {
		ECHOExport.options = options;
	}
	


	private static ECHORestClient getRestClient(){
		if(erc == null){
			erc = new ECHORestClient();
			erc.setEchoHost(DistributeProperty.getInstance().getProperty("podaac.cmr.host"));
			erc.setTokenHost(DistributeProperty.getInstance().getProperty("podaac.token.host"));
			
			//getAuthFile
			String authFile = DistributeProperty.getInstance().getProperty("podaac.echo.auth.file", "echo.tokens");
			Map<String,String> mp = ECHOAuth.readFile(authFile);
			
			//PW manager
			erc.setUsername(mp.get("user"));
			erc.setPassword(mp.get("pass"));
		}		
		return erc;

	}
	
	private static void processByCollectionId(String collectionId) {
		log.info("processByDatasetId: "+collectionId);
		ECHOCollectionFile echoCollectionFile = new ECHOCollectionFile(collectionId);
		echoCollectionFile.setSubmit(toCMR);
		//echoCollectionFile.setOutput(outputXml);
		echoCollectionFile.export();
		if(toCMR){
			try{
				getRestClient().sendDataset(echoCollectionFile.getCollection());
			}catch(IOException e){
				log.error(e);
				log.error("Error document can be found in data directory.");
				echoCollectionFile.toXmlFile();
			}
		}
		if(outputXml){
			echoCollectionFile.toXmlFile();
		}
		log.info("processByCollectionId completed.");
	}
	
	private static void processGranules (String datasetId) {
		log.info("processGranules for dataset:"+datasetId);
		ECHOGranuleFile echoGranuleFile = new ECHOGranuleFile(datasetId);
		echoGranuleFile.setTestOnly(testOnly);
		echoGranuleFile.setSubmit(toCMR);
		echoGranuleFile.setLimit(limit);
		echoGranuleFile.setOutput(outputXml);
		echoGranuleFile.export(forceBB);
		
		log.info("processGranules completed.");
	}

	
	/*
	This method validates all files under the given directory or the given file;
	It validates the xml file(s) aganist xml schema and verifies URLs in the xml file.
	If upload flag is set, it uploads the files to the ftp site 
		by the property, echo.ftp.collection.destination or echo.ftp.granule.destination.
	This runs recursetively if it is a directory.
	*/
	private static void printUsage() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("ECHOExport", options, true );
		System.exit(0);
	}
	
	@SuppressWarnings("static-access")
	private static Options init() {
		Options options = new Options();
		options.addOption(OptionBuilder.withArgName("path")
                .hasArg()
                .withLongOpt("validate")
                .withDescription("Validate (XML & URLs) files in a given path;" )
                .create("v") );

		options.addOption(OptionBuilder.withArgName("datasetId")
                .hasArg()
                .withLongOpt("did")
                .withDescription("Dataset Persistent ID" )
                .create("d") );

		options.addOption( OptionBuilder.withArgName( "dId1,dId2,dId3,..." )
                .hasArgs()
                .withValueSeparator(',')
                .withLongOpt("dataset-list")
                .withDescription("List of dataset persistentIds" )
                .create("dl") );
				
		options.addOption(OptionBuilder
				.hasArg(false)
				.withLongOpt("dataset")
				.withDescription("Export dataset file; Used with -d")
				.create("D"));
		
		options.addOption(OptionBuilder
				.hasArg(false)
				.withLongOpt("granule")
				.withDescription("Export granule files of the given collectionId; Used with 'c';")
				.create("g"));
		
		options.addOption(OptionBuilder
				.hasArg(true)
				.withLongOpt("limit")
				.withDescription("limit the exporting of granules instead of exporting them all.")
				.create("l"));
		
		options.addOption(OptionBuilder
				.hasArg(false)
				.withLongOpt("test-only")
				.withDescription("Does not update echo_submission_times after granules are sent to CMR.")
				.create("t"));
		
		options.addOption(OptionBuilder
				.hasArg(false)
				.withLongOpt("no-transfer")
				.withDescription("Do not submit granules, collections to CMR.")
				.create("x"));
		
		options.addOption(OptionBuilder.withArgName("force-boundingbox")
				.withLongOpt("fb")
				.withDescription("Forces the use of bounding box for spatial element in export.")
				.create("fb"));

		options.addOption(OptionBuilder
				.hasArg(false)
				.withLongOpt("output-xml")
				.withDescription("Enables output XML files to the data directory.")
				.create("o"));
		return options;
	}
	
	//-------------- main execution here -----
	@SuppressWarnings("static-access")
	private static void processCmdLine(String[] args) {
		ECHOExport ex = new ECHOExport();
		CommandLineParser parser = new BasicParser();
		
		boolean exportGranules = false;
		try {
			CommandLine cmd = parser.parse( options, args);
			if(cmd.hasOption("fb")) forceBB = true;
			if(cmd.hasOption("t")) testOnly = true;
			if(cmd.hasOption("x")) toCMR = false;
			if(cmd.hasOption("g")) exportGranules = true; 
			if(cmd.hasOption("o")) outputXml = true; 
			
			if(cmd.hasOption("l")) {
				limit = Integer.valueOf(cmd.getOptionValue("l"));
			}
			
			if (cmd.hasOption("dl")) {
				String[] values = cmd.getOptionValues("dl");
				for (String val : values)
				{
					if(exportGranules){
						processGranules(val);
					}else{
						processByCollectionId(val);
					}
				}
			} else if (cmd.hasOption("d")) {
				log.debug("Has option 'd'");
				String datasetId = cmd.getOptionValue("d");
				if (exportGranules) {
					log.debug("Processing granules.");
					processGranules(datasetId);
				}
				else{
					log.debug("Processing by Collection ID");
					processByCollectionId(datasetId);
				}
			} else printUsage();
		} catch (Exception e) {
			log.error(e.getMessage(), e.getCause());
			e.printStackTrace();
			printUsage();
		}
	}

	public static void main(String[] args) {

		DistributeProperty.getInstance();
		log = LogFactory.getLog(ECHOExport.class);
		processCmdLine(args);
	}
}
