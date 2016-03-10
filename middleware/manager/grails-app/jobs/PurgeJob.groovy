class PurgeJob {
   /*
   def startDelay = 60000
   def timeout = 20000 // execute job once in 20 seconds
   */
   static triggers = {
      simple name: "PurgeJob", startDelay: 60000, repeatInterval: 20000  
   }
   def transactionService
   def concurrent = false
   def sessionRequired = true

   def execute() {
      /*
      try {
         def methodTime = null
         
         methodTime = new Date()
         transactionService.processPurge()
         log.debug("job-purge: processPurge: "+(new Date().getTime() - methodTime.getTime()))
      } catch(Exception exception) {
         log.debug("Exception caught in PurgeJob", exception)
      }
      */
   }
}
