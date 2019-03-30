/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.ui.view;

import org.hypothesis.interfaces.ManagementPresenter;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings({ "serial" })
public class ManagementView extends VerticalLayout implements UIView {

	private final ManagementPresenter presenter;

	public ManagementView(ManagementPresenter presenter) {
		this.presenter = presenter;

		setSizeFull();
		setMargin(true);
		setSpacing(true);

		addComponent(presenter.buildHeader());
		Component table = presenter.buildTable();
		addComponent(table);
		setExpandRatio(table, 1);
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
