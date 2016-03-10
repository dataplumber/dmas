/*
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 */
import gov.nasa.podaac.common.api.zookeeper.api.constants.JobPriority

/**
 * Definition of Product Type domain class
 *
 * @author T. Huang [Thomas.Huang@jpl.nasa.gov]
 * @version $Id: IngProductType.groovy 2490 2009-01-24 01:11:02Z thuang $
 */
class IngProductType {

  /**
   * The  product type name
   */
  String name

  /**
   * Flag to lock/unlock the product type
   */
  Boolean locked

  /**
   * The moment in time when the record is locked
   */
  Long lockedAt

  /**
   * The person who locked this record
   */
  String lockedBy

  /**
   * Flag to determine if products of this type should say in staging area and not to be inventoried.
   */
  Boolean ingestOnly = false

  /**
   * The relative path from the storage location
   */
  String relativePath


  /**
   *  Data purging frequency in minutes
   */
  Integer purgeRate

  /**
   * The moment in time when this record was updated
   */
  Long updated = new Date().getTime()

  /**
   * The person who updated this record last
   */
  IngSystemUser updatedBy

 /**
  * The significant event category associate with the product type
  */
  IngEventCategory eventCategory


  /**
   * Text description
   */
  String note
  
  /**
   * Period of time in hours when PO.DAAC should receive delivery of data
   */
  Integer deliveryRate
  
  /**
   * Priority of the product type with lower numbers representing higher priority
   */
  Integer priority = JobPriority.DEFAULT.value
  
  /**
   * Returns JobPriority enum corresponding to this product type priority integer value
   */
  JobPriority getJobPriority() {
    if (this.priority) {
       return JobPriority.values().find{ it.value == this.priority }
    }
    return JobPriority.DEFAULT
  }

  static belongsTo = [federation: IngFederation]
  static hasMany = [products: IngProduct]

  static transients = ['jobPriority']

  static constraints = {
    eventCategory(nullable:true)
    name(unique: true, size: 1..100)
    locked(nullable: false)
    lockedAt(nullable: true)
    lockedBy(nullable: true)
    ingestOnly(nullable: false)
    purgeRate(nullable: false, min: 1)
    relativePath(nullable: false)
    updated(nullable: false)
    updatedBy(nullable: false)
    note(nullable: true, size: 1..255)
    deliveryRate(nullable: true, min: 1)
    priority(nullable: false, range: 1..3)
  }

  static mapping = {
    //version false
    id generator: 'sequence', params: [sequence: 'ing_producttype_id_seq']
  }
}
