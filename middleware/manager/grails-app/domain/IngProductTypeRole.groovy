/*
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 */

/**
 * Definition of Product Type Role class
 *
 * @author T. Huang [Thomas.Huang@jpl.nasa.gov]
 * @version $Id: IngProductTypeRole.groovy 2173 2008-10-29 20:06:48Z thuang $
 */
class IngProductTypeRole {

   /**
    * The product type reference
    */
   IngProductType productType

   /**
    * The access role reference
    */
   IngAccessRole role

   static constraints = {
      productType(nullable: false)
      role(nullable: false)
   }

   static mapping = {
      //version false
      id generator:'sequence', params:[sequence:'ing_producttyperole_id_seq']            
   }
}
