/**
 * 
 */
package org.hypothesis.slide.ui;

import org.hypothesis.slide.shared.ui.timerlabel.TimerLabelState;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings({ "serial" })
public class TimerLabel extends Label {
	
	public static final String DEAFAULT_TIME_FORMAT = "HH:mm:ss.S";

	private Timer timer = null;

	public TimerLabel() {
		super();
	}

	public String getTimeFormat() {
		return getState().timeFormat;
	}

	public void setTimeFormat(String timeFormat) {
		getState().timeFormat = timeFormat;
	}

	@Override
	protected TimerLabelState getState() {
		return (TimerLabelState) super.getState();
	}

	public Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
		getState().timer = timer;
	}

}
