package gov.nasa.podaac.security.server

class Realm {

	String name
	String description
	Verifier verifier
	Integer tokenExpiration = 0 //How many days old a token must be before it expires. 
								 //0 means it will never expire.
	
	static hasMany = [roles:Role, tokens:Token]
	
	
	public String toString(){
		return name
	}
	
}
