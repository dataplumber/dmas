/**
 * DatasetService
 */
import org.codehaus.groovy.grails.commons.ConfigurationHolder

import gov.nasa.podaac.common.api.metadatamanifest.MetadataManifest
import gov.nasa.podaac.common.api.metadatamanifest.Constant.ActionType

import gov.nasa.jpl.horizon.sigevent.api.EventType

//import gov.nasa.podaac.inventory.api.Inventory
//import gov.nasa.podaac.inventory.api.InventoryFactory
//import gov.nasa.podaac.inventory.api.Query
//import gov.nasa.podaac.inventory.api.QueryFactory
//import gov.nasa.podaac.inventory.model.Dataset

//import gov.nasa.podaac.inventory.hibernate.HibernateSessionFactory

class DatasetService {
   public static final String RESPONSE_OK = "OK"
   public static final String RESPONSE_ERROR = "ERROR"
   private static final String SIGEVENT_DATASET_ADDED = "DMAS-DATASET-ADDED"
   private static final String SIGEVENT_DATASET_UPDATED = "DMAS-DATASET-UPDATED"
      
   boolean transactional = true

   //def sessionFactory
   def sigEventService
   def authenticationService

   public Map authenticate(String userName, String password) {
      def result = [:]
      result.Status = DatasetService.RESPONSE_ERROR
      result.Description = 'Username and password did not match.'
      result.Admin = "false"
      result.Role = null
      
      if(authenticationService.authenticate(userName, password)) {
      //def systemUser = IngSystemUser.findByNameAndPassword(userName, password)
      //if(systemUser) {
         //def userRole = IngSystemUserRole.findByUser(systemUser)
         
         result.Status = DatasetService.RESPONSE_OK
         result.Description = "Authentication successful"
         result.Admin = "true"
         //result.Role = (userRole) ? userRole.role.name : null
         //result.fullName = systemUser.fullname
      }

      return result
   }

   /*
   public MetadataManifest processMetadataManifest(MetadataManifest metadataManifest) throws Exception {
      HibernateSessionFactory.sessionFactory = sessionFactory
      
      def inventoryFactory = InventoryFactory.getInstance()
      def inventory = inventoryFactory.createInventory()

      MetadataManifest result = null
      try {
         result = inventory.processManifest(metadataManifest)
      } catch(Exception exception) {
         throw exception
      }

      return result
   }
   */
   
   public void processDatasetRequest(String userName, MetadataManifest metadataManifest, String previousDatasetName) throws Exception {
      String datasetName = getManifestFieldValue(metadataManifest, "dataset_shortName")
      if(!datasetName) {
         throw new Exception("Dataset name is not provided.")
      }
      
      def actionType = metadataManifest.getActionType()
      String methodName = null
      if(ActionType.CREATE.toString().equalsIgnoreCase(actionType)) {
         methodName = "addDataset"
      } else if(ActionType.UPDATE.toString().equalsIgnoreCase(actionType)) {
         methodName = "updateDataset"
      }
      
      if(methodName) {
         this."$methodName"(userName, datasetName, previousDatasetName, metadataManifest)
      } else {
         throw new Exception("Action type is not recognized.")
      }
   }
   
   /*
   public String getDatasetShortName(int datasetId) throws Exception {
      //HibernateSessionFactory.sessionFactory = sessionFactory
      
      def query = QueryFactory.getInstance().createQuery()
      def dataset = query.fetchDatasetById(datasetId)
      if(!dataset) {
         throw new Exception("Specified dataset does not exist: "+datasetId)
      }
      
      return dataset.getShortName()
   }
   */
   
