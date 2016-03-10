/**
 * DatasetController
 */
import grails.util.JSonBuilder
import java.net.URLDecoder;
import groovy.xml.MarkupBuilder
import grails.converters.deep.XML;
import grails.converters.deep.JSON;

import gov.nasa.podaac.common.api.metadatamanifest.MetadataManifest
import gov.nasa.podaac.common.api.metadatamanifest.Constant.ActionType
import gov.nasa.podaac.common.api.metadatamanifest.Constant.ObjectType

import gov.nasa.podaac.inventory.api.Query
import gov.nasa.podaac.inventory.api.QueryFactory
import gov.nasa.podaac.inventory.model.Dataset
import gov.nasa.podaac.inventory.model.DatasetCoverage
import gov.nasa.podaac.inventory.model.DatasetPolicy;
import gov.nasa.podaac.inventory.model.Provider;

class DatasetController {

	private static final Integer GR_PAGE_SIZE = 1000;
	private static final Integer GR_REF_PAGE_SIZE = 3000;
	
   def authenticationService
   def inventoryService

   def heartbeat = {
	log.debug("Inventory Heartbeat")
        response.status = 200 //Not Found
	render(contentType:"text/xml"){
        "response"{
             "status"("Inventory Service is Online")
           }
        }

        return

   }
   
   def show = {
	   
			   def DatasetId
			   Dataset d = resolveId(params.id);
				   if(d == null){
					   log.debug("404, I'm afraid i can't do that, Dave.")
					   response.status = 404 //Not Found
							 render "Dataset: ${params.id} not found."
					   return
			   }
			   d.setGranuleSet(null);
			   log.debug("elementDDs:");
			   log.debug(d.getDatasetElementSet().size());
	   //		for(de in d.getDatasetElementSet()){
	   //			log.debug de.getElementDD().getShortName();
	   //			}
	   //
			   renderDataset(d)
			   return;
		  }
   
