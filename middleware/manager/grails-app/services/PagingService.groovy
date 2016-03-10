class PagingService {
   static transactional = false
   
   public PagingManager createPaging(int entriesPerPage, int entries, def page) {
      int pageInteger = 1
      try {
         pageInteger = (!page) ? 1 : Integer.parseInt(page)
      } catch(Exception exception) {
         log.debug("page is wrong, using default value: page="+page)
      }
      
      def pagingManager = new PagingManager(entriesPerPage, entries, pageInteger)
      /*
      pagingManager.entriesPerPage = entriesPerPage
      pagingManager.entries = entries
      pagingManager.page = pageInteger
      */
      
      return pagingManager
   }
}
