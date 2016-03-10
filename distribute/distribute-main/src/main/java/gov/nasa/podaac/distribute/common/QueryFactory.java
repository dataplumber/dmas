//Copyright 2008, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
package gov.nasa.podaac.distribute.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class contains query methods to the database via Inventory query api.
 *
 * @author clwong
 *
 * @version
 * $Id: Query.java 2385 2008-12-10 22:32:09Z clwong $
 */


public class QueryFactory {

	private static Log log = LogFactory.getLog(QueryFactory.class);
	private static QueryFactory queryFactory = new QueryFactory();
	private static Query self = null;
	
	/**
	 * Default constructor.
	 */
	private QueryFactory() {
	}

	/**
	 * Gets an instance of QueryFactory object.
	 * 
	 * @return QueryFactory object.
	 */
	 public static QueryFactory getInstance() {
	    return queryFactory;
	 }

	 public Query createQuery() {
		 if(self == null){
			 String propertyName = "gov.nasa.podaac.inventory.factory";
			 String defaultImplName = System.getProperty(propertyName,"gov.nasa.podaac.distribute.common.wsm.Query");
			 log.debug("Creating Factory: " + defaultImplName);
			 Class<?> defaultImpl;   
	            try
	            {
	                defaultImpl = Class.forName (defaultImplName);
	                QueryFactory.self = (Query)defaultImpl.newInstance();
	            }
	            catch (ClassNotFoundException cnfe)
	            {
	                System.err.println ("Error locating factory class '" + defaultImplName + "'. The property '" + propertyName
	                        + "' is incorrectly defined or the JAR file is either corrupt or incorrectly built.");
	            }
	            catch (IllegalAccessException iae)
	            {
	                System.err.println ("Error accessing factory class '" + defaultImplName + "'. The property '" + propertyName
	                        + "' is incorrectly defined or the JAR file is either corrupt or incorrectly built.");
	            }
	            catch (InstantiationException ie)
	            {
	                System.err.println ("Error creating factory object of class '" + defaultImplName + "'. The property '" + propertyName
	                        + "' is incorrectly defined or the JAR file is either corrupt or incorrectly built.");
	            }
			 
		 
		 }
		 return self;
	 }
	
	}
