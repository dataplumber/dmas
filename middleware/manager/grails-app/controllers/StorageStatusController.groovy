import grails.converters.JSON

class StorageStatusController {
  private static final Map IMPORTANCE = [
    High: "High",
    Medium: "Medium",
    Low: "Low"
  ]
  private static final String MESSAGE_TYPE_STORAGE_STATUS = 'StorageStatus'
  
  def index = {
    def storages = IngStorage.findAll()
    
    def messages = []
    storages.each {storage ->
      def message = [:]
      message['type'] = MESSAGE_TYPE_STORAGE_STATUS
      message['importance'] = IMPORTANCE.Low
      message['storageName'] = storage.name
      message['spaceReserved'] = storage.spaceReserved
      message['spaceUsed'] = storage.spaceUsed
      message['ratio'] = String.format("%.2f", ((float)storage.spaceUsed / (float)storage.spaceReserved))
      message['message'] = 'Message'
      messages.add(message)
    }
    
    def result = ['items': messages]
    render result as JSON
  }
}
