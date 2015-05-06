/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.Component;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class AfterRenderContentEvent extends AbstractContentEvent {

	public AfterRenderContentEvent(Component component) {
		this(component, null);
	}

	public AfterRenderContentEvent(Component component, ErrorHandler errorHandler) {
		super(component, errorHandler);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.AfterRender;
	}

}
