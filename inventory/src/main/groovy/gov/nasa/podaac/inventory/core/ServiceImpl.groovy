package gov.nasa.podaac.inventory.core

import gov.nasa.podaac.inventory.exceptions.InventoryException;
import gov.nasa.podaac.inventory.model.Contact;
import gov.nasa.podaac.inventory.exceptions.InventoryException;
import java.util.List;
import gov.nasa.podaac.inventory.model.Collection;
import gov.nasa.podaac.inventory.exceptions.InventoryException;
import java.util.List;
import java.util.Map;
import java.util.Calendar;
import java.net.URI
import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovyx.net.http.HttpResponseException
import org.apache.http.conn.scheme.Scheme
import javax.net.ssl.SSLContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;

import gov.nasa.podaac.common.api.ssl.SSLFactory;
import gov.nasa.podaac.common.api.metadatamanifest.MetadataManifest
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfile;
import gov.nasa.podaac.common.api.serviceprofile.ServiceProfileFactory;
import gov.nasa.podaac.inventory.api.Service;
import gov.nasa.podaac.inventory.exceptions.InventoryException;
import gov.nasa.podaac.inventory.model.Dataset;
import gov.nasa.podaac.inventory.model.ElementDD;
import gov.nasa.podaac.inventory.model.DatasetCoverage;
import gov.nasa.podaac.inventory.model.DatasetPolicy;
import gov.nasa.podaac.inventory.model.GranuleMetaHistory;
import gov.nasa.podaac.inventory.model.GranuleSpatial;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.GranuleReference
import gov.nasa.podaac.inventory.model.Provider;
import gov.nasa.podaac.inventory.core.Transformer;
import gov.nasa.podaac.inventory.exceptions.InventoryException;

import groovyx.net.http.HTTPBuilder;

class ServiceImpl implements Service {

	private static Log log = LogFactory.getLog(ServiceImpl.class)
	private String baseUrl; 
	private HTTPBuilder http;
	
	private String user, pass;
	

	public ServiceImpl(){
		
	}
	
	public void setHost(String baseUrl, int port){
		this.baseUrl = baseUrl;
		URI uri = new URI(baseUrl)
		http = new HTTPBuilder(uri)
		SSLContext sc = SSLFactory.getCustomSSLFactory();
//		SslContextedSecureProtocolSocketFactory secureProtocolSocketFactory =
//			new SslContextedSecureProtocolSocketFactory(sc, false);
//		secureProtocolSocketFactory.setHostnameVerification(false);
		http.client.connectionManager.schemeRegistry.register( SSLFactory.getNaiveScheme(port, sc))
	}
	
	public ServiceImpl(String baseUrl, int port){
		this.baseUrl = baseUrl;
		
		URI uri = new URI(baseUrl)
		http = new HTTPBuilder(uri)
		SSLContext sc = SSLFactory.getCustomSSLFactory();
//		SslContextedSecureProtocolSocketFactory secureProtocolSocketFactory =
//			new SslContextedSecureProtocolSocketFactory(sc, false);
//		secureProtocolSocketFactory.setHostnameVerification(false);
		http.client.connectionManager.schemeRegistry.register( SSLFactory.getNaiveScheme(port, sc))
	}
	
	public void setAuthInfo(String user, String pass){
		this.user = user;
		this.pass = pass;
	}
	
	public boolean isOnline(){
		boolean online = false;
		http.handler.failure = { resp ->
			log.debug "Invntory Service is offline "
			return false;
		  }
		try{
		http.get(path : '/inventory/heartbeat', contentType: ContentType.XML){ resp, xml ->
			log.debug "http - GET - /inventory/heartbeat"
			log.debug "status: " + resp.getStatus()
			if(resp.getStatus() != 200)
				{
					log.debug("returning false")
					return false;
				}
			else{
				log.debug("returning true")
				online =  true;
				}
		}
		}catch(Exception e){
			log.debug("Connection refused most likely.");
			return false;
		}
		return online;
	}
	
	
	/*
	 * MANIFEST
	 */
	public MetadataManifest processManifest(MetadataManifest mm)  throws InventoryException{
		return processManifest(mm, this.user, this.pass);
	}	
	
