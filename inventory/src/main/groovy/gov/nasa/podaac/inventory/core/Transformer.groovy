package gov.nasa.podaac.inventory.core

import gov.nasa.podaac.inventory.model.Contact;
import gov.nasa.podaac.inventory.model.Collection
import gov.nasa.podaac.inventory.model.CollectionContact
import gov.nasa.podaac.inventory.model.CollectionDataset;
import gov.nasa.podaac.inventory.model.CollectionProduct;
import gov.nasa.podaac.inventory.model.CollectionLegacyProduct;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.ElementDD;
import gov.nasa.podaac.inventory.model.DatasetCoverage;
import gov.nasa.podaac.inventory.model.DatasetContact;
import gov.nasa.podaac.inventory.model.DatasetSource;
import gov.nasa.podaac.inventory.model.DatasetMetaHistory;
import gov.nasa.podaac.inventory.model.DatasetPolicy;
import gov.nasa.podaac.inventory.model.DatasetProject;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.GranuleReal;

import gov.nasa.podaac.inventory.model.Source;
import gov.nasa.podaac.inventory.model.Sensor;

import gov.nasa.podaac.inventory.model.GranuleCharacter;
import gov.nasa.podaac.inventory.model.GranuleInteger;
import gov.nasa.podaac.inventory.model.GranuleDateTime;
import gov.nasa.podaac.inventory.model.GranuleSpatial

import gov.nasa.podaac.inventory.model.GranuleMetaHistory;
import gov.nasa.podaac.inventory.model.GranuleArchive
import gov.nasa.podaac.inventory.model.GranuleReference
import gov.nasa.podaac.inventory.model.GranuleSIP;
import gov.nasa.podaac.inventory.api.Constant.GranuleStatus
import gov.nasa.podaac.inventory.model.DatasetCitation;
import gov.nasa.podaac.inventory.model.DatasetResource;
import gov.nasa.podaac.inventory.model.DatasetLocationPolicy;
import gov.nasa.podaac.inventory.model.DatasetParameter;
import gov.nasa.podaac.inventory.model.DatasetRegion;
import gov.nasa.podaac.inventory.model.DatasetElement;
import gov.nasa.podaac.inventory.model.Project;
import gov.nasa.podaac.inventory.model.Provider;
import gov.nasa.podaac.inventory.model.ProviderResource;
import org.apache.commons.lang.StringEscapeUtils

import com.vividsolutions.jts.io.WKTReader
import com.vividsolutions.jts.geom.Polygon

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import groovy.xml.MarkupBuilder;

public class Transformer {
	
	private static Log log = LogFactory.getLog(ServiceImpl.class)
	
	public static Provider parseProvider(xml){
		Provider p = new Provider();
		p.setLongName(xml.longName.text());
		p.setProviderId(xml.providerId.text().toInteger());
		p.setShortName(xml.shortName.text());
		p.setType(xml.type.text());
		
		xml.providerResourceSet.providerResource.each { 		
			ProviderResource pr = new ProviderResource();
			pr.setProviderId(xml.providerId.text().toInteger())
			pr.setResourcePath(it.resourcePath.text())
			p.getProviderResourceSet().add(pr);
		 }
		
		xml.contactSet.contact.each { 
			Contact c = new Contact()
			c.setContactId(it.@id.text().toInteger())
			p.getContactSet().add(c)	
		}
		
		log.debug("")
		
		return p;
	}
	
	public static ElementDD parseElement(xml){
		ElementDD e = new ElementDD();
		e.setElementId(xml.elementId.text().toInteger())
		e.setShortName(xml.shortName.text())
		e.setLongName(xml.longName.text())
		
		e.setType(xml.type?.text())
		e.setDescription(xml.description?.text())
		if(!xml.maxLength?.text().equals("") && xml.maxLength?.text() != null )
			e.setMaxLength(xml.maxLength?.text()?.toInteger())
		e.setScope(xml.scope?.text())
		
		def deSet = [] as Set
		
		xml.datasetElementSet.datasetElement.each { 
			DatasetElement de = new DatasetElement()
			de.setDeId(it.@id.text().toInteger())	
			deSet.add(de)
		 }
		
		
		
		e.setDatasetElementSet(deSet)
		return e
		
	}
	
