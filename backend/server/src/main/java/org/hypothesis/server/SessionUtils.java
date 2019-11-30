/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.server;

import com.vaadin.server.VaadinSession;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public class SessionUtils {

	public static synchronized <T> void setAttribute(Class<T> type, T value) {
		setAttribute(VaadinSession.getCurrent(), type, value);
	}

	public static synchronized <T> void setAttribute(VaadinSession session, Class<T> type, T value) {
		if (session != null) {
			session.setAttribute(type, value);
		}
	}

	public static <T> T getAttribute(Class<T> type) {
		return getAttribute(VaadinSession.getCurrent(), type);
	}

	public static <T> T getAttribute(VaadinSession session, Class<T> type) {
		if (session != null) {
			return session.getAttribute(type);
		}
		return null;
	}

	public static <T> void clearAttribute(Class<T> type) {
		clearAttribute(VaadinSession.getCurrent(), type);
	}

	public static synchronized <T> void clearAttribute(VaadinSession session, Class<T> type) {
		if (session != null) {
			session.setAttribute(type, null);
		}
	}

	public static synchronized void setAttribute(String name, Object value) {
		setAttribute(VaadinSession.getCurrent(), name, value);
	}

	public static synchronized void setAttribute(VaadinSession session, String name, Object value) {
		if (session != null) {
			session.setAttribute(name, value);
		}
	}

	public static Object getAttribute(String name) {
		return getAttribute(VaadinSession.getCurrent(), name);
	}

	public static Object getAttribute(VaadinSession session, String name) {
		if (session != null) {
			return session.getAttribute(name);
		}
		return null;
	}

	public static String getStringAttribute(String name) {
		return getStringAttribute(VaadinSession.getCurrent(), name);
	}

	public static String getStringAttribute(VaadinSession session, String name) {
		Object object = getAttribute(session, name);
		if (object instanceof String) {
			return (String) object;
		}
		return null;
	}
}
