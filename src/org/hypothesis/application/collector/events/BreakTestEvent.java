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
public class BreakTestEvent extends AbstractTestEvent {

	public BreakTestEvent() {
		this(null);
	}

	public BreakTestEvent(Test test) {
		super(test);
	}

	public String getName() {
		return ProcessEvents.BreakTest;
	}

	public Status getStatus() {
		return Status.BROKEN_BY_CLIENT;
	}

}
