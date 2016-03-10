/*****************************************************************************
 * Copyright (c) 2010 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.podaac.common.httpfetch.api;

import gov.nasa.podaac.common.api.util.ChecksumUtility;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;

import gov.nasa.podaac.common.api.file.FileProduct;
import gov.nasa.podaac.common.api.util.DateTimeUtility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.lang.StringBuffer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpConnectionParams;

/**
 * This is the HTTP fetch class.  It provide utility methods to retrieve data from
 * remote HTTP links.
 *
 * @author Thomas Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: $
 */
public class HttpFetcher {

   private static Log _logger = LogFactory.getLog(HttpFetcher.class);

   private HttpClient _httpclient = null;
   private boolean _shared = false;
   private int     m_timeoutsDefault = 20;

   /**
    * Default constructor to initialize the internal HTTP client
    */
   public HttpFetcher() {
      this._httpclient = new DefaultHttpClient();
      this.setDefaultParametersToHandleTimeouts();
   }

   public HttpFetcher(int i_timeoutsDefault) {
      this._httpclient = new DefaultHttpClient();
      this.setDefaultParametersToHandleTimeouts(i_timeoutsDefault);
   }

   public HttpFetcher(HttpClient httpclient) {
      this._httpclient = httpclient;
      this._shared = true;
      this.setDefaultParametersToHandleTimeouts();
   }

   public HttpFetcher(HttpClient httpclient,int i_timeoutsDefault) {
      this._httpclient = httpclient;
      this._shared = true;
      this.setDefaultParametersToHandleTimeouts(i_timeoutsDefault);
   }

   public void setDefaultParametersToHandleTimeouts() {
      // Reference: http://blog.jayway.com/2009/03/17/configuring-timeout-with-apache-httpclient-40/
      //
      // This function allows the user to set the time-out parameters to smaller values so when the system
      // hangs, the connections/sockets will be closed after a smaller time, thus allow the crawler to continue to do its job.
   
      int connectionTimeoutMillis = m_timeoutsDefault*1000; // Should be sufficient time for the HTTP client to establish connection before timing out.
      int socketTimeoutMillis     = m_timeoutsDefault*1000; // Should be sufficient time for the HTTP client to operate on the socket before timing out.

      HttpParams httpParams = this._httpclient.getParams();
      HttpConnectionParams.setConnectionTimeout(httpParams, connectionTimeoutMillis);
      HttpConnectionParams.setSoTimeout(httpParams, socketTimeoutMillis);
   }

   public void setDefaultParametersToHandleTimeouts(int i_timeoutsDefault) {
      // Reference: http://blog.jayway.com/2009/03/17/configuring-timeout-with-apache-httpclient-40/
      //
      // This function allows the user to set the time-out parameters to smaller values so when the system
      // hangs, the connections/sockets will be closed after a smaller time, thus allow the crawler to continue to do its job.

      // Save the last time out setting so subsequent call to setDefaultParametersToHandleTimeouts() without parameter will use the lastest setting.

      m_timeoutsDefault = i_timeoutsDefault;

      int connectionTimeoutMillis = i_timeoutsDefault*1000; // Should be sufficient time for the HTTP client to establish connection before timing out.
      int socketTimeoutMillis     = i_timeoutsDefault*1000; // Should be sufficient time for the HTTP client to operate on the socket before timing out.

      HttpParams httpParams = this._httpclient.getParams();
      HttpConnectionParams.setConnectionTimeout(httpParams, connectionTimeoutMillis);
      HttpConnectionParams.setSoTimeout(httpParams, socketTimeoutMillis);
   }

