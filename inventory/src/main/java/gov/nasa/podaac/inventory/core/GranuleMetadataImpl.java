// Copyright 2007, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
 
package gov.nasa.podaac.inventory.core;

import gov.nasa.podaac.common.api.serviceprofile.BasicFileInfo;
import gov.nasa.podaac.common.api.serviceprofile.BoundingPolygon;
import gov.nasa.podaac.common.api.serviceprofile.BoundingRectangle;
import gov.nasa.podaac.common.api.serviceprofile.Ellipse;
import gov.nasa.podaac.common.api.serviceprofile.GranuleFile;
import gov.nasa.podaac.common.api.serviceprofile.GranuleHistory;
import gov.nasa.podaac.common.api.serviceprofile.Point;
import gov.nasa.podaac.common.api.serviceprofile.Swath;
import gov.nasa.podaac.inventory.api.Constant;
import gov.nasa.podaac.inventory.api.DataManager;
import gov.nasa.podaac.inventory.api.DataManagerFactory;
import gov.nasa.podaac.inventory.api.GranuleMetadata;
import gov.nasa.podaac.inventory.api.Query;
import gov.nasa.podaac.inventory.api.QueryFactory;
import gov.nasa.podaac.inventory.api.Constant.AccessType;
import gov.nasa.podaac.inventory.api.Constant.AppendBasePathType;
import gov.nasa.podaac.inventory.api.Constant.GranuleArchiveStatus;
import gov.nasa.podaac.inventory.api.Constant.GranuleStatus;
import gov.nasa.podaac.inventory.api.Constant.LocationPolicyType;
import gov.nasa.podaac.inventory.exceptions.InventoryException;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.DatasetElement;
import gov.nasa.podaac.inventory.model.DatasetLocationPolicy;
import gov.nasa.podaac.inventory.model.DatasetPolicy;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.GranuleArchive;
import gov.nasa.podaac.inventory.model.GranuleCharacter;
import gov.nasa.podaac.inventory.model.GranuleDateTime;
import gov.nasa.podaac.inventory.model.GranuleElement;
import gov.nasa.podaac.inventory.model.ElementDD;
import gov.nasa.podaac.inventory.model.GranuleInteger;
import gov.nasa.podaac.inventory.model.GranuleMetaHistory;
import gov.nasa.podaac.inventory.model.GranuleReal;
import gov.nasa.podaac.inventory.model.GranuleReference;
import gov.nasa.podaac.inventory.model.GranuleSpatial;
import gov.nasa.podaac.inventory.model.GranuleMetaHistory.GranuleMetaHistoryPK;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;

/**
 * This class contains implementation of mapping between SIP metadata to database.
 * @author clwong
 * @version Jul 24, 2007
 * $Id: GranuleMetadataImpl.java 13176 2014-04-03 18:56:47Z gangl $
 */
public class GranuleMetadataImpl implements GranuleMetadata {

	private static Log log = LogFactory.getLog(GranuleMetadataImpl.class);
	private Query q = QueryFactory.getInstance().createQuery();
	private DataManager manager = DataManagerFactory.getInstance().createDataManager();

//	private String dsVersioned = "N";
//	private String dsVersionPolicy = null;
	private Granule granule = new Granule();
	private gov.nasa.podaac.common.api.serviceprofile.Granule metadata;
	private String archiveBasePath;
	private String archiveSubDir;
	private List<ElementDD> ledds = null;
	
	public GranuleMetadataImpl() {
		this.granule.setStatus(GranuleStatus.OFFLINE);			
	}

	public Granule getGranule() {
		return this.granule;
	}

	public void setGranule(Granule granule) {
		this.granule = granule;
	}

	public gov.nasa.podaac.common.api.serviceprofile.Granule getMetadata() {
		return metadata;
	}

	public void setMetadata(gov.nasa.podaac.common.api.serviceprofile.Granule metadata) {
		this.metadata = metadata;
	}

	public String getArchiveBasePath() {
		return archiveBasePath;
	}

	public void setArchiveBasePath(String archiveBasePath) {
		this.archiveBasePath = archiveBasePath;
	}

	public String getArchiveSubDir() {
		return archiveSubDir;
	}

	public void setArchiveSubDir(String archiveSubDir) {
		this.archiveSubDir = archiveSubDir;
	}

