/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.entity.SimpleTest;
import com.tilioteo.hypothesis.entity.Status;
import com.vaadin.server.ErrorHandler;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class FinishTestEvent extends AbstractTestEvent {

	public FinishTestEvent(SimpleTest test) {
		this(test, null);
	}

	public FinishTestEvent(SimpleTest test, ErrorHandler errorHandler) {
		super(test, errorHandler);
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
