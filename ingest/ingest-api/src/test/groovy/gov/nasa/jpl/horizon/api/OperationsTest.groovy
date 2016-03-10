package gov.nasa.jpl.horizon.api

import gov.nasa.jpl.horizon.api.Constants
import gov.nasa.jpl.horizon.api.Session
import gov.nasa.jpl.horizon.api.SessionException
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

class OperationsTest extends GroovyTestCase {
  private static Log log = LogFactory.getLog(OperationsTest.class)


  /*
  void testAdd() {
    try {
      String productType = "GHRSST-OSDPD-L2P-GOES11"
      String domainFile = System.getProperty(Constants.PROP_DOMAIN_FILE)
      Session session = new Session(domainFile)
      session.open()
      session.currentDir = "/Volumes/mobile8gb/Development/work/shared/data/ghrsst_test_data_lapinta/"
      session.add(productType, "*.daac")

      while (session.transactionCount > 0) {
        def result = session.result()
        if (result)
          log.info result.description
      }

      session.close()
    } catch (SessionException e) {
      log.error(e.message)
      log.debug(e.message, e)
    }
  }
  */


  void testListManagerOffline() {
     String productType = "xyz"
     String federation = "podaacDev"
     String domainFile = System.getProperty(Constants.PROP_DOMAIN_FILE)
     
     int transactionId = -1
     
     Session session = new Session(domainFile)
     
     assert session.domain.default == federation
     
     assert session.transactionCount == 0
     try {
        transactionId = session.list(productType, ['xyz'] as String[])
     } catch (SessionException e) {
        assert e.getErrno() == Errno.CONN_FAILED
        assert true
     }
     
     assert transactionId == -1
     
     assert session.transactionCount == 0
     
     assert session.result() == null
     
     transactionId = session.close()
     
     assert transactionId == -1
     
     assert session.result() == null
     
     assert session.transactionCount == 0
  }
  
  void testListInvalidProductType() {
     String productType = "xyz"
     String federation = "podaacDev"
     String domainFile = System.getProperty(Constants.PROP_DOMAIN_FILE)
     
     int transactionId = -1
     int transactionIdCounter = 0
     
     Session session = new Session(domainFile)
     
     assert session.domain.default == federation
     
     assert session.transactionCount == 0
     transactionId = session.list(productType, ['xyz'] as String[])
     assert transactionId == ++transactionIdCounter
     
     assert session.transactionCount == 1
     while (session.transactionCount > 0) {
        def result = session.result()
        if (result) {
          assert result.errno == Errno.DENIED
          
          String description = "Unable to retrieve product type $productType for federation $federation."
          assert result.description.trim() == description
        }
     }
     
     assert session.result() == null
     
     assert session.transactionCount == 0
     
     transactionId = session.close()
     
     assert transactionId == ++transactionIdCounter
     
     assert session.transactionCount == 1
     while (session.transactionCount > 0) {
        def result = session.result()
        if (result) {
          assert result.errno == Errno.OK
          assert result.description.trim() == "Connection closed."
        }
     }
     
     assert session.result() == null
     
     assert session.transactionCount == 0
  }

  /*
  void testList() {
    try {
      String productType = "GHRSST-OSDPD-L2P-GOES11"
      String domainFile = System.getProperty(Constants.PROP_DOMAIN_FILE)
      Session session = new Session(domainFile)
      session.open()
      session.list(productType, ['20070828-GOES11-OSDPD-L2P-GOES11_South_2100Z-v01', '20070828-GOES11-OSDPD-L2P-GOES11_North_0200Z-v01', 'xyz'] as String[])
      while (session.transactionCount > 0) {
        def result = session.result()
        if (result) {
          log.info result.description
          def products = result.products
          if (products && products.size > 0) {
            products.each {
              log.info it
            }
          }
        }
      }
      session.close()
    } catch (SessionException e) {
      log.error(e.message)
      log.debug(e.message, e)
    }
  }
*/

}