	public static parseCollection(xml){
		
		Collection c = new Collection();
		c.setAggregate(xml.aggregate.text())
		c.setCollectionId(xml.id.text().toInteger())
		c.setShortName(xml.shortName.text())
		c.setLongName(xml.longName.text())
		c.setDescription(xml.description.text())
		c.setFullDescription(xml.fullDescription.text())
		c.setType(xml.type.text())
		
		xml.collectionContacts.contact.each {
			Contact contact = parseContact(it)
			CollectionContact cc = new CollectionContact();
			cc.getCollectionContactPK().setContact(contact)
			cc.getCollectionContactPK().setCollection(c);
			c.getCollectionContactSet().add(cc);
		 }
		
		xml.datasets.dataset.each { 
			CollectionDataset cd = new CollectionDataset();
			cd.getCollectionDatasetPK().setCollection(c)
			
			log.debug(it.name())
			log.debug(it.attributes())
			log.debug(it.text())
			if(!it.@id.text().equals("") && it.@id.text() != null ){
				Dataset d = new Dataset()
				d.setDatasetId(it.@id.text().toInteger())
				cd.getCollectionDatasetPK().setDataset(d)
			}
			cd.setGranuleRange360(it.@granuleRange360.text().toCharacter())
			c.getCollectionDatasetSet().add(cd)
		 }
		
		xml.legacyProducts.legacyProduct.each { 
			CollectionLegacyProduct clp = new CollectionLegacyProduct()
			clp.setCollectionId(xml.id.text().toInteger())
			clp.setLegacyProductId(it.text().toInteger())
			c.getCollectionLegacyProductSet().add(clp)	
		 }
		
		CollectionProduct cp = new CollectionProduct()
		
		if(!xml.collectionProduct.echoSubmitTime?.text().equals("") && xml.collectionProduct.echoSubmitTime?.text() != null )
			cp.setEchoSubmitDateLong(xml.collectionProduct.echoSubmitTime.text().toLong())
		if(!xml.collectionProduct.gcmdSubmitTime?.text().equals("") && xml.collectionProduct.gcmdSubmitTime?.text() != null )
			cp.setGcmdSubmitDateLong(xml.collectionProduct.gcmdSubmitTime.text().toLong())
		
		cp.setProductId(xml.collectionProduct.productId?.text())
		if(!xml.collectionProduct.visibleFlag?.text().equals("") && xml.collectionProduct.visibleFlag?.text() != null )
		cp.setVisibleFlag(xml.collectionProduct.visibleFlag.text().toCharacter())
		
		c.setCollectionProduct(cp)
		
		return c;
			
	}
	
	public static Project parseProject(xml){
		Project p = new Project();
		p.setProjectId(xml.projectId.text().toInteger())
		p.setProjectShortName(xml.projectShortName.text())
		p.setProjectLongName(xml.projectLongName.text())
		
		return p;
	}
	
	public static Contact parseContact(xml){
		log.debug("Parsing contact")
		Contact c = new Contact();
		c.setContactId(xml.contactId.text().toInteger())
		c.setFirstName(xml.first.text())
		if(!xml.middle.text().equals("") && xml.middle.text() != null)
			c.setMiddleName(xml.middle.text())
		c.setLastName(xml.last.text())
		c.setRole(xml.role.text())
		c.setPhone(xml.phone.text())
		c.setEmail(xml.email.text())
		Provider pr = new Provider()
		pr.setLongName(xml.provider.text())
		c.setProvider(pr)
		return c;
	}
	
