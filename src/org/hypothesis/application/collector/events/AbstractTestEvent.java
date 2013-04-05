/**
 * 
 */
package org.hypothesis.application.collector.events;

import org.hypothesis.entity.Test;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public abstract class AbstractTestEvent extends AbstractRunningEvent implements
		HasStatus {

	protected AbstractTestEvent(Test test) {
		super(test);
	}

	public Test getTest() {
		return (Test) getSource();
	}
}
