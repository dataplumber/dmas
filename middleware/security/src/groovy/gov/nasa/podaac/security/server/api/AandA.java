package gov.nasa.podaac.security.server.api;

import java.util.Set;
import java.util.List;
import gov.nasa.podaac.security.server.Role;

public interface AandA {

	public boolean authenticate(String user, String pass);
	public boolean authorize(String user, String group);
	public List<String> getRoles(Set<Role> roles, String user);
	
}
