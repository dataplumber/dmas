class DispatchJob {
   /*
   def startDelay = 60000
   def timeout = 1000 // execute job once in 5 seconds
   */
   static triggers = {
      simple name: "DispatchJob", startDelay: 60000, repeatInterval: 1000 * 60
   }
   def concurrent = false
   def sessionRequired = true
   def transactionService

   def execute() {
      try {
         def methodTime = null
         
         /*
         methodTime = new Date()
         transactionService.processAdds()
         log.debug("job-dispatch: processAdds: "+(new Date().getTime() - methodTime.getTime()))
         */
         
         methodTime = new Date()
         transactionService.checkActiveJob()
         log.debug("[LOOKATTHIS]job-dispatch: checkActiveJob: "+(new Date().getTime() - methodTime.getTime()))
      } catch(Exception exception) {
         log.debug("Exception caught in DispatchJob", exception)
      }
   }
}
