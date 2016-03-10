/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile;

import gov.nasa.podaac.common.api.serviceprofile.Common.MessageLevel;

/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id: Notification.java 1234 2008-05-30 04:45:50Z thuang $
 */
public interface Notification extends Accessor {

   String getAddress();

   String getEmail();

   String getFax();

   String getFirstName();

   String getLastName();

   MessageLevel getMessageLevel();

   String getMiddleName();

   String getPhone();

   String getRole();

   void setAddress(String address);

   void setEmail(String email);

   void setFax(String fax);

   void setFirstName(String firstName);

   void setLastName(String lastName);

   void setMessageLevel(MessageLevel messageLevel);

   void setMiddleName(String middleName);

   void setPhone(String phone);

   void setRole(String role);
}
