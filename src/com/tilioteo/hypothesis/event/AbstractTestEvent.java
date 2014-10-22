/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.core.HasStatus;
import com.tilioteo.hypothesis.entity.SimpleTest;
import com.vaadin.server.ErrorHandler;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public abstract class AbstractTestEvent extends AbstractRunningEvent implements	HasStatus {

	protected AbstractTestEvent(SimpleTest test, ErrorHandler errorHandler) {
		super(test, errorHandler);
	}

	public SimpleTest getTest() {
		return (SimpleTest) getSource();
	}
}
