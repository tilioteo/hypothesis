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
public class CloseTestEvent extends AbstractTestEvent {

	public CloseTestEvent(SimpleTest test) {
		super(test);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.Null;
	}

	@Override
	public Status getStatus() {
		return null;
	}

}
