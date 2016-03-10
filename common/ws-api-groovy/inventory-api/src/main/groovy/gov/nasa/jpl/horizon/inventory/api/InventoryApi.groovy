package gov.nasa.jpl.horizon.inventory.api

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import java.net.URI
import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

import gov.nasa.podaac.common.api.serviceprofile.ArchiveFileInfo;
import gov.nasa.podaac.common.api.serviceprofile.ArchiveGranule;
import gov.nasa.podaac.common.api.util.StringUtility;
import gov.nasa.podaac.common.api.serviceprofile.ArchiveProfile;
import gov.nasa.podaac.common.api.serviceprofile.GranuleHistory;
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfile;
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfileFactory;

public class InventoryApi {

	private static Log log = LogFactory.getLog(InventoryApi.class)
	private String baseUrl; 
	private HTTPBuilder http;
	
	private String user, pass;
	
	public InventoryApi(String baseUrl){
		this.baseUrl = baseUrl;
		
		URI uri = new URI(baseUrl)
		http = new HTTPBuilder(uri)
	}
	
	public void setAuthInfo(String user, String pass){
		this.user = user;
		this.pass = pass;
	}
	
	/*
	 * SIP API
	 * 
	 */
	public ServiceProfile ingestSip(ServiceProfile sip){
		return ingestSip(sip, this.user, this.pass)
	}
	
	public ServiceProfile ingestSip(ServiceProfile sip, String userName,String password){
		//sip-to-xml
		println sip.toString()
		//now send the XML
		log.debug "http - POST - /inventory/sip/"
		def postBody = [sip:"$sip",userName:"$userName", password:"$password"]
		http.post(path : "/inventory/sip", body:postBody, requestContentType: URLENC, contentType : TEXT, headers : [Accept : 'application/xml']){ resp, txt ->
			if(resp.getStatus() != 200)
			{
				log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
				return false;
			}
			log.debug "Status updated..."
			
			//create a sip from xml...
			BufferedReader br = new BufferedReader(txt);
			String line = br.readLine()
			StringBuilder sb = new StringBuilder(); 
			while(line != null){
				sb.append(line);
				line = br.readLine();
			}
			
			ServiceProfile serviceProfile =
			ServiceProfileFactory.getInstance().
			createServiceProfileFromMessage(sb.toString());
			log.debug("Returning sip")
			return serviceProfile;
		}

	}
	
	
	/*
	 * PROVIDER API
	 * 
	 */
	
	public List<Map<String,String>> getProviders(){
		http.get(path : '/inventory/providers', contentType: ContentType.XML){ resp, xml ->
			log.debug "http - GET - /inventory/providers"
			if(resp.getStatus() != 200)
				{
					log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
					return;
				}
			def result = []
			for (provider in xml.provider) {
				def entry = [:]
				provider.children().each{ 
					entry[it.name()] = it.text()}
				result << entry
			}
			
			return result
			//xml.provider.each {
			//  println "provider: ${it.id}, ${it.shortName}, ${it.longName}"
			//}
		}
	}
	
	def getProvider(int id){
		return getProvider("$id")	
	}
	
	def getProvider(String id){
		log.debug "http - GET - /inventory/provider/$id"
		http.get(path : "/inventory/provider/$id", contentType: ContentType.XML){ resp, xml ->
			log.debug "http - GET - /inventory/provider/$id"
			if(resp.getStatus() != 200)
				{
					log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
					return;
				}
			
			return xml;
		}
		
	}
	
	/*
	 * DATASET API
	 */
	
	def getDataset(int dataset){
		log.debug "getDataset by integer: $dataset"
		return getDataset("$dataset")
	}
	def getDataset(String dataset){
		http.get(path : "/inventory/dataset/$dataset", contentType: ContentType.XML){ resp, xml ->
			log.debug "http - GET - /inventory/dataset/$dataset"
			if(resp.getStatus() != 200)
				{
					log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
					return;
				}
			return xml
		}
	}
	
	public Integer getLatestGranuleIdByDataset(int id){
		return getLatestGranuleIdByDataset("$id")
	}
	
