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

	private User loggedUser;

	public UserPacksPresenter() {
		super();

		userService = UserService.newInstance();
	}

	@Override
	protected List<Pack> getPacks() {
		loggedUser = getLoggedUser();
		if (loggedUser != null) {
			try {
				loggedUser = userService.get(loggedUser.getId());

				return permissionService.getUserPacksVN(loggedUser);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	@Override
	public void refreshView() {
		getView().clearMainLayout();

		List<Pack> packs = getPacks();

		if (packs != null && !packs.isEmpty()) {
			boolean notFirst = false;
			for (Pack pack : packs) {
				PackPanel packPanel = createPackPanel(pack);
				getView().addPackPanel(packPanel);
				if (notFirst || loggedUser.isTestingSuspended()) {
					packPanel.setEnabled(false);
					packPanel.addStyleName("disabled");
				}
				notFirst = true;
			}
		} else {
			getView().setEmptyInfo();
		}

		cleanOldTestData(packs);

		getView().markAsDirty();
	}

}
