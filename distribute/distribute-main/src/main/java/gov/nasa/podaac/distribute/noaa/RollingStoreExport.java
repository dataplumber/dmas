//Copyright 2013, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.podaac.distribute.noaa;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import gov.nasa.podaac.common.api.util.DateTimeUtility;
import gov.nasa.podaac.distribute.common.DistributeProperty;
import gov.nasa.podaac.inventory.api.Constant;

/**
 * Class to generate a text file to be parsed by NOAA to pick up granules that
 * need to be rolled off.
 *
 * @author nchung
 * @version $Id$
 */
public class RollingStoreExport {

   /**
    * Solr metadata keys
    */
   public static final String GRANULE_ARCHIVE_TIME_LONG = "Granule-ArchiveTimeLong";
   public static final String GRANULE_ARCHIVE_CHECKSUM = "GranuleArchive-Checksum";
   public static final String GRANULE_ARCHIVE_TYPE = "GranuleArchive-Type";
   public static final String GRANULE_REFERENCE_PATH = "GranuleReference-Path";
   public static final String GRANULE_REFERENCE_TYPE = "GranuleReference-Type";
   public static final String GRANULE_NAME = "Granule-Name";
   
   private static Log log = LogFactory
         .getLog(RollingStoreExport.class);

   private static Options options = init();

   @SuppressWarnings("static-access")
   private static Options init() {
      DistributeProperty.getInstance();

      Options options = new Options();
      options.addOption(OptionBuilder.withArgName("help").hasArg(false)
            .withLongOpt("help")
            .withDescription("Display help and usage information").create("h"));
      options.addOption(OptionBuilder
            .withArgName("persistentId1,persistentId2,persistentId3,...")
            .hasArg().withLongOpt("datasetList")
            .withDescription("List of persistent IDs").create("dl"));
      options.addOption(OptionBuilder.withArgName("number of days").hasArg()
            .withLongOpt("daysAgo").withDescription("Archived n days ago")
            .create("d"));
      options.addOption(OptionBuilder.withArgName("file").hasArg()
            .withLongOpt("file").withDescription("Full path to index file")
            .create("f"));
      return options;
   }

   private static void printUsage() {
      HelpFormatter formatter = new HelpFormatter();
      formatter.setWidth(100);
      formatter.printHelp("RollingStoreExport", options, true);
      System.exit(0);
   }

   private static void processCmdLine(String[] args) {
      if (args.length == 0) {
         printUsage();
         System.exit(0);
      }

      List<String> persistentIds = null;
      int numDays = 30;
      File file = null;

      CommandLineParser parser = new BasicParser();

      try {
         CommandLine cmd = parser.parse(options, args);
         if (cmd.hasOption("help")) {
            printUsage();
            System.exit(0);
         }

         if (cmd.hasOption("daysAgo")) {
            numDays = Integer.parseInt(cmd.getOptionValue("daysAgo"));
         }

         if (cmd.hasOption("datasetList")) {
            persistentIds = Arrays.asList(cmd.getOptionValue("datasetList")
                  .split(","));
         } else {
            // Locate datasetList from configuration file
            String datasetList = System.getProperty("noaa.rolling.store.dataset.list");
            if (datasetList == null) {
               printUsage();
               System.exit(0);
            } else {
               log.info("Using dataset list from distribute.config file: " + datasetList);
               persistentIds = Arrays.asList(datasetList
                     .split(","));
            }
         }

         if (cmd.hasOption("file")) {
            file = new File(cmd.getOptionValue("file"));
         } else {
            printUsage();
            System.exit(0);
         }

      } catch (ParseException e) {
         log.error("Cannot parse commands ", e);
         e.printStackTrace();
         printUsage();
      }
      // should now have all defaults and values necessary, print out the info
      // used to run the program:
      log.info("Running RollingStoreExport with the following parameters:");
      log.info("Dataset List: " + persistentIds);
      log.info("Number of days: " + numDays);
      log.info("Index file: " + file);
      log.info("distribute.solr.url (from distribute.config file): "
            + System.getProperty("distribute.solr.url"));
      log.info("distribute.solr.items.per.page (from distribute.config file): "
            + System.getProperty("distribute.solr.items.per.page"));
      
      createIndex(System.getProperty("distribute.solr.url"), Integer.parseInt(
            System.getProperty("distribute.solr.items.per.page")),
            persistentIds, numDays, file);
   }

   private static JSONObject getSolrJsonResponse(String url) {
      BufferedReader reader = null;
      JSONObject jsonObj = null;
      try {
         URI uri = new URI(url);
         reader = new BufferedReader(new InputStreamReader(uri.toURL()
               .openStream()));
         JSONParser parser = new JSONParser();
         jsonObj = (JSONObject) parser.parse(reader);
      } catch (URISyntaxException e) {
         log.error("Unable to make request to Solr: " + url, e);
      } catch (MalformedURLException e) {
         log.error("Unable to make request to Solr: " + url, e);
      } catch (IOException e) {
         log.error("Unable to make request to Solr: " + url, e);
      } catch (org.json.simple.parser.ParseException e) {
         log.error("Unable to parse response from Solr: " + url, e);
      } finally {
         if (reader != null) {
            try {
               reader.close();
            } catch (IOException e) {
               log.warn("Cannot close reader", e);
            }
         }
      }
      return jsonObj;
   }