	public void mapToGranule () throws InventoryException {
		// find the belonging dataset & its policy
		String datasetName = null;
		Dataset ds = null;
		try {
			datasetName = metadata.getDatasetName(); 
		} catch (NullPointerException npe) {
			throw new InventoryException("DatasetName NPE!");
		}
		if (datasetName==null)
			throw new InventoryException("DatasetName is null!");
		else {
	
			Dataset dsInstance = new Dataset();
			dsInstance.setShortName(datasetName);
			//List<Dataset> dsList = q.fetchDatasetList(dsInstance, "datasetPolicy", "locationPolicySet", "datasetElementSet");
			List<Dataset> dsList = q.fetchDatasetList(dsInstance, "datasetPolicy", "locationPolicySet");
			
			if (dsList == null)	
					throw new InventoryException("DatasetName "+datasetName+" not found!");
			else {	
				 ds = null;
				try{
					 ds = dsList.get(0);
				}catch(IndexOutOfBoundsException e)
				{
					throw new InventoryException("Dataset["+datasetName+"] does not exist.");
				}
				//System.out.println("Setting dataset to Granule.");
				this.granule.setDataset(ds);
				DatasetPolicy policy = ds.getDatasetPolicy();

				if (policy == null) {
					throw new InventoryException("Cannot find dataset policy!");
				}
				
//				dsVersioned = policy.getVersioned().toString();
//				dsVersionPolicy = policy.getVersionPolicy();
//				
//				log.debug("Dataset versioned: " + dsVersioned);
//				log.debug("Dataset versionPolicy: " + dsVersionPolicy);
				
				// set granule metadata inherited from its dataset
				try {	
					if (policy.getAccessType()==null) 
						throw new InventoryException("AccessType is null!");
					else 
						this.granule.setAccessType(policy.getAccessType());
				} catch (NullPointerException npe) {
					throw new InventoryException("AccessType NPE!");
				}
				//Checksum code
				boolean checksumFound = false;
				try {
					Set<GranuleFile> gf = metadata.getFiles();
					for(GranuleFile granuleFile: gf)
					{
						for(BasicFileInfo b :granuleFile.getSources())
						{
							if (b.getChecksumAlgorithm()==null) 
							{}
							else 
							{
								this.granule.setChecksumType(b.getChecksumAlgorithm().toString());
								checksumFound = true;
								break;
							}
						}
						if(checksumFound)
							break;
					}
					if(!checksumFound)
					{
						if (policy.getChecksumType()==null) 
							throw new InventoryException("Checksum is null!");
						else 
							this.granule.setChecksumType(policy.getChecksumType().toString());	
					}
					//throw new InventoryException("ChecksumType NPE!");
				}catch (NullPointerException npe) {
					throw new InventoryException("ChecksumType NPE!");
				}
				
				boolean compressFound = false;
				try {
					Set<GranuleFile> gf = metadata.getFiles();
					for(GranuleFile granuleFile: gf)
					{
						for(BasicFileInfo b :granuleFile.getSources())
						{
							if (b.getCompressionAlgorithm()==null) 
							{}
							else 
							{
								this.granule.setCompressType(b.getCompressionAlgorithm().toString());
								compressFound = true;
								break;
							}
						}
						if(compressFound)
							break;
					}
					if(!compressFound)
					{
						if (policy.getCompressType()==null) 
							throw new InventoryException("CompressType is null!");
						else 
							this.granule.setCompressType(policy.getCompressType());
						
					}
					/*
					if (policy.getCompressType()==null) 
						throw new InventoryException("CompressType is null!");
					else 
						this.granule.setCompressType(policy.getCompressType());*/
				} catch (NullPointerException npe) {
					throw new InventoryException("CompressType NPE!");
				}
				try {	
					if (policy.getDataFormat()==null) 
						throw new InventoryException("DataFormat is null!");
					else 
						this.granule.setDataFormat(policy.getDataFormat());
				} catch (NullPointerException npe) {
					throw new InventoryException("DataFormat NPE!");
				}
				// set granule references based on dataset location policy
				Set<DatasetLocationPolicy> locationPolicySet = 
									ds.getLocationPolicySet();
				for (DatasetLocationPolicy locationPolicy : locationPolicySet)	{
					if (locationPolicy.getType().toUpperCase().trim().startsWith(LocationPolicyType.ARCHIVE.toString()))
						archiveBasePath = locationPolicy.getBasePath();
				}
				log.debug("granuleReference.locationPath="+this.archiveBasePath);				
				if (this.archiveBasePath == "")
					throw new InventoryException("Cannot find local base path for Archive!");
				
				try {
					if (policy.getBasePathAppendType()==null) 
						throw new InventoryException("BasePathAppendType is null!");
				} catch (NullPointerException npe) {
					throw new InventoryException("BasePathAppendType NPE!");
				}
				//System.out.println("Setting dataset");
				this.granule.setDataset(ds);
			}
		}

			
		
		try {	
			if (metadata.getName()==null) 
				throw new InventoryException("GranuleName is null!");
			else {
				this.granule.setName(metadata.getName());
			}
		} catch (NullPointerException npe) {
			throw new InventoryException("GranuleName NPE!");
		}
//		if(dsVersioned.equals("Y"))
//			granule.setOfficialName(metadata.getOfficialName());
	
		// Determine replacement
		if (replace()) { 
			log.info("Process replacement for name="+metadata.getReplace()
						+" id="+this.granule.getGranuleId()+" version="+this.granule.getVersion());
		} else {
			// Check for duplication
			if (q.findGranule(this.granule.getName(), this.granule.getDataset()) != null)
				throw new InventoryException("Granule with name="+this.granule.getName()+" already exists!");
			this.granule.setVersion(1);		
		}

		// translate to Granule main table
		try {	
			if (metadata.getCreateTime()==null) 
				throw new InventoryException("CreateTime is null!");
			else {
				this.granule.setCreateTime(metadata.getCreateTime());
			}
		} catch (NullPointerException npe) {
			throw new InventoryException("CreateTime NPE!");
		}
		/*
		try {	
			if (metadata.getTemporalCoverageStartTime()==null) 
				throw new InventoryException("TemporalCoverageStartTime is null!");
			else {
				Date startTime = metadata.getTemporalCoverageStartTime();
			}
		} catch (NullPointerException npe) {
			throw new InventoryException("TemporalCoverageStartTime NPE!");
		}
		try {	
			if (metadata.getTemporalCoverageStopTime()==null) 
				throw new InventoryException("TemporalCoverageStopTime is null!");
			else 
				this.granule.setStopTime(metadata.getTemporalCoverageStopTime());
		} catch (NullPointerException npe) {
			throw new InventoryException("TemporalCoverageStopTime NPE!");
		}
		 */

		// translate to Granule optional tables
		try {
			if (metadata.getTemporalCoverageStartTime()!=null)
				this.granule.setStartTime(metadata.getTemporalCoverageStartTime());
		} catch (NullPointerException npe) {}
		try {
			if (metadata.getTemporalCoverageStopTime()!=null)
				this.granule.setStopTime(metadata.getTemporalCoverageStopTime());
		} catch (NullPointerException npe) {}
		/*
		Set<GranuleElement> elements = q.fetchDatasetById(
				this.granule.getDataset().getDatasetId(), "granuleElementSet").getGranuleElementSet();
		for (GranuleElement element : elements) mapOptionalAttr(element, metadata);
		*/
		boolean elementsFound = false;
		//now we need to get the element set for this part of the ingestion process.
		Dataset dsInstance = new Dataset();
		dsInstance.setShortName(datasetName);

		List<Dataset> dsList = q.fetchDatasetList(dsInstance, "datasetPolicy", "locationPolicySet", "datasetElementSet");
		if (dsList == null)	{
			//System.out.println("No dataset elements");
			log.warn("Dataset doesn't have elements associated with it, skipping optional attribute addtion.");
		}
		else {	
			 ds = null;
			try{
				 ds = dsList.get(0);
				 this.granule.setDataset(ds);
				 for(DatasetElement dee : ds.getDatasetElementSet()){
//					 System.out.println(dee.getElementDD().getShortName());
				 }
				 elementsFound=true;
			}catch(IndexOutOfBoundsException e)
			{
				log.warn("Dataset doesn't have elements associated with it, skipping optional attribute addtion.");
			}
			
		}
		
		if(elementsFound){
			try {
				mapOptionalAttr("version", metadata.getVersion());
			} catch (NullPointerException npe) {}
			try {
				mapOptionalAttr("ancillaryName", metadata.getAncillaryName());
			} catch (NullPointerException npe) {}
			try {
				mapOptionalAttr("revolution", metadata.getRevolution());
			} catch (NullPointerException npe) {}
			try {
				mapOptionalAttr("cycle", metadata.getCycle());
			} catch (NullPointerException npe) {}
			try {
				mapOptionalAttr("pass", metadata.getPass());
			} catch (NullPointerException npe) {}
			try {
				mapOptionalAttr("passType", metadata.getPassType());
			} catch (NullPointerException npe) {}	
			try {
				mapOptionalAttr("dayNightType", metadata.getDayNightMode());
			} catch (NullPointerException npe) {}	
			try {
				mapOptionalAttr("dayOfYearStart", metadata.getDayOfYearStart());
			} catch (NullPointerException npe) {}
			
			// translate latitudeAndLongitude
			try {
				List<Point> points = metadata.getPoints();
				for (Point point : points) {
				  mapOptionalAttr("pointLatitude", point.getLatitude());
				  mapOptionalAttr("pointLongitude", point.getLongitude());
				}
			} catch (NullPointerException ne) {
				log.debug("SpatialCoveragePoint either not found or incomplete.");
			} catch (Exception e) {};
			
			// translate boundingRectangle
			try {
				BoundingRectangle rectangle = metadata.getBoundingRectangle();
				mapOptionalAttr("westLongitude", rectangle.getWestLongitude());
				mapOptionalAttr("northLatitude", rectangle.getNorthLatitude());
				mapOptionalAttr("southLatitude", rectangle.getSouthLatitude());
				mapOptionalAttr("eastLongitude", rectangle.getEastLongitude());
			} catch (NullPointerException ne) {
				//ne.printStackTrace();
				log.debug("SpatialCoverageBoundingRectangle either not found or incomplete.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// translate boundingPolygon
		if(elementsFound)
		{
			for(DatasetElement de: ds.getDatasetElementSet()){
				if(de.getElementDD().getType().toUpperCase().equals("SPATIAL") && (de.getScope().toUpperCase().equals("BOTH") || de.getScope().toUpperCase().equals("GRANULE"))){
					try {
						List<BoundingPolygon> polygons = metadata.getPolygons();
						for (BoundingPolygon polygon : polygons) {
						  polygon.getRegionName();
						  polygon.getPointOrder();
//						  System.out.println("Size of point list: " + polygon.getPoints().size());
						  StringBuilder sb =  new StringBuilder();
						  for( Point p : polygon.getPoints()){
							  String s = "" + p.getLongitude() + " " + p.getLatitude() +", ";
							  sb.append(s);
						  }
						  if(sb.toString() == null || sb.toString().equals("")){
						    	continue;
						    }
						  String polyString = sb.toString().substring(0,sb.toString().length()-2);
							WKTReader rdr = new WKTReader();
						    Geometry geometry = null;
						    
						    try {
								geometry = rdr.read("POLYGON (("+polyString+"))");
								geometry.setSRID(8307);
								GranuleSpatial gs = new GranuleSpatial(de, (Polygon)geometry);
	//							System.out.println("DE: "+gs.toString());
								this.granule.add(gs);
							} catch (com.vividsolutions.jts.io.ParseException e) {
	//							e.printStackTrace();
								log.debug("Cannot parse Polygon from SIP.");
							}
						}
					} catch (NullPointerException ne) {
						log.debug("SpatialCoverageBoundingPolygon either not found or incomplete.");		
					} catch (Exception e) {};
				}
			  }
			}
		if(elementsFound){
		// translate swath
			try {
				Swath swath = metadata.getSwath();
//				System.out.println("Searching for equator crossing...");
				mapOptionalAttr("equatorCrossingLongitude", swath.getEquatorCrossingLongitude());
				mapOptionalAttr("equatorCrossingTime", swath.getEquatorCrossingTime());
				mapOptionalAttr("inclinationAngle", swath.getInclinationAngle());
				mapOptionalAttr("swathWidth", swath.getSwathWidth());
			} catch (NullPointerException ne) {
				log.debug("SpatialCoverageSwath either not found or incomplete.");
			} catch (Exception e) {};
			try {
				mapOptionalAttr("comment", metadata.getComment());
			} catch (NullPointerException npe) {}
		}
		// translate ellipse
		try {
			Ellipse ellipse = metadata.getEllipse();
			ellipse.getCenterPoint();
			ellipse.getUnit();
			ellipse.getValue();
			// TODO: translate ellipse
		} catch (NullPointerException ne) {
			log.debug("SpatialCoverageEllipse either not found or incomplete.");
		} catch (Exception e) {};

		//process extras
		Properties props =  metadata.getExtras();
		for (Object e :props.keySet()){
			String str = (String) e;
			mapOptionalAttr(str,props.getProperty(str));
		}
		
		this.archiveSubDir = appendArchiveSubDir(this.granule.getDataset().getDatasetPolicy().getBasePathAppendType(), this.granule.getStartTime(), metadata.getCycle());
		Set<GranuleReference> refSet = this.granule.getGranuleReferenceSet();
		for (GranuleReference ref : refSet) {
			ref.setPath(ref.getPath()+appendArchiveSubDir(this.granule.getDataset().getDatasetPolicy().getBasePathAppendType(), this.granule.getStartTime(), metadata.getCycle()));
		}
	}

	private void mapOptionalAttr (String keyName, Object keyValue) {			
		//System.out.println("Processing KeyName: " + keyName);
		if (keyValue==null) {
			//System.out.println("Value is null...");
			return;
		}
		if(keyName.equals("westLongitude")){
			keyName = "westernmostLongitude";
		}
		if(keyName.equals("eastLongitude")){
			keyName = "easternmostLongitude";
		}
		if(keyName.equals("southLatitude")){
			keyName = "southernmostLatitude";
		}
		if(keyName.equals("northLatitude")){
			keyName = "northernmostLatitude";
		}
		if(ledds == null){
			
			String[] x = { };
			ElementDD element = new ElementDD();
			ledds = q.fetchElementDDList(element,x);
		}
		ElementDD element = null;
		for(ElementDD ed : ledds){
			if(ed.getShortName().equals(keyName)){
				//System.out.println("Setting key");
				element = ed;
			}
		}
		try{
			element.getElementId();
		}
		catch(NullPointerException npe)
		{
			log.debug("could not find elementDD named: " + keyName);
			return;
		}
		DatasetElement de = null;
		for(DatasetElement de_i: this.granule.getDataset().getDatasetElementSet()){
			if(de_i.getElementDD().getElementId().equals(element.getElementId()))
			{
				de = de_i;
			}
		}
		if(de == null)
			{
				//System.out.println("Dataset element '" + keyName+"' not found in datasetElement set for this granule's dataset.");
				log.debug("Dataset element '" + keyName+"' not found in datasetElement set for this granule's dataset.");
				return;
			}
		// datatype is dictated by dictionary		
		String type = element.getType();
		//System.out.println("translateOptMetadata: "+keyName+"="+keyValue+" "+type);
		log.info("translateOptMetadata: "+keyName+"="+keyValue+" "+type);
		if (type.equals("character")) 
			this.granule.add(new GranuleCharacter(de, keyValue.toString()));
		else if (type.equals("integer"))
			this.granule.add(new GranuleInteger(de, 
								Integer.valueOf(keyValue.toString())));
		else if (type.equals("real")){
			GranuleReal gr = new GranuleReal(de, Double.valueOf(keyValue.toString()));
			this.granule.add(gr);
		}
		else if (type.equals("date"))
			this.granule.add(new GranuleDateTime(de, (Date)keyValue));
		else if (type.equals("time"))
			this.granule.add(new GranuleDateTime(de, (Date)keyValue));
		
	}

//	private void mapOptionalAttr (GranuleElement element, Object theObject) {
//
//		if (element==null || metadata==null) return;
//		ElementDD elementDD = element.getElementDD();
//		String elementName = elementDD.getShortName();
//		String methodName = "get"+elementName.toUpperCase().charAt(0)+elementName.substring(1);
//		Method method;
//		try {
//			method = theObject.getClass().getMethod(methodName, new Class[] {});
//			Object value = method.invoke(theObject, new Object[0]);
//			
//			// checking type
//			String returnType = method.getReturnType().getSimpleName();
//			String elementType = elementDD.getType();
//			if (!returnType.equalsIgnoreCase(elementType))
//				log.warn(methodName+" method returned type ["+returnType+
//						"] from SIP not matching ElementDD type ["+elementType+"]!");
//			
//			// add to table dictated by elementDD type
//			if (elementType.equals("character")) 
//				this.granule.add(new GranuleCharacter(elementDD.get, value.toString()));
//			else if (elementType.equals("integer"))
//				this.granule.add(new GranuleInteger(elementDD, 
//									Integer.valueOf(value.toString())));
//			else if (elementType.equals("real"))
//				this.granule.add(new GranuleReal(elementDD, Double.valueOf(value.toString())));
//			else if (elementType.equals("date"))
//				this.granule.add(new GranuleDateTime(elementDD, (Date)value));
//			else if (elementType.equals("time"))
//				this.granule.add(new GranuleDateTime(elementDD, (Date)value));
//		} catch (SecurityException e) {
//			log.warn(methodName+" SecurityException ");
//		} catch (NoSuchMethodException e) {
//			log.warn(methodName+" NoSuchMethodException ");
//		} catch (IllegalArgumentException e) {
//			log.warn(methodName+" IllegalArgumentException ");
//		} catch (IllegalAccessException e) {
//			log.warn(methodName+" IllegalAccessException ");
//		} catch (InvocationTargetException e) {
//			log.warn(methodName+" InvocationTargetException ");
//		}
//	}
	
	public String appendArchiveSubDir(String basePathAppendType, Date startTime, Integer cycle ) throws InventoryException {
//		String basePathAppendType = 
//			this.granule.getDataset().getDatasetPolicy().getBasePathAppendType();
		
		String versionString = "";
//		if(dsVersioned.equals("Y")){
//			versionString = metadata.getVersion() + File.separator;
//		}
		
		if (basePathAppendType.equals(AppendBasePathType.YEAR_DOY.toString())) {
			//Date startTime = this.granule.getStartTime();
			if (startTime==null) startTime = this.granule.getCreateTime();
			TimeZone gmt = TimeZone.getTimeZone("GMT");
            Calendar tempCal = Calendar.getInstance();
            tempCal.setTime(startTime);
            GregorianCalendar cal = new GregorianCalendar(gmt);
            cal.setTime(startTime);
            String zero="";
            if(cal.get(Calendar.DAY_OF_YEAR) < 100)
				zero="0";
            if(cal.get(Calendar.DAY_OF_YEAR) < 10)
				zero="00";
            
            //System.out.println("cal_value:" + cal.get(Calendar.YEAR) + "/"  +zero+ cal.get(Calendar.DAY_OF_YEAR));
            //System.out.println("date_value:" + new SimpleDateFormat("yyyy").format(startTime).toString()+"/"+new SimpleDateFormat("DDD").format(startTime).toString());
			
            return ""+versionString  +cal.get(Calendar.YEAR) + "/"  +zero+ cal.get(Calendar.DAY_OF_YEAR);
			//return new SimpleDateFormat("yyyy").format(startTime).toString()
			//		+"/"+new SimpleDateFormat("DDD").format(startTime).toString();

		}
		else if (basePathAppendType.equals(AppendBasePathType.YEAR_MONTH_DAY.toString())) {
			//Date startTime = this.granule.getStartTime();
			if (startTime==null) startTime = this.granule.getCreateTime();
			
			TimeZone gmt = TimeZone.getTimeZone("GMT");
            Calendar tempCal = Calendar.getInstance();
            tempCal.setTime(startTime);
            GregorianCalendar cal = new GregorianCalendar(gmt);
            cal.setTime(startTime);
            
            	String dayZero="";
            	String monthZero="";
            if(cal.get(Calendar.DAY_OF_MONTH) < 10)
				dayZero="0";
            if(cal.get(Calendar.MONTH)+1 < 10){
            	monthZero="0"; 
            }
            
            return ""+versionString  +cal.get(Calendar.YEAR) + "-"  +monthZero.toString()+ (cal.get(Calendar.MONTH) +1) + "-"  +dayZero.toString()+ cal.get(Calendar.DAY_OF_MONTH);

		} 
		else if (basePathAppendType.equals(AppendBasePathType.YEAR.toString())) {
			//Date startTime = this.granule.getStartTime();
			
			if (startTime==null) startTime = this.granule.getCreateTime();
			 TimeZone gmt = TimeZone.getTimeZone("GMT");
             Calendar tempCal = Calendar.getInstance();
             tempCal.setTime(startTime);
             GregorianCalendar cal = new GregorianCalendar(gmt);
             cal.setTime(startTime);
             //System.out.println("cal_value:" + cal.get(Calendar.YEAR));
             //System.out.println("date_value:" + new SimpleDateFormat("yyyy").format(startTime).toString());
             return ""+versionString  +cal.get(Calendar.YEAR);
			//return new SimpleDateFormat("yyyy").format(startTime).toString();

		} else if (basePathAppendType.equals(AppendBasePathType.BATCH.toString())) {
			// need batch number from sip header so this is filled at InventoryImpl
			return ""+versionString ;
		} else if (basePathAppendType.equals(AppendBasePathType.YEAR_WEEK.toString())) {
			//week is value 1-52
			//Date startTime = this.granule.getStartTime();
			if (startTime==null) startTime = this.granule.getCreateTime();
			TimeZone gmt = TimeZone.getTimeZone("GMT");
            Calendar tempCal = Calendar.getInstance();
            tempCal.setTime(startTime);
            GregorianCalendar cal = new GregorianCalendar(gmt);
            cal.setTime(startTime);	
            String zero = "0";
            if(cal.get(Calendar.WEEK_OF_YEAR) > 10)
            {
            	zero="";
            }
            
            return ""+versionString  +cal.get(Calendar.YEAR) + "/"+zero+  + cal.get(Calendar.WEEK_OF_YEAR);
		} else if (basePathAppendType.equals(AppendBasePathType.YEAR_MONTH.toString())) {
			//week is value 1-52
			//Date startTime = this.granule.getStartTime();
			if (startTime==null) startTime = this.granule.getCreateTime();
			TimeZone gmt = TimeZone.getTimeZone("GMT");
            Calendar tempCal = Calendar.getInstance();
            tempCal.setTime(startTime);
            GregorianCalendar cal = new GregorianCalendar(gmt);
            cal.setTime(startTime);	
            String zero = "0";
            if(cal.get(Calendar.MONTH)+1 >= 10)
            {
            	zero="";
            }
            
            return ""+versionString  +cal.get(Calendar.YEAR) + "/" + zero + (cal.get(Calendar.MONTH)+1);
		} else if (basePathAppendType.equals(AppendBasePathType.CYCLE.toString())) {
			try {
				//Integer cycle = metadata.getCycle();
				if (cycle==null) throw new InventoryException("Cycle null!");
				return versionString + "c"+String.format("%03d",cycle);		
			} catch (NullPointerException npe) {
				throw new InventoryException("Cycle NPE!");
			} catch (Exception e) {
				throw new InventoryException(e.getMessage());
			}		
		} else {
			return null;
		}
	}

	private boolean replace() throws InventoryException {
		log.debug("replace: ");
		String replaceName = metadata.getReplace();
		if (replaceName == null) return false;
		if (replaceName.trim().equals("")) return false;
		Granule g = q.findGranule(replaceName, this.granule.getDataset());
		if (g == null)
			throw new InventoryException("Granule to be replaced name="+metadata.getReplace()+" not found!");
//		if(dsVersioned.equals("Y")){
//			throw new InventoryException("Granule to be replaced belongs to a versioned dataset. Replace not allowed!");
//		}
		else {
			// Check for duplication
			if ( (!replaceName.equals(this.granule.getName())) &&
			    q.findGranule(this.granule.getName(), this.granule.getDataset()) != null)
				 throw new InventoryException("Granule to be replaced with name="+this.granule.getName()+" already exists!");
			this.granule.setGranuleId(g.getGranuleId());
			this.granule.setVersion(g.getVersion()+1);
		}
		return true;
	}
	
	public void addGranuleArchive(String checksum, Character compressFlag, Long fileSize,
									String name, String type) {			
		log.debug("addGranuleArchive: ");	
		GranuleArchive archive = new GranuleArchive();	
		archive.setChecksum(checksum);
		archive.setCompressFlag(compressFlag);
		archive.setFileSize(fileSize);
		archive.setName(name);
		archive.setStatus(GranuleArchiveStatus.IN_PROCESS.toString());
		archive.setType(type); 
		this.granule.add(archive);
	}
	
	public void addGranuleReference(String filename) {
		log.debug("addGranuleReference: " + filename);
		String accessType = this.granule.getAccessType();
		if (accessType.equals(AccessType.PRIVATE) || accessType.equals(AccessType.RESTRICTED))
			return;
		// for PUBLIC/PREVIEW/SIMULATED data references
		// set granule references based on dataset location policy
		Set<DatasetLocationPolicy> locationPolicySet = 
			this.granule.getDataset().getLocationPolicySet();
		
		
		for (DatasetLocationPolicy locationPolicy : locationPolicySet) {
			String type = locationPolicy.getType();
			if (type.startsWith(LocationPolicyType.LOCAL.toString())) {
				//System.out.println("Basepath: "+locationPolicy.getBasePath());
				GranuleReference ref = new GranuleReference();
				String path = null;
				if(archiveSubDir != null)
					path = locationPolicy.getBasePath()+"/"+archiveSubDir+"/"+filename;
				else
					path = locationPolicy.getBasePath()+"/"+filename;
				ref.setType(type);
				if (type.trim().endsWith("OPENDAP")) {
					ref.setPath(path + ".html");
				} 
				else
					ref.setPath(path);
				ref.setStatus(GranuleArchiveStatus.IN_PROCESS.toString());
				this.granule.add(ref);
			}
			else{
				if(this.getGranule().getDataset().isRemote()){
					if (type.startsWith(LocationPolicyType.REMOTE.toString())) {
						GranuleReference ref = new GranuleReference();
						String path = null;
						if(archiveSubDir != null )
							path = locationPolicy.getBasePath()+"/"+archiveSubDir+"/"+filename;
						else
							path = locationPolicy.getBasePath()+"/"+filename;
						ref.setType(type);
						if (type.trim().endsWith("OPENDAP")) {
							ref.setPath(path + ".html");
						} 
						else
							ref.setPath(path);
						ref.setStatus(GranuleArchiveStatus.IN_PROCESS.toString());
						this.granule.add(ref);
					}	
				}
			}
		}		
	}

	// add metadata history
	public void addGranuleHistory() throws InventoryException {
		
		// map GranuleHistory to database schema
		GranuleMetaHistory metaHistory = new GranuleMetaHistory();
		GranuleMetaHistoryPK pk = new GranuleMetaHistoryPK();
		GranuleHistory history = null;
		try {	
			if (metadata.getGranuleHistory()==null)
			{
				log.info("No history included with granule. Creating default values.");
				
				metaHistory.setLastRevisionDateLong((new Date()).getTime());
				//metaHistory.se
				
				pk.setVersionId(1);
				pk.setGranule(this.granule);
				
				metaHistory.setRevisionHistory("Ingestion of granule file. Automated GranuleMetaHistory created.");
				metaHistory.setCreationDateLong(this.granule.getCreateTimeLong());
				
				metaHistory.setMetaHistoryPK(pk);
				manager.addGranuleMetaHistory(metaHistory);
				
				return; //throw new InventoryException("GranuleHistory is null!");
			}	
			else {
				history = metadata.getGranuleHistory();
			}
		} catch (NullPointerException npe) {
			return; //throw new InventoryException("GranuleHistory NPE!");
		}				
		try {	
			if (history.getCreateDate()==null) 
				throw new InventoryException("GranuleHistory.CreateDate is null!");
			else {
				metaHistory.setCreationDate(history.getCreateDate());
			}
		} catch (NullPointerException npe) {
			throw new InventoryException("GranuleHistory.CreateDate NPE!");
		}
		try {	
			if (history.getLastRevisionDate()==null) 
				throw new InventoryException("GranuleHistory.LastRevisionDate is null!");
			else {
				metaHistory.setLastRevisionDate(history.getLastRevisionDate());
			}
		} catch (NullPointerException npe) {
			throw new InventoryException("GranuleHistory.LastRevisionDate NPE!");
		}
		try {	
			if (history.getRevisionHistory()==null) 
				throw new InventoryException("GranuleHistory.RevisionHistory is null!");
			else {
				metaHistory.setRevisionHistory(history.getRevisionHistory());
			}
		} catch (NullPointerException npe) {
			throw new InventoryException("GranuleHistory.RevisionHistory NPE!");
		}
		try {	
			if (history.getVersion()==null) 
				throw new InventoryException("GranuleHistory.Version is null!");
			else {
				pk.setVersionId(new Float(Float.parseFloat(history.getVersion())).intValue());
			}
		} catch (NullPointerException npe) {
			throw new InventoryException("GranuleHistory.Version NPE!");
		} catch (NumberFormatException nfe) {
			log.warn("Could not set granule_version_history to value '"+history.getVersion()+"'; setting value to 1.");
			pk.setVersionId(1);
			//throw new InventoryException("GranuleHistory.Version NumberFormatException: "+nfe.getMessage());
		}
		pk.setGranule(this.granule);
		metaHistory.setMetaHistoryPK(pk);
		manager.addGranuleMetaHistory(metaHistory);
	}
	
	public void addGranule() {
		manager.addGranule(this.granule);
	}
	
	public void setGranuleRootPath(String rootPath)
	{
		this.granule.setRootPath(rootPath);
	}
	
	public void setGranuleRelPath(String relPath)
	{
		this.granule.setRelPath(relPath);
	}
	
	public void updateGranule() {
		manager.updateGranule(this.granule);
	}

	public GranuleStatus getGranuleStatus() {
		// TODO Auto-generated method stub
		
		return null;
	}

	public void updateGranuleStatus(Constant.GranuleStatus status) {

		// TODO Auto-generated method stub
		
	}
	
	
}