	public static Dataset parseDataset(xml) throws Exception{
		log.debug("PArsing dataset...")
		Dataset d = new Dataset()
		//set all the dataset info
		d.setDatasetId(xml.datasetId.text().toInteger())
		d.setPersistentId(xml.persistentId.text())
		d.setLongName(xml.longName.text())
		d.setShortName(xml.shortName.text())
		d.setOriginalProvider(xml.originalProvider.text())
		d.setProcessingLevel(xml.processingLevel.text())
		d.setProviderDatasetName(xml.providerDatasetName.text())
		if(!xml.latitudeResolution.text().equals("") && xml.latitudeResolution.text() != null)
			d.setLatitudeResolution(xml.latitudeResolution.text().toDouble())
		if(!xml.longitudeResolution.text().equals("") && xml.longitudeResolution.text() != null)
			d.setLongitudeResolution(xml.longitudeResolution.text().toDouble())
		d.setHorizontalResolutionRange(xml.horizontalResolutionRange.text())
		d.setAltitudeResolution(xml.altitudeResolution.text())
		d.setDepthResolution(xml.depthResolution.text())
		d.setTemporalResolution(xml.temporalResolution.text())
		d.setTemporalResolutionRange(xml.temporalResolutionRange.text())
		if(!xml.acrossTrackResolution.text().equals("") && xml.acrossTrackResolution.text() != null)
			d.setAcrossTrackResolution(xml.acrossTrackResolution.text().toInteger())
		if(!xml.alongTrackResolution.text().equals("") && xml.alongTrackResolution.text() != null)
			d.setAlongTrackResolution(xml.alongTrackResolution.text().toInteger())
		if(!xml.ascendingNodeTime.text().equals("") && xml.ascendingNodeTime.text() != null)
			d.setAscendingNodeTime(xml.ascendingNodeTime.text().toLong())
		d.setEllipsoidType(xml.ellipsoidType.text())
		d.setProjectionType(xml.projectionType.text())
		d.setProjectionDetail(xml.projectionDetail.text())
		d.setRemoteDataset(xml.remoteDataset.text())
		d.setReference(xml.reference.text())
		d.setDescription(xml.description.text())
		d.setMetadata(xml.metadata.text())
		if(!xml.sampleFrequency.text().equals("") && xml.sampleFrequency.text() != null)
			d.setSampleFrequency(xml.sampleFrequency.text().toFloat())
		if(!xml.swathWidth.text().equals("") && xml.swathWidth.text() != null)
			d.setSwathWidth(xml.swathWidth.text().toFloat())
		d.setTemporalRepeat(xml.temporalRepeat.text())
		d.setTemporalRepeatMax(xml.temporalRepeatMax.text())
		d.setTemporalRepeatMin(xml.temporalRepeatMin.text())
		
		//chars
		//ints
		//reals
		//datetimes
		
		Provider p = new Provider();
		p.setProviderId(xml.provider.@id.text().toInteger())
		d.setProvider(p);
		
		//citations
		xml.citationSet.datasetCitation.each { 
			
			DatasetCitation c = new DatasetCitation();
			c.setDatasetId(xml.datasetId.text().toInteger())
			c.setCitationDetail(it.citationDetail.text())
			c.setCreator(it.creator.text())
			c.setOnlineResource(it.onlineResource.text())
			c.setPublisher(it.publisher.text())
			if(!it.releaseDateLong.text().equals("") && it.releaseDateLong.text() != null ){
				c.setReleaseDate(new Date(it.releaseDateLong.text().toLong())) 
				c.setReleaseDateLong(it.releaseDateLong.text().toLong())
			}
			c.setReleasePlace(it.releasePlace.text())
			c.setSeriesName(it.seriesName.text())
			c.setTitle(it.title.text())
			c.setVersion(it.version.text())
			d.add(c)	
		 }
//		log.debug("Resource")
		//resource
		
		xml.resourceSet.datasetResource.each { 
			DatasetResource dr = new DatasetResource()
			dr.setDatasetId(xml.datasetId.text().toInteger())
			dr.setResourceDescription(it.resourceDescription.text())
			dr.setResourceName(it.resourceName.text())
			dr.setResourcePath(it.resourcePath.text())
			dr.setResourceType(it.resourceType.text())
			d.add(dr)
		 }
		
		//software
//		log.debug("LPs")
		//locationPolicySet
		
		xml.locationPolicySet.datasetLocationPolicy.each {  
			DatasetLocationPolicy dlp = new DatasetLocationPolicy()
			dlp.setDatasetId(xml.datasetId.text().toInteger())
			dlp.setBasePath(it.basePath.text())
			dlp.setType(it.type.text())
			d.add(dlp)
		}
//		log.debug("Params")
		xml.parameterSet.datasetParameter.each { 
			DatasetParameter dp = new DatasetParameter()
			dp.setDatasetId(xml.datasetId.text().toInteger())
			dp.setCategory(it.category.text())
			dp.setTerm(it.term.text())
			dp.setTopic(it.topic.text())
			dp.setVariable(it.variable.text())
			dp.setVariableDetail(it.variableDetail.text())
			d.add(dp)
		 }
		//parameter
		
		//regionSet
//		log.debug("Regions")
		xml.regionSet.datasetRegion.each {
			DatasetRegion dr = new DatasetRegion()
			dr.setDatasetId(xml.datasetId.text().toInteger())
			dr.setRegion(it.region.text())
			dr.setRegionDetail(it.regionDetail.text())
			d.getRegionSet().add(dr)
		}
		//datasetElement
//		log.debug("DEs")
		xml.datasetElementSet.datasetElement.each {
			DatasetElement de = new DatasetElement()
			de.setDeId(it.attributes().get("id").toInteger())
			d.getDatasetElementSet().add(de)
		}
		
		xml.contactSet.datasetContact.Contact.each {
			
			Contact c = parseContact(it)
			DatasetContact dc = new DatasetContact();
			
			dc.getDatasetContactPK().setDataset(d)
			dc.getDatasetContactPK().setContact(c)
			
			d.getContactSet().add(dc)
		}
		
		xml.sourceSet.datasetSource.each { 
			def src = it.source
			def sen = it.sensor
			Source source = new Source();
			Sensor sensor = new Sensor();
			//parse them
			source = parseSource(src)
			sensor = parseSensor(sen)
			
			
			DatasetSource ds = new DatasetSource()
			ds.getDatasetSourcePK().setSensor(sensor)
			ds.getDatasetSourcePK().setSource(source)
			d.add(ds);
		}
		
		xml.projectSet.project.each { 
			log.debug("Parsing project")
			Project proj = parseProject(it)
			DatasetProject dp = new DatasetProject();
			dp.getDatasetProjectPK().setDataset(d);
			dp.getDatasetProjectPK().setProject(proj);
			d.getProjectSet().add(dp);	
		}
		
		d.setDatasetPolicy(parseDatasetPolicy(xml.datasetPolicy));
		d.setDatasetCoverage(parseDatasetCoverage(xml.datasetCoverage))
		
		xml.metaHistorySet.datasetMetaHistory.each {
			def dmh = new DatasetMetaHistory()
			
			if(!it.creationDateLong.text().equals("") && it.creationDateLong.text() != null )
				dmh.setCreationDateLong(it.creationDateLong.text().toLong())
			if(!it.lastRevisionDateLong.text().equals("") && it.lastRevisionDateLong.text() != null )
				dmh.setLastRevisionDateLong(it.lastRevisionDateLong.text().toLong())
			dmh.setRevisionHistory(it.revisionHistory.text())

                        if(!it.echoSubmitDateLong.text().equals("") && it.echoSubmitDateLong.text() != null )
				dmh.setEchoSubmitDateLong(it.echoSubmitDateLong.text().toLong())
                        if(!it.gcmdSubmitDateLong.text().equals("") && it.gcmdSubmitDateLong.text() != null )
                                dmh.setGcmdSubmitDateLong(it.gcmdSubmitDateLong.text().toLong())


			d.add(dmh)
		}
			
		
		
		return d
	}
	
