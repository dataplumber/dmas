//import gov.nasa.podaac.common.api.util.DateTimeUtility
import gov.nasa.jpl.horizon.api.Lock
import gov.nasa.jpl.horizon.api.State
import gov.nasa.jpl.horizon.api.Stereotype
import gov.nasa.podaac.common.api.zookeeper.api.constants.JobPriority
import gov.nasa.podaac.common.api.zookeeper.api.ZkAccess
import gov.nasa.podaac.common.api.zookeeper.api.ZkFactory
import groovy.xml.MarkupBuilder
import org.apache.zookeeper.WatchedEvent
import org.apache.zookeeper.Watcher
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.quartz.Trigger

class OperatorService {
   //public static final String UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS"
   static transactional = true
   def quartzScheduler
   
   private static final Map ALLOWED_PARAMETERS = [
      "updateProductType": [
         "deliveryRate": "Integer.parseInt(x)",
         "purgeRate": "Integer.parseInt(x)",
         "priority": "gov.nasa.podaac.common.api.zookeeper.api.constants.JobPriority.valueOf(x.toUpperCase()).value"
      ]
   ]
   
   public def searchProducts(def params, int page, int rows) {
      log.debug("id: "+params.id)
      
      def query = {
         createAlias("productType", "pt")
         createAlias("pt.federation", "fed")
         and {
            if(params.current_state) {
               ilike("currentState", "%"+params.current_state+"%")
            }
            if(params.current_lock) {
               ilike("currentLock", "%"+params.current_lock+"%")
            }
            if(params.id) {
               try {
                  eq("id", Long.parseLong(params.id))
               } catch(Exception exception) {}
            }
            if(params.current_retries) {
               try {
                  eq("currentRetries", Integer.parseInt(params.current_retries))
               } catch(Exception exception) {}
            }
            if(params.contributor) {
               ilike("contributor", "%"+params.contributor+"%")
            }
            if(params.note) {
               ilike("note", "%"+params.note+"%")
            }
            if(params.name) {
               ilike("name", "%"+params.name+"%")
            }
            if(params.priority) {
               int priorityVal = -1
               try {
                  priorityVal = JobPriority.valueOf(params.priority.toUpperCase()).value
               } catch(Exception exception) {}
               eq("pt.priority", priorityVal)
            }
            eq("fed.name", ConfigurationHolder.config.manager_federation)
         }
         if (params.sidx?.equals("priority")) {
            params.sidx = "pt.priority"
         }
         order(params.sidx, params.sord)
      }
      def products = IngProduct.createCriteria().list(
         "max": rows,
         "offset": ((page - 1) * rows),
         query
      )
      def count = IngProduct.createCriteria().count(query)
      
      def entries = []
      products.each {product ->
         def entry = ["id": product.id]
         def cell = [
            product.id,
            product.name,
            product.currentState,
            product.currentLock,
            product.currentRetries,
            product.created,
            product.updated,
            product.archivedAt,
            product.contributor,
            product.note,
            product.productType.jobPriority.name()
         ]
         entry['cell'] = cell
         entries.add(entry)
      }
      
      return this.constructListResponse(page, rows, count, entries)
   }
   
   public def updateProucts(def ids, def state, def lock, int retries) throws Exception {
      def idsWorked = []
      ids.each {id ->
         boolean success = false
         
         def criteria = IngProduct.createCriteria()
         def product = criteria.get {
            eq("id", id)
            productType {
               federation {
                  eq("name", ConfigurationHolder.config.manager_federation)
               }
            }
         }
         if(product) {
            product.discard()
            product = IngProduct.lock(id)
            
            product.currentState = state
            product.currentLock = lock
            product.currentRetries = retries
            if(product.save(flush: true)) {
               idsWorked.add(id)
            } else {
               product.errors.each {
                  log.error(it)
               }
            }
         }
      }
      
      return idsWorked
   }
   
