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
public class ContinueTestEvent extends AbstractTestEvent {

	public ContinueTestEvent(Test test) {
		super(test);
	}

	public String getName() {
		return ProcessEventTypes.ContinueTest;
	}

	public Status getStatus() {
		return Status.STARTED;
	}

}
