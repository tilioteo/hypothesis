/**
 * 
 */
package com.tilioteo.hypothesis.presenter;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.tilioteo.hypothesis.business.SessionManager;
import com.tilioteo.hypothesis.data.model.Pack;
import com.tilioteo.hypothesis.data.model.User;
import com.tilioteo.hypothesis.data.service.UserService;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class UserPacksPresenter extends PublicPacksPresenter {

	private UserService userService;

	public UserPacksPresenter() {
		super();

		userService = UserService.newInstance();
	}

	@Override
	protected List<Pack> getPacks() {
		if (getUser() != null) {
			try {
				User user = userService.merge(getUser());

				Set<Pack> packs = permissionService.findUserPacks(user, false);
				if (packs != null) {
					LinkedList<Pack> list = new LinkedList<>();
					for (Pack pack : packs) {
						list.add(pack);
					}

					return list;
				}
			} catch (Throwable e) {
			}
		}

		return null;
	}
	
	@Override
	public void attach() {
		super.attach();
		
		setUser(SessionManager.getLoggedUser());
	}

	@Override
	public void setUser(User user) {
		if (user != getUser()) {
			super.setUser(user);

			refreshView();
		}
	}
}
