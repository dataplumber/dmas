package gov.nasa.podaac.security.server

class Role {

	static belongsTo = [realm:Realm]
	
	String roleName
	String roleGroup
	
	public String toString(){
		return "${realm.getVerifier().toString()}($roleName - > $roleGroup)"
	}
	
}
