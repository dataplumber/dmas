/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.podaac.common.crawler.spider.provider;

import gov.nasa.podaac.common.api.file.FileFilter;
import gov.nasa.podaac.common.api.file.FileProduct;
import gov.nasa.podaac.common.api.file.FileProductHandler;
import gov.nasa.podaac.common.api.file.VFSFileProduct;
import gov.nasa.podaac.common.api.file.FileFilter.TIME_FILTER_MODE;
import gov.nasa.podaac.common.crawler.spider.Crawler;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSelectInfo;
import org.apache.commons.vfs.FileSelector;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.auth.StaticUserAuthenticator;
import org.apache.commons.vfs.cache.DefaultFilesCache;
import org.apache.commons.vfs.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs.impl.StandardFileSystemManager;
import org.apache.commons.vfs.provider.ftp.FtpFileSystemConfigBuilder;
//import org.apache.commons.vfs.provider.http.HttpFileSystemConfigBuilder;
//import org.apache.commons.vfs.provider.http.HttpInterpretter;
import org.apache.commons.vfs.provider.sftp.SftpFileSystemConfigBuilder;
import org.apache.commons.vfs.provider.sftp.TrustEveryoneUserInfo;
//import org.apache.commons.vfs.provider.ftps.FtpsDataChannelProtectionLevel;
//import org.apache.commons.vfs.provider.ftps.FtpsFileSystemConfigBuilder;

/**
 * Implementation of a file crawler object using the Apache Virtual File System
 * framework. This crawler supports crawling of file://, ftp://, and sftp://
 * protocols.
 * 
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: $
 */
public class VFSCrawler implements Crawler, Runnable
{
   private static Log _logger = LogFactory.getLog(VFSCrawler.class);
   public static final String PROVIDER_ENV = "common.crawler.providers";
   public static final String PROVIDER_FILE = "common-providers.xml";
   private static URL _providerURL = null;
   private String _keyFile = System.getProperty("user.home") + "/.ssh/id_dsa";

   // locate the provider config info to setup the VFS manager.
   static
   {
      if (System.getProperty(VFSCrawlerManager.PROVIDER_ENV) != null)
      {
         File providerFile = new File(System.getProperty(VFSCrawlerManager.PROVIDER_ENV) + File.separator + VFSCrawlerManager.PROVIDER_FILE);
         
         if (providerFile.exists())
         {
            try
            {
               VFSCrawler._providerURL = providerFile.toURL();
            }
            catch (MalformedURLException e)
            {
               VFSCrawler._logger.error(e.getMessage(), e);
            }
         }
      }
      if (VFSCrawler._providerURL == null)
      {
         VFSCrawler._providerURL = VFSCrawler.class.getResource ("/META-INF/" + VFSCrawlerManager.PROVIDER_FILE);
      }
   }

   private boolean _started = false;
   private boolean _passive = true;
   private FileSystemManager _manager;
   private String _uri;
   private long _frequency = -1;
   private FileProductHandler _handler = null;
   private FileFilter _inclusionFilter = null;
   private FileFilter _exclusionFilter = null;
   private ExecutorService _executor = null;
   private FileSystemOptions _opts = new FileSystemOptions();
   private boolean _greedy = true;
   private boolean _recursive = true;
   private FileObject _dirObj = null;
   private ProcessFileProduct _doHandler = ProcessFileProduct.deflt;
   private String _name = "*** Unset ***";
   
   /**
    * Constructor to set the URI to be scan by the crawler
    * 
    * @param uri
    *           the URI to the location to be scanned
    */
   public VFSCrawler(String uri)
   {
      this._uri = uri;
      if (uri.startsWith("sftp"))
      {
         try
         {
            SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(
                  this._opts, "no");
            SftpFileSystemConfigBuilder.getInstance().setUserInfo(this._opts,
                  new TrustEveryoneUserInfo());
            // set up the SFTP data timeout to expire after 5 minutes
            SftpFileSystemConfigBuilder.getInstance().setTimeout(this._opts,
                  300);
         }
         catch (FileSystemException e)
         {
            VFSCrawler._logger.error(e.getMessage(), e);
         }
      }
//      else if (uri.startsWith("ftps"))
//      {
//    	  _logger.debug("Setting FTPS Opts (none)");
//          FtpsFileSystemConfigBuilder.getInstance().setDataChannelProtectionLevel(this._opts, FtpsDataChannelProtectionLevel.C);
//       }
      else if (uri.startsWith("ftp"))
      {
         VFSCrawler._logger.trace("enable FTP passive mode");
         FtpFileSystemConfigBuilder.getInstance().setPassiveMode(this._opts,
               true);
         // set up the FTP data timeout to expire after 5 minutes
	VFSCrawler._logger.info("Setting datatime out to 300");         
	FtpFileSystemConfigBuilder.getInstance().setDataTimeout(this._opts,
               3000);
      }
      /*
      else if (uri.startsWith("http"))
      {
         HttpInterpretter interpretter = HttpInterpretter.deflt;
         
         if (uri.toLowerCase(Locale.US).startsWith ("http://oceandata.sci.gsfc.nasa.gov")) interpretter = new OceanDataGSFC();
         
         VFSCrawler._logger.info("route HTTP to " + interpretter.getClass().getSimpleName());
         HttpFileSystemConfigBuilder.getInstance().setInterpretter (interpretter);
      }
      */
  }
   
