/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.ui;

import com.vaadin.ui.CssLayout;
import org.hypothesis.interfaces.SlidePresenter;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class SlideContainer extends CssLayout {

	private final SlidePresenter presenter;

	public SlideContainer(SlidePresenter presenter) {
		super();
		this.presenter = presenter;

		setSizeFull();
	}

	@Override
	public void attach() {
		super.attach();

		presenter.attach(this, getParent(), getUI(), getSession());
	}

	@Override
	public void detach() {
		presenter.detach(this, getParent(), getUI(), getSession());

		super.detach();
	}

	public SlidePresenter getPresenter() {
		return presenter;
	}

}
