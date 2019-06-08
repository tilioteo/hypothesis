package org.hypothesis.presenter;

import org.hypothesis.business.SessionManager;
import org.hypothesis.data.dto.SimpleUserDto;
import org.hypothesis.interfaces.ViewPresenter;

@SuppressWarnings("serial")
public abstract class AbstractViewPresenter implements ViewPresenter {

	private SimpleUserDto loggedUser;

	public SimpleUserDto getLoggedUser() {
		return loggedUser;
	}

	@Override
	public void init() {
		this.loggedUser = SessionManager.getLoggedUser2();
	}

}
