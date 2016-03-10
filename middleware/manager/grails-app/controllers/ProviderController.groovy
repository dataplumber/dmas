/*
import grails.converters.JSON

import gov.nasa.podaac.inventory.api.Query
import gov.nasa.podaac.inventory.api.QueryFactory
import gov.nasa.podaac.inventory.model.Provider
*/
class ProviderController {
	/*
  def inventoryService
  
  def index = {
    def messages = []
    
    def providers = inventoryService.getProviders()
    providers.each {provider ->
      def message = [:]
      message['id'] = provider['id']
      message['type'] = 'Provider'
      message['importance'] = 'Low'
      message['shortName'] = provider['shortName']
      message['longName'] = provider['longName']
      message['type'] = provider['type']
      message['message'] = "Provider"
      messages.add(message)
    }
    
    def result = ['items': messages]
    render result as JSON
  }
  
  def update = {
    def parameters = [:]
    parameters['id'] = (params.id) ? Integer.parseInt(params.id) : null
    parameters['shortName'] = params.shortName
    parameters['longName'] = params.longName
    parameters['type'] = params.type
    
    boolean success = false
    String message = ''
    try {
      inventoryService.updateProvider(parameters)
      
      success = true
      message = 'Provider updated.'
    } catch(Exception exception) {
      log.debug("Failed to update provider.", exception)
      
      message = 'Failed to update provider.'
    }
    
    def messages = []
    if(success) {
      messages.add(['type': 'ProviderUpdate', 'result': 'OK', 'message': message])
    } else {
      messages.add(['type': 'ProviderUpdate', 'result': 'FAILED', 'message': message])
    }
    
    def result = ['items': messages]
    render result as JSON
  }
  */
}
