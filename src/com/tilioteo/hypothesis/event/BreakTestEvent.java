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
public class BreakTestEvent extends AbstractTestEvent {

	public BreakTestEvent(Test test) {
		super(test);
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
