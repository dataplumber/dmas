import grails.util.JSonBuilder
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Set;

import groovy.xml.MarkupBuilder
import grails.converters.*

import gov.nasa.podaac.inventory.model.Collection;
import gov.nasa.podaac.inventory.model.Dataset
import gov.nasa.podaac.inventory.model.DatasetElement
import gov.nasa.podaac.inventory.model.Granule
import gov.nasa.podaac.inventory.model.GranuleArchive;
import gov.nasa.podaac.inventory.model.GranuleCharacter;
import gov.nasa.podaac.inventory.model.GranuleSIP;
import gov.nasa.podaac.inventory.model.GranuleContact;
import gov.nasa.podaac.inventory.model.GranuleDateTime;
import gov.nasa.podaac.inventory.model.GranuleInteger;
import gov.nasa.podaac.inventory.model.GranuleMetaHistory
import gov.nasa.podaac.inventory.model.GranuleReal;
import gov.nasa.podaac.inventory.model.GranuleReference;
import gov.nasa.podaac.inventory.model.GranuleSIP;
import gov.nasa.podaac.inventory.model.GranuleSpatial;

import com.vividsolutions.jts.io.WKTWriter

import gov.nasa.podaac.inventory.api.Constant.GranuleStatus


class GranuleController {

    def inventoryService;
    def authenticationService;


