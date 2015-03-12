/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.ui.LayoutComponent;
import com.vaadin.server.ErrorHandler;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class AfterRenderContentEvent extends AbstractContentEvent {

	public AfterRenderContentEvent(LayoutComponent content) {
		this(content, null);
	}

	public AfterRenderContentEvent(LayoutComponent content, ErrorHandler errorHandler) {
		super(content, errorHandler);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.AfterRender;
	}

}
