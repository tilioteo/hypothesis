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
public class FinishTestEvent extends AbstractTestEvent {

	public FinishTestEvent(Test test) {
		super(test);
	}

	public String getName() {
		return ProcessEvents.FinishTest;
	}

	public Status getStatus() {
		return Status.FINISHED;
	}

}
