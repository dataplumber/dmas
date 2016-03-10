/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.podaac.common.api.serviceprofile.jaxb;

import gov.nasa.podaac.common.api.jaxb.serviceprofile.AccessRoleType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.ArchiveGranuleFileType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.ArchiveGranuleType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.TimeStampType;
import gov.nasa.podaac.common.api.serviceprofile.ArchiveFileInfo;
import gov.nasa.podaac.common.api.serviceprofile.ArchiveGranule;
import gov.nasa.podaac.common.api.serviceprofile.Common.AccessRole;

import java.math.BigInteger;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of Archive Granule object
 *
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id:$
 */
public class ArchiveGranuleJaxb extends AccessorBase implements ArchiveGranule {

   private ArchiveGranuleType _jaxbObj;

   public ArchiveGranuleJaxb() {
      this._jaxbObj = new ArchiveGranuleType();
   }

   public ArchiveGranuleJaxb(ArchiveGranule granule) {
      this._jaxbObj = new ArchiveGranuleType();
      this.setName(granule.getName());
      this.setDatasetName(granule.getDatasetName());
      this.setProductType(granule.getProductType());
      this.setGranuleId(granule.getGranuleId());
      List<ArchiveFileInfo> files = granule.getFiles();
      for (ArchiveFileInfo file : files) {
         this.addFile(file);
      }
      if (granule.getDeleteName() != null) {
         this.setDeleteName(granule.getDeleteName());
         List<String> deletes = granule.getDeletes();
         for (String delete : deletes)
            this.addDeleteUri(delete);
      }
      this.setArchiveSuccess(granule.isArchiveSuccess());
      this.setArchiveNote(granule.getArchiveNote());
      if (granule.getArchiveStartTime() != null) {
         this.setArchiveStartTime(granule.getArchiveStartTime());
         this.setArchiveStopTime(granule.getArchiveStopTime());
      }
   }

   public ArchiveGranuleJaxb(ArchiveGranuleType jaxbObj) {
      this._jaxbObj = jaxbObj;
   }

   public String getName() {
      return this._jaxbObj.getName();
   }

   public void setName(String name) {
      this._jaxbObj.setName(name);
   }

   public String getDatasetName() {
      return this._jaxbObj.getDatasetName();
   }

   public void setDatasetName(String datasetName) {
      this._jaxbObj.setDatasetName(datasetName);
   }

   public String getProductType() {
      return this._jaxbObj.getProductType();
   }

   public void setProductType(String productType) {
      this._jaxbObj.setProductType(productType);
   }

   public Long getGranuleId() {
      return this._jaxbObj.getGranuleId().longValue();
   }

   public void setGranuleId(long granuleId) {
      this._jaxbObj.setGranuleId(BigInteger.valueOf(granuleId));
   }

   public List<ArchiveFileInfo> getFiles() {
      List<ArchiveFileInfo> result = new LinkedList<ArchiveFileInfo>();

      List<ArchiveGranuleFileType> files = this._jaxbObj.getFiles().getFile();
      for (ArchiveGranuleFileType file : files) {
         ArchiveFileInfo fileInfo = new ArchiveFileInfoJaxb(file);
         fileInfo.setOwner(this);
         result.add(fileInfo);
      }

      return result;
   }

   public ArchiveFileInfo createFile() {
      return new ArchiveFileInfoJaxb();
   }

   public void addFile(ArchiveFileInfo archiveFileInfo) {
      ArchiveFileInfo temp = archiveFileInfo;
      if (!(temp.getImplObj() instanceof ArchiveGranuleFileType)) {
         temp = new ArchiveFileInfoJaxb(archiveFileInfo);
         temp.setOwner(this);
      }

      if (this._jaxbObj.getFiles() == null) {
         this._jaxbObj.setFiles(new ArchiveGranuleType.Files());
      }

      this._jaxbObj.getFiles().getFile().add((ArchiveGranuleFileType) temp.getImplObj());
      archiveFileInfo = temp;
   }

   public void clearFiles() {
      this._jaxbObj.getFiles().getFile().clear();
   }

   public String getDeleteName() {
      String name = null;
      if (this._jaxbObj.getDelete() != null) {
         name = this._jaxbObj.getDelete().getName();
      }

      return name;
   }

   public void setDeleteName(String name) {
      if (this._jaxbObj.getDelete() == null) {
         this._jaxbObj.setDelete(new ArchiveGranuleType.Delete());
      }
      this._jaxbObj.getDelete().setName(name);
   }

   public List<String> getDeletes() {
      if (this._jaxbObj.getDelete() != null) {
         return this._jaxbObj.getDelete().getUri();
      }
      return new LinkedList<String>();
   }

   public void addDeleteUri(String uri) {
      if (this._jaxbObj.getDelete() == null) {
         this._jaxbObj.setDelete(new ArchiveGranuleType.Delete());
      }
      this._jaxbObj.getDelete().getUri().add(uri);
   }

   public void clearDeletes() {
      if (this._jaxbObj.getDelete() != null) {
         this._jaxbObj.getDelete().getUri().clear();
         this._jaxbObj.setDelete(null);
      }
   }

   public boolean isArchiveSuccess() {
      return this._jaxbObj.isArchiveSuccess();
   }

   public void setArchiveSuccess(boolean success) {
      this._jaxbObj.setArchiveSuccess(success);
   }

   public AccessRole getAccessConstraint() {
      if (this._jaxbObj.getAccessConstraint() != null)
         return AccessRole.valueOf(this._jaxbObj.getAccessConstraint()
               .toString());
      return null;
   }

   public String getArchiveNote() {
      return this._jaxbObj.getArchiveNote();
   }

   public void setArchiveNote(String note) {
      this._jaxbObj.setArchiveNote(note);
   }

   protected TimeStampType _getTime() {
      if (this._jaxbObj.getArchiveTime() == null) {
         this._jaxbObj.setArchiveTime(new TimeStampType());
      }
      return this._jaxbObj.getArchiveTime();
   }

   public Date getArchiveStartTime() {
      if (this._jaxbObj.getArchiveTime() == null ||
            this._jaxbObj.getArchiveTime().getStart() == null) {
         return null;
      }
      return new Date(this._jaxbObj.getArchiveTime().getStart().longValue());
   }

   public Date getArchiveStopTime() {
      if (this._jaxbObj.getArchiveTime() == null ||
            this._jaxbObj.getArchiveTime().getStop() == null) {
         return null;
      }
      return new Date(this._jaxbObj.getArchiveTime().getStop().longValue());
   }

   public void setAccessConstraint(AccessRole accessRole) {
      this._jaxbObj.setAccessConstraint(AccessRoleType.valueOf(accessRole
            .toString()));
   }

   public void setArchiveStartTime(Date startTime) {
      TimeStampType time = this._getTime();
      if (startTime != null)
         time.setStart(BigInteger.valueOf(startTime.getTime()));

   }

   public void setArchiveStartTime(long startTime) {
      TimeStampType time = this._getTime();
      time.setStart(BigInteger.valueOf(startTime));
   }

   public void setArchiveStopTime(Date stopTime) {
      TimeStampType time = this._getTime();
      if (stopTime != null)
         time.setStop(BigInteger.valueOf(stopTime.getTime()));
   }

   public void setArchiveStopTime(long stopTime) {
      TimeStampType time = this._getTime();
      time.setStop(BigInteger.valueOf(stopTime));
   }

   public Object getImplObj() {
      return this._jaxbObj;
   }

}