   public def deleteProducts(def ids) {
      def watcher = { WatchedEvent event ->
         log.debug ("OperatorService:deleteProducts Watcher: received event " + event.type + " " + event.state)
      }
      def result = []
      ids.each {id ->
         boolean success = false
         
         def criteria = IngProduct.createCriteria()
         def product = criteria.get {
            eq("id", id)
            productType {
               federation {
                  eq("name", ConfigurationHolder.config.manager_federation)
               }
            }
         }
         if(product) {
            //Find associated IngEngineJob if any
            def engineJob = IngEngineJob.findByProduct(product)
            ZkAccess zk = ZkFactory.getZk(ConfigurationHolder.config.horizon_zookeeper_url as String, watcher as Watcher)
            
            def deleteProduct = true
            if(engineJob) {
               log.debug("Found associated job: ${engineJob.id}")

               if (engineJob.path) {
                  //Delete node from zookeeper
                  if (!zk.removeNode(engineJob.path)) {
                     //Deletion of node failed, so cannot delete product
                     deleteProduct = false
                     log.error("Delete product ${product.id} failed because cannot delete node from ZooKeeper.")
                  } else {
                     //Delete the engine job
                     try {
                        engineJob.delete(flush: true)
                     } catch(Exception exception) {
                        //Deletion of associated engine job failed, so cannot delete product
                        deleteProduct = false
                        log.error("Delete product ${product.id} failed because cannot delete associated engine job ${engineJob.id}.", exception)
                     }
                  }
               } else {
                  //Delete the engine job
                  try {
                     engineJob.delete(flush: true)
                  } catch(Exception exception) {
                     //Deletion of associated engine job failed, so cannot delete product
                     deleteProduct = false
                     log.error("Delete product ${product.id} failed because cannot delete associated engine job ${engineJob.id}.", exception)
                  }
               }
            }
            if (deleteProduct) {
            //Remove associated process node in case it exists
            zk.removeProcessNode(product.productType.name+"/"+product.name)

            product.discard()
            product = IngProduct.lock(id)
            
            try {
               def filePath = null
               if(product.contributeStorage) {
                  def storage = product.contributeStorage
                  def host = storage.location.hostname
                  def localPath = storage.location.localPath + product.productType.relativePath + product.localRelativePath
                  filePath = host+":"+localPath
               }
               product.delete(flush: true)
                              
               result.add(["id": id, "filePath": filePath])
            } catch(Exception exception) {
               log.error("Delete product ${product.id} failed.", exception)
            }
            }
         }
      }
      
      return result
   }
   
   public def countProducts() {
      def query = {
         projections {
            groupProperty("currentState")
            groupProperty("currentLock")
            countDistinct("id")
        }
         productType {
            federation {
               eq("name", ConfigurationHolder.config.manager_federation)
            }
         }
      }
      def rows = IngProduct.createCriteria().list(query)
      def count = IngProduct.createCriteria().count(query)
      
      log.debug("rows: "+rows.size()+", count: "+count)
      
      def entries = []
      rows.each {row ->
         log.debug("row: "+row.size())
         def entry = [
            "currentState": row[0],
            "currentLock": row[1],
            "count": row[2]
         ]
         entries.add(entry)
      }
      
      return this.constructListResponse(1, Integer.MAX_VALUE, count, entries)
   }
   
   /**
    * Returns count by priority of TRASH/ARCHIVED products whose archivedAt  
    * value is greater than or equal to specified archived time
    */
   public def countProductsByPriority(long archivedTime) {
      def query = {
         createAlias("productType", "pt")
         createAlias("pt.federation", "fed")
         projections {
            groupProperty("pt.priority")
            countDistinct("id")
        }
        eq("currentLock", Lock.TRASH.toString())
        eq("currentState", State.ARCHIVED.toString())
        eq("fed.name", ConfigurationHolder.config.manager_federation)
        ge("archivedAt", archivedTime)
      }
      def rows = IngProduct.createCriteria().list(query)
      def count = IngProduct.createCriteria().count(query)
      
      log.debug("countProductsByPriority rows: "+rows.size()+", count: "+count)
      
      def entries = []
      rows.each {row ->
         log.debug("row: "+row.size())
         def entry = [
            "priority": JobPriority.values().find{ it.value == row[0] }.name(),
            "count": row[1]
         ]
         entries.add(entry)
      }
      
      return this.constructListResponse(1, Integer.MAX_VALUE, count, entries)
   }
   
