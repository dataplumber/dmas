/** ***************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 **************************************************************************** */

package gov.nasa.jpl.horizon.client

import gov.nasa.jpl.horizon.api.Constants
import gov.nasa.jpl.horizon.api.SessionException
import gov.nasa.podaac.common.api.serviceprofile.Common.MessageLevel
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfile
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfileException
import gov.nasa.podaac.common.api.transformer.mmr.MMRException
import gov.nasa.podaac.common.api.transformer.mmr.MMRTransformer
import javax.jms.*
import org.apache.commons.cli.*
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory


/**
 * Common client utility application. This application maintains a lookup table
 * for all possible command-line user operations for interacting with the Ingest
 * service.
 *
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: UtilClient.java 1482 2008-07-16 17:54:42Z axt $
 */
public class UtilClient implements Runnable {

   def SubmitType = ['SIP', 'SPM', 'MMR']

   def MsgTopic = [AIP_POST: 'podaac.aip.post', AIP_ACK: 'podaac.aip.ack', AIP_COMPLETE: 'podaac.aip.complete']
   
   private static final String CONNECTION_FACTORY = "/ConnectionFactory"

   static final String PROTOCOL = "horizon_msg_protocol";
   static final String SESSIONID = "horizon_msg_sessionid";
   static final String TRANSACTIONID = "horizon_msg_transactionid";
   static final String USERNAME = "horizon_msg_username";
   static final String COMMAND = "horizon_msg_command";
   static final String SUBMITTER_IP = "horizon_msg_sumbitter_ip";
   static final String HANDLER_IP = "horizon_msg_handler_ip";
   static final String SIP_SIZE = "horizon_msg_sippost_size";
   static final String SIP_MIME = "horizon_msg_sip_mimetype";
   static final String SIP_DIGEST_ALG =
   "horizon_msg_sip_digestalgorithm";
   static final String SIP_DIGEST = "horizon_msg_sip_digest";
   static final String PAYLOAD = "horizon_msg_payload";
   static final String REPLY_ERRNO = "horizon_msg_reply_errno";
   static final String REPLY_MSG = "horizon_msg_reply_msg";
   static final String REPLY_SIP_ID = "horizon_msg_reply_sip_id";


   private static String PROP_USER_HOME = "horizon.user.home";
   private static String PROP_USER_OPERATION = "horizon.user.operation";
   private static String PROP_DOMAIN_FILE = "horizon.domain.file";

   private static String PROP_SCRIPT = "horizon.user.application";

   private static String PROP_SERVER_TYPE = "horizon.server.type";

   private static String ERROR_TAG = "HORIZON ERROR:";

   private static String USAGE_HEADER =
   Constants.COPYRIGHT + "\n" + Constants.CLIENT_VERSION_STR + "\n" + Constants.API_VERSION_STR
   private static Log _logger = LogFactory.getLog(UtilClient.class);

   public static void main(String[] args) {
      UtilClient client = new UtilClient(args);
      client.run();

      System.exit(client.getErrorCount());
   }

   private String _domainFile;
   private String _userOperation;
   private String _userScript;
   private String _command;

   private CommandLine _cmdLine;
   private Options _options;
   private String _cmdUsage = null;

   private int _errorCount = 0;

   private boolean _help = false;

   private String[] _args;

   public UtilClient(String[] args) {
      this._args = args;
      this._domainFile = System.getProperty(UtilClient.PROP_DOMAIN_FILE);
      this._userOperation = System.getProperty(UtilClient.PROP_USER_OPERATION);
      this._userScript = System.getProperty(UtilClient.PROP_SCRIPT);

      this._command = CommandTable.getCommand(this._userOperation);
   }

   private boolean _hasRequiredOptions(String[] requiredOptions) {
      boolean result = true;

      for (String requiredOption: requiredOptions) {
         if (!this._cmdLine.hasOption(requiredOption)) {
            result = false;
            break;
         }
      }

      return result;
   }

