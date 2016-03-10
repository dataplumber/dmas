/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile;

import java.util.List;


/**
 *
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: ArchiveProfile.java 1691 2008-08-27 22:58:10Z thuang $
 *
 */
public interface ArchiveProfile extends Accessor {

   void addGranule(ArchiveGranule granule);

   void clearGranules();

   List<ArchiveGranule> getGranules();

   ArchiveGranule createGranule();

}
