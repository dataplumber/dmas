/** ***************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 **************************************************************************** */
package gov.nasa.jpl.horizon.client

import gov.nasa.jpl.horizon.api.Constants
import gov.nasa.jpl.horizon.api.Domain
import gov.nasa.jpl.horizon.api.Errno
import gov.nasa.jpl.horizon.api.Product
import gov.nasa.jpl.horizon.api.Result
import gov.nasa.jpl.horizon.api.Session
import gov.nasa.jpl.horizon.api.SessionException
import gov.nasa.jpl.horizon.api.State
import gov.nasa.podaac.common.util.CommandLine
import gov.nasa.podaac.common.util.CommandLineHelp
import gov.nasa.podaac.common.util.CommandLineParser
import gov.nasa.podaac.common.util.Input
import gov.nasa.podaac.common.util.Option
import gov.nasa.podaac.common.util.Prefix

import gov.nasa.podaac.common.api.serviceprofile.BasicFileInfo
import gov.nasa.podaac.common.api.serviceprofile.CompleteContent
import gov.nasa.podaac.common.api.serviceprofile.Granule
import gov.nasa.podaac.common.api.serviceprofile.GranuleFile
import gov.nasa.podaac.common.api.serviceprofile.IngestProfile
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfile
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfileFactory
import gov.nasa.podaac.common.api.util.Console
import gov.nasa.podaac.common.api.util.FileUtility

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id$
 */