	public static Source parseSource(xml){
		Source s = new Source();

		s.setSourceId(xml.sourceId.text().toInteger())
		s.setSourceShortName(xml.sourceShortName.text())
		s.setSourceLongName(xml.sourceLongName.text())
		s.setSourceType(xml.sourceType.text())
		
		if(!xml.orbitPeriod.text().equals("") && xml.orbitPeriod.text() != null )
			s.setOrbitPeriod(xml.orbitPeriod.text().toFloat())
		if(!xml.inclinationAngle.text().equals("") && xml.inclinationAngle.text() != null )
			s.setInclinationAngle(xml.inclinationAngle.text().toFloat())
		s.setSourceDescription(xml.sourceDescription.text())
		
		return s;
	}
	
	public static Sensor parseSensor(xml){
		Sensor s = new Sensor();
		s.setSensorId(xml.sensorId.text().toInteger())
		s.setSensorShortName(xml.sensorShortName.text())
		s.setSensorLongName(xml.sensorLongName.text())
		s.setSensorDescription(xml.sensorDescription.text())
		
		if(!xml.swathWidth.text().equals("") && xml.swathWidth.text() != null )
			s.setSwathWidth(xml.swathWidth.text().toFloat())
		if(!xml.sampleFrequency.text().equals("") && xml.sampleFrequency.text() != null )
			s.setSampleFrequency(xml.sampleFrequency.text().toFloat())
		
		return s;
	}
	
