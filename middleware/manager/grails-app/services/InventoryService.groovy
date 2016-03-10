import org.codehaus.groovy.grails.commons.ConfigurationHolder

import gov.nasa.podaac.common.api.serviceprofile.ServiceProfileFactory
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfile
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfileException
import gov.nasa.podaac.common.api.serviceprofile.ArchiveProfile
import gov.nasa.podaac.common.api.serviceprofile.ArchiveGranule
import gov.nasa.podaac.common.api.metadatamanifest.MetadataManifest
import gov.nasa.jpl.horizon.api.Lock
import gov.nasa.jpl.horizon.api.State
import gov.nasa.jpl.horizon.api.Stereotype

import gov.nasa.jpl.horizon.api.Errno

//import gov.nasa.podaac.inventory.api.Inventory
//import gov.nasa.podaac.inventory.api.InventoryFactory
//import gov.nasa.podaac.inventory.api.Query
//import gov.nasa.podaac.inventory.api.QueryFactory
//import gov.nasa.podaac.inventory.api.DataManager;
//import gov.nasa.podaac.inventory.api.DataManagerFactory;
import gov.nasa.podaac.inventory.api.Service;
import gov.nasa.podaac.inventory.api.ServiceFactory;
import gov.nasa.jpl.horizon.sigevent.api.SigEvent
import gov.nasa.jpl.horizon.sigevent.api.EventType
import gov.nasa.podaac.inventory.model.Dataset
import gov.nasa.podaac.inventory.model.DatasetCoverage
import gov.nasa.podaac.inventory.model.DatasetLocationPolicy;
import gov.nasa.podaac.inventory.model.DatasetPolicy;
import gov.nasa.podaac.inventory.model.Granule
import gov.nasa.podaac.inventory.model.Provider
import gov.nasa.podaac.inventory.exceptions.InventoryException
//import gov.nasa.podaac.inventory.hibernate.HibernateSessionFactory
import gov.nasa.jpl.horizon.api.protocol.IngestProtocol

class InventoryService {

  static transactional = false
  
  static int MAX_NOTE = 2999
  
  //private SigEvent sigEvent = new SigEvent(ConfigurationHolder.config.horizon_sigevent_url)

  def sessionFactory
  def sigEventService

