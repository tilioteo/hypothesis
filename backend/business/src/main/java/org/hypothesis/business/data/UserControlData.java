package org.hypothesis.business.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hypothesis.data.dto.PackDto;
import org.hypothesis.data.dto.SimpleUserDto;

public class UserControlData {

	private SimpleUserDto user;
	private List<PackDto> packs = new ArrayList<>();
	private final List<UserSession> sessions = new ArrayList<>();

	public UserControlData(SimpleUserDto user) {
		setUser(user);
	}

	public SimpleUserDto getUser() {
		return user;
	}

	public void setUser(SimpleUserDto user) {
		Objects.requireNonNull(user, "User cannot be null.");

		this.user = user;
	}

	public List<PackDto> getPacks() {
		return packs;
	}

	public void setPacks(List<PackDto> packs) {
		this.packs = packs;
	}

	public List<UserSession> getSessions() {
		return sessions;
	}
}
