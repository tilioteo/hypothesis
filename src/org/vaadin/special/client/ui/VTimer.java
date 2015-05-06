package org.vaadin.special.client.ui;

import java.util.Set;

import org.vaadin.special.client.Timer;
import org.vaadin.special.client.Timer.Direction;
import org.vaadin.special.client.Timer.StartEventHandler;
import org.vaadin.special.client.Timer.StopEventHandler;
import org.vaadin.special.client.Timer.UpdateEventHandler;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.Widget;

public class VTimer extends Widget {

	public static final String CLASSNAME = "v-timer";

	private Timer timer;

	public VTimer() {
		setElement(Document.get().createDivElement());
		setStyleName(CLASSNAME);
		setVisible(false);
		timer = new Timer();
	}

	public void start(final long time) {
		timer.start(time);
	}

	public void stop() {
		stop(false);
	}

	public void stop(boolean silent) {
		timer.stop(silent);
	}

	public void pause() {
		timer.pause();
	}

	public void resume() {
		timer.resume();
	}

	public boolean isRunning() {
		return timer.isRunning();
	}

	public void setDirection(String direction) {
		setDirection(Direction.valueOf(direction));
	}

	public void setDirection(Direction direction) {
		try {
			timer.setDirection(direction);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public void addStartEventHandler(StartEventHandler handler) {
		timer.addStartEventHandler(handler);
	}

	public void removeStartEventHandler(StartEventHandler handler) {
		timer.removeStartEventHandler(handler);
	}

	public void addStopEventHandler(StopEventHandler handler) {
		timer.addStopEventHandler(handler);
	}

	public void removeStopEventHandler(StopEventHandler handler) {
		timer.removeStopEventHandler(handler);
	}

	public void addUpdateEventHandler(long interval, UpdateEventHandler handler) {
		timer.addUpdateEventHandler(interval, handler);
	}

	public void removeUpdateEventHandler(long interval, UpdateEventHandler handler) {
		timer.removeUpdateEventHandler(interval, handler);
	}
	
	public void removeUpdateEventHandler(UpdateEventHandler handler) {
		timer.removeUpdateEventHandler(handler);
	}
	
	public boolean hasUpdateEventHandler(long interval, UpdateEventHandler handler) {
		return timer.hasUpdateEventHandler(interval, handler);
	}
	
	public Set<Long> getHandledIntervals() {
		return timer.getHandledIntervals();
	}

}