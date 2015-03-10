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

import com.tilioteo.hypothesis.core.Messages;

/**
 * @author kamil
 * 
 *         Utilitiy to work with Hibernate
 * 
 */
public class HibernateUtil {
	
	public static final String CONTEXT_PARAM_HIBERNATE_CONFIG_LOCATION = "hibernateConfigLocation";

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
			log.trace("Using allready opened Hibernate Session.");
			return session;
		} else {
			log.trace("Opening new Hibernate Session.");
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
					Messages.getString("Error.SessionFactoryInitialization"));
		}
	}

	public static void initSessionFactory(ServletContext servletContext)
			throws ExceptionInInitializerError {
		log.trace("Initializing Hibernate SessionFactory...");

		if (HibernateUtil.servletContext != servletContext) {
			log.trace("New ServletContext provided.");
			HibernateUtil.servletContext = servletContext;
			shutdown();
		}

		if (null == sessionFactory) {
			try {
				String configFileName = servletContext
						.getInitParameter(CONTEXT_PARAM_HIBERNATE_CONFIG_LOCATION);

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
			log.trace("Hibernate SessionFactory is already initialized");
		}
	}

	public static void beginTransaction() {
		final Session session = getSession();

		if (!session.getTransaction().isActive()) {
			log.trace("Opening database transaction.");
			session.beginTransaction();
		} else {
			log.trace("Session already has an active database transaction.");
		}
	}

	public static void commitTransaction() {
		final Session session = getSession();

		if (session.getTransaction().isActive()) {
			log.trace("Committing active database transaction.");
			session.getTransaction().commit();
		} else {
			log.trace("Session has no active database transaction to commit.");
		}
	}

	public static void rollbackTransaction() {
		final Session session = getSession();

		if (session.getTransaction().isActive()) {
			log.trace("Rollbacking active database transaction.");
			session.getTransaction().rollback();
		} else {
			log.trace("Session has no active database transaction to rollback.");
		}
	}

	public static void closeSession() {
		log.trace("Closing Hibernate Session.");
		final Session session = getSessionFactory().getCurrentSession();

		commitTransaction();
		session.flush();

		if (session.isOpen()) {
			log.trace("Close opened Hibernate Session.");
			session.close();
		}
	}

	public static void shutdown() {
		log.trace("Closing Hibernate SessionFactory.");
		if (sessionFactory != null) {
			sessionFactory.close();
			sessionFactory = null;
		} else {
			log.trace("Hibernate SessionFactory was not initialized.");
		}
	}
}
