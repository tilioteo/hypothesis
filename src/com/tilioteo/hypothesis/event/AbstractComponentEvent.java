/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.AbstractComponent;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public abstract class AbstractComponentEvent<T extends AbstractComponent>
		extends AbstractUserEvent implements HasComponentData<T> {
	
	private AbstractComponentData<T> componentData;

	protected AbstractComponentEvent(AbstractComponentData<T> componentData, ErrorHandler errorHandler) {
		super(errorHandler);
		this.componentData = componentData;
	}

	@Override
	public final AbstractComponentData<T> getComponentData() {
		return componentData;
	}

}
