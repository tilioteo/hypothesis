/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.entity.SimpleTest;
import com.tilioteo.hypothesis.entity.Status;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class ErrorTestEvent extends AbstractTestEvent {

	// private String reason;

	public ErrorTestEvent(SimpleTest test) {
		super(test);
		
		// TODO add reason
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
