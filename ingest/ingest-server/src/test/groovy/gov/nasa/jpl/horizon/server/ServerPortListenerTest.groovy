package gov.nasa.jpl.horizon.server

import gov.nasa.jpl.horizon.api.Constants
import gov.nasa.jpl.horizon.api.Post
import gov.nasa.jpl.horizon.api.protocol.IngestProtocol
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

class ServerPortListenerTest extends GroovyTestCase {

   static Log log = LogFactory.getLog(ServerPortListenerTest.class)

   void testSimpleGet() {

      def listener = new ServerPortListener("user", 16, "HTTP", "localhost", 8182, "/submit")

      listener.start()


      def threads = []
      1000.times {
         threads << Thread.start {
            def request = IngestProtocol.createEnginePingRequest('podaacDev', 'horizonDev_1')
            def result = IngestProtocol.createEnginePingRequest('podaacDev', 'horizonDev_1')


            def post = new Post(url: "http://localhost:8182/submit")
            post.body = request.toRequest()
            result.load(post.text)

            assert result.errno == Constants.OK
            log.trace(result.description)
         }
      }

      threads.each {
         it.join()
      }

      listener.stop()
   }
}
