/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.slide.ui.Timer;
import com.vaadin.server.ErrorHandler;

/**
 * @author kamil
 *
 */
public abstract class TimerEvent extends AbstractComponentEvent<Timer> {

	public static class Start extends TimerEvent {

		public Start(TimerData data) {
			this(data, null);
		}

		public Start(TimerData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.TimerStart;
		}

	}

	public static class Stop extends TimerEvent {

		public Stop(TimerData data) {
			this(data, null);
		}

		public Stop(TimerData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.TimerStop;
		}

	}

	public static class Update extends TimerEvent {

		public Update(TimerData data) {
			this(data, null);
		}

		public Update(TimerData data, ErrorHandler errorHandler) {
			super(data, errorHandler);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.TimerUpdate;
		}

	}

	protected TimerEvent(TimerData data, ErrorHandler errorHandler) {
		super(data, errorHandler);
	}

}
