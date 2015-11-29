/**
 * 
 */
package com.tilioteo.hypothesis.business;

import java.io.Serializable;

import com.tilioteo.hypothesis.data.model.User;
import com.tilioteo.hypothesis.server.SessionUtils;

/**
 * @author kamil
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
