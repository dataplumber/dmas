
import gov.nasa.podaac.security.server.*;
class BootStrap {

	def verifiers = ["LDAP","DATABASE"]
	def realms = [
			[name:"PODAAC-J1SLX",description:"Jason-1 streamline tool realm", verifier:"LDAP", tokenExpire:0, roles:[[name:"USER", group:"podaac.j1slx.dev"]]],
			[name:"PODAAC-MANAGER",description:"Manager Authentication realm", verifier:"DATABASE", tokenExpire:0, 
				roles:[
					[name:"ADMIN", group:"field:read_all"],
					[name:"READ_ALL", group:"field:read_all"],
					[name:"WRITE_ALL", group:"field:read_all"],
					[name:"DEV", group:"ar:devAddAndGet"],
					[name:"DE", group:"ar:deAddAndGet"]
					]
			],
			[name:"PODAAC-INVENTORY",description:"Inventory Authentication realm", verifier:"DATABASE", tokenExpire:0,
				roles:[
					[name:"ADMIN", group:"field:read_all"],
					[name:"READ_ALL", group:"field:read_all"],
					[name:"WRITE_ALL", group:"field:read_all"],
					[name:"DEV", group:"ar:devAddAndGet"],
					[name:"DE", group:"ar:deAddAndGet"]
				]
			],
			[name:"PODAAC-SIGEVENT",description:"Dev Sigevent realm", verifier:"LDAP", tokenExpire:0,
				roles:[
					[name:"USER", group:"podaac.dev.sigevent.user"],
					[name:"ADMIN", group:"podaac.dev.sigevent.admin"]
				]
			],
			[name:"PODAAC-SECURITY",description:"Security realm", verifier:"LDAP", tokenExpire:0,
				roles:[
					[name:"ADMIN", group:"podaac.dev.security.admin"]
				]
			],
			[name:"PODAAC-DMT",description:"DMT Authentication realm", verifier:"DATABASE", tokenExpire:0, roles:[]],
		]
	 
	
    def init = { servletContext ->
    
		verifiers.each{
			def v = Verifier.findByName(it)
			if(v==null)
			{
				Verifier ldapV = new Verifier();
				ldapV.setName(it);
				ldapV.save(flush: true);
			}
		}
	
	    realms.each {
			
			def rlm = Realm.findByName(it.name)
			if(rlm == null){
				Realm r = new Realm();
				r.name= it.name
				r.description=it.description
				r.tokenExpiration= it.tokenExpire
				def v = Verifier.findByName(it.verifier)
				r.setVerifier(v);
				r.save(flush: true);
				
				it.roles.each {	
					Role role = new Role();
					role.setRoleName(it.name)
					role.setRoleGroup(it.group)
					r.addToRoles(role)
				}
				r.save(flush: true);
			}
		}
			
//	
//		Realm r = new Realm();
//		r.name= "MANAGER"
//		r.description="The manager Service"
//		r.setVerifier(oracleV);
//		r.save(flush: true);
//			
//		
//		r = new Realm();
//		r.name= "J1SLX"
//		r.description="Jason-1 Streamline tool Service"
//		r.setVerifier(ldapV);
//		
//		Role role = new Role();
//		role.setRoleName("USER")
//		role.setRoleGroup('podaac.j1slx.dev')
//		
//		r.addToRoles(role)
//		r.save(flush: true);
				
	}
    def destroy = {
    }
}
