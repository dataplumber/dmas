/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile.jaxb;

import gov.nasa.podaac.common.api.jaxb.serviceprofile.ArchiveType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.NotificationType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.ProductType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.SubmissionType;
import gov.nasa.podaac.common.api.serviceprofile.ArchiveProfile;
import gov.nasa.podaac.common.api.serviceprofile.IngestProfile;
import gov.nasa.podaac.common.api.serviceprofile.Notification;
import gov.nasa.podaac.common.api.serviceprofile.ProductProfile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductProfileJaxb extends AccessorBase implements ProductProfile {

   private ProductType _jaxbObj;

   public ProductProfileJaxb() {
      this._jaxbObj = new ProductType();
   }

   public ProductProfileJaxb(ProductProfile product) {
      this._jaxbObj = new ProductType();
      this.setIngestProfile(product.getIngestProfile());

      Set<Notification> notifications = product.getNotifications();
      for (Notification notification : notifications) {
         this.addNotification(notification);
      }

   }

   public ProductProfileJaxb(ProductType jaxbObj) {
      this._jaxbObj = jaxbObj;
   }

   public void addNotification(Notification notification) {
      Notification temp = notification;
      if (!(temp.getImplObj() instanceof NotificationType)) {
         temp = new NotificationJaxb(notification);
         temp.setOwner(this);
      }
      this._jaxbObj.getNotify().add((NotificationType) temp.getImplObj());
      notification = temp;
   }

   public void clearNotifications() {
      this._jaxbObj.getNotify().clear();
   }

   public ArchiveProfile createArchiveProfile() {
      return new ArchiveProfileJaxb();
   }

   public IngestProfile createIngestProfile() {
      return new IngestProfileJaxb();
   }

   public Notification createNotification() {
      return new NotificationJaxb();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      final ProductProfileJaxb other = (ProductProfileJaxb) obj;
      if (_jaxbObj == null) {
         if (other._jaxbObj != null)
            return false;
      } else if (!_jaxbObj.equals(other._jaxbObj))
         return false;
      return true;
   }

   public ArchiveProfile getArchiveProfile() {
      if (this._jaxbObj.getArchive() != null) {
         ArchiveProfile profile =
               new ArchiveProfileJaxb(this._jaxbObj.getArchive());
         profile.setOwner(this);
         return profile;
      }
      return null;
   }

   @Override
   public Object getImplObj() {
      return this._jaxbObj;
   }

   public IngestProfile getIngestProfile() {
      IngestProfile result = null;
      if (this._jaxbObj.getSubmission() != null) {
         result = new IngestProfileJaxb(this._jaxbObj.getSubmission());
         result.setOwner(this);
      }
      return result;
   }

   public Set<Notification> getNotifications() {
      Set<Notification> result =
            new HashSet<Notification>();
      List<NotificationType> notifications = this._jaxbObj.getNotify();
      for (NotificationType notification : notifications) {
         Notification n = new NotificationJaxb(notification);
         n.setOwner(this);
         result.add(n);
      }
      return result;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((_jaxbObj == null) ? 0 : _jaxbObj.hashCode());
      return result;
   }

   public void setArchiveProfile(ArchiveProfile archiveProfile) {
      ArchiveProfile temp = archiveProfile;
      if (!(temp.getImplObj() instanceof ArchiveType)) {
         temp = new ArchiveProfileJaxb(archiveProfile);
         temp.setOwner(this);
      }
      this._jaxbObj.setArchive((ArchiveType) temp.getImplObj());
      archiveProfile = temp;
   }

   public void setIngestProfile(IngestProfile ingestProfile) {
      if (ingestProfile == null)
         return;
      IngestProfile temp = ingestProfile;
      if (!(temp.getImplObj() instanceof SubmissionType)) {
         temp = new IngestProfileJaxb(ingestProfile);
         temp.setOwner(this);
      }
      this._jaxbObj.setSubmission((SubmissionType) temp.getImplObj());
      ingestProfile = temp;
   }

}