  def processStaged() {
    //HibernateSessionFactory.sessionFactory = sessionFactory
    def testStartDate = new Date()
    log.debug("Processing staged.")
    def criteria = IngProduct.createCriteria()
    def pendings = criteria.list {
      'in'('currentLock', [Lock.ADD.toString(), Lock.REPLACE.toString()])
      eq('currentState', State.STAGED.toString())
      productType {
         federation {
            eq("name", ConfigurationHolder.config.manager_federation)
         }
         order('priority', 'asc')
      }
      maxResults(1)
      order('created', 'asc')
    }
    //def pendings = IngProduct.findAllByCurrentLockAndCurrentState('GET', "STAGED")
    if (!pendings || pendings.size() == 0) {
      return
    }

    for (IngProduct product in pendings) {
      //product.discard()
      //product = IngProduct.lock(product.id)
      if ((![Lock.ADD, Lock.REPLACE].find{it == Lock.valueOf(product.currentLock)}) ||
        (State.valueOf(product.currentState) != State.STAGED)) {
         // the product's lock/state was updated since the query
         continue
      }
      // if the product should be inventoried
      if (!product.productType.ingestOnly) {
        IngestProtocol protocol = IngestProtocol.createEngineIngestRequest(
                Stereotype.ARCHIVE,
                product.productType.federation.name,
                product.productType.federation.id,
                product.productType.name,
                product.productType.id,
                product.name,
                product.id
        )
        protocol.addMetadata = product.completeText
        
        log.debug('Calling InventoryService')
        long granuleId = this.updateInventory(protocol)
        log.debug("Return status ${protocol.errno}: ${protocol.description}")
        
        def loop = true
        while (loop) {
           loop = false
           try {
              product.refresh()
              if (granuleId < 0 || protocol.errno != Errno.OK) {
                product.currentState = State.ABORTED.toString()
                product.currentLock = Lock.RESERVED.toString()
                product.note = trimNote(product.note + "{${product.currentState}}: Inventory failure  - ${protocol.description}.") + "\n"
                log.error("Error cataloging ${product.name}: ${protocol.description}.")
                
                sigEventService.send(product.productType.eventCategory.name, EventType.Error, "Problem to catalog", "{${product.currentState}}: Inventory failure  - ${protocol.description}.")
                //sigEvent.create(EventType.Error, product.productType.eventCategory.name, "manager", "manager", "manager", "Problem to catalog", null, "{${product.currentState}}: Inventory failure  - ${protocol.description}.")
              } else {
                boolean isReplace = (product.currentLock == Lock.REPLACE.toString())
                log.debug('isReplace: '+isReplace)
                if(isReplace) {
                  ServiceProfile serviceProfile =
                     ServiceProfileFactory.getInstance().createServiceProfileFromMessage(protocol.addMetadata)
                  ArchiveProfile archiveProfile = serviceProfile.getProductProfile().getArchiveProfile()
                  ArchiveGranule archiveGranule = archiveProfile.getGranules().get(0)
                  List<String> filesToDelete = archiveGranule.getDeletes()
                  if((filesToDelete == null) || (filesToDelete.size() == 0)) {
                    log.info("Nothing needs to be purged for product '${product.name}' to replace.")
                    isReplace = false
                  }
                }
                
                product.inventoryId = granuleId
                product.archiveText = protocol.addMetadata
                product.currentLock = Lock.INVENTORY.toString()
                product.currentState = (isReplace) ? State.PENDING_ARCHIVE.toString() : State.INVENTORIED.toString()
                product.note = trimNote(product.note + "{${product.currentState}}: Product has been inventoried.") + "\n"
                log.debug("Done cataloging ${product.name}")
              }
              //product.note += protocol.description + "\n"
              log.debug("${product.name} ${product.currentState} ${product.currentLock}")
              product.updated = new Date().getTime()
              product.save(flush: true)
           } catch (org.springframework.dao.OptimisticLockingFailureException e) {
              //product.discard()
              loop = true
           }
        }
      }
    }
    
    def testEndDate = new Date()
    log.debug("Processing staged done: "+(testEndDate.getTime() - testStartDate.getTime()))
  }
  
  
  def processInventoried() {
    //HibernateSessionFactory.sessionFactory = sessionFactory
    def testStartDate = new Date()
    log.debug("Processing inventoried.")
    def criteria = IngProduct.createCriteria()
    def pendings = criteria.list {
      eq('currentLock', Lock.INVENTORY.toString())
      eq('currentState', State.INVENTORIED.toString())
      productType {
         federation {
            eq("name", ConfigurationHolder.config.manager_federation)
         }
         order('priority', 'asc')
      }
      maxResults(5)
      order('created', 'asc')
    }
    if (!pendings || pendings.size() == 0) {
      return
    }
    
    for (IngProduct product in pendings) {
      //product.discard()
      //product = IngProduct.lock(product.id)
      if (Lock.valueOf(product.currentLock) != Lock.INVENTORY ||
        State.valueOf(product.currentState) != State.INVENTORIED) {
         // the product's lock/state was updated since the query
         continue
      }
      
      def loop = true
      while (loop) {
         loop = false
         try {
            product.refresh()
            // set granule online/offline
            product.currentLock = Lock.ARCHIVE.toString()
            product.currentState = State.PENDING_ARCHIVE.toString()
            product.currentRetries = 5
            product.note = trimNote(product.note + "{${product.currentState}}: Archive requested.") + "\n"
            log.debug("Done updated granule status ${product.name}, ${product.currentState}")
      
            product.updated = new Date().getTime()
            product.save(flush: true)
         } catch (org.springframework.dao.OptimisticLockingFailureException e) {
            //product.discard()
            loop = true
         }
      }
    }
    
    def testEndDate = new Date()
    log.debug("Processing inventoried done: "+(testEndDate.getTime() - testStartDate.getTime()))
  }


