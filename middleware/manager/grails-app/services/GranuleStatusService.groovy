import java.sql.Timestamp

import grails.converters.JSON

import gov.nasa.podaac.common.api.util.DateTimeUtility
import gov.nasa.jpl.horizon.api.Lock
import gov.nasa.jpl.horizon.api.State

class GranuleStatusService {
  private static final long PENDING_LIMIT_IN_MILLISECOND = 24 * 60 * 60 * 1000
  boolean transactional = true

  def getGranules(int page, granulesPerPage) {
    def productsCriteria = IngProduct.createCriteria()
    def products = productsCriteria.list {
      or {
        'in'("currentState", [State.ABORTED.toString(), State.PENDING_STORAGE.toString(), State.PENDING_ARCHIVE_STORAGE.toString()])
        and {
          'in'("currentState", [State.PENDING.toString(), State.PENDING_ARCHIVE.toString()])
          lt('updated', (new Date().getTime() - PENDING_LIMIT_IN_MILLISECOND))
        }
      }
      firstResult((page * granulesPerPage))
      maxResults(granulesPerPage)
      order("updated", 'desc')
    }
    
    return products
  }
  
  int getGranulesCount() {
    def productsCriteria = IngProduct.createCriteria()
    int count = productsCriteria.count {
      or {
        'in'("currentState", [State.ABORTED.toString(), State.PENDING_STORAGE.toString(), State.PENDING_ARCHIVE_STORAGE.toString()])
        and {
          'in'("currentState", [State.PENDING.toString(), State.PENDING_ARCHIVE.toString()])
          lt('updated', (new Date().getTime() - PENDING_LIMIT_IN_MILLISECOND))
        }
      }
    }
    
    return count
  }
  
  def getGranule(int id) {
    def product = IngProduct.findById(id);
    return product
  }
  
  def updateGranule(int id, State state, Lock lock) {
    def product = IngProduct.get(id) //IngProduct.lock(id)
    if(!product) {
      throw new Exception("Failed to lock")
    }
    
    def loop = true
    while (loop) {
       loop = false
       try {
          product.refresh()
          product.currentState = state.toString()
          product.currentLock = lock.toString()
          product.save(flush: true)
       } catch (org.springframework.dao.OptimisticLockingFailureException e) {
          product.discard()
          loop = true
       }
    }
  }
}