	public static DatasetPolicy parseDatasetPolicy(xml){
		DatasetPolicy dp = new DatasetPolicy()
		Dataset d = new Dataset().setDatasetId(xml.datasetId.text().toInteger());
		dp.setDataset(d)
		dp.setDataClass(xml.dataClass.text())
		
		if(!xml.dataFrequency.text().equals("") && xml.dataFrequency.text() != null )
			dp.setDataFrequency(xml.dataFrequency.text().toInteger())
		if(!xml.dataVolume.text().equals("") && xml.dataVolume.text() != null )
			dp.setDataVolume(xml.dataVolume.text().toInteger())
		if(!xml.dataDuration.text().equals("") && xml.dataDuration.text() != null )
			dp.setDataDuration(xml.dataDuration.text().toInteger())
		if(!xml.dataLatency.text().equals("") && xml.dataLatency.text() != null )
			dp.setDataLatency(xml.dataLatency.text().toInteger())
		
		dp.setAccessType(xml.accessType.text())
		dp.setBasePathAppendType(xml.basePathAppendType.text())
		dp.setDataFormat(xml.dataFormat.text())
		dp.setCompressType(xml.compressType.text())
		dp.setChecksumType(xml.checksumType.text())
		dp.setSpatialType(xml.spatialType.text())
		dp.setAccessConstraint(xml.accessConstraint.text())
		dp.setUseConstraint(xml.useConstraint.text())
		dp.setViewOnline(xml.viewOnline.text())
//		dp.setVersioned(xml.versioned.text())
//		dp.setVersionPolicy(xml.versionPolicy.text())
		return dp;
	}
	
	public static  DatasetCoverage parseDatasetCoverage(xml){
	
		DatasetCoverage dc = new DatasetCoverage()
		dc.setDatasetId(xml.datasetId.text().toInteger())
		

		if(!xml.startTimeLong.text().equals("") && xml.startTimeLong.text() != null )
			dc.setStartTimeLong(xml.startTimeLong.text().toLong())
		if(!xml.stopTimeLong.text().equals("") && xml.stopTimeLong.text() != null )
			dc.setStopTimeLong(xml.stopTimeLong.text().toLong())
		if(!xml.northLat.text().equals("") && xml.northLat.text() != null )
			dc.setNorthLat(xml.northLat.text().toDouble())
		if(!xml.southLat.text().equals("") && xml.southLat.text() != null )
			dc.setSouthLat(xml.southLat.text().toDouble())
		if(!xml.eastLon.text().equals("") && xml.eastLon.text() != null )
			dc.setEastLon(xml.eastLon.text().toDouble())
		if(!xml.westLon.text().equals("") && xml.westLon.text() != null )
			dc.setWestLon(xml.westLon.text().toDouble())
		if(!xml.minAltitude.text().equals("") && xml.minAltitude.text() != null )
			dc.setMinAltitude(xml.minAltitude.text().toDouble())
		if(!xml.maxAltitude.text().equals("") && xml.maxAltitude.text() != null )
			dc.setMaxAltitude(xml.maxAltitude.text().toDouble())
		if(!xml.minDepth.text().equals("") && xml.minDepth.text() != null )
			dc.setMinDepth(xml.minDepth.text().toDouble())
		if(!xml.maxDepth.text().equals("") && xml.maxDepth.text() != null )
			dc.setMaxDepth(xml.maxDepth.text().toDouble())
                if(!xml.granuleRange360.text().equals("") && xml.granuleRange360.text() != null ){
                        dc.setGranuleRange360(xml.granuleRange360.text().toCharacter())
			}

                        //dc.setGranuleRange360(xml.granuleRange360.text().charAt(0))

		return dc;
	}
	
	public static parseGranuleRefs(xml){
		
		
		def list = [];
		//reference
		xml.granuleReference.each {
			
			GranuleReference gr = new GranuleReference()
			try{
			gr.setDescription(it.description.text())
			gr.setPath(it.path.text())
			gr.setStatus(it.status.text())
			gr.setType(it.type.text())
			gr.setGranuleId(it.granuleId.text().toInteger())
			}catch(Exception e){
				log.debug("Caught exception parsing refs...");
			}
			list.add(gr);
		}
		return list;
	}
	
