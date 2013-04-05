/**
 * 
 */
package org.hypothesis.common.application;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.hypothesis.common.file.ConfigFile;
import org.hypothesis.persistence.hibernate.Util;

import com.vaadin.Application;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Application context reads and initializes user application
 *         configuration and adds log support
 * 
 */
public class ApplicationsContext {

	private static final String PROPERTY_CONFIG_FILE = "configFile";
	private static final String PROPERTY_LOG4J_CONFIG_FILE = "log4jConfigFile";

	private static Logger logger = Logger.getLogger(ApplicationsContext.class);

	private static ApplicationsContext instance;

	public static final ApplicationsContext getInstance(Application application) {
		if (instance == null) {

			instance = new ApplicationsContext(application);
		}

		return instance;
	}

	private ApplicationConfig appConfigFile = null;
	private boolean initialized = false;

	private String error = "";

	private ApplicationsContext(Application application) {
		initFrom(application);
	}

	public ApplicationConfig getConfig() {
		return appConfigFile;
	}

	public String getError() {
		return error;
	}

	private void initConfig(Application application) {
		String configFileName = application.getProperty(PROPERTY_CONFIG_FILE);
		logger.info(String
				.format("Application config file: %s", configFileName));
		File dir = application.getContext().getBaseDirectory();

		if (configFileName != null && configFileName.length() > 0) {
			String realName = dir.getAbsolutePath() + configFileName;
			logger.info(String.format("Application config file: %s", realName));
			ConfigFile configFile = new ConfigFile(realName);
			if (!configFile.exists()) {
				logger.fatal(String.format("Config file: %s was not found.",
						realName));
				appConfigFile = null;
			} else {
				appConfigFile = new ApplicationConfig(dir.getAbsolutePath());
				appConfigFile.open(configFile);
			}
		}
	}

	private void initFrom(Application application) {
		if (!initialized) {
			try {
				initLogger(application);
				initConfig(application);
				initHibernate();

				initialized = true;
			} catch (Throwable e) {
				if (e.getMessage() != null)
					error = e.getMessage();
			}
		}
	}

	private void initHibernate() {
		try {
			if (appConfigFile.getHibernateConfigName().length() == 0)
				throw new Exception(
						"Hibernate configuration file not defined properly.");

			Util.getSessionFactory(appConfigFile
					.getHibernateConfigName());
		} catch (Throwable t) {
			logger.fatal("Hibernate session factory creation failed.", t);
			throw new ExceptionInInitializerError(
					"Hibernate session factory creation failed.");
		}
	}

	private void initLogger(Application application) {
		String configFileName = application
				.getProperty(PROPERTY_LOG4J_CONFIG_FILE);
		File dir = application.getContext().getBaseDirectory();

		if (configFileName != null && configFileName.length() > 0) {
			String realName = dir.getAbsolutePath() + configFileName;

			try {
				DOMConfigurator.configure(realName);
				logger.info(String.format("Log4j config file: %s", realName));
			} catch (Throwable e) {
				error = e.getMessage();
				// servletContext.log("ERROR: Loading log4j config file: " +
				// realName + ".");
			}
		}
	}

	public boolean isInitialized() {
		return initialized;
	}

}
