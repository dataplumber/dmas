// Copyright 2007, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;

/**
 * This class contains an interface shared by all data access objects.
 *
 * @author clwong
 * @version $Id: GenericDAO.java 2014 2008-09-29 15:40:32Z clwong $
 * @param <T> persistent class
 * @param <ID> sequence id used as primary key
 */
public interface GenericDAO<T, ID extends Serializable> {

	/** Persist the entity into database */
	T create(T entity);

	T save(T entity);

	/**
	 * Retrieve an entity that was previously persisted to the database using
	 * the indicated id as primary key
	 */
	T read(ID id);
	T findById(ID id);


	/** Save changes */
	void update(T entity);

	/** Remove an data entity from persistent storage in the database */
	void delete(T entity);

	/** Criteria Query */
	List<T> findAll();
	List<T> findByPropertyName(String propertyName, Object value);
	List<T> findByExample(T exampleInstance, String... excludeProperty);

	/** Criteria Query with JOIN Mode */	
	List<T> fetch(T exampleInstance, String... joinProperties);
	List<T> fetchByProperties(Properties properties, String... joinProperties);
	T fetchById(ID id, String... dependencies);
	T excludeFetchById(ID id, String... excludes);
	
	/** HQL Query */
	List<Integer> listId(String...whereClause);
	List<T> fetch(String[] joinProperties, String...whereClause);
	Integer sizeOfJoin(String[] joinProperties, String...whereClause);
	Integer size();
	Integer size(String[] whereClause);
	Integer size(String fromClause, String...whereClause);

	/**
	 * Affects every managed instance in the current persistence context!
	 */
	void flush();

	/**
	 * Affects every managed instance in the current persistence context!
	 */
	void clear();

	Document toXML();

	void toXMLFile(Document doc, String filename);
}