	public static parseGranuleRefLists(xml){
		
		
		def list = [];
		//reference
		xml.granuleReference.each {
			GranuleReference gr = new GranuleReference()
			gr.setDescription(it.description.text())
			gr.setPath(it.path.text())
			gr.setStatus(it.status.text())
			gr.setType(it.type.text())
			gr.setGranuleId(it.granuleId.text().toInteger())
			list.add(gr);
		}
		
		return list;
	}
	
	
	public static parseGranuleIds(xml){
		
		
		
	}
	
	
	public static String renderUpdateGranule(g){
		
		def writer = new StringWriter()
		def xml = new MarkupBuilder(writer)
		
		xml."granule"(){
			"granuleId"(g.getGranuleId())
			"name"(g.getName())
			"startTimeLong"(g.getStartTimeLong())
			"stopTimeLong"(g.getStopTimeLong())
			"requestedTimeLong"(g.getRequestedTimeLong())
			"acquiredTimeLong"(g.getAcquiredTimeLong())
			"createTimeLong"(g.getCreateTimeLong())
			"ingestTimeLong"(g.getIngestTimeLong())
			"verifyTimeLong"(g.getVerifyTimeLong())
			"archiveTimeLong"(g.getArchiveTimeLong())
			"version"(g.getVersion())
			"accessType"(g.getAccessType())
			"dataFormat"(g.getDataFormat())
			"compressType"(g.getCompressType())
			"checksumType"(g.getChecksumType())
			"rootPath"(g.getRootPath())
			"relPath"(g.getRelPath())
			"status"(g.getStatus().toString())
			
			"granuleCharacterSet"{
				GranuleCharacter gc = null
				g.getGranuleCharacterSet().each {
					gc = it
					"granuleCharacter"{
						DatasetElement de = gc.getDatasetElement();
						"datasetElement"(id:de.getDeId()){
							"deId"(de?.getDeId())
//							"dataset"(id:de?.getDataset()?.getDatasetId())
							"elementDD"(id:de?.getElementDD()?.getElementId() , name:de?.getElementDD()?.getShortName())
//							"scope"(de?.getScope())
//							"obligationFlag"(de?.getObligationFlag())
						}
						"value"(gc.getValue())
					}
				}
			}
			"granuleIntegerSet"{
				GranuleInteger gc = null
				g.getGranuleIntegerSet().each {
					gc = it
					"granuleInteger"{
						DatasetElement de = gc.getDatasetElement();
						"datasetElement"(id:de?.getDeId()){
							"deId"(de?.getDeId())
//							"dataset"(id:de?.getDataset()?.getDatasetId())
							"elementDD"(id:de?.getElementDD()?.getElementId() , name:de?.getElementDD()?.getShortName())
//							"scope"(de?.getScope())
//							"obligationFlag"(de?.getObligationFlag())
						}
						"value"(gc.getValue())
						"units"(gc.getUnits())
					}
				}
			}
			"granuleRealSet"{
				GranuleReal gc = null
				g.getGranuleRealSet().each {
					gc = it
					"granuleReal"{
						DatasetElement de = gc?.getDatasetElement();
						"datasetElement"(id:de?.getDeId()){
							"deId"(de?.getDeId())
//							"dataset"(id:de?.getDataset()?.getDatasetId())
							"elementDD"(id:de?.getElementDD()?.getElementId() , name:de?.getElementDD()?.getShortName())
//							"scope"(de?.getScope())
//							"obligationFlag"(de?.getObligationFlag())
						}
						"value"(gc.getValue())
						"units"(gc.getUnits())
					}
				}
			}
			"granuleDateTimeSet"{
				GranuleDateTime gc = null
				g.getGranuleDateTimeSet().each {
					gc = it
					"granuleDateTime"{
						DatasetElement de = gc.getDatasetElement();
						"datasetElement"(id:de?.getDeId()){
							"deId"(de?.getDeId())
//							"dataset"(id:de?.getDataset()?.getDatasetId())
							"elementDD"(id:de?.getElementDD()?.getElementId() , name:de?.getElementDD()?.getShortName())
//							"scope"(de?.getScope())
//							"obligationFlag"(de?.getObligationFlag())
						}
						"value"(gc.getValueLong())
					}
				}
			}
//			"granuleSpatialSet"{
//				GranuleSpatial gc = null
//				g.getGranuleSpatialSet().each {
//					gc = it
//					"granuleSpatial"{
//						DatasetElement de = gc.getDatasetElement();
//						"datasetElement"(id:de.getDeId()){
//							"deId"(de.getDeId())
//							"dataset"(id:de.getDataset().getDatasetId())
//							"elementDD"(id:de.getElementDD().getElementId())
//							"scope"(de.getScope())
//							"obligationFlag"(de.getObligationFlag())
//						}
//						
//						WKTWriter wr = new WKTWriter();
//						def wkt = wr.write(gc.getValue());
//						"value"(type:"wkt",wkt)
//					}
//				}
//			}
		}
		
		return writer.toString();
		
	}
	
