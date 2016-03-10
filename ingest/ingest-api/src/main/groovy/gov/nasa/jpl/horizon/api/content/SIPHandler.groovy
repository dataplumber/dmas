/*****************************************************************************
 * Copyright (c) 2009 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.horizon.api.content

import gov.nasa.jpl.horizon.api.ProductFile
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfile
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfileFactory
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfileException

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import gov.nasa.podaac.common.api.serviceprofile.Granule
import gov.nasa.podaac.common.api.serviceprofile.GranuleFile
import gov.nasa.podaac.common.api.serviceprofile.IngestDetails
import gov.nasa.podaac.common.api.serviceprofile.Common.ChecksumAlgorithm
import gov.nasa.podaac.common.api.serviceprofile.Common.Status
import gov.nasa.podaac.common.api.util.URIPath
import gov.nasa.podaac.common.api.serviceprofile.SubmissionHeader
import gov.nasa.podaac.common.api.serviceprofile.Common.MessageLevel


/**
 * Utility class for extract information for an input submission information package.
 * The purpose of this class is to keep the ingest programs from having direct
 * dependency on the ServiceProfile interface.  This class assumes the input
 * SIP contains just one product (granule)
 *
 * @author T. Huang
 * @version $Id: $
 */
class SIPHandler {
   private final Log log = LogFactory.getLog(SIPHandler.class)

   private ServiceProfile profile
   private def completeContent

   /**
    * Initialize the class with the input SIP message (XML string)
    *
    * @param contents the SIP message string
    * @throws Exception when failed to create a SerivceProfile object from the input string
    */
   SIPHandler(String contents) throws Exception {
      log.trace "creating SIPHandler"
      profile = ServiceProfileFactory.instance.createServiceProfileFromMessage(contents)
      log.trace "done create profile"
      log.trace profile.toString()

      completeContent = profile?.productProfile?.ingestProfile?.completeContent;
      if (!completeContent) {
         log.trace 'completeContent is null'
         throw new ServiceProfileException("Invalid SIP message.")
      }

      log.trace "done creating SIPHandler"
   }

   /**
    * Method to obtain the product (granule) name.
    *
    * @return the product name
    * @throws Exception when failed to find a product from the ServiceProfile object
    */
   String getProductName() throws Exception {
      def granules = completeContent.granules.asList()
      if (granules.size() > 1) {
         throw new ServiceProfileException("Invalid number of granules.")
      }
      return granules[0].name
   }

   /**
    * Method to obtain the product type name
    *
    * @return the product type name
    * @throws Exception when failed  to find a product from the ServiceProfile object
    */
   String getProductType() throws Exception {
      def granules = completeContent.granules.asList()
      if (granules.size() > 1) {
         throw new ServiceProfileException("Invalid number of granules.")
      }
      return granules[0].productType
   }

   /**
    * Method to obtain the dataset name
    *
    * @return the dataset name
    * @throws Exception when failed to find a product from the ServiceProfile object.
    */
  String getDataset() throws Exception {
      def granules = completeContent.granules.asList()
      if (granules.size() > 1) {
         throw new ServiceProfileException("Invalid number of granules.")
      }
      return granules[0].datasetName
   }

   /**
    * Method to return true if the notify entry exists
    *
    * @return true if message level is set to verbose or erroronly
    */
   boolean isNotify() {
      SubmissionHeader header = profile?.productProfile?.ingestProfile?.header
      if (!header) return false
      return header.contributorMessageLevel in [MessageLevel.VERBOSE, MessageLevel.ERRORONLY]
   }

   /**
    * Method to to return the list of product files associated with the product
    *
    * @return the list of product files
    * @throws Exception when failed to find a product from the ServiceProfile object.
    */
   List getProductFiles() throws Exception {
      log.trace "inside getProductFiles"
      def granules = completeContent.granules
      if (granules.size() > 1) {
         throw new ServiceProfileException("Invalid number of granules.")
      }
      def result = []
      granules.each {granule ->
         granule.files.each {
            def sources = it.sources.asList()
            URIPath path = URIPath.createURIPath(sources[0].link.toString())
            def file = new ProductFile(
                  name: path.filename,
                  source: sources[0].link.toString())
            if (sources[0].size) file.size = sources[0].size
            if (sources[0].checksum) file.checksum = sources[0].checksum
            result << file
         }
      }

      log.trace "File count: ${result.size()}."
      return result
   }

   /**
    * Method to return the name of the original product to be replaced
    *
    * @return the original product name
    * @throws Exception when failed to find a product from the ServiceProfile object
    */
   String getReplace() throws Exception {
      def granules = profile.productProfile.ingestProfile.completeContent.granules as List
      if (!granules || granules.size() != 1) {
         throw new ServiceProfileException("Invalid number of granules.")
      }

      Granule granule = granules[0]
      if (granule != null) {
         return granule.replace
      }
      return null
   }

   /**
    * Method to set the name of the product to be replaced
    *
    * @param originalProduct the original product name
    * @throws Exception when failed to find a product from the ServiceProfile object
    */
   void setReplace(String originalProduct) throws Exception {
      def granules = profile.productProfile.ingestProfile.completeContent.granules as List
      if (!granules || granules.size() != 1) {
         throw new ServiceProfileException("Invalid number of granules.")
      }

      Granule granule = granules[0]
      if (granule != null) {
         granule.replace = originalProduct
      }
   }

   /**
    * Method to return the ServiceProfile object as an XML message string
    *
    * @return the XML string representing the ServiceProfile object
    */
   def getMetadataText() {
      return profile.toString()
   }

   /**
    * Method to update the ingest details
    *
    * @param productType the product type name
    * @param replace the product it replaces
    * @param files the list of product files
    * @param remoteUrl the remote URL to the staging area
    * @param startTime the start time of ingestion
    * @param stopTime the stop time of ingestion
    * @throws Exception when failed to find a product from ServiceProfile object
    */
   def updateIngestDetails(String productType, String replace, def files, String remoteUrl, Date startTime, Date stopTime) throws Exception {
      def granules = profile.productProfile.ingestProfile.completeContent.granules
      if (!granules || granules.size() != 1) {
         throw new ServiceProfileException("Invalid number of granules.")
      }

      granules.each {granule ->
         if (!granule.productType) {
            granule.productType = productType
         }
         if (replace) {
            granule.replace = replace
         }
         granule.files.each {GranuleFile file ->
            def source = file.sources.asList()[0]

            def productFile = files.find {
               it.source == source.link.toString()
            }


            if (!productFile) {
               log.warn("Unable to lookup ingest details for source link ${source.link}")
            } else {
               IngestDetails details = file.createIngestDetails()
               details.source = source
               details.localStaging = new URI("file://${productFile.destination}${productFile.name}")
               details.remoteStaging = new URI("${remoteUrl}${productFile.name}")
               details.size = productFile.size
               details.checksum = productFile.checksum
               details.checksumAlgorithm = ChecksumAlgorithm.MD5
               details.ingestStartTime = productFile.startTime
               details.ingestStopTime = productFile.stopTime
               file.ingestDetails = details
            }
         }
      }
      profile.productProfile.ingestProfile.header.processStartTime = startTime
      profile.productProfile.ingestProfile.header.processStopTime = stopTime
      profile.productProfile.ingestProfile.header.status = Status.STAGED
   }
}
