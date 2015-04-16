/**
 * 
 */
package com.tilioteo.hypothesis.ui.view;

import java.util.List;

import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.entity.User;
import com.vaadin.server.VaadinSession;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class PacksView extends PublicPacksView {

	@Override
	protected List<Pack> getPacks() {
		User user = (User) VaadinSession.getCurrent().getAttribute(User.class.getName());

		return packsModel.getUserPacks(user);
	}

}