public class IngestClient {
   private static Log log = LogFactory.getLog(IngestClient.class)
   private static final String HEADER =
      Constants.COPYRIGHT + "\n" + Constants.CLIENT_VERSION_STR + "\n" + Constants.API_VERSION_STR
   private static final String PROGRAM_NAME = "ingest"
   private static final List NO_FILE_SIZE_CHECK_REGEXES = [
      /[Rr][Ee][Vv]_[Tt][Ii][Mm][Ee]\.[Dd][Aa][Tt]/
   ]
   private static final Map OPERATIONS = [
      ADD: new Option(
         name: "add",
         required: false,
         description: "Add operation.",
         withValue: false,
         prefixes: [
            new Prefix(name: "add")
         ]
      ),
      REPLACE: new Option(
         name: "replace",
         required: false,
         description: "Replace operation.",
         withValue: false,
         prefixes: [
            new Prefix(name: "replace")
         ]
      ),
      LIST: new Option(
         name: "list",
         required: false,
         description: "List operation.",
         withValue: false,
         prefixes: [
            new Prefix(name: "list")
         ]
      ),
      LOGIN: new Option(
         name: "login",
         required: false,
         description: "Login operation.",
         withValue: false,
         prefixes: [
            new Prefix(name: "login")
         ]
      ),
      HELP: new Option(
         name: "help",
         required: false,
         description: "Help operation.",
         withValue: false,
         prefixes: [
            new Prefix(name: "help")
         ]
      )
   ]
   private static final Map COMMANDS = [
      ADD: [
         OPTIONS: [
            FEDERATION: new Option(
               name: "Federation",
               required: false,
               description: "Federation.",
               withValue: true,
               prefixes: [
                  new Prefix(name: "-f"),
                  new Prefix(name: "--federation", valueSeparator: "=", isLong: true)
               ]
            ),
            PRODUCT_TYPE: new Option(
               name: "ProductType",
               required: false,
               description: "Product Type.",
               withValue: true,
               prefixes: [
                  new Prefix(name: "-t"),
                  new Prefix(name: "--type", valueSeparator: "=", isLong: true)
               ]
            ),
            PRODUCT_FILE: new Option(
               name: "ProductFile",
               required: false,
               description: "Product File.",
               withValue: true,
               prefixes: [
                  new Prefix(name: "-n"),
                  new Prefix(name: "--name", valueSeparator: "=", isLong: true)
               ]
            ),
         ],
         INPUTS: [
            PRODUCT_FILES: new Input(
               name: "ProductFiles",
               required: false,
               description: "Product Files."
            )
         ] as LinkedHashMap,
         METHOD: "addRequested"
      ],
      REPLACE: [
         OPTIONS: [
            FEDERATION: new Option(
               name: "Federation",
               required: false,
               description: "Federation.",
               withValue: true,
               prefixes: [
                  new Prefix(name: "-f"),
                  new Prefix(name: "--federation", valueSeparator: "=", isLong: true)
               ]
            ),
            PRODUCT_TYPE: new Option(
               name: "ProductType",
               required: false,
               description: "Product Type.",
               withValue: true,
               prefixes: [
                  new Prefix(name: "-t"),
                  new Prefix(name: "--type", valueSeparator: "=", isLong: true)
               ]
            ),
            PREVIOUS_PRODUCT_NAME: new Option(
               name: "PreviousProductName",
               required: false,
               description: "Previous Product Name.",
               withValue: true,
               prefixes: [
                  new Prefix(name: "-pn"),
                  new Prefix(name: "--previous", valueSeparator: "=", isLong: true)
               ]
            ),
            NEW_PRODUCT_FILE: new Option(
               name: "NewProductFile",
               required: false,
               description: "New Product File.",
               withValue: true,
               prefixes: [
                  new Prefix(name: "-nn"),
                  new Prefix(name: "--new", valueSeparator: "=", isLong: true)
               ]
            ),
         ],
         INPUTS: [
            NEW_PRODUCT_FILES: new Input(
               name: "NewProductFiles",
               required: false,
               description: "New Product Files."
            )
         ] as LinkedHashMap,
         METHOD: "replaceRequested"
      ],
      LIST: [
         OPTIONS: [
            FEDERATION: new Option(
               name: "Federation",
               required: false,
               description: "Federation.",
               withValue: true,
               prefixes: [
                  new Prefix(name: "-f"),
                  new Prefix(name: "--federation", valueSeparator: "=", isLong: true)
               ]
            ),
            PRODUCT_TYPE: new Option(
               name: "ProductType",
               required: true,
               description: "Product Type.",
               withValue: true,
               prefixes: [
                  new Prefix(name: "-t"),
                  new Prefix(name: "--type", valueSeparator: "=", isLong: true)
               ]
            ),
            PRODUCT_NAME: new Option(
               name: "ProductName",
               required: true,
               description: "Product Name.",
               withValue: true,
               prefixes: [
                  new Prefix(name: "-n"),
                  new Prefix(name: "--name", valueSeparator: "=", isLong: true)
               ]
            ),
         ],
         INPUTS: [:] as LinkedHashMap,
         METHOD: "listRequested"
      ],
      LOGIN: [
         OPTIONS: [
            FEDERATION: new Option(
               name: "Federation",
               required: false,
               description: "Federation.",
               withValue: true,
               prefixes: [
                  new Prefix(name: "-f"),
                  new Prefix(name: "--federation", valueSeparator: "=", isLong: true)
               ]
            ),
            USER_NAME: new Option(
               name: "Username",
               required: false,
               description: "Username.",
               withValue: true,
               prefixes: [
                  new Prefix(name: "-u"),
                  new Prefix(name: "--user", valueSeparator: "=", isLong: true)
               ]
            ),
         ],
         INPUTS: [:] as LinkedHashMap,
         METHOD: "loginRequested"
      ],
      HELP: [
         OPTIONS: [:],
         INPUTS: [
            OPERATION: new Input(
               name: "TargetOperation",
               required: false,
               description: "Target for help."
            )
         ] as LinkedHashMap,
         METHOD: "helpRequested"
      ]
   ]
   private Preference preference
   private Domain domain

   public IngestClient() throws Exception {
      preference = new Preference()
      domain = preference.getDomain()
   }

