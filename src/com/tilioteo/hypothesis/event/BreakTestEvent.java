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
public class BreakTestEvent extends AbstractTestEvent {

	public BreakTestEvent() {
		this(null);
	}

	public BreakTestEvent(ErrorHandler errorHandler) {
		super(errorHandler);
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
