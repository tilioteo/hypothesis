/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.interfaces;

import com.vaadin.ui.Component;
import org.hypothesis.ui.LoginScreen;

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