   public void run(String[] arguments) {
      log.info(HEADER+"\n")
      
      CommandLineParser clp = new CommandLineParser()
      OPERATIONS.each {key, value ->
         clp.options.add(value)
      }
      CommandLine initialCommandLine = clp.parse(arguments)

      String commandName = "HELP"
      OPERATIONS.each {key, value ->
         if (initialCommandLine.hasOption(value)) {
            commandName = key
         }
      }
      prepareCommandLineParser(clp, commandName)
      
      List invalidArguments = getInvalidArguments(clp, arguments)
      if(invalidArguments.size() > 0) {
         invalidArguments.each { invalidArgument ->
            log.error("Invalid argument: "+invalidArgument)
         }
         
         arguments = [
            OPERATIONS["HELP"].prefixes[0].name,
            OPERATIONS[commandName].prefixes[0].name
         ] as String[]
         prepareCommandLineParser(clp, "HELP")
         commandName = "HELP"
      }
      
      try {
         CommandLine commandLine = clp.parse(arguments)
         this."${COMMANDS[commandName].METHOD}"(commandLine)
      } catch (Exception e) {
         log.error(e.message)
      }
   }

   protected void addRequested(CommandLine commandLine) {
      String federation = getFederation(commandLine, COMMANDS.ADD.OPTIONS.FEDERATION)
      if (!isLoggedIn(federation)) {
         log.info("Please login first.")
         System.exit(0)
      }
      LoginInformation loginInformation = preference.getLoginInformation(federation)

      String productType = commandLine.getOptionValue(COMMANDS.ADD.OPTIONS.PRODUCT_TYPE)
      List productFiles = []
      String productFile = commandLine.getOptionValue(COMMANDS.ADD.OPTIONS.PRODUCT_FILE)
      if (productFile) {
         productFiles.add(productFile)
      } else {
         List values = commandLine.getInputValues(COMMANDS.ADD.INPUTS.PRODUCT_FILES)
         if (values) {
            productFiles.addAll(values)
         }
      }

      log.debug("Fed: ${federation}")
      log.debug("ProductType: ${productType}")
      log.debug("ProductFiles: ${productFiles}")
      log.debug("Username: ${loginInformation.userName}")
      log.debug("Password: ${loginInformation.password}")
      log.debug("Domain: ${preference.getDomainFile().getAbsolutePath()}")

      productFiles.each {
         try {
            addProduct(federation, loginInformation, productType, it)
         } catch(Exception exception) {
            log.error("Failed to add "+it+": "+exception.getMessage())
            log.debug("Failed to add.", exception)
         }
      }
   }

   protected void helpRequested(CommandLine commandLine) {
      CommandLineHelp clh = new CommandLineHelp()
      List options = new LinkedList(OPERATIONS.values())
      List inputs = []
      boolean isLong = false
      
      List helpTargets = commandLine.getInputValues(COMMANDS.HELP.INPUTS.OPERATION)
      if(helpTargets) {
         Map.Entry targetEntry = OPERATIONS.find {entry ->
            entry.value.prefixes.find{it.name == helpTargets[0]} != null
         }
         if(targetEntry != null) {
            options = [targetEntry.value]
            Map commandMap = COMMANDS.get(targetEntry.key)
            options.addAll(commandMap.OPTIONS.values())
            inputs.addAll(commandMap.INPUTS.values())
            isLong = true
         }
      }
      
      log.info(clh.format(PROGRAM_NAME, options, inputs, isLong))
   }

