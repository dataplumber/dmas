/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile.jaxb;

import gov.nasa.podaac.common.api.jaxb.serviceprofile.DataFormatType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.FileClassType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.FileType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.GranuleFileType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.IngestDetailsType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.GranuleFileType.Sources;
import gov.nasa.podaac.common.api.serviceprofile.BasicFileInfo;
import gov.nasa.podaac.common.api.serviceprofile.GranuleFile;
import gov.nasa.podaac.common.api.serviceprofile.IngestDetails;
import gov.nasa.podaac.common.api.serviceprofile.Common.DataFormat;
import gov.nasa.podaac.common.api.serviceprofile.Common.FileClass;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GranuleFileJaxb extends AccessorBase implements GranuleFile {

   private GranuleFileType _jaxbObj;

   public GranuleFileJaxb() {
      this._jaxbObj = new GranuleFileType();
   }

   public GranuleFileJaxb(GranuleFile granuleFile) {
      this._jaxbObj = new GranuleFileType();

      this.setName(granuleFile.getName());
      this.setFormat(granuleFile.getFormat());
      this.setGroup(granuleFile.getGroup());
      this.setType(granuleFile.getType());

      Set<BasicFileInfo> sources = granuleFile.getSources();
      for (BasicFileInfo source : sources) {
         this.addSource(source);
      }
   }

   public GranuleFileJaxb(GranuleFileType jaxbObj) {
      this._jaxbObj = jaxbObj;
   }

   protected List<FileType> _getSources() {
      if (this._jaxbObj.getSources() == null) {
         this._jaxbObj.setSources(new Sources());
      }

      return this._jaxbObj.getSources().getSource();
   }

   public synchronized void addSource(BasicFileInfo source) {
      BasicFileInfo temp = source;
      if (!(temp.getImplObj() instanceof FileType)) {
         temp = new BasicFileInfoJaxb(source);
      }
      this._getSources().add((FileType) temp.getImplObj());
      temp.setOwner(this);
      source = temp;
   }

   public void clearSources() {
      this._getSources().clear();
   }

   public IngestDetails createIngestDetails() {
      return new IngestDetailsJaxb();
   }

   public BasicFileInfo createSource() {
      return new BasicFileInfoJaxb();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      final GranuleFileJaxb other = (GranuleFileJaxb) obj;
      if (_jaxbObj == null) {
         if (other._jaxbObj != null)
            return false;
      } else if (!_jaxbObj.equals(other._jaxbObj))
         return false;
      return true;
   }

   public DataFormat getFormat() {
      if (this._jaxbObj.getFormat() != null) {
         return DataFormat.valueOf(this._jaxbObj.getFormat().value());
      }
      return null;
   }

   public String getGroup() {
      return this._jaxbObj.getGroup();
   }

   @Override
   public Object getImplObj() {
      return this._jaxbObj;
   }

   public IngestDetails getIngestDetails() {
      IngestDetails details = new IngestDetailsJaxb(this._jaxbObj.getIngest());
      details.setOwner(this);
      return details;
   }

   public String getName() {
      return this._jaxbObj.getName();
   }

   public synchronized Set<BasicFileInfo> getSources() {
      Set<BasicFileInfo> result = new HashSet<BasicFileInfo>();
      List<FileType> files = this._getSources();
      for (FileType file : files) {
         BasicFileInfo info = new BasicFileInfoJaxb(file);
         info.setOwner(this);
         result.add(info);
      }
      return result;
   }

   public FileClass getType() {
      if (this._jaxbObj.getType() != null) {
         return FileClass.valueOf(this._jaxbObj.getType().value());
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

   public void setFormat(DataFormat format) {
      this._jaxbObj.setFormat(DataFormatType.fromValue(format.toString()));
   }

   public void setGroup(String group) {
      this._jaxbObj.setGroup(group);
   }

   public void setIngestDetails(IngestDetails details) {
      if (details == null) return;
      IngestDetails temp = details;
      if (!(temp.getImplObj() instanceof IngestDetailsType)) {
         temp = new IngestDetailsJaxb(details);
      }

      this._jaxbObj.setIngest((IngestDetailsType) temp.getImplObj());
      temp.setOwner(this);
      details = temp;
   }

   public void setName(String name) {
      this._jaxbObj.setName(name);
   }

   public void setType(FileClass type) {
      this._jaxbObj.setType(FileClassType.valueOf(type.toString()));
   }

}
