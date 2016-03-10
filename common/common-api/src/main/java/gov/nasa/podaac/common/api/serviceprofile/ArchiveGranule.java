/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile;

import gov.nasa.podaac.common.api.serviceprofile.Common.AccessRole;

import java.util.List;
import java.util.Date;


/**
 * Definition of Archive Granule object
 *
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id:$
 */
public interface ArchiveGranule extends Accessor {

   AccessRole getAccessConstraint();

   String getName();

   void setName(String name);

   String getDatasetName();

   void setDatasetName(String datasetName);

   String getProductType();

   void setProductType(String productType);

   Long getGranuleId();

   void setGranuleId(long granuleId);

   List<ArchiveFileInfo> getFiles();

   ArchiveFileInfo createFile();

   void addFile(ArchiveFileInfo archiveFileInfo);

   void clearFiles();

   String getDeleteName();

   void setDeleteName(String name);

   List<String> getDeletes();

   void addDeleteUri(String uri);

   void clearDeletes();

   boolean isArchiveSuccess();

   void setAccessConstraint(AccessRole accessRole);

   void setArchiveSuccess(boolean success);

   String getArchiveNote();

   void setArchiveNote(String note);

   Date getArchiveStartTime();

   Date getArchiveStopTime();

   void setArchiveStartTime(Date startTime);

   void setArchiveStartTime(long startTime);

   void setArchiveStopTime(Date stopTime);

   void setArchiveStopTime(long stopTime);

}
