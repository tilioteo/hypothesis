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
public class ContinueTestEvent extends AbstractTestEvent {

	public ContinueTestEvent(SimpleTest test) {
		super(test);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.ContinueTest;
	}

	@Override
	public Status getStatus() {
		return Status.STARTED;
	}

}
