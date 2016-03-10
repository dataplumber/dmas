/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.podaac.common.api.transformer.lod;

import java.util.List;

/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id$
 */
public abstract class DefaultLodPopulator implements LodPopulator {
   public static final String NOT_AVAILABLE = "N/A";
   
   public String getGranuleName(List<String> values) {
      return values.get(1);
   }
   
   protected boolean _isEmpty(String value) {
      return ((value == null) || (value.equals("")) || (NOT_AVAILABLE.equals(value)));
   }
}
