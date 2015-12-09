/**
 * 
 */
package org.hypothesis.event.model;

import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.Component;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class RenderContentEvent extends AbstractContentEvent {

	public RenderContentEvent(Component component) {
		this(component, null);
	}

	public RenderContentEvent(Component component, ErrorHandler errorHandler) {
		super(component, errorHandler);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.RenderSlide;
	}
}
