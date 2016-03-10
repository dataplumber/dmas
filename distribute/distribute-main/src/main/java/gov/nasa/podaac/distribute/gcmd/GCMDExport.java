//Copyright 2011, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.podaac.distribute.gcmd;

import gov.nasa.podaac.distribute.common.DistributeProperty;
import gov.nasa.podaac.distribute.common.Query;
import gov.nasa.podaac.distribute.common.QueryFactory;
import gov.nasa.podaac.inventory.model.Collection;
import gov.nasa.podaac.inventory.model.CollectionDataset;
import gov.nasa.podaac.inventory.model.Dataset;

import java.io.File;
import java.util.Properties;
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
 * Class to export GCMD DIF file.
 * 
 * @author nchung
 * @version $Id: GCMDExport.java 9736 2012-03-12 23:42:24Z gangl $
 */
public class GCMDExport {
	private static Log log = null;
	private static Options options = init();
	private static boolean validate = false;
	private static boolean useShortName = false;
	private static String defaultDirectory;
	private static Properties properties;
	private Query q = QueryFactory.getInstance().createQuery();
	
	/**
	 * Export dataset to XML.
	 * 
	 * @param dataset
	 *            Inventory dataset object
	 * @param rangeIs360
	 *            if true longitude range is 0 to 360, else -180 to 180
	 * @param directory
	 *            location to store output XML
	 */
	private static void exportDataset(final Dataset dataset,
			final boolean rangeIs360, final String directory) {
		if (dataset != null) {
			GCMDDatasetFile gcmdDatasetFile = new GCMDDatasetFile(dataset,
					rangeIs360);
			gcmdDatasetFile.export();
			String filename = gcmdDatasetFile.toXmlFile(directory);
			if (validate) {
				if (gcmdDatasetFile.validateXML(filename)) {
					log.info(filename + " conforms to DIF schema");
				} else {
					log.warn(filename + " violates DIF schema");
				}
			}
		} else {
			log.error("Dataset does not exist. Check the name and try again.");
		}
	}

	private void processByDatasetShortName(final String shortName) {
		log.info("Process dataset: " + shortName);
		Dataset dataset = null;
		if (useShortName) {
			dataset = q.findDatasetByShortNameDeep(shortName);
		} else {
			dataset = q.findDatasetByPersistentId(shortName);
		}
		if (dataset != null) {
			Dataset ds = q.fetchDataset(dataset.getDatasetId());
			Collection c = q.fetchCollectionByDataset(ds);
			boolean rangeIs360 = false;
			if (c.getCollectionDatasetSet().iterator().hasNext()) {
				rangeIs360 = c.getCollectionDatasetSet().iterator().next().getGranuleRange360().equals('Y');
			}
			exportDataset(ds, rangeIs360, defaultDirectory);
			log.info("Process dataset " + shortName + " completed.");
		} else {
			log.error("Dataset does not exist. Check the name and try again.");
		}
	}

	private void processByCollectionId(final int collectionId) {
		log.info("Process collection ID: " + collectionId);
		Collection collection = q.fetchCollection(collectionId);
		if (collection != null) {
			// Create directory to save dataset XML files
			String directory = defaultDirectory + File.separator
					+ "collection-" + collectionId;
			File f = new File(directory);
			if (!f.exists()) {
				if (!f.mkdirs()) {
					log.warn("Unable to create directory " + directory);
					directory = defaultDirectory;
				}
			}
			Set<CollectionDataset> collectionDatasetSet = collection
					.getCollectionDatasetSet();
			for (CollectionDataset collectionDs : collectionDatasetSet) {
				Dataset dataset = q.fetchDataset(collectionDs
						.getCollectionDatasetPK().getDataset().getDatasetId());
				exportDataset(dataset, collectionDs.getGranuleRange360()
						.equals('Y'), directory);
			}
			log.info("Process collection " + collectionId + " completed.");
		} else {
			log.error("Collection does not exist. Check the collection ID and try again.");
		}
	}

	private static void printUsage() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("GCMDExport", options, true);
		System.exit(0);
	}

	@SuppressWarnings("static-access")
	private static Options init() {
		Options options = new Options();
		options.addOption(OptionBuilder.withArgName("help")
				.hasArg(false)
				.withLongOpt("help")
				.withDescription("Display help and usage information")
				.create("h"));
		options.addOption(OptionBuilder.withArgName("validate")
				.hasArg(false)
				.withLongOpt("validate")
				.withDescription("Validate resulting XML file(s)")
				.create("v"));
		options.addOption(OptionBuilder.withArgName("shortname")
                .hasArg(false)
                .withLongOpt("use-shortname")
                .withDescription("Flag to use the legacy short name to find dataset and generate DIF XML file" )
                .create("shortname"));
		options.addOption(OptionBuilder.withArgName("ds1,ds2,ds3,...")
				.hasArgs()
				.withLongOpt("dataset")
				.withValueSeparator(',')
				.withDescription("Dataset persistent ID or short name")
				.create("dataset"));
		return options;
	}

	private static void processCmdLine(String[] args) {
		GCMDExport gcmde = new GCMDExport();
		CommandLineParser parser = new BasicParser();
		try {
			CommandLine cmd = parser.parse(options, args);

			if (cmd.hasOption("v")) {
				log.debug("Has option 'v'");
				validate = true;
			}
			if(cmd.hasOption("shortname")){
				log.debug("Has option 'shortname'");
				useShortName = true;
			}
			if (cmd.hasOption("dataset")) {
				log.debug("Has option 'dataset'");
				String[] datasetShortNames = cmd.getOptionValues("dataset");
				for (String shortName : datasetShortNames) {
					gcmde.processByDatasetShortName(shortName);
				}
			} else {
				printUsage();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e.getCause());
			e.printStackTrace();
			printUsage();
		}
	}

	public static void main(String[] args) {
		properties = DistributeProperty.getInstance();
		log = LogFactory.getLog(GCMDExport.class);
		defaultDirectory = properties.getProperty("gcmd.data.location");
		File f = new File(defaultDirectory);
		if (!f.exists()) {
			if (!f.mkdirs()) {
				log.warn("Unable to create directory " + defaultDirectory);
			}
		}
		processCmdLine(args);
	}
}
