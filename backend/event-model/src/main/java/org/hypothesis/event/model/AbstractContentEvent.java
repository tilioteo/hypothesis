/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.event.model;

import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.Component;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public abstract class AbstractContentEvent extends AbstractRunningEvent {

	private Component component;

	protected AbstractContentEvent(Component component, ErrorHandler errorHandler) {
		super(errorHandler);
		this.component = component;
	}

	public Component getComponent() {
		return component;
	}
}
