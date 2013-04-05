/**
 * 
 */
package org.hypothesis.application.collector.events;

import org.hypothesis.entity.Test;
import org.hypothesis.entity.Test.Status;

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
		return ProcessEvents.TestError;
	}

	public Status getStatus() {
		return Status.BROKEN_BY_ERROR;
	}

}
