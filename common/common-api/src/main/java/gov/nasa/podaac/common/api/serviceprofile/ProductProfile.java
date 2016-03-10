/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile;

import java.util.Set;


/**
 * Root interface for product-related profile objects. This is the root object
 * to access submission data.
 * 
 * @author Thomas Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: ProductProfile.java 1234 2008-05-30 04:45:50Z thuang $
 * 
 */
public interface ProductProfile extends Accessor {
   void addNotification(Notification notification);

   void clearNotifications();

   ArchiveProfile createArchiveProfile();

   IngestProfile createIngestProfile();

   Notification createNotification();

   ArchiveProfile getArchiveProfile();

   IngestProfile getIngestProfile();

   Set<Notification> getNotifications();

   void setArchiveProfile(ArchiveProfile archiveProfile);

   void setIngestProfile(IngestProfile ingestProfile);
}
