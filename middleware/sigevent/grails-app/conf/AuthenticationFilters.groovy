class AuthenticationFilters {
   private static String GUEST_USERNAME = "guest"
   private static String GUEST_PASSWORD = "guest"
   private static final String GUEST_ROLE = "Guest"
   def authenticationService
   
   def filters = {
      authenticated(controller: "*", action: "(create|update|delete)") {
         before = {
            def username = params.username ? params.username : ""
            def password = params.password ? params.password : ""
            
            def result = authenticationService.authenticate(username, password)
            if(!result) {
               render(
                  text: '{"Response": {"Type": "ERROR", "Content": "Username and password did not match."}}',
                  contentType: 'application/json'
               )
               return false
            }
            
            if("delete".equals(actionName)) {
               if(!result.admin) {
                  render(
                     text: '{"Response": {"Type": "ERROR", "Content": "'+"Admin priviledge needed: "+actionName+'."}}',
                     contentType: 'application/json'
                  )
                  return false
               }
            } else if ("sysUser".equals(controllerName)) {
               // Check user is not guest when saving user specific settings
               if (result.role.equals(GUEST_ROLE)) {
                  render(
                     text: '{"Response": {"Type": "ERROR", "Content": "Non guest priviledge needed."}}',
                     contentType: 'application/json'
                  )
                  return false
               }
            }
            
            flash.username = result.username
            flash.role = result.role
            flash.admin = result.admin
            
            return true
         }
      }
   }
}
