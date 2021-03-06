/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api.serviceprofile;

import java.io.IOException;

/**
 * <p>
 * Interface to the service profile object instance. It is the data token that
 * is being populated by the ingest service and access by the Inventory service.
 * </p>
 * 
 * <p>
 * Note that all getters on ServiceProfile and its related classes referring SIP
 * returns null when the information for the call is not available. All setters
 * throws ServiceProfileException when it fails to set the given value.
 * </p>
 * 
 * <p>
 * Following code shows an example of how to use ServiceProfile:
 * </p>
 * <code>
 * ServiceProfileFactory spf = ServiceProfileFactory.getInstance();<br>
 * ServiceProfile sp = spf.createServiceProfileFromSip(new File("SIP.xml"));<br>
 * <br>
 * // get project name<br>
 * System.out.println("Project: "+sp.getProject());<br>
 * <br>
 * // go through packages<br>
 * for(IngestiblePackage ingestiblePackage : sp.getIngestiblePackages()) {<br>
 * &nbsp;&nbsp;&nbsp;// for through files in a package<br>
 * &nbsp;&nbsp;&nbsp;for(IngestibleFile ingestibleFile : ingestiblePackage.getIngestibleFiles()) {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("FileName: "+ingestibleFile.getFileName());<br>
 * <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Note that it can return null when metadata is not available<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// in this ingestible file.<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Metadata metadata = ingestibleFile.getMetadata();<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if(metadata != null) {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// print granule name<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("GranuleName: "+metadata.getGranuleName());<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br>
 * &nbsp;&nbsp;&nbsp;}<br>
 * }<br>
 * </code>
 * 
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: ServiceProfile.java 1234 2008-05-30 04:45:50Z thuang $
 */
public interface ServiceProfile extends Accessor {

   /**
    * Factory method to create an Agent object instance
    * 
    * @return an Agent object instance
    */
   Agent createAgent();

   /**
    * Factory method to create a ProductProfile object.
    * 
    * @return creates a new ProductProfile object
    */
   ProductProfile createProductProfile();

   Agent getMessageOriginAgent();

   Agent getMessageTargetAgent();

   /**
    * An accessor method to return the ProductProfile object, if the
    * ServiceProfile was created with product information. This maps to the
    * <productMsg> element of the service message.
    * 
    * @return a ProductProfile object reference or null.
    */
   ProductProfile getProductProfile();

   boolean hasArchiveProfile();

   /**
    * Method to return true if the ServiceProfile contains product ingestion
    * information.
    * 
    * @return true if ServiceProfile contains ingestion information
    */
   boolean hasIngestProfile();

   /**
    * Method to set the message origin Agent reference. If the input Agent is
    * not the same implementation as the current ServiceProfile object, this
    * method will convert it to the same implementation and override the input
    * 'agent' object reference.
    * 
    * @param agent the input Agent object reference
    */
   void setMessageOriginAgent(Agent agent);

   /**
    * Method to set the message target Agent reference. If the input Agent is
    * not the same implementation as the current ServiceProfile object, this
    * method will convert it to the same implementation and override the input
    * 'agent' object reference.
    * 
    * @param agent the input Agent object reference
    */
   void setMessageTargetAgent(Agent agent);

   /**
    * Method to set the ProductProfile object reference. This will also override
    * the internal <productMsg> object if there is already one.
    * 
    * @param profile the input ProductProfile.
    */
   void setProductProfile(ProductProfile profile);

   /**
    * Method to output the ServiceProfile into a file for long-term storage. The
    * output file is nicely formated with indentations.
    * 
    * @param filename the output file name
    * @throws ServiceProfileException when the current ServiceProfile fail to
    *            validate correctly. Possible missing some fields being set.
    * @throws IOException when failed to write to output file
    */
   void toFile(String filename) throws ServiceProfileException, IOException;
}
