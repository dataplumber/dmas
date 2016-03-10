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
import gov.nasa.podaac.inventory.model.CollectionContact;
import gov.nasa.podaac.inventory.model.CollectionLegacyProduct
import gov.nasa.podaac.inventory.model.CollectionDataset

class ContactController {

	def authenticationService
	def inventoryService
	
	private void renderContact(Contact c){
		render(contentType:"text/xml"){
			
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
	
	def resolveId = { cId ->
		log.debug("resolveId, contact: $cId")
		Contact c = null
		def contactId
			    try{
				  contactId = Integer.valueOf(cId);
				  c = inventoryService.fetchContactById(contactId)
				  return c;
				}
				catch(java.lang.NumberFormatException e){ /*must be a string we got...*/ }
				return null
			}
	
	def show = { 
		log.debug("In show Contact: ${params.id}")
		Contact c = resolveId(params.id)
		if(!c){
				log.debug("404, I'm afraid i can't do that, Dave.")
				response.status = 404 //Not Found
					  render "Contact: ${params.id} not found."
				return
		}
		response.status=200
		renderContact(c)
	}
}
