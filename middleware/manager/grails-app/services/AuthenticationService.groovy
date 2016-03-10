import gov.nasa.jpl.horizon.api.Encrypt

import gov.nasa.podaac.security.client.*;
import gov.nasa.podaac.security.client.api.*;

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class AuthenticationService {
  boolean transactional = true
  
  boolean isLoggedIn(session) {
    return (session['monitor.user'] != null)
  }
  
  void logIn(session, user) {
    session['monitor.user'] = user
  }
  
  void logOut(session) {
    session['monitor.user'] = null
  }
  
  IngSystemUser verifyUser(String username, String password) {
    return IngSystemUser.findWhere(name: username, password: Encrypt.encrypt(password))
  }
  
  boolean authenticate(String username, String password) {
     def host = ConfigurationHolder.config.gov.nasa.podaac.security.host
     def port = ConfigurationHolder.config.gov.nasa.podaac.security.port
     def realm = ConfigurationHolder.config.gov.nasa.podaac.security.realm
     
     SecurityAPI sapi =  SecurityAPIFactory.getInstance(host, port)
     return sapi.authenticate(username, password, realm)
  }
}
