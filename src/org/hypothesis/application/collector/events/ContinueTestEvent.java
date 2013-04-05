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
public class ContinueTestEvent extends AbstractTestEvent implements HasName {

	public ContinueTestEvent(Test test) {
		super(test);
	}

	public String getName() {
		return ProcessEvents.ContinueTest;
	}

	public Status getStatus() {
		return Status.STARTED;
	}

}
