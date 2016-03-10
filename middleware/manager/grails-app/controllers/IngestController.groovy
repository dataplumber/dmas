import gov.nasa.jpl.horizon.api.Errno
import gov.nasa.jpl.horizon.api.protocol.IngestProtocol
import gov.nasa.jpl.horizon.api.Product
import gov.nasa.jpl.horizon.api.Lock
import gov.nasa.jpl.horizon.api.State
import gov.nasa.jpl.horizon.api.ProductFile

import gov.nasa.jpl.horizon.sigevent.api.EventType

class IngestController {
   private static final String SIG_EVENT_CATEGORY = "UNCATEGORIZED"

   def transactionService
   def sessionFactory
   def sigEventService
   
   /*
   def debug = {
      render(text: "<debug />",
            contentType: "text/xml",
            encoding: "UTF-8")
   }
   */

   def init = {
      log.debug("calling IngestRequest.loadRequest with params ${params.horizon.params}")
      IngestProtocol protocol = new IngestProtocol()
      protocol.load(params.horizon.params)
      log.debug("return from loadRequest")

      flash.protocol = protocol

      if (transactionService.checkUser(protocol) != Errno.OK) {
         log.debug("${protocol.errno}: ${protocol.description}.")
         render(text: protocol.toString(),
                contentType: "text/xml",
                encoding: "UTF-8")
      } else {
         transactionService.createUserSession(protocol)
         log.debug "RESPONSE: ${protocol.toString()}."
         render(text: protocol.toString(),
                contentType: "text/xml",
                encoding: "UTF-8")
      }
   }

   def index = {
      redirect {action: list}
   }

   def report = {
      def protocol = flash.protocol

      log.debug("${protocol.errno}: ${protocol.description}.")
      render(text: protocol.toString(),
             contentType: "text/xml",
             encoding: "UTF-8")
   }

   def add = {
      try {
      log.debug "Executing action $actionName ${params.horizon.params}"
      IngestProtocol protocol = new IngestProtocol()
      protocol.load(params.horizon.params)

      flash.protocol = protocol

      if (transactionService.verifyUserSession(protocol) != Errno.OK) {
         log.debug("${protocol.errno}: ${protocol.description}.")
         render(text: protocol.toString(),
                contentType: "text/xml",
                encoding: "UTF-8")
      } else {
         log.debug("calling service method")

         def methodTime = new Date()
         transactionService.addProduct(protocol)
         log.trace("controller: add: " + (new Date().getTime() - methodTime.getTime()));

         log.debug("${protocol.errno}: ${protocol.description}.")
         render(text: protocol.toString(),
                contentType: "text/xml",
                encoding: "UTF-8")
      }
      } catch(Exception exception) {
         log.debug("AARRGGG!!!!", exception)
         render(text: '<ohno />', contentType: "text/xml", encoding: "UTF-8")
      }
   }


   def addupdate = {
      try {
      log.debug "Executing action ${actionName}"
      IngestProtocol protocol = new IngestProtocol()
      protocol.load(params.horizon.params)

      log.debug(params.horizon.params)

      flash.protocol = protocol
      if (transactionService.verifyUserSession(protocol) != Errno.OK) {
         log.debug("${protocol.errno}: ${protocol.description}.")
         render(text: protocol.toString(),
                contentType: "text/xml",
                encoding: "UTF-8")
      } else {
         def methodTime = new Date()
         transactionService.addProductUpdate(protocol)
         log.trace("controller: addupdate: " + (new Date().getTime() - methodTime.getTime()));

         log.debug("${protocol.errno}: ${protocol.description}.")
         render(text: protocol.toString(),
                contentType: "text/xml",
                encoding: "UTF-8")
      }
      } catch(Exception exception) {
         log.debug("AARRGGG!!!!", exception)
         render(text: '<ohno />', contentType: "text/xml", encoding: "UTF-8")
      }
   }


