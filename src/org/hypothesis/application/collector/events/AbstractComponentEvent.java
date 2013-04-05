/**
 * 
 */
package org.hypothesis.application.collector.events;

import com.vaadin.ui.AbstractComponent;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public abstract class AbstractComponentEvent<T extends AbstractComponent>
		extends AbstractRunningEvent implements HasComponentData<T> {
	// private ProcessEvent event;

	protected AbstractComponentEvent(
			/* ProcessEvent event, */AbstractComponentData<T> componentData) {
		super(componentData);
		// this.event = event;
	}

	@SuppressWarnings("unchecked")
	public final AbstractComponentData<T> getComponentData() {
		return (AbstractComponentData<T>) getSource();
	}

	// public final ProcessEvent getEvent() {
	// return event;
	// }

}
