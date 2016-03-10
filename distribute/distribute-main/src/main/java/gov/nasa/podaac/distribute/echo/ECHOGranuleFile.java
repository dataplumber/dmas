//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.podaac.distribute.echo;

import gov.nasa.podaac.distribute.common.DistributeProperty;
import gov.nasa.podaac.distribute.common.DistributeValidationEventHandler;
import gov.nasa.podaac.distribute.common.FTP;
import gov.nasa.podaac.distribute.common.Query;
import gov.nasa.podaac.distribute.common.QueryFactory;
import gov.nasa.podaac.distribute.common.TimeConversion;
import gov.nasa.podaac.distribute.common.URL;
import gov.nasa.podaac.distribute.echo.jaxb.granule.Boundary;
import gov.nasa.podaac.distribute.echo.jaxb.granule.BoundingRectangle;
import gov.nasa.podaac.distribute.echo.jaxb.granule.CollectionRef;
import gov.nasa.podaac.distribute.echo.jaxb.granule.DataGranule;
import gov.nasa.podaac.distribute.echo.jaxb.granule.DayNightFlag;
import gov.nasa.podaac.distribute.echo.jaxb.granule.Geometry;
import gov.nasa.podaac.distribute.echo.jaxb.granule.GranuleMetaDataFile;
import gov.nasa.podaac.distribute.echo.jaxb.granule.ListOfGranules;
import gov.nasa.podaac.distribute.echo.jaxb.granule.ListOfOnlineAccessURLs;
import gov.nasa.podaac.distribute.echo.jaxb.granule.ListOfOrbitCalculatedSpatialDomains;
import gov.nasa.podaac.distribute.echo.jaxb.granule.ObjectFactory;
import gov.nasa.podaac.distribute.echo.jaxb.granule.OnlineAccessURL;
import gov.nasa.podaac.distribute.echo.jaxb.granule.OrbitCalculatedSpatialDomain;
import gov.nasa.podaac.distribute.echo.jaxb.granule.Point;
import gov.nasa.podaac.distribute.echo.jaxb.granule.RangeDateTime;
import gov.nasa.podaac.distribute.echo.jaxb.granule.Temporal;
import gov.nasa.podaac.distribute.echo.jaxb.granule.Spatial;
import gov.nasa.podaac.distribute.echo.jaxb.granule.HorizontalSpatialDomain;
import gov.nasa.podaac.inventory.api.Constant.GranuleArchiveType;
import gov.nasa.podaac.inventory.api.Constant.LocationPolicyType;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.DatasetMetaHistory;
import gov.nasa.podaac.inventory.model.DatasetVersion;
import gov.nasa.podaac.inventory.model.ElementDD;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.GranuleArchive;
import gov.nasa.podaac.inventory.model.GranuleCharacter;
import gov.nasa.podaac.inventory.model.GranuleDateTime;
import gov.nasa.podaac.inventory.model.GranuleMetaHistory;
import gov.nasa.podaac.inventory.model.GranuleReal;
import gov.nasa.podaac.inventory.model.GranuleReference;
import gov.nasa.podaac.inventory.model.GranuleSpatial;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.dom.DOMResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;



/**
 * This class contains ECHO granule object.
 *
 * @author clwong
 *
 * @version
 * $Id: ECHOGranuleFile.java 14502 2015-12-01 21:35:56Z nchung $
 */
public class ECHOGranuleFile {
	
	private static Log log = LogFactory.getLog(ECHOGranuleFile.class);
	private Integer collectionId = null;
	private String datasetId = null;
	private ECHOCollectionFile echoCollectionFile = null;
	private static List<String> echoGranuleFilenames = new ArrayList<String>();
	private Schema schema = null;
	boolean rangeIs360 = false;
	boolean toCMR = true;
	boolean testOnly = false;
	ECHORestClient erc = null;
	

	private GranuleMetaDataFile echoGranuleFile = new GranuleMetaDataFile();
	private Integer limit = -1;
	private boolean output;

	public ECHOGranuleFile() {  init(); }

	
	private ECHORestClient getRestClient(){
		
		if(erc == null){
			erc = new ECHORestClient();
			erc.setEchoHost(DistributeProperty.getInstance().getProperty("podaac.cmr.host"));
			erc.setTokenHost(DistributeProperty.getInstance().getProperty("podaac.token.host"));
			
			//PW manager
			erc.setUsername("mike.gangl");
			erc.setPassword("W1sconsin");
		}
		
		return erc;
		
	}
	
