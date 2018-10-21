/**
 * 
 */
package org.hypothesis.ui.view;

import org.hypothesis.interfaces.ControlPresenter;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class ControlView extends VerticalLayout implements View {

	private final ControlPresenter presenter;

	public ControlView(ControlPresenter presenter) {
		this.presenter = presenter;

		setSizeFull();
		setMargin(true);
		setSpacing(true);

		Component header = presenter.buildHeader();
		if (header != null) {
			addComponent(header);
		}
		Component control = presenter.buildControl();
		if (control != null) {
			addComponent(control);
		}
		setExpandRatio(control, 1);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		presenter.enter(event);
	}

}
