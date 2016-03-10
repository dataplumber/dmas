package gov.nasa.jpl.horizon.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.Ignore;

import java.util.List;

import gov.nasa.jpl.horizon.api.protocol.ProtocolException;


/**
 * Created by IntelliJ IDEA. User: thuang Date: Sep 14, 2008 Time: 11:43:53 PM To change this template use File |
 * Settings | File Templates.
 */

public class JavaOperationsTest {
   private static Log log = LogFactory.getLog(JavaOperationsTest.class);

   @Ignore @Test public void testAdd() {
      Session session = null;
      try {
         String productType = "GHRSST-OSDPD-L2P-GOES11"; //"GHRSST_L2P_GOES11";
         String domainFile =
               System.getProperty(Constants.getPROP_DOMAIN_FILE());
         session = new Session(domainFile);
         session.open();
         session.setCurrentDir("/Volumes/mobile8gb/Development/work/shared/data/ghrsst_test_data_local/");
         session.add(productType, "FR-20070828-GOES11-OSDPD-L2P-GOES11_South_1730Z-v01.xml.daac");

         while (session.getTransactionCount() > 0) {
            log.info ("transaction count: " + session.getTransactionCount());
            Result result = session.result(0);
            if (result != null) {
               log.info(result.getDescription());
            }
         }

      } catch (SessionException e) {
         log.error(e.getMessage(), e);
      } finally {
         if (session != null) {
            try {
               session.close();
            } catch (SessionException e) {
               log.error(e.getMessage(), e);
            }
         }
      }
   }


   @Test public void testList() {
      Session session = null;
      try {
         String productType = "GHRSST-OSDPD-L2P-GOES11"; //"GHRSST_L2P_GOES11";
         String domainFile =
               System.getProperty(Constants.getPROP_DOMAIN_FILE());
         session = new Session(domainFile);
         session.open();
         session.list(productType, new String[]{"20070828-GOES11-OSDPD-L2P-GOES11_South_2100Z-v01", "20070828-GOES11-OSDPD-L2P-GOES11_North_0200Z-v01", "xyz"});

         while (session.getTransactionCount() > 0) {
            log.info ("transaction count: " + session.getTransactionCount());
            Result result = session.result(0);
            if (result != null) {
               log.info("Errno=" + result.getErrno() + "  Description:" + result.getDescription());
               List<Product> products = (List<Product>)result.getProducts();
               for (Product product : products) {
                  log.info(product.toString());
               }
            }
         }
      } catch (ProtocolException e) {

      } catch (SessionException e) {
         log.error(e.getMessage(), e);
      } finally {
         if (session != null) {
            try {
               session.close();
            } catch (SessionException e) {
               log.error(e.getMessage(), e);
            }
         }
      }
   }
}
