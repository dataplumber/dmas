/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile;

public interface GranuleFileLink extends Accessor {

   String getFileName();

   String getGranuleName();

   void setFileName(String name);

   void setGranuleName(String name);

}
