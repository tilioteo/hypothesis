/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.core.HasStatus;
import com.vaadin.server.ErrorHandler;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public abstract class AbstractTestEvent extends AbstractRunningEvent implements	HasStatus {

	protected AbstractTestEvent(ErrorHandler errorHandler) {
		super(errorHandler);
	}
}
