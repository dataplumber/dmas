/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile.jaxb;

import gov.nasa.podaac.common.api.jaxb.serviceprofile.ArchiveGranuleType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.ArchiveType;
import gov.nasa.podaac.common.api.serviceprofile.ArchiveFileInfo;
import gov.nasa.podaac.common.api.serviceprofile.ArchiveProfile;
import gov.nasa.podaac.common.api.serviceprofile.ArchiveGranule;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.LinkedList;

public class ArchiveProfileJaxb extends AccessorBase implements ArchiveProfile {

   private ArchiveType _jaxbObj;

   public ArchiveProfileJaxb() {
      this._jaxbObj = new ArchiveType();
   }

   public ArchiveProfileJaxb(ArchiveProfile archive) {
      this._jaxbObj = new ArchiveType();

      List<ArchiveGranule> granules = archive.getGranules();
      for (ArchiveGranule granule : granules) {
         this.addGranule(granule);
      }
   }

   public ArchiveProfileJaxb(ArchiveType jaxbObj) {
      this._jaxbObj = jaxbObj;
   }

   @Override
   public Object getImplObj() {
      return this._jaxbObj;
   }

   public void addGranule(ArchiveGranule granule) {
      ArchiveGranule temp = granule;
      if (!(temp.getImplObj() instanceof ArchiveGranuleType)) {
         temp = new ArchiveGranuleJaxb(granule);
         temp.setOwner(this);
      }
      this._jaxbObj.getGranule().add((ArchiveGranuleType) temp.getImplObj());
      granule = temp;
   }

   public void clearGranules() {
      this._jaxbObj.getGranule().clear();
   }

   public List<ArchiveGranule> getGranules() {
      List<ArchiveGranule> result = new LinkedList<ArchiveGranule>();
      for (ArchiveGranuleType granule : this._jaxbObj.getGranule()) {
         ArchiveGranuleJaxb obj = new ArchiveGranuleJaxb(granule);
         obj.setOwner(this);
         result.add(obj);
      }

      return result;
   }

   public ArchiveGranule createGranule() {
      return new ArchiveGranuleJaxb();
   }
}
