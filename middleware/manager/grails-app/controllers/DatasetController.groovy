/**
 * DatasetController
 */
import grails.util.JSonBuilder
import java.net.URLDecoder;
import groovy.xml.MarkupBuilder

import gov.nasa.podaac.common.api.metadatamanifest.MetadataManifest
import gov.nasa.podaac.common.api.metadatamanifest.Constant.ActionType
import gov.nasa.podaac.common.api.metadatamanifest.Constant.ObjectType

/*
import gov.nasa.podaac.inventory.api.Query
import gov.nasa.podaac.inventory.api.QueryFactory
import gov.nasa.podaac.inventory.model.Dataset
*/

class DatasetController {
   def datasetService
   def inventoryService
   
   def index = {
      redirect(action: authenticate)
   }

   def authenticate = {
      Map result = datasetService.authenticate(params.userName, params.password)

      def stringWriter = new StringWriter()
      def markupBuilder = new MarkupBuilder(stringWriter)
      markupBuilder.Response() {
         result.each {key, value ->
            "$key"(value)
         }
      }

      render(text: stringWriter.toString(), contentType: "application/xml", encoding: "UTF-8")
   }

   def process = {
      Map result = [:]
      
      Map authResult = datasetService.authenticate(params.userName, params.password)
      result.Status = authResult.Status
      result.Content = ""
      
      if(DatasetService.RESPONSE_ERROR.equals(authResult.Status)) {
         result.Description = authResult.Description
      } else {
         try {
            //def manifestXml = '<dataset type="list"></dataset>'
            log.debug("manifest received: "+params.manifest)
            def manifestXml = params.manifest
            //def manifestXml = URLDecoder.decode(params.manifest, "UTF-8")
            log.debug("manifest decoded: "+manifestXml)
            
            def requestMetadataManifest = new MetadataManifest(manifestXml)
            def actionType = requestMetadataManifest.getActionType()
            
			log.debug("manifest from MetadataManifest generateXml(): " + requestMetadataManifest.generateXml())
			
            // test
            /*
            if(actionType.equalsIgnoreCase(ActionType.UPDATE.toString())) {
               requestMetadataManifest = new MetadataManifest(MANIFEST)
               log.debug("using faked one!")
            }
            */
            
            String previousDatasetShortName = null
            /*
            if(actionType.equalsIgnoreCase(ActionType.UPDATE.toString())) {
               def manifestField = requestMetadataManifest.getFields().find{it.getName().equalsIgnoreCase("dataset_datasetId")}
               int datasetId = Integer.parseInt(manifestField.getValue())
               previousDatasetShortName = datasetService.getDatasetShortName(datasetId)
            }
            */
            
            def resultMetadataManifest = inventoryService.processMetadataManifest(requestMetadataManifest)
            if((actionType.equalsIgnoreCase(ActionType.UPDATE.toString())) ||
               (actionType.equalsIgnoreCase(ActionType.CREATE.toString()))) {
               def objectType = requestMetadataManifest.getObjectType()
               if(objectType.equalsIgnoreCase(ObjectType.DATASET.toString())) {
                  datasetService.processDatasetRequest(params.userName, requestMetadataManifest, previousDatasetShortName)
               }
            }
            
            result.Content = resultMetadataManifest.generateXml()
            result.Description = "Success"
               
            log.debug("response: "+result.Content)
         } catch(Exception exception) {
            log.error("Exception in process(): ", exception)
            
            def exceptionMessage = new StringBuilder()
            exceptionMessage.append(exception.getClass().toString()+": "+exception.getMessage()+"\n")
            for(StackTraceElement ste : exception.getStackTrace()) {
               exceptionMessage.append("\t"+ste.toString())
            }
            
            result.Status = DatasetService.RESPONSE_ERROR
            result.Description = exceptionMessage.toString()
         }
      }

      def stringWriter = new StringWriter()
      def markupBuilder = new MarkupBuilder(stringWriter)
      markupBuilder.Response() {
         result.each {key, value ->
            "$key"(value)
         }
      }

      render(text: stringWriter.toString(), contentType: "application/xml", encoding: "UTF-8")
      
      //render(text: "<debugging />", contentType: "application/xml", encoding: "UTF-8")
   }
}
