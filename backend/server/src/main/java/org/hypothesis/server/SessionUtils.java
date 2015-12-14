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

	public static void setAttribute(String name, Object value) {
		VaadinSession session = VaadinSession.getCurrent();
		if (session != null) {
			session.setAttribute(name, value);
		}
	}

	public static Object getAttribute(String name) {
		VaadinSession session = VaadinSession.getCurrent();
		if (session != null) {
			session.getAttribute(name);
		}
		return null;
	}

	public static String getStringAttribute(String name) {
		Object object = getAttribute(name);
		if (object instanceof String) {
			return (String) object;
		}
		return null;
	}
}
