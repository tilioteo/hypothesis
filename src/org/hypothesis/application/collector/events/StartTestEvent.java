/**
 * 
 */
package org.hypothesis.application.collector.events;

import org.hypothesis.entity.Test;
import org.hypothesis.entity.Test.Status;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class StartTestEvent extends AbstractTestEvent implements HasName {

	public StartTestEvent(Test test) {
		super(test);
	}

	public String getName() {
		return ProcessEvents.StartTest;
	}

	public Status getStatus() {
		return Status.STARTED;
	}
}
