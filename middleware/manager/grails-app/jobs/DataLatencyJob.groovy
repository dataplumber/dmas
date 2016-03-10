/**
 * DataLatencyJob
 */
class DataLatencyJob {
   static triggers = {
      simple name: "DataLatencyJob", startDelay: 60000, repeatInterval: 60000 * 60
   }
   def dataLatencyService
   def concurrent = false
   def sessionRequired = true

   def execute() {
      try {
         def methodTime = null
         
         methodTime = new Date()
         dataLatencyService.checkDataLatency()
         log.debug("[LOOKATTHIS]job-dataLatency: checkDataLatency: "+(new Date().getTime() - methodTime.getTime()))
      } catch(Exception exception) {
         log.debug("Exception in DataLatencyJob.", exception)
      }
   }
}
