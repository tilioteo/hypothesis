/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.vaadin.ui.AbstractComponent;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public interface HasComponentData<T extends AbstractComponent> {

	public AbstractComponentData<T> getComponentData();

}
