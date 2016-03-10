package gov.nasa.podaac.distribute.subscriber.plugins;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.nasa.podaac.distribute.subscriber.api.Subscriber;
import gov.nasa.podaac.distribute.subscriber.model.Dataset;
import gov.nasa.podaac.distribute.subscriber.model.Granule;
import gov.nasa.podaac.distribute.subscriber.model.GranuleFile;

import gov.nasa.podaac.inventory.api.Service;
import gov.nasa.podaac.inventory.api.ServiceFactory;
import gov.nasa.podaac.inventory.exceptions.InventoryException;

//import gov.nasa.podaac.inventory.api.Query;
//import gov.nasa.podaac.inventory.api.QueryFactory;
import gov.nasa.podaac.inventory.model.GranuleArchive;
import gov.nasa.podaac.distribute.subscriber.model.GranuleReference;

public class IWSSubscriber implements Subscriber {

	static Log _logger = LogFactory.getLog(IWSSubscriber.class);
	public boolean list(Dataset dataset, Date lastRunTime) {
		
		String IWS_HOST = System.getProperty("inventory.host");
		Integer IWS_PORT ;
		try{
			IWS_PORT = Integer.valueOf(System.getProperty("inventory.port"));
		}catch(Exception e){IWS_PORT = 8443;}
		
		// TODO Auto-generated method stub
		_logger.info("BasicSubscriber.list("+dataset.getShortName()+")");
		
		Service q = ServiceFactory.getInstance().createService(IWS_HOST, IWS_PORT);
		
		String[] props = {"shortName"};
		boolean addedGranule = false;
		
		//get DS information
		
		_logger.info("Fetching Dataset information");
		
		
		gov.nasa.podaac.inventory.model.Dataset d = null;
		try {
			d = q.getDataset(dataset.getShortName());
		} catch (InventoryException e1) {
			_logger.error("Error connecting to inventory Service. Could not retrieve dataset");
		}
		if(d!=null)
		{
		    
			dataset.setId(d.getDatasetId());
			dataset.setLongName(d.getLongName());
			dataset.setDescription(d.getDescription());
			dataset.setProcessingLevel(d.getProcessingLevel());
		
		}
		
		_logger.info("Fetching granules for dataset ["+dataset.getId()+"]:" + dataset.getShortName());
		
		//get granules
		//String[] args = {"DATASET_ID","ARCHIVE_TIME"};
		String[] args = {"dataset"};
		//listGranuleId(String...whereClause)
		//for(gov.nasa.podaac.inventory.model.Granule g:q.listGranuleByProperty("dataset",d))
		//List<BigDecimal> li = q.listLatestGranuleIdsByDatasetID(dataset.getId());
		
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(lastRunTime.getTime());
		
		List<Integer> li = null;
		try {
			li = q.getGranuleIdListAll( dataset.getId(), c, null, null);
		} catch (InventoryException e) {
			// TODO Auto-generated catch block
			_logger.error("Error connecting to inventory Service. Could not retrieve list of granules.");
		}
		
		_logger.info("running with last run time: " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS").format(lastRunTime));
		
		for(Integer bd: li)
		{
			//System.out.println(g.getArchiveTime().toString());
			//if(g.getArchiveTime().after(lastRunTime))
			//{
			Integer i = bd;
				gov.nasa.podaac.inventory.model.Granule g = null;
				try {
					g = q.getGranuleById(i);
				} catch (InventoryException e) {
					// TODO Auto-generated catch block
					_logger.error("Error connecting to inventory Service. Could not get granule.");
				} //make a deep copy (get archive and reference)
				
				if(g.getStatus().equals("OFFLINE"))
				{
					_logger.debug("Granule id["+g.getGranuleId()+"] status is offline. skipping granule.");
					continue;
				}
//				if(g.getArchiveTime().after(lastRunTime))
//					{
				_logger.info("adding granule with name: " + g.getName());
				Granule add = new Granule(g.getAccessType(),new Date(g.getCreateTimeLong()), g.getGranuleId(), g.getName(),new Date(g.getStartTimeLong()),g.getStatus().toString(),new Date(g.getStopTimeLong()));
				
				for(GranuleArchive ga : g.getGranuleArchiveSet())
				{
					GranuleFile gf = new GranuleFile();
					
					gf.setName(ga.getName());
					if(g.getRelPath() != null){
						gf.setPath(g.getRootPath() + File.separator + g.getRelPath());
					}
					else{
						gf.setPath(g.getRootPath());
					}
					
					gf.setStatus(ga.getStatus());
					gf.setType(ga.getType());
					add.addFile(gf);
				}
				
				for(gov.nasa.podaac.inventory.model.GranuleReference gr : g.getGranuleReferenceSet())
				{
					
					if(gr.getStatus().equals("IN-PROCESS"))
					{
						//if the granule is online and the refrence is 'in-process' then we really have a problem... shouldn't ever see this one.
						_logger.debug("Reference is 'in-process'. Skipping.");
						_logger.error("Error- granule ["+g.getGranuleId()+"] is online but references are listed as 'IN-PROCESS'.");
						continue;
					}
					GranuleReference gra = new GranuleReference();
					gra.setPath(gr.getPath());
					gra.setStatus(gr.getStatus());
					gra.setType(gr.getType());
					add.addReference(gra);
				}
				dataset.addGranule(add);
				addedGranule = true;
				
//			}
		}
		return true;
	}

}
