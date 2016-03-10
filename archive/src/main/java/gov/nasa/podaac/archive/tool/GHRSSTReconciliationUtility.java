package gov.nasa.podaac.archive.tool;



import gov.nasa.podaac.archive.external.InventoryFactory;
import gov.nasa.podaac.common.api.file.FileProduct;
import gov.nasa.podaac.common.api.util.ChecksumUtility;
import gov.nasa.podaac.common.api.util.FileProductUtility;
import gov.nasa.podaac.common.api.util.StringUtility;
import gov.nasa.podaac.inventory.api.QueryFactory;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.GranuleArchive;
import gov.nasa.podaac.inventory.model.GranuleReference;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class GHRSSTReconciliationUtility {

	private static Log log = LogFactory.getLog(GHRSSTReconciliationUtility.class);
	private static Options opt;
	private static int DataSetID;
	private static String usage = "java gov.nasa.podaac.archive.tool.GHRSSTReconciliationUtility -id <integer>";
	private static FileProductUtility vfs;
	
	public static void main(String[] args) {
		log.info("Running GHRSSTReconciliationUtility");
		run(args);
	}
	
	
	public static void run(String[] args)
	{
		createOptions();
		processCmdLine(args);
	}


	private static void processCmdLine(String[] args) {
		CommandLineParser parser = new BasicParser();
		CommandLine cmdLine = null;
		try{
			cmdLine = parser.parse(opt, args);
		}
	    catch (ParseException e) {
	    	printUsage();
	    	System.exit(4);
	    }
	    if(cmdLine == null)
	    {
	    	printUsage();
	    	System.exit(4);
	    }
	    else if(!cmdLine.hasOption("id"))
	    {
	    	printUsage();
	    	System.exit(4);
	    }
	    
	    DataSetID = Integer.parseInt(cmdLine.getOptionValue("id"));
	    log.info("Using DatasetID: " + DataSetID);
	    
	    process(DataSetID);
		

	}
	
	private static void process(int dataset)
	{
		List<Integer> granuleList = InventoryFactory.getInstance().getQuery().getGranuleIdList(DataSetID);
		log.info("Granule list size for dataset ID "+ DataSetID +": " + granuleList.size());
		
		
		
		gov.nasa.podaac.inventory.api.Query q = QueryFactory.getInstance().createQuery();
		List<GranuleArchive> gaList = q.fetchArchiveByDatasetId(dataset);
		for(GranuleArchive ga :gaList)
		{
			Granule g = q.fetchGranuleById(ga.getGranuleId());
			
			if(ga.getStatus().equalsIgnoreCase("ONLINE"))
			{
				long archiveSize = ga.getFileSize();
				String archiveDigest = ga.getChecksum();
				 if(archiveDigest == null)
				 {
					 archiveDigest = new String("");
				 }
				
				//System.out.println("GRANULE ID: " + ga.getGranuleId());
				//Check to see if the file path exists
				//String path = ga.getPath().substring(7);
				 String path = (StringUtility.cleanPaths(g.getRootPath(), g.getRelPath(),ga.getName())).substring(7);
				File f = new File(path);
				
				if(f.exists()) //if the file exists
				{
					log.info("GranuleID " + ga.getGranuleId() +" found at "+ path);
					//checksum
					//String archiveDigest = ga.getChecksum();
					String fileDigest = ChecksumUtility.getDigest(ChecksumUtility.DigestAlgorithm.MD5, f);
					
					//filesize
					//long archiveSize = ga.getFileSize();
					long fileSize = f.length();
					
					//compare & update
					if(!archiveDigest.equals(fileDigest))
					{
						log.info("MD5 Sum Mismatch: archive MD5 = "+ archiveDigest+" file MD5= " + fileDigest);
						//ga.setChecksum(fileDigest);
						InventoryFactory.getInstance().getAccess().updateGranuleArchiveChecksum(ga.getGranuleId(), ga.getName(), fileDigest);
					}
					if(archiveSize != fileSize)
					{
						log.info("FileSize Mismatch: archive size = "+ archiveSize+" file size= " + fileSize);
						//ga.setFileSize(fileSize);
						InventoryFactory.getInstance().getAccess().updateGranuleArchiveSize(ga.getGranuleId(), ga.getName(), fileSize);
					}
					
					
				}
				else //file does not exist
				{
					
					log.info("GranuleID " + ga.getGranuleId() +" not found at "+ path);
					log.info("Setting Granule Status to \"DELETED\"");
					InventoryFactory.getInstance().getAccess().updateGranuleArchiveStatus(ga.getGranuleId(), "DELETED");
					log.info("Granule Type: "+ga.getType());
					//if type = DATA, query the granule_reference for type REMOTE-FTP.
					if(ga.getType().equalsIgnoreCase("DATA"))
					{	
						log.info("Granule has type Data, retreiving Granule reference list");
						
						Set<GranuleReference> gRef = InventoryFactory.getInstance().getQuery().getReferenceSet(ga.getGranuleId());
						if(gRef != null)
						{
							log.info("Successfully retreived reference set");
							for(GranuleReference gr : gRef)
							{
								if(gr.getType().equalsIgnoreCase("REMOTE-FTP"))
								{
									log.info("Using VFSUtility to FTP file at " +gr.getPath() );
									//test all until a valid link is found is found
									vfs = new FileProductUtility(gr.getPath());
									
									vfs.setAuthentication("anonymous","podaac@jpl.nasa.gov");
									FileProduct vfsFile = null;
									try {
										vfsFile = vfs.getFile();
									} catch (Exception e) {
										// TODO Auto-generated catch block
										log.info(e.getMessage());
									}								
	
									if(vfsFile != null && vfsFile.isReadable())
									{
										log.info("File successfully retrieved from " + gr.getPath());
										//String archiveDigest = ga.getChecksum();
										String fileDigest = null;
										try
										{
										    fileDigest = ChecksumUtility.getDigest(
										        ChecksumUtility.DigestAlgorithm.toAlgorithm("MD5"),
										        vfsFile.getInputStream()
										    );
										}
										catch(Exception exception)
										{
										    exception.printStackTrace();
										}
										
										//filesize
										//long archiveSize = ga.getFileSize();
										long fileSize = vfsFile.getSize();
										
										//compare & update
										if(!archiveDigest.equals(fileDigest))
										{
											log.info("\tMD5 Sum Mismatch: archive MD5 = "+ archiveDigest+" Remote file MD5= " + fileDigest);
											//ga.setChecksum(fileDigest);
											InventoryFactory.getInstance().getAccess().updateGranuleArchiveChecksum(ga.getGranuleId(), ga.getName(), fileDigest);
										}
										if(archiveSize != fileSize)
										{
											log.info("\tFileSize Mismatch: archive size = "+ archiveSize+" Remote file size= " + fileSize);
											//ga.setFileSize(fileSize);
											InventoryFactory.getInstance().getAccess().updateGranuleArchiveSize(ga.getGranuleId(), ga.getName(), fileSize);
										}
										vfs.cleanUp();
									    break;
									}
									else
									{
										log.info("Cannot FTP file at " +gr.getPath() );
										vfs.cleanUp();
									}
								}
							}
						}
					}
				}
			}
		}
		log.info("Finished reconciling dataset with ID "+ DataSetID);
	}

	private static void printUsage() {
		// TODO Auto-generated method stub
		System.out.println("Usage: " + usage);
	}


	private static void createOptions() {
		// TODO Auto-generated method stub
		opt = new Options();

		Option option = new Option("id", true, "The dataset ID to use for granule identification");
		option.setRequired(true);
        opt.addOption("h", false, "Print help for this application");
        opt.addOption(option);
		
	}
	
}
