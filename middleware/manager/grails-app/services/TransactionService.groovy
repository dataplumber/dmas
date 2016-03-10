import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.springframework.orm.hibernate3.HibernateObjectRetrievalFailureException
import org.hibernate.StaleObjectStateException

import gov.nasa.jpl.horizon.api.*
import gov.nasa.jpl.horizon.api.content.SIPHandler
import gov.nasa.jpl.horizon.api.protocol.IngestProtocol

import gov.nasa.jpl.horizon.manager.ManagerWatcher
import gov.nasa.jpl.horizon.sigevent.api.EventType

import gov.nasa.podaac.common.api.util.URIPath
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfile
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfileFactory
import gov.nasa.podaac.common.api.serviceprofile.ArchiveProfile
import gov.nasa.podaac.common.api.serviceprofile.ArchiveFileInfo
import gov.nasa.podaac.common.api.serviceprofile.ArchiveGranule
import gov.nasa.podaac.common.api.serviceprofile.IngestDetails
import gov.nasa.podaac.common.api.zookeeper.api.constants.RegistrationStatus
import gov.nasa.podaac.common.api.zookeeper.api.ZkAccess
import gov.nasa.podaac.common.api.zookeeper.api.ZkFactory

import gov.nasa.podaac.inventory.api.Constant.GranuleStatus
import gov.nasa.podaac.inventory.api.Service;
import gov.nasa.podaac.inventory.api.ServiceFactory;
import gov.nasa.podaac.inventory.exceptions.InventoryException

import gov.nasa.podaac.inventory.model.Granule

import org.apache.zookeeper.WatchedEvent
import org.apache.zookeeper.Watcher

class TransactionService {

  static transactional = false
  
  private static final String SIG_EVENT_CATEGORY = "UNCATEGORIZED"
  private static final String SIG_EVENT_DMAS_CATEGORY = "DMAS"
  private final Watcher mw = new ManagerWatcher()

  //def sessionFactory
  def inventoryService
  def sigEventService
  def storageService
  def managerWatcherService
  def authenticationService
  
  static int MAX_NOTE = 2999

  /**
   * Method to authenticate and retreive user privilege
   *
   * @param username the user name
   * @param password the user password
   * @param result the result map for retrieved data and error tracking.
   *
   * @return the result map
   */
  def checkUser(IngestProtocol protocol) {
    IngSystemUser user
    if (!protocol.loginPassword) {
      user = IngSystemUser.findByName(protocol.user)
    } else {
      if (authenticationService.authenticate(protocol.user, protocol.loginPassword)) {
         user = IngSystemUser.findByName(protocol.user)
      }
    }
    long userid = Constants.NOT_SET
    long usertype = Constants.NO_ACCESS
    Errno errno = Errno.INVALID_LOGIN
    log.trace "errno = ${errno.toString()}"
    String description = "Unable to authenticate user '${protocol.user}'."
    if (user) {
      userid = user.id
      if (user.admin) {
        usertype = Constants.ADMIN
      }
      else if (user.readAll) {
        usertype = Constants.READ_ALL
      } else if (user.writeAll) {
        usertype = Constants.WRITE_ALL
      }
      errno = Errno.OK
      description = "User ${protocol.user} found."
      log.trace "User ${protocol.user} found.  Errno = ${errno}.  OK=${Errno.OK}.."

    }
    log.trace "checkUser errno ${errno}."
    protocol.errno = errno
    protocol.userId = userid
    protocol.userType = usertype
    protocol.description = description
    return errno
  }

  /**
   * Method to create a new user session
   *
   * @param userid the user identifier for direct access to the user record
   * @param producttype the product type name
   * @param result the result map for retrieved data and error tracking.
   *
   * @return the result map
   */
  def createUserSession(IngestProtocol protocol) {
    IngSystemUser user = IngSystemUser.get(protocol.userId)
    if (!user) {
      protocol.errno = Errno.DENIED
      protocol.description = "Unable to retrieve user recode id ${userid}."
      return protocol.errno
    }

    IngProductType pt = null

    if (protocol.productType) {
      pt = IngProductType.createCriteria().get {
         eq("name", protocol.productType)
         federation {
            eq("name", ConfigurationHolder.config.manager_federation)
         }
      }
      if (!pt) {
        protocol.errno = Errno.DENIED
        protocol.description =
          "Unable to retrieve product type ${protocol.productType} for federation ${ConfigurationHolder.config.manager_federation}."
        return protocol.errno
      }
    }

    def issueTime = new Date()
    def expireTime = issueTime + 1
    protocol.errno = Errno.OK

    def token =
    Encrypt.encrypt("${user.name}${user.password}${issueTime.time}")
    def userSession =
    IngSystemUserSession.findByUserAndProductType(user, pt)
    def loop = true
//    while (loop) {
//       loop = false
//       try {
          if (!userSession) {
            userSession = new IngSystemUserSession(
                    user: user,
                    productType: pt,
                    token: token,
                    issueTime: issueTime.getTime()
            )
            if (pt)
              protocol.description =
                "Create new session for user ${user.name} for type ${pt.name}."
            else
              protocol.description =
                "Create new session for admin user ${user.name}."
          } else {
             //userSession.clearErrors()
             //userSession = userSession.refresh()
            //userSession.lock()
            //userSession.discard()
            //userSession = IngSystemUserSession.lock(userSession.id)
            userSession.token = token
            userSession.issueTime = issueTime.getTime()
            if (pt)
              protocol.description =
                "Refresh session for user ${user.name} for type ${pt.name}."
            else
              protocol.description =
                "Refresh session for admin user ${user.name}."
          }

          if (user.admin) {
            userSession.expireTime = null
          } else {
            userSession.expireTime = expireTime.getTime()
          }

          if (!userSession.save(flush: true)) {
            userSession.errors.each {
              protocol.description += "${it}\n"
            }
            protocol.errno = Errno.DENIED
            return protocol.errno
          }
//       } catch (org.springframework.dao.OptimisticLockingFailureException e) {
//          userSession.discard()
//          loop = true
//       }
//    }
    log.trace "createUserSession errno ${protocol.errno}."
    protocol.userId = user.id
    if (pt)
      protocol.productTypeId = pt.id
    protocol.sessionId = userSession.id
    protocol.sessionToken = token
    protocol.loginIssueTime = new Date(userSession.issueTime)
    protocol.loginExpireTime = userSession.expireTime ? new Date(userSession.expireTime) : null
    return protocol.errno
  }

  /**
   * Method to verify user session by checking the input session token
   */
  def verifyUserSession(IngestProtocol protocol) {
    if (!protocol.description)
      protocol.description = ""

    IngSystemUser user = IngSystemUser.get(protocol.userId)
    if (!user) {
      protocol.errno = Errno.DENIED
      protocol.description += "Unable to retrieve user record id ${protocol.userId}.\n"
      return protocol.errno
    }

    IngProductType pt = null

    if (protocol.productType) {
      pt = IngProductType.createCriteria().get {
         eq("name", protocol.productType)
         federation {
            eq("name", ConfigurationHolder.config.manager_federation)
         }
      }
      if (!pt) {
        protocol.errno = Errno.DENIED
        protocol.description += "Unable to retrieve product type ${protocol.productType} for federation ${ConfigurationHolder.config.manager_federation}.\n"
        return protocol.errno
      }
    }

    IngSystemUserSession usersession = IngSystemUserSession.get(protocol.sessionId)
    if (!usersession) {
      protocol.errno = Errno.INVALID_LOGIN
      protocol.description += "Invalid user ${user.name} session.\n"
      return protocol.errno

    }

    if (usersession.productType && protocol.productType && usersession.productType.name != protocol.productType) {
      protocol.errno = Errno.INVALID_SESSION
      protocol.description +=
        "Session ${protocol.sessionId} is not registered by product type ${protocol.productType}.\n"
      return protocol.errno
    }

    if (!user.admin && usersession.expireTime && usersession.expireTime < new Date().time) {
      protocol.errno = Errno.SESSION_EXPIRED
      protocol.description += "User session has expired.\n"
      return protocol.errno
    }

    if (!protocol.sessionId)
      protocol.sessionId = usersession.id
    if (pt)
      protocol.productTypeId = pt.id
    if (!protocol.description)
      protocol.description = ""
    //protocol.description += "User session ${usersession.id} found.\n"
    return Errno.OK
  }

  /**
   * Method called by controller for product add
   * @param protocol the request
   * @return the transaction status
   */
  def addProduct(IngestProtocol protocol) {
    return addReplaceProduct(protocol, false)
  }

  /**
   * Method called by controller for product replace
   * @param protocol the request
   * @return the transaction status
   */
  def replaceProduct(IngestProtocol protocol) {
    return addReplaceProduct(protocol, true)
  }


