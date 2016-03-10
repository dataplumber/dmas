/*****************************************************************************
 * Copyright (c) 2007-2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.transformer;

import gov.nasa.podaac.common.api.serviceprofile.ServiceProfile;
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfileException;

import java.io.IOException;
import java.net.URI;

/**
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: $
 * 
 */
public interface Transformer {

   ServiceProfile transform(String contents) throws ServiceProfileException;

   ServiceProfile transform(URI uri) throws IOException,
         ServiceProfileException;
}
