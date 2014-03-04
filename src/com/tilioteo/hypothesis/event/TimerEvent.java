/**
 * 
 */
package com.tilioteo.hypothesis.event;

import com.tilioteo.hypothesis.ui.Timer;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class TimerEvent extends AbstractComponentEvent<Timer> {

	public static class Start extends TimerEvent {

		public Start(TimerData data) {
			super(data);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.TimerStart;
		}

	}

	public static class Stop extends TimerEvent {

		public Stop(TimerData data) {
			super(data);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.TimerStop;
		}

	}

	public static class Update extends TimerEvent {

		public Update(TimerData data) {
			super(data);
		}

		@Override
		public String getName() {
			return ProcessEventTypes.TimerUpdate;
		}

	}

	protected TimerEvent(TimerData data) {
		super(data);
	}

}
