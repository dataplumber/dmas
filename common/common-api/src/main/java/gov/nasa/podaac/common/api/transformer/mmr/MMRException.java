/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.transformer.mmr;

/**
 * MMR translation exception
 * 
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: MMRException.java 594 2008-02-13 01:15:29Z thuang $
 */
public class MMRException extends Exception {

   private static final long serialVersionUID = 1L;

   public MMRException() {
      super();
   }

   public MMRException(String message) {
      super(message);
   }

   public MMRException(String message, Throwable cause) {
      super(message, cause);
   }

   public MMRException(Throwable cause) {
      super(cause);
   }
}
