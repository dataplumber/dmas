class AuthenticationFilters {
   def filters = {
       loginCheck(controllerExclude:'auth') {
           before = {
			   log.debug("In filter: ${controllerName} ${actionName}.")
			   if(controllerName == 'auth'){
				    //Allow access to the /auth controllers
			   		return true;
			   }
			   if(!session.loggedIn && (!actionName.equals('login') && !actionName.equals('doLogin'))) {
                  redirect(controller:'session',action:'login')
                  return false
               }
			   if(session.loggedIn && session.admin){
				   return true;
			   }
			   if(session.loggedIn){
				    //logged in, not admin
				    //default controller/index.html
				    if(controllerName == 'session'){
					   return true;
				    }
					if(controllerName == null){ 
				   		return true;
					}//ingSystemUser, change password
				    else if(controllerName == 'ingSystemUser' && (actionName == 'changePassword' || actionName == 'updatePass')){
						log.debug("returning true")
				   		return true;
				    }else{
						flash.message = 'You are not allowed access to the specified resource.';
						redirect(uri:'/')
				    	return false;
					}
			   }
           }
       }
   }
}