	public ECHOGranuleFile(String datasetId) {
		this.datasetId = datasetId;
		this.echoCollectionFile = new ECHOCollectionFile(datasetId);
		init();
	}

	public void setTestOnly(boolean to){
		log.debug("Setting testOnly flag to " + to);
		this.testOnly = to;
	}
	
	public void init() {
		SchemaFactory schemaFactory = SchemaFactory
				.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			this.schema = schemaFactory.newSchema(new File(System
					.getProperty("distribute.schemas")
					+ "/Granule.xsd"));
		} catch (SAXException e) {
		}
	}

	public GranuleMetaDataFile getEchoGranuleFile() {
		return echoGranuleFile;
	}
	
	public void setEchoGranuleFile(GranuleMetaDataFile echoGranuleFile) {
		this.echoGranuleFile = echoGranuleFile;
	}
	
	public void export(boolean forceBB) {
//		if (echoCollectionFile!=null) 
//			export(echoCollectionFile.getDataset(), forceBB);
		if (datasetId!=null)
			export(datasetId, forceBB);
		else
			log.error("Unspecified collectionId!");
	}

	public void export(String datasetId, boolean forceBB) {
		//this.collectionId = collectionId;
		this.datasetId = datasetId;
		this.echoCollectionFile = new ECHOCollectionFile(datasetId);
		export(echoCollectionFile.getDataset(), forceBB);
	}

	public void export(Dataset dataset, boolean forceBB) {
		
		
		int errorCount = 0;
		log.info("forcingBB: " + forceBB);
		
		Query q = QueryFactory.getInstance().createQuery();
		
		if(dataset.getDatasetPolicy().getViewOnline().equals('N'))
		{
			log.info("Collection["+collectionId+"] has Collection.product.visibile_flag set to 'N'. Skipping collection export.");
			return;
		}
		
		Date echoSubmitDate = null; 
		for(DatasetMetaHistory dmh : dataset.getMetaHistorySet()){
			if(echoSubmitDate == null){
				echoSubmitDate = dmh.getEchoSubmitDate();
			}
			else if(dmh.getEchoSubmitDate() == null){
				continue;
			}
			else if(echoSubmitDate.before(dmh.getEchoSubmitDate())){
				echoSubmitDate = dmh.getEchoSubmitDate();
			}
		}
		Date newEchoSubmitDate = new Date();
		
		if(echoSubmitDate != null)
			log.info("Collection's Last Submission date: " + echoSubmitDate.toString());
		
		/*
		 * Need to change this to only fetch granules within the correct date.
		 */
		
		//new query:
		//select granule_id from (select granule_id,version_id from (select granule_id, version_id, max(version_id) over (partition by granule_id) max_version from   granule_meta_history where (LAST_REVISION_DATE_LONG > ECHO_SUBMIT_DATE_LONG OR ECHO_SUBMIT_DATE_LONG is null)) where version_id = max_version AND granule_id in (select granule_id from granule where DATASET_ID=39))
		List<java.math.BigDecimal> granuleIdList = q.getResultList(dataset.getDatasetId());
		
		//Query.getGranuleIdList(datasetId);
		int granuleSize = granuleIdList.size();
		if(granuleSize == 0)
		{
			log.info("No granules to process, returning.");
			return;
		}
		
		log.info("granuleSize="+granuleSize);
		//int processCount=0;
		//int collectionNum = 1;
		//int scrollSize = new Integer(System.getProperty("distribute.granule.file.size"));
		
		echoGranuleFile.setDataCenter("PODAAC");
		ListOfGranules echoGranules = new ListOfGranules();
		if(dataset.getDatasetCoverage().getGranuleRange360().equals('Y')){
				rangeIs360 = true;
		}
		
		int i = 1;
		for (java.math.BigDecimal granuleID : granuleIdList) {
			
			
			
				boolean modified = false;
				Integer granuleId = granuleID.intValue();
				Granule granule = q.fetchGranuleByIdDeep(granuleId);
				log.info("Processing Granule ["+ granule.getGranuleId() +"]: " + granule.getName()); 
				
				if(echoSubmitDate != null)
				{
					//check date here
					Set<GranuleMetaHistory> gmhs = granule.getMetaHistorySet();
					for(GranuleMetaHistory gmh : gmhs)
					{
						if(gmh.getLastRevisionDate().after(echoSubmitDate))
							{
								log.info("Last revision date is newer than last Submission date. Generating granule information.");
							    modified = true;
							}
						if(gmh.getEchoSubmitDate() == null)
						{
							log.info("Granule has not been submitted. Generating granule information.");
						    modified = true;
						}
					}
				}
				else
					modified = true;
				
				if(modified==false)
				{
					log.info("File not modified since last update: Skipping processing");
					continue;
				}
				//increment count
				//processCount++;
				//log.info("Granule modified since last submission. Adding to granule file."); 
				//Set<GranuleMetaHistory> gmhs = granule.getMetaHisorySet();
				
				gov.nasa.podaac.distribute.echo.jaxb.granule.Granule echoGranule = 
					new gov.nasa.podaac.distribute.echo.jaxb.granule.Granule();
				echoGranule.setGranuleUR(granule.getName());
				echoGranule.setDataFormat(granule.getDataFormat());
				echoGranule.setInsertTime(TimeConversion.convertDate(granule.getIngestTime()));
				
				// Populate revision dates
				/* Current database has this table as optional. This logic will be further developed.
				Set<GranuleMetaHistory> historySet = granule.getMetaHistorySet();
				List<Date> revisionDates = new ArrayList<Date>();
				for(GranuleMetaHistory history : historySet) {
					revisionDates.add(history.getLastRevisionDate());
				}
				echoGranule.setLastUpdate(TimeConversion.convertDate(Collections.max(revisionDates)));
				*/
				// set this to current date
				echoGranule.setLastUpdate(TimeConversion.convertDate(new Date()));

				// Populate Collection metadata
				CollectionRef echoCollection = new CollectionRef();
				echoCollection.setShortName(dataset.getShortName().toUpperCase());
				echoCollection.setVersionId(
						exportVersion(dataset.getVersionSet())
				);
				// ECHO needs only a set of identifier not both
				//echoCollection.setDataSetId(collection.getShortName()+":" + echoCollection.getVersionId());			
				echoGranule.setCollection(echoCollection);
				
				// Populate Data Granule
				echoGranule.setDataGranule(
						exportDataGranule(granule)
						);

				// Populate the temporal metadata
				echoGranule.setTemporal(
						exportTemporal(granule)
				);
				
				// Populate the Spatial metadata
				echoGranule.setSpatial(
						exportSpatial(granule, forceBB)
				);
				
				// Populate the Orbital Metadata
				echoGranule.setOrbitCalculatedSpatialDomains(
						exportOrbital(granule)
				);
				log.info("Setting granule resource information");
				// Populate the resource information.
				echoGranule.setOnlineAccessURLs(
						exportAccessURL(granule.getGranuleReferenceSet())
				);	
				
			
				if(toCMR){
					//send to CMR
					//update the collection time
					try{
						getRestClient().sendGranule(datasetId, echoGranule);
					}catch(IOException e){
						log.error(e);
						//output the XML
						errorCount++;
						log.error("Granule error: " + this.toXmlString());
						continue;
					}
					if(!testOnly){
						for( GranuleMetaHistory gmh : granule.getMetaHistorySet())
						{
							if(!testOnly)
								//q.updateGranuleEchoSubmitTime(gmh);
								q.updateGranuleEchoSubmitTime(granule, new Date());
						}
					}
				}
				
				if(output){
					String outPath = (String)DistributeProperty.getInstance().getProperty("distribute.data.location");
					this.toXmlFile(outPath + "/" + granule.getName() +".xml", echoGranule);
				}
				
				if(i == limit){
					log.debug("Reached limit ["+limit+"] of processing. Returning."); 
					return;
				}
				i++;
		}			
	}
	
	
	public boolean validate (gov.nasa.podaac.distribute.echo.jaxb.granule.Granule echoGranule) {
		GranuleMetaDataFile tempEchoGranuleFile = new GranuleMetaDataFile();
		tempEchoGranuleFile.setDataCenter("PODAAC");
		ListOfGranules echoGranules = new ListOfGranules();
		echoGranules.getGranule().add(echoGranule);
		tempEchoGranuleFile.setGranules(echoGranules);
		try {
			JAXBContext jc = JAXBContext
				.newInstance("gov.nasa.podaac.distribute.echo.jaxb.granule");
			JAXBElement<GranuleMetaDataFile> element = (new ObjectFactory())
				.createGranuleMetaDataFile(tempEchoGranuleFile);
			Marshaller m = jc.createMarshaller();
			if (this.schema==null){
				log.info("Schema is null... returning false");
				return false;}
			m.setSchema(this.schema);
			m.setEventHandler(new DistributeValidationEventHandler());
			DOMResult result = new DOMResult();
			m.marshal( element, result );
		} catch (JAXBException e) {
			log.error("Invalid Granule Object:\n"+e.getMessage());
		}
		return true;
	}

	public boolean validate(String filename) {
		if (validateXML(filename)) {
			if (verifyURLs()) return true;
		}
		return false;
	}
	
	public boolean verifyURLs() {
		boolean status = true;
		List<gov.nasa.podaac.distribute.echo.jaxb.granule.Granule> granules 
										= echoGranuleFile.getGranules().getGranule();
		for (gov.nasa.podaac.distribute.echo.jaxb.granule.Granule granule : granules) {
			List<OnlineAccessURL> urls = granule.getOnlineAccessURLs().getOnlineAccessURL();
			for (OnlineAccessURL url : urls) {
				if (!URL.verify(url.getURL())) status = false;
			}
		}
		return status;
	}
	
	public boolean validateXML(String filename) {
		JAXBContext context;
		try {
			context = JAXBContext
				.newInstance("gov.nasa.podaac.distribute.echo.jaxb.granule");
			Unmarshaller unmarshaller = context.createUnmarshaller();
			if (this.schema==null) return false;
		    unmarshaller.setSchema(this.schema);
		    unmarshaller.setEventHandler(new DistributeValidationEventHandler());
			echoGranuleFile = (GranuleMetaDataFile) 
			((JAXBElement)unmarshaller.unmarshal(new File(filename))).getValue();
			echoGranuleFilenames.add(filename);
		    return true;
		} catch (Exception e) {
			log.error("Non-ECHO-Granule File:\n"+e.getMessage());
			return false;
		}
	}
	
	/**
	 *  Create the XML representation of the ECHO Granule submission.
	 *
	 *  @param  filename   The filename for output file.
	 */
	public boolean toXmlFile(String filename, gov.nasa.podaac.distribute.echo.jaxb.granule.Granule g) {
		log.debug("Writing Granule Data");
		File outputFile = new File(filename);
		try {
			JAXBContext jc = JAXBContext
					.newInstance("gov.nasa.podaac.distribute.echo.jaxb.granule");
			JAXBElement<gov.nasa.podaac.distribute.echo.jaxb.granule.Granule> element = (new ObjectFactory())
					.createGranule(g);
			Marshaller m = jc.createMarshaller();
			// validation
			//if (this.schema==null) return false;
			//m.setSchema(this.schema);
			//m.setEventHandler(new DistributeValidationEventHandler());
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(element, outputFile);			
			//echoGranuleFilenames.add(filename);
			return true;
		} catch (JAXBException je) {
			log.error(je.getMessage(), je.getCause());
		}
		if (outputFile.exists()) outputFile.delete();
		return false;
	}
	
	public String toXmlString(){
		try {
			JAXBContext jc = JAXBContext
					.newInstance("gov.nasa.podaac.distribute.echo.jaxb.granule");
			JAXBElement<GranuleMetaDataFile> element = (new ObjectFactory())
					.createGranuleMetaDataFile(echoGranuleFile);
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter sw = new StringWriter();
			m.marshal(element, sw);			
			return sw.toString();
			
		} catch (JAXBException je) {
			log.error(je.getMessage(), je.getCause());
		}
		return null;
	}
	
	public void upload(String ftpUsername, String ftpPassword) {
		String destination = System.getProperty("echo.ftp.granule.destination");
		FTP.getInstance(ftpUsername, ftpPassword);
		for (String granuleFile : echoGranuleFilenames) {
			log.info("uploading "+FTP.copyFile(granuleFile, destination));
		}
		FTP.release();
	}

	// Populate the version information.
	private String exportVersion(Set<DatasetVersion> versionSet) {
		List<Integer> versionIds = new ArrayList<Integer>();
		for (DatasetVersion version : versionSet) 
			versionIds.add(version.getDatasetVersionPK().getVersionId());
		if(versionIds.size() == 0)
			return "1";
		return String.valueOf(Collections.max(versionIds));
	}

	// Populate Data Granule
	private DataGranule exportDataGranule(Granule granule) {
		DataGranule echoDataGranule = new DataGranule();
		echoDataGranule.setLocalVersionId(String.valueOf(granule.getVersion()));
		echoDataGranule.setProductionDateTime(TimeConversion.convertDate(granule.getCreateTime()));
		echoDataGranule.setDayNightFlag(DayNightFlag.UNSPECIFIED);
		
		Set<GranuleCharacter> characterSet = granule.getGranuleCharacterSet();
		for (GranuleCharacter element : characterSet) {
			if(element.getDatasetElement().getElementDD().getShortName() == null){
				Query q = QueryFactory.getInstance().createQuery();
				ElementDD edd = q.fetchElementDDById(element.getDatasetElement().getElementDD().getElementId());
				element.getDatasetElement().setElementDD(edd);
			}
			
			if (element.getDatasetElement().getElementDD().getShortName().equals("dayNightType")) {
				if (element.getValue().equalsIgnoreCase(DayNightFlag.DAY.name()))
					echoDataGranule.setDayNightFlag(DayNightFlag.DAY);
				else if (element.getValue().equalsIgnoreCase(DayNightFlag.NIGHT.name()))
					echoDataGranule.setDayNightFlag(DayNightFlag.NIGHT);
				else if (element.getValue().equalsIgnoreCase(DayNightFlag.BOTH.name()))
					echoDataGranule.setDayNightFlag(DayNightFlag.BOTH);
			}
		}

		Set<GranuleArchive> archiveSet = granule.getGranuleArchiveSet();
		for (GranuleArchive archive : archiveSet) {
			if (archive.getType().equalsIgnoreCase(GranuleArchiveType.DATA.toString())) 
				echoDataGranule.setSizeMBDataGranule((Double)(archive.getFileSize()/(1024.*1024)));
		}
		return echoDataGranule;
	}
	
	// Populate the temporal metadata
	private Temporal exportTemporal(Granule granule) {		
		Temporal echoTemporal = new Temporal();
		RangeDateTime echoRange = new RangeDateTime();
		echoRange.setBeginningDateTime(TimeConversion.convertDate(granule.getStartTime()));
		echoRange.setEndingDateTime(TimeConversion.convertDate(granule.getStopTime()));
		echoTemporal.setRangeDateTime(echoRange);
		return echoTemporal;
	}

	private static BigDecimal convertBoundingVal(double val, boolean lon)
	{
		double convertedVal = 0;
		if(lon)
		{
			if(val > 180)
				convertedVal =  val - 360;
			else
				convertedVal = val;
		}
		else
		{
			if(val > 90)
				convertedVal =val-90;
			else if(val < -90)
				convertedVal =val+90;
			else
				convertedVal = val;
		}
		if(val != convertedVal)
			log.warn("Collection has longitude range of 0-360. Performing Automated conversion to -180/180 range. Converted value ["+val+"] to ["+convertedVal+"]");
		
		return BigDecimal.valueOf(convertedVal);
	}
	
	public Spatial exportSpatial(Granule granule, boolean ForceBB) {		
		Spatial echoSpatial = new Spatial();
		Geometry boundingBox = new Geometry();
		BoundingRectangle br = new BoundingRectangle();
		
		BoundingRectangle br2;
		HorizontalSpatialDomain hsd = new HorizontalSpatialDomain();
		
		if(granule.getGranuleSpatialSet().size() > 0 && !ForceBB){
			
			//create the object to store the points
			
			Boundary b = new Boundary();
			
			//get the iterator
			Iterator<GranuleSpatial> it = granule.getGranuleSpatialSet().iterator();
			GranuleSpatial gs = it.next();
			
			
			for(Coordinate c :gs.getValue().getCoordinates()){
				Point p = new Point();
				p.setPointLatitude(new BigDecimal(c.y));
				p.setPointLongitude(new BigDecimal(c.x));
				boundingBox.getPointOrBoundingRectangleOrGPolygon().add(p);
				//				System.out.println("Coordinate: ["+c.x+","+c.y+" ]");
				//b.getPoint().add(p);
			}
			//hsd.getGeometry().getPointOrBoundingRectangleOrGPolygon().
			
			//boundingBox.getPointOrBoundingRectangleOrGPolygon().add(b);
			hsd.setGeometry(boundingBox);
			echoSpatial.setHorizontalSpatialDomain(hsd);
			
			return echoSpatial;
		}
		
		double north=0, south=0, east = 0, west = 0;
		
		Set<GranuleReal> grs = granule.getGranuleRealSet();
		
		for(GranuleReal gr : grs)
		{
			switch(gr.getDatasetElement().getElementDD().getElementId()){
				case 28: west = gr.getValue();
						break;
				case 15: north = gr.getValue();
					break;
				case 8: east = gr.getValue();
					break;
				case 23: south = gr.getValue();
					break;
			}
			
		}
//			
		boundingBox = genBoundingBox(rangeIs360,east,west,north,south);
		hsd.setGeometry(boundingBox);
		
		echoSpatial.setHorizontalSpatialDomain(hsd);
		hsd.setGeometry(boundingBox);
		
		echoSpatial.setHorizontalSpatialDomain(hsd);
		
		//ListOfVerticalSpatialDomains lvsd = new ListOfVerticalSpatialDomains();
		//VerticalSpatialDomain vsd = new VerticalSpatialDomain();
		//vsd.setType(null);
		//vsd.setValue(null);
		//lvsd.getVerticalSpatialDomain().add(vsd);
		//echoSpatial.setVerticalSpatialDomains(lvsd);
		
		
		//echoSpatial.setGranuleLocality(null);
		
		return echoSpatial;
	}
	
