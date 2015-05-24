/**
 * 
 */
package com.tilioteo.hypothesis.server;

import com.vaadin.server.VaadinSession;

/**
 * @author kamil
 *
 */
public class SessionUtils {
	
	public static <T> void setAttribute(Class<T> type, T value) {
		VaadinSession session = VaadinSession.getCurrent();
		if (session != null) {
			session.setAttribute(type, value);
		}
	}
	
	public static <T> T getAttribute(Class<T> type) {
		VaadinSession session = VaadinSession.getCurrent();
		if (session != null) {
			return session.getAttribute(type);
		}
		return null;
	}

	public static <T> void clearAttribute(Class<T> type) {
		VaadinSession session = VaadinSession.getCurrent();
		if (session != null) {
			session.setAttribute(type, null);
		}
	}
	
}