   protected void replaceRequested(CommandLine commandLine) {
      String federation = getFederation(commandLine, COMMANDS.REPLACE.OPTIONS.FEDERATION)
      if (!isLoggedIn(federation)) {
         log.info("Please login first.")
         System.exit(0)
      }
      LoginInformation loginInformation = preference.getLoginInformation(federation)

      String productType = commandLine.getOptionValue(COMMANDS.REPLACE.OPTIONS.PRODUCT_TYPE)
      String previousProductName =
         commandLine.getOptionValue(COMMANDS.REPLACE.OPTIONS.PREVIOUS_PRODUCT_NAME)

      List newProductFiles = []
      String newProductFile = commandLine.getOptionValue(COMMANDS.REPLACE.OPTIONS.NEW_PRODUCT_FILE)
      if (newProductFile) {
         newProductFiles.add(newProductFile)
      } else {
         List values = commandLine.getInputValues(COMMANDS.REPLACE.INPUTS.NEW_PRODUCT_FILES)
         if (values) {
            newProductFiles.addAll(values)
         }
      }

      log.debug("ProductType: " + productType)
      log.debug("Previous: " + previousProductName)
      log.debug("New: " + newProductFiles)

      newProductFiles.each {
         try {
            replaceProduct(
               federation,
               loginInformation,
               productType,
               previousProductName,
               it
            )
         } catch(Exception exception) {
            log.error("Failed to replace "+it+": "+exception.getMessage())
            log.debug("Failed to replace.", exception)
         }
      }
   }

   protected void listRequested(CommandLine commandLine) {
      String federation = getFederation(commandLine, COMMANDS.LIST.OPTIONS.FEDERATION)
      if (!isLoggedIn(federation)) {
         log.info("Please login first.")
         System.exit(0)
      }
      LoginInformation loginInformation = preference.getLoginInformation(federation)
      
      String productType = commandLine.getOptionValue(COMMANDS.LIST.OPTIONS.PRODUCT_TYPE)
      String productName = commandLine.getOptionValue(COMMANDS.LIST.OPTIONS.PRODUCT_NAME)
      
      log.debug("ProductType: "+productType)
      log.debug("ProductName: "+productName)
      
      try {
         List<Product> products = listProducts(
            federation,
            loginInformation,
            productType,
            productName
         )
         
         log.info(products.size()+" products found.")
         products.each {product ->
            log.info(product.toString())
         }
      } catch(Exception exception) {
         log.debug("Failed to list.", exception)
         throw new Exception("Failed to list: "+exception.getMessage())
      }
   }

   protected void loginRequested(CommandLine commandLine) {
      String federation = getFederation(commandLine, COMMANDS.LOGIN.OPTIONS.FEDERATION)
      String userName = commandLine.getOptionValue(COMMANDS.LOGIN.OPTIONS.USER_NAME)
      String password = null

      login(federation, userName, password)
   }
   
   protected String readLineFromStandardInput(String prompt) {
      String line = null

      BufferedReader bufferedReader = null
      try {
         print(prompt)
         bufferedReader = new BufferedReader(
               new InputStreamReader(System.in)
         )
         line = bufferedReader.readLine()
      } catch (exception) {
      } finally {
         try {
            if (bufferedReader != null) {
               bufferReader.close()
            }
         } catch (e) {
         }
      }

      return line
   }

   private void login(String federation, String userName, String password) {
      if (!userName) {
         userName = readLineFromStandardInput(federation + ":User name >> ")
      }
      if (!password) {
         password = Console.readPassword(federation + ":Password >> ")
      }

      LoginInformation loginInformation = new LoginInformation()
      loginInformation.userName = userName
      loginInformation.password = password
      preference.setLoginInformation(federation, loginInformation)
   }

   private boolean isLoggedIn(String federation) {
      return (preference.getLoginInformation(federation) != null)
   }

   private String getFederation(CommandLine commandLine, Option option) {
      String federation = commandLine.getOptionValue(option)
      if (!federation) {
         federation = domain.getDefault()
         log.info("Using default federation: " + federation)
      }

      return federation
   }

   private Session createSession(
      String federation,
      LoginInformation loginInformation
      ) throws Exception {
      return new Session(
         federation,
         loginInformation.userName,
         loginInformation.password,
         preference.getDomainFile().getAbsolutePath()
      )
   }

   private void closeSession(Session session) {
      try {
         if (session != null) {
            session.close()
         }
      } catch (exception) {
      }
   }
   
