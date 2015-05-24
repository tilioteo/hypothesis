/**
 * 
 */
package com.tilioteo.hypothesis.ui.view;

import java.util.List;

import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.entity.User;
import com.tilioteo.hypothesis.server.SessionUtils;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class PacksView extends PublicPacksView {

	@Override
	protected List<Pack> getPacks() {
		User user = SessionUtils.getAttribute(User.class);

		return packsModel.getUserPacks(user);
	}

}
