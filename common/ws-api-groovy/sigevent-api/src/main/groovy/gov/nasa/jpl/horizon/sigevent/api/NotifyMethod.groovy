/**
 * 
 */
package gov.nasa.jpl.horizon.sigevent.api

/**
 * @author axt
 *
 */
public enum NotifyMethod {
   Email("EMAIL"),
   Jms("JMS"),
   Multicast("MULTICAST");
   
   final String value
   
   public NotifyMethod(String value) {
      this.value = value
   }
}
