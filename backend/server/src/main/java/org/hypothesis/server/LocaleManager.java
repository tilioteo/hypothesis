/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.server;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.WrappedHttpSession;
import com.vaadin.server.WrappedSession;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.util.Locale;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public final class LocaleManager {

	public static final String LOCALE_CONFIG_DEFAULT_LANGUAGE = "defaultLanguage";
	public static final String LOCALE_PARAM_LANGUAGE = "lang";

	private static Locale defaultLocale = null;
	private static Locale currentLocale = null;

	private LocaleManager() {
	}

	public static void initializeLocale(VaadinRequest request) {
		WrappedSession session = request.getWrappedSession();
		HttpSession httpSession = ((WrappedHttpSession) session).getHttpSession();
		ServletContext servletContext = httpSession.getServletContext();

		String defaultLanguage = servletContext.getInitParameter(LOCALE_CONFIG_DEFAULT_LANGUAGE);
		defaultLocale = new Locale(defaultLanguage);

		String language = request.getParameter(LOCALE_PARAM_LANGUAGE);

		if (null == language) {
			currentLocale = defaultLocale;
		} else {
			currentLocale = new Locale(language);
		}

		Messages.initMessageSource(currentLocale);
	}

	public static Locale getDefaultLocale() {
		return defaultLocale;
	}

	public static Locale getCurrentLocale() {
		return currentLocale;
	}
}