   public VFSCrawler(String uri, boolean passive)
   {
      this._uri = uri;
      if (uri.startsWith("sftp"))
      {
         try
         {
            SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(
                  this._opts, "no");
            SftpFileSystemConfigBuilder.getInstance().setUserInfo(this._opts,
                  new TrustEveryoneUserInfo());
            // set up the SFTP data timeout to expire after 5 minutes
            SftpFileSystemConfigBuilder.getInstance().setTimeout(this._opts,
                  300);
         }
         catch (FileSystemException e)
         {
            VFSCrawler._logger.error(e.getMessage(), e);
         }
      }
      else if (uri.startsWith("ftp"))
      {
    	  if(passive){
    		  VFSCrawler._logger.trace("enable FTP passive mode");
    		  FtpFileSystemConfigBuilder.getInstance().setPassiveMode(this._opts,
    				  true);
    		  
    	  }
    	  else{
    		  _logger.info("FTP Crawler mode set to active");
    		  _passive= false;
    	  }
         // set up the FTP data timeout to expire after 5 minutes
	VFSCrawler._logger.info("Setting datatime out to 300");         
	FtpFileSystemConfigBuilder.getInstance().setDataTimeout(this._opts,
               3000);
      }
      /*
      else if (uri.startsWith("http"))
      {
         HttpInterpretter interpretter = HttpInterpretter.deflt;
         
         if (uri.toLowerCase(Locale.US).startsWith ("http://oceandata.sci.gsfc.nasa.gov")) interpretter = new OceanDataGSFC();
         
         VFSCrawler._logger.info("route HTTP to " + interpretter.getClass().getSimpleName());
         HttpFileSystemConfigBuilder.getInstance().setInterpretter (interpretter);
      }
      */
  }

   /**
    * Constructor to configure the crawler to operate in daemon mode. With the
    * default connection to one.
    * 
    * @param uri
    *           the URI to be scanned
    * @param frequency
    *           the frequency for scanning
    */
   public VFSCrawler(String uri, long frequency)
   {
      this(uri);
      this._frequency = frequency;
   }