  /**
   * Method to support add/replace product
   * @param protocol the request protocol
   * @param forReplace flag to indicate if the transaction is for replace
   * @return the transaction status
   */
  def addReplaceProduct(IngestProtocol protocol, boolean forReplace) {
    log.debug ("addReplaceProduct - ${protocol.toString()}")
    IngSystemUserRole userRole = IngSystemUserRole.findByUser(IngSystemUser.get(protocol.userId))
    if (!userRole) {
      protocol.errno = Errno.DENIED
      protocol.description = "Access role for user ${IngSystemUser.get(protocol.userId)} not found."
      return protocol.errno
    }

    IngProductType productType = IngProductType.get(protocol.productTypeId)
    if (!productType) {
      protocol.errno = Errno.DENIED
      protocol.description = "Product type ${protocol.productType} not found."
      return protocol.errno
    }

    IngProductTypeRole ptr = IngProductTypeRole.findByProductType(productType)
    if (!ptr) {
      protocol.errno = Errno.DENIED
      protocol.description = "Access role for product type ${productType.name} not found."
      return protocol.errno
    }

    if (!userRole.canAdd(ptr)) {
      protocol.errno = Errno.DENIED
      protocol.description = "Attempt to add/replace product to type ${productType.name} denied."
      return protocol.errno
    }

    def product
    Lock currentLock = Lock.ADD
    def loop = true

    if (protocol.originalProduct && forReplace) { 
      // this is a replace operation
      currentLock = Lock.REPLACE
      def original = IngProduct.findWhere(productType: productType, name: protocol.originalProduct, versionNumber: 1)
      if (original) {
         protocol.errno = Errno.PRODUCT_EXISTS
         protocol.description = "Illegal replace operation.  Original product '${productType.name}:${protocol.product} is currently locked for ''${original.currentLock}'."
         return protocol.errno
      }

      product = IngProduct.findWhere(productType: productType, name: protocol.product, versionNumber: 1)
      if (product) {
        protocol.errno = Errno.PRODUCT_EXISTS
        protocol.description = "Illegal replace operation.  Product '${productType.name}:${protocol.product}' already exist."
        return protocol.errno
      }
      
      def granuleExists = inventoryService.isExists(protocol)
      if (protocol.errno && protocol.errno != Errno.OK) {
         return protocol.errno
      }
      if (!granuleExists) {
        // clears the replace block and allow this to be an ADD
        protocol.originalProduct = null
        SIPHandler sipHandler = new SIPHandler(protocol.addMetadata)
        sipHandler.replace = null
        protocol.addMetadata = sipHandler.metadataText
      }
      // if it gets here, the granule is in inventory but
      // it was not ingested by Ingest or has already been purged.
      // It is possible the granule was migrated from
      // legacy system.  Therefore,
      // we will keep the REPLACE option for Inventory
   } else {
      // this is an add operation
      product = IngProduct.findWhere(productType: productType, name: protocol.product, versionNumber: 1)
      if (product || inventoryService.isExists(protocol)) {
         protocol.errno = Errno.PRODUCT_EXISTS
         protocol.description = "Product '${protocol.product}' for type '${productType.name}' already exist."
         return protocol.errno
      }
      if (protocol.errno && protocol.errno != Errno.OK) {
         return protocol.errno
      }
   }

   protocol.errno = Errno.OK

   Date now = new Date()

   log.debug "trying to add product ${protocol.product} from user ${IngSystemUser.get(protocol.userId).name}"
   product = new IngProduct(
      name: protocol.product,
      contributor: IngSystemUser.get(protocol.userId).name,
      updated: now.getTime(),
      currentLock: currentLock.toString(),
      currentState: State.RESERVED.toString(),
      initialText: protocol.addMetadata,
      productType: productType,
      notify: protocol.notify,
      versionNumber: 1, // TODO hardecode the version for now
      note: "")

   if (!product.save(flush:true)) {
      product.errors.each {
         log.trace it
      }
   }
   log.trace ("Product ${product.name} saved to ID ${product.id}, current lock ${currentLock} = ${currentLock.toString()}")

   //product.discard()
   //product = IngProduct.lock(product.id)

   Calendar cal = Calendar.instance
   cal.time = now

   // set the relative path for the files to be ingested
   String path =
   sprintf("%04d${File.separator}%02d${File.separator}%02d${File.separator}%d${File.separator}%s${File.separator}",
         [cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH),
                 product.id, protocol.product])

   loop = true
   while (loop) {
      loop = false
      try {
         product.refresh()
         product.localRelativePath = path
         product.remoteRelativePath = path
         product.save(flush:true)
      } catch (org.springframework.dao.OptimisticLockingFailureException e) {
         //product.discard()
         loop = true
      }
      log.trace("Looping: 375")
   }

   log.trace "destination ${path}"

   def files = protocol.addFiles

   for (ProductFile file in files) {
      URIPath remotePath = URIPath.createURIPath(file.source)
      def remoteSystem = IngRemoteSystem.findByRootUri(remotePath.hostURI)

      // the contributing host must be registered
      if (!remoteSystem) {
         protocol.errno = Errno.UNREGISTERED_HOST
         protocol.description = "Remote host ${remotePath.hostURI} has not been approved."
         product.delete(flush: true)
         return protocol.errno
      }
      log.trace "add product file ${file.name} ${remoteSystem.rootUri} ${path}${file.name} ${file.size}."
      IngDataFile datafile = new IngDataFile(
         name: remotePath.filename,
         provider: remoteSystem,
         remotePath: remotePath.path,
         fileSize: file.size,
         currentLock: Lock.ADD.toString()
      )

      if (remotePath.filename.toLowerCase().endsWith(".gzip") ||
         remotePath.filename.toLowerCase().endsWith(".gz")) {
         datafile.compression = "GZIP"
      } else if (remotePath.filename.toLowerCase().endsWith(".bzip2")) {
         datafile.compression = "BZIP2"
      } else if (remotePath.filename.toLowerCase().endsWith(".zip")) {
         datafile.compression = "ZIP"
      } else {
         datafile.compression = "NONE"
      }
      datafile.save(flush:true)
      product.addToFiles(datafile)
   }

   product.currentState = State.PENDING.toString()
   product.updated = new Date().getTime()
   if (!product.note) {
      product.note = ''
   }
   product.note = trimNote(product.note + "{${product.currentState}}: Ingestion request received.") + "\n"
   protocol.productId = product.id
   protocol.description = "Product ${protocol.product} of type ${productType.name} has been registered for '${product.currentLock}' with current state in '${product.currentState}'."

   product.save(flush: true)

