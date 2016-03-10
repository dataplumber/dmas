import java.net.URLDecoder;
import groovy.xml.MarkupBuilder

import gov.nasa.podaac.common.api.metadatamanifest.MetadataManifest
import gov.nasa.podaac.common.api.metadatamanifest.Constant.ActionType
import gov.nasa.podaac.common.api.metadatamanifest.Constant.ObjectType
import gov.nasa.podaac.inventory.exceptions.InventoryException

class ManifestController {

	def inventoryService
	def authenticationService
	
    def index = { }

    def processManifestDMT = {
	def result = [:]
	result.Status=200
	result.Content=""
	result.Description=""

               log.debug("Processing Manifest")
                if(!authenticationService.authenticate(params.userName, params.password)){
                                response.status =  404
                                render "You're not authorized to update the granule status"
                                return
                        }
            log.debug("manifest received: "+params.manifest)
        def manifestXml = params.manifest

        def requestMetadataManifest = new MetadataManifest(manifestXml)
        def actionType = requestMetadataManifest.getActionType()
                log.debug("Manifest Action: $actionType")
        try{
                def retMani  = inventoryService.processManifest(requestMetadataManifest,params.userName);
                result.Status =  201
		result.Content = retMani.generateXml();
                //render(text: retMani.generateXml(), contentType: "application/xml", encoding: "UTF-8")
                //return
        }catch(InventoryException ex)
        {
		result.Description = ex.getMessage();
                result.Sstatus =  401
        }
	def stringWriter = new StringWriter()
	def markupBuilder = new MarkupBuilder(stringWriter)
      	markupBuilder.Response() {
         result.each {key, value ->
            "$key"(value)
         }
	}	
	render(text: stringWriter.toString(), contentType: "application/xml", encoding: "UTF-8")
      }


    def processManifest = { 
	
		log.debug("Processing Manifest")
		if(!authenticationService.authenticate(params.userName, params.password)){
				response.status =  404
				render "You're not authorized to update the granule status"
				return
			}
	    log.debug("manifest received: "+params.manifest)
        def manifestXml = params.manifest

        def requestMetadataManifest = new MetadataManifest(manifestXml)
        def actionType = requestMetadataManifest.getActionType()
		log.debug("Manifest Action: $actionType")
	try{	
		def retMani  = inventoryService.processManifest(requestMetadataManifest,params.userName);
		response.status =  201
		render(text: retMani.generateXml(), contentType: "application/xml", encoding: "UTF-8")
		return
	}catch(InventoryException ex)
	{
		response.status =  401
                render(text: ex.getMessage(), contentType: "application/xml", encoding: "UTF-8")
                return
	}
    }
}
