/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.server;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.WrappedHttpSession;
import com.vaadin.server.WrappedSession;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public class LocaleManager {

	public static void initializeLocale(VaadinRequest request, Locale locale) {
		WrappedSession session = request.getWrappedSession();
		HttpSession httpSession = ((WrappedHttpSession) session).getHttpSession();
		ServletContext servletContext = httpSession.getServletContext();

		Messages.initMessageSource(WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext), locale);
	}
}
