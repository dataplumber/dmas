/**
 * 
 */
package gov.nasa.jpl.horizon.sigevent.api



/**
 * @author axt
 *
 */
public enum EventType {
   Info("INFO"),
   Warn("WARN"),
   Error("ERROR");
   
   final String value
   
   public EventType(value) {
      this.value = value
   }
}