   public def listStorages() {
      def storages = IngLocation.list()
      def count = IngLocation.count()
      
      def entries = []
      storages.each {storage ->
         def entry = ["id": storage.id]
         def locationStore = []
         storage.storages.each {s ->
            locationStore.add(["id": s.id, "name": s.name, "priority": s.storageJobPriority ? s.storageJobPriority.name() : null])
         }
         def cell = [
            storage.id,
            storage.localPath,
            storage.remoteAccessProtocol,
            storage.spaceUsed,
            storage.spaceReserved,
            storage.spaceThreshold,
            storage.active,
            locationStore
         ]
         entry["cell"] = cell
         entries.add(entry)
      }
      
      return this.constructListResponse(1, Integer.MAX_VALUE, count, entries)
   }
   
   def countJobsByStorage() {
      def query = {
         projections {
            groupProperty("contributeStorage")
            groupProperty("priority")
            countDistinct("id")
        }
      }
      def rows = IngEngineJob.createCriteria().list(query)
      def count = IngEngineJob.createCriteria().count(query)
      
      def entries = [:]
      rows.each {row ->
         def countByPriority = [
            "priority": JobPriority.values().find{ it.value == row[1] }.name(),
            "count": row[2]
         ]
         def id = row[0].id
         if (entries.containsKey(id)) {
            entries[id].counts.add(countByPriority)
         } else {
            def entry = [
               "id": id,
               "name" : row[0].name,
               "counts": [countByPriority]
            ]
            entries[id] = entry
         }
      }
      
      return this.constructListResponse(1, Integer.MAX_VALUE, count, entries.isEmpty() ? [] : entries.values())
   }
   
   public def listEngines() {
      def engines = IngEngine.list()
      def count = IngEngine.count()
      
      def entries = []
      engines.each {engine ->
         def entry = ["id": engine.id]
         def cell = [
            engine.id,
            engine.name,
            engine.active,
            engine.isOnline,
            engine.hostname,
            engine.stereotype,
            engine.startedAt,
            engine.stoppedAt,
            engine.storageId,
            engine.note
         ]
         entry["cell"] = cell
         entries.add(entry)
      }
      
      return this.constructListResponse(1, Integer.MAX_VALUE, count, entries)
   }
   
   public def updateLocation(def ids, long spaceReserved, long spaceThreshold, long spaceUsed, Boolean active) {
      def idsWorked = []
      ids.each {id ->
         boolean success = false
         
         def storage = IngLocation.get(id)
         if(storage) {
            storage.discard()
            storage = IngLocation.lock(id)
            
            storage.spaceReserved = spaceReserved
            storage.spaceThreshold = spaceThreshold
            storage.spaceUsed = spaceUsed
            if (active != null) {
               storage.active = active
            }
            if(storage.save(flush: true)) {
               idsWorked.add(id)
            } else {
               storage.errors.each {
                  log.error(it)
               }
            }
         }
      }
      
      return idsWorked
   }
   
   def updateStorage(def ids, JobPriority priority) {
      def idsWorked = []
      ids.each {id ->
         boolean success = false
         
         def storage = IngStorage.get(id)
         if(storage) {
            storage.discard()
            storage = IngStorage.lock(id)
            
            if (!priority || priority != JobPriority.DEFAULT) {
               storage.priority = priority ? priority.value : null
            }
            if(storage.save(flush: true)) {
               idsWorked.add(id)
            } else {
               storage.errors.each {
                  log.error(it)
               }
            }
         }
      }
      
      return idsWorked
   }
   
   private def constructListResponse(int page, int rows, int records, def entries) {
      int total = (int)Math.ceil(records / rows)
      def response = ["page": page, "total": total, "records": records, "rows": entries]
      
      return response
   }
   