   /**
    * Writes granule archive time, ftp path, checksum to specified file.
    *
    * @param solrUrl
    *           URL path to Solr granule index
    * @param resultsPerPage
    *           max number of granules to be returned by Solr
    * @param persistentIds
    *           list of dataset persistent IDs
    * @param numDays
    *           number of days ago when granules were archived
    * @param file
    *           full path to output file
    */
   public static void createIndex(String solrUrl, int resultsPerPage,
         List<String> persistentIds, int numDays, File file) {
      Date today = new Date();
      Calendar cal = new GregorianCalendar();
      cal.setTime(today);
      cal.add(Calendar.DAY_OF_MONTH, -numDays);
      log.info("Search for granules where archive time <= " + cal.getTimeInMillis());

      BufferedWriter out = null;
      try {
         out = new BufferedWriter(new FileWriter(file));
         for (String persistentId : persistentIds) {
            int startIndex = 0;
            long totalResults = 0;
            
            boolean firstIter = true;
            do {
               String urlString = solrUrl + "/select/?"
                     + "fq=GranuleArchive-Status:ONLINE"
                     + "&sort=" + GRANULE_ARCHIVE_TIME_LONG + "+asc"
                     + "&fq=" + GRANULE_ARCHIVE_TIME_LONG + ":[*+TO+" + cal.getTimeInMillis() + "]"
                     + "&q=Dataset-PersistentId:" + persistentId
                     + "&wt=json"
                     + "&fl=" + GRANULE_ARCHIVE_TYPE +  "," + GRANULE_ARCHIVE_CHECKSUM +  "," + GRANULE_REFERENCE_PATH +  "," + GRANULE_REFERENCE_TYPE +  "," + GRANULE_ARCHIVE_TIME_LONG +  "," + GRANULE_NAME
                     + "&rows=" + resultsPerPage + "&start=" + startIndex;
               log.debug("Solr Query: " + urlString);

               JSONObject jsonObj = getSolrJsonResponse(urlString);

               if (jsonObj != null) {
                  JSONObject responseObj = (JSONObject) jsonObj.get("response");
                  
                  totalResults = (Long) responseObj.get("numFound");
                  startIndex += resultsPerPage;
                  if (firstIter) {
                     log.info("Total granules found for dataset "
                           + persistentId + " : " + totalResults);
                  }

                  JSONArray docs = (JSONArray) responseObj.get("docs");
                  for (int i = 0; i < docs.size(); i++) {
                     String checksum = null;
                     String archiveTime = null;
                     String ftp = null;

                     JSONObject doc = (JSONObject) docs.get(i);

                     if (doc.get(GRANULE_ARCHIVE_TIME_LONG) != null) {
                        long archiveTimeLong = 
                              ((Long) ((JSONArray) doc
                                    .get(GRANULE_ARCHIVE_TIME_LONG)).get(0));
                        archiveTime = DateTimeUtility.parseDate(
                              "yyyy/MM/dd HH:mm:ss", new Date(archiveTimeLong));
                     }

                     JSONArray archiveType = (JSONArray) doc
                           .get(GRANULE_ARCHIVE_TYPE);
                     int dataIndex = archiveType
                           .indexOf(Constant.GranuleArchiveType.DATA.toString());
                     if (dataIndex != -1) {
                        checksum = (String) ((JSONArray) doc
                              .get(GRANULE_ARCHIVE_CHECKSUM)).get(dataIndex);
                     }

                     JSONArray refType = (JSONArray) doc
                           .get(GRANULE_REFERENCE_TYPE);
                     int localFtpIndex = refType.indexOf("LOCAL-FTP");
                     if (localFtpIndex != -1) {
                        ftp = (String) ((JSONArray) doc
                              .get(GRANULE_REFERENCE_PATH)).get(localFtpIndex);
                     }
 
                     if (checksum != null && archiveTime != null && ftp != null) {
                        out.write(archiveTime + " " + ftp + " " + checksum
                              + "\n");
                     } else {
                        String granuleName = (String) ((JSONArray) doc
                              .get(GRANULE_NAME)).get(0);
                        if (checksum == null) {
                           log.error("Granule " + granuleName
                                 + " missing checksum.");
                        }
                        if (archiveTime == null) {
                           log.error("Granule " + granuleName
                                 + " missing archive time.");
                        }
                        if (ftp == null) {
                           log.error("Granule " + granuleName
                                 + " missing LOCAL-FTP path.");
                        }
                     }
                  }
               }

               firstIter = false;
            } while (startIndex < totalResults);
            log.info("Finished exporting granules for dataset: " + persistentId);
         }
         log.info("Index file created at: " + file.getAbsolutePath());
      } catch (IOException e) {
         // TODO Raise sigevent
         log.error("Failed to write to index file: " + file.getAbsolutePath(),
               e);
      } finally {
         if (out != null) {
            try {
               out.close();
            } catch (IOException e) {
               log.warn("Cannot close index file: " + file.getAbsolutePath(), e);
            }
         }
      }
   }

   public static void main(String[] args) {
      final long startTime = System.currentTimeMillis();
      processCmdLine(args);
      final long endTime = System.currentTimeMillis();

      log.info("Total execution time: " + (endTime - startTime) + " milliseconds");
   }
}
