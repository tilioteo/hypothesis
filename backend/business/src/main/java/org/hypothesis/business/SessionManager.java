/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import java.io.Serializable;

import org.hypothesis.data.model.User;
import org.hypothesis.server.SessionUtils;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class SessionManager implements Serializable {

	public static void setLoggedUser(User user) {
		if (user != null) {
			SessionUtils.setAttribute(User.class, user);
		} else {
			SessionUtils.clearAttribute(User.class);
		}
	}

	public static User getLoggedUser() {
		return SessionUtils.getAttribute(User.class);
	}

	public static String getMainUID() {
		return SessionUtils.getStringAttribute("MainUID");
	}

	public static void setMainUID(String uid) {
		SessionUtils.setAttribute("MainUID", uid);
	}

}
