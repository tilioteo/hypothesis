/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.interfaces;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public interface ViewPresenter extends ComponentPresenter {

	void enter(ViewChangeEvent event);

}