	public Integer getLatestGranuleIdByDataset(String id){
		//inventory/dataset/15/latestGranule
		http.get(path : "/inventory/dataset/$id/latestGranule", contentType: ContentType.XML){ resp, xml ->
			log.debug "http - GET - /inventory/dataset/$id/latestGranule"
			if(resp.getStatus() != 200)
				{
					log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
					return;
				}
			xml.children().each{
				log.debug "Value:  ${it.text()}"
				return Integer.valueOf(it.text().toString())
			}
		}
	}
	
	def getDatasetPolicy(int id){
		return getDatasetPolicy("$id")
	}
	
	def getDatasetPolicy(String id){
		http.get(path : "/inventory/dataset/$id/policy", contentType: ContentType.XML){ resp, xml ->
			log.debug "http - GET - /inventory/dataset/$id/policy"
			if(resp.getStatus() != 200)
				{
					log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
					return;
				}
			return xml
		}
	}
	
	def getDatasetCoverage(int id){
		return getDatasetCoverage("$id")	
	}
	
	def getDatasetCoverage(String id){
		http.get(path : "/inventory/dataset/$id/coverage", contentType: ContentType.XML){ resp, xml ->
			log.debug "http - GET - /inventory/dataset/$id/coverage"
			if(resp.getStatus() != 200)
				{
					log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
					return;
				}
			return xml
		}
	}
		
	public List getDatasets(){

		http.get(path : '/inventory/datasets', contentType: ContentType.XML){ resp, xml ->
			log.debug "http - GET - /inventory/datasets"
			if(resp.getStatus() != 200)
				{
					log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
					return;
				}
			def result = []
				for (ds in xml.dataset) {
					def entry = [:]
					ds.children().each{
						entry[it.name()] = it.text()}
					result << entry
				}
				return result
		}
	}
	
	/*
	 * Granule API
	 * 
	 */
	
	public boolean updateGranuleStatus(int granuleId, String status){
		return updateGranuleStatus(granuleId, status, this.user, this.pass)
	}
	
	public boolean updateGranuleStatus(int granuleId, String status, String userName, String password){
		//http://localhost:8080/inventory/granule/55/status/?status=ONLINE&userName=axt&password=axt388
		log.debug "http - POST - /inventory/granule/$granuleId/status/?status=$status&userName=$userName&password=$password"
		def postBody = [status:"$status",userName:"$userName", password:"$password"]
		
		http.post(path : "/inventory/granule/$granuleId/status/", body:postBody, requestContentType: URLENC){ resp ->
			if(resp.getStatus() != 200)
			{
				log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
				return false;
			}
			log.debug "Status updated..."
			return true;
		}
	}
	
	//returns XML
	def getGranuleById(int id){
		return getGranuleByName( "$id")
	}
	
	//returns XML
	def getGranuleByName(String id){
		http.get(path : "/inventory/granule/$id", contentType: ContentType.XML){ resp, xml ->
			log.debug "http - GET - /inventory/granule/$id"
			if(resp.getStatus() != 200)
				{
					log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
					return;
				}
			return xml
		}
	}
	
	//returns XML
	def getGranuleByNameAndDataset(String id, int datasetId){
		return getGranuleByNameAndDataset(id,"$datasetId")
	}
	
	//returns XML
	def getGranuleByNameAndDataset(String id, String datasetId){
		//?dataset=46
		log.debug "http - GET - /inventory/granule/$id?dataset=$datasetId"
		http.get(path : "/inventory/granule/$id", contentType: ContentType.XML, query : [dataset:"$datasetId"]){ resp, xml ->
			if(resp.getStatus() != 200)
				{
					log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
					return;
				}
			return xml
		}
	}
	
	def getGranuleArchivePath(int id){
		http.get(path : "/inventory/granule/$id/archivePath", contentType: ContentType.XML){ resp, xml ->
			log.debug "http - GET - /inventory/granule/$id/archivePath"
			if(resp.getStatus() != 200)
				{
					log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
					return;
				}
			xml.children().each {
				log.debug("ArchivePath: ${it.text()}")
				return it.text()
			}
		}
	}

	
}
	
