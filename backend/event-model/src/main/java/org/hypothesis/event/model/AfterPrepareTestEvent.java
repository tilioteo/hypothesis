/**
 * 
 */
package org.hypothesis.event.model;

import org.hypothesis.data.model.SimpleTest;
import com.vaadin.server.ErrorHandler;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class AfterPrepareTestEvent extends AbstractProcessEvent {

	private SimpleTest test;

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
