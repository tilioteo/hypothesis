/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import java.util.List;

import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.User;
import org.hypothesis.data.service.UserService;
import org.hypothesis.ui.PackPanel;

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
		User loggedUser = getLoggedUser();
		if (loggedUser != null) {
			try {
				User user = userService.merge(loggedUser);

				return permissionService.getUserPacksVN(user);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		return null;
	}
	
	@Override
	protected void refreshView() {
		getView().clearMainLayout();

		List<Pack> packs = getPacks();
		getPanelBeans().clear();

		if (packs != null && !packs.isEmpty()) {
			boolean notFirst = false;
			for (Pack pack : packs) {
				PackPanel packPanel = createPackPanel(pack);
				getView().addPackPanel(packPanel);
				if (notFirst) {
					packPanel.setEnabled(false);
					packPanel.addStyleName("disabled");
				}
				notFirst = true;
			}
		} else {
			getView().setEmptyInfo();
		}

		getView().markAsDirty();
	}


}