   /**
    * Method to retrieve text contents from the specified URL
    *
    * @param url the remote URL link
    * @return the text from the remote URL link
    * @throws FetchException when errors occurs
    */
   public String getTextContent(String url) throws FetchException {
      StringBuffer o_htmlContent = new StringBuffer();

      HttpGet httpget = new HttpGet(url);
      HttpFetcher._logger.debug("executing request " + httpget.getURI());

      try {
         HttpResponse response = this._httpclient.execute(httpget);

         HttpFetcher._logger
               .debug("\n----------------------------------------\n"
                     + response.getStatusLine()
                     + "\n----------------------------------------");

         HttpEntity entity = response.getEntity();
         if (entity != null) {
            // We use read() function instead of readLine() because we don't want to add extra carriage
            // return at the end unnecessarily.
 
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                  entity.getContent()));
            char[] cbuf = new char[1024];
            int    numCharsRead = 0;
            try {
               while ((numCharsRead = reader.read(cbuf,0,1024)) != -1) {
                  // Save what we just read into o_htmlContent. Becareful to append the cbuf only up the numCharsRead and not the buffer length of 1024.
                  o_htmlContent.append(cbuf,0,numCharsRead); 
               }

            } catch (IOException ex) {
               HttpFetcher._logger.debug(ex);
               throw new FetchException(ex.getMessage(), ex);
            } catch (RuntimeException ex) {
               httpget.abort();
               throw new FetchException(ex.getMessage(), ex);
            } finally {
               reader.close();
            }
         }
      } catch (IOException ex) {
//System.out.println("HttpFetcher::getTextContent:IOException found [" + ex.getMessage() + "] reading url [" + url + "].  Network to data provider may be busy.");
         HttpFetcher._logger.debug("HttpFetcher::getTextContent:IOException found [" + ex.getMessage() + "] reading url [" + url + "].  Network to data provider may be busy.");
         HttpFetcher._logger.debug(ex);
      } catch (Exception ex) {
//System.out.println("HttpFetcher::getTextContent:Exception found [" + ex.getMessage() + "] reading url [" + url + "].  Network to data provider may be busy.");
         HttpFetcher._logger.error(ex);
      } 

      return o_htmlContent.toString();
   }

   /**
    * Method to retrieve text content from the input URL and apply the input regular expression
    * to find any substrings that matches.
    *
    * @param url   the remote URL link
    * @param regex the regular expression to apply to the retrieved text content
    * @return the list of strings that matches the regular expression
    * @throws FetchException when an error is occurred
    */
   public List<String> getFilteredText(String url, String regex)
         throws FetchException {
      List<String> result = new ArrayList<String>();
      String html = this.getTextContent(url);
      Pattern p = Pattern.compile(regex);
      for (String line : html.split("\n")) {
         Matcher m = p.matcher(line);
         if (m.find()) {
            result.add(m.group(1));
         }
      }
      return result;
   }

   /**
    * Method to create a FileProduct object from the input URL
    *
    * @param url the remote URL
    * @return the File Product object
    * @throws FetchException when an error is occurred
    * @see gov.nasa.podaac.common.api.file.FileProduct
    */
   public HttpFileProduct createFileProduct(String url) throws FetchException {
      HttpFileProduct result = new HttpFileProduct(url, url.substring(
            url.lastIndexOf("/") + 1, url.length()));
      HttpHead httphead = new HttpHead(url);
      // force to close the connection when operation is complete.
      // this prevents dangling TIME_WAITS
      //httphead.setHeader("Connection", "close");
      try {
         HttpResponse response = this._httpclient.execute(httphead);
         Header modHeader = response.getFirstHeader("Last-Modified");
         Header sizeHeader = response.getFirstHeader(
               "Content-Length");
         Date modified = new Date(0);
         if (modHeader != null) {
            modified = DateTimeUtility
                  .parseDate("EEE, dd MMM yyyy hh:mm:ss zzz", modHeader.getValue());
         } else {
            HttpFetcher._logger.warn("Missing 'Last-Modified' from HTTP response for " + url);
         }
// DEBUG: Setting date to zero to simulate bad header.
//modified = new Date(0);
         long size = 0;
         if (sizeHeader != null) {
            size = Long.parseLong(sizeHeader.getValue());
         } else {
            HttpFetcher._logger.warn("Missing 'Content-Length' from HTTP response for " + url);
         }
// DEBUG: Setting size to zero to simulate bad header.
//size = 0;
         result = new HttpFileProduct(this._httpclient, url, url.substring(
               url.lastIndexOf("/") + 1, url.length()), modified.getTime(),
               size);
      } catch (ClientProtocolException ex) {
         throw new FetchException(ex.getMessage(), ex);
      } catch (IOException ex) {
         throw new FetchException(ex.getMessage(), ex);
      } catch (Exception ex) {
         // Added a generic exception to catch error such as this:
         //
         // Exception in thread "main" java.lang.IllegalStateException: Invalid use of SingleClientConnManager: connection still allocated.
         // Make sure to release the connection before allocating another one.
         //         at org.apache.http.impl.conn.SingleClientConnManager.getConnection(SingleClientConnManager.java:199)
         //         at org.apache.http.impl.conn.SingleClientConnManager$1.getConnection(SingleClientConnManager.java:173)
         //         at org.apache.http.impl.client.DefaultRequestDirector.execute(DefaultRequestDirector.java:356)
         //         at org.apache.http.impl.client.AbstractHttpClient.execute(AbstractHttpClient.java:641)
         //         at org.apache.http.impl.client.AbstractHttpClient.execute(AbstractHttpClient.java:576)
         //         at org.apache.http.impl.client.AbstractHttpClient.execute(AbstractHttpClient.java:554)
         //         at gov.nasa.podaac.common.httpfetch.api.HttpFetcher.createFileProduct(HttpFetcher.java:206)
         throw new FetchException(ex.getMessage(), ex);
      }
      return result;
   }

   public HttpFileProduct createFileProduct(String url, ChecksumUtility.DigestAlgorithm digestAlgorithm, String digestValue) throws FetchException {
      HttpFileProduct result = this.createFileProduct(url);
      result.setDigestAlgorithm(digestAlgorithm);
      result.setDigestValue(digestValue);
      return result;
   }

   /**
    * Method to shutdown the http client and free any memory resources
    */
   public void shutdown() {
      if (!this._shared && this._httpclient != null) {
         this._httpclient.getConnectionManager().shutdown();
      }
   }

   public HttpClient getHttpClient() {
      return this._httpclient;
   }
}