   def replace = {
      try {
      log.debug "Executing action ${actionName}"
      IngestProtocol protocol = new IngestProtocol()
      protocol.load(params.horizon.params)

      flash.protocol = protocol

      if (transactionService.verifyUserSession(protocol) != Errno.OK) {
         log.debug("${protocol.errno}: ${protocol.description}.")
         render(text: protocol.toString(),
                contentType: "text/xml",
                encoding: "UTF-8")
      } else {
         def methodTime = new Date()
         transactionService.replaceProduct(protocol)
         log.trace("controller: replace: " + (new Date().getTime() - methodTime.getTime()));

         log.debug("${protocol.errno}: ${protocol.description}.")
         render(text: protocol.toString(),
                contentType: "text/xml",
                encoding: "UTF-8")
      }
      } catch(Exception exception) {
         log.debug("AARRGGG!!!!", exception)
         render(text: '<ohno />', contentType: "text/xml", encoding: "UTF-8")
      }
   }


   def deleteupdate = {
      try {
      log.debug "Executing action ${actionName}"
      IngestProtocol protocol = new IngestProtocol()
      protocol.load(params.horizon.params)

      log.debug(params.horizon.params)

      flash.protocol = protocol
      if (transactionService.verifyUserSession(protocol) != Errno.OK) {
         log.debug("${protocol.errno}: ${protocol.description}.")
         render(text: protocol.toString(),
                contentType: "text/xml",
                encoding: "UTF-8")
      } else {
         def methodTime = new Date()
         transactionService.deleteProductUpdate(protocol)
         log.trace("controller: deleteupdate: " + (new Date().getTime() - methodTime.getTime()));

         log.debug("${protocol.errno}: ${protocol.description}.")
         render(text: protocol.toString(),
                contentType: "text/xml",
                encoding: "UTF-8")
      }
      } catch(Exception exception) {
         log.debug("AARRGGG!!!!", exception)
         render(text: '<ohno />', contentType: "text/xml", encoding: "UTF-8")
      }
   }


   def list = {
      try {
      log.trace "Executing action ${actionName}"
      IngestProtocol protocol = new IngestProtocol()
      protocol.load(params.horizon.params)

      log.debug "LIST REQUEST: ${protocol.toString()}"

      flash.protocol = protocol

      def methodTime = new Date()
      if (transactionService.verifyUserSession(protocol) != Errno.OK) {
         log.debug("${protocol.errno}: ${protocol.description}.")
         render(text: protocol.toString(),
                contentType: "text/xml",
                encoding: "UTF-8")
      } else if (!protocol.productType) {
         protocol.errno = Errno.INVALID_TYPE
         protocol.description = 'Product type is missing.'
         log.debug("${protocol.errno}: ${protocol.description}.")
         render(text: protocol.toString(),
                contentType: "text/xml",
                encoding: "UTF-8")
      } else {
         IngProductType productType = IngProductType.findByName(protocol.productType)
         if (!productType) {
            protocol.errno = Errno.INVALID_TYPE
            protocol.description = "Product type '${protocol.productType}' not found."
            log.debug("${protocol.errno}: ${protocol.description}.")
            render(text: protocol.toString(),
                   contentType: "text/xml",
                   encoding: "UTF-8")
         } else if (!protocol.product) {
            protocol.errno = Errno.PRODUCT_NOT_FOUND
            protocol.description = 'Product name is missing.'
            log.debug("${protocol.errno}: ${protocol.description}.")
            render(text: protocol.toString(),
                   contentType: "text/xml",
                   encoding: "UTF-8")
         } else {
            def criteria = IngProduct.createCriteria()
            def products = criteria.list {
               eq("name", protocol.product)
               eq("productType", productType)
            }
            if (!products || products.size == 0) {
               protocol.errno = Errno.PRODUCT_NOT_FOUND
               protocol.description = "Product '[${productType.name}:${protocol.product}]' not found."
               log.trace protocol.toString()
               render(text: protocol.toString(),
                      contentType: "text/xml",
                      encoding: "UTF-8")
            } else {
               products.each {IngProduct prod ->
                  Product product = new Product(name: prod.name,
                                                state: State.valueOf(prod.currentState),
                                                lock: Lock.valueOf(prod.currentLock),
                                                note: prod.note,
                                                archiveNote: prod.archiveNote,
                                                createdTime: (prod.created) ? new Date(prod.created) : null,
                                                stagedTime: (prod.completed) ? new Date(prod.completed) : null,
                                                archivedTime: (prod.archivedAt) ? new Date(prod.archivedAt) : null)
                  if (prod.currentState == State.ARCHIVED) {
                     product.metadataText = prod.archiveText.toString()
                  } else if (prod.currentState == State.INVENTORIED) {
                     product.metadataText = prod.completeText.toString()
                  } else {
                     product.metadataText = prod.initialText.toString()
                  }
                  def prodFiles = []
                  if (prod.files) {
                     prod.files.each {IngDataFile file ->
                        ProductFile pFile = new ProductFile(
                              name: file.name,
                              size: file.fileSize,
                              checksum: file.checksum
                        )
                        prodFiles << pFile
                     }
                  }
                  product.productFiles = prodFiles
                  protocol.addProduct(product)
               }
               protocol.errno = Errno.OK
               log.debug("LIST result: "+protocol.toString())
               render(text: protocol.toString(),
                      contentType: "text/xml",
                      encoding: "UTF-8")
            }
         }
      }

      log.trace("controller: list: " + (new Date().getTime() - methodTime.getTime()));
      } catch(Exception exception) {
         log.debug("AARRGGG!!!!", exception)
         render(text: '<ohno />', contentType: "text/xml", encoding: "UTF-8")
      }
   }

