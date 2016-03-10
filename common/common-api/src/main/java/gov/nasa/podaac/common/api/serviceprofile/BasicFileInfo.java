/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile;

import gov.nasa.podaac.common.api.serviceprofile.Common.ChecksumAlgorithm;
import gov.nasa.podaac.common.api.serviceprofile.Common.CompressionAlgorithm;

import java.net.URI;

/**
 * Interface to capture physical file attributes
 * 
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: BasicFileInfo.java 1579 2008-08-07 22:10:41Z thuang $
 * 
 */
public interface BasicFileInfo extends Accessor {

   /**
    * Factory method to create file ingestion details
    * 
    * @return object for file ingestion deails
    */
   IngestDetails createIngestDetails();

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
   ChecksumAlgorithm getChecksumAlgorithm();

   /**
    * Method to return the compression algorithm used.
    * 
    * @return the compression algorithm used.
    */
   CompressionAlgorithm getCompressionAlgorithm();

   /**
    * Method to return the URI link to the file.
    * 
    * @return the URI link to the file
    */
   URI getLink();

   /**
    * Method to return the size of the file
    *
    * @return the source file size
    */
   Long getSize();
   
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
   void setChecksumAlgorithm(ChecksumAlgorithm algorithm);

   /**
    * Method to set the compression algorithm
    * 
    * @param algorithm the compression algorithm
    */
   void setCompressionAlgorithm(CompressionAlgorithm algorithm);

   /**
    * Method to set the URI link to the file
    * 
    * @param link the URI link
    */
   void setLink(URI link);
   
   /**
    * Method to set the file size
    *
    * @param size the file size
    */
   void setSize(long size);

}
