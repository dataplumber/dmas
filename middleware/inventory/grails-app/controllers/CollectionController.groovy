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
import gov.nasa.podaac.inventory.model.Contact
import gov.nasa.podaac.inventory.model.Collection
import gov.nasa.podaac.inventory.model.Dataset
import gov.nasa.podaac.inventory.model.CollectionContact;
import gov.nasa.podaac.inventory.model.CollectionProduct;
import gov.nasa.podaac.inventory.model.CollectionLegacyProduct
import gov.nasa.podaac.inventory.model.CollectionDataset

class CollectionController {

	def authenticationService
	def inventoryService
	
	private void renderCollection(Collection c){
		render(contentType:"text/xml"){
			"collection"{
					"id"(c.getCollectionId())
					"shortName"(c.getShortName())
					"longName"(c.getLongName())
					"aggregate"(c.getAggregate())
					"type"(c.getType())
					"description"(c.getDescription())
					"fullDescription"(c.getFullDescription())
					"datasets"{
						for(CollectionDataset cd : c.getCollectionDatasetSet()){
							"dataset"(id:cd.getCollectionDatasetPK().getDataset().getDatasetId(), granuleRange360:cd.getGranuleRange360())
						}
					}
					"legacyProducts"{
						for(CollectionLegacyProduct clp : c.getCollectionLegacyProductSet()){
							"legacyProduct"(clp.getLegacyProductId())
						}
					}
					"collectionProduct"{
						def cp =  c.getCollectionProduct();
						"productId"(cp.getProductId())
						"visibleFlag"(cp.getVisibleFlag())
						"echoSubmitTime"(cp.getEchoSubmitDateLong())
						"gcmdSubmitTime"(cp.getGcmdSubmitDateLong())
					}
					"collectionContacts"{
						for(CollectionContact cc : c.getCollectionContactSet()){
							"contact"{
								Contact con = cc.getCollectionContactPK().getContact()
								"contactId"(con.getContactId())
								"first"(con.getFirstName())
								"middle"(con.getMiddleName())
								"last"(con.getLastName())
								"role"(con.getRole())
								"phone"(con.getPhone())
								"email"(con.getEmail())
								"provider"(con.getProvider().getLongName())
							}
						}
					}
			}
		}
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
	
	def resolveDatasetId = { dId ->
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
	
	def collectionByDataset = {  
		Dataset d = resolveDatasetId(params.id);
		if(d == null){
			log.debug("404, I'm afraid i can't do that, Dave.")
			response.status = 404 //Not Found
				  render "Dataset: ${params.id} not found."
			return
		}
		Collection c = null
		d.getCollectionDatasetSet().each { 
			
			if(c == null){
				c = it.getCollectionDatasetPK().getCollection();
				
			 }
			 else{
				 if(c.getCollectionId() < it.getCollectionDatasetPK().getCollection().getCollectionId()){
					 c = it.getCollectionDatasetPK().getCollection()
				 }
			 }
		
		}
		response.status=200
		renderCollection(c)
		
	}
	
	def resolveId = { cId ->
		log.debug("resolveId, collection: $cId")
		Collection c = null
		def collectionId
			    try{
				  collectionId = Integer.valueOf(cId);
				  c = inventoryService.fetchCollectionById(collectionId)
				  return c;
				}
				catch(java.lang.NumberFormatException e){ /*must be a string we got...*/ }
				collectionId = cId
				c = inventoryService.fetchCollectionByShortName(collectionId);
				
				return c;
			}
	
	def show = { 
		log.debug("In show Collection: ${params.id}")
		Collection c = resolveId(params.id)
		if(!c){
				log.debug("404, I'm afraid i can't do that, Dave.")
				response.status = 404 //Not Found
					  render "Collection: ${params.id} not found."
				return
		}
		log.debug("Datasets:")
		c.getCollectionDatasetSet().each { 
			log.debug("DatasetId: ${it.getCollectionDatasetPK().getDataset().getDatasetId()}")
			 }
		response.status=200
		renderCollection(c)
			
	}
	
	def updateCollectionProduct = {
		if(!authenticationService.authenticate(params.userName, params.password)){
			response.status =  409
			render "You're not authorized to update the granule status"
			return
		}
		try{
			CollectionProduct cp = new CollectionProduct();
		cp.setCollectionId(params.collectionId as int)
		cp.setProductId(params.productId)
		cp.setVisibleFlag(params.visibleFlag)
		cp.setEchoSubmitDateLong(params.echoDate as long)
		cp.setGcmdSubmitDateLong(params.gcmdDate as long)
		
		inventoryService.updateCollectionProduct(cp);
		}catch(Exception e){
			response.status = 400;
			render "Error processing request: "+e.getMessage()
		}
		response.status = 200
		return
	}
	
	def list = {
		log.debug("In collectionList")
		def lst = inventoryService.fetchCollectionList();
		if(params.useJson.equals("true") || params.json.equals("1"))
		{
		  def pList = []
		  for(c in lst)
		  {
				def col= [id:c.getCollectionId(), shortname:c.getShortName()]
				pList << col
		  }
				def items = [collections: pList]
				render items as JSON
		   return
		}

	render(contentType:"text/xml"){
	   "items"{
			  for(c in lst){
			 "collection"{
			   "id"(c.getCollectionId())
		   "shortName"(c.getShortName())
			 }
		   }
		}
	  }
	}
	
	
}
