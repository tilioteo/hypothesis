/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.provider;

import org.hypothesis.presenter.MainUIPresenter;
import org.hypothesis.ui.MainUI;

import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
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
		//ui.setPresenter(new MainUIPresenter());
		
		return ui;
	}

}
