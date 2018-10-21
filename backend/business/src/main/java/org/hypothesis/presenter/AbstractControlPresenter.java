package org.hypothesis.presenter;

import org.hypothesis.eventbus.HasMainEventBus;
import org.hypothesis.interfaces.ControlPresenter;
import org.hypothesis.ui.view.ControlView;

import com.vaadin.navigator.View;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractControlPresenter extends AbstractViewPresenter
		implements ControlPresenter, HasMainEventBus {

	@Override
	public View createView() {
		return new ControlView(this);
	}

}
