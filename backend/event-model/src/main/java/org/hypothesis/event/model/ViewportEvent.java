/**
 * 
 */
package org.hypothesis.event.model;

import java.util.Date;
import java.util.EventObject;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public abstract class ViewportEvent extends EventObject {

	private Date timestamp;

	protected ViewportEvent(Object source) {
		super(source);
		timestamp = new Date();
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public static class Init extends ViewportEvent {
		public Init(Object source) {
			super(source);
		}
	}

	public static class Show extends ViewportEvent {
		public Show(Object source) {
			super(source);
		}
	}
}
