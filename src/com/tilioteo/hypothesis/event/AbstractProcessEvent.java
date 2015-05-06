/**
 * 
 */
package com.tilioteo.hypothesis.event;

import java.util.Date;

import com.vaadin.server.ErrorHandler;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public abstract class AbstractProcessEvent implements
		ProcessEvent {

	private Date timestamp;
	private Date clientTimestamp = null;
	
	private ErrorHandler errorHandler = null;

	protected AbstractProcessEvent(ErrorHandler errorHandler) {
		this.timestamp = new Date();
		this.errorHandler = errorHandler;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public Date getClientTimestamp() {
		return clientTimestamp;
	}

	public void setClientTimestamp(Date clientTimestamp) {
		this.clientTimestamp = clientTimestamp;
	}

	@Override
	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}
	
	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}
	
	/*public void updateTimestamp() {
		timestamp = new Date();
	}*/
}
