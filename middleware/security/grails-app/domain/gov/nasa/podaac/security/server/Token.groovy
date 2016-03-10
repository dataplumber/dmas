package gov.nasa.podaac.security.server

class Token {
	
	static belongsTo = [ realm:Realm]
	String user
	String token
	Long createDate = new Date().getTime();	
	
	static mapping = {
		user column: 'USER_NAME'
	}
}
