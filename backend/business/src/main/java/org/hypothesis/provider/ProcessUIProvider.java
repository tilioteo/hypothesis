/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.provider;

import org.hypothesis.presenter.ProcessUIPresenter;
import org.hypothesis.ui.ProcessUI;

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
public class ProcessUIProvider extends UIProvider {

	@Override
	public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
		return ProcessUI.class;
	}

	@Override
	public UI createInstance(UICreateEvent event) {
		ProcessUI ui = (ProcessUI) super.createInstance(event);
		ui.setPresenter(new ProcessUIPresenter(ui));

		return ui;
	}
}
