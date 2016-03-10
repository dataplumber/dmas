/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile;

import gov.nasa.podaac.common.api.serviceprofile.Common.FileClass;

import java.util.Date;


import java.net.URI;

/**
 * Interface to the ArchiveFileType defined in the PO.DAAC message specification.
 *
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: ArchiveFileInfo.java 1691 2008-08-27 22:58:10Z thuang $
 */
public interface ArchiveFileInfo extends Accessor {

   /**
    * Getter for the ingest deatils
    *
    * @return the ingest deatils object
    */
   IngestDetails getIngestDetails();

   /**
    * Setter for the ingest details
    *
    * @param ingestDetails the ingest details
    */
   void setIngestDetails(IngestDetails ingestDetails);

   /**
    * Getter for the file type
    *
    * @return the file type
    */
   FileClass getType();

   /**
    * Setter for the file type
    *
    * @param type the file type
    */
   void setType(FileClass type);

   /**
    * Setter for the archive destination.
    *
    * @param destination URI for the archive destination.
    */
   void setDestination(URI destination);

   /**
    * Getter for the archive destination of the file.
    *
    * @return URI for the archive destination.
    */
   URI getDestination();

   /**
    * Getter for the archive start time
    *
    * @return archive start time
    */
   Date getArchiveStartTime();

   /**
    * Getter for the archive stop time
    *
    * @return the archive stop time
    */
   Date getArchiveStopTime();

   /**
    * Setter for the archive start time
    *
    * @param startTime archive start time
    */
   void setArchiveStartTime(Date startTime);

   /**
    * Setter for the archive start time
    *
    * @param startTime the archive start time
    */
   void setArchiveStartTime(long startTime);

   /**
    * Setter for the archive stop time
    *
    * @param stopTime the archive stop time
    */
   void setArchiveStopTime(Date stopTime);

   /**
    * Setter for the archive stop time
    *
    * @param stopTime the archive stop time
    */
   void setArchiveStopTime(long stopTime);

}
