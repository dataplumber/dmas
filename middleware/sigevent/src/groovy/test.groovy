def url = new java.net.URL("http://localhost:8090/sigevent/group/save")
def connection = url.openConnection()
connection.requestMethod = 'POST'
//connection.setRequestProperty("Content-Type", "text-plain")

connection.doOutput = true

def writer = new OutputStreamWriter(connection.outputStream)
writer.write("type=INFO&category=NEW_PRODUCT")
writer.flush()
connection.connect()
result = connection.content.text
println result

connection = url.openConnection()
connection.requestMethod = 'POST'
//connection.setRequestProperty("Content-Type", "text-plain")

connection.doOutput = true

writer = new OutputStreamWriter(connection.outputStream)
writer.write("type=ERROR&category=NEW_PRODUCT")
writer.flush()
connection.connect()
result = connection.content.text
println result

connection = url.openConnection()
connection.requestMethod = 'POST'
//connection.setRequestProperty("Content-Type", "text-plain")

connection.doOutput = true

writer = new OutputStreamWriter(connection.outputStream)
writer.write("type=INFO&category=INGEST_STATS")
writer.flush()
connection.connect()
result = connection.content.text
println result