   private void prepareCommandLineParser(
      CommandLineParser commandLineParser,
      String commandName
      ) {
      commandLineParser.inputs.clear()
      commandLineParser.options.clear()
      commandLineParser.options.add(OPERATIONS[commandName])
      
      Map command = COMMANDS[commandName]
      def addAll = {list, map ->
         map.each {key, value ->
            list.add(value)
         }
      }
      addAll(commandLineParser.options, command.OPTIONS)
      addAll(commandLineParser.inputs, command.INPUTS)
   }
   
   private List getInvalidArguments(
      CommandLineParser commandLineParser,
      String[] arguments
      ) {
      List invalidCandidates = arguments.findAll {argument ->
         argument.startsWith("-")
      }
      List invalidArguments = invalidCandidates.findAll {candidate ->
         Option targetOption = commandLineParser.options.find {option ->
            option.prefixes.find {prefix -> prefix.name == candidate} != null
         }
         return targetOption == null
      }
      
      return invalidArguments
   }
   
   private void addProduct(
      String federation,
      LoginInformation loginInformation,
      String productType,
      String productFile
      ) throws Exception {
      Session session = null
      try {
         session = createSession(federation, loginInformation)
         session.open()

         String defaultCurrentDirectory = session.currentDir
         File file = new File(productFile)
         
         if((!productType) || ("".equals(productType.trim()))) {
            List<String> productTypes = retrieveProductTypes(file)
            if(productTypes.size() == 1) {
               productType = productTypes.get(0)
            } else {
               throw new Exception(
                  "None or more than one of product types found: "
                  +productTypes.join(", ")+"."
               )
            }
         }
         log.debug("ProductType: "+productType)
         
         String currentDirectory = defaultCurrentDirectory
         if (file.getParentFile()) {
            currentDirectory = file.getParentFile().getAbsolutePath()
         }
         log.debug("dir: " + currentDirectory + ", name: " + file.getName())

         session.setCurrentDir(currentDirectory)
         session.add(productType, file.getName())
         while (session.transactionCount > 0) {
            Result result = session.result(60)
            
            boolean success = false
            if (result) {
               log.debug(
                  "[ "+result.getErrno()+" ] Action=Add, ProductType="
                  +productType+", File="+file.getName()
               )
               log.info("\t"+result.getDescription())
               if (result.getErrno() == Errno.OK) {
                  success = true
               }
            }
            if(!success) {
               throw new Exception(result.getDescription())
            }
         }
      } catch (SessionException exception) {
         throw exception
      } finally {
         closeSession(session)
      }
   }
   
   private void replaceProduct(
      String federation,
      LoginInformation loginInformation,
      String productType,
      String previousProductName,
      String newProductFile
      ) throws Exception {
      Session session = null
      try {
         session = createSession(federation, loginInformation)
         session.open()

         String defaultCurrentDirectory = session.currentDir
         File file = new File(newProductFile)
         
         if((!productType) || ("".equals(productType.trim()))) {
            List<String> productTypes = retrieveProductTypes(file)
            if(productTypes.size() == 1) {
               productType = productTypes.get(0)
            } else {
               throw new Exception(
                  "None or more than one of product types found: "
                  +productTypes.join(", ")+"."
               )
            }
         }
         log.debug("ProductType: "+productType)
         
         String currentDirectory = defaultCurrentDirectory
         if (file.getParentFile()) {
            currentDirectory = file.getParentFile().getAbsolutePath()
         }
         log.debug("dir: " + currentDirectory + ", name: " + file.getName())

         session.setCurrentDir(currentDirectory)
         if (previousProductName) {
            session.replace(productType, previousProductName, file.getName())
         } else {
            session.replace(productType, file.getName())
         }
         while (session.transactionCount > 0) {
            Result result = session.result(60)
            
            boolean success = false
            if (result) {
               log.info(
                  "[ "+result.getErrno()+" ] Action=Replace, ProductType="
                  +productType+", File="+file.getName()
               )
               log.info("\t"+result.getDescription())
               if (result.getErrno() == Errno.OK) {
                  success = true
               }
            }
            if(!success) {
               throw new Exception(result.description)
            }
         }
      } catch (SessionException exception) {
         throw exception
      } finally {
         closeSession(session)
      }
   }
   
