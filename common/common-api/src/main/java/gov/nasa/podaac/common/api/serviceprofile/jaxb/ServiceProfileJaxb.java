/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile.jaxb;

import gov.nasa.podaac.common.api.jaxb.serviceprofile.AgentType;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.Message;
import gov.nasa.podaac.common.api.jaxb.serviceprofile.ProductType;
import gov.nasa.podaac.common.api.serviceprofile.Accessor;
import gov.nasa.podaac.common.api.serviceprofile.Agent;
import gov.nasa.podaac.common.api.serviceprofile.ProductProfile;
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfile;
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfileException;
import gov.nasa.podaac.common.api.util.Binder;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

public class ServiceProfileJaxb extends Binder<Message> implements Accessor,
      ServiceProfile {

   private static Log _logger = LogFactory.getLog(ServiceProfileJaxb.class);
   private static final String SCHEMA_ENV = "common.config.schema";
   private static final String SIP_SCHEMA = "podaac_message.xsd";
   private static final String JAXB_CONTEXT =
         "gov.nasa.podaac.common.api.jaxb.serviceprofile";
   private static URL _schemaUrl;

   static {
      if (System.getProperty(ServiceProfileJaxb.SCHEMA_ENV) != null) {
         File schemaFile =
               new File(System.getProperty(ServiceProfileJaxb.SCHEMA_ENV)
                     + File.separator + ServiceProfileJaxb.SIP_SCHEMA);
         if (schemaFile.exists()) {
            try {
               ServiceProfileJaxb._schemaUrl = schemaFile.toURL();
            } catch (MalformedURLException e) {
               if (ServiceProfileJaxb._logger.isDebugEnabled())
                  e.printStackTrace();
            }
         }
      }

      if (ServiceProfileJaxb._schemaUrl == null) {
         ServiceProfileJaxb._schemaUrl =
               ServiceProfileJaxb.class.getResource("/META-INF/schemas/"
                     + ServiceProfileJaxb.SIP_SCHEMA);
      }
   }

   public static synchronized ServiceProfile createServiceProfile()
         throws ServiceProfileException {
      ServiceProfileJaxb profile = null;

      try {
         profile = new ServiceProfileJaxb();
         profile._setJaxbObj(new Message());
      } catch (JAXBException e) {
         if (ServiceProfileJaxb._logger.isDebugEnabled())
            e.printStackTrace();
         throw new ServiceProfileException(e.getMessage());
      }
      return profile;
   }

   public static synchronized ServiceProfile loadServiceProfile(Reader reader)
         throws ServiceProfileException {
      ServiceProfileJaxb profile = null;
      try {
         profile =
               new ServiceProfileJaxb(ServiceProfileJaxb.JAXB_CONTEXT,
                     ServiceProfileJaxb._schemaUrl, reader);
      } catch (JAXBException e) {
         ServiceProfileJaxb._logger.error(e.getMessage());
         ServiceProfileJaxb._logger.debug(e.getMessage(), e);
         ServiceProfileJaxb._logger.error(e.getCause().getMessage());
         throw new ServiceProfileException(e.getMessage());
      } catch (SAXException e) {
         ServiceProfileJaxb._logger.error(e.getMessage());
         ServiceProfileJaxb._logger.debug(e.getMessage(), e);
         throw new ServiceProfileException(e.getMessage());
      } catch (ParserConfigurationException e) {
         ServiceProfileJaxb._logger.error(e.getMessage());
         ServiceProfileJaxb._logger.debug(e.getMessage(), e);
         throw new ServiceProfileException(e.getMessage());
      }
      return profile;
   }

   private Object _owner = null;

   protected ServiceProfileJaxb() throws JAXBException {
      super(ServiceProfileJaxb.JAXB_CONTEXT);
   }

   protected ServiceProfileJaxb(String contextpath, URL schemaUrl, Reader reader)
         throws JAXBException, SAXException, ParserConfigurationException {
      super(contextpath, schemaUrl, reader, true);
   }

   public Agent createAgent() {
      return new AgentJaxb();
   }

   public ProductProfile createProductProfile() {
      return new ProductProfileJaxb();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      final ServiceProfileJaxb other = (ServiceProfileJaxb) obj;
      if (_owner == null) {
         if (other._owner != null)
            return false;
      } else if (!_owner.equals(other._owner))
         return false;
      return true;
   }

   public Object getImplObj() {
      return this._getJaxbObj();
   }

   public Agent getMessageOriginAgent() {
      Agent result = null;
      if (this._getJaxbObj().getOrigin() != null) {
         result = new AgentJaxb(this._getJaxbObj().getOrigin());
         result.setOwner(this);
      }
      return result;
   }

   public Agent getMessageTargetAgent() {
      Agent result = null;
      if (this._getJaxbObj().getOrigin() != null) {
         result = new AgentJaxb(this._getJaxbObj().getTarget());
         result.setOwner(this);
      }
      return result;

   }

   public Object getOwner() {
      return this._owner;
   }

   public ProductProfile getProductProfile() {
      ProductProfile result = null;
      if (this._getJaxbObj().getProduct() != null) {
         result = new ProductProfileJaxb(this._getJaxbObj().getProduct());
         result.setOwner(this);
      }
      return result;
   }

   public boolean hasArchiveProfile() {
      return (this._getJaxbObj().getProduct() != null && this._getJaxbObj()
            .getProduct().getArchive() != null);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_owner == null) ? 0 : _owner.hashCode());
      return result;
   }

   public boolean hasIngestProfile() {
      return (this._getJaxbObj().getProduct() != null && this._getJaxbObj()
            .getProduct().getSubmission() != null);
   }

   public void setMessageOriginAgent(Agent agent) {
      if (agent == null)
         return;
      Agent temp = agent;
      if (!(temp.getImplObj() instanceof AgentType)) {
         temp = new AgentJaxb(agent);
         temp.setOwner(this);
      }
      this._getJaxbObj().setOrigin((AgentType) temp.getImplObj());
      agent = temp;
   }

   public void setMessageTargetAgent(Agent agent) {
      if (agent == null)
         return;
      Agent temp = agent;
      if (!(temp.getImplObj() instanceof AgentType)) {
         temp = new AgentJaxb(agent);
         temp.setOwner(this);
      }
      this._getJaxbObj().setTarget((AgentType) temp.getImplObj());
      agent = temp;
   }

   public void setOwner(Object owner) {
      this._owner = owner;
   }

   public void setProductProfile(ProductProfile profile) {
      if (profile == null)
         return;
      ProductProfile temp = profile;
      if (!(temp.getImplObj() instanceof ProductType)) {
         temp = new ProductProfileJaxb(profile);
         temp.setOwner(this);
      }
      this._getJaxbObj().setProduct((ProductType) temp.getImplObj());
      profile = temp;
   }

   public void toFile(String filename) throws ServiceProfileException,
         IOException {
      try {
         Binder.toFile(this._getJaxbObj(), this._getMarshaller(), filename);
      } catch (JAXBException e) {
         throw new ServiceProfileException(e);
      } catch (SAXException e) {
         throw new ServiceProfileException(e);
      }
   }

   @Override
   public String toString() {
      String result = null;
      try {
         result = Binder.toString(this._getJaxbObj(), this._getMarshaller());
      } catch (Exception e) {
         ServiceProfileJaxb._logger.error("Unable to transform object to XML.",
               e);
      }
      return result;
   }
}
