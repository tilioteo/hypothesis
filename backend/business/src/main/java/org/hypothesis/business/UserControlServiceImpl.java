package org.hypothesis.business;

import java.util.Objects;

import org.hypothesis.business.data.UserControlData;
import org.hypothesis.business.data.UserSession;
import org.hypothesis.data.model.User;
import org.hypothesis.data.service.PermissionService;
import org.hypothesis.server.SessionUtils;
import org.hypothesis.servlet.SessionRegister;

public class UserControlServiceImpl {

	private final PermissionService permissionService;

	public UserControlServiceImpl() {
		permissionService = PermissionService.newInstance();
	}

	public UserControlData ensureUserControlData(User user) {
		Objects.requireNonNull(user, "User cannot be null.");

		return SessionRegister.getActiveSessions().stream()//
				.map(s -> SessionUtils.getAttribute(s, UserControlData.class))//
				.filter(Objects::nonNull)//
				.filter(ucd -> user.getId().equals(ucd.getUser().getId()))//
				.map(ucd -> updateUser(ucd, user))
				.findFirst().orElseGet(() -> createAndRegisterUserControlData(user));
	}
	
	private static UserControlData updateUser(UserControlData userControlData, User user) {
		userControlData.setUser(user);
		return userControlData;
	}


	private static UserControlData createAndRegisterUserControlData(User user) {
		UserControlData data = new UserControlData(user);
		SessionUtils.setAttribute(UserControlData.class, data);

		return data;
	}

	public UserControlData updateUserControlDataWithSession(UserControlData data, String uid) {
		if (data != null) {
			updateUserControlData(data);

			if (uid != null) {
				ensureUserSession(data, uid);
			}
		}

		return data;
	}

	public UserControlData updateUserControlData(UserControlData data) {
		if (data != null) {
			User user = data.getUser();

			data.setPacks(permissionService.getUserPacksVN(user));
		}

		return data;
	}

	private static UserSession ensureUserSession(UserControlData data, String uid) {
		Objects.requireNonNull(data, "UserControlData cannot be null.");
		Objects.requireNonNull(uid, "Uid cannot be null.");

		return data.getSessions().stream()//
				.filter(s -> uid.equals(s.getUid())).findFirst()//
				.orElseGet(() -> createAndRegisterUserSession(data, uid));
	}

	private static UserSession createAndRegisterUserSession(UserControlData data, String uid) {
		UserSession session = new UserSession(uid);
		data.getSessions().add(session);
		return session;
	}
}
