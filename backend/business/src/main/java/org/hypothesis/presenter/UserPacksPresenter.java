/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.hypothesis.business.SessionManager;
import org.hypothesis.cdi.UserPacks;
import org.hypothesis.data.interfaces.UserService;
import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.User;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.event.interfaces.MainUIEvent.UserPacksRequestRefresh;

import com.vaadin.cdi.NormalViewScoped;

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
	
	@Inject
	private Event<MainUIEvent> mainEvent;
	
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
					LinkedList<Pack> list = new LinkedList<>();
					packs.forEach(list::add);

					return list;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

}
