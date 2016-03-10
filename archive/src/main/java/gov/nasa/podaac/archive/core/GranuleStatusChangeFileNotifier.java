package gov.nasa.podaac.archive.core;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.nasa.jpl.horizon.sigevent.api.EventType;
import gov.nasa.jpl.horizon.sigevent.api.Response;
import gov.nasa.jpl.horizon.sigevent.api.SigEvent;
import gov.nasa.podaac.archive.external.ArchiveMetadata;
import gov.nasa.podaac.archive.external.InventoryFactory;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.GranuleArchive;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class GranuleStatusChangeFileNotifier implements GranuleStatusChangeNotification{

	private String fileName;
	private OutputStreamWriter out;
	private boolean fileError =false;
	private static Log log = LogFactory.getLog(GranuleStatusChangeFileNotifier.class);
	private String hostName;
	
	
	@Override
	public void notifyChange(GranuleStatusChange gsc, Integer gId, Map<String,Object> mp){
		ArchiveMetadata am = InventoryFactory.getInstance().getQuery().getGranuleMetadata(gId);
		notifyChange(gsc,am, mp);
	}

	@Override
	public void notifyChange(GranuleStatusChange gsc, ArchiveMetadata g, Map<String,Object> mp){
		//granule should have everything here?
		openFile();
		String entry = createGraunleEntry(gsc,g, mp);
		try {
			if(fileError){
				System.out.println(entry);
				log.error(entry);
				//SIG EVENT
				String URL =  System.getProperty("sig.event.url");
				log.debug("sigevent URL: " + URL);
				SigEvent se = new SigEvent(URL);
				//shortname should be either updated or added...
				System.out.println("DATASET ID::::"+g.getGranule().getDataset().getDatasetId());
				Dataset d = InventoryFactory.getInstance().getQuery().fetchDataset(g.getGranule().getDataset().getDatasetId());
				String localhostname = java.net.InetAddress.getLocalHost().getHostName();
				Response r = se.create(EventType.Error, d.getShortName(), "Archive tool", "unknown", localhostname, "Granule Status Change Update Failure - " + g.getGranule().getName(),  99, entry);
				if(r.hasError()){
					log.error("sig event error: " + r.getError());
				}
				
			}else{
				out.append(entry);
				out.append("\n");
			}
		} catch (IOException e) {
			log.error("Error writing to gsc report file:",e);
			System.out.println(entry);
			log.error(entry);
		}
		closeFile();
	}
	
	//need to add a map of thigns to add as well...
	private String createGraunleEntry(GranuleStatusChange gsc, ArchiveMetadata am, Map<String,Object> mp) {
		//This is where the metadata comes from...
		/* 
		 *  Operation (DELETE, FULL DELETE, REASSOCIATE, ROLLING-STORE) GSC	
		 *  Granule (ID, shortname) G
		 *	Date this
		 *	Data file Checksum GA
		 *	Data file size GA
		 *	Dataset (id, persistent ID) D 
		 *	Location - GA
		 *  EMS Archive metrics (Archive time, ingest time, version etc) G
		 *	--REASSOCIATE--
		 *  Previous dataset (Reassociate) CFG
		 *	Previous Location (Reassociate) CDF
		 *	--RELOCATE--
		 *  Previous Location (Reassociate) CDF
		 *  --DELETE--
		 *  n/a
		 *  --ROLLING_STORE--
		 *  n/a 
		 */


		JSONObject subObj = new JSONObject();
		JSONObject mainObj = new JSONObject();	
		Granule g = am.getGranule();
		StringBuilder sb = new StringBuilder();
		long volume = 0;
		int files = 0;
		for(GranuleArchive ga: g.getGranuleArchiveSet()){
			if(ga.getType().equals("DATA")){
				//sb.append(",fileName:"+ga.getName()+",fileSize:"+ga.getFileSize() +",checkSum:"+ga.getChecksum());
				subObj.put("fileName",ga.getName());
				subObj.put("fileSize", ga.getFileSize());
				subObj.put("checksum",ga.getChecksum());
			}
			files++;
			volume += ga.getFileSize();
		}
		
		if(gsc.equals(GranuleStatusChange.RELOCATE) || gsc.equals(GranuleStatusChange.REASSOCIATE)){
			for(String key: mp.keySet()){
				//sb.append(String.format(",%s:%s", key,mp.get(key).toString()));
				subObj.put(key,mp.get(key));
			}
		}
		
		String operation = GranuleStatusChange.getOperation(gsc);
				
		subObj.put("time",new Date().getTime());
		subObj.put("granuleId",g.getGranuleId());
		subObj.put("granuleName",g.getName());
		
		subObj.put("datasetId",g.getDataset().getDatasetId());
		subObj.put("location",g.getRootPath() + File.separator+g.getRelPath());
		
		subObj.put("ingestTime",g.getIngestTimeLong());
		subObj.put("archiveTime",g.getArchiveTimeLong());
		subObj.put("startTime",g.getStartTimeLong());
		subObj.put("stopTime",g.getStopTimeLong());
		subObj.put("createTime",g.getCreateTimeLong());
		
		subObj.put("productVolume",volume);
		subObj.put("productFileCount",files);
		//+ sep + productVolume 
		//+ sep + totalFiles 
		mainObj.put("op",operation);
		mainObj.put("attributes",subObj);
		return mainObj.toJSONString();
		
//		return "[op:"+ operation+",attributes:[time:"+date+" " +
//				",granuleID:"+g.getGranuleId()
//				+ sb.toString() 
//				+",datasetId:" + g.getDataset().getDatasetId() //might not work...
//				+",location:" + g.getRootPath() + File.separator+g.getRelPath()
////				+"previousLocation:" +
////				+"previousDataset:" +
//				+",ingestTime:" + g.getIngestTimeLong()
//				+",archiveTime:" + g.getArchiveTimeLong()
//				+"]]\n";
	}

	private String getFileName(){
		if(hostName == null){
			String localhostname;
			try {
				localhostname = java.net.InetAddress.getLocalHost().getHostName();
				hostName = localhostname;
			} catch (UnknownHostException e) {
				hostName = "localhost";
			}
			
		}
		
		ArchiveProperty.getInstance();
		String fname = null;
		String notifyFileDir = System.getProperty("granule.status.change.dir");
		if(!notifyFileDir.endsWith("/"))
			notifyFileDir += "/";
		
		//get the yyyy/mm/dd based on GMT. So after 5pm, this should be the next day.
		Calendar cal = Calendar.getInstance();
		TimeZone gmt = TimeZone.getTimeZone("GMT");
		cal.setTimeZone(gmt);
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		//print the calendar date using GMT time...
		
		format1.setTimeZone(gmt);
		String formatted = format1.format(cal.getTime());

		fname = notifyFileDir + "archive."+hostName+"."+formatted +".gsc";
		return fname;
	}
	
	private void openFile(){
		fileError = false;
		if(fileName == null)
			fileName = getFileName();
		try{
			FileOutputStream fos = new FileOutputStream(fileName, true); 
			out = new OutputStreamWriter(fos, "UTF-8");
		}catch(IOException e){
			fileError = true;
			System.out.println("***Errors saving to report file. Outputting granule status change to screen. ***");
			//e.printStackTrace();
		}
	}
	
	private void closeFile(){
		if(!fileError){
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
		
		
}
