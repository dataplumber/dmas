/*
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 */

/**
 * Definition of Federation domain class
 *
 * @author T. Huang [Thomas.Huang@jpl.nasa.gov]
 * @version $Id: IngFederation.groovy 2173 2008-10-29 20:06:48Z thuang $
 */
class IngFederation {

   /**
    * Name of the ingest federation
    */
   String name

   /**
    * Text description
    */
   String note

   /**
    * The moment in time when this record was updated
    */
   Long updated = new Date().getTime()

   /**
    * The name of the host where the manager is located
    */
   String hostname

   /**
    * The port number where the manager is located
    */
   Integer port

   /**
    * Method to return current property values
    */
   String toString() {
      "$name $note $updated"
   }

   static hasMany = [productTypes: IngProductType]

   static constraints = {
      name(unique: true, size: 1..30)
      note(blank: true, size: 1..255)
      updated(nullable: true)
      hostname(blank: false, size: 1..30)
      port(nullable: false)
   }

   static mapping = {
      //version false
      id generator:'sequence', params:[sequence:'ing_federation_id_seq']      
   }
}