   private void addDataset(String userName, String datasetName, String previousDatasetName, MetadataManifest metadataManifest) throws Exception {
      def federationValue = ConfigurationHolder.config.horizon_dataset_update_federation
      def purgeRateValue = ConfigurationHolder.config.horizon_dataset_update_purge_rate
      
      def federation = IngFederation.findByName(federationValue)
      if(!federation) {
         throw new Exception("Federation not found: "+federationValue)
      }
      
      def systemUser = IngSystemUser.findByName(userName)
      if(!systemUser) {
         throw new Exception("User not found: "+userName)
      }
      
      def eventCategory = new IngEventCategory(name: datasetName)
      if(!eventCategory.save(flush: true)) {
         throw new Exception("Failed to save IngEventCategory.")
      }
      
      def productType = new IngProductType()
      productType.federation = federation
      productType.name = datasetName
      productType.locked = false
      productType.ingestOnly = false
      productType.relativePath = datasetName+'/'
      productType.purgeRate = purgeRateValue
      productType.updatedBy = systemUser
      productType.note = datasetName+' product'
      productType.eventCategory = eventCategory
      if(!productType.save(flush: true)) {
         throw new Exception("Failed to save IngProductType.")
      }
      
      def accessRoles = IngAccessRole.getAll()
      accessRoles.each {accessRole ->
         def productTypeRole = new IngProductTypeRole()
         productTypeRole.productType = productType
         productTypeRole.role = accessRole
         if(!productTypeRole.save(flush: true)) {
            throw new Exception("Failed to save IngProductTypeRole")
         }
      }
      
      def sigeventGroups = ConfigurationHolder.config.horizon_dataset_update_sigevent
      def newGroups = []
      EventType.each {eventType ->
         def entry = sigeventGroups.find{eventType.value.equalsIgnoreCase(it.type)}
         if(entry) {
            newGroups.add(['type': eventType, 'purgeRate': entry.purgeRate])
         }
      }
      newGroups.each {newGroup ->
         if(sigEventService.createGroup(datasetName, newGroup.type, newGroup.purgeRate)) {
            log.info('Created sigevent category for new dataset: '+datasetName+', '+newGroup.type.value)
         } else {
            log.error('Failed to create sigevent category for new dataset: '+datasetName+', '+newGroup.type.value)
         }
      }
      
      sigEventService.send(SIGEVENT_DATASET_ADDED, EventType.Info, "New dataset added: "+datasetName, "New dataset added "+datasetName+" by "+userName+": "+metadataManifest.getManifest())
      log.info("New dataset added "+datasetName+" by "+userName+": "+metadataManifest.getManifest())
   }
   
   private void updateDataset(String userName, String datasetName, String previousDatasetName, MetadataManifest metadataManifest) throws Exception {
      def datasetId = getManifestFieldValue(metadataManifest, "dataset_datasetId")
      def message = "Dataset updated: id="+datasetId+", shortName="+datasetName
      def description = message+" by "+userName+": "+metadataManifest.getManifest()
      
      sigEventService.send(SIGEVENT_DATASET_UPDATED, EventType.Info, message, description)
      log.info("Updaing dataset for manager is not supported yet but dataset is updated: "+description)
      
      /*
      def systemUser = IngSystemUser.findByName(userName)
      if(!systemUser) {
         throw new Exception("User not found: "+userName)
      }
      
      log.debug("updating for: "+previousDatasetName)
      
      def eventCategory = IngEventCategory.findByName(previousDatasetName)
      if(eventCategory) {
         eventCategory.name = datasetName
         if(!eventCategory.save(flush: true)) {
            throw new Exception("Failed to update IngEventCategory.")
         }
      } else {
         throw new Exception("Failed to find IngEventCategory: "+previousDatasetName)
      }
      
      def productType = IngProductType.findByName(previousDatasetName)
      if(productType) {
         productType.name = datasetName
         productType.relativePath = datasetName+'/'
         productType.updatedBy = systemUser
         productType.note = datasetName+' product'
         if(!productType.save(flush: true)) {
            throw new Exception("Failed to update IngProductType.")
         }
      } else {
         throw new Exception("Failed to find IngProductType: "+previousDatasetName)
      }
      
      log.info("Dataset updated: "+datsetId)
      */
   }
   
   private String getManifestFieldValue(MetadataManifest metadataManifest, String name) {
      def metadataField = metadataManifest.getFields().find{it.getName().equalsIgnoreCase(name)}
      return (metadataField) ? metadataField.getValue() : null;
   }
}
