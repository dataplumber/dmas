package gov.nasa.podaac.inventory.api;

import gov.nasa.podaac.inventory.core.ServiceImpl;
import gov.nasa.podaac.inventory.api.Service;

public class ServiceFactory {

	private static ServiceFactory serviceFactory = new ServiceFactory();

	/**
	 * Default constructor.
	 */
	private ServiceFactory() {
	}

	/**
	 * Gets an instance of QueryFactory object.
	 * 
	 * @return QueryFactory object.
	 */
	 public static ServiceFactory getInstance() {
	    return serviceFactory;
	 }

	 //URI is required by the webservice Impl
	 public Service createService(String URI, int PORT) {
		 return new ServiceImpl(URI, PORT);
	 }
	 
	 //this could be the hibernate impl
//	 public Service createQuery() {
//		return new ServiceImpl();
//	 }
	 
}
