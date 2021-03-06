package gov.nasa.jpl.horizon.server
import gov.nasa.jpl.horizon.api.Post

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import gov.nasa.jpl.horizon.api.protocol.IngestProtocol


class EchoTest extends GroovyTestCase {

   static Log log = LogFactory.getLog(ServerPortListenerTest.class)

   void testSimpleGet() {

      def listener = new ServerPortListener(
         "user",
         "HTTP",
         "kingdom.jpl.nasa.gov",
         8182,
         "/ingest")

      listener.start()

      IngestProtocol request = IngestProtocol.createEngineIngestRequest ("GHRSST_GOES11", 1, "productname", 1)

      Post post = new Post(url: "http://kingdom.jpl.nasa.gov:8182/ingest")
      post.body = request.toRequest()
      log.debug (post.body)
      IngestProtocol response = new IngestProtocol()
      response.load(post.text)
      log.debug ("${response.errno}: ${response.description}")


      listener.stop()
      log.debug ("listener stopped.")
   }
}