   /**
    * Internal method to perform a single pass to the specified URI.
    * 
    * @param uri
    *           the URI to be scanned
    * @return the list of files found.
    */
   protected synchronized Set<FileProduct> _crawl(String uri)
   {
      Set<FileProduct> products = new HashSet<FileProduct>();
      if (this._manager == null) return products;

      VFSCrawler._logger.debug("Crawling : " + uri);

      try
      {
         VFSCrawler._logger.trace("Passive Mode before resolveFile " + FtpFileSystemConfigBuilder.getInstance().getPassiveMode(this._opts));
         this._dirObj = this._manager.resolveFile(uri, this._opts);

         if (!this._dirObj.isReadable())
         {
            String msg = uri + " not readable.";
            VFSCrawler._logger.error(msg);
            throw new FileSystemException("vfs.provider/read-not-readable.error", msg);
         }

         // get the list of files object with a file selector.
         FileObject[] files = this._dirObj.findFiles(new FileSelector()
         {
            private boolean _found = false;

            public boolean includeFile(FileSelectInfo fileInfo) throws Exception
            {
               boolean result = false;
               boolean match = false;

               if (!_greedy && this._found) return false;

               VFSCrawler._logger.trace("Looking at " + fileInfo.getFile().getName().getFriendlyURI());
               FileObject fileObj = fileInfo.getFile();
               
               if (fileObj.getType() != FileType.FILE) return false;
               
               VFSCrawler._logger.trace("Looking at file: " + fileObj.getName().getFriendlyURI());
               if (!fileObj.isReadable())
               {
                  VFSCrawler._logger.trace("File is not readable.  Reject this file.");
                  return false;
               }

               if (_inclusionFilter == null)
               {
                  VFSCrawler._logger.trace("No filter specified.  Accept this file.");
                  match = true;
               }
               else
               {
                  String[] nameExp = _inclusionFilter.getNameExpression();
                  for (String exp : nameExp)
                  {
                     VFSCrawler._logger.trace("Comparing file: " + fileObj.getName().getBaseName());

                     if (fileObj.getName().getBaseName().matches(exp))
                     {
                        match = true;
                        break;
                     }
                  }
               }

               if (match && _exclusionFilter != null)
               {
                  String[] nameExp = _exclusionFilter.getNameExpression();
                  for (String exp : nameExp)
                  {
                     if (fileObj.getName().getBaseName().matches(exp))
                     {
                        match = false;
                        break;
                     }
                  }
               }

               this._found = match;

               if (!match) return false;

               Set<FileProduct> excludes = _inclusionFilter.getExcludeList();
               FileProduct product = new VFSFileProduct(fileObj);
               if (excludes.contains(product)) return false;

               FileFilter.TIME_FILTER_MODE mode = _inclusionFilter.getTimeFilterMode();
               if (mode == null) mode = TIME_FILTER_MODE.NONE;

               long[] timevalue = _inclusionFilter.getTimeFilterValue();
               long lastModified = fileObj.getContent().getLastModifiedTime();

               switch (mode)
               {
                  case BEFORE:
                     result = lastModified < timevalue[0];
                     break;
                  case AFTER:
                     result = lastModified > timevalue[0];
                     break;
                  case BETWEEN:
                     result = (lastModified >= timevalue[0] && lastModified <= timevalue[1]);
                     break;
                  case NONE:
                     result = true;
                     break;
               }

               if (result)
               {
                  HashSet<FileProduct> products = new HashSet<FileProduct>(1);
                  
                  products.add (product);
                  VFSCrawler.this._doHandler.process (product);

                  if (VFSCrawler.this._handler != null) VFSCrawler.this._handler.onProducts (products);
               }
               
               return false;
            }

            public boolean traverseDescendents(FileSelectInfo fileInfo) throws Exception
            {
               if (!_greedy && this._found) return false;

               VFSCrawler._logger.trace("Looking at " + fileInfo.getFile().getName().getFriendlyURI());
               FileObject fileObj = fileInfo.getFile();
               
               if (fileObj.getType() == FileType.FILE) return false;
               else if (fileObj.getType() == FileType.FOLDER)
               {
                  if (!_recursive && fileObj != _dirObj) return false;
                  return true;
               }
               else if (fileObj.getType() == FileType.IMAGINARY) return false;
               else return false;
            }
         });
         
         if (this._handler != null) this._handler.preprocess();
         
         for (FileObject fo : files) products.add (new VFSFileProduct(fo));
         
         if (this._handler != null) this._handler.postprocess();
      }
      catch (FileSystemException e)
      {
         VFSCrawler._logger.error(e.getMessage(), e);
         if (this._handler != null) this._handler.onError(e);
      }
      finally
      {
         if (this._dirObj != null)
         {
            try
            {
               this._dirObj.close();
            }
            catch (FileSystemException e)
            {
               VFSCrawler._logger.error(e.getMessage(), e);
               if (this._handler != null) this._handler.onError(e);
            }
         }
         VFSCrawler._logger.trace("Release semaphore.");
         if (this._manager instanceof StandardFileSystemManager)
         {
            ((StandardFileSystemManager) this._manager).freeUnusedResources();
         }
      }
      return products;
   }

   /**
    * Implements the required method.
    */
   public synchronized Set<FileProduct> crawl()
   {
      Set<FileProduct> result;
      
      try
      {
         if (this._manager == null)
         {
            StandardFileSystemManager manager = new StandardFileSystemManager();
            manager.setFilesCache(new DefaultFilesCache());
            
            if (VFSCrawler._providerURL != null)
            {
               VFSCrawler._logger.trace("Load provider info from: " + VFSCrawler._providerURL.toString());
               manager.setConfiguration(VFSCrawler._providerURL);
            }
            
            manager.init();
            this._manager = manager;
         }
         
         if (this._manager instanceof StandardFileSystemManager)
         {
            ((StandardFileSystemManager)this._manager).freeUnusedResources();
         }
      }
      catch (FileSystemException e)
      {
         VFSCrawler._logger.error(e.getMessage(), e);
         if (this._handler != null) this._handler.onError(e);
      }
      
      if (this._doHandler == ProcessFileProduct.deflt)
      {
         ProductSet handler = (ProductSet)this._doHandler;
         
         handler.clear();
         this._crawl (this._uri);
         result = handler.getShallowCopy();
      }
      else result = this._crawl (this._uri);
      
      return result;
   }

