import org.codehaus.groovy.grails.commons.ConfigurationHolder

import gov.nasa.jpl.horizon.api.*
import gov.nasa.jpl.horizon.api.content.SIPHandler
import gov.nasa.jpl.horizon.api.protocol.IngestProtocol

import gov.nasa.jpl.horizon.sigevent.api.EventType

import gov.nasa.podaac.common.api.util.URIPath
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfile
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfileFactory
import gov.nasa.podaac.common.api.serviceprofile.ArchiveProfile
import gov.nasa.podaac.common.api.serviceprofile.ArchiveFileInfo
import gov.nasa.podaac.common.api.serviceprofile.ArchiveGranule
import gov.nasa.podaac.common.api.serviceprofile.IngestDetails
import gov.nasa.podaac.common.api.zookeeper.api.ZkAccess
import gov.nasa.podaac.common.api.zookeeper.api.ZkFactory

import gov.nasa.podaac.inventory.api.Constant.GranuleStatus
import gov.nasa.podaac.inventory.api.Service;
import gov.nasa.podaac.inventory.api.ServiceFactory;
import gov.nasa.podaac.inventory.exceptions.InventoryException

import org.apache.zookeeper.WatchedEvent
import org.apache.zookeeper.Watcher

class ManagerWatcherService {
   
   static transactional = true
   
   def sigEventService
   def storageService
   def inventoryService
   
   static int MAX_NOTE = 2999
   
