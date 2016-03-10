/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile;

import gov.nasa.podaac.common.api.transformer.Transformer;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;

/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id: ServiceProfileWriter.java 1234 2008-05-30 04:45:50Z thuang $
 * 
 */
public class ServiceProfileWriter {
   static Log _logger = LogFactory.getLog(ServiceProfileWriter.class);

   /**
    * Creates an instance.
    * 
    */
   public ServiceProfileWriter() {
   }

   /**
    * Reads given package and writes into SIP.
    * 
    * @param transformer PackageReader to process the data from inputSource.
    * @param inputStream Input for data representing the package.
    * @param streamResult Output for resulting SIP.
    * @throws IOException If there is an error related to IO operations.
    * @throws ServiceProfileException If there is an error related to SIP.
    */

   public void write(Transformer transformer, File file,
         StreamResult streamResult) throws IOException, ServiceProfileException {
      ServiceProfile serviceProfile = transformer.transform(file.toURI());

      Writer writer = streamResult.getWriter();
      writer.write(serviceProfile.toString());
      writer.flush();
   }

   /**
    * Overloaded.
    * 
    * @param transformer PackageReader to process the data from inputSource.
    * @param inputStream Input for data representing the package.
    * @param writer Output for resulting SIP.
    * @throws IOException If there is an error related to IO operations.
    * @throws ServiceProfileException If there is an error related to SIP.
    * @see #write(Transformer, InputSource, StreamResult, File)
    */

   public void write(Transformer transformer, File file, Writer writer)
         throws IOException, ServiceProfileException {
      this.write(transformer, file, new StreamResult(writer));
   }

}
