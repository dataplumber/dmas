import gov.nasa.podaac.common.api.serviceprofile.ServiceProfile;
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfileFactory;

class SipController {

	def inventoryService
	def authenticationService
	
    def index = { }

    def addSip = {
	log.debug("Processing Sip")
	if(!authenticationService.authenticate(params.userName, params.password)){
            response.status =  404
            render "You're not authorized to update the granule status"
            return
        }
	ServiceProfile serviceProfile =
			ServiceProfileFactory.getInstance().
			createServiceProfileFromMessage(params.sip);

	//
//	response.sendError(201,'Done')
//	return
	
	//
	//println "Request: " + request.XML.text()
	def start = new Date().getTime();
			serviceProfile = inventoryService.ingestSip(serviceProfile)
	def stop = new Date().getTime();
	log.debug("StoreSip took " + (stop-start)/1000 + " seconds to complete")
	
	response.status =  201
	render(text: serviceProfile.toString(), contentType: "application/xml", encoding: "UTF-8")
//        response.sendError(201,serviceProfile.toString());
    }
}