   /**
    * Method to update an existing product after it has been ingested
    * @param protocol the request protocol
    * @return the transaction status
    */
   private Errno addProductUpdate(IngestProtocol protocol) {
      boolean isIngest = (Stereotype.INGEST == protocol.stereotype)
      def loop = true
      
      def methodTime = new Date()
      def segTime = new Date()
      // obtain and lock the product record for update
      //log.trace("Trying to lock product ${protocol.product}.")
      //IngProduct product = IngProduct.lock(protocol.productId)
      //log.trace("Locked product ${protocol.product}.")
      IngProduct product = IngProduct.get(protocol.productId)
      if (!product || product.name != protocol.product) {
         protocol.errno = Errno.UNEXPECTED_UPDATE
         protocol.description = "Unexpected product update for product ${protocol.product}."
         log.error(protocol.description)
         return protocol.errno
      }
      
      log.trace("Trying to lock product type ${protocol.productType}")
      IngProductType productType = IngProductType.get(protocol.productTypeId)
      //log.trace("Locked product type ${protocol.productType}")
      if (!productType || productType.name != protocol.productType) {
         protocol.errno = Errno.UNEXPECTED_UPDATE
         protocol.description = "Unexpected product update for product ${protocol.product} for product type ${protocol.productType}."
         log.error(protocol.description)
         return protocol.errno
      }
      
      //log.trace("Trying to lock engine job ${protocol.jobId}")
      //IngEngineJob engineJob = IngEngineJob.lock(protocol.jobId)
      //log.trace("Locked engine job ${protocol.jobId}")
      IngEngineJob engineJob = IngEngineJob.get(protocol.jobId)
      if (!engineJob) {
         protocol.errno = Errno.UNEXPECTED_UPDATE
         protocol.description = "Unable to locate job record for this product ${product.name}."
         product.note = trimNote(product.note + protocol.description) + "\n"
         product.currentState = State.ABORTED.toString()
         product.currentLock = Lock.RESERVED.toString()
         log.trace(protocol.description + " JobID: ${protocol.jobId}")
         product.save(flush: true)
         log.error(protocol.description)
         
         sigEventService.send(product.productType.eventCategory.name, EventType.Error, "Unable to locate job: ${product.name}", "Unable to locate job record for this product ${product.name}.")
         
         return protocol.errno
      }
      log.trace("addProductUpdate: segment 1: "+((isIngest) ? "ingest" : "archive")+": "+(new Date().getTime() - segTime.getTime()));
      
      segTime = new Date()
      
      //IngEngine engine = engineJob.engine
      
      while (loop) {
         loop = false
         engineJob.refresh()
         if (!engineJob) break
            try {
               engineJob.delete(flush: true)
            } catch (org.springframework.dao.OptimisticLockingFailureException e) {
               //engineJob.discard()
               loop = true
            }
         log.trace("Looping: 491")
      }
      log.trace("addProductUpdate: segment 2: "+((isIngest) ? "ingest" : "archive")+": "+(new Date().getTime() - segTime.getTime()));
      
      /*
      if (!engine) {
         protocol.errno = Errno.UNEXPECTED_UPDATE
         protocol.description = "Unexpected product update for product ${protocol.product} from ingest engine ${protocol.engine}."
         log.error(protocol.description)
         return protocol.errno
      }
      */
      
      //product.note += protocol.description + "\n"
      
      if (protocol.errno == Errno.CHECKSUM_ERROR || protocol.errno == Errno.INGEST_ERR) {
         segTime = new Date()
         
         // return the preallocated storage
         IngStorage storage = product.contributeStorage
         //loop = true
         //while (loop) {
         //loop = false
         //try {
         //storage.refresh()
         //def storageId = storage.id
         //storage.discard()
         //storage = IngStorage.lock(storageId)
         long totalSize = product.files.sum { it.fileSize }
         //storage.spaceUsed -= totalSize
         //storage.lastUsed = new Date().getTime()
         //storage.save(flush: true)
         storageService.updateStorage(-totalSize, new Date().getTime(), storage.location.id)
         
         //log.debug("ingested space update: "+engine.id+", "+totalSize)
         //} catch (org.springframework.dao.OptimisticLockingFailureException e) {
         //storage.discard()
         //loop = true
         //}
         log.trace("Looping: 524")
         //}
         
         loop = true
         while (loop) {
            loop = false
            try {
               product.refresh()
               // put the product back for another try
               product.currentState = (isIngest) ? State.PENDING.toString() : State.PENDING_ARCHIVE.toString()
               product.note = trimNote(product.note + "{$product.currentState}}: ${protocol.description}") + "\n"
               product.updated = new Date().getTime()
               product.save(flush: true)
            } catch (org.springframework.dao.OptimisticLockingFailureException e) {
               //product.discard()
               loop = true
            }
            log.trace("Looping: 541")
         }
         //sessionFactory.currentSession.clear()
         log.trace("addProductUpdate: segment 3: "+((isIngest) ? "ingest" : "archive")+": "+(new Date().getTime() - segTime.getTime()));
         
         return protocol.errno
      }
      
      if (protocol.errno == Errno.OK) {
         SIPHandler handler = null
         if(isIngest) {
            segTime = new Date()
            
            log.trace("update file metadata")
            handler = new SIPHandler(product.initialText)
            handler.updateIngestDetails(
                  productType.name,
                  protocol.originalProduct,
                  protocol.addFiles,
                  product.contributeStorage.location.remoteAccessProtocol.toLowerCase() + "://" + product.contributeStorage.location.hostname + "/" +
                  product.contributeStorage.location.remotePath + productType.relativePath + product.remoteRelativePath,
                  protocol.operationStartTime, protocol.operationStopTime
                  )
            log.trace("[COMPLETE XML]: ${handler.metadataText}")
            
            log.trace("addProductUpdate: segment 4: "+((isIngest) ? "ingest" : "archive")+": "+(new Date().getTime() - segTime.getTime()));
         } else {
            segTime = new Date()
            
            Service service = inventoryService.getAuthInventoryClient()
            
            //Granule granule = query.findGranuleByName(product.name)
            //query.updateGranuleStatusByID(granule.getGranuleId(), GranuleStatus.ONLINE)
            def updateStatus = false
            try {
               updateStatus = service.updateGranuleStatus((Integer)product.inventoryId, GranuleStatus.ONLINE.toString())
            } catch (InventoryException e) {
               protocol.errno = Errno.INVENTORY_ERR
               protocol.description = e.message
               
               log.error(e.message, e)
               return protocol.errno
            }
            if (!updateStatus) {
               protocol.errno = Errno.UNEXPECTED_UPDATE
               protocol.description = "Unable to update granule status of " + product.name + " to ONLINE through Inventory Service"
               log.error(protocol.description)
               return protocol.errno
            }
            log.debug("Updating granule status: "+((Integer)product.inventoryId))
            
            log.trace("addProductUpdate: segment 5: "+((isIngest) ? "ingest" : "archive")+": "+(new Date().getTime() - segTime.getTime()));
         }
         
         segTime = new Date()
         
         def productId = product.id
         product.discard()
         product = IngProduct.lock(productId)
         //loop = true
         //while (loop) {
         //   loop = false
         //   try {
         //      product.refresh()
         if(isIngest) {
            product.completeText = handler.metadataText
            product.currentState = State.STAGED.toString()
            product.currentLock = product.currentLock
            product.completed = new Date().getTime()
            
            def fileMap = [:]
            product.files.each {file ->
               fileMap[file.name] = file
            }
            protocol.addFiles.each {file ->
               def filename = URIPath.createURIPath(file.source).filename
               fileMap[filename].fileSize = file.size
               fileMap[filename].checksum = file.checksum
               fileMap[filename].currentLock = Lock.GET
               fileMap[filename].ingestStarted = file.startTime.getTime()
               fileMap[filename].ingestCompleted = file.stopTime.getTime()
               fileMap[filename].note = file.description
               //product.note += file.description + "\n"
            }
            
            product.note = trimNote(product.note + "{${product.currentState}}: All ${protocol.addFiles.size()} file(s) have been ingested.") + "\n"
         } else {
            product.currentState = State.ARCHIVED.toString()
            product.currentLock = Lock.DELETE.toString()
            product.archiveNote = trimNote('['+protocol.errno+']: Product has been archived.') + "\n"
            product.archivedAt = protocol.operationStopTime.getTime()
            product.note = trimNote(product.note + "{${product.currentState}}: Archive OK.  Product will be purged from ingest in approximately ${product.productType.purgeRate / 60.0} hour(s).") + "\n"
         }
         product.updated = new Date().getTime()
         product.save(flush: true)
         //   } catch (org.springframework.dao.OptimisticLockingFailureException e) {
         //      //product.discard()
         //      loop = true
         //   }
         //   log.debug("Looping: 609")
         //}
         log.trace("addProductUpdate: segment 6: "+((isIngest) ? "ingest" : "archive")+": "+(new Date().getTime() - segTime.getTime()));
         
         // @TODO add call to send sigevent
         // post to jms
         /*
          if(!isIngest) {
          segTime = new Date()
          sendPubSubJMSMessage('podaac/aip/complete', product.archiveText)
          log.debug('Published AIP to JMS bus.')
          log.trace("addProductUpdate: segment 7: "+((isIngest) ? "ingest" : "archive")+": "+(new Date().getTime() - segTime.getTime()));
          }
          */
         if(!isIngest) {
            def dateStart = new Date()
            sigEventService.send(product.productType.eventCategory.name, EventType.Info, "AIP Published: "+product.name, product.archiveText)
            def dateEnd = new Date()
            log.debug("sigEvent.create to notify AIP: "+((dateEnd.getTime() - dateStart.getTime()) / 1000.0f)+" sec.")
         }
      }
      //sessionFactory.currentSession.clear()
      
      log.trace("addProductUpdate: method: "+((isIngest) ? "ingest" : "archive")+": "+(new Date().getTime() - methodTime.getTime()))
      
      return protocol.errno
   }
   
   
   /**
    * Method called by the ingest engine to update on a move request
    * @param protocol the request packet
    * @return the transaction status
    */
   private Errno moveProductUpdate(IngestProtocol protocol) {
      def loop = true
      //IngProduct product = IngProduct.lock(protocol.productId)
      IngProduct product = IngProduct.get(protocol.productId)
      if (!product) {
         protocol.errno = Errno.UNEXPECTED_UPDATE
         protocol.description = "Unexpected product update for product '${protocol.product}' from ingest engine '${protocol.engine}'."
         log.error(protocol.description)
         return protocol.errno
      }
      
      //IngEngineJob engineJob = IngEngineJob.lock(protocol.jobId)
      IngEngineJob engineJob = IngEngineJob.get(protocol.jobId)
      if (!engineJob) {
         protocol.errno = Errno.UNEXPECTED_UPDATE
         protocol.description = "Unable to locate job record for product ${product.name}."
         
         while (loop) {
            loop = false
            try {
               product.refresh()
               product.currentState = State.ABORTED.toString()
               product.currentLock = Lock.RESERVED.toString()
               product.note = trimNote(product.note + "{${product.currentState}}: ${protocol.description}") + "\n"
               product.updated = new Date().getTime()
               product.save(flush: true)
               
               sigEventService.send(product.productType.eventCategory.name, EventType.Error, "Unable to locate job: ${product.name}", "Unable to locate job record for product ${product.name}.")
            } catch (org.springframework.dao.OptimisticLockingFailureException e) {
               //product.discard()
               loop = true
            }
            log.trace("Looping: 657")
         }
         log.error(protocol.description)
         //sessionFactory.currentSession.clear()
         return protocol.errno
      }
      
      //IngEngine engine = engineJob.engine
      
      loop = true
      while (loop) {
         loop = false
         try {
            engineJob.refresh()
            if (!engineJob) break
               engineJob.delete(flush: true)
         } catch (org.springframework.dao.OptimisticLockingFailureException e) {
            //engineJob.discard()
            loop = true
         }
         log.trace("Looping: 677")
      }
      
      /*
      if (!engine || engine.name != product.contributeEngine.name) {
         protocol.errno = Errno.UNEXPECTED_UPDATE
         protocol.description = "Unexpected product update for product ${protocol.product} from ingest engine ${protocol.engine}."
         log.error(protocol.description)
         return protocol.errno
      }
       */
      
      if (protocol.errno == Errno.FILE_NOT_FOUND) {
         log.warn("File(s) not found during deletion: '${protocol.description}'")
      }
      
      def productId = product.id
      product.discard()
      product = IngProduct.lock(productId)
      //loop = true
      //while (loop) {
      //   loop = false
      //   try {
      //      product.refresh()
      if (protocol.errno == Errno.OK || protocol.errno == Errno.FILE_NOT_FOUND) {
         product.currentLock = Lock.TRASH.toString()
         product.localRelativePath = '.trashbin/' + product.localRelativePath
         product.note = trimNote(product.note + "INFO: All ${product.files.size()} file(s) have been moved to trashbin.") + "\n"
      } else {
         product.currentLock = Lock.RESERVED.toString()
         product.currentState = State.ABORTED.toString()
         product.note = trimNote(product.note + "{${product.currentState}}: Problem(s) during move to trashbin: '${protocol.description}.'") + "\n"
      }
      product.updated = new Date().getTime()
      product.save(flush: true)
      
      if(product.currentState == State.ABORTED.toString()) {
         sigEventService.send(product.productType.eventCategory.name, EventType.Error, "Unable to locate job", "{${product.currentState}}: Problem(s) during move to trashbin: '${protocol.description}.'")
      }
      //   } catch (org.springframework.dao.OptimisticLockingFailureException e) {
      //      //product.discard()
      //      loop = true
      //   }
      //   log.debug("Looping: 712")
      //}
      return protocol.errno
   }
   
   
   private Errno deleteProductUpdate(IngestProtocol protocol) {
      def loop = true
      IngProduct product = IngProduct.get(protocol.productId)
      if (!product || product.name != protocol.product) {
         protocol.errno = Errno.UNEXPECTED_UPDATE
         protocol.description = "Unexpected product update for product '${protocol.product}' from ingest engine '${protocol.engine}'."
         log.error(protocol.description)
         return protocol.errno
      }
      
      //IngEngineJob engineJob = IngEngineJob.lock(protocol.jobId)
      IngEngineJob engineJob = IngEngineJob.get(protocol.jobId)
      if (!engineJob) {
         log.debug ("Warning: Unable to locate job record ${protocol.jobId} for product ${product.name}.")
         while (loop) {
            loop = false
            try {
               product.refresh()
               product.currentState = State.ABORTED.toString()
               product.currentLock = Lock.RESERVED.toString()
               protocol.errno = Errno.UNEXPECTED_UPDATE
               protocol.description = "Unable to locate job record ${protocol.jobId} for product ${product.name}."
               product.note = trimNote(product.note + "{${product.currentState}}: '${protocol.description}'") + "\n"
               product.updated = new Date().getTime()
               product.save(flush: true)
               
               sigEventService.send(product.productType.eventCategory.name, EventType.Error, "Unable to locate job", "Unable to locate job record ${protocol.jobId} for product ${product.name}.")
            } catch (org.springframework.dao.OptimisticLockingFailureException e) {
               //product.discard()
               loop = true
            }
            log.trace("Looping: 749")
         }
         log.error(protocol.description)
         return protocol.errno
      } else {
         log.trace ("Delete job ${engineJob.id} for product ${product.name}.")
         loop = true
         while (loop) {
            loop = false
            try {
               engineJob.refresh()
               if (!engineJob) break
                  engineJob.delete (flush: true)
            } catch (org.springframework.dao.OptimisticLockingFailureException e) {
               //engineJob.discard()
               loop = true
            }
            log.trace("Looping: 766")
         }
      }
      
      // check if this delete is for replace or regular purge
      boolean isReplace = (product.currentState == State.PENDING_ARCHIVE.toString())
      //IngEngine engine = engineJob.engine/*product.contributeEngine*/
      log.debug("isReplace: "+isReplace+", storage: "+product.contributeStorage.id+", totalSize: "+protocol.totalSize)
      
      if (protocol.errno == Errno.FILE_NOT_FOUND) {
         log.warn("Product '${protocol.product}' not found during purge: '${protocol.description}'.")
      }
      
      // purging database records
      if (protocol.errno == Errno.OK || protocol.errno == Errno.FILE_NOT_FOUND) {
         
         IngStorage storage = engineJob.contributeStorage
         //def storageId = storage.id
         //storage.discard();
         //storage = IngStorage.lock(storageId)
         //while (loop) {
         //   loop = false
         //   try {
         //      storage.refresh()
         //      storage.spaceUsed -= protocol.totalSize
         //      storage.lastUsed = new Date().getTime()
         //      storage.save(flush: true)
         storageService.updateStorage(-protocol.totalSize, new Date().getTime(), storage.location.id)
         //   } catch (org.springframework.dao.OptimisticLockingFailureException e) {
         //      log.debug("Ops exception: 789", e)
         //      //storage.discard()
         //      loop = true
         //   }
         //   log.debug("Looping: 792, "+storage.id)
         //}
         
         // performs cascade delete
         def productId = product.id
         product.discard()
         product = IngProduct.lock(productId)
         log.trace("deleteProductUpdate: locked")
         if(!isReplace) {
            product.refresh()
            product.delete(flush:true)
         } else {
            product.refresh()
            product.currentLock  = Lock.INVENTORY.toString()
            product.currentState = State.INVENTORIED.toString()
            product.note = trimNote(product.note + "INFO: Done purging for replace.") + "\n"
            product.updated      = new Date().getTime()
            product.save(flush: true)
         }
         log.trace("deleteProductUpdate: released")
      } else {
         loop = true
         while (loop) {
            loop = false
            try {
               product.refresh()
               product.currentLock = Lock.RESERVED.toString()
               product.currentState = State.ABORTED.toString()
               product.note = trimNote(product.note + "{${product.currentState}}: Problem(s) during purge: '${protocol.description}'") + "\n"
               product.updated = new Date().getTime()
               product.save(flush: true)
               
               sigEventService.send(product.productType.eventCategory.name, EventType.Error, "Problem to purge", "{${product.currentState}}: Problem(s) during purge: '${protocol.description}'")
            } catch (org.springframework.dao.OptimisticLockingFailureException e) {
               //product.discard()
               loop = true
            }
            log.trace("Looping: 830")
         }
      }
      //sessionFactory.currentSession.clear()
      return protocol.errno
   }
   