   //boy was this one annoying to marshall
   def renderDataset = {Dataset d ->
	   render(contentType:"text/xml"){
		   "dataset"(id:d.getDatasetId()){
				   "datasetId"(d.getDatasetId())
				   "persistentId"(d.getPersistentId())				   
				   "shortName"(d.getShortName())
				   "longName"(d.getLongName())
				   "originalProvider"(d.getOriginalProvider())
				   "providerDatasetName"(d.getProviderDatasetName())
				   "processingLevel"(d.getProcessingLevel())
				   "latitudeResolution"(d.getLatitudeResolution())
				   "longitudeResolution"(d.getLongitudeResolution())
				   "horizontalResolutionRange"(d.getHorizontalResolutionRange())
				   "altitudeResolution"(d.getAltitudeResolution())
				   "depthResolution"(d.getDepthResolution())
				   "temporalResolution"(d.getTemporalResolution())
				   "temporalResolutionRange"(d.getTemporalResolutionRange())
				   "acrossTrackResolution"(d.getAcrossTrackResolution())
				   "alongTrackResolution"(d.getAlongTrackResolution())
				   "ascendingNodeTime"(d.getAscendingNodeTime())
				   "ellipsoidType"(d.getEllipsoidType())
				   "projectionType"(d.getProjectionType())
				   "projectionDetail"(d.getProjectionDetail())
				   "remoteDataset"(d.getRemoteDataset())
				   "reference"(d.getReference())
				   "description"(d.getDescription())
				   "metadata"(d.getMetadata())
				   "sampleFrequency"(d.getSampleFrequency())
				   "swathWidth"(d.getSwathWidth())
				   "temporalRepeat"(d.getTemporalRepeat())
				   "temporalRepeatMin"(d.getTemporalRepeatMin())
				   "temporalRepeatMax"(d.getTemporalRepeatMax())
				   "note"(d.getNote())
				   //characters
				   "DatasetCharacterSet"{}
				   //integers
				   "DatasetIntegerSet"{}
				   //reals
				   "DatasetRealSet"{}
				   //datetime
				   "DatasetDateTimeSet"{}
				   "provider"(id:d.getProvider().getProviderId()){
					   
				   }
				   "citationSet"{
					   d.getCitationSet().each {
						   def cit = it; 
						   "datasetCitation"{
							   "citationDetail"(cit.getCitationDetail())
							   "creator"(cit.getCreator())
							   "datasetId"(d.getDatasetId())
							   "onlineResource"(cit.getOnlineResource())
							   "publisher"(cit.getPublisher())
							   "releaseDateLong"(cit.getReleaseDateLong())
							   "releasePlace"(cit.getReleasePlace())
							   "seriesName"(cit.getSeriesName())
							   "title"(cit.getTitle())
							   "version"(cit.getVersion())
						   }
					   }
				   }
				   "regionSet"{
					   d.getRegionSet().each {
						   def reg = it 
						   "datasetRegion"{
							   "region"(reg.getRegion())
							   "regionDetail"(reg.getRegionDetail())
							   "datasetId"(d.getDatasetId())
						   }
					   }
				   }
				   "resourceSet"{
					   d.getResourceSet().each { 
						   def res = it
						   "datasetResource"{
							   "datasetId"(d.getDatasetId())
							   "resourceDescription"(res.getResourceDescription())
							   "resourceName"(res.getResourceName())
							   "resourcePath"(res.getResourcePath())
							   "resourceType"(res.getResourceType())
						   }
					   }
				   }
				   "softwareSet"{}
				   "locationPolicySet"{
					   d.getLocationPolicySet().each {
						   def loc = it
						   "datasetLocationPolicy"{
							   "datasetId"(d.getDatasetId())
							   "type"(loc.getType())
							   "basePath"(loc.getBasePath())
						   }
					   }
				   }
				   "parameterSet"{
					   d.getParameterSet().each { 
						   def param = it
						   "datasetParameter"{
						   		"category"(param.getCategory())
								"term"(param.getTerm())
								"topic"(param.getTopic())
								"variable"(param.getVariable())
								"variableDetail"(param.getVariableDetail())
						   }
					   }
				   }
				   "granuleSet"{}
				   "datasetElementSet"{
					   d.getDatasetElementSet().each { 
						"datasetElement"(id:it.getDeId())   
					   }
				   }
				   "versionSet"{
					   d.getVersionSet().each { 
						def ver = it;
						    "datasetVersion"{
								"version"(ver.getVersion())
								"versionDateLong"(ver.getVersionDateLong())
								"description"(ver.getDescription())
								"datasetVersionPK"{
									"dataset"(id:d.getDatasetId())
									"versionId"(ver.getDatasetVersionPK().getVersionId())
								}
							}
					   }
				   }
				   "metaHistorySet"{
					   d.getMetaHistorySet().each { 
							def dmh = it;
							"datasetMetaHistory"{
								"creationDateLong"(dmh.getCreationDateLong())
								"lastRevisionDateLong"(dmh.getLastRevisionDateLong())
								"revisionHistory"(dmh.getRevisionHistory())
								"echoSubmitDateLong"(dmh.getEchoSubmitDateLong())
								"datasetMetaHistoryPK"{
									"dataset"(id:d.getDatasetId())
									"versionId"(dmh.getMetaHistoryPK().getVersionId())
								}
							}   
					   }
				   }
				   "sourceSet"{
					   d.getSourceSet().each {
						   def src = it.getDatasetSourcePK().getSource()
						   def sen = it.getDatasetSourcePK().getSensor()
						   "datasetSource"{
							   "source"{
								   "sourceId"(src.getSourceId())
								   "sourceShortName"(src.getSourceShortName())
								   "sourceLongName"(src.getSourceLongName())
								   "sourceType"(src.getSourceType())
								   "orbitPeriod"(src.getOrbitPeriod())
								   "inclinationAngle"(src.getInclinationAngle())
								   "sourceDescription"(src.getSourceDescription())
							   }
							   "sensor"{
								   "sensorId"(sen.getSensorId())
								   "sensorShortName"(sen.getSensorShortName())
								   "sensorLongName"(sen.getSensorLongName())
								   "swathWidth"(sen.getSwathWidth())
								   "sensorDescription"(sen.getSensorDescription())
								   "sampleFrequency"(sen.getSampleFrequency())
							   }
						   }
					   }
				   }
				   "contactSet"{
					   d.getContactSet().each {
						   def c = it.getDatasetContactPK().getContact() 
						   "datasetContact"{
						   		"Contact"{
									"contactId"(c.getContactId())
									"first"(c.getFirstName())
									"middle"(c.getMiddleName())
									"last"(c.getLastName())
									"role"(c.getRole())
									"phone"(c.getPhone())
									"email"(c.getEmail())
									"provider"(c.getProvider().getLongName())
							   }
						   }
					   }
					}
				   "projectSet"{
					   d.getProjectSet().each {  
					   		def p = it.getDatasetProjectPK().getProject()
							"project"{
								"projectId"(p.getProjectId())
								"projectShortName"(p.getProjectShortName())
								"projectLongName"(p.getProjectLongName())
							}
					   }
				   }
				   "datasetPolicy"{
					   def p = d.getDatasetPolicy();
					   "datasetId"(p.getDatasetId())
					   "dataClass"(p.getDataClass())
					   "dataFrequency"(p.getDataFrequency())
					   "dataVolume"(p.getDataVolume())
					   "dataDurationv"(p.getDataDuration())
					   "dataLatency"(p.getDataLatency())
					   "accessType"(p.getAccessType())
					   "viewOnline"(p.getViewOnline())
					   "basePathAppendType"(p.getBasePathAppendType())
					   "dataFormat"(p.getDataFormat())
					   "compressType"(p.getCompressType())
					   "checksumType"(p.getChecksumType())
					   "spatialType"(p.getSpatialType())
					   "accessConstraint"(p.getAccessConstraint())
					   "useConstraint"(p.getUseConstraint())
				   }
				   "datasetCoverage"(id:d.getDatasetId()){
					   def c = d.getDatasetCoverage()
					   "datasetId"(d.getDatasetId())
					   "northLat"(c.getNorthLat())
					   "southLat"(c.getSouthLat())
					   "eastLon"(c.getEastLon())
					   "westLon"(c.getWestLon())
					   "startTimeLong"(c.getStartTimeLong())
					   "stopTimeLong"(c.getStopTimeLong())
					   "minAltitude"(c.getMinAltitude())
					   "maxAltitude"(c.getMaxAltitude())
					   "minDepth"(c.getMinDepth())
					   "maxDepth"(c.getMaxDepth())
					   "granuleRange360"(c.getGranuleRange360())
					   "dataset"(id:d.getDatasetId())
				   }
		   }
	   }
   }

