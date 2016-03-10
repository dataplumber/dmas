import grails.util.JSonBuilder
import java.net.URLDecoder;
import groovy.xml.MarkupBuilder
import grails.converters.*
import gov.nasa.podaac.inventory.model.Source

class SourceController {

	def inventoryService
	
    def index = { }

    def list = {
		log.debug("list Sources")
	 	def sources = inventoryService.fetchSources()	
	def cType = "application/xml"
	
	if(params.useJson.equals("true") || params.json.equals("1"))
	{
	  def pList = []
          for(s in sources)
	  {
		def source = [id:s.getSourceId(), shortname:s.getSourceShortName(), longname:s.getSourceLongName()]
		pList << source
	  }
		def items = [sources: pList]
		render items as JSON
	   return
	}
	
	render(contentType:"text/xml"){
        "items"{
            for(s in sources){
             "source"{
               "id"(s.getSourceId())
               "shortName"(s.getSourceShortName())
               "longName"(s.getSourceLongName())
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



}
