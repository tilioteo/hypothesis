/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.event.model;

import org.hypothesis.data.model.SimpleTest;
import org.hypothesis.data.model.Status;
import com.vaadin.server.ErrorHandler;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class StartTestEvent extends AbstractTestEvent {

	private SimpleTest test;

	public StartTestEvent(SimpleTest test) {
		this(test, null);
	}

	public StartTestEvent(SimpleTest test, ErrorHandler errorHandler) {
		super(errorHandler);
		this.test = test;
	}

	@Override
	public String getName() {
		return ProcessEventTypes.StartTest;
	}

	@Override
	public Status getStatus() {
		return Status.STARTED;
	}

	public SimpleTest getTest() {
		return test;
	}
}