   def index = {
      redirect(action: authenticate)
   }

   private void renderResult(Object theObject){

	if(params.json.equals("1") || params.useJson.equals("true"))
                render theObject as JSON
         else
                render theObject as XML		
   }
   
   def renderReturnResult = {theObject->
	   def ret;
		   if(params.json.equals("1") || params.useJson.equals("true"))
					   ret = theObject as JSON
				else
					   ret = theObject as XML
					   
					   return ret;
		  }
   

   def policy = {
	def DatasetId
        Dataset d = resolveId(params.id);
        if(d==null)
        {       
               log.debug("404, I'm afraid i can't do that, Dave.")
               response.status = 404 //Not Found
               render "Dataset: ${params.id} not found."
               return
        }
	DatasetPolicy pol = inventoryService.fetchDatasetPolicy(d.getDatasetId())
	if(pol == null)
	{
	  response.status = 404;
	  render "No policy found for dataset ${params.id}"
	} 
	renderResult(pol)
	return
   }

   def findByProductId = {
	   def pId = params.id;
	   if(pId==null)
	   {
		  log.debug("404, I'm afraid i can't do that, Dave.")
			  response.status = 404 //Not Found
			  render "Product Id not specified."
			  return
	   }
	   int i = pId.toInteger().intValue()
	   def res = inventoryService.findDatasetByProductId(i)
	   res.setGranuleSet(null);
	   renderResult(res);
   }
   def getAIP = {
	   def DatasetId
	   def Dataset d = resolveId(params.id);
	   if(d==null)
	   {
		  log.debug("404, I'm afraid i can't do that, Dave.")
			  response.status = 404 //Not Found
			  render "Dataset: ${params.id} not found."
			  return
	   }
	   def refList = inventoryService.getArchiveAIPByDataset(d.getDatasetId())
	   renderResult(refList)
   }
   def listOfGranules = {
	   def DatasetId
	   def Dataset d = resolveId(params.id);
	   if(d==null)
	   {
		  log.debug("404, I'm afraid i can't do that, Dave.")
			  response.status = 404 //Not Found
			  render "Dataset: ${params.id} not found."
			  return
	   }
	   def start = null;
	   def stop = null;
	   def pattern = params.pattern;
	   
	   log.debug("pattern: $pattern");
	  
	   
	   if(params.startTime != null){
		   log.debug("ValueOf [params.startTime]:"+params.startTime);
	   		start = Long.valueOf(params.startTime)
	   }
	   if(params.stopTime != null){
		   log.debug("ValueOf [params.stopTime]:"+params.stopTime);
		   stop = Long.valueOf(params.stopTime)
	   }
	   
	   def refList = inventoryService.getGranulesByDataset(d.getDatasetId(),start,stop,pattern)
//	   renderResult(refList)
	   def page =null;
	   if(params.page==null)
		   page = 1;
	   else{
		   try{
			   page = Integer.valueOf(params.page)
		   }catch(Exception e){
				   log.debug("Exception parsing page number: ${e.getMessage()}. Setting page=1")
				   page=1;
		   }
	   }
	   def startp = (page-1)*GR_PAGE_SIZE;
	   def stopp = (page*GR_PAGE_SIZE);
		
	   if(stopp > refList.size())
			   stopp = refList.size();
	   log.debug("page: $page, total pages: "+Math.ceil(refList.size()/GR_PAGE_SIZE));
	   log.debug("Reflist subset: "+refList.subList(startp,stopp).size());
//	   page++;
	   render(contentType:"text/xml"){
		   "response"(page:"$page",numPages: ""+(Integer)Math.ceil(refList.size()/GR_PAGE_SIZE)){
				for(i in refList.subList(startp,stopp))
					"integer"(i)
		   }
	   }
	   
   }
   
   
   
   
   def listOfGranuleReferences = {
	   def DatasetId
	   def Dataset d = resolveId(params.id);
	   if(d==null)
	   {
		  log.debug("404, I'm afraid i can't do that, Dave.")
			  response.status = 404 //Not Found
			  render "Dataset: ${params.id} not found."
			  return
	   }
	   def refList
	   try{
		   refList= inventoryService.getGranuleReferences(d.getDatasetId())
	   }
	   catch(Exception e){
		   log.dbeug("Error fetching granuleRef List: " + e.getMessage());
		   response.status = 500 //Not Found
		   render "error: " + e.getMessage();
		   return;
		   
	   }
	   //refList.sort()
	   def page =null;
	   if(params.page==null)
	   	page = 1;
	   else{
		   try{
			   page = Integer.valueOf(params.page)
		   }catch(Exception e){
		   		log.debug("Exception parsing page number: ${e.getMessage()}. Setting page=1")
				   page=1;
		   }
	   }
	   def start = (page-1)*GR_REF_PAGE_SIZE;
	   def stop = (page*GR_REF_PAGE_SIZE);
	    
	   if(stop > refList.size())
	   		stop = refList.size();
	   log.debug("page: $page, total pages: "+Math.ceil(refList.size()/GR_REF_PAGE_SIZE));
	   log.debug("Reflist subset: "+refList.subList(start,stop).size());
//	   page++;
	   render(contentType:"text/xml"){
		   "response"(page:"$page",numPages: ""+(Integer)Math.ceil(refList.size()/GR_REF_PAGE_SIZE)){
				for(gr in refList.subList(start,stop))
		   		 "granuleReference"{
						granuleId(gr.getGranuleId())
						description(gr.getDescription())
						status(gr.getStatus())
						path(gr.getPath())
						type(gr.getType())
					}
		   }
	   }
	   
	   //def ret = renderReturnResult(refList.subList(start,stop))
	   
	   return;
	   
	   //items.attribute("page","$page");
	   //items.attribute("numPages",Math.ceil(refList.size()/500));
	   //render ret;
   }
   
