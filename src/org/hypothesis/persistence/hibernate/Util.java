/**
 * 
 */
package org.hypothesis.persistence.hibernate;

import java.io.File;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class Util {

	private static SessionFactory sessionFactory = null;
	private static ServiceRegistry serviceRegistry = null;
	private static String CONFIG_FILE_LOCATION = "hibernate.cfg.xml";

	/**
	 * All hibernate operations take place within a session. The session for the
	 * current thread is provided here.
	 */
	public static Session getSession() {
		Session session = sessionFactory.getCurrentSession();
		if (session.isOpen())
			return session;
		else
			return sessionFactory.openSession();
	}

	public static SessionFactory getSessionFactory() {
		return getSessionFactory(CONFIG_FILE_LOCATION);
	}

	public static SessionFactory getSessionFactory(String configFileName) {
		if (sessionFactory == null) {
			try {
				// Create the SessionFactory from configFileName
				File configFile = new File(configFileName);
				
				Configuration configuration = new Configuration().configure(
						configFile);
				serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();        
			    sessionFactory = configuration.buildSessionFactory(serviceRegistry);
			} catch (Throwable ex) {
				// Make sure you log the exception, as it might be swallowed
				System.err.println("Initial SessionFactory creation failed. "
						+ ex);
				throw new ExceptionInInitializerError(ex);
			}
		}

		return sessionFactory;
	}

	public static void shutdown() {
		sessionFactory.close();
	}
}
