package com.tilioteo.hypothesis.ui.view;

import com.tilioteo.hypothesis.interfaces.ExportPresenter;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings({ "serial" })
public class ExportView extends VerticalLayout implements View {

	private ExportPresenter presenter;

	public ExportView(ExportPresenter presenter) {
		this.presenter = presenter;

		setSizeFull();
		setMargin(true);
		setSpacing(true);

		addComponent(presenter.buildHeader());
		Component content = presenter.buildContent();
		addComponent(content);
		setExpandRatio(content, 1);
	}
	
	@Override
	public void attach() {
		super.attach();
		
		presenter.attach();
	}
	
	@Override
	public void detach() {
		presenter.detach();
		
		super.detach();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		presenter.enter(event);
	}

}
