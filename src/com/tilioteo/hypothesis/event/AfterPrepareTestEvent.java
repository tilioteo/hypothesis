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
@SuppressWarnings("serial")
public class AfterPrepareTestEvent extends AbstractProcessEvent {

	
	public AfterPrepareTestEvent(SimpleTest source) {
		this(source, null);
	}

	public AfterPrepareTestEvent(SimpleTest source, ErrorHandler errorHandler) {
		super(source, errorHandler);
	}

	@Override
	public String getName() {
		return ProcessEventTypes.Null;
	}
	
	public SimpleTest getTest() {
		return (SimpleTest) super.getSource();
	}

}
