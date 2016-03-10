import gov.nasa.jpl.horizon.api.Encrypt

import gov.nasa.podaac.security.client.*;
import gov.nasa.podaac.security.client.api.*;
import org.codehaus.groovy.grails.commons.*

class AuthenticationService {
  boolean transactional = true
  
  private static SecurityAPI sapi = null;
  
  //cache looks like [user:[pass:PASSWORD,time:TIMECACHED]]
  def cacheMap = [:]
  
  
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
     println "username, password: $username:$password"
     return IngSystemUser.findWhere(name: username, password: Encrypt.encrypt(password))
  }

  public boolean authenticate(String userName, String password) {
	  
	  
	  def useCache = (boolean)ConfigurationHolder.config.gov.nasa.podaac.auth.cache.useCache
	  def limit = (Integer)ConfigurationHolder.config.gov.nasa.podaac.auth.cache.timeLimit = 2
	  
	   log.debug("Authenticating...")
	   def start = new Date().getTime();
	   if(userName == null || password == null)
	  		return false;
	  
		 def encPass = Encrypt.encrypt(password);
		
		 
		  if(cacheMap.get(userName)?.pass?.equals(encPass))
		  {
			  if(limit != 0){
				  def diff = start - cacheMap.get(userName)?.time
				  log.debug("Diff: $diff")
				  //if(diff > (limit * 60 * 60 * 1000)) //2 hours in miliseconds
				  if(diff > (limit * 60 * 1000)) //2 minutes in miliseconds
				  {
					  log.debug("invalidating cache, must re-auth")
				  } 
				  else{
					  def stop = new Date().getTime();
					  log.debug("Auth took " + (stop-start)/1000 + " seconds to complete")
					  log.debug("User in Cache, returning true")
					  return true;
				  }
			  }
			  else{
				  def stop = new Date().getTime();
				  log.debug("Auth took " + (stop-start)/1000 + " seconds to complete")
				  log.debug("User in Cache, returning true")
				  return true;
			  }
		  }
		  
      //def systemUser = IngSystemUser.findByNameAndPassword(userName, encPass)
		def systemUser = null;
		
		def host = (String)ConfigurationHolder.config.gov.nasa.podaac.security.host
		def port = (Integer)ConfigurationHolder.config.gov.nasa.podaac.security.port
		def realm = (String)ConfigurationHolder.config.gov.nasa.podaac.security.realm
		def roles = ConfigurationHolder.config.gov.nasa.podaac.security.roles
		
		sapi =  SecurityAPIFactory.getInstance(host, port)
		if(sapi.authenticate(userName,password,realm))
		{
			log.debug("Size: " + roles.size())
		  if(roles != null && roles.size() > 0){
			  def found = false;
			  log.debug("Authorizing users")
			  def allowedRoles = sapi.getRoles(userName,realm)
			  log.debug("System Roles: $roles")
			  log.debug("AllowedRoles: $allowedRoles")
			  for(String s: allowedRoles){
				  if(roles.contains(s)){
					  log.debug("Roles contains $s")
					  found = true;
					  break;
				  }
			  }
			  if(!found){
				  log.debug("Roles not found, not authorizing")
				  return false;
			  }
		  }	
			
		  if(useCache)	
		  	cacheMap.put(userName,[pass:encPass, time:start])
		  def stop = new Date().getTime();
		  log.debug("*** New Auth took " + (stop-start)/1000 + " seconds to complete")
		  
		  return true
	  }
	  else
	  {
		  def stop = new Date().getTime();
		  log.debug("Auth took " + (stop-start)/1000 + " seconds to complete")
		  return false
	  }

   }

}
