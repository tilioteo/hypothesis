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
public class FinishTestEvent extends AbstractTestEvent {

	public FinishTestEvent() {
		this(null);
	}

	public FinishTestEvent(ErrorHandler errorHandler) {
		super(errorHandler);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.FinishTest;
	}

	@Override
	public Status getStatus() {
		return Status.FINISHED;
	}

}
