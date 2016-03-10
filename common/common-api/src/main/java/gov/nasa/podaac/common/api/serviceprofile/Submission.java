/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile;

public interface Submission extends Accessor {

   CompleteContent createCompleteContent();

   SubmissionHeader createHeader();

   CompleteContent getCompleteContent();

   SubmissionHeader getHeader();

   void setCompleteContent(CompleteContent content);

   void setHeader(SubmissionHeader header);
}
