/**
 * 
 */
package com.tilioteo.hypothesis.event;

import java.util.Date;
import java.util.EventObject;

import com.vaadin.server.ErrorHandler;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public abstract class AbstractProcessEvent extends EventObject implements
		ProcessEvent {

	private Date timestamp;
	private ErrorHandler errorHandler = null;

	protected AbstractProcessEvent(Object source, ErrorHandler errorHandler) {
		super(source);
		this.timestamp = new Date();
		this.errorHandler = errorHandler;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}
	
	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}
	
	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}
}
