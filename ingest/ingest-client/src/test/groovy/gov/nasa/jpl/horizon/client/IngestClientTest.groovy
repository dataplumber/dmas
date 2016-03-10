/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.jpl.horizon.client

/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id$
 */
public class IngestClientTest extends GroovyTestCase {
   private static final String[] ARGUMENTS = [
      "help"
   ] as String[]
   
   public void test() {
      System.setProperty("horizon.domain.file", "/Users/axt/Documents/Development/Eclipse/Ingest/ingest-client/src/main/resources/config/horizondomain.xml")
      println "Arguments: "+ARGUMENTS.join(" ")
      
      try {
         IngestClient ingestClient = new IngestClient()
         ingestClient.run(ARGUMENTS)
      } catch(exception) {
         exception.printStackTrace()
      }
   }
}
