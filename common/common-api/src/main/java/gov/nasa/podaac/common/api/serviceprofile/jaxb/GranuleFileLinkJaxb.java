/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile.jaxb;

import gov.nasa.podaac.common.api.jaxb.serviceprofile.GranuleFileLinkType;
import gov.nasa.podaac.common.api.serviceprofile.Accessor;
import gov.nasa.podaac.common.api.serviceprofile.GranuleFileLink;

public class GranuleFileLinkJaxb extends AccessorBase implements
      GranuleFileLink, Accessor {

   private GranuleFileLinkType _jaxbObj;

   public GranuleFileLinkJaxb(GranuleFileLink link) {
      this._jaxbObj = new GranuleFileLinkType();
      if (link != null) {
         this._jaxbObj.setGranule(link.getGranuleName());
         this._jaxbObj.setName(link.getFileName());
      }
   }

   public GranuleFileLinkJaxb(GranuleFileLinkType jaxbObj) {
      this._jaxbObj = jaxbObj;
   }

   public GranuleFileLinkJaxb(String granuleName, String fileName) {
      this._jaxbObj = new GranuleFileLinkType();
      this._jaxbObj.setGranule(granuleName);
      this._jaxbObj.setName(fileName);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      final GranuleFileLinkJaxb other = (GranuleFileLinkJaxb) obj;
      if (_jaxbObj == null) {
         if (other._jaxbObj != null)
            return false;
      } else if (!_jaxbObj.equals(other._jaxbObj))
         return false;
      return true;
   }

   public String getFileName() {
      return this._jaxbObj.getName();
   }

   public String getGranuleName() {
      return this._jaxbObj.getGranule();
   }

   @Override
   public Object getImplObj() {
      return this._jaxbObj;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((_jaxbObj == null) ? 0 : _jaxbObj.hashCode());
      return result;
   }

   public void setFileName(String name) {
      this._jaxbObj.setGranule(name);
   }

   public void setGranuleName(String name) {
      this._jaxbObj.setName(name);
   }

}
