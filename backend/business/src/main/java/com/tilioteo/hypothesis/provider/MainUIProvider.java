/**
 * 
 */
package com.tilioteo.hypothesis.provider;

import com.tilioteo.hypothesis.presenter.MainUIPresenter;
import com.tilioteo.hypothesis.ui.MainUI;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class MainUIProvider extends UIProvider {

	@Override
	public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
		return MainUI.class;
	}

	@Override
	public UI createInstance(UICreateEvent event) {
		MainUI ui = (MainUI) super.createInstance(event);
		ui.setPresenter(new MainUIPresenter(ui));

		return ui;
	}
}
