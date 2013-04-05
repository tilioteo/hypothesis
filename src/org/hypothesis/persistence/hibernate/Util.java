/**
 * 
 */
package org.hypothesis.persistence.hibernate;

import java.io.File;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class Util {

	private static/* final */SessionFactory sessionFactory = null;
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

				sessionFactory = new AnnotationConfiguration().configure(
						configFile).buildSessionFactory();
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
