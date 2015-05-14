/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.vaadin.server.ErrorHandler;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class FinishTaskEvent extends AbstractRunningEvent {

	public FinishTaskEvent() {
		this(null);
	}

	public FinishTaskEvent(ErrorHandler errorHandler) {
		super(errorHandler);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.FinishTask;
	}

}
