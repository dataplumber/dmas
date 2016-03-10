/**
 * ProductJob
 */
class ProductJob {
   static triggers = {
      simple name: "ProductJob", startDelay: 60000, repeatInterval: 60000 * 60
   }
   def concurrent = false
   def sessionRequired = true
   def productService
   
   def execute() {
      try {
         def methodTime = null
         
         methodTime = new Date()
         productService.checkGranulesPending()
         log.debug("[LOOKATTHIS]job-product: checkGranulesPending: "+(new Date().getTime() - methodTime.getTime()))
      } catch(Exception exception) {
         log.debug("Exception in ProductJob.", exception)
      }
   }
}
