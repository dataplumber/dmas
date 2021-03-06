package gov.nasa.jpl.horizon.api

class ProductFile {

   String name
   String source
   String sourceUsername
   String sourcePassword
   int maxConnections = 1
   String destination
   long size = 0
   String checksum = ""
   Date startTime
   Date stopTime
   String description

   String toString() {
      return "name=$name, source=$source, size=$size, checksum=$checksum"
   }
}
