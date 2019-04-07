package org.hypothesis.business.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.User;

public class UserControlData {

	private User user;
	private List<Pack> packs = new ArrayList<>();
	private final List<UserSession> sessions = new ArrayList<>();

	public UserControlData(User user) {
		setUser(user);
	}

	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		Objects.requireNonNull(user, "User cannot be null.");

		this.user = user;
	}

	public List<Pack> getPacks() {
		return packs;
	}

	public void setPacks(List<Pack> packs) {
		this.packs = packs;
	}

	public List<UserSession> getSessions() {
		return sessions;
	}
}
