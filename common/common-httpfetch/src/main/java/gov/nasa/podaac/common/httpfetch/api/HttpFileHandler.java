/*****************************************************************************
 * Copyright (c) 2010 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.podaac.common.httpfetch.api;

import java.util.Map;

/**
 * This is the HttpFileHandler interface that is dispatched by the OceanDataWalker
 * object.
 *
 * @author Thomas Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: $
 */
public interface HttpFileHandler {

   /**
    * Hook method called by the walker when an URL is found.
    *
    * @param rootURL the top-level URL of the HTML where the file URL is found
    * @param url     the file URL.
    */
   public void handle(HttpFetcher fetcher, String rootURL, String url, Map<String, String> checksum);

   /**
    * Hook method use to clear any memory resources allocated by the handler
    */
   public void shutdown();

}
