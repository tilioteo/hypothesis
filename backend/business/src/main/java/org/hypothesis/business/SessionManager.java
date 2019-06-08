/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import java.io.Serializable;

import org.hypothesis.data.dto.SimpleUserDto;
import org.hypothesis.server.SessionUtils;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class SessionManager implements Serializable {

	private static final String MAIN_UID = "MainUID";

	/*public static void setLoggedUser(User user) {
		if (user != null) {
			SessionUtils.setAttribute(User.class, user);
		} else {
			SessionUtils.clearAttribute(User.class);
		}
	}*/

	public static void setLoggedUser2(SimpleUserDto user) {
		if (user != null) {
			SessionUtils.setAttribute(SimpleUserDto.class, user);
		} else {
			SessionUtils.clearAttribute(SimpleUserDto.class);
		}
	}

	public static SimpleUserDto getLoggedUser2() {
		return SessionUtils.getAttribute(SimpleUserDto.class);
	}

	/*public static User getLoggedUser() {
		return SessionUtils.getAttribute(User.class);
	}*/

	public static String getMainUID() {
		return SessionUtils.getStringAttribute(MAIN_UID);
	}

	public static void setMainUID(String uid) {
		SessionUtils.setAttribute(MAIN_UID, uid);
	}

}
