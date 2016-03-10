/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile.jaxb;

import gov.nasa.podaac.common.api.jaxb.serviceprofile.ChecksumType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.CompressionType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.FileType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.FileType.Checksum;
import gov.nasa.podaac.common.api.serviceprofile.IngestDetails;
import gov.nasa.podaac.common.api.serviceprofile.Common.ChecksumAlgorithm;
import gov.nasa.podaac.common.api.serviceprofile.Common.CompressionAlgorithm;

import java.net.URI;
import java.net.URISyntaxException;
import java.math.BigInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BasicFileInfoJaxb extends AccessorBase implements
      gov.nasa.podaac.common.api.serviceprofile.BasicFileInfo {

   public static Log _logger = LogFactory.getLog(BasicFileInfoJaxb.class);

   private FileType _jaxbObj;

   public BasicFileInfoJaxb() {
      this._jaxbObj = new FileType();
   }

   public BasicFileInfoJaxb(FileType jaxbObj) {
      this._jaxbObj = jaxbObj;
   }

   public BasicFileInfoJaxb(
         gov.nasa.podaac.common.api.serviceprofile.BasicFileInfo fileType) {
      this._jaxbObj = new FileType();

      if (fileType.getChecksum() != null) {
         this.setChecksum(fileType.getChecksum());
         this.setChecksumAlgorithm(fileType.getChecksumAlgorithm());
      }
      if (fileType.getCompressionAlgorithm() != null)
         this.setCompressionAlgorithm(fileType.getCompressionAlgorithm());
      this.setLink(fileType.getLink());
   }

   protected Checksum _getChecksumType() {
      Checksum result = this._jaxbObj.getChecksum();
      if (result == null) {
         result = new Checksum();
         this._jaxbObj.setChecksum(result);
      }
      return result;
   }

   public IngestDetails createIngestDetails() {
      return new IngestDetailsJaxb();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      final BasicFileInfoJaxb other = (BasicFileInfoJaxb) obj;
      if (_jaxbObj == null) {
         if (other._jaxbObj != null)
            return false;
      } else if (!_jaxbObj.equals(other._jaxbObj))
         return false;
      return true;
   }

   public String getChecksum() {
      if (this._jaxbObj.getChecksum() != null)
         return this._jaxbObj.getChecksum().getValue();
      return null;
   }

   public ChecksumAlgorithm getChecksumAlgorithm() {
      if (this._jaxbObj.getChecksum() != null
            && this._jaxbObj.getChecksum().getType() != null) {
         return ChecksumAlgorithm.valueOf(this._jaxbObj.getChecksum().getType()
               .value());
      }
      return null;
   }

   public CompressionAlgorithm getCompressionAlgorithm() {
      if (this._jaxbObj.getCompression() != null)
         return CompressionAlgorithm.valueOf(this._jaxbObj.getCompression()
               .value());
      return null;
   }

   @Override
   public Object getImplObj() {
      return this._jaxbObj;
   }

   public URI getLink() {
      URI result = null;
      try {
         result = new URI(this._jaxbObj.getLink().trim());
      } catch (URISyntaxException e) {
         if (BasicFileInfoJaxb._logger.isDebugEnabled())
            e.printStackTrace();
         BasicFileInfoJaxb._logger.error("Invalid URI format: "
               + this._jaxbObj.getLink());
      }
      return result;
   }


   public Long getSize() {
      if (this._jaxbObj.getSize() != null) {
         return this._jaxbObj.getSize().longValue();
      }
      return null;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((_jaxbObj == null) ? 0 : _jaxbObj.hashCode());
      return result;
   }

   public void setChecksum(String checksum) {
      this._getChecksumType().setValue(checksum);
   }

   public void setChecksumAlgorithm(ChecksumAlgorithm algorithm) {
      this._getChecksumType().setType(
            ChecksumType.fromValue(algorithm.toString()));
   }

   public void setCompressionAlgorithm(CompressionAlgorithm algorithm) {
      this._jaxbObj.setCompression(CompressionType.fromValue(algorithm
            .toString()));
   }

   public void setLink(URI link) {
      this._jaxbObj.setLink(link.toString());
   }

   public void setSize(long size) {
      this._jaxbObj.setSize(BigInteger.valueOf(size));
   }

}
