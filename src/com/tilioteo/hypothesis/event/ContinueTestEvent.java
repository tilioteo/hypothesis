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
public class ContinueTestEvent extends AbstractTestEvent {

	public ContinueTestEvent(SimpleTest test) {
		this(test, null);
	}

	public ContinueTestEvent(SimpleTest test, ErrorHandler errorHandler) {
		super(test, errorHandler);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.ContinueTest;
	}

	@Override
	public Status getStatus() {
		return Status.STARTED;
	}

}