    def index = { }

//   def authenticate = {
//	log.debug "Authenticating"
//	if(params.userName == null || params.password == null)
//		return false;
//      
//	Map result = authenticationService.authenticate(params.userName, params.password)
//	if(result.status == "Error")
//	{		
//	   log.debug "Retunring False"
//  	   return false;
//	}
//        return true 
//   }

	
	private void renderGranule(Granule g){
		render(contentType:"text/xml"){
			"granule"{
				
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
					
					"granuleSIPSet"{
						g.getGranuleSIPSet().each {
							GranuleSIP gsip = it;
							"granuleSip"{ 
								"sip"(gsip.getSip())
							}
						}
					}
					"granuleCharacterSet"{
						GranuleCharacter gc = null
						g.getGranuleCharacterSet().each {  
							gc = it
							"granuleCharacter"{
								DatasetElement de = gc.getDatasetElement(); 
								"datasetElement"(id:de.getDeId()){
									"deId"(de.getDeId())
									"dataset"(id:de.getDataset().getDatasetId())
									"elementDD"(id:de.getElementDD().getElementId())
									"scope"(de.getScope())
									"obligationFlag"(de.getObligationFlag())
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
								"datasetElement"(id:de.getDeId()){
									"deId"(de.getDeId())
									"dataset"(id:de.getDataset().getDatasetId())
									"elementDD"(id:de.getElementDD().getElementId())
									"scope"(de.getScope())
									"obligationFlag"(de.getObligationFlag())
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
								DatasetElement de = gc.getDatasetElement(); 
								"datasetElement"(id:de.getDeId()){
									"deId"(de.getDeId())
									"dataset"(id:de.getDataset().getDatasetId())
									"elementDD"(id:de.getElementDD().getElementId())
									"scope"(de.getScope())
									"obligationFlag"(de.getObligationFlag())
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
								"datasetElement"(id:de.getDeId()){
									"deId"(de.getDeId())
									"dataset"(id:de.getDataset().getDatasetId())
									"elementDD"(id:de.getElementDD().getElementId())
									"scope"(de.getScope())
									"obligationFlag"(de.getObligationFlag())
								}
								"value"(gc.getValueLong())
							}	
						}
					}
					"granuleSpatialSet"{
						GranuleSpatial gc = null
						g.getGranuleSpatialSet().each {
							gc = it
							"granuleSpatial"{
								DatasetElement de = gc.getDatasetElement();
								"datasetElement"(id:de.getDeId()){
									"deId"(de.getDeId())
									"dataset"(id:de.getDataset().getDatasetId())
									"elementDD"(id:de.getElementDD().getElementId())
									"scope"(de.getScope())
									"obligationFlag"(de.getObligationFlag())
								}
								
								WKTWriter wr = new WKTWriter();
								def wkt = wr.write(gc.getValue());
								"value"(type:"wkt",wkt)
							}
						}
					}
					"granuleArchiveSet"{
						g.getGranuleArchiveSet().each {
							GranuleArchive garch = it
							"granuleArchive"{
								"checksum"(garch.getChecksum())
								"compressFlag"(garch.getCompressFlag())
								"fileSize"(garch.getFileSize())
								"name"(garch.getName())
								"status"(garch.getStatus())
								"type"(garch.getType())
							}	
						}	
					}
					"granuleReferenceSet"{
						g.getGranuleReferenceSet().each {
							GranuleReference garch = it
							"granuleReference"{
								"description"(garch.getDescription())
								"path"(garch.getPath())
								"status"(garch.getStatus())
								"type"(garch.getType())
							}	
						}	
					}
					"metaHistorySet"{
						
						g.getMetaHistorySet().each { 
							GranuleMetaHistory gmh = (GranuleMetaHistory)it
							"granuleMetaHistory"{
								"metaHistoryPK"{
									"versionId"(gmh.getMetaHistoryPK().getVersionId())
								}
								"creationDateLong"(gmh.getCreationDateLong())
								"lastRevisionDateLong"(gmh.getLastRevisionDateLong())
								"revisionHistory"(gmh.getRevisionHistory())
								"echoSubmitDateLong"(gmh.getEchoSubmitDateLong())
							}
						}
					}
					"granuleContactSet"{}
					"dataset"(id:g.getDataset().getDatasetId());
			}
		}
	}
	
	//lists granules matching the criteria
	def list = {
		//params //start, //stop, // compareField (ingest or archive) //dsId 
		def lst = null
		try{
			def start = params.start as long;
			def stop = params.stop as long;
			def field = params.compareField.toUpperCase()
			def dataset = params.dataset as Integer
			
			if(field.contains("INGEST"))
				field = "INGEST_TIME_LONG"
			if(field.contains("ARCHIVE"))
				field = "ARCHIVE_TIME_LONG"
				
			lst = inventoryService.listGranuleByDateRange(start,stop,field,dataset)
		}catch(Exception e){
			response.status = 400
			render "Error processing request: " + e.getMessage()
			return
		}
		
		
		response.status = 200;
		render(contentType:"text/xml"){
        "response"{
			for(Granule g: lst){
             "granule"(dataset:g.getDataset().getDatasetId(),g.getGranuleId())
			}
           }
        }
	}
	
	def findGranuleList = {
		def ids = params.ids;
		def list = []
		
		ids.split(',').each {  
			list.add(it);
			}
		
		log.debug("size of list: " + list.size);
		log.debug("resolveId list: $ids");
		def gIdList = inventoryService.findGranuleList(list);
		
			renderResult(gIdList);
		
	}
	def findGranuleAIPList = {
		def ids = params.ids;
		def list = []
		
		ids.split(',').each {
			list.add(it);
			}
		
		log.debug("size of list: " + list.size);
		log.debug("resolveId list: $ids");
		def gIdList = inventoryService.getArchiveAIPByGranuleList(list);
		renderResult(gIdList);
	} 

    private void renderResult(Object theObject){
       if(theObject == null){
		 theObject = []    
       }
		
	   //XML.use("deep")
		 if(params.json.equals("1") || params.useJson.equals("true"))
                render theObject as JSON
         else
                render theObject as XML
   }

	
	
    def resolveId = { gId, dId -> 
	log.debug("Request URI: " + request.getRequestURI())	
	log.debug("resolveId granule: $gId, dataset: $dId")
	def granuleId;
	Granule g;
	try{
	    granuleId = Integer.valueOf(gId)
	    g = inventoryService.fetchGranuleById(granuleId)
	    return g;
	}catch(NumberFormatException nfe){
		log.debug("Granule id is a string...")
	}
	gId = gId.replace(' ','+');
	log.debug("searching for $gId");
	
	if(dId != null){
		Dataset d = resolveDatasetId(dId)
		if(d==null)
		{
		   response.status=404
		   render "Dataset not found, datasetId: $dId"
		   return
		}
		g = inventoryService.fetchGranuleByNameAndDataset(gId,d);		
	}else
                g = inventoryService.fetchGranuleByName(gId);
	return g;
    }

	def updateEchoSubmitTime = {
		try{
			if(!authenticationService.authenticate(params.userName, params.password)){
				response.status =  409
				render "You're not authorized to update the granule status"
				return
			}
			if(params.echoTime == null)
			{
				response.status 400
				render "You must set the echo submit time [echoTime]"
				return
			}
			Granule g = resolveId(params.id,null)
			g.getMetaHistorySet().each { gmh ->
				//GranuleMetaHistory gmh;
				gmh.setLastRevisionDateLong(params.echoTime as long)
				gmh.setEchoSubmitDateLong(params.echoTime as long)
			
			}
			render "granule updated"		
			response.status = 200
			return
		}catch(Exception e){
			response.status=400
			render "Error submitting request: " + e.getMessage()
		}

	}
	
    def updateGranuleStatus = {
	log.debug("updateGranuleStatus gId: ${params.id}, status:${params.status}") 
	if(!authenticationService.authenticate(params.userName, params.password)){
	    response.status =  409
	    render "You're not authorized to update the granule status"
	    return
	}
	if(params.status == null)
	{
		response.status 400
		render "You must set the granule status"
		return
	}
	def status
	try{
		status= GranuleStatus.valueOf(params.status)
	}catch(IllegalArgumentException iae){
		response.status= 400
                render "You must set a valid granule status"
                return
	}

	Granule g =  resolveId(params.id, params.dataset)
        if(g==null)
        {
		log.debug("Granule Not Found")
                response.status = 404
                render "Granule id not found granuleId: ${params.id}"
                return;
        }
	inventoryService.updateGranuleStatus(g.getGranuleId(), status)
	response.status=200
	render "Granule status changed to $status"

    }   



   def fetchArchivePath = {
	def granuleId
	 try{
                granuleId = Integer.valueOf(params.id)		
		}
                catch(java.lang.NumberFormatException e){ /*must be a string we got...*/ 
			response.status = 400
			render "Granule Id must be an integer, you supplied \"${params.id}\""
			return
		}
	def path = inventoryService.fetchArchivePathByGranuleId(granuleId)
	if(path == null)
	{
		response.status = 404
		render "Granule with granuleId $granuleId was not found."
		return
	}

	render(contentType:"text/xml"){
        "response"{
             "str"(name:"path",path)
           }
        }
	return

   }
	
   def resolveDatasetId = { dId ->
        log.debug("resolveId, dataset: $dId")
        Dataset d = null
        def DatasetId
		if(dId == null)
			return d;
			
                try{
                  DatasetId = Integer.valueOf(dId);
                  d = inventoryService.fetchDatasetById(DatasetId)
                  return d;
                }
                catch(java.lang.NumberFormatException e){ /*must be a string we got...*/ }
                DatasetId = dId
                d = inventoryService.fetchDatasetByPersistentId(DatasetId);
                if(d== null){
                    d = inventoryService.fetchDatasetByShortName(DatasetId);
                }
                return d;
   }

    def updateAIPRef = {
		if(!authenticationService.authenticate(params.userName, params.password)){
			response.status =  409
			render "You're not authorized to update the granule status"
			return
		}
		
		def type = params.type;
		def dest = params.dest;
		def fileName = params.fname;
		def status = params.status;
		
		log.debug("type: $type\ndest: $dest\nfileName:$fileName\nstatus:$status");
		if(type ==null || dest==null || fileName==null || status==null || params.id==null){
			render "Error Processing input."
			response.status =  405
			return
		}
	
		log.debug("updating...");
		
		inventoryService.updateGranuleAIPRef(status,dest,fileName,params.id,type);
		
		response.status=200;
		render "success";

	}
	
	def updateAIPArch = {
		if(!authenticationService.authenticate(params.userName, params.password)){
			response.status =  409
			render "You're not authorized to update the granule status"
			return
		}
		
		def type = params.type;
		def dest = params.dest;
		def fileName = params.fname;
		def status = params.status;
		
		log.debug("type: $type\ndest: $dest\nfileName:$fileName\nstatus:$status");
		if(type ==null || dest==null || fileName==null || status==null || params.id==null){
			render "Error Processing input."
			response.status =  405
			return
		}
	
		log.debug("updating...");
		inventoryService.updateGranuleAIPArch(status,dest,fileName,params.id,type);
		response.status=200;
		render "success";

	}
	 def updateReassociateGranuleElement = {
		 if(!authenticationService.authenticate(params.userName, params.password)){
			 log.debug("error authenticating.");
			 response.status =  409
			 render "You're not authorized to update the granule status"
			 return
		 }
		 def Granule g = resolveId(params.id, null);
		 if(g==null)
		 {
			 render "Granule not found."
			 response.status = 404
			 return;
		 }
		 
		 def fromD = params.from;
		 def toD = params.to;
		 
		 def from = resolveDatasetId(fromD)
		 def to = resolveDatasetId(toD)
		 
		 if(from == null || to == null){
			 render "From or To datasets were incorrectly formed."
			 response.status = 400
			 return;
		 }
		 
		 try{
			 log.debug("calling reassoc GEs");
			 inventoryService.reassociateGranuleElement(g,to,from);
		 }catch(Exception e){
			 render "Error processing requiest: " + e.getMessage();
			 response.status = 500
			 return;
		 }
		 
		 response.status=200;
		 render "success";
		 return;
		 
	 }
	
	def updateReassociateGranule = {
		if(!authenticationService.authenticate(params.userName, params.password)){
			response.status =  409
			render "You're not authorized to update the granule status"
			return
		}
		def Granule g = resolveId(params.id, null);
		if(g==null)
		{
			render "Granule not found."
			response.status = 404
			return;
		}
		def root = params.rootpath;
		def dId = params.dataset;
		def at = params.accessType;

		log.debug("to datasetId: $dId")
		def dataset = resolveDatasetId(dId)
		if(root==null || dataset == null){
			render "Status Variable not supplied."
			response.status = 404
			return;
		}
		
		g.setDataset(dataset)
		g.setRootPath(root);
		
		if(at != null && !at.equals("")){
		  	g.setAccessType(at);
		}

		try{
			inventoryService.updateGranule(g);
		}catch(Exception e){
			render "Error processing requiest: " + e.getMessage();
			response.status = 500
			return;
		}
		
		response.status=200;
		render "success";
		return;
		
	}
	
	def updateGranuleRootPath = {
		if(!authenticationService.authenticate(params.userName, params.password)){
			response.status =  409
			render "You're not authorized to update the granule status"
			return
		}
		def Granule g = resolveId(params.id, null);
		if(g==null)
		{
			render "Granule not found."
			response.status = 404
			return;
		}
		def root = params.rootpath;
		if(root==null){
			render "Status Variable not supplied."
			response.status = 404
			return;
		}
		
		try{
			inventoryService.updateGranuleRootPath(g,root)
		}catch(Exception e){
			render "Error processing requiest: " + e.getMessage();
			response.status = 500
			return;
		}
		
		response.status=200;
		render "success";
		return;
		
	}
	
	//post
	//id,path,status, type,desc?
	def addNewGranuleReference = {
		if(!authenticationService.authenticate(params.userName, params.password)){
			response.status =  409
			render "You're not authorized to update the granule status"
			return
		}
		def Granule g = resolveId(params.id, null);
		
		if(g==null)
		{
			render "Granule not found."
			response.status = 404
			return;
		}
		
		def type = params.type;
		def status = params.status;
		def path = params.path;
		def desc = params.desc;
		
		if(type == null || status == null || path == null){
			render "type Variable not supplied."
			response.status = 400
			return;
		}
		
		try{
			inventoryService.addGranuleReference(g.getGranuleId(), type,status,path,desc);
			
		}catch(Exception e){
			render "Error processing requiest: " + e.getMessage();
			response.status = 500
			return;
		}
		response.status=200;
		render "success";
		return;
	}
	
	def updateGranuleReferencePath = {
		if(!authenticationService.authenticate(params.userName, params.password)){
			response.status =  409
			render "You're not authorized to update the granule status"
			return
		}
		def Granule g = resolveId(params.id, null);
		
		if(g==null)
		{
			render "Granule not found."
			response.status = 404
			return;
		}

		def newpath = params.newpath;
		def path = params.path;
		
		if( newpath == null || path == null){
			render "Variable not supplied."
			response.status = 400
			return;
		}
		try{
			inventoryService.updateGranuleReferencePath(g.getGranuleId(), path,newpath);
		}catch(Exception e){
			render "Error processing requiest: " + e.getMessage();
			response.status = 500
			return;
		}
		response.status=200;
		render "success";
		return;
	}
	//id,path,status
	//post
	def updateGranuleReferenceStatus = {
		//updateGranuleReferenceStatus
		if(!authenticationService.authenticate(params.userName, params.password)){
			response.status =  409
			render "You're not authorized to update the granule status"
			return
		}
		def Granule g = resolveId(params.id, null);
		
		if(g==null)
		{
			render "Granule not found."
			response.status = 404
			return;
		}

		def status = params.status;
		def path = params.path;
		
		if( status == null || path == null){
			render "type Variable not supplied."
			response.status = 400
			return;
		}
		try{
			inventoryService.updateGranuleReferenceStatus(g.getGranuleId(), status,path);
			
		}catch(Exception e){
			render "Error processing requiest: " + e.getMessage();
			response.status = 500
			return;
		}
		response.status=200;
		render "success";
		return;
	}
	
	//delete
	def deleteGranuleReference ={
		if(!authenticationService.authenticate(params.userName, params.password)){
			response.status =  409
			render "You're not authorized to update the granule status"
			return
		}
		def Granule g = resolveId(params.id, null);
		if(g==null)
		{
			render "Granule not found."
			response.status = 404
			return;
		}
		def type = params.type;
		if(type==null){
			render "type Variable not supplied."
			response.status = 400
			return;
		}
		
		try{
			inventoryService.deleteGranuleReference(g,type)
		}catch(Exception e){
			render "Error processing requiest: " + e.getMessage();
			response.status = 500
			return;
		}
		
		response.status=200;
		render "success";
		return;
	}
	
	def updateGranStatusAndVerify = {
		if(!authenticationService.authenticate(params.userName, params.password)){
			response.status =  409
			render "You're not authorized to update the granule status"
			return
		}
		def Granule g = resolveId(params.id, null);
		if(g==null)
		{
			render "Granule not found."
			response.status = 404
			return;
		}
		def status = params.status;
		if(status==null){
			render "Status Variable not supplied."
			response.status = 404
			return;
		}
		try{
			inventoryService.updateGranuleStatusAndVerify(g.getGranuleId(),status)
		}catch(Exception e){
			render "Error processing requiest: " + e.getMessage();
			response.status = 500
			return;
		}
		
		render "Successfull Updated Status";
		response.status = 200;
		return;
	}
	
    def updateArchiveChecksum = {
		if(!authenticationService.authenticate(params.userName, params.password)){
			response.status =  409
			render "You're not authorized to update the granule status"
			return
		}
		def Granule g = resolveId(params.id, null);
		if(g==null)
		{
			render "Granule not found."
			response.status = 404
			return;
		}
		
		def name = params.name;
		def csum = params.checksum;
		
		
		if(name==null || csum==null)
		{
			log.debug("name=$name");
			log.debug("csum=$csum");
			render "Missing data."
			response.status = 400
			return;
		}
		try{
			inventoryService.updateGranuleArchiveChecksum(g, name, csum)
		}catch(Exception e){
			render "Error processing requiest: " + e.getMessage();
			response.status = 500
			return;
		}	
		render "Successfully Updated Checksum."
		response.status = 200;
		return;
	}
	
	def updateArchiveStatus = {
		if(!authenticationService.authenticate(params.userName, params.password)){
			response.status =  409
			render "You're not authorized to update the granule status"
			return
		}
		def Granule g = resolveId(params.id, null);
		if(g==null)
		{
			render "Granule not found."
			response.status = 404
			return;
		}
		
//		def name = params.name;
		def status = params.status;
		
		if(status==null )
		{
			render "Missing data."
			response.status = 400
			return;
		}
		try{
			inventoryService.updateGranuleArchiveStatus(g, status)
		}catch(Exception e){
			render "Error processing requiest: " + e.getMessage();
			response.status = 500
			return;
		}
		render "Successfully Updated Checksum."
		response.status = 200;
		return;
	}
	
	def updateArchiveSize = {
		if(!authenticationService.authenticate(params.userName, params.password)){
			response.status =  409
			render "You're not authorized to update the granule status"
			return
		}
		def Granule g = resolveId(params.id, null);
		if(g==null)
		{
			render "Granule not found."
			response.status = 404
			return;
		}
		
		def name = params.name;
		def fsize = params.fsize;
		
		
		if(name==null || fsize==null)
		{
			render "Missing data."
			response.status = 400
			return;
		}
		
		
		try{
			inventoryService.updateGranuleArchiveSize(g, name, Long.valueOf(fsize))
		}catch(Exception e){
			render "Error processing requiest: " + e.getMessage();
			response.status = 500
			return;
		}
		render "Successfully Updated Checksum."
		response.status = 200;
		return;
	}
	
    def delete = {
		if(!authenticationService.authenticate(params.userName, params.password)){
			response.status =  409
			render "You're not authorized to update the granule status"
			return
		}
		
		Granule g =  resolveId(params.id, null);
		
		log.debug("delete granule " + g.getName());
		def dOnly = false;
		if(params.dataOnly == null || params.dataOnly.equals("false"))
		{
			dOnly=false;
			log.debug("DELETING ALL DATA");	
		}
		else{
			dOnly=true;
			log.debug("DELETING DATA ONLY");
		}
		
		try{
			if(dOnly)
				inventoryService.deleteDataOnly(g);
			else
				inventoryService.deleteDataAndMetadata(g);
				
		}catch(Exception e){
			render "Error Deleting Granule." 
			log.debug("StackTrace: ",e);
			response.status = 500
			return;
		}
			
		render "Successfully Deleted."
		response.status = 200;
		return;
	}
   
   
    def show = {
		
		log.debug("params: $params")	
		
		Granule g =  resolveId(params.id, params.dataset)
		if(g==null)
		{
			response.status = 404
			render "Granule id not found granuleId: ${params.id}"
			return;
		}
		g.getMetaHistorySet().each { GranuleMetaHistory gmh = (GranuleMetaHistory) it;
				gmh.getMetaHistoryPK().setGranule(null)			
			 }
		//work around...
		//g.setGranuleSpatialSet(null)
		//renderResult(g)
		renderGranule(g)
		return;
    }
	
	def spatial = {
		
		Granule g =  resolveId(params.id, params.dataset)
		if(g==null)
		{
			response.status = 404
			render "Granule id not found granuleId: ${params.id}"
			return;
		}
		
		if(g.getGranuleSpatialSet() != null){
			if(!g.getGranuleSpatialSet().isEmpty()){
				//log.debug("Rendering Spatial: " + g.getGranuleSpatialSet())
				g.getGranuleSpatialSet().each {
					log.debug(it.toString())
					String s = it.toString();
					def m = [:]
					WKTWriter wr = new WKTWriter();
					m.wkt = wr.write(it.getValue());
					renderResult(m) }
				response.status=200
				return;
			}
		}
	}
	
	
	def gmh = {
		Granule g =  resolveId(params.id, params.dataset)
		if(g==null)
		{
			response.status = 404
			render "Granule id not found granuleId: ${params.id}"
			return;
		}
		g.getMetaHistorySet().each { GranuleMetaHistory gmh = (GranuleMetaHistory) it;
				gmh.getMetaHistoryPK().setGranule(null)
			 }
		
		renderResult(g.getMetaHistorySet())
		return;
	}

    def info = { 
	println params.id
	response.sendError(200,'Done');
    }

}
