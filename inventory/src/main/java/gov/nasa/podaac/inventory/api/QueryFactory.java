/**
 * 
 */
package gov.nasa.podaac.inventory.api;

import gov.nasa.podaac.inventory.core.QueryImpl;

/**
 * @author clwong
 * $Id: QueryFactory.java 291 2007-10-05 21:53:21Z clwong $
 */
public class QueryFactory {
	
	private static QueryFactory queryFactory = new QueryFactory();

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
		return new QueryImpl();
	 }

}
