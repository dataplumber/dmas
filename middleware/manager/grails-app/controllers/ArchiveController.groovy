/*
import gov.nasa.podaac.archive.xml.Packet
import gov.nasa.podaac.archive.xml.Packet.IdList
import gov.nasa.podaac.archive.external.wsm.Utilities

import gov.nasa.podaac.inventory.model.GranuleReference
*/

/**
 * 
 */
class ArchiveController {
   /*
    * Uncomment below once archive sutff is ready... 
    *
   def archiveService
   def pagingService
   
   def datasetIds = {
      def ids = archiveService.getDatasetIds()
      Collections.sort(ids)
      
      def pagingManager = pagingService.createPaging(100, ids.size(), params.page)
      def idList = new IdList()
      for(int index in pagingManager.getRange()) {
         idList.getIds().add(ids.get(index))
      }
      def packet = new Packet()
      packet.setPage(pagingManager.page)
      packet.setOf(pagingManager.getPages())
      packet.setIdList(idList)
      
      sendResponse(response, packet)
   }
   
   def dataset = {
      def datasetKeys = []
      if(params.datasetId) {
         def datasetIdsString = Arrays.asList(params.datasetId)
         datasetIdsString.each {datasetIdString ->
            datasetKeys.add(Integer.parseInt(datasetIdString))
         }
         //Collections.sort(datasetKeys)
      } else if(params.datasetShortName) {
         datasetKeys = Arrays.asList(params.datasetShortName)
         //Collections.sort(datasetKeys)
      }
      
      try {
         if(datasetKeys.size() > 0) {
            def packet = new Packet()
            
            def pagingManager = pagingService.createPaging(10, datasetKeys.size(), params.page)
            for(int index in pagingManager.getRange()) {
               def dataset = archiveService.getDataset(datasetKeys.get(index))
               Utilities.getInstance().add(packet, dataset)
            }
            packet.setPage(pagingManager.page)
            packet.setOf(pagingManager.getPages())
            sendResponse(response, packet)
         } else {
            throw new Exception("no dataset found!")
         }
      } catch(Exception exception) {
         sendErrorResponse(response, "Faield to retrieve datasets")
      }
   }
   
   def granuleSize = {
      try {
         if(params.datasetId) {
            def datasetIdsString = Arrays.asList(params.datasetId)
            def datasetIds = []
            datasetIdsString.each {datasetIdString ->
               datasetIds.add(Integer.parseInt(datasetIdString))
            }
            
            def packet = new Packet()
            def pagingManager = pagingService.createPaging(5, datasetIds.size(), params.page)
            for(int index in pagingManager.getRange()) {
               def granuleSize = archiveService.getGranuleSize(datasetIds.get(index))
               Utilities.getInstance().add(packet, granuleSize)
            }
            packet.setPage(pagingManager.page)
            packet.setOf(pagingManager.getPages())
            
            sendResponse(response, packet)
         } else {
            throw new Exception("no dataset id found")
         }
      } catch(Exception exception) {
         sendErrorResponse(response, "Faield to retrieve granule size.")
      }
   }
   
   def granuleIds = {
      try {
         if(params.datasetId) {
            def datasetId = Integer.parseInt(params.datasetId)
            def granuleIds = archiveService.getGranuleIds(datasetId)
            Collections.sort(granuleIds)
            
            def packet = new Packet()
            def pagingManager = pagingService.createPaging(1000, granuleIds.size(), params.page)
            for(int index in pagingManager.getRange()) {
               Utilities.getInstance().add(packet, granuleIds.get(index))
            }
            packet.setPage(pagingManager.page)
            packet.setOf(pagingManager.getPages())
            
            sendResponse(response, packet)
         } else {
            throw new Exception("no dataset id found")
         }
      } catch(Exception exception) {
         sendErrorResponse(response, "Faield to retrieve granule ids.")
      }
   }
   
   def granule = {
      try {
         if(params.granuleId) {
            def granuleIdsString = Arrays.asList(params.granuleId)
            def granuleIds = []
            granuleIdsString.each {granuleIdString ->
               granuleIds.add(Integer.parseInt(granuleIdString))
            }
            
            def packet = new Packet()
            def pagingManager = pagingService.createPaging(5, granuleIds.size(), params.page)
            for(int index in pagingManager.getRange()) {
               def granule = archiveService.getGranule(granuleIds.get(index))
               Utilities.getInstance().add(packet, granule)
            }
            packet.setPage(pagingManager.page)
            packet.setOf(pagingManager.getPages())
            
            sendResponse(response, packet)
         } else {
            throw new Exception("no granule id found")
         }
      } catch(Exception exception) {
         log.debug(exception)
         sendErrorResponse(response, "Faield to retrieve granule.")
      }
   }
   
   def granuleReferences = {
      try {
         if(params.datasetId) {
            def datasetId = Integer.parseInt(params.datasetId)
            def granuleReferences = archiveService.getGranuleReferences(datasetId)
            Collections.sort(granuleReferences, new Comparator<GranuleReference>() {
               public int compare(GranuleReference gr1, GranuleReference gr2) {
                  return gr1.getGranuleId().compareTo(gr2.getGranuleId())
               }
            })
            
            def packet = new Packet()
            def pagingManager = pagingService.createPaging(1000, granuleReferences.size(), params.page)
            for(int index in pagingManager.getRange()) {
               Utilities.getInstance().add(packet, granuleReferences.get(index))
            }
            packet.setPage(pagingManager.page)
            packet.setOf(pagingManager.getPages())
            
            sendResponse(response, packet)
         } else {
            throw new Exception("no dataset id found")
         }
      } catch(Exception exception) {
         log.debug(exception)
         sendErrorResponse(response, "Faield to retrieve granule ids.")
      }
   }
   
   def updateGranuleStatus = {
      try {
         int granuleId = this.getGranuleId(params)
         if(params.status) {
            archiveService.updateGranuleStatus(granuleId, params.status)
            
            def pagingManager = pagingService.createPaging(1, 1, 1)
            def packet = new Packet()
            Utilities.getInstance().add(packet, 0)
            packet.setPage(pagingManager.page)
            packet.setOf(pagingManager.getPages())
            
            sendResponse(response, packet)
         } else {
            throw new Exception("Missing parameters.")
         }
      } catch(Exception exception) {
         sendErrorResponse(response, "Faield to update granule status.")
      }
   }
   
   def deleteGranule = {
      try {
         int granuleId = this.getGranuleId(params)
         archiveService.deleteGranule(granuleId)
         
         def pagingManager = pagingService.createPaging(1, 1, 1)
         def packet = new Packet()
         Utilities.getInstance().add(packet, 0)
         packet.setPage(pagingManager.page)
         packet.setOf(pagingManager.getPages())
         
         sendResponse(response, packet)
      } catch(Exception exception) {
         sendErrorResponse(response, "Faield to update granule status.")
      }
   }
   
   def updateApiArchive = {
      try {
         int granuleId = this.getGranuleId(params)
         if((params.status) && (params.type) && (params.name)) {
            archiveService.updateApiArchive(granuleId, params.type, params.status, params.name)
            
            def pagingManager = pagingService.createPaging(1, 1, 1)
            def packet = new Packet()
            Utilities.getInstance().add(packet, 0)
            packet.setPage(pagingManager.page)
            packet.setOf(pagingManager.getPages())
            
            sendResponse(response, packet)
         } else {
            throw new Exception("Missing parameters.")
         }
      } catch(Exception exception) {
         sendErrorResponse(response, "Faield to update granule status.")
      }
   }
   
   def updateAipGranuleArchive = {
      try {
         int granuleId = this.getGranuleId(params)
         if((params.status) && (params.name)) {
            archiveService.updateAipGranuleArchive(granuleId, params.status, params.name)
            
            def pagingManager = pagingService.createPaging(1, 1, 1)
            def packet = new Packet()
            Utilities.getInstance().add(packet, 0)
            packet.setPage(pagingManager.page)
            packet.setOf(pagingManager.getPages())
            
            sendResponse(response, packet)
         } else {
            throw new Exception("Missing parameters.")
         }
      } catch(Exception exception) {
         sendErrorResponse(response, "Faield to update granule status.")
      }
   }
   
   def updateAipGranuleReference = {
      try {
         int granuleId = this.getGranuleId(params)
         if((params.status) && (params.path)) {
            archiveService.updateAipGranuleReference(granuleId, params.status, params.path)
            
            def pagingManager = pagingService.createPaging(1, 1, 1)
            def packet = new Packet()
            Utilities.getInstance().add(packet, 0)
            packet.setPage(pagingManager.page)
            packet.setOf(pagingManager.getPages())
            
            sendResponse(response, packet)
         } else {
            throw new Exception("Missing parameters.")
         }
      } catch(Exception exception) {
         sendErrorResponse(response, "Faield to update granule status.")
      }
   }
   
   def updateGranuleArchive = {
      if(params.checksum) {
         this.updateGranuleArchiveChecksum()
      } else if(params.size) {
         this.updateGranuleArchiveSize()
      } else if(params.status) {
         this.updateGranuleArchiveStatus()
      } else {
         sendErrorResponse(response, "Missing params.")
      }
   }
   
   def updateGranuleArchiveChecksum = {
      try {
         int granuleId = this.getGranuleId(params)
         if((params.name) && (params.checksum)) {
            archiveService.updateGranuleArchiveChecksum(granuleId, params.name, params.checksum)
            
            def pagingManager = pagingService.createPaging(1, 1, 1)
            def packet = new Packet()
            Utilities.getInstance().add(packet, 0)
            packet.setPage(pagingManager.page)
            packet.setOf(pagingManager.getPages())
            
            sendResponse(response, packet)
         } else {
            throw new Exception("Missing parameters.")
         }
      } catch(Exception exception) {
         sendErrorResponse(response, "Faield to update granule status.")
      }
   }
   
   def updateGranuleArchiveSize = {
      try {
         int granuleId = this.getGranuleId(params)
         if((params.name) && (params.size)) {
            long size = Long.parseLong(params.size)
            archiveService.updateGranuleArchiveSize(granuleId, params.name, size)
            
            def pagingManager = pagingService.createPaging(1, 1, 1)
            def packet = new Packet()
            Utilities.getInstance().add(packet, 0)
            packet.setPage(pagingManager.page)
            packet.setOf(pagingManager.getPages())
            
            sendResponse(response, packet)
         } else {
            throw new Exception("Missing parameters.")
         }
      } catch(Exception exception) {
         sendErrorResponse(response, "Faield to update granule status.")
      }
   }
   
   def updateGranuleArchiveStatus = {
      try {
         int granuleId = this.getGranuleId(params)
         if(params.status) {
            archiveService.updateGranuleArchiveStatus(granuleId, params.status)
            
            def pagingManager = pagingService.createPaging(1, 1, 1)
            def packet = new Packet()
            Utilities.getInstance().add(packet, 0)
            packet.setPage(pagingManager.page)
            packet.setOf(pagingManager.getPages())
            
            sendResponse(response, packet)
         } else {
            throw new Exception("Missing parameters.")
         }
      } catch(Exception exception) {
         sendErrorResponse(response, "Faield to update granule status.")
      }
   }
   
   def updateGranuleLocation = {
      try {
         int granuleId = this.getGranuleId(params)
         if(params.basepath) {
            archiveService.updateGranuleLocation(granuleId, params.basepath)
            
            def pagingManager = pagingService.createPaging(1, 1, 1)
            def packet = new Packet()
            Utilities.getInstance().add(packet, 0)
            packet.setPage(pagingManager.page)
            packet.setOf(pagingManager.getPages())
            
            sendResponse(response, packet)
         } else {
            throw new Exception("Missing parameters.")
         }
      } catch(Exception exception) {
         sendErrorResponse(response, "Faield to update granule status.")
      }
   }
   
   def addGranuleReference = {
      try {
         int granuleId = this.getGranuleId(params)
         if((params.type) && (params.status) && (params.description) && (params.path)) {
            archiveService.addGranuleReference(granuleId, params.type, params.status, params.description, params.path)
            
            def pagingManager = pagingService.createPaging(1, 1, 1)
            def packet = new Packet()
            Utilities.getInstance().add(packet, 0)
            packet.setPage(pagingManager.page)
            packet.setOf(pagingManager.getPages())
            
            sendResponse(response, packet)
         } else {
            throw new Exception("Missing parameters.")
         }
      } catch(Exception exception) {
         sendErrorResponse(response, "Faield to update granule status.")
      }
   }
   
   def removeGranuleReference = {
      try {
         int granuleId = this.getGranuleId(params)
         if(params.type) {
            archiveService.removeGranuleReference(granuleId, params.type)
            
            def pagingManager = pagingService.createPaging(1, 1, 1)
            def packet = new Packet()
            Utilities.getInstance().add(packet, 0)
            packet.setPage(pagingManager.page)
            packet.setOf(pagingManager.getPages())
            
            sendResponse(response, packet)
         } else {
            throw new Exception("Missing parameters.")
         }
      } catch(Exception exception) {
         sendErrorResponse(response, "Faield to update granule status.")
      }
   }
   
   def updateVerifyGranuleStatus = {
      try {
         int granuleId = this.getGranuleId(params)
         if(params.status) {
            archiveService.updateVerifyGranuleStatus(granuleId, params.status)
            
            def pagingManager = pagingService.createPaging(1, 1, 1)
            def packet = new Packet()
            Utilities.getInstance().add(packet, 0)
            packet.setPage(pagingManager.page)
            packet.setOf(pagingManager.getPages())
            
            sendResponse(response, packet)
         } else {
            throw new Exception("Missing parameters.")
         }
      } catch(Exception exception) {
         sendErrorResponse(response, "Faield to update granule status.")
      }
   }
   
   private void sendResponse(def response, def packet) {
      response.setContentType("application/xml")
      def outputStream = response.getOutputStream()
      Utilities.getInstance().send(packet, outputStream)
      outputStream.close()
   }
   
   private void sendErrorResponse(def response, String message) {
      sendErrorResponse(response, message, null)
   }
   
   private void sendErrorResponse(def response, String message, Exception exception) {
      def packet = new Packet()
      def error = new Error()
      error.setMessage(message)
      packet.setError(error)
      
      sendResponse(response, packet)
   }
   
   private int getGranuleId(def params) throws Exception {
      int granuleId
      try {
         granuleId = Integer.parseInt(params.granuleId)
      } catch(Exception exception) {
         throw new Exception("Failed to convert granule id to integer.")
      }
      if(granuleId < 0) {
         throw new Exception("Granule id cannot be negative integer.")
      }
      
      return granuleId
   }
   */
}
