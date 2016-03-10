/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.podaac.common.api;

import gov.nasa.podaac.common.api.serviceprofile.ServiceProfile;
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfileFactory;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id$
 */
public class ServiceProfileSplitTest {
   private static final String SIP_PATH = "Original.sip";
   private String _rootPath = "";
   
   @Before
   public void setUp() {
      if (System.getProperty("common.test.path") != null)
         this._rootPath =
            System.getProperty("common.test.path") + File.separator;
   }
   
   @After
   public void cleanUp() {
   }
   
   @Test
   public void splitGranules() {
      try {
         ServiceProfileFactory spf = ServiceProfileFactory.getInstance();
         System.out.println("Path: "+this._rootPath+SIP_PATH);
         ServiceProfile serviceProfile = spf.createServiceProfileFromMessage(
            new File(this._rootPath+SIP_PATH)
         );
         List<ServiceProfile> serviceProfiles = spf.splitGranules(serviceProfile);
         System.out.println("Service Profiles: "+serviceProfiles.size());
         
         /*
         for(int i = 0; i < serviceProfiles.size(); i++) {
            serviceProfiles.get(i).toFile("/Users/axt/Desktop/PodaacTest/"+i+".sip");
         }
         */
      } catch(Exception exception) {
         System.out.println(exception.toString()+": "+exception.getMessage());
         exception.printStackTrace();
         Assert.fail();
      }
   }
}
