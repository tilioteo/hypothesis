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
public class StartTestEvent extends AbstractTestEvent {

	public StartTestEvent(Test test) {
		super(test);
	}

	public String getName() {
		return ProcessEventTypes.StartTest;
	}

	public Status getStatus() {
		return Status.STARTED;
	}
}
