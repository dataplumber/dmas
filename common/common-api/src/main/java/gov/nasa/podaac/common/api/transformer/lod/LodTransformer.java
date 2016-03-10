/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.podaac.common.api.transformer.lod;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;

import gov.nasa.podaac.common.api.serviceprofile.BasicFileInfo;
import gov.nasa.podaac.common.api.serviceprofile.Common;
import gov.nasa.podaac.common.api.serviceprofile.CompleteContent;
import gov.nasa.podaac.common.api.serviceprofile.Granule;
import gov.nasa.podaac.common.api.serviceprofile.GranuleFile;
import gov.nasa.podaac.common.api.serviceprofile.IngestProfile;
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfile;
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfileException;
import gov.nasa.podaac.common.api.transformer.Transformer;

/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id$
 */
public abstract class LodTransformer implements Transformer {
   protected ServiceProfile _serviceProfile;
   protected String _granuleFileName;
   protected HashMap<String,String> m_problemGranulesList = new HashMap<String,String>();
   
   public LodTransformer(ServiceProfile serviceProfile) {
      _serviceProfile = serviceProfile;
   }
   
   public LodTransformer() {
   }

   public  HashMap<String,String> getProblemGranulesList() {
       return m_problemGranulesList;
   }
   
   public void setServiceProfile(ServiceProfile serviceProfile) {
      _serviceProfile = serviceProfile;
   }
   
   public ServiceProfile getServiceProfile() {
      return _serviceProfile;
   }
   
   /*
    * Transforms QuikSCAT LOD file. Note that #setGranuleFileName method needs
    * to be called with a filename of this LOD file prior to call this method.
    *
    * @param contents Content of LOD file.
    *
    * @see gov.nasa.podaac.common.api.transformer.Transformer#transform(java.net.URI)
    */
   public ServiceProfile transform(String contents)
         throws ServiceProfileException {
      ServiceProfile serviceProfile = null;

      StringReader stringReader = new StringReader(contents);
      try {
         serviceProfile = this._transform(stringReader, null, contents.length());
      } catch (ServiceProfileException exception) {
         throw exception;
      } finally {
         stringReader.close();
      }

      return serviceProfile;
   }

   public ServiceProfile transform(URI uri) throws IOException,
         ServiceProfileException {
      ServiceProfile profile = null;

      InputStream inputStream = null;
      InputStreamReader isr = null;
      FileObject fileObject = null;
      FileContent fileContent = null;
      try {
         FileSystemManager fsm = VFS.getManager();
         fileObject = fsm.resolveFile(uri.toString());
         fileContent = fileObject.getContent();
         long fileSize = fileContent.getSize();
         inputStream = fileContent.getInputStream();
         isr = new InputStreamReader(inputStream);
         profile = this._transform(isr, uri.toURL(), fileSize);
      } catch (IOException exception) {
         exception.printStackTrace();
         throw exception;
      } catch (ServiceProfileException exception) {
         throw exception;
      } finally {
         try {
            if (isr != null) {
               isr.close();
            }
            if (inputStream != null) {
               inputStream.close();
            }
            if (fileContent != null) {
               fileContent.close();
            }
            if (fileObject != null) {
               fileObject.close();
            }
         } catch (Exception exception) {
         }
      }

      return profile;
   }

   public String getGranuleFileName() {
      return _granuleFileName;
   }

   public boolean setGranuleFileName(String granuleFileName) {
      _granuleFileName = granuleFileName;
      return true;
   }
   
   public abstract void completeTransform();
   
   protected abstract ServiceProfile _transform(Reader reader, URL url, long fileSize)
      throws ServiceProfileException;
      
   protected Granule _getGranule(String granuleName) {
      IngestProfile ingestProfile =
            _serviceProfile.getProductProfile().getIngestProfile();
      CompleteContent completeContent = ingestProfile.getCompleteContent();
      if (completeContent == null) {
         completeContent = ingestProfile.createCompleteContent();
         ingestProfile.setCompleteContent(completeContent);
      }

      Granule granule = completeContent.getGranule(granuleName);

      return granule;
   }
   
   protected GranuleFile _getGranuleFile(Granule granule, String granuleFileName) {
      GranuleFile granuleFile = null;
      for(GranuleFile element : granule.getFiles()) {
         if(element.getName().equals(granuleFileName)) {
            granuleFile = element;
            break;
         }
      }
      
      return granuleFile;
   }
   
   protected LinkedList<String> _readHeader(BufferedReader bufferedReader)
      throws Exception {
      LinkedList<String> header = new LinkedList<String>();
      
      String line = null;
      while ((line = bufferedReader.readLine()) != null) {
         header.add(line);
         if (line.startsWith("End_Group")) {
            break;
         }
      }
      
      return header;
   }
   
   protected abstract LodPopulator _getLodPopulator(LinkedList<String> header);
}
