/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.core.HasStatus;
import com.tilioteo.hypothesis.entity.Test;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public abstract class AbstractTestEvent extends AbstractRunningEvent implements	HasStatus {

	protected AbstractTestEvent(Test test) {
		super(test);
	}

	public Test getTest() {
		return (Test) getSource();
	}
}
