/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.context;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public class LogUtil {

	public static final String CONTEXT_PARAM_LOG4J_CONFIG_LOCATION = "log4jConfigLocation";

	private static final Logger log = Logger.getLogger(LogUtil.class);

	private static boolean initialized = false;

	public static void initLogging(ServletContext servletContext) {
		if (!initialized) {
			String configFileName = servletContext.getInitParameter(CONTEXT_PARAM_LOG4J_CONFIG_LOCATION);

			if (null != configFileName && configFileName.length() > 0) {
				configFileName = servletContext.getRealPath(configFileName);

				try {
					DOMConfigurator.configure(configFileName);
					log.info(String.format("Log4j config file: %s", configFileName));
					initialized = true;
				} catch (Exception e) {
					System.err.println("ERROR: Loading log4j config file: " + configFileName + ".");
					System.err.println(e.getMessage());
				}
			}
		} else {
			log.info("Log4j already initialized");
		}

	}

}
