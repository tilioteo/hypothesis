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
public class FinishTestEvent extends AbstractTestEvent {

	public FinishTestEvent(SimpleTest test) {
		super(test);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.FinishTest;
	}

	@Override
	public Status getStatus() {
		return Status.FINISHED;
	}

}
