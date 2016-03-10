class InventoryJob {
   /*
   def startDelay = 60000
   def timeout = 1000 // execute job once in 6 seconds
   */
   static triggers = {
      simple name: "InventoryJob", startDelay: 60000, repeatInterval: 1000  
   }
   def inventoryService
   def concurrent = false
   def sessionRequired = true

   def execute() {
      /*
      try {
         def methodTime = null
         
         methodTime = new Date()
         inventoryService.processStaged()
         log.debug("job-inventory: processStaged: "+(new Date().getTime() - methodTime.getTime()))
         
         methodTime = new Date()
         inventoryService.processInventoried()
         log.debug("job-inventory: processInventoried: "+(new Date().getTime() - methodTime.getTime()))
      } catch(Exception exception) {
         log.debug("Exception caught InventoryJob", exception)
      }
      */
   }
}
