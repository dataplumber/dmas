import gov.nasa.jpl.horizon.sigevent.api.EventType
import gov.nasa.jpl.horizon.api.Stereotype
import groovy.sql.*

/**
 * StorageService
 */
class StorageService {
   private static final String SIG_EVENT_CATEGORY = "DMAS"
   def sigEventService
   def dataSource
   boolean transactional = false
   
   public void checkStorage() {
      log.debug("checkStorage started")
      
      def storages = IngLocation.list()
      storages.each {storage ->
         log.debug("\tchecking storage: "+storage.localPath)
      
         if(storage.spaceUsed >= storage.spaceThreshold) {
            sendNotification(storage)
         }
      }
      
      log.debug("checkStorage stopped")
   }
   
   private void sendNotification(IngLocation storage) {
      sigEventService.send(
         StorageService.SIG_EVENT_CATEGORY,
         EventType.Warn,
         "Storage ${storage.localPath} hitting its threshold",
         "Storage ${storage.localPath} is hitting its threshold "+
         "(${storage.spaceUsed} bytes / ${storage.spaceReserved} bytes). "+
         "This can prevent granules from being ingested and archived."
      )
   }
   
   public int getStorage(long totalSize, Stereotype stereotype, int priority) {
      Sql sql = new Sql(dataSource)
      
      def retval = -1
      try {
         sql.call("{?=call INGEST.getStorage(?,?,?)}",[Sql.INTEGER, stereotype.toString(), totalSize, priority]) { storage ->  retval=storage}
      } catch (Exception e) {
      
      }
      return retval
   }
   
   public int getStorageByTypeAndPriority(Stereotype stereotype, int priority) {
      Sql sql = new Sql(dataSource)
      
      def retval = -1
      try {
         sql.call("{?=call INGEST.getStorageByTypeAndPriority(?,?)}",[Sql.INTEGER, stereotype.toString(), priority]) { storage ->  retval=storage}
      } catch (Exception e) {
      
      }
      return retval
   }
   
   public void updateStorage(long totalSize, long lastUsed, long id) {
      Sql sql = new Sql(dataSource)
      sql.call("BEGIN INGEST.updateStorage(?,?,?); END;",[totalSize, lastUsed, id])
   }
}