   public def searchProductTypes(def params, int page, int rows) {
      def query = {
         and {
            if(params.id) {
               try {
                  eq("id", Long.parseLong(params.id))
               } catch(Exception exception) {}
            }
            if(params.name) {
               ilike("name", "%"+params.name+"%")
            }
            if(params.priority) {
               int priorityVal = -1
               try {
                  priorityVal = JobPriority.valueOf(params.priority.toUpperCase()).value
               } catch(Exception exception) {}
               eq("priority", priorityVal)
            }
            federation {
               eq("name", ConfigurationHolder.config.manager_federation)
            }
         }
         if (params.sidx && params.sord) {
            order(params.sidx, params.sord)
         }
      }
      def productTypes = IngProductType.createCriteria().list(
         "max": rows,
         "offset": ((page - 1) * rows),
         query
      )
      def count = IngProductType.createCriteria().count(query)
      
      def entries = []
      productTypes.each {productType ->
         def entry = ["id": productType.id]
         entry['cell'] = formatProductType(productType)
         entries.add(entry)
      }
      
      return this.constructListResponse(page, rows, count, entries)
   }
   
   public def updateProductTypes(def ids, Map parameters) {
      def fed = null
      if (parameters.federation) {
         fed = IngFederation.findByName(parameters.federation)
         if (!fed) {
            throw new RuntimeException("Federation not found: "+parameters.federation)
         }
      }
      
      def fieldsToUpdate = [:]
      ALLOWED_PARAMETERS.updateProductType.each {key, value ->
         if(parameters.containsKey(key)) {
            if(parameters."$key") {
               fieldsToUpdate[key] = (value) ? Eval.x(parameters."$key", value) : parameters."$key"
            } else {
               //allow empty parameter to mean null value
               fieldsToUpdate[key] = null
            }
         }
      }
      
      def idsWorked = []
      ids.each {id ->
         
         def criteria = IngProductType.createCriteria()
         def productType = criteria.get {
            eq("id", id)
            federation {
               eq("name", ConfigurationHolder.config.manager_federation)
            }
         }
         if(productType) {
            productType.discard()
            productType = IngProductType.lock(id)
            if (fed && !fed.equals(productType.federation)) {
               def count = IngProduct.createCriteria().count {
                  createAlias("productType", "pt")
                  ne("currentLock", Lock.TRASH.toString())
                  ne("currentState", State.ARCHIVED.toString())
                  eq("pt.name", productType.name)
               }
               if (count == 0) {
                  productType.federation = fed
               } else {
                  log.debug("Cannot change federation for product type: " + productType.name)
               }
            }
            
            fieldsToUpdate.each {key, value ->
               productType."$key" = value
            }
            if(productType.save(flush: true)) {
               idsWorked.add(id)
            } else {
               productType.errors.each {
                  log.error(it)
               }
            }
         }
      }
      
      return idsWorked
   }
   
   public def createProductType(def userName, def datasetName) {
      def federationValue = ConfigurationHolder.config.manager_federation
      def purgeRateValue = ConfigurationHolder.config.horizon_dataset_update_purge_rate
      
      def federation = IngFederation.findByName(federationValue)
      if (!federation) {
         throw new RuntimeException("Federation not found: "+federationValue)
      }
      
      def systemUser = IngSystemUser.findByName(userName)
      if(!systemUser) {
         throw new RuntimeException("User not found: "+userName)
      }
      
      IngEventCategory eventCategory = IngEventCategory.findByName(datasetName)
      if (!eventCategory) {
         eventCategory = new IngEventCategory(name: datasetName)
         if (!eventCategory.save(flush:true)) {
            throw new RuntimeException("Failed to save IngEventCategory: " + eventCategory.errors.allErrors.join())
         }
      }
      
      IngProductType productType = IngProductType.findByFederationAndName(federation, datasetName)
      if (!productType) {
         productType = new IngProductType()
         productType.federation = federation
         productType.name = datasetName
         productType.locked = false
         productType.ingestOnly = false
         productType.relativePath = datasetName+'/'
         productType.purgeRate = purgeRateValue
         productType.updatedBy = IngSystemUser.findByName(userName)
         productType.note = datasetName+' product'
         productType.eventCategory = eventCategory
         if(!productType.save(flush: true)) {
            throw new RuntimeException("Failed to save IngProductType: " + productType.errors.allErrors.join())
         }
         
         def accessRoles = IngAccessRole.getAll()
         accessRoles.each {accessRole ->
            def productTypeRole = new IngProductTypeRole()
            productTypeRole.productType = productType
            productTypeRole.role = accessRole
            if(!productTypeRole.save(flush: true)) {
               throw new RuntimeException("Failed to save IngProductTypeRole: "  + productTypeRole.errors.allErrors.join())
            }
         }
         
         def entries = []
         def entry = ["id": productType.id]
         entry['cell'] = formatProductType(productType)
         entries.add(entry)
         
         return this.constructListResponse(1, Integer.MAX_VALUE, 1, entries)
      } else {
         throw new RuntimeException("Product type for ${datasetName} already exists for federation ${federation.name}.")
      }
   }
   
