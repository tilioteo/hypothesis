/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.vaadin.ui.AbstractComponent;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public abstract class AbstractComponentEvent<T extends AbstractComponent>
		extends AbstractRunningEvent implements HasComponentData<T> {

	protected AbstractComponentEvent(AbstractComponentData<T> componentData) {
		super(componentData);
	}

	@SuppressWarnings("unchecked")
	public final AbstractComponentData<T> getComponentData() {
		return (AbstractComponentData<T>) getSource();
	}

}
