/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.entity.Status;
import com.vaadin.server.ErrorHandler;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class ErrorTestEvent extends AbstractTestEvent {

	// TODO add reason
	// private String reason;

	public ErrorTestEvent() {
		this(null);
	}

	public ErrorTestEvent(ErrorHandler errorHandler) {
		super(errorHandler);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.TestError;
	}

	@Override
	public Status getStatus() {
		return Status.BROKEN_BY_ERROR;
	}

}
