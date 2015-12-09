/**
 * 
 */
package org.hypothesis.event.model;

import java.util.Date;

import org.hypothesis.event.interfaces.ProcessEvent;

import com.vaadin.server.ErrorHandler;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public abstract class AbstractProcessEvent implements ProcessEvent {

	private String name;
	private Date timestamp;
	private Date clientTimestamp = null;

	private ErrorHandler errorHandler = null;

	protected AbstractProcessEvent(ErrorHandler errorHandler) {
		this.timestamp = new Date();
		this.errorHandler = errorHandler;
	}

	protected void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

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
