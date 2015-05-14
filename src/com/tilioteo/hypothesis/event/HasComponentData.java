/**
 * 
 */
package com.tilioteo.hypothesis.event;

import java.io.Serializable;

import com.vaadin.ui.AbstractComponent;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public interface HasComponentData<T extends AbstractComponent> extends Serializable {

	public AbstractComponentData<T> getComponentData();

}
