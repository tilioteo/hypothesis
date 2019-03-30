package org.hypothesis.utility;

import org.hypothesis.business.data.UserControlData;
import org.hypothesis.business.data.UserSession;

public class UserControlDataUtility {

	public static UserSession getUserSession(UserControlData data, String uid) {
		return data.getSessions().stream()//
				.filter(s -> uid.equals(s.getUid()))//
				.findFirst()//
				.orElse(null);
	}

}
