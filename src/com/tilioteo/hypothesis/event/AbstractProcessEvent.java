/**
 * 
 */
package com.tilioteo.hypothesis.event;

import java.util.Date;
import java.util.EventObject;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public abstract class AbstractProcessEvent extends EventObject implements
		ProcessEvent {

	private Date timestamp;

	protected AbstractProcessEvent(Object source) {
		super(source);
		this.timestamp = new Date();
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}
}
