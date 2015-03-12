/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.vaadin.server.ErrorHandler;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public abstract class AbstractRunningEvent extends AbstractProcessEvent {

	protected AbstractRunningEvent(Object source, ErrorHandler errorHandler) {
		super(source, errorHandler);
	}
}
