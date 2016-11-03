/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import com.vaadin.cdi.NormalViewScoped;
import org.hypothesis.business.SessionManager;
import org.hypothesis.cdi.UserPacks;
import org.hypothesis.data.interfaces.UserService;
import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.User;

import javax.inject.Inject;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
@UserPacks
@NormalViewScoped
public class UserPacksPresenter extends PublicPacksPresenter {

	@Inject
	protected UserService userService;

	public UserPacksPresenter() {
		System.out.println("Construct " + getClass().getName());
	}

	@Override
	protected List<Pack> getPacks() {
		User user = SessionManager.getLoggedUser();

		if (user != null) {
			try {
				user = userService.merge(user);

				Set<Pack> packs = permissionService.findUserPacks(user, false);
				if (packs != null) {
					return packs.stream().collect(Collectors.toCollection(LinkedList::new));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return Collections.emptyList();
	}

}
