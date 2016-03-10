import grails.util.JSonBuilder
import java.net.URLDecoder;
import groovy.xml.MarkupBuilder
import grails.converters.*
import gov.nasa.podaac.inventory.model.Provider

class ProviderController {

	def inventoryService
	
    def index = { }

    def list = {
		log.debug("list Providers")
	 	def providers = inventoryService.fetchProviders()	
	def cType = "application/xml"
	
	if(params.useJson.equals("true") || params.json.equals("1"))
	{
	  def pList = []
          for(p in providers)
	  {
		def provider = [id:p.getProviderId(), shortname:p.getShortName(), longname:p.getLongName()]
		pList << provider
	  }
		def items = [providers: pList]
		render items as JSON
	   return
	}
	
	render(contentType:"text/xml"){
        "items"{
            for(p in providers){
             "provider"{
               "id"(p.getProviderId())
               "shortName"(p.getShortName())
               "longName"(p.getLongName())
             }
           }
        }
        }

	}

    def show = {
		log.debug("show provider ${params.id}")
		def pId
		Provider p
		try{
		  pId = Integer.valueOf(params.id)
		  p = inventoryService.fetchProviderById(pId);
	 	}catch(NumberFormatException nfe){
		  log.debug("Attempting to find provider by name");
                   p = inventoryService.fetchProviderByName(params.id);
		}
		if(p == null)
		{
			response.status = 404
			render("Could not find provider, provider id: $pId")
			return;
		}
		renderResult(p);
		return;			

	}

    private void renderResult(Object theObject){

        if(params.json.equals("1") || params.useJson.equals("true"))
                render theObject as JSON
         else
                render theObject as XML
   }



}
