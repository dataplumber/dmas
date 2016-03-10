/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile.jaxb;

import gov.nasa.podaac.common.api.jaxb.serviceprofile.ChecksumType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.FileType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.IngestDetailsType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.IngestDetailsType.Checksum;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.TimeStampType;
import gov.nasa.podaac.common.api.serviceprofile.BasicFileInfo;
import gov.nasa.podaac.common.api.serviceprofile.Common;
import gov.nasa.podaac.common.api.serviceprofile.IngestDetails;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

public class IngestDetailsJaxb extends AccessorBase implements IngestDetails {

   private static Log _logger = LogFactory.getLog(IngestDetailsJaxb.class);

   private IngestDetailsType _jaxbObj;

   public IngestDetailsJaxb() {
      this._jaxbObj = new IngestDetailsType();
   }

   public IngestDetailsJaxb(IngestDetails details) {
      this._jaxbObj = new IngestDetailsType();

      this.setIngestStartTime(details.getIngestStartTime());
      this.setIngestStopTime(details.getIngestStopTime());
      this.setLocalStaging(details.getLocalStaging());
      this.setRemoteStaging(details.getRemoteStaging());
      this.setSize(details.getSize());
      this.setSource(details.getSource());
   }

   public IngestDetailsJaxb(IngestDetailsType jaxbObj) {
      this._jaxbObj = jaxbObj;
   }

   protected TimeStampType _getTime() {
      if (this._jaxbObj.getTime() == null) {
         this._jaxbObj.setTime(new TimeStampType());
      }
      return this._jaxbObj.getTime();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      final IngestDetailsJaxb other = (IngestDetailsJaxb) obj;
      if (_jaxbObj == null) {
         if (other._jaxbObj != null)
            return false;
      } else if (!_jaxbObj.equals(other._jaxbObj))
         return false;
      return true;
   }

   @Override
   public Object getImplObj() {
      return this._jaxbObj;
   }

   public String getChecksum() {
      if (this._jaxbObj.getChecksum() != null)
         return this._jaxbObj.getChecksum().getValue();
      return null;
   }

   public Common.ChecksumAlgorithm getChecksumAlgorithm() {
      if (this._jaxbObj.getChecksum() != null
            && this._jaxbObj.getChecksum().getType() != null) {
         return Common.ChecksumAlgorithm.valueOf(this._jaxbObj.getChecksum().getType()
               .value());
      }
      return null;
   }

   public Date getIngestStartTime() {
      if (this._jaxbObj.getTime() != null
            && this._jaxbObj.getTime().getStart() != null) {
         return new Date(this._jaxbObj.getTime().getStart().longValue());
      }
      return null;
   }

   public Date getIngestStopTime() {
      if (this._jaxbObj.getTime() != null
            && this._jaxbObj.getTime().getStop() != null) {
         return new Date(this._jaxbObj.getTime().getStop().longValue());
      }
      return null;
   }

   public URI getLocalStaging() {
      URI result = null;

      if (this._jaxbObj.getLocalStaging() != null) {
         try {
            result = new URI(this._jaxbObj.getLocalStaging().trim());
         } catch (URISyntaxException e) {
            if (IngestDetailsJaxb._logger.isDebugEnabled()) {
               e.printStackTrace();
            }
            IngestDetailsJaxb._logger.error("Invalid URI format: "
                  + this._jaxbObj.getLocalStaging());

         }
      }
      return result;
   }

   public URI getRemoteStaging() {
      URI result = null;

      if (this._jaxbObj.getRemoteStaging() != null) {
         try {
            result = new URI(this._jaxbObj.getRemoteStaging().trim());
         } catch (URISyntaxException e) {
            if (IngestDetailsJaxb._logger.isDebugEnabled()) {
               e.printStackTrace();
            }
            IngestDetailsJaxb._logger.error("Invalid URI format: "
                  + this._jaxbObj.getRemoteStaging());

         }
      }
      return result;
   }

   public Long getSize() {
      if (this._jaxbObj.getSize() != null)
         return this._jaxbObj.getSize().longValue();
      return null;
   }

   public BasicFileInfo getSource() {
      if (this._jaxbObj.getSource() == null)
         return null;

      BasicFileInfo info = new BasicFileInfoJaxb(this._jaxbObj.getSource());
      info.setOwner(this);
      return info;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((_jaxbObj == null) ? 0 : _jaxbObj.hashCode());
      return result;
   }

   protected Checksum _getChecksumType() {
      Checksum result = this._jaxbObj.getChecksum();
      if (result == null) {
         result = new Checksum();
         this._jaxbObj.setChecksum(result);
      }
      return result;
   }

   public void setChecksum(String checksum) {
      this._getChecksumType().setValue(checksum);
   }

   public void setChecksumAlgorithm(Common.ChecksumAlgorithm algorithm) {
      this._getChecksumType().setType(
            ChecksumType.fromValue(algorithm.toString()));
   }

   public void setIngestStartTime(Date time) {
      if (time == null)
         return;
      this._getTime().setStart(BigInteger.valueOf(time.getTime()));
   }

   public void setIngestStartTime(long time) {
      this._getTime().setStart(BigInteger.valueOf(time));
   }

   public void setIngestStopTime(Date time) {
      if (time == null)
         return;
      this._getTime().setStop(BigInteger.valueOf(time.getTime()));
   }

   public void setIngestStopTime(long time) {
      this._getTime().setStop(BigInteger.valueOf(time));
   }

   public void setLocalStaging(URI localStaging) {
      this._jaxbObj.setLocalStaging(localStaging.toString());
   }

   public void setRemoteStaging(URI remoteStaging) {
      this._jaxbObj.setRemoteStaging(remoteStaging.toString());
   }

   public void setSize(long size) {
      this._jaxbObj.setSize(BigInteger.valueOf(size));
   }

   public void setSource(BasicFileInfo source) {
      BasicFileInfo temp = source;
      if (!(temp.getImplObj() instanceof FileType)) {
         temp = new BasicFileInfoJaxb(source);
      }
      this._jaxbObj.setSource((FileType) temp.getImplObj());
      temp.setOwner(this);
      source = temp;
   }

}
