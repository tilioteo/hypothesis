/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import com.tilioteo.hypothesis.interfaces.SlidePresenter;
import com.vaadin.ui.CssLayout;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class SlideContainer extends CssLayout {

	private SlidePresenter presenter;

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
