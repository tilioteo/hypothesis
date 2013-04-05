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
public class CloseTestEvent extends AbstractTestEvent {

	public CloseTestEvent(Test test) {
		super(test);
	}

	public String getName() {
		return ProcessEvents.Null;
	}

	public Status getStatus() {
		return null;
	}

}
