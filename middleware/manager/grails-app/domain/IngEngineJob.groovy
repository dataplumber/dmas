/*
* Copyright (c) 2008 Jet Propulsion Laboratory,
* California Institute of Technology.  All rights reserved
*/

import gov.nasa.jpl.horizon.api.Lock
import gov.nasa.jpl.horizon.api.State


/**
 * Definition of Engine Job domain class
 *
 * @author T. Huang [Thomas.Huang@jpl.nasa.gov]
 * @version $Id: IngEngineJob.groovy 2698 2009-03-06 01:46:15Z axt $
 */
class IngEngineJob {

  /**
   * The engine
   */
  //IngEngine engine

  /**
   * The product the engin is handling
   */
  IngProduct product

  /**
   * The operation to be performed
   */
  String operation

  /**
   * The previous state for the job in case a retry
   */
  String previousState

  /**
   * The moment in time when the job was assigned
   */
  Long assigned = new Date().getTime()

  /**
   * The path to zookeeper queue node for this job
   */
  String path

  /**
   * Priority of the current job with lower numbers representing higher priority
   */
  Integer priority

  /**
   * The storage used for handling this job
   */
  IngStorage contributeStorage

  /*
  Lock getOperation() {
    if (this.operation) {
      return Lock.valueOf(this.operation)
    }
    return null
  }

  void setOperation(Lock operation) {
    this.operation = operation.toString()
  }


  State getPreviousState() {
    if (this.previousState) {
      return State.valueOf(this.previousState)
    }
    return null
  }

  void setPreviousState(State state) {
    this.previousState = state.toString()
  }
  */

  static belongsTo = [contributeStorage: IngStorage]

  static constraints = {
    //engine(nullable: false)
    product(nullable: false)
    operation(nullable: false, inList: ['ADD', 'ARCHIVE', 'REPLACE', 'DELETE', 'TRASH', 'INVENTORY'])
    previousState(nullable: false)
    assigned(nullable: false)
    path(nullable: true, size: 1..255)
    priority(nullable: false, range: 1..3)
  }

  static mapping = {
    //version false
    id generator: 'sequence', params: [sequence: 'ing_enginejob_id_seq']
  }
}
