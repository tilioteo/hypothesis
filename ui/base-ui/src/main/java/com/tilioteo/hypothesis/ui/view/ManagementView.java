package com.tilioteo.hypothesis.ui.view;

import com.tilioteo.hypothesis.interfaces.ManagementPresenter;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings({ "serial" })
public class ManagementView extends VerticalLayout implements View {

	private ManagementPresenter presenter;
	
	public ManagementView(ManagementPresenter presenter) {
		this.presenter = presenter;
		
		setSizeFull();
		setMargin(true);
		setSpacing(true);

		addComponent(presenter.buildHeader());
		Table table = presenter.buildTable();
		addComponent(table);
		setExpandRatio(table, 1);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		presenter.enter(event);
	}

}
