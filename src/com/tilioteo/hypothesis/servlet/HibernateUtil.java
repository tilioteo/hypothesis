/**
 * 
 */
package com.tilioteo.hypothesis.servlet;

import java.io.File;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/**
 * @author kamil
 * 
 *         Utilitiy to work with Hibernate
 * 
 */
public class HibernateUtil {

	private static Logger log = Logger.getLogger(HibernateUtil.class);

	private static SessionFactory sessionFactory = null;
	private static ServiceRegistry serviceRegistry = null;
	private static ServletContext servletContext = null;

	/**
	 * All hibernate operations take place within a session. The session for the
	 * current thread is provided here.
	 */
	public static Session getSession() throws NullPointerException {
		Session session = getSessionFactory().getCurrentSession();
		if (session.isOpen()) {
			log.debug("Using allready opened Hibernate Session.");
			return session;
		} else {
			log.debug("Opening new Hibernate Session.");
			return sessionFactory.openSession();
		}
	}

	public static SessionFactory getSessionFactory()
			throws NullPointerException {
		if (sessionFactory != null) {
			return sessionFactory;
		} else {
			log.error("Hibernate SessionFactory not yet initialized.");
			throw new NullPointerException(
					"Hibernate SessionFactory not yet initialized.");
		}
	}

	public static void initSessionFactory(ServletContext servletContext)
			throws ExceptionInInitializerError {
		log.debug("Initializing Hibernate SessionFactory...");

		if (HibernateUtil.servletContext != servletContext) {
			log.debug("New ServletContext provided.");
			HibernateUtil.servletContext = servletContext;
			shutdown();
		}

		if (null == sessionFactory) {
			try {
				String configFileName = servletContext
						.getInitParameter("hibernateConfigLocation");

				Configuration configuration = null;
				if (null == configFileName || configFileName.length() == 0) {
					log.debug("Creating new Hibernate SessionFactory using default configuration file location.");
					configuration = new Configuration().configure();
				} else {
					configFileName = servletContext.getRealPath(configFileName);
					log.debug(String
							.format("Creating new Hibernate SessionFactory using configuration file %s.",
									configFileName));
					File configFile = new File(configFileName);
					configuration = new Configuration().configure(configFile);
				}

				serviceRegistry = new StandardServiceRegistryBuilder()
						.applySettings(configuration.getProperties()).build();
				sessionFactory = configuration
						.buildSessionFactory(serviceRegistry);
			} catch (Throwable ex) {
				// Make sure you log the exception, as it might be swallowed
				log.error("Initial SessionFactory creation failed.");
				System.err.println("Initial SessionFactory creation failed."
						+ ex);
				throw new ExceptionInInitializerError(ex);
			}
		} else {
			log.debug("Hibernate SessionFactory is already initialized");
		}
	}

	public static void beginTransaction() {
		final Session session = getSession();

		if (!session.getTransaction().isActive()) {
			log.debug("Opening database transaction.");
			session.beginTransaction();
		} else {
			log.debug("Session already has an active database transaction.");
		}
	}

	public static void commitTransaction() {
		final Session session = getSession();

		if (session.getTransaction().isActive()) {
			log.debug("Committing active database transaction.");
			session.getTransaction().commit();
		} else {
			log.debug("Session has no active database transaction to commit.");
		}
	}

	public static void rollbackTransaction() {
		final Session session = getSession();

		if (session.getTransaction().isActive()) {
			log.debug("Rollbacking active database transaction.");
			session.getTransaction().rollback();
		} else {
			log.debug("Session has no active database transaction to rollback.");
		}
	}

	public static void closeSession() {
		log.debug("Closing Hibernate Session.");
		final Session session = getSessionFactory().getCurrentSession();

		commitTransaction();

		if (session.isOpen()) {
			log.debug("Close opened Hibernate Session.");
			session.close();
		}
	}

	public static void shutdown() {
		log.debug("Closing Hibernate SessionFactory.");
		if (sessionFactory != null) {
			sessionFactory.close();
			sessionFactory = null;
		} else {
			log.debug("Hibernate SessionFactory was not initialized.");
		}
	}
}
