class InventoryPingJob{

   // ping inventory once every 5 minutes
   static triggers = {
      simple name: "InventoryPingJob", startDelay: 60000, repeatInterval: 300000
   }
   def transactionService
   def concurrent = false
   def sessionRequired = true

   def execute() {
      try {
         def methodTime = null
         
         methodTime = new Date()
         transactionService.pingInventory()
         log.debug("[LOOKATTHIS]job-inventoryPing: pingInventory: "+(new Date().getTime() - methodTime.getTime()))
      } catch(Exception exception) {
         log.debug("Exception caught in InventoryPingJob", exception)
      }
   }
}
