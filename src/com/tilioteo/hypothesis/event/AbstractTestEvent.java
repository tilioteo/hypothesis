/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.core.HasStatus;
import com.tilioteo.hypothesis.entity.SimpleTest;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public abstract class AbstractTestEvent extends AbstractRunningEvent implements	HasStatus {

	protected AbstractTestEvent(SimpleTest test) {
		super(test);
	}

	public SimpleTest getTest() {
		return (SimpleTest) getSource();
	}
}
