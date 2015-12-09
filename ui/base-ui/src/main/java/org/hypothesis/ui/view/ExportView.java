/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.ui.view;

import org.hypothesis.interfaces.ExportPresenter;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 * Hypothesis
 *
 */
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