   public int getAvailableConnections()
   {
      return 1;
   }

   public long getFrequency()
   {
      return this._frequency;
   }

   public String getName()
   {
      return this._name;
   }

   public String getRootURI()
   {
      return this._uri;
   }

   public boolean isGreedy()
   {
      return this._greedy;
   }

   public boolean isRecursive()
   {
      return this._recursive;
   }

   public void setGreedy(boolean greedy)
   {
      this._greedy = greedy;
   }

   public void setRecursive(boolean recursive)
   {
      this._recursive = recursive;
   }

   public void init()
   {
   }

   public void registerDoHandler (ProcessFileProduct handler)
   {
      if (handler == null) this._doHandler = ProcessFileProduct.deflt;
      else this._doHandler = handler;
   }
   
   public void registerProductHandler(FileProductHandler handler)
   {
      this._handler = handler;
   }

   public void registerProductSelector(FileFilter inclusionFilter)
   {
      this.registerProductSelector(inclusionFilter, null);
   }

   public void registerProductSelector(FileFilter inclusionFilter,
         FileFilter exclusionFilter)
   {
      this._inclusionFilter = inclusionFilter;
      this._exclusionFilter = exclusionFilter;
   }

   public void run()
   {
      if (!this._started) return;

      // this._crawl(this._uri);
      this.crawl();
   }

   public void setAuthentication(String username, String password)
   {
      try
      {
         StaticUserAuthenticator auth = new StaticUserAuthenticator(null,
               username, password);
         // this._opts = new FileSystemOptions();
         DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(
               this._opts, auth);

         if (password == null && this._uri.startsWith("sftp") && new File(this._keyFile).exists()){
            SftpFileSystemConfigBuilder.getInstance().setIdentities(this._opts,
                  new File[] { new File(this._keyFile) });
         }
         
      }
      catch (FileSystemException e)
      {
         VFSCrawler._logger.error(e.getMessage(), e);
         if (this._handler != null) this._handler.onError(e);
      }
   }

   public void setName(String name)
   {
      this._name = name;
   }

   public synchronized void start()
   {
      if (this._frequency > 0)
      {
         VFSCrawler._logger.info("Activate crawler to " + this._uri
               + " with frequency: " + this._frequency + " second(s).");
         ScheduledExecutorService executor = Executors
               .newScheduledThreadPool(1);
         executor.scheduleWithFixedDelay(this, 0, this._frequency,
               TimeUnit.SECONDS);
         this._executor = executor;
      }
      else
      {
         VFSCrawler._logger.info("Active crawler to " + this._uri + ".");
         this._executor = Executors.newFixedThreadPool(1);
         this._executor.execute(this);
      }
      this._started = true;
   }

   public synchronized void stop()
   {

      if (this._started)
      {
         this._executor.shutdown();

         int timeout = 1;
         while (!this._executor.isTerminated())
         {
            try
            {
               if (!this._executor.awaitTermination(timeout, TimeUnit.SECONDS))
               {
                  if (timeout > 60)
                  {
                     this._executor.shutdownNow();
                     throw new InterruptedException(
                           "Giving up on waiting for worker thread(s) to terminate.");
                  }
                  timeout *= 2;
                  VFSCrawler._logger
                        .debug("Waiting for crawler worker thread(s) to terminate.  Check again in "
                              + timeout + " seconds.");
               }
            }
            catch (InterruptedException e)
            {
               VFSCrawler._logger.error(e.getMessage(), e);
               if (this._handler != null) this._handler.onError(e);
            }
         }
      }

      this._started = false;

      if (this._manager instanceof StandardFileSystemManager)
      {
         StandardFileSystemManager vmanager = (StandardFileSystemManager) this._manager;
         vmanager.freeUnusedResources();
         vmanager.close();
         this._manager = null;

      }
      VFSCrawler._logger.debug("Stop crawling : " + this._uri);
   }

}
