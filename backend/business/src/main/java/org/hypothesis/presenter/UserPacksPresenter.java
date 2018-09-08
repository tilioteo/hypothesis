/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import java.util.List;

import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.User;
import org.hypothesis.data.service.UserService;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class UserPacksPresenter extends PublicPacksPresenter {

	protected final UserService userService;

	public UserPacksPresenter() {
		super();

		userService = UserService.newInstance();
	}

	@Override
	protected List<Pack> getPacks() {
		if (getUser() != null) {
			try {
				User user = userService.merge(getUser());

				return permissionService.getUserPacksVN(user);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	@Override
	public void setUser(User user) {
		super.setUser(user);
		if (user != getUser()) {
			refreshView();
		}
	}
}
