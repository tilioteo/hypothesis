/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import com.tilioteo.hypothesis.ui.LoginScreen;
import com.vaadin.ui.Component;

/**
 * @author kamil
 *
 */
public interface LoginPresenter extends ComponentPresenter {

	public Component buildLoginForm();

	public void refreshLoginForm();

	public LoginScreen createScreen();

}
