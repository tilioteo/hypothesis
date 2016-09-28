package org.hypothesis.interfaces;

import org.hypothesis.data.model.User;

public interface UserSettingsWindowPresenter {

	/**
	 * Show window with user settings
	 * 
	 * @param user
	 */
	void showWindow(User user);

}