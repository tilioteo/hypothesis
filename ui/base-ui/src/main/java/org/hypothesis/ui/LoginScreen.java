/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.ui;

import org.hypothesis.interfaces.LoginPresenter;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class LoginScreen extends VerticalLayout {

	private final LoginPresenter presenter;

	public LoginScreen(LoginPresenter presenter) {
		this.presenter = presenter;

		setSizeFull();

		Component loginForm = presenter.buildLoginForm();
		addComponent(loginForm);
		setComponentAlignment(loginForm, Alignment.MIDDLE_CENTER);
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

	public void refresh() {
		presenter.refreshLoginForm();
	}

}
