/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile;

import gov.nasa.podaac.common.api.serviceprofile.jaxb.ServiceProfileJaxb;
import gov.nasa.podaac.common.api.transformer.Transformer;
import gov.nasa.podaac.common.api.transformer.mmr.MMRException;
import gov.nasa.podaac.common.api.transformer.mmr.MMRTransformer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id: ServiceProfileFactory.java 2668 2009-02-18 00:33:44Z axt $
 *
 */
public class ServiceProfileFactory {
   public static Log _logger = LogFactory.getLog(ServiceProfileFactory.class);

   private static ServiceProfileFactory _instance = new ServiceProfileFactory();

   /**
    * Gets an instance of ServiceProfileFactory object.
    *
    * @return ServiceProfileFactory object.
    */
   public static ServiceProfileFactory getInstance() {
      return ServiceProfileFactory._instance;
   }

   protected ServiceProfileFactory() {
   }

   protected <T extends Reader> ServiceProfile _load(T reader)
         throws ServiceProfileException {
      return ServiceProfileJaxb.loadServiceProfile(reader);
   }

   /**
    * Creates an empty ServiceProfile object to be filled by user application.
    *
    * @return a ServiceProfile object
    */
   public ServiceProfile createServiceProfile() throws ServiceProfileException {
      return ServiceProfileJaxb.createServiceProfile();
   }

   /**
    * Creates ServiceProfile object from package.
    *
    * @param transformer PackageReader object to be used.
    * @param file File object representing the package.
    * @return ServiceProfile object on success.
    * @throws IOException If there is an error when reading from Reader object.
    * @throws ServiceProfileException If there is an error when creating
    *            ServiceProfile object.
    * @see #createServiceProfile(Transformer, String)
    */
   public ServiceProfile createServiceProfile(Transformer transformer, File file)
         throws IOException, ServiceProfileException {
      ServiceProfile serviceProfile = transformer.transform(file.toURI());
      return serviceProfile;
   }

   /**
    * Overloaded.
    *
    * @param transformer PackageReader object to be used.
    * @param contents Content of the package.
    * @return ServiceProfile object on success.
    * @throws IOException If there is an error when reading from Reader object.
    * @throws ServiceProfileException If there is an error when creating
    *            ServiceProfile object.
    * @see #createServiceProfile(Transformer, File)
    */
   public ServiceProfile createServiceProfile(Transformer transformer,
         String contents) throws ServiceProfileException {

      return transformer.transform(contents);
   }

   /**
    * Creates ServiceProfile object from new XML message.
    *
    * @param file File object representing XML message.
    * @return ServiceProfile object on success.
    * @throws IOExceotion If there is an error when reading from Reader object.
    * @throws ServiceProfileException If there is an error when creating
    *            ServiceProfile object.
    * @see #createServiceProfileFromMessage(String)
    */
   public ServiceProfile createServiceProfileFromMessage(File file)
         throws IOException, ServiceProfileException {
      ServiceProfileFactory._logger.trace("load xml file: " + file.getName());
      FileReader reader = new FileReader(file);
      ServiceProfile result = null;
      try {
         result = this._load(reader);
      } finally {
         reader.close();
      }
      return result;
   }

   /**
    * Creates ServiceProfile object from new XML message.
    *
    * @param contents XML document.
    * @return ServiceProfile object on success.
    * @throws ServiceProfileException If there is an error when creating
    *            ServiceProfile object.
    * @see #createServiceProfileFromMessage(File)
    */
   public ServiceProfile createServiceProfileFromMessage(String contents)
         throws ServiceProfileException {
      ServiceProfile result = null;
      StringReader reader = new StringReader(contents);
      try {
         result = this._load(reader);
      } finally {
         reader.close();
      }
      return result;
   }

   public ServiceProfile createServiceProfileFromMMR(String rootPath,
         String emailAddress, String searchPath, File file) throws IOException,
         ServiceProfileException {
      ServiceProfile profile = null;
      try {
         MMRTransformer transformer =
               new MMRTransformer(emailAddress, rootPath);
         if (searchPath != null)
            transformer.setSearchPath(searchPath);
         profile = this.createServiceProfile(transformer, file);
      } catch (MMRException e) {
         if (ServiceProfileFactory._logger.isDebugEnabled())
            e.printStackTrace();
         throw new ServiceProfileException(e.getMessage());

      }
      return profile;
   }

   public ServiceProfile createServiceProfileFromMMR(String rootPath,
         String emailAddress, String searchPath, String contents)
         throws IOException, ServiceProfileException {
      ServiceProfile profile = null;
      try {
         MMRTransformer transformer =
               new MMRTransformer(emailAddress, rootPath);
         if (searchPath != null)
            transformer.setSearchPath(searchPath);
         profile = this.createServiceProfile(transformer, contents);
      } catch (MMRException e) {
         if (ServiceProfileFactory._logger.isDebugEnabled())
            e.printStackTrace();
         throw new ServiceProfileException(e.getMessage());

      }
      return profile;
   }

   /**
    * Factory method to create a ServiceProfile object from the input Submission
    * Package Message (SPM) file. This method will translate the input SPM text
    * into the standard Submission Information Package in XML, performs
    * validation, decodes the XML message into a ServiceProfile JavaBean, and
    * return it to the caller.
    *
    * @param file the file that contains the SPM
    * @return the ServiceProfile object
    * @throws IOException when file processing failure
    * @throws ServiceProfileException when fail to validate the input SPM
    */
   /*
   public ServiceProfile createServiceProfileFromSPM(File file)
         throws IOException, ServiceProfileException {
      ServiceProfile profile = null;
      profile = this.createServiceProfile(new SPMTransformer(), file);
      return profile;
   }
   */

   /**
    * Factory method to create a ServiceProfile object from the input Submission
    * Package Message (SPM). This method will translate the input SPM text into
    * the standard Submission Information Package in XML, performs validation,
    * decodes the XML message into a ServiceProfile JavaBean, and return it to
    * the caller.
    *
    * @param spmString the SPM string
    * @return the ServiceProfile object
    * @throws ServiceProfileException when fail to validate the input SPM
    */
   /*
   public ServiceProfile createServiceProfileFromSPM(String contents)
         throws ServiceProfileException {

      return this.createServiceProfile(new SPMTransformer(), contents);
   }
   */
   
   public List<ServiceProfile> splitGranules(
      ServiceProfile serviceProfile
      ) throws ServiceProfileException {
      LinkedList<ServiceProfile> serviceProfiles = new LinkedList<ServiceProfile>();
      
      try {
         ServiceProfile copiedServiceProfile =
            this.createServiceProfileFromMessage(serviceProfile.toString());
         
         IngestProfile ingestProfile =
            copiedServiceProfile.getProductProfile().getIngestProfile();
         
         CompleteContent completeContent = ingestProfile.getCompleteContent();
         Set<Granule> granules = completeContent.getGranules();
         
         for(Granule granule : granules) {
            granule.clearLinks();
            
            completeContent.clearGranules();
            completeContent.addGranule(granule);
            serviceProfiles.add(
               this.createServiceProfileFromMessage(copiedServiceProfile.toString())
            );
         }
      } catch(ServiceProfileException spe) {
         throw spe;
      }
      
      return serviceProfiles;
   }
}
