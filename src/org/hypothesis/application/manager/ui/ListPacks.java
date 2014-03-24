package org.hypothesis.application.manager.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.hypothesis.application.ManagerApplication;
import org.hypothesis.common.i18n.ApplicationMessages;
import org.hypothesis.common.i18n.Messages;
import org.hypothesis.entity.Pack;
import org.hypothesis.entity.User;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

/**
 * The class represents available packs list
 * 
 * @author David Kabáth - Hypothesis
 * @author Petr Jestřábek - Hypothesis
 * @author Kamil Morong - Hypothesis
 */
public class ListPacks extends VerticalLayout {
	private static final long serialVersionUID = -1972069125917529719L;

	/**
	 * Constructor
	 * 
	 * @param manager
	 *            - manager application
	 */
	public ListPacks() {
		// set layout
		this.setSpacing(true);
		this.setMargin(true);

		// heading
		Label heading = new Label("<h2>"
				+ ApplicationMessages.get().getString(Messages.TEXT_ENABLED_PACKS)
				+ "</h2>");
		heading.setContentMode(Label.CONTENT_XHTML);
		addComponent(heading);

		// publish the packs
		User user = ManagerApplication.getInstance().getUserGroupManager()
				.findUser(ManagerApplication.getInstance().getCurrentUser().getId());
		/*
		 * for (Pack pack :
		 * ManagerApplication.getInstance().getPermitionManager().findUserPacks(user,
		 * false)) { Button button = new Button(pack.getName() + " - " +
		 * pack.getDescription()); button.setStyleName(BaseTheme.BUTTON_LINK);
		 * addComponent(button); }
		 */

		Set<Pack> packs = ManagerApplication.getInstance().getPermissionManager()
				.findUserPacks2(user, false);
		List<String> sortedPacks = new ArrayList<String>();
		for (Pack pack : packs) {
			sortedPacks.add(String.format("%s (%d) - %s", pack.getName(),
					pack.getId(), pack.getDescription()));
		}
		Collections.sort(sortedPacks);
		for (String packTitle : sortedPacks) {
			Button button = new Button(packTitle);
			button.setStyleName(BaseTheme.BUTTON_LINK);
			addComponent(button);
		}
	}

}