   String trimNote(String note) {
      def buf = new StringBuffer(note)
      buf.length = MAX_NOTE
      return buf.toString().trim()
   }
   
   def handleZkWatcher(String path, Watcher w) {
      if (path.contains("queue") && (path.contains("ingest") || path.contains("archive"))) {
         int i = 0
         while (i < 3) {
            IngEngineJob engineJob = IngEngineJob.findByPath(path)
            if (engineJob) {
               path = engineJob.product.productType.name+"/"+engineJob.product.name
               break
            } else {
               // The path probably just didn't get saved yet so wait for at most 3 seconds
               log.debug("Cannot locate corresponding engine job for queue node " + path + " sleeping for 1 second")
               Thread.sleep(1000)
               i++
            }
         }
         if (i == 3) {
            log.debug("Cannot locate corresponding engine job for queue node " + path)
            return false
         }
      }
      def watcher = { WatchedEvent event ->
         log.debug ("ManagerWatcherService handleZkWatcher: received event " + event.type + " " + event.state)
      }
      try {
         ZkAccess zk = ZkFactory.getZk(ConfigurationHolder.config.horizon_zookeeper_url as String, watcher as Watcher)
         // Attempt to read and put a watch on the process node created by the engine
         String s = zk.readProcessNode(path, w)
         log.debug("[process node data] " + s)
         if (s != null) {
            try {
               def xml = new XmlSlurper().parseText(s)
               if (zk.removeProcessNode(path)) {
               log.debug("Removed process node: " + path)
               IngestProtocol protocol = new IngestProtocol()
               protocol.load(xml.params.toString())
               if (protocol.operation == Opcode.ENGINE_INGEST) {
                  addProductUpdate(protocol)
               } else if (protocol.operation == Opcode.ENGINE_DELETE) {
                  deleteProductUpdate(protocol)
               } else if (protocol.operation == Opcode.ENGINE_MOVE) {
                  moveProductUpdate(protocol)
               } else {
                  log.error("Unrecognized operation " + protocol.operation)
               }
               } else {
               log.debug("Unable to remove process node: " + path)
               }
               return true
            } catch (org.xml.sax.SAXParseException e) {
               String[] names = path.split("/")
               String productTypeName = names[0]
               String productName = names[1]
               IngProduct product = IngProduct.createCriteria().get {
                  eq("name", productName)
                  productType { eq("name", productTypeName) }
               }
               if (product) {
                  log.debug("Got non xml data from engine so updating product note")
                  productStateUpdate(product.id, s)
                  return true
               } else {
                  log.error("Cannot locate product to update state for process node: " + path)
               }
            } catch (Exception e) {
               log.error(e.message, e)
            }
         }
      } catch (IOException e) {
         //TODO fire sigevent
         if (e.message.startsWith("Max attempts failed")) {
            log.debug(e.message)
         } else {
            log.error(e.message, e)
         }
      }
      return false
   }
   
   private void productStateUpdate(Long productId, String note) {
      IngProduct product = IngProduct.get(productId)
      if (product) {
         product.refresh()
         if (product.currentState == State.PENDING_ASSIGNED.toString()) {
            product.currentState = State.ASSIGNED.toString()
         } else if (product.currentLock == Lock.PENDING_RESERVED.toString()) {
            product.currentLock = Lock.RESERVED.toString()
         } else {
            product.discard()
            return
         }
         product.note = trimNote(product.note + "{${product.currentState}}: " + note) + "\n"
         product.updated = new Date().getTime()
         //TODO catch exception and ignore
         product.save(flush: true)
      }
   }
}
