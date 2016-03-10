/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile.jaxb;

import gov.nasa.podaac.common.api.jaxb.serviceprofile.ArchiveGranuleFileType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.FileClassType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.IngestDetailsType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.TimeStampType;
import gov.nasa.podaac.common.api.serviceprofile.ArchiveFileInfo;
import gov.nasa.podaac.common.api.serviceprofile.Common;
import gov.nasa.podaac.common.api.serviceprofile.IngestDetails;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

public class ArchiveFileInfoJaxb extends AccessorBase implements
      ArchiveFileInfo {

   protected static Log _logger = LogFactory.getLog(ArchiveFileInfoJaxb.class);

   private ArchiveGranuleFileType _jaxbObj;

   public ArchiveFileInfoJaxb() {
      this._jaxbObj = new ArchiveGranuleFileType();
   }

   public ArchiveFileInfoJaxb(ArchiveFileInfo info) {
      this._jaxbObj = new ArchiveGranuleFileType();
      this.setIngestDetails(info.getIngestDetails());
      this.setType(info.getType());
      this.setDestination(info.getDestination());
      if (info.getArchiveStartTime() != null) {
         this.setArchiveStartTime(info.getArchiveStartTime());
         this.setArchiveStopTime(info.getArchiveStopTime());
      }
   }

   public ArchiveFileInfoJaxb(ArchiveGranuleFileType jaxbObj) {
      this._jaxbObj = jaxbObj;
   }

   protected TimeStampType _getTime() {
      if (this._jaxbObj.getIngest().getTime() == null) {
         this._jaxbObj.getIngest().setTime(new TimeStampType());
      }
      return this._jaxbObj.getIngest().getTime();
   }

   public Date getArchiveStartTime() {
      TimeStampType time = this._jaxbObj.getIngest().getTime();
      if (time != null) {
         if (time.getStart() != null) {
            return new Date(time.getStart().longValue());
         }
      }
      return null;
   }

   public Date getArchiveStopTime() {
      TimeStampType time = this._jaxbObj.getIngest().getTime();
      if (time != null) {
         if (time.getStop() != null) {
            return new Date(time.getStop().longValue());
         }
      }
      return null;
   }

   public void setArchiveStartTime(Date startTime) {
      if (startTime != null) {
         this.setArchiveStartTime(startTime.getTime());
      }
   }

   public void setArchiveStartTime(long startTime) {
      this._getTime().setStart(BigInteger.valueOf(startTime));
   }

   public void setArchiveStopTime(Date stopTime) {
      if (stopTime != null) {
         this.setArchiveStopTime(stopTime.getTime());
      }
   }

   public void setArchiveStopTime(long stopTime) {
      this._getTime().setStop(BigInteger.valueOf(stopTime));
   }

   @Override
   public Object getImplObj() {
      return this._jaxbObj;
   }

   public IngestDetails getIngestDetails() {
      IngestDetails details = null;
      if (this._jaxbObj.getIngest() != null) {
         details = new IngestDetailsJaxb(this._jaxbObj.getIngest());
         details.setOwner(this);
      }
      return details;
   }

   public void setIngestDetails(IngestDetails ingestDetails) {
      if (ingestDetails == null)
         return;
      IngestDetails temp = ingestDetails;
      if (!(temp.getImplObj() instanceof IngestDetailsType)) {
         temp = new IngestDetailsJaxb(ingestDetails);
         temp.setOwner(this);
      }
      this._jaxbObj.setIngest((IngestDetailsType) temp.getImplObj());
      ingestDetails = temp;
   }

   public Common.FileClass getType() {
      if (this._jaxbObj.getType() == null)
         return null;

      return Common.FileClass.valueOf(this._jaxbObj.getType().toString());
   }

   public void setType(Common.FileClass type) {
      if (type == null)
         return;

      this._jaxbObj.setType(FileClassType.valueOf(type.toString()));
   }

   public URI getDestination() {
      URI result = null;
      if (this._jaxbObj.getDestination() != null) {
         try {
            result = new URI(this._jaxbObj.getDestination().trim());
         } catch (URISyntaxException e) {
            if (ArchiveFileInfoJaxb._logger.isDebugEnabled())
               e.printStackTrace();
            ArchiveFileInfoJaxb._logger.error("Invalid URI format: "
                  + this._jaxbObj.getDestination());
         }
      }
      return result;
   }

   public void setDestination(URI destination) {
      this._jaxbObj.setDestination(destination.toString());
   }

}
