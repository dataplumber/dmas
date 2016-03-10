class EnginePingJob{
   /*
   def startDelay = 300000
   def timeout = 300000 // execute job once in 5 minutes
   */
   static triggers = {
      simple name: "EnginePingJob", startDelay: 300000, repeatInterval: 300000  
   }
   def transactionService
   def concurrent = false
   def sessionRequired = true

   def execute() {
      /*
      try {
         def methodTime = null
         
         methodTime = new Date()
         transactionService.pingAllEngines()
         log.debug("[LOOKATTHIS]job-enginePing: pingAllEngines: "+(new Date().getTime() - methodTime.getTime()))
      } catch(Exception exception) {
         log.debug("Exception caught in EnginePingJob", exception)
      }
      */
   }
}
