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
public class ContinueTestEvent extends AbstractTestEvent {

	private SimpleTest test;
	
	public ContinueTestEvent(SimpleTest test) {
		this(test, null);
	}

	public ContinueTestEvent(SimpleTest test, ErrorHandler errorHandler) {
		super(errorHandler);
		this.test = test;
	}

	@Override
	public String getName() {
		return ProcessEventTypes.ContinueTest;
	}

	@Override
	public Status getStatus() {
		return Status.STARTED;
	}

	public SimpleTest getTest() {
		return test;
	}
}
