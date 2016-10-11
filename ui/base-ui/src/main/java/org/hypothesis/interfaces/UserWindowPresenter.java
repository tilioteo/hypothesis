package org.hypothesis.interfaces;

import java.util.Collection;

import org.hypothesis.data.model.User;

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