   def sizeOfGranule = {
	def DatasetId
        Dataset d = resolveId(params.id);
        if(d==null)
        {
	       log.debug("404, I'm afraid i can't do that, Dave.")
               response.status = 404 //Not Found
               render "Dataset: ${params.id} not found."
               return
        }
	
	Integer size = inventoryService.sizeOfGranule(d.getDatasetId())
	if(size == null){
		response.status = 404 //Not Found
                render "No granule size information found for dataset ${params.id}"
	}
	render(contentType:"text/xml"){
        "response"{
             "int"(name:"sizeOfGranule",size)  
           }
        }
	return

   }

   def echoGranules = {
	   Dataset d = resolveId(params.id);
	   if(d==null)
	   {
			  log.debug("404, I'm afraid i can't do that, Dave.")
			  response.status = 404 //Not Found
			  render "Dataset: ${params.id} not found."
			  return
	   }
	    def listing = inventoryService.fetchEchoGranules(d.getDatasetId())
		response.status=200
		renderResult(listing)
		return
   }
   
   def latestGranule = {
	Dataset d = resolveId(params.id);
        if(d==null)
        {
               log.debug("404, I'm afraid i can't do that, Dave.")
               response.status = 404 //Not Found
               render "Dataset: ${params.id} not found."
               return
        }
	Integer gId = inventoryService.fetchLatestGranule(d.getDatasetId())
	if(gId == null)
	{
	        response.status = 404 //Not Found
                render "No granule size information found for dataset ${params.id}"
        }
        render(contentType:"text/xml"){
        "response"{
             "int"(name:"latestGranule",gId)
           }
        }

   }


