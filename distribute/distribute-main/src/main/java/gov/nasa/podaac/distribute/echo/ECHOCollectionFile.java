//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.podaac.distribute.echo;

import gov.nasa.podaac.distribute.common.DistributeValidationEventHandler;
import gov.nasa.podaac.distribute.common.FTP;
import gov.nasa.podaac.distribute.common.Query;
import gov.nasa.podaac.distribute.common.QueryFactory;
import gov.nasa.podaac.distribute.common.TimeConversion;
import gov.nasa.podaac.distribute.common.URL;
import gov.nasa.podaac.distribute.echo.jaxb.collection.Boundary;
import gov.nasa.podaac.distribute.echo.jaxb.collection.BoundingRectangle;
import gov.nasa.podaac.distribute.echo.jaxb.collection.Collection;
import gov.nasa.podaac.distribute.echo.jaxb.collection.CollectionMetaDataFile;
import gov.nasa.podaac.distribute.echo.jaxb.collection.ContactPerson;
import gov.nasa.podaac.distribute.echo.jaxb.collection.CoordinateSystem;
import gov.nasa.podaac.distribute.echo.jaxb.collection.Geometry;
import gov.nasa.podaac.distribute.echo.jaxb.collection.GranuleSpatialRepresentation;
import gov.nasa.podaac.distribute.echo.jaxb.collection.HorizontalSpatialDomain;
import gov.nasa.podaac.distribute.echo.jaxb.collection.ScienceKeyword;
import gov.nasa.podaac.distribute.echo.jaxb.collection.Instrument;
import gov.nasa.podaac.distribute.echo.jaxb.collection.ListOfCollections;
import gov.nasa.podaac.distribute.echo.jaxb.collection.ListOfContactPersons;
import gov.nasa.podaac.distribute.echo.jaxb.collection.ListOfContacts;
import gov.nasa.podaac.distribute.echo.jaxb.collection.ListOfScienceKeywords;
import gov.nasa.podaac.distribute.echo.jaxb.collection.ListOfEmails;
import gov.nasa.podaac.distribute.echo.jaxb.collection.ListOfInstruments;
import gov.nasa.podaac.distribute.echo.jaxb.collection.ListOfKeywords;
import gov.nasa.podaac.distribute.echo.jaxb.collection.ListOfOnlineAccessURLs;
import gov.nasa.podaac.distribute.echo.jaxb.collection.ListOfOnlineResources;
import gov.nasa.podaac.distribute.echo.jaxb.collection.ListOfPhones;
import gov.nasa.podaac.distribute.echo.jaxb.collection.ListOfPlatforms;
import gov.nasa.podaac.distribute.echo.jaxb.collection.ListOfSensors;
import gov.nasa.podaac.distribute.echo.jaxb.collection.ObjectFactory;
import gov.nasa.podaac.distribute.echo.jaxb.collection.OnlineAccessURL;
import gov.nasa.podaac.distribute.echo.jaxb.collection.OnlineResource;
import gov.nasa.podaac.distribute.echo.jaxb.collection.Phone;
import gov.nasa.podaac.distribute.echo.jaxb.collection.Platform;
import gov.nasa.podaac.distribute.echo.jaxb.collection.RangeDateTime;
import gov.nasa.podaac.distribute.echo.jaxb.collection.Spatial;
import gov.nasa.podaac.distribute.echo.jaxb.collection.Temporal;
import gov.nasa.podaac.distribute.echo.jaxb.collection.VariableLevel1Keyword;
import gov.nasa.podaac.inventory.api.Constant.LocationPolicyType;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.DatasetContact;
import gov.nasa.podaac.inventory.model.DatasetCoverage;
import gov.nasa.podaac.inventory.model.DatasetLocationPolicy;
import gov.nasa.podaac.inventory.model.DatasetMetaHistory;
import gov.nasa.podaac.inventory.model.DatasetParameter;
import gov.nasa.podaac.inventory.model.DatasetRegion;
import gov.nasa.podaac.inventory.model.DatasetResource;
import gov.nasa.podaac.inventory.model.DatasetSource;
import gov.nasa.podaac.inventory.model.DatasetVersion;
import gov.nasa.podaac.inventory.model.Source;

