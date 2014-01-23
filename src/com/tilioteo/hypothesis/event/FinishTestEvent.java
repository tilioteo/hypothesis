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
public class FinishTestEvent extends AbstractTestEvent {

	public FinishTestEvent(Test test) {
		super(test);
	}

	public String getName() {
		return ProcessEventTypes.FinishTest;
	}

	public Status getStatus() {
		return Status.FINISHED;
	}

}
