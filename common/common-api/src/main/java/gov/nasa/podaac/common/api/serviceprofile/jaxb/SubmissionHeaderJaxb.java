/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile.jaxb;

import gov.nasa.podaac.common.api.jaxb.serviceprofile.MessageFrequencyType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.SubmissionHeaderType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.SubmitStatusValue;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.TimeStampType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.SubmissionHeaderType.Contributor;
import gov.nasa.podaac.common.api.serviceprofile.SubmissionHeader;
import gov.nasa.podaac.common.api.serviceprofile.Common.MessageLevel;
import gov.nasa.podaac.common.api.serviceprofile.Common.Status;

import java.math.BigInteger;
import java.util.Date;

public class SubmissionHeaderJaxb extends AccessorBase implements
      SubmissionHeader {

   public Date getInventoryStartTime() {
      if (this._jaxbObj.getInventoryCoverage() != null
            && this._jaxbObj.getInventoryCoverage().getStart() != null) {
         return new Date(this._jaxbObj.getInventoryCoverage().getStart()
               .longValue());
      }
      return null;
   }

   public Date getInventoryStopTime() {
      if (this._jaxbObj.getInventoryCoverage() != null
            && this._jaxbObj.getInventoryCoverage().getStart() != null) {
         return new Date(this._jaxbObj.getInventoryCoverage().getStop()
               .longValue());
      }
      return null;
   }

   public void setInventoryStartTime(Date startTime) {
      if (startTime != null)
         this._getInventoryTime().setStart(
               BigInteger.valueOf(startTime.getTime()));
   }

   public void setInventoryStartTime(long startTime) {
      this._getInventoryTime().setStart(BigInteger.valueOf(startTime));
   }

   public void setInventoryStopTime(Date stopTime) {
      if (stopTime != null)
         this._getInventoryTime().setStop(
               BigInteger.valueOf(stopTime.getTime()));
   }

   public void setInventoryStopTime(long stopTime) {
      this._getInventoryTime().setStop(BigInteger.valueOf(stopTime));
   }

   private SubmissionHeaderType _jaxbObj;

   public SubmissionHeaderJaxb() {
      this._jaxbObj = new SubmissionHeaderType();
   }

   public SubmissionHeaderJaxb(SubmissionHeader header) {
      this._jaxbObj = new SubmissionHeaderType();
      this.setBatch(header.getBatch());
      this.setComment(header.getComment());
      this.setContributorEmail(header.getContributorEmail());
      this.setContributorMessageLevel(header.getContributorMessageLevel());
      this.setProcessStartTime(header.getProcessStartTime());
      this.setProcessStopTime(header.getProcessStopTime());
      this.setRequested(header.getRequested());
      this.setSubmissionId(header.getSubmissionId());
   }

   public SubmissionHeaderJaxb(SubmissionHeaderType jaxbObj) {
      this._jaxbObj = jaxbObj;
   }

   protected Contributor _getContributor() {
      Contributor contributor = this._jaxbObj.getContributor();
      if (contributor == null) {
         contributor = new Contributor();
         this._jaxbObj.setContributor(contributor);
      }
      return contributor;
   }

   protected TimeStampType _getInventoryTime() {
      TimeStampType timeStamp = this._jaxbObj.getInventoryCoverage();
      if (timeStamp == null) {
         timeStamp = new TimeStampType();
         this._jaxbObj.setInventoryCoverage(timeStamp);
      }
      return timeStamp;
   }

   protected TimeStampType _getProcessTime() {
      TimeStampType timeStamp = this._jaxbObj.getTime();
      if (timeStamp == null) {
         timeStamp = new TimeStampType();
         this._jaxbObj.setTime(timeStamp);
      }
      return timeStamp;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      final SubmissionHeaderJaxb other = (SubmissionHeaderJaxb) obj;
      if (_jaxbObj == null) {
         if (other._jaxbObj != null)
            return false;
      } else if (!_jaxbObj.equals(other._jaxbObj))
         return false;
      return true;
   }

   public Date getAcquired() {
      if (this._jaxbObj.getAcquired() != null) {
         return new Date(this._jaxbObj.getAcquired().longValue());
      }
      return null;
   }

   public String getBatch() {
      return this._jaxbObj.getBatch();
   }

   public String getComment() {
      return this._jaxbObj.getComment();
   }

   public String getContributorEmail() {
      if (this._jaxbObj.getContributor() != null) {
         return this._jaxbObj.getContributor().getEmail();
      }
      return null;
   }

   public MessageLevel getContributorMessageLevel() {
      if (this._jaxbObj.getContributor() != null
            && this._jaxbObj.getContributor().getMessageLevel() != null) {
         return MessageLevel.valueOf(this._jaxbObj.getContributor()
               .getMessageLevel().value());
      }
      return null;
   }

   @Override
   public Object getImplObj() {
      return this._jaxbObj;
   }

   public Date getProcessStartTime() {
      if (this._jaxbObj.getTime() != null
            && this._jaxbObj.getTime().getStart() != null) {
         return new Date(this._jaxbObj.getTime().getStart().longValue());
      }
      return null;
   }

   public Date getProcessStopTime() {
      if (this._jaxbObj.getTime() != null
            && this._jaxbObj.getTime().getStop() != null) {
         return new Date(this._jaxbObj.getTime().getStop().longValue());
      }
      return null;
   }

   public String getProject() {
      return this._jaxbObj.getProject();
   }

   public Date getRequested() {
      if (this._jaxbObj.getRequested() != null) {
         return new Date(this._jaxbObj.getRequested().longValue());
      }
      return null;
   }

   public Status getStatus() {
      return Status.valueOf(this._jaxbObj.getStatus().value());
   }

   public String getSubmissionId() {
      return this._jaxbObj.getSubmissionId();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((_jaxbObj == null) ? 0 : _jaxbObj.hashCode());
      return result;
   }

   public void setAcquired(Date acquired) {
      if (acquired != null)
         this._jaxbObj.setAcquired(BigInteger.valueOf(acquired.getTime()));
   }

   public void setAcquired(long acquired) {
      this._jaxbObj.setAcquired(BigInteger.valueOf(acquired));
   }

   public void setBatch(String batch) {
      this._jaxbObj.setBatch(batch);
   }

   public void setComment(String comment) {
      this._jaxbObj.setComment(comment);
   }

   public synchronized void setContributorEmail(String email) {
      this._getContributor().setEmail(email);
   }

   public void setContributorMessageLevel(MessageLevel messageLevel) {
      this._getContributor().setMessageLevel(
            MessageFrequencyType.valueOf(messageLevel.toString()));
   }

   public void setProcessStartTime(Date startTime) {
      if (startTime != null)
         this._getProcessTime().setStart(
               BigInteger.valueOf(startTime.getTime()));
   }

   public void setProcessStartTime(long startTime) {
      this._getProcessTime().setStart(BigInteger.valueOf(startTime));
   }

   public void setProcessStopTime(Date endTime) {
      if (endTime != null)
         this._getProcessTime().setStop(BigInteger.valueOf(endTime.getTime()));
   }

   public void setProcessStopTime(long stopTime) {
      this._getProcessTime().setStop(BigInteger.valueOf(stopTime));
   }

   public void setProject(String project) {
      this._jaxbObj.setProject(project);
   }

   public void setRequested(Date requested) {
      if (requested != null)
         this._jaxbObj.setRequested(BigInteger.valueOf(requested.getTime()));
   }

   public void setRequested(long requested) {
      this._jaxbObj.setRequested(BigInteger.valueOf(requested));
   }

   public void setStatus(Status status) {
      this._jaxbObj.setStatus(SubmitStatusValue.valueOf(status.toString()));
   }

   public void setSubmissionId(String submissionId) {
      this._jaxbObj.setSubmissionId(submissionId);
   }

}