import java.io.File;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
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

/**
 * This class contains ECHO colletion object.
 *
 * @author clwong
 *
 * @version
 * $Id: ECHOCollectionFile.java 14484 2015-11-25 00:18:25Z gangl $
 */
public class ECHOCollectionFile {
	
	private static Log log = LogFactory.getLog(ECHOCollectionFile.class);
	private String datasetId = null;
	private Dataset dataset = null;
	private String echoCollectionFilename = null;
	private Schema schema = null;
	private boolean rangeIs360 = false;
	private boolean toCMR = true;
	
	private Query q = QueryFactory.getInstance().createQuery();
	private gov.nasa.podaac.distribute.echo.jaxb.collection.Collection echoCollection = null;

	public ECHOCollectionFile() {
		init();
	}
	
	public ECHOCollectionFile(String datasetId) {
		this.datasetId = datasetId;
		this.dataset = q.fetchDataset(datasetId);
		init();
	}

	public ECHOCollectionFile(Dataset dataset) {
		this.datasetId = dataset.getPersistentId();
		this.dataset = dataset;
		init();
	}
	
	public void init() {
		SchemaFactory schemaFactory = SchemaFactory
				.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			this.schema = schemaFactory.newSchema(new File(System
					.getProperty("distribute.schemas")
					+ "/Collection.xsd"));
		} catch (SAXException e) {
		}
	}
	
	public static Geometry genBoundingBox(boolean rangeIs360, double east, double west, double north, double south ){
		
		Geometry boundingBox = new Geometry();
		BoundingRectangle br = new BoundingRectangle();
		
		BoundingRectangle br2;
		
		if(rangeIs360)
		{
			BigDecimal bdeast = convertBoundingVal(east,true);
			BigDecimal bdwest = convertBoundingVal(west,true);
			
			/*
			 * If special case for 0 and 360
			 */
			//System.out.println("EAST " + east);
			//System.out.println("WEST " + west);
			if(east == 360d && west==0d){
				//System.out.println("**** SPCIAL CASE ****");
				br.setWestBoundingCoordinate(new BigDecimal(-180));
				br.setEastBoundingCoordinate(new BigDecimal(180));
				//don't need to convert the lat ones, they are all in the -90/90 range
				br.setNorthBoundingCoordinate(new BigDecimal(north));
				br.setSouthBoundingCoordinate(new BigDecimal(south));
				
				boundingBox.getPointOrBoundingRectangleOrGPolygon().add(br);
				return boundingBox;
			}
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
	
	public void export() {
		if (dataset!=null) export(dataset);
		else if (datasetId!=null) export(datasetId);
		else log.error("Unspecified collection!");
	}

	public void export(String datasetId) {
		this.datasetId = datasetId;
		export(q.fetchDataset(datasetId));
	}

	public static String maxLengthString(String in, int max ){
		return in.substring(0,(Math.min(max, in.length())));
	}
	
	
	public void export(Dataset dataset) {
		
		log.info("Collection["+datasetId+"] has Collection.product.visibile_flag: " + dataset.getDatasetPolicy().getViewOnline());
		if(dataset.getDatasetPolicy().getViewOnline().equals('N'))
		{
			log.info("Collection["+datasetId+"] has Dataset.Coverage.visibile_flag set to 'N'. Skipping collection export.");
			return;
		}
		
		ListOfCollections echoCollections = new ListOfCollections();
		
		log.debug("is360: " + dataset.getDatasetCoverage().getGranuleRange360());
		if(dataset.getDatasetCoverage().getGranuleRange360().equals('Y')){
			rangeIs360 = true;
		}
			
		
		log.info("Process collectionId="+datasetId+" & datasetId="+dataset.getDatasetId());
		// Populate the collection information.
		echoCollection = 
			new gov.nasa.podaac.distribute.echo.jaxb.collection.Collection();
		echoCollection.setDataSetId(dataset.getPersistentId());
		echoCollection.setShortName(dataset.getShortName().toUpperCase());
		echoCollection.setLongName(dataset.getLongName());
		echoCollection.setArchiveCenter("PO.DAAC");
		String description = dataset.getDescription();
		if ((description!=null) && (!description.equalsIgnoreCase("TBD")))
				echoCollection.setDescription(dataset.getDescription());
		else log.warn("Dataset Description TBD!");
		
		echoCollection.setVisible(true);
		echoCollection.setRevisionDate(TimeConversion.currentDate());
		String processingCenter = dataset.getOriginalProvider();
		echoCollection.setProcessingCenter( maxLengthString(processingCenter, 80));
		echoCollection.setProcessingLevelId(dataset.getProcessingLevel());
		echoCollection.setDataFormat(dataset.getDatasetPolicy().getDataFormat());
		
		// Populate creation & revision dates
		Set<DatasetMetaHistory> historySet = dataset.getMetaHistorySet();
		List<Date> createDates = new ArrayList<Date>();
		List<Date> revisionDates = new ArrayList<Date>();
		for(DatasetMetaHistory history : historySet) {
			log.debug("History - Creation: " + history.getCreationDateLong());
			log.debug("History - LastRevDate: " + history.getLastRevisionDateLong());
			
			createDates.add(history.getCreationDate());
			revisionDates.add(history.getLastRevisionDate());
		}
		echoCollection.setInsertTime(TimeConversion.convertDate(Collections.min(createDates)));
		echoCollection.setLastUpdate(TimeConversion.convertDate(Collections.max(revisionDates)));
		
		// Populate the version information.
		try{
		echoCollection.setVersionId(
				exportVersion(dataset.getVersionSet())
				);
		}catch(Exception e)
		{
			log.info("skipping dataset " + dataset.getDatasetId());
		}
		echoCollection.setDataSetId(datasetId);

		// Populate Spatial & Temporal metadata
		echoCollection.setSpatialKeywords(
				exportSpatial(dataset)
				);
		echoCollection.setTemporal(
				exportTemporal(dataset.getDatasetCoverage())
				);
		echoCollection.setSpatial(
				exportSpatialVals(dataset)
		);
		
		// Populate the contact information.
		echoCollection.setContacts(
				exportContact(dataset.getContactSet())
				);		
		
		// Populate the parameter information.
		echoCollection.setScienceKeywords(
				exportParameter(dataset.getParameterSet())
				);
		
		// Populate the platform information.
		echoCollection.setPlatforms(
				exportPlatform(dataset.getSourceSet())
				);
		
		// Populate the resource information.
		echoCollection.setOnlineAccessURLs(
				exportAccessURL(dataset.getLocationPolicySet())
				);
		
		// Populate the resource information.			
		echoCollection.setOnlineResources(
				exportResource(dataset.getResourceSet())
				);
		if (!validate(echoCollection)){
			log.error("ERROR VALIDATING ECHO COLLECTION");
		}

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
	
	public Spatial exportSpatialVals(Dataset d) {
		//create HorizontalSpatialDomain
		HorizontalSpatialDomain hsd = new HorizontalSpatialDomain();
		
		//create geometry/BoundingBox
		Geometry geo = new Geometry();
		
		//Boundary b = new Boundary();
		//add ponts to the lsit for a spatial object

		double west = d.getDatasetCoverage().getWestLon();
		double east = d.getDatasetCoverage().getEastLon();
		double north= d.getDatasetCoverage().getNorthLat();
		double south=d.getDatasetCoverage().getSouthLat();
			
		geo = genBoundingBox(rangeIs360,east,west,north,south);
		geo.setCoordinateSystem(CoordinateSystem.CARTESIAN);
		hsd.setGeometry(geo);
		hsd.setZoneIdentifier(null);		
		
		//create spatial
		Spatial sp = new Spatial();
		
		//add these items to the SPATIAL type
		sp.setSpatialCoverageType("HORIZONTAL");
		sp.setGranuleSpatialRepresentation(GranuleSpatialRepresentation.CARTESIAN);
		sp.setHorizontalSpatialDomain(hsd);
		
		return sp;
	}

	public boolean validate (gov.nasa.podaac.distribute.echo.jaxb.collection.Collection echoCollection) {
		
		
		try {
			JAXBContext jc = JAXBContext
				.newInstance("gov.nasa.podaac.distribute.echo.jaxb.collection");
			JAXBElement<gov.nasa.podaac.distribute.echo.jaxb.collection.Collection> element = (new ObjectFactory())
				.createCollection(echoCollection);
			Marshaller m = jc.createMarshaller();
			if (this.schema==null) return false;
			log.debug("Marshalling schema.");
			m.setSchema(this.schema);
			m.setEventHandler(new DistributeValidationEventHandler());
			DOMResult result = new DOMResult();
			m.marshal( element, result );
			return true;
		} catch (JAXBException e) {
			e.printStackTrace();
			log.error("Invalid Collection Object:\n"+e.getMessage());
		}
		
		return false;
	}
	
	public boolean validate() {
		return validate(echoCollectionFilename);
	}
	
	public boolean validate(String filename) {
		if (validateXML(filename)) {
			if (verifyURLs()) return true;
		}
		return false;
	}
	
	public boolean verifyURLs() {
		boolean status = true;

	    	List<OnlineAccessURL> urls = echoCollection.getOnlineAccessURLs().getOnlineAccessURL();
	    	for (OnlineAccessURL url : urls) {
	    		if (!URL.verify(url.getURL())) status = false;
	    	}
		return status;
	}
	
	public boolean validateXML(String filename) {
		JAXBContext context;
		try {
			context = JAXBContext
				.newInstance("gov.nasa.podaac.distribute.echo.jaxb.collection");
			Unmarshaller unmarshaller = context.createUnmarshaller();
			if (this.schema==null) return false;
		    unmarshaller.setSchema(this.schema);
		    unmarshaller.setEventHandler(new DistributeValidationEventHandler());
		    echoCollection =  (gov.nasa.podaac.distribute.echo.jaxb.collection.Collection) 
					((JAXBElement)unmarshaller.unmarshal(new File(filename))).getValue();
			echoCollectionFilename = filename;
		    return true;
		} catch (Exception e) {
			log.error("Non-ECHO-Collection File:\n"+e.getMessage());
			return false;
		}
	}
	
	/**
	 *  Create the XML representation of the ECHO Collection submission.
	 */
	public boolean toXmlFile() {
		try{
		if (echoCollection == null) return false;
		}catch(NullPointerException npe)
		{
			log.info("No collections to output. Not outputting to XML.");
			return false;
		}
		
		String filename = System.getProperty("distribute.data.location")
		+"/podaac-echo-collection-" + datasetId + ".xml";
		File outputFile = new File(filename);
		try {
			JAXBContext jc = JAXBContext
					.newInstance("gov.nasa.podaac.distribute.echo.jaxb.collection");
			JAXBElement<gov.nasa.podaac.distribute.echo.jaxb.collection.Collection> coElement = (new ObjectFactory()).createCollection(echoCollection);
					//.createCollectionMetaDataFile(echoCollection);
			Marshaller m = jc.createMarshaller();
			// validation
			if (this.schema==null) return false;
			m.setSchema(this.schema);
			m.setEventHandler(new DistributeValidationEventHandler());
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(coElement, outputFile);	
			
			echoCollectionFilename = filename;
			return true;
		} catch (JAXBException je) {
			log.error("toXMLFile:"+je.getMessage());
		} catch (Exception e) {
			log.error("toXMLFile:"+e.getMessage());
		}
		if (outputFile.exists()) outputFile.delete();
		return false;
	}
	
	public String toXML(){
		try{
			JAXBContext jc = JAXBContext
					.newInstance("gov.nasa.podaac.distribute.echo.jaxb.collection");
			JAXBElement<gov.nasa.podaac.distribute.echo.jaxb.collection.Collection> coElement = (new ObjectFactory()).createCollection(echoCollection);
					//.createCollectionMetaDataFile(echoCollection);
			Marshaller m = jc.createMarshaller();
			// validation
			if (this.schema==null) return null;
			m.setSchema(this.schema);
			m.setEventHandler(new DistributeValidationEventHandler());
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter sw = new StringWriter();
			m.marshal(coElement, sw);
			return sw.toString();
		} catch (JAXBException je) {
			log.error("toXMLFile:"+je.getMessage());
		} catch (Exception e) {
			log.error("toXMLFile:"+e.getMessage());
		}
		return null;
		
	}
	

	private String exportVersion(Set<DatasetVersion> versionSet) throws Exception {
		List<Integer> versionIds = new ArrayList<Integer>();
		for (DatasetVersion version : versionSet) 
			versionIds.add(version.getDatasetVersionPK().getVersionId());
		
		if(versionIds.isEmpty())
		{
			log.info("No version records associated with this dataset.");
			log.warn("No version record, default value of \"1\" used,");
			//throw new Exception("No version associated");
			return "1";
		}
		
		return String.valueOf(Collections.max(versionIds));
	}
	
	// Populate the spatial metadata
	private ListOfKeywords exportSpatial(Dataset dataset) {
		ListOfKeywords echoSpatialKeywords = new ListOfKeywords();
		for(DatasetRegion dr : dataset.getRegionSet()){
			echoSpatialKeywords.getKeyword().add(dr.getRegion());	
		}
		return echoSpatialKeywords;
	}
	
	// Populate the temporal metadata
	private Temporal exportTemporal(DatasetCoverage coverage) {		
		Temporal echoTemporal = new Temporal();
		RangeDateTime echoRange = new RangeDateTime();
		echoRange.setBeginningDateTime(TimeConversion.convertDate(coverage.getStartTime()));
		echoRange.setEndingDateTime(TimeConversion.convertDate(coverage.getStopTime()));
		echoTemporal.getRangeDateTime().add(echoRange);
		return echoTemporal;
	}
	
	// Populate the contact information.
	private ListOfContacts exportContact(Set<DatasetContact> DatasetContactSet) {		
		
		ListOfContacts echoContacts = new ListOfContacts();
		for (DatasetContact collectionContact : DatasetContactSet) {			
			gov.nasa.podaac.inventory.model.Contact contact = 
				collectionContact.getDatasetContactPK().getContact();
			String providerLongname = null;
			String[] depends = {"provider"};
			contact = q.fetchContact(contact.getContactId(), depends);
			//contact = q.fetchContact(contact.getContactId());
			providerLongname = contact.getProvider().getLongName();
			
			gov.nasa.podaac.distribute.echo.jaxb.collection.Contact echoContact = 
				new gov.nasa.podaac.distribute.echo.jaxb.collection.Contact();
			echoContact.setRole("Archiver");
			echoContact.setOrganizationName(providerLongname);
			
			ListOfPhones echoPhones = new ListOfPhones();
			Phone echoPhone = new Phone();
			echoPhone.setNumber(contact.getPhone());
			echoPhone.setType("Primary");
			echoPhones.getPhone().add(echoPhone);				
			echoContact.setOrganizationPhones(echoPhones);
			
			ListOfEmails echoEmail = new ListOfEmails();
			echoEmail.getEmail().add(contact.getEmail());
			echoContact.setOrganizationEmails(echoEmail);
			
			ListOfContactPersons echoPersons = new ListOfContactPersons();
			ContactPerson echoPerson = new ContactPerson();
			echoPerson.setFirstName(contact.getFirstName());
			if(contact.getMiddleName() != null)
				echoPerson.setMiddleName(contact.getMiddleName());
			echoPerson.setLastName(contact.getLastName());
			echoPerson.setJobPosition(contact.getRole());
			echoPersons.getContactPerson().add(echoPerson);
			echoContact.setContactPersons(echoPersons);
			echoContacts.getContact().add(echoContact);
		}
		return echoContacts;
	}
	
	// Populate the parameter information.
	private ListOfScienceKeywords exportParameter(Set<DatasetParameter> parameterSet) {
		ListOfScienceKeywords echoParameters = new ListOfScienceKeywords();
		for (DatasetParameter parameter : parameterSet) {
			ScienceKeyword echoParameter = new ScienceKeyword();
			echoParameter.setCategoryKeyword(parameter.getCategory());
			echoParameter.setTopicKeyword(parameter.getTopic());
			echoParameter.setTermKeyword(parameter.getTerm());
			
			
			VariableLevel1Keyword var = new VariableLevel1Keyword();
			var.setValue(parameter.getVariable());
			
			echoParameter.setVariableLevel1Keyword(var);			
			echoParameter.setDetailedVariableKeyword(parameter.getVariableDetail());
			
			//echoParameter.setVariableLevel1Keyword(value);
			
			echoParameters.getScienceKeyword().add(echoParameter);
			
			//echoParameters.getDisciplineTopicParameter().add(echoParameter);
		}
		return echoParameters;
	}
	
	// Populate the platform information.
	private ListOfPlatforms exportPlatform(Set<DatasetSource> datasetSourceSet) {	
		ListOfPlatforms echoPlatforms = new ListOfPlatforms();
		Set<String> addedSources = new HashSet<String>(); //to store the shortnames of added sources
		
		for (DatasetSource datasetSource : datasetSourceSet) {
			Source source = datasetSource.getDatasetSourcePK().getSource();
			boolean update = false;
			
			gov.nasa.podaac.inventory.model.Sensor sensor = 
				datasetSource.getDatasetSourcePK().getSensor();
			Platform echoPlatform = null;
			
			if(addedSources.contains(source.getSourceShortName()))
			{
				log.debug("Source ["+source.getSourceShortName()+"] already added, finding and updating.");
				for(Platform p: echoPlatforms.getPlatform()){
					if(p.getShortName().equals(source.getSourceShortName()))
					{
						log.debug("Platform ["+source.getSourceShortName()+"] found, setting to current.");
						echoPlatform = p;
						update = true;
						break;
					}
				}
			}
			else{
				log.debug("Source ["+source.getSourceShortName()+"] is a new source.");
				echoPlatform = new Platform();
			}
			
			echoPlatform.setShortName(source.getSourceShortName());
			String longName = source.getSourceLongName();
			if (longName==null){
				log.warn("Instrument(source) long_name is null. Using source short_name instead. See source.long_name with id=" + source.getSourceId());
				echoPlatform.setLongName(source.getSourceShortName());
			}
			else 
				echoPlatform.setLongName(source.getSourceLongName());
			if(source.getSourceType()==null)
			{
				log.warn("Instrument(source) type is null. Using sensor short_name instead. See source.type with id=" + sensor.getSensorId());
				echoPlatform.setType("unknown");
			}
			else
				echoPlatform.setType(source.getSourceType());
			
			ListOfInstruments echoInstruments = new ListOfInstruments();
			if(echoPlatform.getInstruments()!=null)
				echoInstruments.getInstrument().addAll(echoPlatform.getInstruments().getInstrument());
			// only one instrument
			Instrument echoInstrument = new Instrument();
			echoInstrument.setShortName(sensor.getSensorShortName());
			
			if(sensor.getSensorLongName()==null)
			{
				log.warn("Instrument(sensor) long_name is null. Using sensor short_name instead. See SENSOR.long_name with id=" + sensor.getSensorId());
				echoInstrument.setLongName(sensor.getSensorShortName());
			}
			else
			echoInstrument.setLongName(sensor.getSensorLongName());			

			ListOfSensors echoSensors = new ListOfSensors();
			// only one sensor
			gov.nasa.podaac.distribute.echo.jaxb.collection.Sensor echoSensor = 
				new gov.nasa.podaac.distribute.echo.jaxb.collection.Sensor();
			echoSensor.setShortName(sensor.getSensorShortName());
			echoSensor.setLongName(sensor.getSensorLongName());
			echoSensors.getSensor().add(echoSensor);
			
			echoInstrument.setSensors(echoSensors);
			
			echoInstruments.getInstrument().add(echoInstrument);
			echoPlatform.setInstruments(echoInstruments);
			addedSources.add(source.getSourceShortName());
			if(!update)
				echoPlatforms.getPlatform().add(echoPlatform);
		}
		return echoPlatforms;
	}
	
	// Populate the online access information.
	private ListOfOnlineAccessURLs exportAccessURL(Set<DatasetLocationPolicy> locationSet) {
		ListOfOnlineAccessURLs echoAccessURLs = new ListOfOnlineAccessURLs();
		HashSet<String> accessURLs = new HashSet<String>();
		for (DatasetLocationPolicy location : locationSet) {
			String type = location.getType();
			if (!type.startsWith(LocationPolicyType.ARCHIVE.toString())) {
				
				if(accessURLs.add(location.getBasePath())){
					
					OnlineAccessURL echoAccessURL = new OnlineAccessURL();
					echoAccessURL.setURL(location.getBasePath());
					
					if (type.endsWith("FTP"))
						echoAccessURL
						.setURLDescription("The FTP base directory location for the collection.");
					else if (type.endsWith("OPENDAP")){
						echoAccessURL
						.setURLDescription("The OPeNDAP base directory location for the collection.");
						if(!location.getBasePath().endsWith("/")){
							echoAccessURL.setURL(location.getBasePath()+"/");
						}
					}
					else
						echoAccessURL
						.setURLDescription("The base directory location for the collection.");
					echoAccessURLs.getOnlineAccessURL().add(echoAccessURL);
				}
				else{
					log.info("Location Base path \""+location.getBasePath()+"\" is a non-unique entry. Not adding to Collection xml file.");
				}
			}
		}
		return echoAccessURLs;
	}
	
	// Populate the resource information.			
	private ListOfOnlineResources exportResource(Set<DatasetResource> datasetResourceSet) {
		ListOfOnlineResources echoResources = new ListOfOnlineResources();
		log.debug("dataset resource size="+datasetResourceSet.size());
		for (DatasetResource resource : datasetResourceSet) {
			log.debug("resPath"+resource.getResourcePath());
			log.debug("resDesc"+resource.getResourceDescription());
			log.debug("resType"+resource.getResourceType());
			
			if(resource.getResourceType().toUpperCase().equals("THUMBNAIL")){
				log.debug("Not adding 'thumbnail' resource to list of resources.");
				continue;
			}
			OnlineResource echoResource = new OnlineResource();
			echoResource.setURL(resource.getResourcePath());
			echoResource.setDescription(resource.getResourceDescription());
			echoResource.setType(resource.getResourceType());
			echoResources.getOnlineResource().add(echoResource);
		}
		return echoResources;
	}
	
	public Dataset getDataset() {
		return dataset;
	}

	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}

	public String getEchoCollectionFilename() {
		return echoCollectionFilename;
	}

	public void setEchoCollectionFilename(String echoCollectionFilename) {
		this.echoCollectionFilename = echoCollectionFilename;
	}

	public Schema getSchema() {
		return schema;
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
	}

	public void setSubmit(boolean toCMR) {
		this.toCMR = toCMR;
	}

	public Collection getCollection() {
		return this.echoCollection;
	}
}
