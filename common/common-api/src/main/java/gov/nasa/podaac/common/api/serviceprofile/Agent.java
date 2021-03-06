/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile;

import java.net.InetAddress;
import java.util.Date;

/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id: Agent.java 1234 2008-05-30 04:45:50Z thuang $
 */
public interface Agent extends Accessor {
   InetAddress getAddress();

   String getAgent();

   Date getTime();

   void setAddress(InetAddress address) throws ServiceProfileException;

   void setAgent(String name) throws ServiceProfileException;

   void setTime(Date timestamp) throws ServiceProfileException;

   void setTime(long timestamp) throws ServiceProfileException;
}