	public MetadataManifest processManifest(MetadataManifest mm, String userName, String password) throws InventoryException{
		log.debug "http - POST - /inventory/manifest?"
		String mmXml = mm.generateXml()
		def postBody = [manifest:"$mmXml",userName:"$userName", password:"$password"]
		http.handler.failure = { resp ->
			log.debug "Service error processing manifest"
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		
		try{
			http.post(path : "/inventory/manifest", body:postBody, requestContentType: URLENC, contentType : TEXT, headers : [Accept : 'application/xml']){ resp, txt ->

			MetadataManifest mf = null
			if(resp.getStatus() == 201){
				//create a sip from xml...
				BufferedReader br = new BufferedReader(txt);
				String line = br.readLine()
				StringBuilder sb = new StringBuilder();
				while(line != null){
					sb.append(line);
					line = br.readLine();
				}			
				mf= new MetadataManifest(sb.toString())
			}
			return mf;
			}
		}catch(Exception ex){
			log.debug("Error Processing Manifest")
			throw new InventoryException(ex.getMessage());
		}	
	}
	
	/*
	 * SIP API
	 * 
	 */
	public ServiceProfile ingestSip(ServiceProfile sip) throws InventoryException{
		return ingestSip(sip, this.user, this.pass)
	}
	
	public ServiceProfile ingestSip(ServiceProfile sip, String userName,String password) throws InventoryException{
		//sip-to-xml
		println sip.toString()
		//now send the XML
		log.debug "http - POST - /inventory/sip/"
		log.debug "$userName , $password";
		def postBody = [sip:"$sip",userName:"$userName", password:"$password"]
		http.handler.failure = { resp ->
			log.debug "Service error processing SIP"
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		try{
			http.post(path : "/inventory/sip", body:postBody, requestContentType: URLENC, contentType : TEXT, headers : [Accept : 'application/xml']){ resp, txt ->
				log.debug("Response status: " + resp.getStatus())
				if(resp.getStatus() != 201)
				{
					log.info("Status returned non-201 code: ${resp.getStatus()}; returning")
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
		}catch(ex){
			log.debug("Error storing sip")
			throw new InventoryException(ex.getMessage());
		}

	}
	
	
	/*
	 * PROVIDER API
	 * 
	 */
	
	public List<Map<String,String>> getProviders() throws InventoryException{
		http.handler.failure = { resp ->
			log.debug "Service error processing providers "
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
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
	
	public Provider getProvider(Integer id) throws InventoryException{
		return getProvider("$id")	
	}
	
	public Provider getProvider(String id) throws InventoryException{
		log.debug "http - GET - /inventory/provider/$id"
		
		http.handler.failure = { resp ->
			log.debug "404 - provider not found"
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		
		try{
			http.get(path : "/inventory/provider/$id", contentType: ContentType.XML){ resp, xml ->
				log.debug "http - GET - /inventory/provider/$id"
				if(resp.getStatus() != 200)
					{
						log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
						return;
					}
				
				return Transformer.parseProvider(xml);
			}
	
		}catch(HttpResponseException ex){
			println "msg: "+ ex.getMessage();
			println "resp: "+ ex.getResponse();
		}
		
	}
	
	/*
	 * COLLECTION API
	 */
	
	public Collection getCollectionById(Integer id)throws InventoryException{
		return getCollectionById("$id");
	}
	public Collection getCollectionById(String id)throws InventoryException{
		
		http.handler.failure = { resp ->
			log.debug "Service error processing Collection Request"
			log.debug(resp.statusLine);
			
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		try{
				http.get(path : "/inventory/collection/$id", contentType: ContentType.XML){ resp, xml ->
				log.info "http - GET - /inventory/collection/$id"
				if(resp.getStatus() != 200)
					{
						log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
						return;
					}
					log.debug("Parsing collection.")
				return Transformer.parseCollection(xml);
				}
		}catch(HttpResponseException ex){
			ex.printStackTrace()
			log.debug "404- dataset not found"
			return null
		}
		
	}
	
	public Collection getCollectionByDataset(Integer datasetId) throws InventoryException{
		return getCollectionByDataset("$datasetId");
	}
	
	public Collection getCollectionByDataset(String id) throws InventoryException{
		http.handler.failure = { resp ->
			log.debug "Service error processing Collection Request"
			log.debug(resp.statusLine);
			
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		 try{
				http.get(path : "/inventory/collection/dataset/$id", contentType: ContentType.XML){ resp, xml ->
				log.info "http - GET - /inventory/collection/dataset/$id"
				if(resp.getStatus() != 200)
					{
						log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
						return;
					}
					log.debug("Parsing collection.")
				return Transformer.parseCollection(xml);
				}
		}catch(HttpResponseException ex){
			ex.printStackTrace()
			log.debug "404- dataset not found"
			return null
		}
	}
	
	public List<Collection> listCollections() throws InventoryException{
		http.handler.failure = { resp ->
			log.debug "Service error processing Collection List"
			log.debug(resp.statusLine);
			
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		try{
				http.get(path : "/inventory/collections", contentType: ContentType.XML){ resp, xml ->
				log.info "http - GET - /inventory/collections"
				if(resp.getStatus() != 200)
					{
						log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
						return;
					}
					log.debug("Parsing collection list.")
					def colList = []
					
					xml.collection.each{
						Collection c = new Collection()
						c.setCollectionId(it.id.text().toInteger())
						c.setShortName(it.shortName.text())
						colList.add(c)
					}
					
					
					return colList
				}
		}catch(HttpResponseException ex){
			ex.printStackTrace()
			log.debug "404- dataset not found"
			return null
		}
		
	}
	
	public List<BigDecimal> getEchoGranulesByDataset(Integer datasetId) throws InventoryException{
		return getEchoGranulesByDataset("$datasetId");
	}
	public List<BigDecimal> getEchoGranulesByDataset(String datasetId) throws InventoryException{
		http.handler.failure = { resp ->
			log.debug "Service error processing Echo Granule List"
			log.debug(resp.statusLine);
			
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		try{
				http.get(path : "/inventory/dataset/$datasetId/echo", contentType: ContentType.XML){ resp, xml ->
				log.info "http - GET - /inventory/dataset/$datasetId/echo"
				if(resp.getStatus() != 200)
					{
						log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
						return;
					}
					log.debug("Parsing Echo list.")
					def granList = []
					
					xml.bigDecimal.each{
						granList.add(new BigDecimal(it.text()))
					}
					
					
					return granList
				}
		}catch(HttpResponseException ex){
			ex.printStackTrace()
			log.debug "404- dataset not found"
			return null
		}
	}
	
	
	/*
	 * Contact API
	 */
	
	public Contact getContactByID(Integer contactId) throws InventoryException{
		http.handler.failure = { resp ->
			log.debug "Service error processing Contact Request"
			log.debug(resp.statusLine);
			
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		try{
				http.get(path : "/inventory/contact/$contactId", contentType: ContentType.XML){ resp, xml ->
				log.info "http - GET - /inventory/contact/$contactId"
				if(resp.getStatus() != 200)
					{
						log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
						return;
					}
					log.debug("Parsing contact.")
				return Transformer.parseContact(xml);
				}
		}catch(HttpResponseException ex){
			ex.printStackTrace()
			log.debug "404- dataset not found"
			return null
		}
	}
	
	/*
	 * ELEMENT API
	 */
	public ElementDD getElementDDByShortName(String elementId) throws InventoryException{
		return getElement(elementId)
	}
	
	public GranuleSpatial getSpatial(Integer gId) throws InventoryException {
		http.handler.failure = { resp ->
			log.debug "Service error processing Element Request"
			log.debug(resp.statusLine);
			
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		try{
				http.get(path : "/inventory/granule/$gId/spatial", contentType: ContentType.XML){ resp, xml ->
				log.info "http - GET - /inventory/granule/$gId/spatial"
				if(resp.getStatus() != 200)
					{
						log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
						return;
					}
					log.debug("Parsing element.")
				return Transformer.parseGranuleSpatial(xml);
				}
		}catch(HttpResponseException ex){
			ex.printStackTrace()
			log.debug "404- dataset not found"
			return null
		}
		
		
	}
	
	
	public ElementDD getElementDDById(Integer elementId) throws InventoryException{
		return getElement("$elementId")
	}
	
	public ElementDD getElement(String elementId) throws InventoryException{
		http.handler.failure = { resp ->
			log.debug "Service error processing Element Request"
			log.debug(resp.statusLine);
			
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		try{
				http.get(path : "/inventory/element/$elementId", contentType: ContentType.XML){ resp, xml ->
				log.info "http - GET - /inventory/element/$elementId"
				if(resp.getStatus() != 200)
					{
						log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
						return;
					}
					log.debug("Parsing element.")
				return Transformer.parseElement(xml);
				}
		}catch(HttpResponseException ex){
			ex.printStackTrace()
			log.debug "404- dataset not found"
			return null
		}
	}
	
	/*
	 * DATASET API
	 */
	
	public Dataset getDataset(Integer dataset) throws InventoryException{
		log.debug "getDataset by integer: $dataset"
		return getDataset("$dataset")
	}
	public Dataset getDataset(String dataset){
		http.handler.failure = { resp ->
			log.debug "Service error processing Dataset Request"
			log.debug(resp.statusLine);
			
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		try{
				http.get(path : "/inventory/dataset/$dataset", contentType: ContentType.XML){ resp, xml ->
				log.info "http - GET - /inventory/dataset/$dataset"
				if(resp.getStatus() != 200)
					{
						log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
						return;
					}
					log.debug("Parsing dataset.")
				return Transformer.parseDataset(xml);
				}
		}catch(HttpResponseException ex){
			ex.printStackTrace()
			log.debug "404- dataset not found"
			return null
		}
	}
	
	public Integer getLatestGranuleIdByDataset(Integer id) throws InventoryException{
		return getLatestGranuleIdByDataset("$id")
	}
	
	public Integer getLatestGranuleIdByDataset(String id) throws InventoryException{
		//inventory/dataset/15/latestGranule
		Integer x = null;
		http.handler.failure = { resp ->
			log.debug "Service error processing latest Granule"
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		try{
				http.get(path : "/inventory/dataset/$id/latestGranule", contentType: ContentType.XML){ resp, xml ->
				log.debug "http - GET - /inventory/dataset/$id/latestGranule"
				if(resp.getStatus() != 200)
					{
						log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
						return;
					}
				xml."int".each{
					log.debug "Value:  ${it.text()}"
				 x = it[0].text().toInteger();
		
				}
			}
			return x
		}catch(HttpResponseException ex){
			log.debug "404- dataset not found"
			return null
		}
	}
	
	public DatasetPolicy getDatasetPolicy(Integer id) throws InventoryException{
		return getDatasetPolicy("$id")
	}
	
	public DatasetPolicy getDatasetPolicy(String id) throws InventoryException{
		http.handler.failure = { resp ->
			log.debug "Service error processing dataset policy "
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
			try{
				http.get(path : "/inventory/dataset/$id/policy", contentType: ContentType.XML){ resp, xml ->
				log.debug "http - GET - /inventory/dataset/$id/policy"
				if(resp.getStatus() != 200)
					{
						log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
						return;
					}
				return Transformer.parseDatasetPolicy(xml)
				}
		} catch(HttpResponseException ex){
			log.debug "404- dataset not found"
			return null
		}
	}
	
	public DatasetCoverage getDatasetCoverage(Integer id) throws InventoryException{
		return getDatasetCoverage("$id")	
	}
	
	public DatasetCoverage getDatasetCoverage(String id) throws InventoryException{
		http.handler.failure = { resp ->
			log.debug "Service error processing  Dataset Coverage"
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		
		try{
				http.get(path : "/inventory/dataset/$id/coverage", contentType: ContentType.XML){ resp, xml ->
				log.debug "http - GET - /inventory/dataset/$id/coverage"
				if(resp.getStatus() != 200)
					{
						log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
						return;
					}
				return Transformer.parseDatasetCoverage(xml);
			}
		}catch(HttpResponseException ex){
			log.debug "404- dataset not found"
			return null
		}
	}
		
	public List<Map<String,String>> getDatasets() throws InventoryException{

		http.handler.failure = { resp ->
			log.debug "Service error processing  Datasets"
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		
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
	
	public Integer getGranuleSize(Integer dId) throws InventoryException{
		return getGranuleSize("$dId");
	}
	
	public Integer getGranuleSize(String dId) throws InventoryException{
		http.handler.failure = { resp ->
			log.debug "Service error processing  Dataset Coverage"
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		
		try{
				// i already removed the mp variable in this call.
				http.get(path : "/inventory/dataset/$dId/granuleSize", contentType: ContentType.XML ){ resp, xml ->
				log.debug "http - GET - /inventory/dataset/$dId/granuleSize"
				def x;
				if(resp.getStatus() != 200)
					{
						log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
						return;
					}
				xml."int".each{
					log.debug "Value:  ${it.text()}"
				 x = it[0].text().toInteger();
				}
				return x;
			}
		}catch(HttpResponseException ex){
			log.debug "404- dataset not found"
			return null
		}

	}
	
	/*
	 * Granule API
	 * 
	 */
	
	public List<Integer> getGranuleIdListByDateRange(long begin, long stop,
		String timefield) throws InventoryException{
		//simply return the enxt methods call with a null datasetId
		return getGranuleIdListByDateRange(begin,stop,timefield,null)
		
	}
	public List<Integer>getGranuleIdListByDateRange(long begin, long stop,
		String timefield, Integer dsId) throws InventoryException{
		List<Integer> ll = null;
		
		http.handler.failure = { resp ->
			log.debug "Service error processing  GetGranuleIdList"
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		
		try{
			def mp = [:];
			mp.putAt("start", begin);
			mp.putAt("stop", stop)
			mp.putAt("compareField", timefield)
			if(dsId)
				mp.putAt("dataset", dsId)
			
				http.get(path : "/inventory/granules", contentType: ContentType.XML, query: mp){ resp, xml ->
					if(resp.getStatus() != 200)
					{
						log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
						return;
					}
					ll = new ArrayList<Integer>();
					xml.children().each {
						//log.debug("GranuleId: ${it.text()}")
						ll.add(Integer.valueOf(it.text()));
					}
				}
		}catch(HttpResponseException ex){
			log.debug "404- dataset not found"
			return null
		}
		
		return ll;
	}
	
	public Map<String,Object> getGranuleIdList(Integer dId, Calendar start,
		Calendar stop, String pattern, Integer page) throws InventoryException{
		List<Integer> ll = null;
		def map = [:];
		
		http.handler.failure = { resp ->
			log.debug "Service error processing  Dataset Coverage"
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		
		try{
				def mp = [:];
				mp.putAt("pattern", pattern)
				mp.putAt("page", page)
				if(start != null)
					mp.putAt("startTime", start.getTimeInMillis())
				if(stop != null)
					mp.putAt("stopTime", stop.getTimeInMillis())
				
				http.get(path : "/inventory/dataset/$dId/granuleList", contentType: ContentType.XML, query: mp){ resp, xml ->
				log.debug "http - GET - /inventory/dataset/$dId/granuleList"
				if(resp.getStatus() != 200)
					{
						log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
						return;
					}
				ll = new ArrayList<Integer>();
					xml.children().each {
						//log.debug("GranuleId: ${it.text()}")
						ll.add(Integer.valueOf(it.text()));
					}
					map.put("page",Integer.valueOf(xml.@page.text()))
					map.put("numPages",Integer.valueOf(xml.@numPages.text()))
					map.put("items", ll);
					
					return map;
			}
		}catch(HttpResponseException ex){
			log.debug "404- dataset not found"
			return null
		}
		
		return map;
	
	}
	
	public List<Integer> findGranuleList(List<Integer> idList) throws InventoryException{
		
		//convert list to strings.
		StringBuilder sb = new StringBuilder("");
		boolean first = true;
		for(Integer i : idList ){
			if(first){
				sb = sb.append("$i")
				first = false;
				}
			else
				sb = sb.append(",$i")
		}
		String listing = sb.toString()
		log.debug("ids: $listing");
		
		http.handler.failure = { resp ->
			log.debug "Service error processing findGranuleList"
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		try{
				log.debug "http - GET - /inventory/granules/listById?ids=$listing"
				http.get(path : "/inventory/granules/listById", contentType: ContentType.XML,  query : [ids:listing]){ resp, xml ->
				
				if(resp.getStatus() != 200)
					{
						log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
						return;
					}
//				return Transformer.parseGranuleRefs(xml)
					List<Integer> ll = new ArrayList<Integer>();
					xml.children().each {
						log.debug("GranuleId: ${it.text()}")
						ll.add(Integer.valueOf(it.text().trim()));
					}
					return ll;
				}
		} catch(HttpResponseException ex){
			log.debug "404 - granule not found"
			return null
		}
	}
	
	public List<GranuleReference> getGranuleAllReferences(String datasetId) throws InventoryException{
		return getGranuleAllReferences("$datasetId")
	}
	
	
	public List<Integer> getGranuleIdListAll(Integer datasetId, Calendar start, Calendar stop, String pattern) throws InventoryException{
		def mp = getGranuleIdList(datasetId,start,stop,pattern,1)
		def page,numpage,lst;
		if(mp!=null){
			page = Integer.valueOf(mp.getAt("page"));
			numpage = Integer.valueOf(mp.getAt("numPages"));
			lst = (List<Integer>)mp.getAt("items");
		}
		
		page++; //we already got page one
		
		for(page; page<=numpage; page++){
			log.debug("fetching $page of $numpage");
			def tMap  = getGranuleIdList(datasetId,start,stop,pattern,page);
			def lst2 = (List<Integer>)tMap.getAt("items");
			lst.addAll(lst2)
		}
		
		return lst;
	}
	
	public List<GranuleReference> getGranuleAllReferences(Integer datasetId) throws InventoryException
	{
		def mp = getGranuleReferences(datasetId,1)
		def page,numpage,lst;
		if(mp!=null){
			page = Integer.valueOf(mp.getAt("page"));
			numpage = Integer.valueOf(mp.getAt("numPages"));
			lst = (List<GranuleReference>)mp.getAt("refs");
		}
		
		page++; //we already got page one
		
		for(page; page<=numpage; page++){
			log.debug("fetching $page of $numpage");
			def tMap  = getGranuleReferences(datasetId,page);
			def lst2 = (List<GranuleReference>)tMap.getAt("refs");
			lst.addAll(lst2)
		}
		
		return lst;
		
	}
	
	public Map<String,Object> getGranuleReferences(Integer dId,Integer page){
		return getGranuleReferences("$dId", page);
	}	
	
	public Map<String,Object> getGranuleReferences(String dId, Integer page){
		List<GranuleReference> gr = null;
		def map = [:];
		http.handler.failure = { resp ->
			log.debug "Service error processing getGranuleReferences"
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		try{
				http.get(path : "/inventory/dataset/$dId/granuleReferences", contentType: ContentType.XML, query:[page:page]){ resp, xml ->
				log.debug "http - GET - /inventory/dataset/$dId/granuleReferences?page=$page"
				if(resp.getStatus() != 200)
					{
						log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
						return null;
					}
		
					log.debug "Xml page: " + xml.@page
					log.debug "Xml numPage: " + xml.@numPages
					log.debug "Function Page: " + page
					
					map.put("page",Integer.valueOf(xml.@page.text()))
					map.put("numPages",Integer.valueOf(xml.@numPages.text()))
					
					gr =  Transformer.parseGranuleRefs(xml) //fix to pull multiple pages.
					map.put("refs", gr);	
					
					return map	
				}
		} catch(HttpResponseException ex){
//			log.debug "404 - Error not found"
			log.debug("Error: " + ex.getMessage());
			return null
		}
		
		return map;
	}	
	
	public List<Integer> getGranulesByDatasetAndPattern(Integer id, String gnp){
		
			return getGranulesByDatasetAndPattern("$id",gnp);
		}
	
	public List<Integer> getGranulesByDatasetAndPattern(String id, String gnp){
		http.handler.failure = { resp ->
			log.debug "Service error processing getGranulesByDatasetAndPattern"
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }

		def mp = [:]
		mp.putAt("pattern", gnp)
		
		try{
				http.get(path : "/inventory/dataset/$id/granuleList", query: mp, contentType: ContentType.XML){ resp, xml ->
				log.debug "http - GET - /inventory/dataset/$id/granules?pattern=$gnp"
				if(resp.getStatus() != 200)
					{
						log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
						return;
					}
				return Transformer.parseGranuleIds(xml)
				}
		} catch(HttpResponseException ex){
			log.debug "404 - granule not found"
			return null
		}

	}
	
	public boolean updateGranule(Granule g ) throws InventoryException{
		return updateGranule(g, this.user, this.pass);
	}
	public boolean updateGranule(Granule g, String userName, String password) throws InventoryException{
		//updateGranule
		log.debug "http - POST - /inventory/granule/${g.getGranuleId()}"
		//render granule as XML
		String gXml = Transformer.renderUpdateGranule(g);
		def postBody = [granule:"$gXml",userName:"$userName", password:"$password"]
		http.handler.failure = { resp ->
			log.debug "Service error processing  update granule request"
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		try{
			http.post(path : "/inventory/granule/${g.getGranuleId()}", body:postBody, requestContentType: URLENC){ resp ->
				if(resp.getStatus() != 200)
				{
					log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
					return false;
				}
				log.debug "Granule updated..."
				return true;
			}
		} catch(HttpResponseException ex){
			log.debug "404 - granule not found"
			return false
		}
	}

	public boolean updateGranuleEcho(Granule g, long time ) throws InventoryException{
                return updateGranuleEcho(g, time, this.user, this.pass);
        }
        public boolean updateGranuleEcho(Granule g, long time,String userName, String password) throws InventoryException{
                //updateGranule
                log.debug "http - POST - /inventory/granule/${g.getGranuleId()}/gmh/echo"
                //render granule as XML
                def postBody = [echoTime:time,userName:"$userName", password:"$password"]
                http.handler.failure = { resp ->
                        log.debug "Service error processing  update granule request"
                        throw new InventoryException(resp.getEntity().getContent().getText());
                  }
                try{
                        http.post(path : "/inventory/granule/${g.getGranuleId()}/gmh/echo", body:postBody, requestContentType: URLENC){ resp ->
                                if(resp.getStatus() != 200)
                                {
                                        log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
                                        return false;
                                }
                                log.debug "Granule updated..."
                                return true;
                        }
                } catch(HttpResponseException ex){
                        log.debug "404 - granule not found"
                        return false
                }
        }


	
	
	public boolean updateGranuleStatus(int granuleId, String status) throws InventoryException{
		return updateGranuleStatus(granuleId, status, this.user, this.pass)
	}
	
	public boolean updateGranuleStatus(int granuleId, String status, String userName, String password) throws InventoryException{
		//http://localhost:8080/inventory/granule/55/status/?status=ONLINE&userName=axt&password=axt388
		log.debug "http - POST - /inventory/granule/$granuleId/status/?status=$status&userName=$userName&password=$password"
		def postBody = [status:"$status",userName:"$userName", password:"$password"]
		http.handler.failure = { resp ->
			log.debug "Service error processing  update granule request"
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		try{
			http.post(path : "/inventory/granule/$granuleId/status/", body:postBody, requestContentType: URLENC){ resp ->
				if(resp.getStatus() != 200)
				{
					log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
					return false;
				}
				log.debug "Status updated..."
				return true;
			}
		} catch(HttpResponseException ex){
			log.debug "404 - granule not found"
			return false
		}
	}
	
	public boolean deleteGranule(Granule granule, boolean deleteDataOnly) throws InventoryException{
		
		try{
			http.request(baseUrl ,DELETE, ANY ) { req ->
				uri.path = "/inventory/granule/"+granule.getGranuleId()
				log.debug("checking: " + baseUrl + "/inventory/granule/"+granule.getGranuleId());
				
				uri.query = [ dataOnly:deleteDataOnly,userName:this.user, password:this.pass ]
				
				response.success = { resp, reader ->
					log.debug("successfully deleted granule");
					return true;
				  }
				response.failure = { resp->
					log.debug("in here...");
					log.debug("failed to delete granule.");
					log.debug "Unexpected failure: ${resp.statusLine}"
					return false;
				}
			}
		}catch(HttpResponseException ex){
			log.debug "404 - granule not found"
			return null
		}
	}
	//Update (usually set to online) AIP
	public boolean updateGranuleAIPReference(Integer granuleId, String type, String destination, String name, String status){
		log.debug "http - POST - /inventory/granule/$granuleId/aipReference?status=$status&userName=$user&password=$pass&type=$type&dest=$destination&name=$name"
		def postBody = [
			type:"$type",
			dest:destination,
			fname:name,
			status:"$status",
			userName:"$user", 
			password:"$pass"
			]
		http.handler.failure = { resp ->
			log.debug "Service error processing  update granule request"
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		try{
			http.post(path : "/inventory/granule/$granuleId/aipReference", body:postBody, requestContentType: URLENC){ resp ->
				if(resp.getStatus() != 200)
				{
					log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
					return false;
				}
				log.debug "Status updated..."
				return true;
			}
		} catch(HttpResponseException ex){
			log.debug "404 - granule not found"
			return false
		}	
	}
	
	public boolean updateGranuleAIPArchive(Integer granuleId, String type, String destination, String name, String status){
		log.debug "http - POST - /inventory/granule/$granuleId/aipArchive?status=$status&userName=$user&password=$pass&type=$type&dest=$destination&name=$name"
		def postBody = [
			type:"$type",
			dest:destination,
			fname:name,
			status:"$status",
			userName:"$user",
			password:"$pass"
			]
		http.handler.failure = { resp ->
			log.debug "Service error processing  update granule request"
			log.debug("AIP Archive Error");
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		try{
			http.post(path : "/inventory/granule/$granuleId/aipArchive", body:postBody, requestContentType: URLENC){ resp ->
				if(resp.getStatus() != 200)
				{
					log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
					return false;
				}
				log.debug "Status updated..."
				return true;
			}
		} catch(HttpResponseException ex){
			log.debug "404 - granule not found"
			return false
		}
	}
	
	public boolean updateGranuleArchiveChecksum(Integer gId, String name, String cSum) throws InventoryException{
		log.debug "http - POST - /inventory/granule/$gId/archive/checksum?name=$name&checksum=$cSum&userName=$user&password=$pass"
		def postBody = [
			name:name,
			checksum:"$cSum",
			userName:"$user",
			password:"$pass"
			]
		
		http.handler.failure = { resp ->
			log.debug "Service error processing  update granule request"
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		try{
			http.post(path : "/inventory/granule/$gId/archive/checksum", body:postBody, requestContentType: URLENC){ resp ->
				if(resp.getStatus() != 200)
				{
					log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
					return false;
				}
				log.debug "checksum updated..."
				return true;
			}
		}catch(HttpResponseException ex){
			log.debug "404 - granule not found"
			return false
		}
	}
	
	public boolean updateStatusAndVerify(Integer gId, String status) throws InventoryException{
		log.debug "http - POST - /inventory/granule/$gId/statAndVerify?&status=$status&userName=$user&password=$pass"
		def postBody = [status:"$status",userName:"$user",password:"$pass"]
		http.handler.failure = { resp ->
			log.debug "Service error processing  update granule request"
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		try{
			http.post(path : "/inventory/granule/$gId/statAndVerify", body:postBody, requestContentType: URLENC){ resp ->
				if(resp.getStatus() != 200)	{
					log.info("Status returned non-200 code: ${resp.getStatus()}; returning");return false;	}
				log.debug "status updated..."
				return true;
			}
		} catch(HttpResponseException ex){
			log.debug "404 - granule not found";return false}
	}
	
	public boolean deleteGranuleLocalReference(Integer gId, String type) throws InventoryException{
		try{
			http.request(baseUrl ,DELETE, ANY ) { req ->
				uri.path = "/inventory/granule/$gId/reference/local"
				uri.query = [ type:type,userName:this.user, password:this.pass ]
				log.debug("checking: " + baseUrl + "/granule/$gId/reference/local");
				
				response.success = { resp, reader ->
					log.debug("successfully deleted granule reference");
					return true;
				  }
				response.failure = { resp->
					log.debug("failed to delete granule.");
					log.debug "Unexpected failure: ${resp.statusLine}"
					log.debug("Entity: "+resp.getEntity().getContent().getText())
					throw new InventoryException(resp.getEntity().getContent().getText());
				}
			}
		}catch(HttpResponseException ex){
			throw new InventoryException(ex.getMessage());
		}
		
	}
	
	public boolean addGranuleReference(Integer gId, String path, String status, String type, String desc) throws InventoryException{
		log.debug "http - POST - /inventory/granule/$gId/reference/newReference?path=$path&status=status&type=$type&desc=$desc&userName=$user&password=****"
		def postBody = [path:"$path",status:status, type:type, desc:desc,userName:"$user",password:"$pass"]
		http.handler.failure = { resp ->
			log.debug "Service error processing  update granule request"
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		try{
			http.post(path : "/inventory/granule/$gId/reference/newReference", body:postBody, requestContentType: URLENC){ resp ->
				if(resp.getStatus() != 200)	{
					log.info("Status returned non-200 code: ${resp.getStatus()}; returning");return false;	}
				log.debug "reference added..."
				return true;
			}
		} catch(HttpResponseException ex){
			log.debug "404 - granule not found";return false}
	}
	
	
	
	public boolean updateGranuleReassociate(Integer gId, String rootpath, Integer dataset, String accessType) throws InventoryException{
		updateGranuleReassociate(gId, rootpath, "$dataset", accessType);
	}
	public boolean updateGranuleReassociate(Integer gId, String rootpath, String dataset, String accessType) throws InventoryException{
		log.debug "http - POST - /inventory/granule/$gId/granule/reassociate?rootpath=$rootpath&dataset=$dataset&userName=$user&password=$pass"
		def postBody = [rootpath:"$rootpath",dataset:dataset,accessType:accessType, userName:"$user",password:"$pass"]
		http.handler.failure = { resp ->
			log.debug "Service error processing  update granule request"
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		try{
			http.post(path : "/inventory/granule/$gId/granule/reassociate", body:postBody, requestContentType: URLENC){ resp ->
				if(resp.getStatus() != 200)	{
					log.info("Status returned non-200 code: ${resp.getStatus()}; returning");return false;	}
				log.debug "Granule reassociated - basepath, datasetID updated..."
				return true;
			}
		} catch(HttpResponseException ex){
			log.debug "404 - granule not found";return false}
	}
	
	public boolean updateGranuleReassociateElement(Integer gId, Integer fromD, Integer toD) throws InventoryException{
		log.debug "http - POST - /inventory/granule/$gId/granule/element/reassociate?from=$fromD&to=$toD&userName=$user&password=$pass"
		def postBody = [to:"$toD",from:fromD,userName:"$user",password:"$pass"]
		http.handler.failure = { resp ->
			log.debug "Service error processing  update granule request"
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		try{
			http.post(path : "/inventory/granule/$gId/granule/element/reassociate", body:postBody, requestContentType: URLENC){ resp ->
				if(resp.getStatus() != 200)	{
					log.info("Status returned non-200 code: ${resp.getStatus()}; returning");return false;	}
				log.debug "Granule elements reassociated..."
				return true;
			}
		} catch(HttpResponseException ex){
			log.debug "404 - granule not found";return false}
	}
	
	public boolean updateGranuleReferencePath(Integer gId, String path, String newPath) throws InventoryException{
		log.debug "http - POST - /inventory/granule/$gId/reference/path?path=$path&newpath=$newPath&userName=$user&password=$pass"
		def postBody = [path:"$path",newpath:newPath,userName:"$user",password:"$pass"]
		http.handler.failure = { resp ->
			log.debug "Service error processing  update granule request"
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		try{
			http.post(path : "/inventory/granule/$gId/reference/path", body:postBody, requestContentType: URLENC){ resp ->
				if(resp.getStatus() != 200)	{
					log.info("Status returned non-200 code: ${resp.getStatus()}; returning");return false;	}
				log.debug "Granule Reference path updated..."
				return true;
			}
		} catch(HttpResponseException ex){
			log.debug "404 - granule not found";return false}
	}
	
	public boolean updateGranuleReferenceStatus(Integer gId, String path, String status) throws InventoryException{
		log.debug "http - POST - /inventory/granule/$gId/reference/status?path=$path&status=$status&userName=$user&password=$pass"
		def postBody = [path:"$path",status:status,userName:"$user",password:"$pass"]
		http.handler.failure = { resp ->
			log.debug "Service error processing  update granule request"
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		try{
			http.post(path : "/inventory/granule/$gId/reference/status", body:postBody, requestContentType: URLENC){ resp ->
				if(resp.getStatus() != 200)	{
					log.info("Status returned non-200 code: ${resp.getStatus()}; returning");return false;	}
				log.debug "Granule Reference status updated..."
				return true;
			}
		} catch(HttpResponseException ex){
			log.debug "404 - granule not found";return false}
	}
	
	public boolean updateGranuleRootPath(Integer gId, String path) throws InventoryException{
		log.debug "http - POST - /inventory/granule/$gId/granule/rootpath?rootpath=$path&userName=$user&password=$pass"
		def postBody = [rootpath:"$path",userName:"$user",password:"$pass"]
		http.handler.failure = { resp ->
			log.debug "Service error processing  update granule request"
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		try{
			http.post(path : "/inventory/granule/$gId/granule/rootpath", body:postBody, requestContentType: URLENC){ resp ->
				if(resp.getStatus() != 200)	{
					log.info("Status returned non-200 code: ${resp.getStatus()}; returning");return false;	}
				log.debug "Granule RootPath updated..."
				return true;
			}
		} catch(HttpResponseException ex){
			log.debug "404 - granule not found";return false}
	}
	
	public boolean updateGranuleArchiveStatus(Integer gId, String status) throws InventoryException{
		log.debug "http - POST - /inventory/granule/$gId/archive/status?&status=$status&userName=$user&password=$pass"
		def postBody = [
			status:"$status",
			userName:"$user",
			password:"$pass"
			]
		http.handler.failure = { resp ->
			log.debug "Service error processing  update granule request"
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		try{
			http.post(path : "/inventory/granule/$gId/archive/status", body:postBody, requestContentType: URLENC){ resp ->
				if(resp.getStatus() != 200)
				{
					log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
					return false;
				}
				log.debug "Archive Status updated..."
				return true;
			}
		} catch(HttpResponseException ex){
			log.debug "404 - granule not found"
			return false
		}
		
	}
	public boolean updateGranuleArchiveSize(Integer gId, String name, Long fsize) throws InventoryException{
		log.debug "http - POST - /inventory/granule/$gId/archive/size?name=$name&fsize=$fsize&userName=$user&password=$pass"
		def postBody = [
			name:name,
			fsize:"$fsize",
			userName:"$user",
			password:"$pass"
			]
		http.handler.failure = { resp ->
			log.debug "Service error processing  update granule request"
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		try{
			http.post(path : "/inventory/granule/$gId/archive/size", body:postBody, requestContentType: URLENC){ resp ->
				if(resp.getStatus() != 200)
				{
					log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
					return false;
				}
				log.debug "fsize updated..."
				return true;
			}
		} catch(HttpResponseException ex){
			log.debug "404 - granule not found"
			return false
		}
	}
	
	
	//returns XML
	public Granule getGranuleById(int id) throws InventoryException{
		log.debug("Get granule by id $id");
		return getGranuleByName("$id")
	}
	
	public Set<GranuleMetaHistory> getGMH(Integer id) throws InventoryException{
		return getGMH("$id");
	}
	
	public Set<GranuleMetaHistory> getGMH(String id) throws InventoryException{
		http.handler.failure = { resp ->
			log.debug "Service error processing granuleByName"
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		try{
				http.get(path : "/inventory/granule/$id/gmh", contentType: ContentType.XML){ resp, xml ->
				log.debug "http - GET - /inventory/granule/$id/gmh"
				if(resp.getStatus() != 200)
					{
						log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
						return;
					}
				return Transformer.parseGranuleMetaHistory(xml)
				}
		} catch(HttpResponseException ex){
			log.debug "404 - granule not found"
			return null
		}
	}
	
	//returns XML
	public Granule getGranuleByName(String id) throws InventoryException{
		http.handler.failure = { resp ->
			log.debug "Service error processing granuleByName"
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		try{
				http.get(path : "/inventory/granule/$id", contentType: ContentType.XML){ resp, xml ->
				log.debug "http - GET - /inventory/granule/$id"
				if(resp.getStatus() != 200)
					{
						log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
						return;
					}
				Granule g = Transformer.parseGranule(xml)
				return g;
				}
		} catch(HttpResponseException ex){
			log.debug "404 - granule not found"
			return null
		}
		
	}
	
	//returns XML
	public Granule getGranuleByNameAndDataset(String id, int datasetId) throws InventoryException{
		return getGranuleByNameAndDataset(id,"$datasetId")
	}
	
	//returns XML
	public Granule getGranuleByNameAndDataset(String id, String datasetId) throws InventoryException{
		//?dataset=46
		log.debug "http - GET - /inventory/granule/$id?dataset=$datasetId"
		http.handler.failure = { resp ->
			log.debug "Service error processing granule By Name and Dataset"
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		try { http.get(path : "/inventory/granule/$id", contentType: ContentType.XML, query : [dataset:"$datasetId"]){ resp, xml ->
			if(resp.getStatus() != 200)
				{
					log.info("Status returned non-200 code: ${resp.getStatus()}; returning")
					return;
				}
			return Transformer.parseGranule(xml)
		}
		} catch(HttpResponseException ex){
			log.debug "404 - granule not found"
			return null
		}
	}
	
	public String getGranuleArchivePath(Integer id) throws InventoryException{
		return getGranuleArchivePath("$id")
	}
	
	public String getGranuleArchivePath(String id) throws InventoryException{
		http.handler.failure = { resp ->
			log.debug "Service error processing granule archive path"
			throw new InventoryException(resp.getEntity().getContent().getText());
		  }
		try {
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
		} catch(HttpResponseException ex){
			log.debug "404 - granule not found"
			return null
		}
	}
}
