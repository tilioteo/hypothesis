/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.event.model;

import com.vaadin.server.ErrorHandler;
import org.hypothesis.data.model.SimpleTest;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class AfterPrepareTestEvent extends AbstractProcessEvent {

	private final SimpleTest test;

	public AfterPrepareTestEvent(SimpleTest source) {
		this(source, null);
	}

	public AfterPrepareTestEvent(SimpleTest test, ErrorHandler errorHandler) {
		super(errorHandler);
		this.test = test;
	}

	@Override
	public String getName() {
		return ProcessEventTypes.Null;
	}

	public SimpleTest getTest() {
		return test;
	}

}
