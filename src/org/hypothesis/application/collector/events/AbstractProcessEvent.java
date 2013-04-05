/**
 * 
 */
package org.hypothesis.application.collector.events;

import java.util.Date;
import java.util.EventObject;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public abstract class AbstractProcessEvent extends EventObject implements
		HasName {

	private Date datetime;

	protected AbstractProcessEvent(Object source) {
		super(source);
		this.datetime = new Date();
	}

	public Date getDatetime() {
		return datetime;
	}
}
