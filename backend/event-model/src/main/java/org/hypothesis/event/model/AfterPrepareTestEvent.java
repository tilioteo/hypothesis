/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.event.model;

import org.hypothesis.data.dto.TestDto;

import com.vaadin.server.ErrorHandler;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class AfterPrepareTestEvent extends AbstractProcessEvent {

	private final TestDto test;

	public AfterPrepareTestEvent(TestDto source) {
		this(source, null);
	}

	public AfterPrepareTestEvent(TestDto test, ErrorHandler errorHandler) {
		super(errorHandler);
		this.test = test;
	}

	@Override
	public String getName() {
		return ProcessEventTypes.Null;
	}

	public TestDto getTest() {
		return test;
	}

}
