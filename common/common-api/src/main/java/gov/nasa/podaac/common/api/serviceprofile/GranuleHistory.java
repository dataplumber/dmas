/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile;

import java.util.Date;

/**
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: GranuleHistory.java 1239 2008-05-30 07:04:36Z thuang $
 */
public interface GranuleHistory extends Accessor {

   String getVersion();

   void setVersion(String version);

   Date getCreateDate();

   void setCreateDate(Date date);

   void setCreateDate(long date);

   Date getLastRevisionDate();

   void setLastRevisionDate(Date date);

   void setLastRevisionDate(long date);

   String getRevisionHistory();

   void setRevisionHistory(String history);
}
