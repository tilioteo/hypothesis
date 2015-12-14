/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.event.model;

import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.Component;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
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
