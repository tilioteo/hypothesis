/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.ui.menu;

import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class ValoMenuItemButton extends Button {

	private static final String STYLE_SELECTED = "selected";

	private final String viewName;

	public ValoMenuItemButton(final String caption, final String viewName, final Resource icon) {
		this.viewName = viewName;

		setPrimaryStyleName("valo-menu-item");
		setIcon(icon);
		setCaption(caption);

		addClickListener(new ClickListener() {
			@Override
			public void buttonClick(final ClickEvent event) {
				UI.getCurrent().getNavigator().navigateTo(viewName);
			}
		});

	}

	public void afterViewChange(String viewName) {
		removeStyleName(STYLE_SELECTED);
		if (this.viewName.equals(viewName)) {
			addStyleName(STYLE_SELECTED);
		}
	}
}
