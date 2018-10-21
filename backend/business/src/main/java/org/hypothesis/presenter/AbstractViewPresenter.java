package org.hypothesis.presenter;

import org.hypothesis.business.SessionManager;
import org.hypothesis.data.model.User;
import org.hypothesis.interfaces.ViewPresenter;

@SuppressWarnings("serial")
public abstract class AbstractViewPresenter implements ViewPresenter {

	private User loggedUser;

	public User getLoggedUser() {
		return loggedUser;
	}

	@Override
	public void init() {
		this.loggedUser = SessionManager.getLoggedUser();
	}

}
