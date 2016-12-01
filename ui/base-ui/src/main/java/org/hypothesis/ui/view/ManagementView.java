/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.ui.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import org.hypothesis.interfaces.ManagementPresenter;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings({ "serial" })
public abstract class ManagementView extends VerticalLayout implements View {

	private ManagementPresenter presenter;

	public ManagementView() {
		setSizeFull();
		setMargin(true);
		setSpacing(true);
	}

	private void buildContent() {
		removeAllComponents();

		addComponent(presenter.buildHeader());
		Table table = presenter.buildTable();
		addComponent(table);
		setExpandRatio(table, 1);
	}

	protected void setPresenter(ManagementPresenter presenter) {
		this.presenter = presenter;

		buildContent();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		presenter.enter(event);
	}

}
