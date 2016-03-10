/*****************************************************************************
 * Copyright (c) 2010 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.httpfetch.oceandata;

import gov.nasa.podaac.common.api.file.FileProduct;
import gov.nasa.podaac.common.api.util.ChecksumUtility;
import gov.nasa.podaac.common.api.util.FileUtility;
import gov.nasa.podaac.common.httpfetch.api.FetchException;
import gov.nasa.podaac.common.httpfetch.api.HttpFetcher;
import gov.nasa.podaac.common.httpfetch.api.HttpFileHandler;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.Assert.*;

import java.io.*;
import java.util.Map;

/**
 * This is the Unit test class for the OceanDataWalker class
 *
 * @author Thomas Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: $
 */
public class OceanDataWalkerTest extends TestCase {
   private static Log _logger = LogFactory.getLog(OceanDataWalker.class);

   @Test
   public void testSimple() {
      OceanDataWalker odw = new OceanDataWalker();
      HttpFileHandler handler = new TestHandler();
      odw.walk(//"http://oceandata.sci.gsfc.nasa.gov/MODISA/L3SMI/2010/160",
            "http://oceandata.sci.gsfc.nasa.gov/Aquarius/L3SMI/2007/001",
            //"(http://.+/(A|T)(\\d{4})(\\d{3})\\d*\\.L3m_(DAY|8D|MO|YR)_N{0,1}SST4_(4|9)\\.bz2)",
            "(http://.+/(Q)(\\d{7})\\d*\\.L\\S*\\.bz2)",
            handler);
      int stat = odw.checkResult();
      OceanDataWalkerTest._logger.info(stat + " URLs found.");

      // don't forget to clear the memory used by the handler
      handler.shutdown();

      // don't forget to clear the memory used by the OceanDataWalker
      odw.shutdown();
   }


   /**
    * Implement the HttpFileHandler to retrieve the file for each URL
    */
   class TestHandler implements HttpFileHandler {


      public TestHandler() {
      }

      public void handle(HttpFetcher fetcher, String rootURL, String url, Map<String, String> checksum) {
         FileProduct fp = null;
         try {
            String filename = url.substring(url.lastIndexOf('/') + 1, url.length());
            String filechecksum = checksum.get(filename);
            if (filechecksum == null) {
               fp = fetcher.createFileProduct(url);
            } else {
               fp = fetcher.createFileProduct(url, ChecksumUtility.DigestAlgorithm.SHA1, filechecksum);
            }
            OceanDataWalkerTest._logger.info(fp.toString());
            // skip zero-length file
            if (fp.getSize() == 0) {
               OceanDataWalkerTest._logger.warn("File: " + url + " is zero size.");
               try {
                  fp.close();
                  fetcher.shutdown();
               } catch (IOException e) {
               }
               return;
            }
            InputStream is = null;
            OutputStream os = null;
            try {
               if (fp.exists()) {
                  is = fp.getInputStream();
                  os = new FileOutputStream(new File("/tmp/" + fp.getName()));
                  if (fp.getDigestValue() != null) {
                     String computed = ChecksumUtility.getDigest(ChecksumUtility.DigestAlgorithm.SHA1, is, os);
                     OceanDataWalkerTest._logger.debug("actual: " + computed + "  expected: " + fp.getDigestValue());

                     assertEquals("Verify checksum", fp.getDigestValue(), computed);
                  } else {
                     FileUtility.downloadFromStream(is, os);
                  }
               }
            } catch (IOException e) {
               OceanDataWalkerTest._logger.error(e.getMessage(), e);
            } finally {
               try {
                  if (is != null) is.close();
                  if (os != null) os.close();
               } catch (IOException e) {
               }
            }
         } catch (FetchException e) {
            OceanDataWalkerTest._logger.error(e.getMessage(), e);
         } finally {
            if (fp != null && fp.exists()) {
               try {
                  fp.close();
               } catch (IOException e) {
                  OceanDataWalkerTest._logger.error(e.getMessage(), e);

               }
            }
            fetcher.shutdown();
         }
      }

      public void shutdown() {
      }
   }
}
