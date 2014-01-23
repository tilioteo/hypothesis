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
public class CloseTestEvent extends AbstractTestEvent {

	public CloseTestEvent(Test test) {
		super(test);
	}

	public String getName() {
		return ProcessEventTypes.Null;
	}

	public Status getStatus() {
		return null;
	}

}
