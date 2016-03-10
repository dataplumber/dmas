/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile.jaxb;

import gov.nasa.podaac.common.api.jaxb.serviceprofile.CompleteSubmissionType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.GranuleType;
import gov.nasa.podaac.common.api.serviceprofile.CompleteContent;
import gov.nasa.podaac.common.api.serviceprofile.Granule;
import gov.nasa.podaac.common.api.serviceprofile.GranuleFile;
import gov.nasa.podaac.common.api.serviceprofile.GranuleFileLink;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CompleteContentJaxb extends AccessorBase implements
   CompleteContent {

   private CompleteSubmissionType _jaxbObj;

   public CompleteContentJaxb() {
      this._jaxbObj = new CompleteSubmissionType();
   }

   public CompleteContentJaxb(CompleteContent content) {
      this._jaxbObj = new CompleteSubmissionType();

      Set<Granule> granules = content.getGranules();
      for (Granule granule : granules) {
         this.addGranule(granule);
      }
   }

   public CompleteContentJaxb(CompleteSubmissionType jaxbObj) {
      this._jaxbObj = jaxbObj;
   }

   public void addGranule(Granule granule) {
      List<GranuleType> granules = this._jaxbObj.getGranule();

      Granule temp = granule;
      if (!(temp.getImplObj() instanceof GranuleType)) {
         temp = new GranuleJaxb(granule);
      }

      granules.add((GranuleType) temp.getImplObj());
      temp.setOwner(this);
      granule = temp;
   }

   public void clearGranules() {
      this._jaxbObj.getGranule().clear();
   }

   public Granule createGranule() {
      return new GranuleJaxb();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      final CompleteContentJaxb other = (CompleteContentJaxb) obj;
      if (_jaxbObj == null) {
         if (other._jaxbObj != null)
            return false;
      } else if (!_jaxbObj.equals(other._jaxbObj))
         return false;
      return true;
   }

   public synchronized Granule getGranule(String name) {
      Granule result = null;
      
      List<GranuleType> granules = this._jaxbObj.getGranule();
      for (GranuleType granule : granules) {
         if (granule.getName().equals(name)) {
            result = new GranuleJaxb(granule);
            result.setOwner(this);
            break;
         }
      }
      return result;
   }

   public int getGranuleCount() {
      return this._jaxbObj.getGranule().size();
   }
   
   public GranuleFile getGranuleFile(GranuleFileLink link) {
      GranuleFile result = null;
      Granule granule = this.getGranule(link.getGranuleName());
      if (granule != null) {
         for (GranuleFile file : granule.getFiles()) {
            if (file.getName().equals(link.getFileName())) {
               result = file;
            }
         }
      }
      
      return result;
   }
   
   public Set<Granule> getGranules() {
      Set<Granule> result = new HashSet<Granule>();
      List<GranuleType> granules = this._jaxbObj.getGranule();
      for (GranuleType granule : granules) {
         Granule g = new GranuleJaxb(granule);
         g.setOwner(this);
         result.add(g);
      }
      return result;
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

}
