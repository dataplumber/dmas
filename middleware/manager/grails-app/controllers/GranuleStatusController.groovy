import java.sql.Timestamp

import grails.converters.JSON

import gov.nasa.podaac.common.api.util.DateTimeUtility
import gov.nasa.jpl.horizon.api.Lock
import gov.nasa.jpl.horizon.api.State

class GranuleStatusController {
  public static final List GRANULE_UPDATE_OPTIONS = [
    ['Name': 'ReIngestAdd', 'Description': 'Trigger re-ingestion with add', 'State': State.PENDING, 'Lock': Lock.ADD],
    ['Name': 'ReIngestReplace', 'Description': 'Trigger re-ingestion with replace', 'State': State.PENDING, 'Lock': Lock.REPLACE],
    ['Name': 'ReArchive', 'Description': 'Trigger re-archive', 'State': State.PENDING_ARCHIVE, 'Lock': Lock.ARCHIVE],
  ]
  public static final String GRANULE_UPDATE_OPTIONS_NAME = 'option'
  public static final int GRANULES_PER_PAGE = 10
  private static final Map IMPORTANCE = [
    High: "High",
    Medium: "Medium",
    Low: "Low"
  ]
  private static final List STATES = [
    ['State': State.ABORTED, 'Importance': IMPORTANCE.High],
    ['State': State.PENDING_STORAGE, 'Importance': IMPORTANCE.High],
    ['State': State.PENDING_ARCHIVE_STORAGE, 'Importance': IMPORTANCE.High],
  ]
  private static final List PENDINGS = [
    State.PENDING,
    State.PENDING_ARCHIVE
  ]
  private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
  private static final String MESSAGE_TYPE_GRANULE_STATUS = 'GranuleStatus'
  def granuleStatusService
  
  def index = {
    int page = (params.page) ? Integer.parseInt(params.page) : 0
    int numberOfGranules = granuleStatusService.getGranulesCount()
    
    int numberOfPages = (numberOfGranules / GRANULES_PER_PAGE)
    if((numberOfGranules % GRANULES_PER_PAGE) != 0) {
      numberOfPages += 1
    }
    if(page >= numberOfPages) {
      page = (numberOfPages - 1)
    }
    if(page < 0) {
      page = 0
    }
    
    // get products
    def products = granuleStatusService.getGranules(page, GRANULES_PER_PAGE)
    
    // process products
    LinkedList messages = new LinkedList()
    products.each {product ->
      // bad ones
      def state = STATES.find{product.currentState == it.State.toString()}
      if(state) {
        def message = [:]
        message['id'] = product.id
        message['type'] = MESSAGE_TYPE_GRANULE_STATUS
        message['importance'] = state.Importance
        message['granuleName'] = product.name
        message['state'] = product.currentState
        message['lock'] = product.currentLock
        message['updated'] = product.updated
        message['message'] = 'This granule needs attention.'
        message['note'] = product.note
        messages.add(message)
      } else {
        def pending = PENDINGS.find{product.currentState == it.toString()}
        if(pending) {
          long currentTime = new Date().getTime()
          long updatedTime = product.updated
          long elapsedTime = (currentTime - updatedTime)

          def message = [:]
          message['id'] = product.id
          message['type'] = MESSAGE_TYPE_GRANULE_STATUS
          message['importance'] = IMPORTANCE.High
          message['granuleName'] = product.name
          message['state'] = product.currentState
          message['lock'] = product.currentLock
          message['updated'] = product.updated
          message['message'] = 'Product seems to be stack with this state.'
          message['note'] = product.note
          messages.add(message)
        }
      }
    }
    
    messages.addFirst([
      'type': 'Paging',
      'page': page,
      'pages': numberOfPages,
      'granulesPerPage': GRANULES_PER_PAGE,
      'granulesCount': numberOfGranules,
      'granulesFrom': (page * GRANULES_PER_PAGE) + 1,
      'granulesTo': ((((page + 1) * GRANULES_PER_PAGE) > numberOfGranules) ? numberOfGranules : ((page + 1) * GRANULES_PER_PAGE)),
      'newer': (page > 0) ? (page - 1) : 'null',
      'newest': (page > 1) ? 0 : 'null',
      'older': ((page + 1) < numberOfPages) ? (page + 1) : 'null',
      'oldest': ((page + 2) < numberOfPages) ? (numberOfPages - 1) : 'null'
    ])
    
    def result = ['items': messages]
    render result as JSON
  }
  
  def update = {
    int id = Integer.parseInt(params.id);
    String option = params[GRANULE_UPDATE_OPTIONS_NAME]

    boolean success = true
    String message = ''
    try {
      def optionEntry = GRANULE_UPDATE_OPTIONS.find{it.Name == option}
      granuleStatusService.updateGranule(id, optionEntry.State, optionEntry.Lock)
      
      message = 'Granule updated'
    } catch(Exception exception) {
      log.debug('Failed to update granule.', exception)
      
      success = false
      message = 'Failed to update granule.'
    }
    
    def messages = []
    if(success) {
      messages.add(['type': 'GranuleStatusUpdate', 'result': 'OK', 'message': message])
    } else {
      messages.add(['type': 'GranuleStatusUpdate', 'result': 'FAILED', 'message': message])
    }
    
    def result = ['items': messages]
    render result as JSON
  }
}
