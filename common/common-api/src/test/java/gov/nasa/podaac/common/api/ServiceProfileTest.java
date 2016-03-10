/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.api;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import gov.nasa.podaac.common.api.serviceprofile.Agent;
import gov.nasa.podaac.common.api.serviceprofile.BasicFileInfo;
import gov.nasa.podaac.common.api.serviceprofile.BoundingRectangle;
import gov.nasa.podaac.common.api.serviceprofile.CompleteContent;
import gov.nasa.podaac.common.api.serviceprofile.Granule;
import gov.nasa.podaac.common.api.serviceprofile.GranuleFile;
import gov.nasa.podaac.common.api.serviceprofile.IngestDetails;
import gov.nasa.podaac.common.api.serviceprofile.IngestProfile;
import gov.nasa.podaac.common.api.serviceprofile.Notification;
import gov.nasa.podaac.common.api.serviceprofile.ProductProfile;
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfile;
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfileException;
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfileFactory;
import gov.nasa.podaac.common.api.serviceprofile.SubmissionHeader;
import gov.nasa.podaac.common.api.serviceprofile.Common.ChecksumAlgorithm;
import gov.nasa.podaac.common.api.serviceprofile.Common.CompressionAlgorithm;
import gov.nasa.podaac.common.api.serviceprofile.Common.DataFormat;
import gov.nasa.podaac.common.api.serviceprofile.Common.MessageLevel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Date;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * A collection of test for ServiceProfile creation and manipulation.
 * 
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $$Id: ServiceProfileTest.java 644 2008-02-27 19:05:04Z thuang $$
 */
public class ServiceProfileTest {
   private static final String _SIP_FILE_NAME = "podaac-ingest-example01.xml";
   private static final String _SPM_FILE_NAME = "spa.0.testbed";

   private String _rootPath = "";

   private String _fileToString(String filename) throws IOException {
      FileInputStream input = null;
      try {
         input = new FileInputStream(new File(filename));
         FileChannel channel = input.getChannel();
         MappedByteBuffer buffer =
               channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
         Charset charset = Charset.forName("ISO-8859-1");
         CharsetDecoder decoder = charset.newDecoder();
         CharBuffer charBuffer = decoder.decode(buffer);
         return charBuffer.toString();
      } finally {
         if (input != null)
            input.close();
      }
   }

   @Before
   public void setup() {
      if (System.getProperty("common.test.path") != null)
         this._rootPath =
               System.getProperty("common.test.path") + File.separator;
   }

   /**
    * Test for updating communication agent information. This test uses GHRSST
    * MODIS SIP as input.
    */
   @Test
   public void testAgentUpdate() {
      try {

         ServiceProfileFactory spf = ServiceProfileFactory.getInstance();
         ServiceProfile serviceProfile = null;

         try {
            serviceProfile =
                  spf.createServiceProfileFromMessage(new File(this._rootPath
                        + ServiceProfileTest._SIP_FILE_NAME));

            assertNotNull(serviceProfile);
         } catch (Exception exception) {
            exception.printStackTrace();
            fail("Failed to create ServiceProfile object.");
         }

         ProductProfile product = serviceProfile.getProductProfile();
         assertNotNull(product);
         IngestProfile ingest = product.getIngestProfile();
         assertNotNull(ingest);

         SubmissionHeader header = ingest.getHeader();

         System.out.println("requestedTime: "
               + header.getRequested().toString());
         header.setRequested(new Date());

         Set<Notification> notifications = product.getNotifications();

         for (Notification notification : notifications) {
            System.out.println(notification);
         }

         System.out.println("\n");

         // add another notification
         Notification nt = product.createNotification();
         nt.setLastName("Smith");
         nt.setFirstName("John");
         nt.setEmail("donotreply@jpl.nasa.gov");
         nt.setMessageLevel(MessageLevel.VERBOSE);

         product.addNotification(nt);

         notifications = product.getNotifications();
         for (Notification notification : notifications) {
            System.out.println(notification + "\n");
         }

         // System.out.println(serviceProfile.toXML());
      } catch (Exception exception) {
         exception.printStackTrace();
         Assert.fail();
      }
   }

