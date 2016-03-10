// Copyright 2007, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.

package gov.nasa.podaac.inventory.hibernate;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * @author clwong
 * @version $Id: HibernateSessionFactory.java 3573 2009-08-19 22:26:03Z thuang $
 * 
 */
public class HibernateSessionFactory {

	private static Log log = LogFactory.getLog(HibernateSessionFactory.class);
	private static Configuration configuration;
	
	/**
	 * Default constructor.
	 */
	private HibernateSessionFactory() {
	}

	/** The single instance of hibernate SessionFactory */
	private static org.hibernate.SessionFactory sessionFactory;

	/**
	 * Initialises the configuration if not yet done and returns the current
	 * instance
	 */
	public static SessionFactory getInstance() {
		if (sessionFactory == null)
			initSessionFactory();
		return sessionFactory;
	}

   public static void setSessionFactory(SessionFactory sf) {
      if (sessionFactory == null)
         sessionFactory = sf;
   }

	/**
	 * Returns the ThreadLocal Session instance. Lazy initialize the
	 * 
	 * <code>SessionFactory</code> if needed.
	 * 
	 * @return Session
	 * @throws HibernateException
	 */
	public Session openSession() {
		return sessionFactory.getCurrentSession();
	}

	/**
	 * The behaviour of this method depends on the session context you have
	 * configured. This factory is intended to be used with a hibernate.cfg.xml
	 * including the following property <property
	 * name="current_session_context_class">thread</property> This would return
	 * the current open session or if this does not exist, will create a new
	 * session
	 * 
	 * @return Session
	 */
	public Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	/**
	 * Initializes the sessionfactory in a safe way even if more than one thread
	 * tries to build a sessionFactory
	 */
	private static synchronized void initSessionFactory() {
		log.info("initSessionFactory:");
		if (sessionFactory == null) {
			try {
				String filename = System.getProperty("inventory.hibernate.config.file");
				if (filename == null)
				   filename = System.getProperty("horizon.inventory.hibernate.config.file");
				if (filename == null) {
					sessionFactory = new Configuration().configure().buildSessionFactory();
				} else {
					File configFile = new File(filename);
					configuration = new Configuration();
					configuration.configure(configFile);
					sessionFactory = configuration.buildSessionFactory();
					log.info("HibernateSessionFactory.initSessionFactory: "+configFile.toString());
				}
			} catch (Exception e) {
				HibernateSessionFactory.log.error("%%%% Error Creating HibernateSessionFactory %%%%\n"
						+ e.getMessage());
				//if (HibernateSessionFactory.log.isDebugEnabled()) 
				e.printStackTrace();			
				throw new HibernateException ("Could not initialize the Hibernate configuration");
			}
		}
	}
	
	/**
	 * Close session
	 */
	public static void close(){
		if (sessionFactory != null) {
			log.info("sessionFactory closed & set to null.");
			sessionFactory.close();
			sessionFactory = null;
		} log.info("sessionFactory null!");
	}

	public static Configuration getConfiguration() {
		return configuration;
	}

	public static void setConfiguration(Configuration configuration) {
		HibernateSessionFactory.configuration = configuration;
	}
}