   public def showProductTypeByName(def name) {
      def criteria = IngProductType.createCriteria()
      def productType = criteria.get {
         eq("name", name)
         federation {
            eq("name", ConfigurationHolder.config.manager_federation)
         }
      }
      
      if (productType) {
         def entries = []
         def entry = ["id": productType.id]
         entry['cell'] = formatProductType(productType)
         entries.add(entry)
         return this.constructListResponse(1, Integer.MAX_VALUE, 1, entries)
      } else {
         throw new RuntimeException("IngProductType does not with Name: " + name)
      }
   }
   
   private List formatProductType(IngProductType productType) {
      return [
         productType.id,
         productType.name,
         productType.federation.name,
         productType.locked,
         productType.lockedAt,
         productType.lockedBy,
         productType.ingestOnly,
         productType.relativePath,
         productType.purgeRate,
         productType.updated,
         productType.updatedBy.name,
         productType.eventCategory.name,
         productType.note,
         productType.deliveryRate,
         productType.jobPriority.name()
      ]
   }
   
   def pauseJob(String name, String group) {
      quartzScheduler.pauseJob(name, group)
   }
   
   def resumeJob(String name, String group) {
      quartzScheduler.resumeJob(name, group)
   }
   
   def getJobState(String name, String group) {
      if (quartzScheduler.getTriggerState(name, group) == Trigger.STATE_PAUSED) {
         return "PAUSED"
      } else {
         return "RESUMED"
      }
   }
   
   def exportDomain(String defaultFederation) {
      if (!defaultFederation) {
         defaultFederation = ConfigurationHolder.config.manager_federation
      }
      def defaultFed = IngFederation.findByName(defaultFederation)
      if (!defaultFed)
         throw new RuntimeException("Federation not found: ${defaultFederation}")
      def writer = new StringWriter()
      def xml = new MarkupBuilder(writer)
      def federations = IngFederation.list(sort:"id", order:"asc")
      xml.domain(xmlns: "http://podaac.jpl.nasa.gov") {
         xml.default(defaultFed.name)
         federations.each {fed ->
            xml.federation() {
               name(fed.name)
               url("https://" + fed.hostname + ":" + fed.port + "/manager/ingest")
               def productTypes = IngProductType.findAllByFederation(fed, [sort:"name", order:"asc"])
               productTypes.each {productType ->
                  xml.productType(productType.name)
               }
            }
         }
         xml.sigevent(ConfigurationHolder.config.horizon_sigevent_url)
         xml.jobkeeper() {
            xml.server(ConfigurationHolder.config.horizon_zookeeper_url)
            xml.webservice(ConfigurationHolder.config.horizon_zookeeper_ws_url)
         }
         xml.discovery(ConfigurationHolder.config.horizon_discovery_url)
         xml.inventory(ConfigurationHolder.config.gov.nasa.podaac.manager.inventory.host+":"+ConfigurationHolder.config.gov.nasa.podaac.manager.inventory.port)
         xml.security(ConfigurationHolder.config.gov.nasa.podaac.security.host+":"+ConfigurationHolder.config.gov.nasa.podaac.security.port)
      }
      
      return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+writer.toString()
   }
}
