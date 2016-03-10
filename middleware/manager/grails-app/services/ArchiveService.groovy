/*
import gov.nasa.podaac.inventory.api.QueryFactory
import gov.nasa.podaac.inventory.model.Dataset
import gov.nasa.podaac.inventory.model.Granule
import gov.nasa.podaac.inventory.model.GranuleReference
import gov.nasa.podaac.inventory.hibernate.HibernateSessionFactory
*/

/**
 * 
 */
class ArchiveService {
   /*
    * Uncomment below once archive stuff is ready...
    *
   static transactional = false
   def sessionFactory
   
   public List<Integer> getGranuleIds(int datasetId) {
      HibernateSessionFactory.sessionFactory = sessionFactory
      
      def query = QueryFactory.getInstance().createQuery()
      def ids = query.listGranuleIdByProperty("dataset.datasetId", datasetId)
      
      return ids
   }
   
   public List<Integer> getDatasetIds() {
      HibernateSessionFactory.sessionFactory = sessionFactory
      
      def query = QueryFactory.getInstance().createQuery()
      def ids = query.listDatasetId()
      return ids
   }
   
   public Dataset getDataset(int datasetId) { 
      HibernateSessionFactory.sessionFactory = sessionFactory
      
      def query = QueryFactory.getInstance().createQuery()
      def dataset = query.fetchDatasetById(datasetId)
      
      return dataset
   }
   
   public Dataset getDataset(String datasetShortName) {
      HibernateSessionFactory.sessionFactory = sessionFactory
      
      def query = QueryFactory.getInstance().createQuery()
      def dataset = query.findDatasetByShortName(datasetShortName)
      
      return dataset
   }
   
   public int getGranuleSize(int datasetId) {
      HibernateSessionFactory.sessionFactory = sessionFactory
      
      def query = QueryFactory.getInstance().createQuery()
      def size = query.sizeOfGranuleByDatasetId(datasetId)
      
      return size
   }
   
   public Granule getGranule(int granuleId) {
      HibernateSessionFactory.sessionFactory = sessionFactory
      
      def query = QueryFactory.getInstance().createQuery()
      def granule = query.fetchGranuleById(granuleId)
      
      return granule
   }
   
   public List<GranuleReference> getGranuleReferences(int datasetId) {
      HibernateSessionFactory.sessionFactory = sessionFactory
      
      def query = QueryFactory.getInstance().createQuery()
      def granuleReferences = query.fetchReferenceByDatasetId(datasetId)
      
      return granuleReferences
   }
   
   public void updateGranuleStatus(int granuleId, String status) throws Exception {
      HibernateSessionFactory.sessionFactory = sessionFactory
      
      def query = QueryFactory.getInstance().createQuery()
      query.updateGranuleStatus(granuleId, status)
   }
   
   public void deleteGranule(int granuleId) throws Exception {
      HibernateSessionFactory.sessionFactory = sessionFactory
      
      def query = QueryFactory.getInstance().createQuery()
      query.deleteGranule(granuleId)
   }
   
   public void updateApiArchive(int granuleId, String type, String status, String name) throws Exception {
      HibernateSessionFactory.sessionFactory = sessionFactory
      
      def query = QueryFactory.getInstance().createQuery()
      query.updateAIPArchiveStatus(granuleId, type, status, name)
   }
   
   public void updateAipGranuleArchive(int granuleId, String status, String name) throws Exception {
      HibernateSessionFactory.sessionFactory = sessionFactory
      
      def query = QueryFactory.getInstance().createQuery()
      query.updateAIPGranuleArchiveStatus(granuleId, status, name)
   }
   
   public void updateAipGranuleReference(int granuleId, String status, String path) throws Exception {
      HibernateSessionFactory.sessionFactory = sessionFactory
      
      def query = QueryFactory.getInstance().createQuery()
      query.updateAIPGranuleReferenceStatus(granuleId, status, path)
   }
   
   public void updateGranuleArchiveChecksum(int granuleId, String name, String checksum) throws Exception {
      HibernateSessionFactory.sessionFactory = sessionFactory
      
      def query = QueryFactory.getInstance().createQuery()
      query.updateGranuleArchiveChecksum(granuleId, name, checksum)
   }
   
   public void updateGranuleArchiveSize(int granuleId, String name, long size) throws Exception {
      HibernateSessionFactory.sessionFactory = sessionFactory
      
      def query = QueryFactory.getInstance().createQuery()
      query.updateGranuleArchiveSize(granuleId, name, size)
   }
   
   public void updateGranuleArchiveStatus(int granuleId, String status) throws Exception {
      HibernateSessionFactory.sessionFactory = sessionFactory
      
      def query = QueryFactory.getInstance().createQuery()
      query.updateGranuleArchiveStatus(granuleId, status)
   }
   
   public void updateGranuleLocation(int granuleId, String basePath) throws Exception {
      HibernateSessionFactory.sessionFactory = sessionFactory
      
      def query = QueryFactory.getInstance().createQuery()
      query.updateGranuleLocation(granuleId, basePath)
   }
   
   public void addGranuleReference(int granuleId, String type, String status, String description, String path) throws Exception {
      HibernateSessionFactory.sessionFactory = sessionFactory
      
      def query = QueryFactory.getInstance().createQuery()
      query.addReference(granuleId, type, status, description, path)
   }
   
   public void removeGranuleReference(int granuleId, String type) throws Exception {
      HibernateSessionFactory.sessionFactory = sessionFactory
      
      def query = QueryFactory.getInstance().createQuery()
      query.removeReference(granuleId, type)
   }
   
   public void updateVerifyGranuleStatus(int granuleId, String status) throws Exception {
      HibernateSessionFactory.sessionFactory = sessionFactory
      
      def query = QueryFactory.getInstance().createQuery()
      query.updateVerifyGranuleStatus(granuleId, status)
   }
   */
}
