/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.interfaces;

import com.vaadin.ui.Component;

import java.io.Serializable;

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
