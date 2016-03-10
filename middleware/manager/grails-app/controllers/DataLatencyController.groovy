/*
import java.sql.Timestamp

import grails.converters.JSON

import gov.nasa.podaac.common.api.util.DateTimeUtility
import gov.nasa.podaac.inventory.api.Query
import gov.nasa.podaac.inventory.api.QueryFactory
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.DatasetPolicy;
*/
class DataLatencyController {
	/*
  private static final Map IMPORTANCE = [
    High: "High",
    Medium: "Medium",
    Low: "Low"
  ]
  private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
  def inventoryService
  
  def index = {
    def messages = []
    List datasetStatusList = inventoryService.getDatasetStatusList()
    for(Map datasetStatus : datasetStatusList) {
      // check data latency
      String overdue = 'None'
      String importance = IMPORTANCE.Low
      String message = 'On time.'
      Date lastIngestTimeDate = null
      
      Integer dataFrequency = datasetStatus['dataFrequency']
      if(dataFrequency) {
        Date lastIngestTime = datasetStatus['lastIngestTime']
        if(lastIngestTime) {
          lastIngestTimeDate = new Date(lastIngestTime.getTime())
          Date currentTime = datasetStatus['currentTime']
          
          long timeElapsedInSecond = (currentTime.getTime() - lastIngestTimeDate.getTime()) / 1000
          long dataFrequencyInSecond = (dataFrequency * 60 * 60)
          if(timeElapsedInSecond > dataFrequencyInSecond) {
            overdue = String.format("%.2f", ((timeElapsedInSecond - dataFrequencyInSecond) / (60.0f * 60.0f)))+" hours"
            importance = IMPORTANCE.High
            message = 'Overdue!'
          }
        } else {
          importance = IMPORTANCE.Medium
          message = 'No products found for this data stream.'
        }
      }
      
      def entry = [
        'id': datasetStatus['id'],
        'type': 'DataLatency',
        'importance': importance,
        'dataStream': datasetStatus['dataStream'],
        'message': message,
        'lastIngested': (lastIngestTimeDate) ? lastIngestTimeDate.getTime() : "",
        'overdue': overdue
      ]
      messages.add(entry)
    }

    def result = ['items': messages]
    render result as JSON
  }
  
  def update = {
    def categories = ['dataset', 'provider', 'locationPolicy', 'policy']
    
    def names = params.keySet().findAll {key ->
      categories.find{key.startsWith(it)}
    }
    
    def values = [:]
    categories.each {category ->
      def targetNames = names.findAll {it.startsWith(category)}
      
      def map = [:]
      targetNames.each {targetName ->
        map[targetName.substring(category.length())] = params[targetName]
        //log.debug(targetName.substring(category.length())+": "+params[targetName])
      }
      values[category] = map
    }
    
    if(params.id) {
      values['dataset'].id = Integer.parseInt(params.id)
    }
    
    
    boolean success = true
    String message = ''
    try {
      inventoryService.updateDataset(values)
      
      message = 'OK'
    } catch(Exception exception) {
      log.debug("Failed to update dataset.", exception)
      
      success = false
      message = 'FAILED'
    }
    
    def messages = []
    if(success) {
      messages.add(['type': 'DatasetUpdate', 'result': 'OK', 'message': message])
    } else {
      messages.add(['type': 'DatasetUpdate', 'result': 'FAILED', 'message': message])
    }
    
    def result = ['items': messages]
    render result as JSON
  }
  */
}