	public static parseGranule(xml){
		log.debug("in parseGranule")
		Granule g = new Granule();
		
		log.debug("Parsing Dataset Info")
		Dataset d = new Dataset();
		try{
		d.setDatasetId(xml.dataset[0].attributes().get("id").toInteger())
		g.setDataset(d);
		}catch(Exception e){
			log.debug(e.getMessage())
		}
		
		
		log.debug("Parsing Granule Info")
		if(!xml.granuleId.text().equals("") && xml.granuleId.text() != null )
			g.setGranuleId(xml.granuleId.text().toInteger())
		if(!xml.name.text().equals("") && xml.name.text() != null )
			g.setName(xml.name.text())
		if(!xml.startTimeLong.text().equals("") && xml.startTimeLong.text() != null )
			g.setStartTimeLong(xml.startTimeLong.text().toLong())
		if(!xml.stopTimeLong.text().equals("") && xml.stopTimeLong.text() != null )
			g.setStopTimeLong(xml.stopTimeLong.text().toLong())
		if(!xml.requestedTimeLong.text().equals("") && xml.requestedTimeLong.text() != null )
			g.setRequestedTimeLong(xml.requestedTimeLong.text().toLong())
		if(!xml.acquiredTimeLong.text().equals("") && xml.acquiredTimeLong.text() != null )
			g.setAcquiredTimeLong(xml.acquiredTimeLong.text().toLong())
		if(!xml.createTimeLong.text().equals("") && xml.createTimeLong.text() != null )
			g.setCreateTimeLong(xml.createTimeLong.text().toLong())
		if(!xml.ingestTimeLong.text().equals("") && xml.ingestTimeLong.text() != null )
			g.setIngestTimeLong(xml.ingestTimeLong.text().toLong())
		if(!xml.archiveTimeLong.text().equals("") && xml.archiveTimeLong.text() != null )
			g.setArchiveTimeLong(xml.archiveTimeLong.text().toLong())
		if(!xml.verifyTimeLong.text().equals("") && xml.verifyTimeLong.text() != null )
			g.setVerifyTimeLong(xml.verifyTimeLong.text().toLong())
		if(!xml.version.text().equals("") && xml.version.text() != null )
			g.setVersion(xml.version.text().toInteger())
			
		g.setAccessType(xml.accessType.text())
		g.setDataFormat(xml.dataFormat.text())
		g.setCompressType(xml.compressType.text())
		g.setChecksumType(xml.checksumType.text())
		g.setStatus(GranuleStatus.valueOf(xml.status.text().trim()))
		g.setRootPath(xml.rootPath.text())
		g.setRelPath(xml.relPath.text())
		
		if(!xml.archiveTimeLong.text().equals("") && xml.archiveTimeLong.text() != null )
			g.setArchiveTimeLong(xml.archiveTimeLong.text().toLong())
		
		//sip
			log.debug("Parsing Sip info")
		xml.granuleSIPSet.granuleSIP.each { 
			GranuleSIP gsip = new GranuleSIP()
			gsip.setSip(StringEscapeUtils.unescapeHtml(it.sip.text()))
			gsip.setGranule(g)
			g.add(gsip);
		}
		
		log.debug("Parsing Archive Set")
		//archiveSet
		xml.granuleArchiveSet.granuleArchive.each { 
			GranuleArchive ga = new GranuleArchive()
			ga.setGranuleId(g.getGranuleId())
			ga.setChecksum(it.checksum.text())
			ga.setFileSize(it.fileSize.text().toLong())
			ga.setName(it.name.text())
			ga.setStatus(it.status.text())
			ga.setType(it.type.text())
			//ga.setCompressFlag(null)
			g.add(ga)
		}
		
		log.debug("Parsing Reference Set")
		//reference
		xml.granuleReferenceSet.granuleReference.each {
			GranuleReference gr = new GranuleReference()
			gr.setDescription(it.description.text())
			gr.setPath(it.path.text())
			gr.setStatus(it.status.text())
			gr.setType(it.type.text())
			gr.setGranuleId(g.getGranuleId())
			g.add(gr)	
		}
		
		log.debug("Parsing Reals")
		//reals
		xml.granuleRealSet.granuleReal.each { 
			GranuleReal gr = new GranuleReal();	
			
			if(!it.value.text().equals("") && it.value.text() != null)
				gr.setValue(it.value.text().toDouble())
			
			DatasetElement de =new DatasetElement();
			de.setDeId(it.datasetElement.deId.text().toInteger())
			ElementDD ed = new ElementDD();
			try{
				ed.setElementId(it.datasetElement.elementDD.'@id'.text()?.toInteger())
			}catch(Exception e){};
			ed.setShortName(it.datasetElement.elementDD.'@name'?.text())
			de.setElementDD(ed)
			gr.setDatasetElement(de);
			g.add(gr);
		}
		
		log.debug("Parsing Chars")
		//chars
		xml.granuleCharacterSet.granuleCharacter.each {
			GranuleCharacter gr = new GranuleCharacter();
			
			if(!it.value.text().equals("") && it.value.text() != null)
				gr.setValue(it.value.text())
			
			DatasetElement de =new DatasetElement();
			de.setDeId(it.datasetElement.deId.text().toInteger())
			ElementDD ed = new ElementDD();
			try{
				ed.setElementId(it.datasetElement.elementDD.'@id'.text()?.toInteger())
			}catch(Exception e){};
			ed.setShortName(it.datasetElement.elementDD.'@name'?.text())
			de.setElementDD(ed)
			gr.setDatasetElement(de);
			g.add(gr);
		}
		
		log.debug("Parsing DateTime")
		//dateTime
		xml.granuleDateTimeSet.granuleDateTime.each {
			GranuleDateTime gr = new GranuleDateTime();
			
			if(!it.value.text().equals("") && it.value.text() != null)
				gr.setValueLong(it.value.text().toLong())
			
			DatasetElement de =new DatasetElement();
			de.setDeId(it.datasetElement.deId.text().toInteger())
			ElementDD ed = new ElementDD();
			try{
				ed.setElementId(it.datasetElement.elementDD.'@id'.text()?.toInteger())
			}catch(Exception e){};
			ed.setShortName(it.datasetElement.elementDD.'@name'?.text())
			de.setElementDD(ed)
			gr.setDatasetElement(de);
			g.add(gr);
		}
		
		log.debug("Parsing Integers")
		//integer
		xml.granuleIntegerSet.granuleInteger.each {
			GranuleInteger gr = new GranuleInteger();
			
			if(!it.value.text().equals("") && it.value.text() != null)
				gr.setValue(it.value.text().toInteger())
			
			DatasetElement de =new DatasetElement();
			de.setDeId(it.datasetElement.deId.text().toInteger())
			ElementDD ed = new ElementDD();
			try{
				ed.setElementId(it.datasetElement.elementDD.'@id'.text()?.toInteger())
			}catch(Exception e){};
			ed.setShortName(it.datasetElement.elementDD.'@name'?.text())
			de.setElementDD(ed)
					
			gr.setDatasetElement(de);
			g.add(gr);
		}
		log.debug("Parsing Spatial")
		//spatial
		g.setGranuleSpatialSet(parseGranuleSpatial(xml.granuleSpatialSet));
		
		//metahistory
		log.debug("Parsing GMH")
		g.setMetaHistorySet(parseGranuleMetaHistory(xml.metaHistorySet))
		//contacts?
		
		return g;
		
	}
	