   def moveupdate = {
      try {
      log.debug "Executing action $actionName"
      IngestProtocol protocol = new IngestProtocol()
      protocol.load(params.horizon.params)
      log.debug(params.horizon.params)
      flash.protocol = protocol
      if (transactionService.verifyUserSession(protocol) != Errno.OK) {
         log.debug("${protocol.errno}: ${protocol.description}.")
         render(text: protocol.toString(),
                contentType: "text/xml",
                encoding: "UTF-8")
      } else {
         def methodTime = new Date()
         transactionService.moveProductUpdate(protocol)
         log.trace("controller: moveupdate: " + (new Date().getTime() - methodTime.getTime()));

         log.debug("${protocol.errno}: ${protocol.description}.")
         render(text: protocol.toString(),
                contentType: "text/xml",
                encoding: "UTF-8")
      }
      } catch(Exception exception) {
         log.debug("AARRGGG!!!!", exception)
         render(text: '<ohno />', contentType: "text/xml", encoding: "UTF-8")
      }
   }

   def archiveupdate = {
      try {
      log.debug "Executing action: ${actionName}"
      IngestProtocol protocol = new IngestProtocol()
      protocol.load(params.horizon.params)
      log.debug(params.horizon.params)
      flash.protcool = protocol
      if (transactionService.verifyUserSession(protcol) != Errno.OK) {
         log.debug("${protocol.errno}: ${protocol.description}.")
         render(text: protocol.toString(),
                contentType: "text/xml",
                encoding: "UTF-8")
      } else {
         def methodTime = new Date()
         transactionService.archiveProductUpdate(protocol)
         log.trace("controller: archiveupdate: " + (new Date().getTime() - methodTime.getTime()));

         log.debug("${protocol.errno}: ${protocol.description}.")
         render(text: protocol.toString(),
                contentType: "text/xml",
                encoding: "UTF-8")
      }
      } catch(Exception exception) {
         log.debug("AARRGGG!!!!", exception)
         render(text: '<ohno />', contentType: "text/xml", encoding: "UTF-8")
      }
   }
}