   protected void _parseArgs() throws ParseException {
      def requiredOptions = []
      CommandLineParser parser = new BasicParser();
      this._options = new Options();

      switch (this._command) {
         case 'TRANSFORM':
            this._options.addOption("u", "contributor", true,
                  "Contributor email address");
            this._options.addOption("m", "vebosity", true,
                  "Email verbosity [VERBOSE, ERRORONLY, SILENT]");
            this._options.addOption("o", "output", true, "Output directory");
            this._options.addOption("e", "extension", true,
                  "Output file extension");
            this._options.addOption("t", "type", true, "[SPM, MMR]");
            this._options.addOption("p", "URL", true, "URL path to data file");
            this._options.addOption("h", "help", false,
                  "Print this usage information");
            this._options.addOption("k", "keep path", false,
                  "Keeps the original file path.");
            this._options.addOption("s", "search path", true,
                  "Search path for data files (separated by ;)");
            requiredOptions = ['t']
            break;
         case 'MSG_SUBSCRIBE':
            StringBuilder topicNames = new StringBuilder();
            MsgTopic.each {key, value ->
               topicNames.append(key.toString())

            }

            this._options.addOption("t", "topic", true, "[" + topicNames.toString() + "]");
            this._options.addOption("u", "username", true, "User name");
            this._options.addOption("p", "password", true, "Password");
            this._options.addOption("h", "help", false,
                  "Print this usage information");
            requiredOptions = ['t', 'u', 'p']
      }
      this._cmdLine = parser.parse(this._options, this._args);
      this._help = (!_hasRequiredOptions((String[])requiredOptions));
   }

   private void _printUsage() {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(this._userScript, UtilClient.USAGE_HEADER,
            this._options, null, false);
   }

   protected void _processMsgSubscribe() throws SessionException {
      this._userScript += " [OPTION]...";
      try {
         this._parseArgs();
         if (this._help || this._cmdLine.hasOption('h')) {
            this._printUsage();
            return;
         }

         String username = this._cmdLine.getOptionValue("u");
         String password = this._cmdLine.getOptionValue("p");
         String topic = null
         try {

            topic = this._cmdLine.getOptionValue("t")
         } catch (IllegalArgumentException e) {
            UtilClient._logger.error("Invalid message topic type: \""
                  + this._cmdLine.getOptionValue("t") + "\".");
            return;
         }

         try {
            TopicConnectionFactory factory =
            ServiceLocator.lookup(UtilClient.CONNECTION_FACTORY);
            TopicConnection connection =
            factory.createTopicConnection(username, password);
            TopicSession subSession =
            connection.createTopicSession(false,
                  javax.jms.Session.AUTO_ACKNOWLEDGE);

            Topic subTopicObj =
            ServiceLocator.lookup(MsgTopic[topic]);

            TopicSubscriber subscriber =
            subSession.createSubscriber(subTopicObj);
            connection.start();

            while (true) {
               try {
                  Message msg = subscriber.receive();
                  TextMessage textMessage = (TextMessage) msg;
                  UtilClient._logger.info(textMessage.getText());
               } catch (JMSException e) {
                  if (UtilClient._logger.isDebugEnabled())
                     e.printStackTrace();
                  UtilClient._logger.error("Subscription error: "
                        + e.getMessage());
               }
            }

         } catch (JMSException e) {
            if (UtilClient._logger.isDebugEnabled())
               e.printStackTrace();
            UtilClient._logger.error(
                  "Unable to establish subscription session.", e);
         }

      } catch (ParseException e) {
         UtilClient._logger.error("Invalid option(s).");
         this._printUsage();
         return;
      }
   }

   public int getErrorCount() {
      return this._errorCount;
   }

   public void run() {
      if (this._command.equals('INVALID_COMMAND')) {
         UtilClient._logger.error(ERROR_TAG + "Unrecognized user operation: "
               + this._userOperation);
         ++this._errorCount;
         return;
      }


      try {
         switch (this._command) {
            case 'MSG_SUBSCRIBE':
               this._processMsgSubscribe();
               break;
            default:
               UtilClient._logger.error("Invalid command");
               ++this._errorCount;
         }
      } catch (SessionException e) {
         UtilClient._logger.error(e.getMessage());
         ++this._errorCount;
      }
   }
}
