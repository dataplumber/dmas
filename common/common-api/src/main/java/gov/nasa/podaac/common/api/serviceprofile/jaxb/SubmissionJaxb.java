/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile.jaxb;

import gov.nasa.podaac.common.api.jaxb.serviceprofile.CompleteSubmissionType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.SubmissionHeaderType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.SubmissionType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.SubmissionType.Content;
import gov.nasa.podaac.common.api.serviceprofile.CompleteContent;
import gov.nasa.podaac.common.api.serviceprofile.Submission;
import gov.nasa.podaac.common.api.serviceprofile.SubmissionHeader;

public class SubmissionJaxb extends AccessorBase implements Submission {

   private SubmissionType _jaxbObj;

   public SubmissionJaxb() {
      this._jaxbObj = new SubmissionType();
   }

   public SubmissionJaxb(Submission submission) {
      this._jaxbObj = new SubmissionType();
      this.setCompleteContent(submission.getCompleteContent());
      this.setHeader(submission.getHeader());
   }

   public SubmissionJaxb(SubmissionType jaxb) {
      this._jaxbObj = jaxb;
   }

   protected Content _getContent() {
      if (this._jaxbObj.getContent() == null) {
         this._jaxbObj.setContent(new Content());
      }
      return this._jaxbObj.getContent();
   }

   public CompleteContent createCompleteContent() {
      CompleteContent result = new CompleteContentJaxb();
      return result;
   }

   public SubmissionHeader createHeader() {
      SubmissionHeader header = new SubmissionHeaderJaxb();
      return header;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      final SubmissionJaxb other = (SubmissionJaxb) obj;
      if (_jaxbObj == null) {
         if (other._jaxbObj != null)
            return false;
      } else if (!_jaxbObj.equals(other._jaxbObj))
         return false;
      return true;
   }

   public CompleteContent getCompleteContent() {
      if (this._jaxbObj.getContent() != null
         && this._jaxbObj.getContent().getComplete() != null) {
         CompleteContent result =
            new CompleteContentJaxb(this._jaxbObj.getContent().getComplete());
         result.setOwner(this);
         return result;
      }
      return null;
   }

   public SubmissionHeader getHeader() {
      if (this._jaxbObj.getHeader() != null) {
         SubmissionHeader result =
            new SubmissionHeaderJaxb(this._jaxbObj.getHeader());
         result.setOwner(this);
         return result;
      }
      return null;
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

   public void setCompleteContent(CompleteContent content) {
      if (content == null)
         return;
      CompleteContent temp = content;
      if (!(temp.getImplObj() instanceof CompleteSubmissionType)) {
         temp = new CompleteContentJaxb(content);
         temp.setOwner(this);
      }
      this._getContent()
         .setComplete((CompleteSubmissionType) temp.getImplObj());
      content = temp;
   }

   public void setHeader(SubmissionHeader header) {
      if (header == null)
         return;
      SubmissionHeader temp = header;
      if (!(temp.getImplObj() instanceof SubmissionHeaderType)) {
         temp = new SubmissionHeaderJaxb(header);
         temp.setOwner(this);
      }
      this._jaxbObj.setHeader((SubmissionHeaderType) temp.getImplObj());
      header = temp;
   }

}
