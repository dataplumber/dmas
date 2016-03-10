/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile;

import gov.nasa.podaac.common.api.serviceprofile.Common.DataFormat;
import gov.nasa.podaac.common.api.serviceprofile.Common.FileClass;

import java.util.Set;

public interface GranuleFile extends Accessor {

   void addSource(BasicFileInfo source);

   void clearSources();

   IngestDetails createIngestDetails();

   BasicFileInfo createSource();

   DataFormat getFormat();

   String getGroup();

   IngestDetails getIngestDetails();

   String getName();

   Set<BasicFileInfo> getSources();

   FileClass getType();

   void setFormat(DataFormat format);

   void setGroup(String group);

   void setIngestDetails(IngestDetails details);

   void setName(String name);

   void setType(FileClass type);

}
