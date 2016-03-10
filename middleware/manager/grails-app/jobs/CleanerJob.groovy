/**
 * This time tiggered job is designed to move archived products from normal staging directory to
 * the trashbin for future purging.
 */
class CleanerJob {
   /*
   def startDelay = 60000
   def timeout = 15000 // execute job once in 15 seconds
   */
   static triggers = {
      simple name: "CleanerJob", startDelay: 60000, repeatInterval: 15000  
   }
   def transactionService
   def concurrent = false
   def sessionRequired = true

   def execute() {
      /*
      try {
         def methodTime = null
        
         methodTime = new Date()
         transactionService.processDeletes()
         log.debug("job-cleaner: processDeletes: "+(new Date().getTime() - methodTime.getTime()))
      } catch(Exception exception) {
         log.debug("Exception caught in CleanerJob", exception)
      }
      */
   }
}
