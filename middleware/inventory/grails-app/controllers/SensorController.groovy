import grails.util.JSonBuilder
import java.net.URLDecoder;
import groovy.xml.MarkupBuilder
import grails.converters.*
import gov.nasa.podaac.inventory.model.Sensor

class SensorController {

	def inventoryService
	
    def index = { }

    def list = {
		log.debug("list Sensors")
	 	def sensors = inventoryService.fetchSensors()	
	def cType = "application/xml"
	
	if(params.useJson.equals("true") || params.json.equals("1"))
	{
	  def pList = []
          for(s in sensors)
	  {
		def source = [id:s.getSensorId(), shortname:s.getSensorShortName(), longname:s.getSensorLongName()]
		pList << source
	  }
		def items = [sensors: pList]
		render items as JSON
	   return
	}
	
	render(contentType:"text/xml"){
        "items"{
            for(s in sensors){
             "sensor"{
               "id"(s.getSensorId())
               "shortName"(s.getSensorShortName())
               "longName"(s.getSensorLongName())
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
