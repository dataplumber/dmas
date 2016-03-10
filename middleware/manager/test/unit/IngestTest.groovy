import groovy.util.XmlSlurper

class QueryString {
   Map params = [:]
   
   QueryString(Map params) {
      if (params) {
         this.params.putAll(params)
      }
   }
   
   void add(String name, Object value) {
      params.put(name, value)
   }
   
   String toString() {
      def list=[]
      params.each{name, value->
         list << "$name=" + URLEncoder.encode(value.toString())
      }
      return list.join("&")
   }
}

class Get{
   String url
   QueryString queryString = new QueryString()
   URLConnection connection
   String text
   
   String getText() {
      def thisUrl = new URL(this.toString())
      connection = thisUrl.openConnection()
      if(connection.responseCode == 200) {
         return connection.content.text
      } else {
         return "Something bad happended\n" + 
         "URL: " + this.toString() + "\n" +
         connection.responseCode + ": " + 
         connection.responseMessage
      }
   }
   
   String toString() {
      return url + "?" + queryString.toString()
   }
}

class Post {
   String url
   QueryString queryString = new QueryString()
   URLConnection connection
   String text
   String body
   
   String getText() {
      def thisUrl = new URL(url)
      connection = thisUrl.openConnection()
      connection.setRequestMethod("GET")
      connection.setRequestProperty("Content-Type", "application/xml")
      connection.doOutput = true
      Writer writer = new OutputStreamWriter(connection.outputStream)
      writer.write(body) //queryString.toString())
      writer.flush()
      writer.close()
      connection.connect()
      return connection.content.text
   }
   
   String toString() {
      return "POST:\n" + url + "\n" + queryString.toString()
   }
}

def post = new Post(url:"http://localhost:8090/ingest-grails/ingest/init")
//post.queryString.add("user", "thuang")
//post.queryString.add("pass", "70426888760e3c63fc010b7be09481ec")
post.body = "<request><user>thuang</user><pass>70426888760e3c63fc010b7be09481ec</pass></request>"
def result = post.text
println result
def postresult = new XmlSlurper().parseText(result)
println postresult.@success

def request = 
"""
<request>
   <command>ADD</command>
   <sessionid>sdfsdfsdf</sessionid>
   <transactionid></transactionid>
   <
</request>
"""

/*
def get = new Get(url:"http://localhost:8090/ingest-grails/ingest/init")
get.queryString.add("request.user", "thuang")
get.queryString.add("request.pass", "70426888760e3c63fc010b7be09481ec")
print get.text
*/
