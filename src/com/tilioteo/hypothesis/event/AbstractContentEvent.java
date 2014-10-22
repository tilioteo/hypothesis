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
public abstract class AbstractContentEvent extends AbstractProcessEvent {

	protected AbstractContentEvent(LayoutComponent content, ErrorHandler errorHandler) {
		super(content, errorHandler);
	}

	public LayoutComponent getContent() {
		return (LayoutComponent) getSource();
	}
}
