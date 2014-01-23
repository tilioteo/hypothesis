/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.entity.Test;
import com.tilioteo.hypothesis.entity.Test.Status;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class ErrorTestEvent extends AbstractTestEvent {

	// private String reason;

	public ErrorTestEvent() {
		this(null);
	}

	public ErrorTestEvent(Test test) {
		super(test);
		
		// TODO add reason
	}

	public String getName() {
		return ProcessEventTypes.TestError;
	}

	public Status getStatus() {
		return Status.BROKEN_BY_ERROR;
	}

}
