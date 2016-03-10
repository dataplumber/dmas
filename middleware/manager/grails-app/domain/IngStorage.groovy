/*
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 */
import gov.nasa.podaac.common.api.zookeeper.api.constants.JobPriority

/**
 * Definition of Storage Location domain class
 *
 * @author T. Huang [Thomas.Huang@jpl.nasa.gov]
 * @version $Id: IngStorage.groovy 10019 2012-05-23 23:05:01Z nchung $
 */
class IngStorage {

   /**
    * A unique storage name (e.g. use the ingest enging host)
    */
   String name

   /**
    * The moment in time when the location was created
    */
   Long created = new Date().getTime()
   
   /**
    * The priority of products this storage can handle
    */
   Integer priority
   
   /**
    * Returns JobPriority enum corresponding to this product type priority integer value
    */
   JobPriority getStorageJobPriority() {
      if (this.priority) {
         return JobPriority.values().find{ it.value == this.priority }
      }
      return null
   }
   
   static transients = ['storageJobPriority']

   static belongsTo = [location: IngLocation]

   static constraints = {
      name(unique: true, size: 1..255)
      created(nullable: true)
      priority(nullable: true, range: 1..3)
   }

   static mapping = {
      //version false
      id generator:'sequence', params:[sequence:'ing_storage_id_seq']            
   }
}
