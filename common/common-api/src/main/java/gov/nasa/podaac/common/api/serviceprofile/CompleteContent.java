/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile;

import java.util.Set;


public interface CompleteContent extends Accessor {

   void addGranule(Granule granule);

   void clearGranules();

   Granule createGranule();

   Granule getGranule(String name);

   int getGranuleCount();
   
   GranuleFile getGranuleFile(GranuleFileLink link);
   
   Set<Granule> getGranules();

}
