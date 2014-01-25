/**
 * 
 */
package com.tilioteo.hypothesis.client.ui;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Label;
import com.tilioteo.hypothesis.client.Timer.StartEvent;
import com.tilioteo.hypothesis.client.Timer.StartEventHandler;
import com.tilioteo.hypothesis.client.Timer.StopEvent;
import com.tilioteo.hypothesis.client.Timer.StopEventHandler;
import com.tilioteo.hypothesis.client.Timer.UpdateEvent;
import com.tilioteo.hypothesis.client.Timer.UpdateEventHandler;
import com.tilioteo.hypothesis.client.ui.timer.TimerConnector;
import com.vaadin.shared.Connector;

/**
 * @author kamil
 *
 */
public class VTimerLabel extends Label {

	public static final String CLASSNAME = "v-timerlabel";
	
	private String timeFormat;
	private DateTimeFormat dateTimeFormat;
	private long time = 0L;

	public VTimerLabel() {
		super();
		setStyleName(CLASSNAME);
	}
	
	public String getTimeFormat() {
		return timeFormat;
	}

	public void setTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
		dateTimeFormat = DateTimeFormat.getFormat(timeFormat);
		setTime(time);
	}

	public void setTime(long time) {
		if (this.time != time)
			this.time = time;
		
		setText(dateTimeFormat.format(new Date(this.time)));
	}

	public void registerTimer(Connector timer) {
		if (timer != null) {
			if (timer instanceof TimerConnector) {
				TimerConnector timerConnector = (TimerConnector) timer; 
				
				timerConnector.getWidget().addStartEventHandler(new StartEventHandler() {
					@Override
					public void start(StartEvent event) {
						setTime(event.getTime());
					}
				});
				
				timerConnector.getWidget().addStopEventHandler(new StopEventHandler() {
					@Override
					public void stop(StopEvent event) {
						setTime(event.getTime());
					}
				});
				
				timerConnector.getWidget().addUpdateEventHandler(100, new UpdateEventHandler() {
					@Override
					public void update(UpdateEvent event) {
						setTime(event.getTime());
					}
				});
			}
		}
	}
}