  long updateInventory(IngestProtocol protocol) {
    long granuleid = -1
     
    //HibernateSessionFactory.sessionFactory = sessionFactory

    try {
      log.trace("[INVENTORY: ] ${protocol.addMetadata}")
      ServiceProfile profile = ServiceProfileFactory.instance.createServiceProfileFromMessage(protocol.addMetadata)
      def granules = profile.productProfile.ingestProfile.completeContent.granules

      granules.each {granule ->
        log.debug("granule: "+granule.productType+", protocol: "+protocol.productType)
        granule.productType = protocol.productType
      }
      Service service = getAuthInventoryClient()
      profile.productProfile.ingestProfile.header.inventoryStartTime = new Date()
      log.debug("Store ServiceProfile")
      profile = service.ingestSip(profile)
      log.debug("Done storing ServiceProfile")
      if (profile) {
         profile.productProfile.ingestProfile.header.inventoryStopTime = new Date()
   
         protocol.addMetadata = profile.toString()
   
         granules = profile.productProfile.ingestProfile.completeContent.granules
         granules.each {
           granuleid = it.id
         }
         protocol.errno = Errno.OK
         protocol.description = "Metadata for granule ${granuleid} has been inventoried."
   
         log.trace "Metadata for granule ${granuleid} has been inventoried."
      } else {
         protocol.errno = Errno.INVENTORY_ERR
         protocol.description = "Unable to ingest SIP; ServiceProfile returned by Inventory Service is null"
   
         log.error(protocol.description)
      }
    } catch (ServiceProfileException e) {
      protocol.errno = Errno.INVALID_PROTOCOL
      protocol.description = e.message

      log.error(e.message, e)
    } catch (Exception e) {
      protocol.errno = Errno.INVENTORY_ERR
      protocol.description = e.message

      log.error(e.message, e)
    }
    return granuleid
  }

  boolean isExists(IngestProtocol protocol) {
    boolean result = false

    //HibernateSessionFactory.sessionFactory = sessionFactory

    try {
      ServiceProfile profile = ServiceProfileFactory.instance.createServiceProfileFromMessage(protocol.addMetadata)
      def granules = profile.productProfile.ingestProfile.completeContent.granules
      Service service = getInventoryClient()
		Dataset ds = service.getDataset(granules.asList()[0].datasetName)
		if (ds) {
        Granule g = service.getGranuleByNameAndDataset(granules.asList()[0].name, ds.getDatasetId())
        if (g) result = true

      } else {
         protocol.errno = Errno.INVENTORY_ERR
         protocol.description = "Unable to retrieve dataset " + granules.asList()[0].datasetName + "; Dataset returned by Inventory Service is null"
         
         log.error(protocol.description)
      }
    } catch (ServiceProfileException e) {
      protocol.errno = Errno.INVALID_PROTOCOL
      protocol.description = e.message

      log.error(e.message, e)
    } catch (InventoryException e) {
      // If granule does not exist, then inventory service throws an exception.
      // Not an error, so just log as debug.
      if (e.message.startsWith("Granule id not found")) {
         log.debug(e.message)
      } else {
         protocol.errno = Errno.INVENTORY_ERR
         protocol.description = e.message
         log.error(e.message, e)
      }
    } catch (Exception e) {
      protocol.errno = Errno.INVENTORY_ERR
      protocol.description = e.message

      log.error(e.message, e)
    }

    return result
  }
  
