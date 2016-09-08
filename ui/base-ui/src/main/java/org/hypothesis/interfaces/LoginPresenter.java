/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.interfaces;

import org.hypothesis.ui.LoginScreen;

import com.vaadin.ui.Component;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface LoginPresenter extends ComponentPresenter {

	Component buildLoginForm();

	void refreshLoginForm();

	LoginScreen createScreen();

}
