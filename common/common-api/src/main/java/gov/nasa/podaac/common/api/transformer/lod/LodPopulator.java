/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.podaac.common.api.transformer.lod;

import gov.nasa.podaac.common.api.serviceprofile.Granule;

import java.util.List;

/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id: LodPopulator.java 2119 2008-10-19 23:19:07Z axt $
 */
public interface LodPopulator {
   public String getGranuleName(List<String> values);
   
   public void populate(Granule granule, List<String> values) throws Exception;
}
