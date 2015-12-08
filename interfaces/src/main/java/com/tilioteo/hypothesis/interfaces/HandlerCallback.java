/**
 * 
 */
package com.tilioteo.hypothesis.interfaces;

import java.io.Serializable;

import com.vaadin.ui.Component;

/**
 * @author kamil
 *
 */
public interface HandlerCallback extends Serializable {

	public void setComponentHandler(Component component, Element element, String name, String actionId, Action action,
			SlidePresenter presenter);

}
