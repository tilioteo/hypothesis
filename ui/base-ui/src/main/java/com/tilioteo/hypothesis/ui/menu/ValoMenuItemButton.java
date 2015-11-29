/**
 * 
 */
package com.tilioteo.hypothesis.ui.menu;

import com.tilioteo.hypothesis.event.interfaces.MainUIEvent.PostViewChangeEvent;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;

import net.engio.mbassy.listener.Handler;

/**
 * @author kamil
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

		// MainEventBus.get().register(this);
		addClickListener(new ClickListener() {
			@Override
			public void buttonClick(final ClickEvent event) {
				UI.getCurrent().getNavigator().navigateTo(viewName);
			}
		});

	}

	@Handler
	public void postViewChange(final PostViewChangeEvent event) {
		removeStyleName(STYLE_SELECTED);
		if (viewName.equals(event.getViewName())) {
			addStyleName(STYLE_SELECTED);
		}
	}
}
