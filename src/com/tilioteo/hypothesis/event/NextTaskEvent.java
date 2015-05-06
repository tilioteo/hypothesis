/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.vaadin.server.ErrorHandler;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class NextTaskEvent extends AbstractRunningEvent {

	public NextTaskEvent() {
		this(null);
	}

	public NextTaskEvent(ErrorHandler errorHandler) {
		super(errorHandler);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.NextTask;
	}

}