   private void resubmitPendingServiceProfiles(
      String federation,
      LoginInformation loginInformation,
      File pendingDirectory,
      File submittedDirectory,
      File errorDirectory
      ) throws Exception {
      def digFiles
      def resubmit
      
      digFiles = {actionSignature, originalFile, file ->
         if(!file.getName().startsWith(".")) {
            if(file.isDirectory()) {
               file.eachFile {
                  digFiles(actionSignature, originalFile, it)
               }
               if(file.getAbsolutePath() != originalFile.getAbsolutePath()) {
                  RepositoryUtility.deleteDirectory(file, true)
               }
            } else {
               log.info("Resubmitting "+file.getAbsolutePath())
               resubmit(actionSignature, file)
            }
         }
      }
      
      resubmit = {actionSignature, file ->
         ServiceProfile serviceProfile =
            ServiceProfileFactory.getInstance().createServiceProfileFromMessage(file)
         CompleteContent completeContent =
            serviceProfile.getProductProfile().getIngestProfile().getCompleteContent()
         
         String productType = null
         String granuleName = null
         completeContent.getGranules().each {granule ->
            productType = granule.getProductType()
            granuleName = granule.getName()
         }
         
         boolean submitted = false
         try {
            List<Product> products = listProducts(
               federation,
               loginInformation,
               productType,
               granuleName
            )
            if((products.size() == 1) && (State.ABORTED == products.get(0).getState())) {
               File saveDirectory = RepositoryUtility.createDirectory(errorDirectory)
               file.renameTo(new File(
                  saveDirectory.getAbsolutePath()+File.separator+file.getName()
               ))
               FileUtility.writeAll(
                  new File(saveDirectory.getAbsolutePath()+File.separator+file.getName()+".err"),
                  "Failed to perform further action due to ABORTED state."
               )
            } else {
                  replaceProduct(
                     federation,
                     loginInformation,
                     productType,
                     null,
                     file.getAbsolutePath()
                  )
               submitted = true
            }
         } catch(Exception exception) {
            log.warn("Failed to resubmit: "+exception.getMessage())
            log.debug("Failed to resubmit.", exception)
         }
         
         if(submitted) {
            log.debug("Resubmitted. Moving SIP: "+submittedDirectory.getAbsolutePath()+File.separator+file.getName())
            file.renameTo(new File(
               submittedDirectory.getAbsolutePath()+File.separator+file.getName()
            ))
         }
      }
   }
   
   private List<Product> listProducts(
      String federation,
      LoginInformation loginInformation,
      String productType,
      String productName
      ) throws Exception
      {
      List<Product> products = null
      Session session = null
      try {
         session = createSession(federation, loginInformation)
         session.open()

         session.list(productType, [productName] as String[])
         while (session.transactionCount > 0) {
            Result result = session.result(60)
            if (result) {
               products = (List<Product>)result.getProducts()
            }
         }
      } catch (SessionException exception) {
         throw exception
      } finally {
         closeSession(session)
      }
      
      return (products) ? products : []
   }
   
   private List<String> retrieveProductTypes(File file) throws Exception {
      LinkedList<String> productTypes = new LinkedList<String>()
      
      ServiceProfile serviceProfile =
         ServiceProfileFactory.getInstance().createServiceProfileFromMessage(file)
      CompleteContent completeContent =
         serviceProfile.getProductProfile().getIngestProfile().getCompleteContent()
      completeContent.getGranules().each {granule ->
         productTypes.add(granule.getProductType())
      }
      
      return productTypes
   }

   public static void main(String[] args) {
      try {
         IngestClient ingestClient = new IngestClient()
         ingestClient.run(args)
      } catch (Exception e) {
         if(log.isDebugEnabled()) {
            IngestClient.log.debug(e.message, e)
         }
         IngestClient.log.error(e.message)
      }
   }
}
