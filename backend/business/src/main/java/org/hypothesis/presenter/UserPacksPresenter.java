/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import java.util.List;

import org.hypothesis.data.dto.PackDto;
import org.hypothesis.data.dto.SimpleUserDto;
import org.hypothesis.data.service.UserService;
import org.hypothesis.data.service.impl.UserServiceImpl;
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

	private SimpleUserDto loggedUser;

	public UserPacksPresenter() {
		super();

		userService = new UserServiceImpl();
	}

	@Override
	protected List<PackDto> getPacks() {
		loggedUser = getLoggedUser();
		if (loggedUser != null) {
			try {
				loggedUser = userService.getSimpleById(loggedUser.getId());

				return permissionService.getUserPacksVN(loggedUser.getId());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	@Override
	public void refreshView() {
		getView().clearMainLayout();

		List<PackDto> packs = getPacks();

		if (packs != null && !packs.isEmpty()) {
			boolean notFirst = false;
			for (PackDto pack : packs) {
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
