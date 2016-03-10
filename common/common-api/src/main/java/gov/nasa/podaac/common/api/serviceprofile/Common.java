/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile;

public interface Common {
   enum AccessRole {
      PRIVATE, RESTRICTED, PUBLIC
   };

   enum ChecksumAlgorithm {
      MD2, MD5, SHA1, SHA256, SHA384, SHA512
   };

   enum CompressionAlgorithm {
      BZIP2, GZIP, ZIP, NONE, UNIX
   };

   enum DataFormat {
      RAW, HDF, HDF5, NETCDF, LOD, GRIB, JPG, PNG, ASCII, JPEG, TIFF, GeoTIFF, GIF
   };

   enum DataPass {
      ASCENDING, DESCENDING
   };

   enum DayNight {
      DAY, NIGHT
   };

   enum FileClass {
      DATA, METADATA, CHECKSUM, THUMBNAIL, IMAGE
   }

   enum MessageLevel {
      VERBOSE, ERRORONLY, SILENT;

      public static boolean toNotify(MessageLevel level, boolean hasError) {
         if (level == VERBOSE)
            return true;
         if (level == ERRORONLY && hasError)
            return true;
         return false;
      }
   };

   enum Status {
      READY, STAGED, REGISTERED, ARCHIVED, ERROR, REJECTED
   };
}
