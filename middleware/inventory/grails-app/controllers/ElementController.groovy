import grails.util.JSonBuilder
import java.net.URLDecoder;
import groovy.xml.MarkupBuilder
import grails.converters.*

class ElementController {

	def inventoryService
	
	def list = {
		
	}
	
	def show = {
		def id
		def ele
		try{
			id = params.id as int
			ele = inventoryService.findElementDDById(id)
		}catch(Exception e){
			//must be  a string, find by name
			ele = inventoryService.findElementDDByShortName(params.id)
		}
		
		if(!ele){
			response.status=404
			return
		}	
		
		response.status=200
		renderResult(ele)
		return
				
	}
	
	private void renderResult(Object theObject){
		if(theObject == null){
		  theObject = []
		}
		 
		  if(params.json.equals("1") || params.useJson.equals("true"))
				 render theObject as JSON
		  else
				 render theObject as XML
	}
	
}
