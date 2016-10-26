/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.interfaces;

import java.io.Serializable;

import com.vaadin.ui.Component;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@FunctionalInterface
public interface HandlerCallback extends Serializable {

	void setComponentHandler(Component component, Element element, Element handlerElement, String name, String actionId, Action action, SlidePresenter presenter);

}
