/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.interfaces;

import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import org.hypothesis.ui.MainScreen;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface MainPresenter extends ComponentPresenter {

	Component buildTopPanel();

	Component buildMainPane();

	Component buildBottomPanel();

	Component buildMenu();

	ComponentContainer getContent();

	MainScreen createScreen();

}
