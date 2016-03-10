import org.codehaus.groovy.grails.commons.ConfigurationHolder

import gov.nasa.jpl.horizon.sigevent.api.SigEvent
import gov.nasa.jpl.horizon.sigevent.api.SigEventGroup
import gov.nasa.jpl.horizon.sigevent.api.EventType

import gov.nasa.podaac.common.api.util.DateTimeUtility

/**
 * SigEventService
 */
class SigEventService {
   private static final String UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
   boolean transactional = false

   def send(
      String category,
      EventType eventType,
      String description,
      String data,
      String hostname = null
      ) {
      def timeStamp = DateTimeUtility.parseDate(SigEventService.UTC_FORMAT, new Date())
      
      def sigEvent = new SigEvent(ConfigurationHolder.config.horizon_sigevent_url)
      sigEvent.create(
         eventType,
         category,
         ConfigurationHolder.config.manager_federation+" manager",
         ConfigurationHolder.config.manager_federation+" manager",
         ((hostname) ? hostname : InetAddress.localHost.hostName),
         description,
         null,
         data
      )
   }
   
   def createGroup(String category, EventType eventType, long purgeRate) {
      def sigEventGroup = new SigEventGroup(ConfigurationHolder.config.horizon_sigevent_url)
      def response = sigEventGroup.create(eventType, category, purgeRate)
      
      return !response.hasError()
   }
}
