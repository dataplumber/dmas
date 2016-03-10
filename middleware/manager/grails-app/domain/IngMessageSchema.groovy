/*
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 */

/**
 * Definition of Message Schema domain class
 *
 * @author T. Huang [Thomas.Huang@jpl.nasa.gov]
 * @version $Id: IngMessageSchema.groovy 2173 2008-10-29 20:06:48Z thuang $
 */
class IngMessageSchema {
   /**
    * The schema revision
    */
   String revision

   /**
    * The moment in time when this revision was released
    */
   Long released

   /**
    * The XML schema text
    */
   String schema

   static constraints = {
      revision(nullable: false, unique: true)
      released(nullable: false)
      schema(nullable: false)
   }

   static mapping = {
      columns {
         schema type: 'text'
      }

      //version false
      id generator:'sequence', params:[sequence:'ing_messageschema_id_seq']            
   }
}
