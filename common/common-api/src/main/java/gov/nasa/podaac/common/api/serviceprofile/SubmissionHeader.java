/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile;

import java.util.Date;

import gov.nasa.podaac.common.api.serviceprofile.Common.MessageLevel;
import gov.nasa.podaac.common.api.serviceprofile.Common.Status;

public interface SubmissionHeader extends Accessor {

	Date getAcquired();
	
   String getBatch();

   String getComment();

   String getContributorEmail();

   MessageLevel getContributorMessageLevel();

   Date getInventoryStartTime();

   Date getInventoryStopTime();

   Date getProcessStartTime();

   Date getProcessStopTime();

   String getProject();

   Date getRequested();

   Status getStatus();

   String getSubmissionId();

	void setAcquired(Date acquired);
	
	void setAcquired(long acquired);

   void setBatch(String batch);

   void setComment(String comment);

   void setContributorEmail(String email);

   void setContributorMessageLevel(MessageLevel messageLevel);

   void setInventoryStartTime(Date startTime);

   void setInventoryStartTime(long startTime);

   void setInventoryStopTime(Date stopTime);

   void setInventoryStopTime(long stopTime);

   void setProcessStartTime(Date startTime);

   void setProcessStartTime(long startTime);

   void setProcessStopTime(Date stopTime);

   void setProcessStopTime(long stopTime);

   void setProject(String project);

   void setRequested(Date requested);

   void setRequested(long requested);

   void setStatus(Status status);

   void setSubmissionId(String submissionId);
}
