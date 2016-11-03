package org.hypothesis.interfaces;

import org.hypothesis.data.model.User;

import java.util.Collection;

public interface UserWindowPresenter extends WindowPresenter {

	/**
	 * Show window for edit user
	 * 
	 * @param user
	 */
	void showWindow(User user);

	/**
	 * Show window for edit more users
	 * 
	 * @param users
	 */
	void showWindow(Collection<User> users);

}