   //sessionFactory.currentSession.clear()
   return protocol.errno
  }


  /**
   * Method to support product update after it has been archived
   * @param protocol request protocol
   * @return transaction status
   */
  /*
  def archiveProductUpdate(IngestProtocol protocol) {
    log.trace("archiveProductUpdate check for productTypeId")
    if (!protocol.productTypeId) {
      protocol.errno = Errno.UNEXPECTED_UPDATE
      protocol.description = "Unexpected product update for archive."
      log.error(protocol.description)
      return protocol.errno
    }

    log.trace("check for productId")
    if (!protocol.productId) {
      protocol.errno = Errno.UNEXPECTED_UPDATE
      protocol.description = "Unexpected product update for type id '${protocol.productTypeId}."
      log.error(protocol.description)
      return protocol.errno
    }

    IngProductType productType = IngProductType.get(protocol.productTypeId)
    IngProduct product = IngProduct.lock(protocol.productId)

    log.trace("make sure the product to be update is of the correct type")
    if (product.productType != productType) {
      protocol.errno = Errno.UNEXPECTED_UPDATE
      protocol.description = "Invalid relation for type '${productType.name}' and product '${product.name}'."
      log.error(protocol.description)
      return protocol.errno
    }

    log.trace("make sure the product is at a correct state")
    if (Lock.valueOf(product.currentLock) != Lock.GET || State.valueOf(product.currentState) != State.PENDING_ARCHIVE) {
      protocol.errno = Errno.UNEXPECTED_UPDATE
      protocol.description = "Invalid product state to be archived [lock:'${product.currentLock}', state: '${product.currentState}']."
      log.error(protocol.description)
      return protocol.errno
    }

    if (protocol.errno == Errno.OK) {
      log.debug("Archive ok, and mark the product for deletion.")
      product.currentLock = Lock.DELETE.toString()
      product.currentState = State.ARCHIVED.toString()
      product.archiveNote = protocol.description
      product.archivedAt = protocol.operationStopTime
      product.note += "{${product.currentState}}: Archive OK.  Product will be purged from ingest in approximately ${product.productType.purgeRate / 60.0} hour(s).\n"
    } else {
      log.debug("Error during archive: ${protocol.description}.")
      product.currentLock = Lock.RESERVED.toString()
      product.currentState = State.ABORTED.toString()
      product.archiveNote = protocol.description
      product.note += "{${product.currentState}}: Error during archive '${protocol.description}'.\n"
    }

    product.updated = new Date()

    log.trace("Write to DB.")
    product.save(flush: true)
    //sessionFactory.currentSession.clear()
    return protocol.errno
  }
  */


   def processDeletes() {
      def loop = true
      log.debug("Process deletes.")
      def criteria = IngProduct.createCriteria()
      def aborts = criteria.list {
         eq("currentLock", Lock.DELETE.toString())
         eq("currentState", State.ABORTED.toString())
         productType {
            federation {
               eq("name", ConfigurationHolder.config.manager_federation)
            }
            order('priority', 'asc')
         }
         maxResults(5)
         order("created", 'asc')
      }

      if (aborts) {
         aborts.each {IngProduct product ->
            //product.discard()
            //product = IngProduct.lock(product.id)
            IngStorage storage = product.contributeStorage
            //storage.discard()
            //storage = IngStorage.lock(storage.id)
            def totalsize = product.files.sum {
               it.fileSize
            }

            def ids = product.files.collect {
               it.id
            }

            ids.each {id ->
               IngDataFile file = IngDataFile.get(id)
               file.product.removeFromFiles(file)
               loop = true
               while (loop) {
                  loop = false
                  try {
                     file?.refresh()
                     if (!file) break
                     file.delete(flush: true)
                  } catch (org.springframework.dao.OptimisticLockingFailureException e) {
                     //file.discard()
                     loop = true
                  }
                  log.trace("Looping: 945")
               }
            }

            while (loop) {
               loop = false
               try {
                  storageService.updateStorage(-totalsize, new Date().getTime(), storage.location.id)
                  product.currentLock = Lock.NONE.toString()
                  product.note = trimNote(product.note + "Ingestion aborted.  Data file records haven been updated.") + "\n"
                  product.save(flush: true)
               } catch (org.springframework.dao.OptimisticLockingFailureException e) {
                  //storage.discard()
                  loop = true
               }
               log.trace("Looping: 962")
            }
         }
      }

      // Process deletes in round-robin manner allowing up to 2 deletes per engine
      def storages = IngStorage.withCriteria {
         location {
            eq('active', true)
            eq('stereotype', Stereotype.INGEST.toString())
         }
      }
      for (IngStorage storage in storages) {
      criteria = IngProduct.createCriteria()
      def pendingDeletes = criteria.list {
         eq("contributeStorage", storage)
         eq("currentLock", Lock.DELETE.toString())
         eq("currentState", State.ARCHIVED.toString())
         productType {
            federation {
               eq("name", ConfigurationHolder.config.manager_federation)
            }
            order('priority', 'asc')
         }
         maxResults(2)
         order("created", "asc")
      }

      if (!pendingDeletes || pendingDeletes.size() == 0) {
         continue
      }

      for (IngProduct product in pendingDeletes) {
         if (Lock.valueOf(product.currentLock) != Lock.DELETE || 
         State.valueOf(product.currentState) != State.ARCHIVED) {
            // the product's lock/state was updated since it was queried into memory.
            continue
         }

         //IngEngine engine = product.contributeEngine
//         if (!engine.active || !engine.isOnline) {
//            log.trace("Engine '${engine.federation.name}:${engine.name}' is unreachable for deleting product '${product.name}'.")
//            continue
//         }
         if (!storage.location.active) {
            log.debug("Cannot move product ${product.name} to trashbin because storage ${storage.name} is inactive")
            continue
         }
         // updated
         IngEngineJob engineJob = new IngEngineJob(
//            engine: engine,
            product: product,
            operation: product.currentLock,
            previousState: product.currentState,
            assigned: new Date().getTime(),
            priority: product.productType.jobPriority.getValue(),
            contributeStorage: product.contributeStorage
         )
         engineJob.save(flush: true)

         //engineJob.discard()
         //engineJob = IngEngineJob.lock(engineJob.id)

         if (!product.name.contains("_TRASH_")) {
            // Append _TRASH_ to product name so that the same granule can be submitted for replace
            def productId = product.id
            product.discard()
            product = IngProduct.lock(productId)
            product.name += "_TRASH_${product.id}"
            product.updated = new Date().getTime()
            product.save(flush: true)
         }
         
         // create the ingest request to be sent to the worker engine
         IngestProtocol protocol = IngestProtocol.createEngineMoveRequest(
            Stereotype.INGEST,
            product.productType.federation.name,
            product.productType.federation.id,
            product.productType.name,
            product.productType.id,
            product.name,
            product.id,
            "${product.name}.sip.xml",
            product.archiveText,
            product.contributeStorage.location.localPath + product.productType.relativePath + product.localRelativePath,
            product.contributeStorage.location.localPath + product.productType.relativePath + ".trashbin/" + product.localRelativePath
         )
         log.debug("processDelete: "+product.contributeStorage.location.localPath + product.productType.relativePath + product.localRelativePath)

//         String uri = engine.protocol +
//            "://" + engine.hostname +
//            ":" + engine.userPort +
//            engine.urlPattern
//
//         def post = new Post(url: uri)

         protocol.jobId = engineJob.id
//         post.body = protocol.toRequest()


//         IngestProtocol response = new IngestProtocol()
         String response = null
         ZkAccess zk = null
         String path = product.productType.name+"/"+product.name
         def watcher = { WatchedEvent event ->
            log.debug ("TransactionService:processDelete Watcher: received event " + event.type + " " + event.state)
         }
         try {
//            def res = post.text
            zk = ZkFactory.getZk(ConfigurationHolder.config.horizon_zookeeper_url as String, watcher as Watcher)
            log.debug("ZK intance " + zk + " " + zk.getSessionInfo().get("id"))
            response = zk.addToIngestQueue(product.contributeStorage.name, protocol.toRequest(), product.productType.jobPriority, mw)
            log.debug("[SERVER RESP] ${response}")
//            response.load(res)
         } catch (IOException e) {
         if (e.message.equals("WATCHER_NOT_SET")) {
            if (managerWatcherService.handleZkWatcher(path, mw)) {
               continue
            } else {
               response = path
            }
         } else {
            log.trace(e.message, e)
            engineJob.delete(flush: true)
            log.trace(e.message, e)
            continue
         }
         }

         if (!response) {
            log.debug("Problem(s) during job assignment for moving product to trashbin: response from ZooKeeper is null.")
            engineJob.delete(flush: true)
         } else {
            //Update engine job with path to queue node
            updateIngEngineJob(engineJob, response)
            
            def productId = product.id
            product.discard()
            product = IngProduct.lock(productId)
            if (product.currentLock == Lock.DELETE.toString()) {
            //loop = true
            //while (loop) {
            //   loop = false
            //   try {
            //      product.refresh()
                  //product.name += "_TRASH_${product.id}"
                  product.note = trimNote(product.note + "INFO: Move request has been assigned. Job sent to storage queue ${product.contributeStorage.name}.") + "\n"
                  log.trace("Done assigning product '${product.name}' for moving to trashbin.")
                  product.currentLock = Lock.PENDING_RESERVED.toString()
                  product.updated = new Date().getTime()
                  product.save(flush: true)
            //   } catch (org.springframework.dao.OptimisticLockingFailureException e) {
            //      loop = true
            //   }
            //   log.trace("Looping: 1062, "+product.id+", "+loop)
            //}
            } else {
            product.discard()
            }
         }
         log.debug ("Method for moving ${product.name} terminates.")
      }
      }
      //sessionFactory.currentSession.clear()
   }


   def processAdds() {
      def loop = true
      log.debug("Process adds")
      // Abort all pending products that have already exceeded
      // the number of retries

      def abortCriteria = IngProduct.createCriteria()
      def aborts = abortCriteria.list {
         'in'("currentLock", [Lock.ADD.toString(), Lock.REPLACE.toString()])
         'in'("currentState", [State.PENDING.toString(), State.PENDING_STORAGE.toString()])
         eq("currentRetries", 0)
         productType {
            federation {
               eq("name", ConfigurationHolder.config.manager_federation)
            }
            order('priority', 'asc')
         }
         maxResults(5)
         order("created", 'asc')
      }

      if (aborts) {
         aborts.each {IngProduct product ->
           //product.discard()
           //product = IngProduct.lock(product.id)
           def ids = product.files.collect {
             it.id
           }

           ids.each {id ->
             IngDataFile file = IngDataFile.get(id)
             file.currentLock = Lock.RESERVED.toString()
             loop = true
             while (loop) {
                loop = false
                try {
                   file?.refresh()
                   if (!file) break
                   file.save(flush: true)
                } catch (org.springframework.dao.OptimisticLockingFailureException e) {
                   //file.discard()
                   loop = true
                }
                log.trace("Looping: 1108")
             }
           }
           
           loop = true
           while (loop) {
              loop = false
              try {
                 product.refresh()
                 product.currentLock = Lock.RESERVED.toString()
                 product.currentState = State.ABORTED.toString()
                 product.note = trimNote(product.note + "{${product.currentState}}: Ingestion exceeds maximum allowable attempts.  Abort ingestion.") + "\n"
                 product.save(flush: true)
                 
                 sigEventService.send(product.productType.eventCategory.name, EventType.Error, "Problem to add", "{${product.currentState}}: Ingestion exceeds maximum allowable attempts. Abort ingestion.")
              } catch (org.springframework.dao.OptimisticLockingFailureException e) {
                 //product.discard()
                 loop = true
              }
              log.trace("Looping: 1125")
           }
         }
      }

      log.trace 'Done processing aborts... now query for pending jobs'
      def pendingCriteria = IngProduct.createCriteria()
      def pendingAdds = pendingCriteria.list {
         'in'("currentLock", [Lock.ADD.toString(), Lock.REPLACE.toString()])
         'in'("currentState", [State.PENDING.toString(), State.PENDING_STORAGE.toString()])
         and {
            gt("currentRetries", 0)
            le("currentRetries", 10)
         }
         productType {
            federation {
               eq("name", ConfigurationHolder.config.manager_federation)
            }
            order('priority', 'asc')
         }
         maxResults(5)
         order("created", 'asc')
      }

      if (!pendingAdds || pendingAdds.size() == 0) {
         return
      }

      // get a list of active engines
      //def engines = IngEngine.findAllByActive(true)
      def storages = IngStorage.withCriteria {
         location {
            eq('active', true)
            eq('stereotype', Stereotype.INGEST.toString())
         }
      }

      if (storages.size() == 0) {
         log.debug("No storage found.")
         return
      }

      for (IngProduct product in pendingAdds) {
         //log.trace("Trying to lock product ${product.name}")
         //product.discard()
         //product = IngProduct.lock(product.id)
         log.trace("Locked product ${product.name}")
         if (![Lock.ADD, Lock.REPLACE].contains(Lock.valueOf(product.currentLock)) || 
            ![State.PENDING, State.PENDING_STORAGE].contains(State.valueOf(product.currentState))) {
            // the product's lock/state was updated since it was queried into memory.
            continue
         }

         long totalSize = product.files.sum {
            it.fileSize
         }

         log.debug("totalSize ${totalSize}")

         // making sure we have storage available to fit this product
         //engines = engines.findAll {engine ->
         //   engine.storage.spaceReserved - (engine.storage.spaceUsed + totalSize) > 0
         //}
         def storageId = storageService.getStorage(totalSize, Stereotype.INGEST, product.productType.jobPriority.getValue())

         if (storageId == -1) {
            log.debug("Product requires ${totalSize} bytes, which is too large to store in current staging areas, or no storage exists to process this ${product.productType.jobPriority.name()} priority product.")
            loop = true
            while (loop) {
               loop = false
               try {
                  product.refresh()
                  if (State.valueOf(product.currentState) != State.PENDING_STORAGE) {
                     product.currentState = State.PENDING_STORAGE.toString()
                     product.note = trimNote(product.note + "{${product.currentState}}: Product requires ${totalSize} bytes, which is too large to store in current staging areas, or no storage exists to process this ${product.productType.jobPriority.name()} priority product.") + "\n"
                     product.updated = new Date().getTime()
                     product.save(flush: true)
                  }
               } catch (org.springframework.dao.OptimisticLockingFailureException e) {
                  //product.discard()
                  loop = true
               }
               log.trace("Looping: 1195")
            }
            continue
         }

         // determine the engine to assign to according to it current
         // load percentage and its available storage space.
         /*
         engines = engines.sort {engine ->
            (int) (IngEngineJob.countByEngine(engine) / (double) engine.maxUserConnections * 100)
            +(engine.storage.spaceReserved - (engine.storage.spaceUsed + totalSize))
         }.reverse()

         if (log.traceEnabled) {
            log.debug("Sorted engine list.")
            engines.each {engine ->
               def load = (int) (IngEngineJob.countByEngine(engine) / (double) engine.maxUserConnections * 100)
               def memory = (engine.storage.spaceReserved - (engine.storage.spaceUsed + totalSize))
               log.debug("${engine.name}: ${load} + ${memory} = ${load + memory}")
            }
         }
         */

         def busycount = 0
         //for (IngEngine worker in engines) {
            //IngEngine worker = engines[0]
            IngStorage storage = IngStorage.get(storageId)

            log.trace("Trying to lock storage ${storage.name}")

            // create the ingest request to be sent to the worker engine
            IngestProtocol protocol = IngestProtocol.createEngineIngestRequest(
               Stereotype.INGEST,
               product.productType.federation.name,
               product.productType.federation.id,
               product.productType.name,
               product.productType.id,
               product.name,
               product.id
            )

            // create the place holder to storage the engine response
            //IngestProtocol response = new IngestProtocol()

            // use a handler to extract file information from the metadata text
            SIPHandler sipHandler = new SIPHandler(product.initialText)

            // lookup login information to the provider host
            def productFiles = sipHandler.productFiles
            productFiles.each {
               URIPath path = URIPath.createURIPath(it.source)
               IngRemoteSystem host = IngRemoteSystem.findByRootUri(path.hostURI)
               if (host) {
                  it.sourceUsername = host.username
                  it.sourcePassword = host.password
                  it.maxConnections = host.maxConnections
               }
               it.destination =
                  storage.location.localPath + product.productType.relativePath + product.localRelativePath
               log.debug("Ingest source: "+it.source+", destination: "+it.destination)
            }

            protocol.addFiles = productFiles
            //String uri = worker.protocol + "://" + worker.hostname + ":" + worker.userPort + worker.urlPattern
            //def post = new Post(url: uri)

//            IngEngineJob engineJob = new IngEngineJob(
//               //engine: worker,
//               product: product,
//               operation: product.currentLock,
//               previousState: product.currentState,
//               assigned: new Date().getTime()
//            )
//
//            if (!engineJob.save(flush: true)) {
//               engineJob.errors.each {
//                  log.error it
//               }
//            }

            //engineJob.discard()
            //engineJob = IngEngineJob.lock(engineJob.id)
            IngEngineJob engineJob = moveProductState(product, storage, totalSize, true)
            protocol.jobId = engineJob.id

            //post.body = protocol.toRequest()
            //log.trace("[REQUEST] ${post.body}")
            String response = null
            ZkAccess zk = null
            String path = product.productType.name+"/"+product.name
            def watcher = { WatchedEvent event ->
               log.debug ("Transaction Manager:processAdd Watcher: received event " + event.type + " " + event.state)
            }
            try {
               zk = ZkFactory.getZk(ConfigurationHolder.config.horizon_zookeeper_url as String, watcher as Watcher)
               log.debug("ZK intance " + zk + " " + zk.getSessionInfo().get("id"))
               response = zk.addToIngestQueue(storage.name, protocol.toRequest(), product.productType.jobPriority, mw)
               //def res = post.text
               log.debug("[ZOOKEEPER RESP] ${response}")
               //response.load(res)
            } catch (IOException e) {
            if (e.message.equals("WATCHER_NOT_SET")) {
               if (managerWatcherService.handleZkWatcher(path, mw)) {
                  continue
               } else {
                  response = path
               }
            } else {
               log.debug(e.message, e)
               revertProductState(product, storage, engineJob, totalSize)
               continue
            }
            }

            //log.trace("Done sending request to ${worker.name}")
            if (!response) {
               ///product.note = response.description + "\n"
               log.debug("Problem(s) during ingestions: response from ZooKeeper is null.")
               revertProductState(product, storage, engineJob, totalSize)
//               engineJob.delete(flush: true)
//               if (response.errno == Errno.SERVER_BUSY) {
//                  ++busycount
//               }
               continue
            } else {
               //Update engine job with path to queue node
               updateIngEngineJob(engineJob, response)
               /*
               def productId = product.id
               product.discard()
               product = IngProduct.lock(productId)
               //loop = true
               //while (loop) {
               //   loop = false
               //   try {
               //      product.refresh()
                     --product.currentRetries
                     log.debug("Done assigning product ${response.product} for ingestion.")
                     product.contributeEngine = worker
                     product.currentState = State.ASSIGNED.toString()
                     product.note = trimNote(product.note + "{${product.currentState}}: Done assigning product ${response.product} for ingestion.") + "\n"
                     product.updated = new Date().getTime()
                     product.save(flush: true)
               //   } catch (org.springframework.dao.OptimisticLockingFailureException e) {
               //      //product.discard()
               //      loop = true
               //   }
               //   log.trace("Looping: 1315")
               //}

               //storage.discard()
               //storage = IngStorage.lock(storage.id)

               def storageId = storage.id
               storage.discard()
               storage = IngStorage.lock(storageId)
               //loop = true
               //while (loop) {
               //   loop = false
               //   try {
               //      storage.refresh()
                     storage.spaceUsed += totalSize
                     storage.lastUsed = new Date().getTime()
                     storage.save(flush: true)
               //   } catch (org.springframework.dao.OptimisticLockingFailureException e) {
               //      //storage.discard()
               //      loop = true
               //   }
               //   log.debug("Looping: 1331")
               //}

               break
               */
            }
         }
//         if (busycount == engines.size()) {
//           log.info("All active ingest engines are busy now.  Skip 'ADD' job assignment until next cycle.")
//           break
//         }
      //}
      //sessionFactory.currentSession.clear()
   }
   
   
   def processReplacePurge() {
      def loop = true
      log.debug ("Processing replace purge.")

      def criteria = IngProduct.createCriteria()
      def products = criteria.list {
         eq('currentLock', Lock.INVENTORY.toString())
         eq('currentState', State.PENDING_ARCHIVE.toString())
         productType {
            federation {
               eq("name", ConfigurationHolder.config.manager_federation)
            }
            order('priority', 'asc')
         }
         maxResults(5)
         order('created', 'asc')
      }

      if (!products || products.size() == 0) {
         return
      }

      log.trace "${products.size()} products to be purged from archive for replace."

      for (IngProduct product in products) {
         //product.discard()
         //product = IngProduct.lock(product.id)
         if (Lock.valueOf(product.currentLock) != Lock.INVENTORY || 
            State.valueOf(product.currentState) != State.PENDING_ARCHIVE) {
            // the product's lock/state was updated since it was queried into memory.
            continue
         }

         // get files to delete
         ServiceProfile serviceProfile =
            ServiceProfileFactory.getInstance().createServiceProfileFromMessage(product.archiveText)
         ArchiveProfile archiveProfile = serviceProfile.getProductProfile().getArchiveProfile()
         ArchiveGranule archiveGranule = archiveProfile.getGranules().get(0)
         List<String> filesToDelete = archiveGranule.getDeletes()
         for(String fileToDelete : filesToDelete) {
           log.debug('file to delete: '+fileToDelete)
         }

         /*
         // get total file size of files to delete
         Query query = QueryFactory.getInstance().createQuery()
         Granule granule = query.findGranuleByName(archiveGranule.getDeleteName());
         granule = query.fetchGranuleById(granule.getGranuleId(), 'granuleArchiveSet');

         log.debug('calculating file size of files to be deleted before replace:')
         long totalSize = 0
         granule.getGranuleArchiveSet().each {
           totalSize += it.getFileSize()
           log.debug("\t"+archiveGranule.getDeleteName()+": "+it.getFileSize())
         }
         */

         /*
         IngEngine engine = product.contributeEngine

         IngStorage storage = engine.storage
         */
//         def engines = IngEngine.findAllWhere(active: true, isOnline: true, stereotype: Stereotype.PURGE.toString())
//         if (engines.size() == 0) {
//            log.debug("No active PURGE engines found. Trying ARCHIVE engines.")
//            
//            engines = IngEngine.findAllWhere(active: true, isOnline: true, stereotype: Stereotype.ARCHIVE.toString())
//            if (engines.size() == 0) {
//               log.debug("No active ARCHIVE engine found neither.")
//               continue
//            }
//         }
//         for(IngEngine engine : engines) {
//           if (!engine.active || !engine.isOnline) {
//             log.trace("Engine '${engine.federation.name}:${engine.name}' is unreachable for purging product '${product.name}'.")
//             continue
//           }

//           def storage = IngStorage.findByActiveAndStereotype(true, Stereotype.ARCHIVE.toString())
//           if (!storage) {
//              log.debug("No archive storage found")
//              continue
//           }
           def storageId = storageService.getStorageByTypeAndPriority(Stereotype.ARCHIVE, product.productType.jobPriority.getValue())
           
           if (storageId == -1) {
              log.debug("No archive storage found")
              continue
           }
           def storage = IngStorage.get(storageId)
           
           // updated
           IngEngineJob engineJob = new IngEngineJob(
//             engine: engine,
             product: product,
             operation: product.currentLock,
             previousState: product.currentState,
             assigned: new Date().getTime(),
             priority: product.productType.jobPriority.getValue(),
             contributeStorage: storage
           )
           if(!engineJob.save(flush: true)) {
             engineJob.errors.each {
                 log.error it
              }
           }
           log.trace ("Product ${product.name} delete job ${engineJob.id}.")

           // create the ingest request to be sent to the worker engine
           IngestProtocol protocol = IngestProtocol.createEngineDeleteRequest(
             Stereotype.PURGE,
             product.productType.federation.name,
             product.productType.federation.id,
             product.productType.name,
             product.productType.id,
             product.name,
             product.id
           )
           //protocol.totalSize = totalSize
           protocol.jobId = engineJob.id

           def destinations = []
           filesToDelete.each {file ->
             destinations << new URI(file).getPath()
           }
           protocol.deletes = destinations

//           String uri = engine.protocol +
//             "://" + engine.hostname +
//             ":" + engine.userPort +
//             engine.urlPattern

           //def post = new Post(url: uri)
           //post.body = protocol.toRequest()


           //IngestProtocol response = new IngestProtocol()
           String response = null
           ZkAccess zk = null
           String path = product.productType.name+"/"+product.name
           def watcher = { WatchedEvent event ->
              log.debug ("TransactionService processReplacePurge Watcher: received event " + event.type + " " + event.state)
           }
           try {
              zk = ZkFactory.getZk(ConfigurationHolder.config.horizon_zookeeper_url as String, watcher as Watcher)
              log.debug("ZK intance " + zk + " " + zk.getSessionInfo().get("id"))
              response = zk.addToArchiveQueue(storage.name, protocol.toRequest(), product.productType.jobPriority, mw)
             //def res = post.text
             log.debug("[SERVER RESP] ${response}")
             //response.load(res)
           } catch (IOException e) {
           if (e.message.equals("WATCHER_NOT_SET")) {
              if (managerWatcherService.handleZkWatcher(path, mw)) {
                 continue
              } else {
                 response = path
              }
           } else {
             log.trace(e.message, e)
             engineJob.delete(flush: true)
             log.trace(e.message, e)
             continue
           }
           }
           
           if (!response) {
              engineJob.delete(flush: true)
              
              loop = true
              while (loop) {
                 loop = false
                 try {
                    log.trace ("Delete job ${engineJob.id} for product ${product.name}.")
                    product.refresh()
                    product.note = trimNote(product.note + "ERROR: Problem(s) during deletion: response from ZooKeeper is null.") + "\n"
                    product.updated = new Date().getTime()
                    product.save(flush: true)
                 } catch (org.springframework.dao.OptimisticLockingFailureException e) {
                    loop = true
                 }
              }
           } else {
              //Update engine job with path to queue node
              updateIngEngineJob(engineJob, response)
              
              def productId = product.id
              product.discard()
              product = IngProduct.lock(productId)
              if (product.currentLock == Lock.INVENTORY.toString()) {
              product.currentLock = Lock.PENDING_RESERVED.toString()
              product.note = trimNote(product.note + "INFO: Product '${product.name}' will be purged for replace. Job sent to storage queue ${storage.name}.") + "\n"
              product.updated = new Date().getTime()
              product.save(flush: true)
              }else {
              product.discard()
              }
           }

//           break
//         }
       }
    }
  
  
   def processArchives() {
      Date methodStart = new Date()
      Date timeStart = null
      
      
      timeStart = new Date()
      def loop = true
      log.debug("Process archives")
      // Abort all pending products that have already exceeded
      // the number of retries

      def abortCriteria = IngProduct.createCriteria()
      def aborts = abortCriteria.list {
         'in'("currentLock", [Lock.ARCHIVE.toString()])
         'in'("currentState", [State.PENDING_ARCHIVE.toString(), State.PENDING_ARCHIVE_STORAGE.toString()])
         eq("currentRetries", 0)
         productType {
            federation {
               eq("name", ConfigurationHolder.config.manager_federation)
            }
            order('priority', 'asc')
         }
         maxResults(5)
         order("created", 'asc')
      }

      if (aborts) {
         aborts.each {IngProduct product ->
            //product.discard()
            //product = IngProduct.lock(product.id)
            loop = true
            while (loop) {
               loop = false
               try {
                  product.refresh()
                  product.currentLock = Lock.RESERVED.toString()
                  product.currentState = State.ABORTED.toString()
                  product.note = trimNote(product.note + "{${product.currentState}}: Ingestion exceeds maximum allowable attempts.  Abort ingestion.") + "\n"

                  def ids = product.files.collect {
                     it.id
                  }

                  ids.each {id ->
                     IngDataFile file = IngDataFile.get(id)
                     file.currentLock = Lock.RESERVED.toString()
                     file.save(flush: true)
                  }

                  product.save(flush: true)
                  
                  sigEventService.send(product.productType.eventCategory.name, EventType.Error, "Problem to archive", "{${product.currentState}}: Ingestion exceeds maximum allowable attempts.  Abort ingestion.")
               } catch (org.springframework.dao.OptimisticLockingFailureException e) {
                  //product.discard()
                  loop = true
               }
               log.trace("Looping: 1544")
            }
         }
      }

      log.trace 'Done processing aborts... now query for pending archive jobs'
      def pendingCriteria = IngProduct.createCriteria()
      def pendingArchives = pendingCriteria.list {
         'in'("currentLock", [Lock.ARCHIVE.toString()])
         'in'("currentState", [State.PENDING_ARCHIVE.toString(), State.PENDING_ARCHIVE_STORAGE.toString()])
         and {
            gt("currentRetries", 0)
            le("currentRetries", 10)
         }
         productType {
            federation {
               eq("name", ConfigurationHolder.config.manager_federation)
            }
            order('priority', 'asc')
         }
         maxResults(5)
         order("created", 'asc')
      }

      if (!pendingArchives || pendingArchives.size() == 0) {
         log.debug("processArchives: returning!")
         return
      }
      log.trace("processArchives: granules="+pendingArchives.size())
      log.trace("processArchives: segment 1: "+(new Date().getTime() - timeStart.getTime())+" sec.");

      
      timeStart = new Date()
      // get a list of active engines
      //def engines = IngEngine.findAllByActive(true)
      def storages = IngStorage.withCriteria {
         location {
            eq('active', true)
            eq('stereotype', Stereotype.ARCHIVE.toString())
         }
      }
//      log.trace("processArchives: engines="+engines.size())

      if (storages.size() == 0) {
         log.debug("No active storage found.")
         return
      }
      log.trace("processArchives: segment 2: "+(new Date().getTime() - timeStart.getTime())+" sec.");

      for (IngProduct product in pendingArchives) {
         timeStart = new Date()
         
         log.trace("Trying to lock product ${product.name}")
         //product.discard()
         //product = IngProduct.lock(product.id)
         //log.trace("Locked product ${product.name}")
         if (![Lock.ARCHIVE].contains(Lock.valueOf(product.currentLock)) || 
            ![State.PENDING_ARCHIVE, State.PENDING_ARCHIVE_STORAGE].contains(State.valueOf(product.currentState))) {
            // the product's lock/state was updated since it was queried into memory.
            continue
         }

         long totalSize = product.files.sum {
            it.fileSize
         }

         log.debug("totalSize ${totalSize}")
         log.trace("processArchives: segment 3: "+(new Date().getTime() - timeStart.getTime())+" sec.");

         
         timeStart = new Date()
         // making sure we have storage available to fit this product
//         engines = engines.findAll {engine ->
//            engine.storage.spaceReserved - (engine.storage.spaceUsed + totalSize) > 0
//         }
//         log.trace("processArchives: after filtering engine="+engines.size())

         def storageId = storageService.getStorage(totalSize, Stereotype.ARCHIVE, product.productType.jobPriority.getValue())

         if (storageId == -1) {
            log.debug("Product requires ${totalSize} bytes, which is too large to store in current staging areas, or no storage exists to process this ${product.productType.jobPriority.name()} priority product.")
            loop = true
            while (loop) {
               loop = false
               try {
                  product.refresh()
                  if (State.valueOf(product.currentState) != State.PENDING_ARCHIVE_STORAGE) {
                     product.currentState = State.PENDING_ARCHIVE_STORAGE.toString()
                     product.note = trimNote(product.note + "{${product.currentState}}: Product requires ${totalSize} bytes, which is too large to store in current staging areas, or no storage exists to process this ${product.productType.jobPriority.name()} priority product.") + "\n"
                     product.updated = new Date().getTime()
                     product.save(flush: true)
                  }
               } catch (org.springframework.dao.OptimisticLockingFailureException e) {
                  //product.discard()
                  loop = true
               }
               log.trace("Looping: 1614")
            }

            continue
         }
         log.trace("segment 4: "+(new Date().getTime() - timeStart.getTime())+" sec.");

         
         timeStart = new Date()
         // determine the engine to assign to according to it current
         // load percentage and its available storage space.
         /*
         engines = engines.sort {engine ->
            (int) (IngEngineJob.countByEngine(engine) / (double) engine.maxUserConnections * 100)
            +(engine.storage.spaceReserved - (engine.storage.spaceUsed + totalSize))
         }.reverse()
         log.trace("processArchives: after sorting engines="+engines.size())

         if (log.traceEnabled) {
            log.debug("Sorted engine list.")
            engines.each {engine ->
               def load = (int) (IngEngineJob.countByEngine(engine) / (double) engine.maxUserConnections * 100)
               def memory = (engine.storage.spaceReserved - (engine.storage.spaceUsed + totalSize))
               log.debug("${engine.name}: ${load} + ${memory} = ${load + memory}")
            }
         }
         */
         log.trace("segment 5: "+(new Date().getTime() - timeStart.getTime())+" sec.");

         /*
         // get archive profile to find out where to fetch the file from
         Date startDate = new Date()
         ServiceProfile serviceProfile =
            ServiceProfileFactory.getInstance().createServiceProfileFromMessage(product.archiveText)
         ArchiveProfile archiveProfile = serviceProfile.getProductProfile().getArchiveProfile()
         ArchiveGranule archiveGranule = archiveProfile.getGranules().get(0)
         Date endDate = new Date()
         log.debug("========================== loading AIP: "+((endDate.getTime() - startDate.getTime()) / 1000.0f)+" seconds")
         */

//         log.trace("processArchives: entering loop engines="+engines.size())
         def busycount = 0
//         for (IngEngine worker in engines) {
//            log.trace("\tprocessArchives: in loop worker="+worker.id)
            
            //IngEngine worker = engines[0]
            IngStorage storage = IngStorage.get(storageId)

            // create the ingest request to be sent to the worker engine
            IngestProtocol protocol = IngestProtocol.createEngineIngestRequest(
               Stereotype.ARCHIVE,
               product.productType.federation.name,
               product.productType.federation.id,
               product.productType.name,
               product.productType.id,
               product.name,
               product.id
            )

            // create the place holder to storage the engine response
            //IngestProtocol response = new IngestProtocol()

            // inventory API
            Service service = inventoryService.getInventoryClient()

            
            timeStart = new Date()
            def segStart = new Date()
            URIPath granulePath = URIPath.createURIPath(service.getGranuleArchivePath((Integer)product.inventoryId))
            if (!granulePath) {
               // Unable to get granule path from inventory service
               log.error("Unable to get granule archive path for " + product.name + " from inventory service")
               break
            }
            log.trace("\tsegment 6.1: "+(new Date().getTime() - segStart.getTime())+" sec. inventoryId="+((Integer)product.inventoryId));
            def productFiles = []
            product.files.each {
               
               segStart = new Date()
               def pf = new ProductFile (
                  name: it.name,
                  source:  product.contributeStorage.location.remoteAccessProtocol.toLowerCase() +
                     "://" + product.contributeStorage.location.hostname + "/" + product.contributeStorage.location.remotePath +
                     product.productType.relativePath + product.remoteRelativePath + it.name,
                  checksum: it.checksum,
                  size: it.fileSize
               )
               log.trace("\tsegment 6.2: "+(new Date().getTime() - segStart.getTime())+" sec.");
               
               segStart = new Date()
               productFiles += pf
               IngRemoteSystem host = IngRemoteSystem.findByRootUri(
                     product.contributeStorage.location.remoteAccessProtocol.toLowerCase() +
                     "://" + product.contributeStorage.location.hostname)
               if (host) {
                  pf.sourceUsername = host.username
                  pf.sourcePassword = host.password
                  pf.maxConnections = host.maxConnections
               }
               log.trace("\tsegment 6.3: "+(new Date().getTime() - segStart.getTime())+" sec.");
               
               segStart = new Date()
               pf.destination = granulePath.getPath()
               log.debug("archive name: "+pf.name+", source: "+pf.source+", destination: "+pf.destination+", checksum: "+pf.checksum)
            }

            // check
            segStart = new Date()
            protocol.addFiles = productFiles
            log.trace("\tsegment 6.4: "+(new Date().getTime() - segStart.getTime())+" sec.");
            
            segStart = new Date()
            //String uri = worker.protocol + "://" + worker.hostname + ":" + worker.userPort + worker.urlPattern
            //def post = new Post(url: uri)
            log.trace("\tsegment 6.5: "+(new Date().getTime() - segStart.getTime())+" sec.");

            segStart = new Date()
//            IngEngineJob engineJob = new IngEngineJob(
//               //engine: worker,
//               product: product,
//               operation: product.currentLock,
//               previousState: product.currentState,
//               assigned: new Date().getTime()
//            )
//
//            if (!engineJob.save(flush: true)) {
//               engineJob.errors.each {
//                  log.error it
//               }
//            }
            log.trace("\tsegment 6.6: "+(new Date().getTime() - segStart.getTime())+" sec.");
            log.trace("segment 6: "+(new Date().getTime() - timeStart.getTime())+" sec.");

            
            timeStart = new Date()
            //engineJob.discard()
            //engineJob = IngEngineJob.lock(engineJob.id)
            IngEngineJob engineJob = moveProductState(product, storage, totalSize, false)
            protocol.jobId = engineJob.id

            //post.body = protocol.toRequest()
            //log.trace("[REQUEST] ${post.body}")
            String response = null
            ZkAccess zk = null
            String path = product.productType.name+"/"+product.name
            def watcher = { WatchedEvent event ->
               log.debug ("TransactionService processArchives Watcher: received event " + event.type + " " + event.state)
            }
            try {
               log.trace("\tprocessArchives: sending request")
               zk = ZkFactory.getZk(ConfigurationHolder.config.horizon_zookeeper_url as String, watcher as Watcher)
               log.debug("ZK intance " + zk + " " + zk.getSessionInfo().get("id"))
               response = zk.addToArchiveQueue(storage.name, protocol.toRequest(), product.productType.jobPriority, mw)
               //def res = post.text
               log.debug("[SERVER RESP] ${response}")
               //response.load(res)
            } catch (IOException e) {
            if (e.message.equals("WATCHER_NOT_SET")) {
               if (managerWatcherService.handleZkWatcher(path, mw)) {
                  continue
               } else {
                  response = path
               }
            } else {
               log.trace("\tprocessArchives: sending request failed")
               log.trace(e.message, e)
               revertProductState(product, storage, engineJob, totalSize)
               continue
            }
            }
            log.trace("\tprocessArchives: sending request succeeded")
            log.trace("segment 7: "+(new Date().getTime() - timeStart.getTime())+" sec.");

            //log.trace("Done sending request to ${worker.name}")
            if (!response) {
               log.trace("\tprocessArchives: response error")
               
               timeStart = new Date()
               
               ///product.note = response.description + "\n"
               log.debug("Problem(s) during ingestions: response from ZooKeeper is null.")
					revertProductState(product, storage, engineJob, totalSize)
               //engineJob.delete(flush: true)

//               if (response.errno == Errno.SERVER_BUSY) {
//                  ++busycount
//               }
               log.trace("segment 8: "+(new Date().getTime() - timeStart.getTime())+" sec.");
               
               continue
            } else {
               //Update engine job with path to queue node
               updateIngEngineJob(engineJob, response)
               /*
               log.trace("\tprocessArchives: response ok")
               timeStart = new Date()
               
               
               def productId = product.id
               product.discard()
               product = IngProduct.lock(productId)
               //loop = true
               //while (loop) {
               //   loop = false
               //   try {
               //      product.refresh()
                     log.debug("Done assigning product ${response.product} for ingestion.")
               //      //product.contributeEngine = worker
                     --product.currentRetries
                     product.currentState = State.ASSIGNED.toString()
                     product.note = trimNote(product.note + "{${product.currentState}}: Done assigning product ${response.product} for ingestion.") + "\n"
                     product.updated = new Date().getTime()
                     product.save(flush: true)
               //   } catch (org.springframework.dao.OptimisticLockingFailureException e) {
               //      //product.discard()
               //      loop = true
               //   }
               //   log.trace("Looping: 1769")
               //}
               
               def storageId = storage.id
               storage.discard()
               storage = IngStorage.lock(storageId)
               //loop = true
               //while (loop) {
               //   loop = false
               //   try { 
               //      storage.refresh()
                     storage.spaceUsed += totalSize
                     storage.lastUsed = new Date().getTime()
                     storage.save(flush: true)
               //   } catch (org.springframework.dao.OptimisticLockingFailureException e) {
               //      //storage.discard()
               //      loop = true
               //   }
               //   log.debug("Looping: 1781")
               //}
               log.trace("segment 9: "+(new Date().getTime() - timeStart.getTime())+" sec.");
               
               break
               */
            }
//         }
         log.trace("\tprocessArchives: out of loop busycount="+busycount)
//         if (busycount == engines.size()) {
//            log.info("All active ingest engines are busy now.  Skip 'ADD' job assignment until next cycle.")
//            break
//         }
      }
      //sessionFactory.currentSession.clear()
      
      
      log.trace("segment method: "+(new Date().getTime() - methodStart.getTime())+" sec.");
   }


   def processPurge() {
      def loop = true
      log.debug ("Processing purge.")

      // Process purges in round-robin manner allowing up to 2 deletes per engine
      def storages = IngStorage.withCriteria {
         location {
            eq('active', true)
            eq('stereotype', Stereotype.INGEST.toString())
         }
      }
      for (IngStorage storage in storages) {
      def criteria = IngProduct.createCriteria()
      def products = criteria.list {
         eq('contributeStorage', storage)
         eq('currentLock', Lock.TRASH.toString())
         eq('currentState', State.ARCHIVED.toString())
         productType {
            federation {
               eq("name", ConfigurationHolder.config.manager_federation)
            }
            order('priority', 'asc')
         }
         maxResults(2)
         order('created', 'asc')
      }

      if (!products || products.size() == 0) {
         continue
      }

      products = products.findAll {IngProduct product ->
         (new Date().time - product.archivedAt) / (60 * 1000) >= product.productType.purgeRate
      }

      if (!products || products.size() == 0) {
         log.trace 'No products to be trashed.'
         continue
      }

      log.trace "${products.size()} products to be purged from trashbin."

      for (IngProduct product in products) {
         //product.discard()
         //product = IngProduct.lock(product.id)
         if (Lock.valueOf(product.currentLock) != Lock.TRASH || 
            State.valueOf(product.currentState) != State.ARCHIVED) {
            // the product's lock/state was updated since it was queried into memory.
            continue
         }

         //IngEngine engine = product.contributeEngine

         if (!storage.location.active) {
            log.debug("Cannot purge product ${product.name} from trashbin because storage ${product.contributeStorage.name} is inactive")
            continue
         }

//         if (!engine.active || !engine.isOnline) {
//            log.trace("Engine '${engine.federation.name}:${engine.name}' is unreachable for purging product '${product.name}'.")
//            continue
//         }

         if (product.files.size() == 0) {
            log.info("Nothing needs to be purged for product '${product.name}'.  Purge product record.")
            product.delete(flush: true)
            continue
         }

         long totalSize = product.files.sum {
            it.fileSize
         }

         IngEngineJob engineJob = new IngEngineJob(
//            engine: engine,
            product: product,
            operation: product.currentLock,
            previousState: product.currentState,
            assigned: new Date().getTime(),
            priority: product.productType.jobPriority.getValue(),
            contributeStorage: storage
         )
         engineJob.save(flush: true)

         log.trace ("Product ${product.name} delete job ${engineJob.id}.")

         //engineJob.discard()
         //engineJob = IngEngineJob.lock(engineJob.id)

         // create the ingest request to be sent to the worker engine
         IngestProtocol protocol = IngestProtocol.createEngineDeleteRequest(
            Stereotype.INGEST,
            product.productType.federation.name,
            product.productType.federation.id,
            product.productType.name,
            product.productType.id,
            product.name,
            product.id
         )
         protocol.totalSize = totalSize
         protocol.jobId = engineJob.id

         def destinations = []
         product.files.each {file ->
            destinations << storage.location.localPath +
               product.productType.relativePath +
               product.localRelativePath +
               file.name
         }

         // also delete the backup SIP/AIP message
         destinations <<  storage.location.localPath +
            product.productType.relativePath +
            product.localRelativePath + product.name + ".sip.xml"

         protocol.deletes = destinations

//         String uri = engine.protocol +
//            "://" + engine.hostname +
//            ":" + engine.userPort +
//            engine.urlPattern

//         def post = new Post(url: uri)
//         post.body = protocol.toRequest()

//         IngestProtocol response = new IngestProtocol()
         String response = null
         ZkAccess zk = null
         String path = product.productType.name+"/"+product.name
         def watcher = { WatchedEvent event ->
            log.debug ("TransactionService:processPurage Watcher: received event " + event.type + " " + event.state)
         }
         try {
//            def res = post.text
            zk = ZkFactory.getZk(ConfigurationHolder.config.horizon_zookeeper_url as String, watcher as Watcher)
            log.debug("ZK intance " + zk + " " + zk.getSessionInfo().get("id"))
            response = zk.addToIngestQueue(product.contributeStorage.name, protocol.toRequest(), product.productType.jobPriority, mw)
            log.debug("[SERVER RESP] ${response}")
//            response.load(res)
         } catch (IOException e) {
            if (e.message.equals("WATCHER_NOT_SET")) {
               if (managerWatcherService.handleZkWatcher(path, mw)) {
                  continue
               } else {
                  response = path
               }
            } else {
            log.debug(e.message, e)
            engineJob.delete(flush: true)
            continue
            }
         }
         
         if (!response) {
            engineJob.delete(flush: true)
            
            loop = true
            while (loop) {
               loop = false
               try {
                  product.refresh()
                  product.note = trimNote(product.note + "ERROR: Problem(s) during deletion: response from ZooKeeper is null.") + "\n"
                  product.updated = new Date().getTime()
                  product.save(flush: true)
               } catch (org.springframework.dao.OptimisticLockingFailureException e) {
                  loop = true
               }
            }  
         } else {
            //Update engine job with path to queue node
            updateIngEngineJob(engineJob, response)
            
            def productId = product.id
            product.discard()
            product = IngProduct.lock(productId)
            if (product.currentLock == Lock.TRASH.toString()) {
            product.currentLock = Lock.PENDING_RESERVED.toString()
            product.note = trimNote(product.note + "INFO: Product '${product.name}' will be purged. Job sent to storage queue ${product.contributeStorage.name}.") + "\n"
            product.updated = new Date().getTime()
            product.save(flush: true)
            } else {
               product.discard()
            }
         }
      }
      }
   }

  /**
   * Method to send ping signal to all engines
   */
  def pingAllEngines() {
    def engines = IngEngine.getAll()
    if (engines) {
      engines.each {engine ->
        ping(engine)
      }
    }
  }

  /**
   * Method to check for active jobs that has been active for more than 30 minutes.  It will
   * send a query to the assigned engine to see the job is still being handled.
   */
   def checkActiveJob() {
      Date cutoff = new Date(new Date().time - 1800000L) // cutoff time: 30 mins ago

      // only check the jobs that were assigned more than 30 minutes ago
      def firstIter = true
      def jobs = null
      int offset = 0
      int maxPerPage = 10
      // Page through all the stuck jobs otherwise we might miss the next 10
      while (firstIter || jobs.size() >= maxPerPage) {
      jobs = IngEngineJob.createCriteria().list {
         lt("assigned", cutoff.getTime())
         product {
            productType {
               federation {
                  eq("name", ConfigurationHolder.config.manager_federation)
               }
            }
         }
         maxResults(maxPerPage)
         firstResult(offset)
         order("assigned", "asc")
      }
      firstIter = false
      offset += maxPerPage

      if (!jobs || jobs.size() == 0) return

      def loop = true

      for (IngEngineJob job in jobs) {
         IngProduct product = job.product

         ZkAccess zk = null
         def watcher = { WatchedEvent event ->
            log.debug ("TransactionService:checkActiveJob Watcher: received event " + event.type + " " + event.state)
         }
         try {
            zk = ZkFactory.getZk(ConfigurationHolder.config.horizon_zookeeper_url as String, watcher as Watcher)
            log.debug("ZK intance " + zk + " " + zk.getSessionInfo().get("id"))
            if (zk.nodeExists(job.path)) {
               //Fire sigevent
               sigEventService.send(
                  product.productType.eventCategory.name,
                  EventType.Warn,
                  "Job with id="+job.id+" has been stuck for more than 1800000 ms. Check that engine is registered to process queue node at " + job.path,
                  "Job with id="+job.id+" has been stuck for more than 1800000 ms. Check that engine is registered to process queue node at " + job.path
               )
               log.debug("Node " + job.path + " for " + product.name + " is still in zk queue")
            } else {
               String path = product.productType.name+"/"+product.name
               log.debug("Node " + job.path + " for " + product.name + " has been removed from queue. Checking job node " + path)
               if (zk.processNodeExists(path)) {
                  String s = zk.readProcessNode(path, null)
                  try {
                     def xml = new XmlSlurper().parseText(s)
                     managerWatcherService.handleZkWatcher(path, null)
                  } catch (org.xml.sax.SAXParseException ex) {
                     log.debug("Job node " + path + " contains " + s)
                     String[] nodes = s.split(",")
                     // check if engine is offline
                     if (zk.checkRegistration(nodes[1], nodes[0]) == RegistrationStatus.OFFLINE) {
                     // delete job node from zk
                     zk.removeProcessNode(path)
                     
                     String previousState = job.previousState
                     String previousLock = job.operation
                     
                     Lock previousLockEnum = Lock.valueOf(previousLock)
                     State previousStateEnum = State.valueOf(previousState)
                     // put storage back as needed
                     if ([Lock.ADD, Lock.REPLACE, Lock.ARCHIVE].find{it == Lock.valueOf(job.operation)}
                        && [State.PENDING, State.PENDING_STORAGE, State.PENDING_ARCHIVE, State.PENDING_ARCHIVE_STORAGE].find{it == State.valueOf(job.previousState)}) {
                        long totalSize = product.files.sum {
                           it.fileSize
                        }
                        storageService.updateStorage(-totalSize, new Date().getTime(), job.contributeStorage.location.id)
                     }
                     
                     // delete the job from the active job list
                     loop = true
                     while (loop) {
                        loop = false
                        try {
                           job.refresh()
                           if (!job) break
                           job.delete(flush: true)
                        } catch (org.springframework.dao.OptimisticLockingFailureException e) {
                           loop = true
                        }
                        log.trace("Looping: 2178")
                     }
                     
                     if (State.valueOf(product.currentState) == State.PENDING_ASSIGNED || State.valueOf(product.currentState) == State.ASSIGNED || Lock.valueOf(product.currentLock) == Lock.PENDING_RESERVED || Lock.valueOf(product.currentLock) == Lock.RESERVED) {
                     loop = true
                     while (loop) {
                        loop = false
                        try {
                           product.refresh()
                           log.trace("reset product state: ${previousState} lock: ${previousLock}")
                           product.currentState = previousState
                           product.currentLock = previousLock
                           ++product.currentRetries
                           product.save(flush: true)
                        } catch (org.springframework.dao.OptimisticLockingFailureException e) {
                           loop = true
                        }
                        log.trace("Looping: 2164")
                     }
                     }
                     } else {
                        //Fire sigevent
                        sigEventService.send(
                           product.productType.eventCategory.name,
                           EventType.Warn,
                           "Job with id="+job.id+" has been stuck for more than 1800000 ms. Check that engine (${nodes[0]}) registered with storage (${nodes[1]}) is still processing job",
                           "Job with id="+job.id+" has been stuck for more than 1800000 ms. Check that engine (${nodes[0]}) registered with storage (${nodes[1]}) is still processing job"
                        )
                     }
                  }
               } else {
                  log.debug("Job node " + path + " does not exist")
                  // check if job still exists
                  boolean revert = true
                  int retries = 0
                  while (retries <= 5) {
                     try {
                        job.refresh()
                        log.debug("[checkActiveJobs] Engine job ${job.id} still exists sleeping for 3 seconds")
                        if (retries < 5) {
                           Thread.sleep(3000)
                        }
                     } catch (HibernateObjectRetrievalFailureException e) {
                        revert = false
                        log.debug("[checkActiveJobs] Engine job ${job.id} deleted")
                        break
                     }
                     retries++
                  }
                  if (revert) {
                  log.debug("[checkActiveJobs] Reverting product state. Engine job ${job.id}")
                  String previousState = job.previousState
                  String previousLock = job.operation
                  
                  Lock previousLockEnum = Lock.valueOf(previousLock)
                  State previousStateEnum = State.valueOf(previousState)
                  // put storage back as needed
                  if ([Lock.ADD, Lock.REPLACE, Lock.ARCHIVE].find{it == Lock.valueOf(job.operation)}
                     && [State.PENDING, State.PENDING_STORAGE, State.PENDING_ARCHIVE, State.PENDING_ARCHIVE_STORAGE].find{it == State.valueOf(job.previousState)}) {
                     long totalSize = product.files.sum {
                        it.fileSize
                     }
                     storageService.updateStorage(-totalSize, new Date().getTime(), job.contributeStorage.location.id)
                  }

                  // delete the job from the active job list
                  loop = true
                  while (loop) {
                     loop = false
                     try {
                        job.refresh()
                        if (!job) break
                        job.delete(flush: true)
                     } catch (org.springframework.dao.OptimisticLockingFailureException e) {
                        loop = true
                     }
                     log.trace("Looping: 2178")
                  }
                  
                  if (State.valueOf(product.currentState) == State.PENDING_ASSIGNED || State.valueOf(product.currentState) == State.ASSIGNED || Lock.valueOf(product.currentLock) == Lock.PENDING_RESERVED || Lock.valueOf(product.currentLock) == Lock.RESERVED) {
                  loop = true
                  while (loop) {
                     loop = false
                     try {
                        product.refresh()
                        log.trace("reset product state: ${previousState} lock: ${previousLock}")
                        product.currentState = previousState
                        product.currentLock = previousLock
                        ++product.currentRetries
                        product.save(flush: true)
                     } catch (org.springframework.dao.OptimisticLockingFailureException e) {
                        loop = true
                     }
                     log.trace("Looping: 2164")
                  }
                  }
                  }
               }
            }
         } catch (IOException e) {
            log.error(e.message, e)
         }
      }
      }
   }

   String trimNote(String note) {
      def buf = new StringBuffer(note)
      buf.length = MAX_NOTE
      return buf.toString().trim()
   }
   
   /**
    * Method to ping inventory to see if it is running
    */
   def pingInventory() {
      Service service = inventoryService.getAuthInventoryClient()
      
      def inventoryUrl = ConfigurationHolder.config.gov.nasa.podaac.manager.inventory.host+":"+ConfigurationHolder.config.gov.nasa.podaac.manager.inventory.port

      if (service.isOnline()) {      
         log.debug("Inventory service is online: ${inventoryUrl}")
      } else {
         log.debug("Inventory service is offline: ${inventoryUrl}")
         sigEventService.send(
            TransactionService.SIG_EVENT_DMAS_CATEGORY,
            EventType.Warn,
            "Inventory service at ${inventoryUrl} seems to be down.",
            "Inventory service at ${inventoryUrl} seems to be down."
         )
      }
   }
   
   /**
    * Reverts product current state. Puts back storage space used that has been allocated.
    * Deletes specified IngEngineJob. This method is used for error handling e.g. when manager 
    * cannot communicate with zookeeper.
    */
   private void revertProductState(IngProduct product, IngStorage storage, IngEngineJob engineJob, long totalSize) {
      def productId = product.id
      product.discard()
      product = IngProduct.lock(productId)
      product.currentState = engineJob.previousState
      product.updated = new Date().getTime()
      product.save(flush: true)

      storageService.updateStorage(-totalSize, new Date().getTime(), storage.location.id)

      engineJob.delete(flush: true)
   }
   
   /**
    * Returns IngEngineJob that has been written to db.  Updates storage space used 
    * and sets product's contribute storage.
    */
   private IngEngineJob moveProductState(IngProduct product, IngStorage storage, long totalSize, boolean setContributeStorage) {
      IngEngineJob engineJob = new IngEngineJob(
         //engine: worker,
         product: product,
         operation: product.currentLock,
         previousState: product.currentState,
         assigned: new Date().getTime(),
         priority: product.productType.jobPriority.getValue(),
         contributeStorage: storage
      )

      if (!engineJob.save(flush: true)) {
         engineJob.errors.each {
            log.error it
         }
      }
      
      def productId = product.id
      product.discard()
      product = IngProduct.lock(productId)
      --product.currentRetries
      if (setContributeStorage) {
         product.contributeStorage = storage
      }
      product.currentState = State.PENDING_ASSIGNED.toString()
      product.note = trimNote(product.note + "{${product.currentState}}: Done sending product ${product.name} to storage queue ${storage.name} in ZooKeeper for ingestion.") + "\n"
      product.updated = new Date().getTime()
      product.save(flush: true)
      
      storageService.updateStorage(totalSize, new Date().getTime(), storage.location.id)
      return engineJob
   }
   
   /**
    * Updates specified IngEngineJob with specified zookeeper path.
    */
   private void updateIngEngineJob(IngEngineJob engineJob, String path) {
      try {
         def engineJobId = engineJob.id
         engineJob.discard()
         engineJob = IngEngineJob.lock(engineJobId)
         engineJob.path = path
         engineJob.save(flush: true)
      } catch (Exception e) {
         log.debug(e.message, e)
         log.debug("Unable to save engine job's path to queue node")
      }
   }
}
