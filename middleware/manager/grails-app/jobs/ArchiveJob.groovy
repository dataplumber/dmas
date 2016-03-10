class ArchiveJob{
   /*
   def startDelay = 60000
   def timeout = 1000 // execute job once in 5 seconds
   */
   static triggers = {
      simple name: "ArchiveJob", startDelay: 60000, repeatInterval: 1000  
   }
   def transactionService
   def concurrent = false
   def sessionRequired = true

   def execute() {
      /*
      try {
         def methodTime = null
          
         methodTime = new Date()
         transactionService.processReplacePurge()
         log.debug("job-archive: processReplacePurge: "+(new Date().getTime() - methodTime.getTime()))
         
         methodTime = new Date()
         transactionService.processArchives()
         log.debug("job-archive: processArchives: "+(new Date().getTime() - methodTime.getTime()))
      } catch(Exception exception) {
         log.debug("Exception caught in ArchiveJob", exception)
      }
      */
   }
}
