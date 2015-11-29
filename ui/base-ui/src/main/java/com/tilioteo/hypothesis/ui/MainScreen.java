package com.tilioteo.hypothesis.ui;

import com.tilioteo.hypothesis.interfaces.MainPresenter;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class MainScreen extends VerticalLayout {

	private MainPresenter presenter;

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