	public static parseGranuleSpatial(xml){
		Set<GranuleSpatial> gss = new HashSet<GranuleSpatial>();
		xml.granuleSpatial.each {
			GranuleSpatial gs = new GranuleSpatial();
			
			if(!it.value.text().equals("") && it.value.text() != null)
			{
				def spString = it.value.text()
				WKTReader rdr = new WKTReader();
				def poly=  (Polygon)rdr.read(spString);
					gs.setValue(poly)
			}
			DatasetElement de =new DatasetElement();
			de.setDeId(it.datasetElement.deId.text().toInteger())
			gs.setDatasetElement(de);
			gss.add(gs)
		}
		return gss;	
		
	}
	
	public static parseGranuleMetaHistory(xml){
	
		Set<GranuleMetaHistory> gmhSet = new HashSet<GranuleMetaHistory>();
		
		xml.granuleMetaHistory.each {
			GranuleMetaHistory gmh = new GranuleMetaHistory()
			
			if(!it.creationDateLong.text().equals("") && it.creationDateLong.text() != null )
				gmh.setCreationDateLong(it.creationDateLong.text().toLong())
			
			if(!it.echoSubmitDateLong.text().equals("") && it.echoSubmitDateLong.text() != null )
				gmh.setEchoSubmitDateLong(it.echoSubmitDateLong.text().toLong())
			
			if(!it.lastRevisionDateLong.text().equals("") && it.lastRevisionDateLong.text() != null )
				gmh.setLastRevisionDateLong(it.lastRevisionDateLong.text().toLong())
			
			gmh.setRevisionHistory(it.revisionHistory.text())
			
			if(!it.metaHistoryPK.versionId.text().equals("") && it.metaHistoryPK.versionId.text() != null )
			{	
				gmh.getMetaHistoryPK().setVersionId(it.metaHistoryPK.versionId.text().toInteger())
				
			}
			
			gmhSet.add(gmh)
		}
		
		return gmhSet
			
	}
	
}
