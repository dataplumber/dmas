package gov.nasa.jpl.horizon.manager

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import org.apache.zookeeper.WatchedEvent
import org.apache.zookeeper.Watcher
import org.apache.zookeeper.Watcher.Event.EventType

import org.codehaus.groovy.grails.commons.ApplicationHolder

/**
 * Implementation of ZooKeeper Watcher callback object
 */
public class ManagerWatcher implements Watcher {
   def managerWatcherService = ApplicationHolder.application.mainContext.managerWatcherService
   
   private static Log log = LogFactory.getLog(ManagerWatcher.class)
   
   public ManagerWatcher() {
   }
   
   /**
    * Method called by ZooKeeper Watcher callback.
    */
   public void process(WatchedEvent event) {
      log.trace("ManagerWatcher: received event " + event.getType() + " " + event.getState())
      /*
       * Watcher code to execute when the event is a node being removed. Usually happens when watching a Queued job node.
       */
      if (event.getType().equals(EventType.NodeDeleted)) {
         log.trace("ManagerWatcher: node removed " + event.getPath())
         
         /*
          * Confirms it's from a ingest/archive job queue. The other time this might happen in this current block:
          * We might set a watch on a node's data (readProcessNode), but it might already be done. The watch can trigger
          * when we delete the node, but we want to ignore that.
          */
         if (event.getPath().contains("queue") && (event.getPath().contains("ingest") || event.getPath().contains("archive"))) {
            managerWatcherService.handleZkWatcher(event.getPath(), this)
         }
      }
      /*
       * Watcher code to execute when a watched node has data change (nominally when a job process goes from in process to complete).
       */
      else if (event.getType().equals(EventType.NodeDataChanged)) {
         log.trace("ManagerWatcher: node changed " + event.getPath())
         String[] names = event.getPath().split("/")
         String productTypeName = names[-2]
         String productName = names[-1]
         managerWatcherService.handleZkWatcher(productTypeName+"/"+productName, this)
      }
   }
}
