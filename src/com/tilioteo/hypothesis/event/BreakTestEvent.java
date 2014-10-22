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
public class BreakTestEvent extends AbstractTestEvent {

	public BreakTestEvent(SimpleTest test) {
		this(test, null);
	}

	public BreakTestEvent(SimpleTest test, ErrorHandler errorHandler) {
		super(test, errorHandler);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.BreakTest;
	}

	@Override
	public Status getStatus() {
		return Status.BROKEN_BY_CLIENT;
	}

}
