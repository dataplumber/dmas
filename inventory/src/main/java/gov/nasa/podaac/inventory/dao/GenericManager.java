// Copyright 2007, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.dao;

import gov.nasa.podaac.inventory.hibernate.HibernateSessionFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.metadata.ClassMetadata;

/**
 * This class implements the generic data access operations using Hibernate APIs.
 *
 * @author clwong
 * @version $Id: GenericManager.java 6645 2011-01-21 17:59:10Z gangl $
 * @param <T> persistent class
 * @param <ID> sequence id used as primary key
 */
public abstract class GenericManager<T, ID extends Serializable>
    implements GenericDAO<T, ID> {

	private static Log log = LogFactory.getLog(GenericManager.class);
	private static final int MAXRESULT = 10000;
	
	private Class<T> entity;
	private Session session;

	public GenericManager() {
		this.entity = (Class<T>) ((ParameterizedType) getClass()
                            .getGenericSuperclass()).getActualTypeArguments()[0];
	}
	
	public void setSession(final Session s) {
		this.session = s;
	}

	protected Session getSession() {
		if (session == null) {
			session = HibernateSessionFactory.getInstance().getCurrentSession();
		}
		return session;
	}
	
	protected ClassMetadata getClassMetadata() {
		return HibernateSessionFactory.getInstance().getClassMetadata(getEntity());
	}
	
	protected NamingStrategy getNameMapping() {
		return HibernateSessionFactory.getConfiguration().getNamingStrategy();
	}

	protected PersistentClass getClassMapping() {
		return HibernateSessionFactory.getConfiguration()
				.getClassMapping(getEntity().getSimpleName());
	}

	protected boolean isCollection(String prop) {
		if (getClassMetadata().getPropertyType(prop).isCollectionType())
			return true;
		else
			return false;
	}
	
	protected String getIdName () {
		return getClassMetadata().getIdentifierPropertyName();
	}
	
	protected String getName () {
		return getEntity().getSimpleName();
	}
	
	protected String getLowerCaseName () {
		return getName().toLowerCase();
	}
	
	public Class<T> getEntity() {
		return entity;
	}

	//------- Basic Methods--------
	
	public T create(final T entity) {
		try {
			getSession().save(entity);
		} catch (HibernateException e) {
			log.error(e.getMessage());
		}
		return entity;
	}

	public T save(final T entity) throws HibernateException {
		try {
			getSession().saveOrUpdate(entity);
		} catch (HibernateException e) {
			log.error(e.getMessage());
			e.printStackTrace();
			throw e;
		}
		return entity;
	}
	
	@SuppressWarnings("unchecked")
	public T read(ID id) {
		return (T) getSession().get(getEntity(), id);
	}

	@SuppressWarnings("unchecked")
	public T findById(ID id) {
		return (T) getSession().get(getEntity(), id);
	}
	
	public void update(T entity) {
		try {
			getSession().update(entity);
		} catch (HibernateException e) {
			e.printStackTrace();
			log.error(e.getMessage());	
		}
	}
	
	public void delete(T entity) {
		try {
			getSession().delete(entity);
		} catch (ObjectNotFoundException e) {			
		} catch (HibernateException e) {
			log.error(e.getMessage());	
		}
	}

	public void flush() {
		getSession().flush();
	}

	public void clear() {
		getSession().clear();
	}

	//--------- Criteria Query --------------
	public List<T> findAll() {
		return findByCriteria();
	}
	
	public List<T> findByPropertyName(String propertyName, Object value) {
		log.debug(propertyName+":"+value);
		return findByCriteria(Restrictions.eq(propertyName, value));
	}
	
	@SuppressWarnings("unchecked")
	public List<T> findByExample(T exampleInstance, String... excludeProperty) {
		Criteria crit = getSession().createCriteria(getEntity());
		Example example =  Example.create(exampleInstance);
		for (final String exclude : excludeProperty) {
			example.excludeProperty(exclude);
		}
		crit.add(example);
		return crit.setMaxResults(MAXRESULT).list();
	}
	
	@SuppressWarnings("unchecked")
	protected List<T> findByCriteria(Criterion... criterion) {
		Criteria crit = getSession().createCriteria(getEntity());
		for (Criterion c : criterion) {
			crit.add(c);
		}
		return crit.setMaxResults(MAXRESULT).list();
	}

	//------------ Criteria Query with JOIN Mode --------------
	@SuppressWarnings("unchecked")
	public List<T> fetch(T exampleInstance, String... joinProperties) {
		Criteria crit = getSession().createCriteria(getEntity());
		Example example =  Example.create(exampleInstance);
		for (final String prop : joinProperties) {
			crit.setFetchMode(prop, FetchMode.JOIN);
			if (isCollection(prop)) {
				crit.add(Restrictions.isNotEmpty(prop));
			}
		}
		crit.add(example);
		return crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.setMaxResults(MAXRESULT).list();
	}

	public List<T> fetchByProperties(Properties properties, String... joinProperties) {
		Criteria crit = getSession().createCriteria(getEntity());
		Set<Entry<Object, Object>> entrySet = properties.entrySet();
		for (Entry entry : entrySet) {
			crit.add(Restrictions.eq(
				entry.getKey().toString(), entry.getValue()));
		}		
		for (final String prop : joinProperties) {
			log.debug("Join property=" + prop);
			crit.setFetchMode(prop, FetchMode.JOIN);
			if (isCollection(prop)) {
				crit.add(Restrictions.isNotEmpty(prop));
			}		
		}	
		return crit.setMaxResults(MAXRESULT)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	public T fetchById(ID id, String... dependencies) {
		Criteria crit = getSession().createCriteria(getEntity());
		crit.add(Restrictions.idEq(id));
		log.debug("fetchById: "+id+":"+dependencies.length);
		if (dependencies.length==0) {
			log.debug("fetch all properties");
			dependencies = getClassMetadata().getPropertyNames();
			//crit.setFetchMode(dependency, FetchMode.JOIN);
		}
		for (final String dependency : dependencies) {
			log.debug("Join dependency=" + dependency);
			crit.setFetchMode(dependency, FetchMode.JOIN);
		}
		
		 return (T) crit.uniqueResult();
		//Collection<T> c = new LinkedHashSet<T>(crit.list());
		//return (c.iterator().next());
		//return (T)
	}
	
	public T excludeFetchById(ID id, String... excludes) {
		Criteria crit = getSession().createCriteria(getEntity());
		crit.add(Restrictions.idEq(id));
		log.debug("excludeFetchById: "+id+":"+excludes.length);
		List<String> dependencies = 
			new ArrayList<String>(Arrays.asList(getClassMetadata().getPropertyNames()));
		for (final String exclude : excludes) dependencies.remove(exclude);
		for (String dependency : dependencies) {
			log.debug("Join dependency=" + dependency);
			crit.setFetchMode(dependency, FetchMode.JOIN);
		}
		return (T)crit.uniqueResult();
	}

	//---------- HQL Query ------------------	
	public List<Integer> listId(String...whereClause) {
		String hql = "select " + getIdName() +
					 " from " + getName() +
					 " as " + getLowerCaseName();
		hql += composeWhereClause(whereClause);
		hql += " order by "+getIdName();
		log.debug("listId:"+hql);
		return (List<Integer>) getSession().createQuery(hql).list();
	}
	
	public List<T> fetch(String[] joinProperties, String...whereClause) {
		String hql = composeFetchHQL(joinProperties, whereClause);
		return getDistinctRootEntityList(hql); // 
		//return getSession().createQuery(hql).list();
	}
	
	public Integer sizeOfJoin(String[] joinProperties, String...whereClause) {
		String alias = getLowerCaseName();
		String hql = getName() + " as " + alias;
		hql += composeJoinClause(alias, joinProperties);
		hql += composeWhereClause(whereClause);
		/* slow performance with this check
		String collectionSizeCheck = composeCollectionSizeCheck(alias, joinProperties);
		if (!collectionSizeCheck.equals("")) {
			if (hql.contains("where")) hql += " and " + composeCollectionSizeCheck(alias, joinProperties);
			else hql += " where " + composeCollectionSizeCheck(alias, joinProperties);
		}*/
		return size(hql);
	}
	public Integer size() {
		return size(getName());
	}
	
	public Integer size(String[] whereClause) {
		String from = getName() + " as " + getLowerCaseName();
		return size(from, whereClause);
	}
	
	public Integer size(String fromClause, String...whereClause){
		String hql = "select count(*) from " + fromClause +
						composeWhereClause(whereClause);
		log.debug(hql);
		return  (Integer) ((Long) getSession().createQuery(hql).uniqueResult()).intValue();
	}

	protected List<T> fetchAll(String...whereClause) {
		String hql = "from " + getName()
					 + " fetch all properties"
					 + composeWhereClause(whereClause);
		log.debug("composeFetchHQL:"+hql);
		return getSession().createQuery(hql).setMaxResults(MAXRESULT).list();
	}

	protected String composeWhereClause(String...whereClause) {
		if (!(whereClause.length>0)) return "";
		String where = " where (" + whereClause[0]+")";
		for (int i=1; i<whereClause.length; i++) {
			where += " and (" + whereClause[i]+")";
		}
		return where;
	}
	
	protected String composeJoinClause(String alias, String[] props) {
		String join = "";
		for (String prop : props) {
			join += " join " + alias + "." + prop;
		}
		return join;
	}
	
	protected String composeJoinFetchClause(String alias, String[] props) {
		String joinFetch = "";
		for (String prop : props) {
			joinFetch += " join fetch " + alias + "." + prop;
		}
		return joinFetch;
	}
	
	protected String composeCollectionSizeCheck(String alias, String[] props) {
		if (!(props.length>0)) return "";
		String joinSize = "";
		List<String>collProps = new ArrayList<String>();
		for (String prop : props) if (isCollection(prop)) collProps.add(prop);
		
		if (!collProps.isEmpty()) {
			joinSize += " ((size("+alias + "." + collProps.get(0)+")>0)";
			for (int i=1; i<collProps.size(); i++) {
				joinSize += " or (size("+alias + "." + collProps.get(i)+")>0)";
			}
			joinSize += ")";
		}
		return joinSize;
	}
	
	protected String composeFetchHQL(String[]joinProperties, String...whereClause) {
		// compose hql for object query
		String alias = getLowerCaseName();
		String hql = "from " + getName() + " as " + alias;
		hql += composeJoinFetchClause(alias, joinProperties);
		hql += composeWhereClause(whereClause);
		
		/* slow performance with this check
		String collectionSizeCheck = composeCollectionSizeCheck(alias, joinProperties);
		if (!collectionSizeCheck.equals("")) {
			if (hql.contains("where")) hql += " and "+composeCollectionSizeCheck(alias, joinProperties);
			else hql += " where " + composeCollectionSizeCheck(alias, joinProperties);
		}
		*/
		
		log.debug("composeFetchHQL:"+hql);
		return hql;
	}
	
	protected List<T> getDistinctList(String hql) {
		return new LinkedList(getSession().createQuery(hql).setMaxResults(MAXRESULT).list()); 
	}
	
	protected List<T> getDistinctRootEntityList(String hql) {
		return getSession().createQuery(hql)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setMaxResults(MAXRESULT).list();
	}

	//----- XML handler------------------
	@SuppressWarnings("unchecked")
	public Document toXML() {
	
	    Document doc = DocumentFactory.getInstance().createDocument();
	    Element root = doc.addElement(getEntity().getSimpleName()+"List");
	    
	    Session dom4jSession = getSession().getSession(EntityMode.DOM4J);
	    int maxSize = (getName().equalsIgnoreCase("granule") && size() > 10) ? 10 : MAXRESULT;
	    List<T> list = dom4jSession.createQuery("from "+getName()).setMaxResults(maxSize).list();
    	if (list.size()==maxSize) 
    		log.info(getName()+"List may have more than "+maxSize+" entries");

	    for (T element : list)  {
	    	root.add((Element) element );
	    }
	    log.debug(doc.asXML());
		return doc;
	}

    public void toXMLFile(Document doc, String filename) {
        try {
        	log.info("toXMLFile: "+filename);
        	OutputFormat format = OutputFormat.createPrettyPrint();
        	XMLWriter writer = new XMLWriter(
        							new FileWriter(filename), format
        							);
        	writer.write(doc);
        	writer.close();
        } catch (IOException e) {
        	log.error(e.getMessage());	
        }
    }
}