  List getDatasetStatusList() {
    List list = []
    //HibernateSessionFactory.sessionFactory = sessionFactory
    long totalStart = new Date().getTime();
    Service service = getInventoryClient()
    int maxPerPage = 10
    def ingProductTypes = null
    boolean firstIter = true
    int offset = 0
    while (firstIter || ingProductTypes.size() >= maxPerPage) {
      firstIter = false
      ingProductTypes = IngProductType.createCriteria().list {//TODO PAGING
//         eq("name", dataset['short-name'])
         federation {
            eq("name", ConfigurationHolder.config.manager_federation)
         }
         isNotNull("deliveryRate")
         maxResults(maxPerPage)
         firstResult(offset)
      }
      offset += maxPerPage
      for (IngProductType ingProductType in ingProductTypes) {
      Dataset dataset = null
      try {
         dataset = service.getDataset(ingProductType.name)
      } catch (Exception e) {
         log.debug("Not monitoring since unable to get dataset from Inventory Service: "+ingProductType.name)
         continue
      }
      if (!dataset) {
         log.debug("Not monitoring since unable to get dataset from Inventory Service: "+ingProductType.name)
         continue
      }
//      if (ingProductType) {
      long allStart = new Date().getTime()
      
      int datasetId = dataset.getDatasetId()
      DatasetCoverage datasetCoverage = null
      DatasetPolicy datasetPolicy = null
      try {
         datasetCoverage = service.getDatasetCoverage(datasetId)
         datasetPolicy = service.getDatasetPolicy(datasetId)
         if((!datasetCoverage) || (!datasetPolicy)) {
            log.debug("Not monitoring since DatasetCoverage and/or DatasetPolicy is missing: "+dataset['id'])
            continue
         }
         
         Date coverageStartTime = datasetCoverage.getStartTime()
         Date coverageStopTime = datasetCoverage.getStopTime()
         if((coverageStopTime) && (coverageStopTime.getTime() < new Date().getTime())) {
            log.debug("Not monitoring since stop time exists and it is past: "+datasetId)
            continue
         }
      } catch (InventoryException e) {
         log.debug("Not monitoring since DatasetCoverage and/or DatasetPolicy is missing: "+datasetId)
         continue
      }
      
      Granule granule = null
      try {
      //long startTime = new Date().getTime()
         int granuleId = service.getLatestGranuleIdByDataset(datasetId)
         
         if (granuleId) {
            granule = service.getGranuleById(granuleId)
         }
      //long endTime = new Date().getTime()
      //log.debug("took: "+((endTime - startTime) / 1000.0f)+" sec.");
      } catch (InventoryException e) {
      	log.debug("Exception in getDatasetStatusList.", e)
      }
      
      Map entry = [:];
      entry['id'] = datasetId
      entry['dataStream'] = dataset.getShortName()
      entry['dataFrequency'] = ingProductType.deliveryRate
      entry['accessType'] = datasetPolicy.getAccessType()
      entry['lastIngestTime'] = (granule) ? granule.getIngestTime() : null
      entry['currentTime'] = new Date()
      entry['name'] = (granule) ? granule.getName() : null
      list.add(entry)
      
      long allEnd = new Date().getTime()
      }
      //log.debug("total: "+((allEnd - allStart) / 1000.0f)+" sec, fetch: "+((endTime - startTime) / 1000.0f))
    }
    
    long totalEnd = new Date().getTime()
    //log.debug("all: "+((totalEnd - totalStart) / 1000.0f)+" sec")

    return list
  }
  /*
  Dataset getDataset(int id) {
    HibernateSessionFactory.sessionFactory = sessionFactory
    Query query = QueryFactory.getInstance().createQuery()
    Dataset dataset = query.fetchDatasetById(id, "provider", "locationPolicySet", "datasetPolicy")
    
    return dataset
  }
  
  def updateDataset(Map parameters) {
    HibernateSessionFactory.sessionFactory = sessionFactory

    Query query = QueryFactory.getInstance().createQuery();
    
    Dataset dataset = query.fetchDatasetById(parameters.dataset.id, "provider", "locationPolicySet", "datasetPolicy");
    if(!dataset) {
      dataset = new Dataset()
    }
    dataset.setShortName(parameters.dataset.ShortName)
    dataset.setLongName(parameters.dataset.LongName)
    dataset.setProcessingLevel(parameters.dataset.ProcessingLevel)
    dataset.setRegion(parameters.dataset.Region)
    dataset.setLatitudeResolution(parameters.dataset.LatitudeResolution)
    dataset.setLongitudeResolution(parameters.dataset.LongitudeResolution)
    
    Provider provider = query.findProviderById(Integer.parseInt(parameters.provider.Provider))
    dataset.setProvider(provider)
    
    Set<DatasetLocationPolicy> locationPolicySet = dataset.getLocationPolicySet();
    if(!locationPolicySet) {
      for(int i = 0; i < 5; i++) {
        if(parameters.locationPolicy['BasePath'+i]) {
          DatasetLocationPolicy dlp = new DatasetLocationPolicy()
          dlp.setBasePath(parameters.locationPolicy['BasePath'+i])
          dlp.setType(parameters.locationPolicy['Type'+i])
          dataset.add(dlp)
        }
      }
    } else {
      int count = 0
      for(DatasetLocationPolicy dlp : locationPolicySet) {
        dlp.setBasePath(parameters.locationPolicy['BasePath'+count])
        dlp.setType(parameters.locationPolicy['Type'+count])
        count++
      }
    }
    
    DataManager manager = DataManagerFactory.getInstance().createDataManager();
    manager.addProvider(provider);
    manager.addDataset(dataset);
    
    boolean datasetPolicyNew = false
    DatasetPolicy datasetPolicy = dataset.getDatasetPolicy();
    if(!datasetPolicy) {
      datasetPolicy = new DatasetPolicy()
      datasetPolicyNew = true
    }
    datasetPolicy.setDataClass(parameters.policy.DataClass);
    datasetPolicy.setAccessType(parameters.policy.AccessType);
    datasetPolicy.setBasePathAppendType(parameters.policy.BasePathAppendType);
    datasetPolicy.setDataFormat(parameters.policy.DataFormat);
    datasetPolicy.setCompressType(parameters.policy.CompressType);
    datasetPolicy.setChecksumType(parameters.policy.ChecksumType);
    datasetPolicy.setSpatialType(parameters.policy.SpatialType);
    datasetPolicy.setAccessConstraint(parameters.policy.AccessConstraint);
    datasetPolicy.setUseConstraint(parameters.policy.UseConstraint);
    datasetPolicy.setDataset(dataset)
    dataset.setDatasetPolicy(datasetPolicy)
    
    if(datasetPolicyNew) {
		  manager.addDatasetPolicy(datasetPolicy);
	  }
	  //manager.addDataset(dataset);
  }
  
  List getProviders() {
    def list = []

    HibernateSessionFactory.sessionFactory = sessionFactory
    Query query = QueryFactory.getInstance().createQuery()
    List<Provider> providers = query.listProvider()
    providers.each {provider ->
      def entry = [:]
      entry['id'] = provider.getProviderId()
      entry['shortName'] = provider.getShortName()
      entry['longName'] = provider.getLongName()
      entry['type'] = provider.getType()
      list.add(entry)
    }
    
    return list
  }
  
  Map getProvider(int id) {
    HibernateSessionFactory.sessionFactory = sessionFactory
    Query query = QueryFactory.getInstance().createQuery()
    Provider provider = query.findProviderById(id)
    
    def entry = [:]
    entry['id'] = (provider) ? provider.getProviderId() : ""
    entry['shortName'] = (provider) ? provider.getShortName() : ""
    entry['longName'] = (provider) ? provider.getLongName() : ""
    entry['type'] = (provider) ? provider.getType() : ""
    
    return entry
  }
  
  def updateProvider(Map parameters) {
    HibernateSessionFactory.sessionFactory = sessionFactory
    Query query = QueryFactory.getInstance().createQuery()
    
    Provider provider = null
    if(parameters.id) {
      provider = query.findProviderById(parameters.id)
    } else {
      provider = new Provider()
    }
    provider.setShortName(parameters['shortName'])
    provider.setLongName(parameters['longName'])
    provider.setType(parameters['type'])
    
    DataManager manager = DataManagerFactory.getInstance().createDataManager();
    manager.addProvider(provider);
  }
  */
  String trimNote(String note) {
     def buf = new StringBuffer(note)
     buf.length = MAX_NOTE
     return buf.toString().trim()
  }
  
  public MetadataManifest processMetadataManifest(MetadataManifest metadataManifest) throws Exception {
     //HibernateSessionFactory.sessionFactory = sessionFactory
     
     Service service = getAuthInventoryClient()

     MetadataManifest result = null
     try {
        result = service.processManifest(metadataManifest)
     } catch(Exception exception) {
        throw exception
     }

     return result
  }
  
  /**
   * Returns inventory client containing authentication info used to access inventory web service
   */
  def getAuthInventoryClient() {
     Service service = getInventoryClient()
     service.setAuthInfo(ConfigurationHolder.config.gov.nasa.podaac.manager.inventory.user, ConfigurationHolder.config.gov.nasa.podaac.manager.inventory.pass)
     return service
  }
  
  /**
   * Returns inventory client used to access inventory web service
   */
  def getInventoryClient() {
     Service service = ServiceFactory.getInstance().createService(ConfigurationHolder.config.gov.nasa.podaac.manager.inventory.host, ConfigurationHolder.config.gov.nasa.podaac.manager.inventory.port)
     return service
  }
}
