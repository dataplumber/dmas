package gov.nasa.podaac.distribute.echo;

import gov.nasa.podaac.distribute.common.DistributeValidationEventHandler;
import gov.nasa.podaac.distribute.echo.jaxb.collection.Collection;
import gov.nasa.podaac.distribute.echo.jaxb.collection.ObjectFactory;
import gov.nasa.podaac.distribute.echo.jaxb.granule.Granule;
import gov.nasa.podaac.distribute.echo.jaxb.granule.GranuleMetaDataFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;











import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ECHORestClient {

	private static Log log = LogFactory.getLog(ECHORestClient.class);
	
	String username = null;
    String password = null;
	String echoHost = null;
    String tokenHost = null;
    
    String provider = "PODAAC";
    DefaultHttpClient httpClient  = null;
    String token = null;
    static boolean CMR = true;
	
 
  private void send(String url, HttpEntity entity) throws ClientProtocolException, IOException{
	  HttpPut httpPut = new HttpPut(url);
	  httpPut.setEntity(entity);
	  String tkn = null;
	try {
		tkn = getToken(this.username, this.password);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		//ERROR getting token.
		throw new IOException("Could not retrieve token...");
	}
	  //Set the Echo-Token Header
	httpPut.setHeader("Echo-Token", tkn );
	
	// Send the request
	HttpResponse response = httpClient.execute(httpPut);
	
	// Check for a successful response
	int responseCode = response.getStatusLine().getStatusCode();
	
	log.debug("CMR Response: " + responseCode);
	if (responseCode == 201)
	{
	  log.debug("object created");
	}
	else if (responseCode == 200)
	{
		log.debug("object updated");
	}
	else
	{
	  String body = readInputStream(response.getEntity().getContent());
	  log.error("Unexpected response code["+responseCode+"]: " + body);
	  throw new IOException("Error submitting entry to CMR.");
	}
	httpPut.releaseConnection();
  }
  
  private void sendGranule(String granuleUr, File file) throws Exception {
	  
	    String url = echoHost + "/providers/"
	        + provider + "/granules/" + granuleUr;

	    // Set the body of the request to the granule1.xml file
	    //FileEntity requestEntity = new FileEntity(new File( granuleFile  ));
	    FileEntity requestEntity = new FileEntity(file);
	    
	    // Set the ContentType header
	    requestEntity.setContentType("application/echo10+xml");
	    send(url, requestEntity);
	  
}
  
  
  public void sendGranule(String datasetId,
			gov.nasa.podaac.distribute.echo.jaxb.granule.Granule echoGranule) throws IOException {
		// TODO Auto-generated method stub
	  String url = echoHost + "providers/"
		        + provider + "/granules/" + URLEncoder.encode(echoGranule.getGranuleUR());
		  	log.debug(url);
		  	StringEntity requestEntity;
		  	try {
		  		requestEntity = new StringEntity(toXML(echoGranule));
		  		requestEntity.setContentType("application/echo10+xml");
			    send(url, requestEntity);
			    
		  	} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  	catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
  
  public void sendDataset(Collection ecf) throws IOException {
	  	String url = echoHost + "providers/"
	        + provider + "/collections/" + URLEncoder.encode(ecf.getDataSetId());
	  	log.debug(url);
	  	StringEntity requestEntity;
	  	try {
	  		requestEntity = new StringEntity(toXML(ecf));
	  		requestEntity.setContentType("application/echo10+xml");
		    send(url, requestEntity);
		    
	  	} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  	catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  }
  
  private void sendDataset(String dataset, File file) throws IOException {
	  	String url = null;
	  	if(CMR){
	  		url = echoHost + "providers/"
	        + provider + "/collections/" + URLEncoder.encode(dataset);
	  	}
	  	else{ url = echoHost + "providers/"
		        + provider + "/datasets/" + URLEncoder.encode(dataset);
	    
	  	}
	    FileEntity requestEntity = new FileEntity(file);
	    requestEntity.setContentType("application/echo10+xml");
	    send(url, requestEntity);
}

  public ECHORestClient(String user, String pass, String host) {
	  this.username = user; 
	  this.password = pass;
	  
	  try {
		this.httpClient =  httpClientTrustingAllSSLCerts();
	  } catch (KeyManagementException e) {
		e.printStackTrace();
	  } catch (NoSuchAlgorithmException e) {
		e.printStackTrace();
	  }
	  this.echoHost = host;
  }
  
  public ECHORestClient() {
	  try {
		this.httpClient =  httpClientTrustingAllSSLCerts();
	  } catch (KeyManagementException e) {
		e.printStackTrace();
	  } catch (NoSuchAlgorithmException e) {
		e.printStackTrace();
	  }
  }
  
  private DefaultHttpClient httpClientTrustingAllSSLCerts() throws NoSuchAlgorithmException, KeyManagementException {
      DefaultHttpClient httpclient = new DefaultHttpClient();

      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, getTrustingManager(), new java.security.SecureRandom());

      SSLSocketFactory socketFactory = new SSLSocketFactory(sc);
      Scheme sch = new Scheme("https", 443, socketFactory);
      httpclient.getConnectionManager().getSchemeRegistry().register(sch);
      return httpclient;
  }
  private TrustManager[] getTrustingManager() {
      TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
          @Override
          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
              return null;
          }

          @Override
          public void checkClientTrusted(X509Certificate[] certs, String authType) {
              // Do nothing
          }

          @Override
          public void checkServerTrusted(X509Certificate[] certs, String authType) {
              // Do nothing
          }

      } };
      return trustAllCerts;
  } 
  /**
   * Logs in and returns a token.
   * 
   * @return the token.
   * @throws Exception
   */
  private String getToken(String username, String password) throws Exception
  {
	  
	if(this.token != null ){
		return this.token;
	}
	
    HttpPost httpPost = new HttpPost(tokenHost);
    String login = getLoginPostBody(username,password); 
    //System.out.println(login);
    
    StringEntity requestEntity = new StringEntity(login);
    requestEntity.setContentType("application/xml");
    httpPost.setEntity(requestEntity);

    HttpResponse response = httpClient.execute(httpPost);
    String tkn = null;
    try
    {
      String xmlResponse = readInputStream(response.getEntity().getContent());
      log.debug(xmlResponse);
      // Parse out the token value from the XML response
      tkn = applyXPath(xmlResponse, "/token/id");
    } catch (XPathExpressionException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    finally
    {
      httpPost.releaseConnection();
    }
    this.token = tkn;
    return this.token;
  }

  /**
   * Returns the POST body to login.
   * 
   * @param username
   * @param password
   * @return XML POST body to login
   */
  private static String getLoginPostBody(String username, String password)
  {
    return "<token><username>"
        + username
        + "</username><password>"
        + password
        + "</password><client_id>PODAAC</client_id><user_ip_address>127.0.0.1</user_ip_address><provider>PODAAC</provider></token>";
  }

  /**
   * Applies an XPath to an XML string and returns the text result.
   * 
   * @param xml
   * @param xpath
   * @return Result of applying XPath.
   * @throws XPathExpressionException
   */
  private static String applyXPath(String xml, String xpath)
      throws XPathExpressionException
  {
    XPath evaluator = XPathFactory.newInstance().newXPath();
    return evaluator.evaluate(xpath, new InputSource(new StringReader(xml)));
  }

  /**
   * Reads an input stream and returns the contents as a string.
   * 
   * @param is
   *          input stream
   * @return string contents of input string
   * @throws IOException
   */
  private static String readInputStream(InputStream is) throws IOException
  {
    LineNumberReader reader = new LineNumberReader(new InputStreamReader(is));
    try
    {
      StringBuilder builder = new StringBuilder();
      String line = null;
      while ((line = reader.readLine()) != null)
      {
        if (builder.length() != 0)
        {
          builder.append("\n");
        }
        builder.append(line);
      }
      return builder.toString();
    }
    finally
    {
      reader.close();
    }
  }

  	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setEchoHost(String echoHost) {
		this.echoHost = echoHost;
		if(!this.echoHost.endsWith("/"))
			this.echoHost += "/";
	}

	public void setTokenHost(String tokenHost) {
		this.tokenHost = tokenHost;
	}
	
	
	/*
	 * XML Writers, schemas
	 */
	public String toXML(Collection ecf){
		try{
			JAXBContext jc = JAXBContext
					.newInstance("gov.nasa.podaac.distribute.echo.jaxb.collection");
			JAXBElement<gov.nasa.podaac.distribute.echo.jaxb.collection.Collection> coElement = (new ObjectFactory()).createCollection(ecf);
					//.createCollectionMetaDataFile(echoCollection);
			Marshaller m = jc.createMarshaller();
			// validation
//			m.setSchema(getCollectionSchema());
			m.setEventHandler(new DistributeValidationEventHandler());
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter sw = new StringWriter();
			m.marshal(coElement, sw);
			return sw.toString();
		} catch (JAXBException je) {
			log.error("toXMLFile:"+je.getMessage());
		} catch (Exception e) {
			log.error("toXMLFile:"+e.getMessage());
		}
		return null;
	}
	
	public String toXML(Granule g){
		try{
			JAXBContext jc = JAXBContext
					.newInstance("gov.nasa.podaac.distribute.echo.jaxb.granule");
			JAXBElement<Granule> element = (new gov.nasa.podaac.distribute.echo.jaxb.granule.ObjectFactory())
					.createGranule(g);
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter sw = new StringWriter();
			m.marshal(element, sw);	
			return sw.toString();
		} catch (JAXBException je) {
			log.error("toXMLFile:"+je.getMessage());
		} catch (Exception e) {
			log.error("toXMLFile:"+e.getMessage(), e);
		}
		return null;
		
	}
	
	public Schema getCollectionSchema(){
		SchemaFactory schemaFactory = SchemaFactory
				.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			Schema schema = schemaFactory.newSchema(new File(System
					.getProperty("distribute.schemas")
					+ "/Collection.xsd"));
			return schema;
		}catch (SAXException e) {
		}
		return null;
	}
}
