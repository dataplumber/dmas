import gov.nasa.jpl.horizon.sigevent.api.EventType
import gov.nasa.jpl.horizon.api.Lock
import gov.nasa.jpl.horizon.api.State

import org.codehaus.groovy.grails.commons.ConfigurationHolder

/**
 * ProductService
 */
class ProductService {
   private static final long MAXIMUM_PENDING_DURATION = 1000 * 60 * 60 * 24
   private static final String MAXIMUM_PENDING_DURATION_TEXT = "24 hours"
   private static final int PRODUCTS_PER_PAGING = 10
   private static final String SIG_EVENT_CATEGORY = "DMAS"
   boolean transactional = false
   def sigEventService
   
   public void checkGranulesPending() {
      Date triggeringDate = new Date(new Date().getTime() - ProductService.MAXIMUM_PENDING_DURATION)
      int productsIndex = 0
      boolean moreProducts = true
      
      while(moreProducts) {
         log.debug("Checking granules stuck")
         
         def criteria = IngProduct.createCriteria()
         def products = criteria.list(
            max: ProductService.PRODUCTS_PER_PAGING,
            offset: productsIndex,
            order: "desc",
            sort: "updated"
            ) {
            and {
               le("updated", triggeringDate.getTime())
               ne("currentLock", Lock.TRASH.toString())
               productType {
                  federation {
                     eq("name", ConfigurationHolder.config.manager_federation)
                  }
               }
            }
         }
         
         log.debug("\tgranules stack processing: "+products.size())
         
         products.each {product ->
            sigEventService.send(
               ProductService.SIG_EVENT_CATEGORY,
               EventType.Warn,
               "Granule with id="+product.id+" has been stuck for "+ProductService.MAXIMUM_PENDING_DURATION_TEXT,
               "Granule with id="+product.id+", name="+product.name+" has been stuck for "+ProductService.MAXIMUM_PENDING_DURATION_TEXT
            )
         }
         
         productsIndex += ProductService.PRODUCTS_PER_PAGING
         moreProducts = (products.size() > 0)
         
         log.debug("Done checking granules stuck")
      }
   }
}
