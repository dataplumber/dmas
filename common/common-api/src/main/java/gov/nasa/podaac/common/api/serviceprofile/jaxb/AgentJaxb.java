/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.podaac.common.api.serviceprofile.jaxb;

import gov.nasa.podaac.common.api.jaxb.serviceprofile.AgentType;
import gov.nasa.podaac.common.api.serviceprofile.Agent;
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfileException;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * Implementation of Agent object using JAXB for XML marshalling and
 * unmarshalling.
 * 
 * @author T. Huang <mailto:thomas.huang@jpl.nasa.gov>Thomas.Huang@jpl.nasa.gov</mailto>
 * @version $Id: AgentJaxb.java 1234 2008-05-30 04:45:50Z thuang $
 */
public class AgentJaxb extends AccessorBase implements Agent {

   private AgentType _jaxbObj;

   public AgentJaxb() {
      this._jaxbObj = new AgentType();
   }

   public AgentJaxb(Agent agent) {
      this._jaxbObj = new AgentType();
      this._jaxbObj.setAgent(agent.getAgent());
      this._jaxbObj.setAddress(agent.getAddress().toString());
      this._jaxbObj.setTime(BigInteger.valueOf(agent.getTime().getTime()));
   }

   public AgentJaxb(AgentType jaxbObj) {
      this._jaxbObj = jaxbObj;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      final AgentJaxb other = (AgentJaxb) obj;
      if (_jaxbObj == null) {
         if (other._jaxbObj != null)
            return false;
      } else if (!_jaxbObj.equals(other._jaxbObj))
         return false;
      return true;
   }

   public synchronized InetAddress getAddress() {
      try {
         return InetAddress.getByName(this._jaxbObj.getAddress());
      } catch (UnknownHostException e) {
         return null;
      }
   }

   public synchronized String getAgent() {
      return this._jaxbObj.getAgent();
   }

   public synchronized Object getImplObj() {
      return this._jaxbObj;
   }

   public synchronized Date getTime() {
      Date date = null;
      if (this._jaxbObj.getTime() != null)
         date = new Date(this._jaxbObj.getTime().longValue());

      return date;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((_jaxbObj == null) ? 0 : _jaxbObj.hashCode());
      return result;
   }

   public synchronized void setAddress(InetAddress address)
         throws ServiceProfileException {
      this._jaxbObj.setAddress(address.getHostAddress());
   }

   public synchronized void setAgent(String name)
         throws ServiceProfileException {
      this._jaxbObj.setAgent(name);

   }

   public synchronized void setTime(Date timestamp)
         throws ServiceProfileException {
      this._jaxbObj.setTime(BigInteger.valueOf(timestamp.getTime()));
   }

   public synchronized void setTime(long timestamp)
         throws ServiceProfileException {
      if (timestamp < 0)
         throw new ServiceProfileException("Invalid time value.");

      this.setTime(new Date(timestamp));
   }
}
