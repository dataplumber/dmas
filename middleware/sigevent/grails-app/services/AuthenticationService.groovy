/**
 * 
 */
import gov.nasa.jpl.horizon.sigevent.SysUser
import gov.nasa.jpl.horizon.sigevent.SysRole

import gov.nasa.podaac.security.client.*
import gov.nasa.podaac.security.client.api.*

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class AuthenticationService {
   private static final String GUEST_USERNAME = "guest"
   private static final String GUEST_ROLE = "Guest"
   
   static transactional = true

   def authenticate(username, password) {
      def result = null
      
      if (!username || !password) {
         result = [
            "username": GUEST_USERNAME,
            "role": GUEST_ROLE,
            "admin": false
         ]
      } else {
         if (ConfigurationHolder.config.gov.nasa.podaac.security.service.enable) {
            def host = ConfigurationHolder.config.gov.nasa.podaac.security.host
            def port = ConfigurationHolder.config.gov.nasa.podaac.security.port
            def realm = ConfigurationHolder.config.gov.nasa.podaac.security.realm
            def role = ConfigurationHolder.config.gov.nasa.podaac.security.role
            
            SecurityAPI sapi =  SecurityAPIFactory.getInstance(host, port)
            if (sapi.authenticate(username, password, realm)) {
               def roles = sapi.getRoles(username, realm)
               log.info(roles)
               result = [
                  "username": username,
                  "role": roles ? roles[0] : GUEST_ROLE,
                  "admin": roles && roles.contains(role)
               ]
            }
         } else {
            def user = SysUser.findByUsernameAndPassword(
               username,
               DigestUtility.getDigest("SHA-1", password).toLowerCase()
            )
            if(user) {
               result = [
                  "username": user.username,
                  "role": user.role.name,
                  "admin": (user.role.admin == 1) ? true : false
               ]
            }
         }
      }
      
      return result
   }
}
