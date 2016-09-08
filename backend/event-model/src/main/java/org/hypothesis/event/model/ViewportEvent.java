/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.event.model;

import java.util.Date;
import java.util.EventObject;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public abstract class ViewportEvent extends EventObject {

	private final Date timestamp;

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
	
	public static class Finish extends ViewportEvent {
		public Finish(Object source) {
			super(source);
		}
	}
}
