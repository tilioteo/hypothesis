/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.WrappedHttpSession;
import com.vaadin.server.WrappedSession;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class UI extends com.vaadin.ui.UI {

	private ApplicationContext applicationContext = null;
	private String language = null;
	
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public String getLanguage() {
		return language;
	}
	
	@Override
	protected void init(VaadinRequest request) {
		language = request.getParameter("lang");
		if (null == language) {
			language = "cs"; // default
		}
		
		setLocale(new Locale(language));
		
		WrappedSession session = request.getWrappedSession();
		HttpSession httpSession = ((WrappedHttpSession)session).getHttpSession();
		ServletContext servletContext = httpSession.getServletContext();
		applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
	}
	
	public static UI getCurrent() {
		com.vaadin.ui.UI current = com.vaadin.ui.UI.getCurrent();
		if (current instanceof UI) {
			return (UI)current;
		}
		return null;
	}
	
	public static String getCurrentLanguage() {
		UI ui = getCurrent();
		if (ui != null) {
			return ui.getLanguage();
		}
		return null;
	}
}