public static Geometry genBoundingBox(boolean rangeIs360, double east, double west, double north, double south ){
		
		Geometry boundingBox = new Geometry();
		BoundingRectangle br = new BoundingRectangle();
		
		BoundingRectangle br2;
		
		if(rangeIs360)
		{
			BigDecimal bdeast = convertBoundingVal(east,true);
			BigDecimal bdwest = convertBoundingVal(west,true);
			
			if(bdeast.compareTo(bdwest) == 1){
				br.setWestBoundingCoordinate(convertBoundingVal(west, true));
				br.setEastBoundingCoordinate(convertBoundingVal(east, true));
				//don't need to convert the lat ones, they are all in the -90/90 range
				br.setNorthBoundingCoordinate(new BigDecimal(north));
				br.setSouthBoundingCoordinate(new BigDecimal(south));
				
				boundingBox.getPointOrBoundingRectangleOrGPolygon().add(br);
			}
			else{
				log.debug("East Longitude is less than West Longitude: separating bounding boxes.");
				br2 = new BoundingRectangle();
			
				if(bdwest.compareTo(BigDecimal.valueOf(180)) != 0){
					br.setWestBoundingCoordinate(convertBoundingVal(west, true));
					br.setEastBoundingCoordinate(BigDecimal.valueOf(180));
					br.setNorthBoundingCoordinate(BigDecimal.valueOf(north));
					br.setSouthBoundingCoordinate(BigDecimal.valueOf(south));	
				
					boundingBox.getPointOrBoundingRectangleOrGPolygon().add(br);
				}
				
				br2.setWestBoundingCoordinate(BigDecimal.valueOf(-180));
				br2.setEastBoundingCoordinate(convertBoundingVal(east, true));
				br2.setNorthBoundingCoordinate(BigDecimal.valueOf(north));
				br2.setSouthBoundingCoordinate(BigDecimal.valueOf(south));		
				boundingBox.getPointOrBoundingRectangleOrGPolygon().add(br2);
				
			}
		}
		else{
			
			if(east >= west){
				br.setWestBoundingCoordinate(BigDecimal.valueOf(west));
				br.setEastBoundingCoordinate(BigDecimal.valueOf(east));
				//don't need to convert the lat ones, they are all in the -90/90 range
				br.setNorthBoundingCoordinate(new BigDecimal(north));
				br.setSouthBoundingCoordinate(new BigDecimal(south));
				
				boundingBox.getPointOrBoundingRectangleOrGPolygon().add(br);
			}
			else{
				log.debug("East Longitude is less than West Longitude: separating bounding boxes.");
				br2 = new BoundingRectangle();
				
				if(west != 180d){
				
				br.setWestBoundingCoordinate(BigDecimal.valueOf(west));
				br.setEastBoundingCoordinate(BigDecimal.valueOf(180));
				br.setNorthBoundingCoordinate(BigDecimal.valueOf(north));
				br.setSouthBoundingCoordinate(BigDecimal.valueOf(south));	
			
				boundingBox.getPointOrBoundingRectangleOrGPolygon().add(br);
				}
				
				if(east != -180d){
					
					br2.setWestBoundingCoordinate(BigDecimal.valueOf(-180));
					br2.setEastBoundingCoordinate(BigDecimal.valueOf(east));
					br2.setNorthBoundingCoordinate(BigDecimal.valueOf(north));
					br2.setSouthBoundingCoordinate(BigDecimal.valueOf(south));		
					boundingBox.getPointOrBoundingRectangleOrGPolygon().add(br2);
				}
			}
			
		}
		
		return boundingBox;
	}
	
	private ListOfOrbitCalculatedSpatialDomains exportOrbital(Granule granule) {
		ListOfOrbitCalculatedSpatialDomains locsd = new ListOfOrbitCalculatedSpatialDomains();
		
		OrbitCalculatedSpatialDomain ocsd = new OrbitCalculatedSpatialDomain();
		
		double ecl = 0;
				
		for(GranuleDateTime gdt : granule.getGranuleDateTimeSet())
		{
			if(gdt.getDatasetElement().getElementDD().getElementId() == 10)
			{
				ocsd.setEquatorCrossingDateTime(TimeConversion.convertDate(gdt.getValue()));
			}
		}
	
		for(GranuleReal gr : granule.getGranuleRealSet())
		{
			if(gr.getDatasetElement().getElementDD().getElementId() == 9)
			{
				ocsd.setEquatorCrossingLongitude(new BigDecimal(ecl));
			}
		}
		locsd.getOrbitCalculatedSpatialDomain().add(ocsd);
		
		return locsd;
	}
	
	// Populate the online access information.
	private ListOfOnlineAccessURLs exportAccessURL(Set<GranuleReference> referenceSet) {
		ListOfOnlineAccessURLs echoAccessURLs = new ListOfOnlineAccessURLs();
		HashSet<String> accessURLs = new HashSet<String>();
		for (GranuleReference reference : referenceSet) {
			
			String type = reference.getType();			
			if (!type.startsWith(LocationPolicyType.ARCHIVE.toString())) {
				
				if(accessURLs.add(reference.getPath())){
					log.info("Access URL \""+reference.getPath() +"\" will be added to granule export information");
					OnlineAccessURL echoAccessURL = new OnlineAccessURL();
					echoAccessURL.setURL(reference.getPath());
					if (type.endsWith("FTP"))
						echoAccessURL
						.setURLDescription("The FTP location for the granule.");
					else if (type.endsWith("OPENDAP"))
						echoAccessURL
						.setURLDescription("The OPENDAP location for the granule.");
					else
						echoAccessURL
						.setURLDescription("The base directory location for the granule.");
					echoAccessURLs.getOnlineAccessURL().add(echoAccessURL);
				}
				else
				{
					log.info("Reference/Access URL has already been added: " + reference.getPath());
					log.info("Access URL will not be added to granule export information");
				}
			}
		}
		return echoAccessURLs;
	}

	public static List<String> getEchoGranuleFilenames() {
		return echoGranuleFilenames;
	}

	public static void setEchoGranuleFilenames(List<String> echoGranuleFilenames) {
		ECHOGranuleFile.echoGranuleFilenames = echoGranuleFilenames;
	}

	public Integer getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(Integer collectionId) {
		this.collectionId = collectionId;
	}

	public ECHOCollectionFile getEchoCollectionFile() {
		return echoCollectionFile;
	}

	public void setEchoCollectionFile(ECHOCollectionFile echoCollectionFile) {
		this.echoCollectionFile = echoCollectionFile;
	}

	public void setSubmit(boolean toCMR) {
		this.toCMR = toCMR;
	}


	public void setLimit(Integer limit) {
		this.limit = limit;
		
	}

	public void setOutput(boolean outputXml) {
		output = outputXml;
		
	}

}
