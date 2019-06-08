/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.event.model;

import org.hypothesis.data.api.Status;
import org.hypothesis.data.dto.TestDto;

import com.vaadin.server.ErrorHandler;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class StartTestEvent extends AbstractTestEvent {

	private final TestDto test;

	public StartTestEvent(TestDto test) {
		this(test, null);
	}

	public StartTestEvent(TestDto test, ErrorHandler errorHandler) {
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

	public TestDto getTest() {
		return test;
	}
}
