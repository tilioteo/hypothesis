/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.entity.SimpleTest;
import com.vaadin.server.ErrorHandler;

/**
 * @author kamil
 *
 */
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
