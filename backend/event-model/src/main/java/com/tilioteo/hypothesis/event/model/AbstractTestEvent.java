/**
 * 
 */
package com.tilioteo.hypothesis.event.model;

import com.tilioteo.hypothesis.data.interfaces.HasStatus;
import com.vaadin.server.ErrorHandler;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public abstract class AbstractTestEvent extends AbstractRunningEvent implements HasStatus {

	protected AbstractTestEvent(ErrorHandler errorHandler) {
		super(errorHandler);
	}
}