   def list = {
	def list = inventoryService.fetchDatasetList()

	if(params.useJson.equals("true") || params.json.equals("1"))
        {
          def pList = []
          for(d in list)
          {
                def dataset = [id:d.getDatasetId(), shortname:d.getShortName(), persistentId:d.getPersistentId()]
                pList << dataset
          }
                def items = [datasets: pList]
                render items as JSON
           return
        }



	render(contentType:"text/xml"){
   	"items"{
      	    for(d in list){
             "dataset"{
               "id"(d.getDatasetId())
	       "short-name"(d.getShortName())
               "persistent-id"(d.getPersistentId())
             }
           }        
        }
  	}
	return
   }

   def coverage = {
		log.debug("Dataset Coverage")
		def datasetId
		Dataset d = resolveId(params.id);
	        if(d==null)
                {               log.debug("404, I'm afraid i can't do that, Dave.")
                                response.status = 404 //Not Found
                                render "Dataset: ${params.id} not found."
                                return
                }
 	
		DatasetCoverage coverage = null;
		coverage = inventoryService.fetchDatasetCoverage(d.getDatasetId());
  		renderResult(coverage);
		return;
   }

   def resolveId = { dId ->
	log.debug("resolveId, dataset: $dId")
	Dataset d = null
	def DatasetId
	        try{
                  DatasetId = Integer.valueOf(params.id);
                  d = inventoryService.fetchDatasetById(DatasetId)
                  return d;
                }
                catch(java.lang.NumberFormatException e){ /*must be a string we got...*/ }
                DatasetId = params.id
                d = inventoryService.fetchDatasetByPersistentId(DatasetId);
                if(d== null){
                    d = inventoryService.fetchDatasetByShortName(DatasetId);
                }
                return d;
   }

   
}
