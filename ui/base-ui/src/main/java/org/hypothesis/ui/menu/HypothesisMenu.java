/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.ui.menu;

import org.hypothesis.interfaces.MenuPresenter;

import com.vaadin.ui.CustomComponent;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings({ "serial" })
public final class HypothesisMenu extends CustomComponent {

	private static final String ID = "hypothesis-menu";

	private final MenuPresenter presenter;

	public HypothesisMenu(MenuPresenter presenter) {
		this.presenter = presenter;

		addStyleName("valo-menu-color2");
		setId(ID);
		setSizeUndefined();

		setCompositionRoot(presenter.buildContent());
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

}
