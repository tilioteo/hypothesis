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
public class StartTestEvent extends AbstractTestEvent {

	private SimpleTest test;
	
	public StartTestEvent(SimpleTest test) {
		this(test, null);
	}

	public StartTestEvent(SimpleTest test, ErrorHandler errorHandler) {
		super(errorHandler);
		this.test = test;
	}

	@Override
	public String getName() {
		return ProcessEventTypes.StartTest;
	}

	@Override
	public Status getStatus() {
		return Status.STARTED;
	}
	
	public SimpleTest getTest() {
		return test;
	}
}
