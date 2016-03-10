/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile;

import java.net.URI;
import java.util.Date;

public interface IngestDetails extends Accessor {

   /**
    * Method to return the checksum value of the file
    *
    * @return the checksum value
    */
   String getChecksum();

   /**
    * Method to return the checksum algorithm used.
    *
    * @return the checksum algorithm used.
    */
   Common.ChecksumAlgorithm getChecksumAlgorithm();

   Date getIngestStartTime();

   Date getIngestStopTime();

   URI getLocalStaging();

   URI getRemoteStaging();

   Long getSize();

   BasicFileInfo getSource();

   /**
    * Method to set the file checksum value.
    *
    * @param checksum the checksum value
    */
   void setChecksum(String checksum);

   /**
    * Method to set the checksum algorithm
    *
    * @param algorithm the checksum algorithm
    */
   void setChecksumAlgorithm(Common.ChecksumAlgorithm algorithm);


   void setIngestStartTime(Date time);

   void setIngestStartTime(long time);

   void setIngestStopTime(Date time);

   void setIngestStopTime(long time);

   void setLocalStaging(URI localStaging);

   void setRemoteStaging(URI remoteStaging);

   void setSize(long size);

   void setSource(BasicFileInfo source);
}
