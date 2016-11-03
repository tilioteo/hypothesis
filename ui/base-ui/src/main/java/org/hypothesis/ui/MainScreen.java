/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.ui;

import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.VerticalLayout;
import org.hypothesis.interfaces.MainPresenter;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class MainScreen extends VerticalLayout {

	private final MainPresenter presenter;

	public MainScreen(MainPresenter presenter) {
		this.presenter = presenter;

		setSizeFull();
		// addStyleName("mainview");

		addComponent(presenter.buildTopPanel());

		Component mainPane = presenter.buildMainPane();
		mainPane.setSizeFull();
		addComponent(mainPane);
		setExpandRatio(mainPane, 1.0f);

		addComponent(presenter.buildBottomPanel());
	}

	public ComponentContainer getContent() {
		return presenter.getContent();
	}

}