   /**
    * Testing the new ServiceProfile API to create a ServiceProfile using simple
    * API calls without any input data files.
    */
   @Test
   public void testBootstrapServiceProfile() {
      try {
         ServiceProfile sp =
               ServiceProfileFactory.getInstance().createServiceProfile();
         assertNotNull(sp);

         sp.setProductProfile(sp.createProductProfile());

         Agent origin = sp.createAgent();
         origin.setAgent("qchau@sipsubmit");
         origin.setAddress(InetAddress.getByName("127.0.0.1"));
         origin.setTime(1192147092433L);
         sp.setMessageOriginAgent(origin);

         Agent target = sp.createAgent();
         target.setAgent("QSCAT:QSCAT_2");
         target.setAddress(InetAddress.getByName("127.0.0.1"));
         target.setTime(1192147092455L);
         sp.setMessageTargetAgent(target);

         ProductProfile pp = sp.getProductProfile();
         IngestProfile ip = sp.getProductProfile().createIngestProfile();
         assertNotNull(ip);
         pp.setIngestProfile(ip);

         SubmissionHeader header = ip.createHeader();
         ip.setHeader(header);

         CompleteContent content = ip.createCompleteContent();
         ip.setCompleteContent(content);

         header.setProject("JPL-L2P-MODIS_A");
         header.setBatch("1192145906-4");
         header.setContributorEmail("Thomas.Huang@jpl.nasa.gov");
         header.setContributorMessageLevel(MessageLevel.VERBOSE);
         header.setRequested(1192147092397L);
         header.setProcessStartTime(1192147092631L);
         header.setProcessStopTime(1192147095460L);
         header.setSubmissionId("2007_10_11_0003");
         header
               .setComment("Quicklook MODIS L2P created at the GHRSST Global Data Assembly Center (GDAC), Wed Aug 1 12:15:39 2007");

         Notification notification = pp.createNotification();
         notification.setLastName("Huang");
         notification.setFirstName("Thomas");
         notification.setEmail("Thomas.Huang@jpl.nasa.gov");
         pp.addNotification(notification);

         Granule granule = content.createGranule();
         content.addGranule(granule);

         granule
               .setName("20070801-MODIS_A-JPL-L2P-A2007213074500.L2_LAC_GHRSST-v01");
         granule.setDatasetName("JPL-L2P-MODIS_A");
         granule.setId(1L);
         granule.setTemporalCoverageStartTime(1185954308000L);
         granule.setTemporalCoverageStopTime(1185954606000L);

         BoundingRectangle rectangle = granule.createBoundingRectangle();
         rectangle.setWestLongitude(76.309);
         rectangle.setNorthLatitude(-1.600);
         rectangle.setEastLongitude(101.738);
         rectangle.setSouthLatitude(-22.541);
         granule.setBoundingRectangle(rectangle);

         granule.setCreateTime(1185970528000L);
         granule.setVersion("0");

         GranuleFile file = granule.createFile();
         granule.addFile(file);

         BasicFileInfo source = file.createSource();
         file.addSource(source);
         file.setFormat(DataFormat.NETCDF);
         source
               .setLink(new URI(
                     "ftp://seastorm.jpl.nasa.gov/home/qchau/mmr_drop/MODIS_A/2007/213/20070801-MODIS_A-JPL-L2P-A2007213074500.L2_LAC_GHRSST-v01.nc.bz2"));
         source.setCompressionAlgorithm(CompressionAlgorithm.BZIP2);
         source.setChecksum("7f87aa1f9f29662f992b657f5a54e1");
         source.setChecksumAlgorithm(ChecksumAlgorithm.MD5);
         System.out.println(source.getChecksumAlgorithm());

         IngestDetails details = file.createIngestDetails();
         file.setIngestDetails(details);
         details.setSource(source);
         details
               .setLocalStaging(new URI(
                     "file:///data/dev/users/thuang/dev/horizon_lapinta/staging/2007/10/11/213/20070801-MODIS_A-JPL-L2P-A2007213074500.L2_LAC_GHRSST-v01.nc.bz2"));
         details.setIngestStartTime(1192147092631L);
         details.setIngestStopTime(1192147095458L);
         details.setSize(24165232L);
      } catch (ServiceProfileException e) {
         e.printStackTrace();
      } catch (URISyntaxException e) {
         e.printStackTrace();
      } catch (UnknownHostException e) {
         e.printStackTrace();
      }
   }

   /**
    * Test to quickly verify creation of a ServiceProfile object from an input
    * SIP file.
    */
   @Test
   public void testSIPFileTranslation() {
      try {
         ServiceProfile serviceProfile =
               ServiceProfileFactory.getInstance()
                     .createServiceProfileFromMessage(
                           new File(this._rootPath
                                 + ServiceProfileTest._SIP_FILE_NAME));
         assertNotNull(serviceProfile);
      } catch (ServiceProfileException e) {
         fail(e.getMessage());
      } catch (IOException e) {
         fail(e.getMessage());
      }
   }

   /**
    * Test to quickly verify creation of a ServiceProfile object from an input
    * text string.
    */
   @Test
   public void testSIPStringTranslation() {
      try {
         String buffer =
               this._fileToString(this._rootPath
                     + ServiceProfileTest._SIP_FILE_NAME);
         assertNotNull(buffer);
         ServiceProfile serviceProfile =
               ServiceProfileFactory.getInstance()
                     .createServiceProfileFromMessage(buffer);
         assertNotNull(serviceProfile);
      } catch (ServiceProfileException e) {
         fail(e.getMessage());
      } catch (IOException e) {
         fail(e.getMessage());
      }
   }

   /**
    * Test to quickly verify translation from an SPM file to a ServiceProfile
    * and to a SIP.
    */
   /*
   @Test
   public void testSPMFileTranslation() {
      try {
         ServiceProfile serviceProfile =
               ServiceProfileFactory.getInstance().createServiceProfileFromSPM(
                     new File(this._rootPath
                           + ServiceProfileTest._SPM_FILE_NAME));
         assertNotNull(serviceProfile);

         System.out.println(serviceProfile);
      } catch (ServiceProfileException e) {
         // e.printStackTrace();
         fail(e.getMessage());
      } catch (IOException e) {
         // e.printStackTrace();
         fail(e.getMessage());
      }
   }
   */

   /**
    * Test to quickly verify translation from an SPM text string to a
    * ServiceProfile and to a SIP.
    */
   /*
   @Test
   public void testSPMStringTranslation() {
      try {
         String buffer =
               this._fileToString(this._rootPath
                     + ServiceProfileTest._SPM_FILE_NAME);
         assertNotNull(buffer);
         ServiceProfile serviceProfile =
               ServiceProfileFactory.getInstance().createServiceProfileFromSPM(
                     buffer);
         assertNotNull(serviceProfile);

         System.out.println(serviceProfile);
      } catch (ServiceProfileException e) {
         fail(e.getMessage());
      } catch (IOException e) {
         fail(e.getMessage());
      }
   }
   */
}
