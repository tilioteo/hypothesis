package com.tilioteo.hypothesis.client.ui;

import com.google.gwt.user.client.ui.Widget;
import com.tilioteo.hypothesis.client.Timer;
import com.tilioteo.hypothesis.client.Timer.Direction;
import com.tilioteo.hypothesis.client.Timer.StartEventHandler;
import com.tilioteo.hypothesis.client.Timer.StopEventHandler;
import com.tilioteo.hypothesis.client.Timer.UpdateEventHandler;

public class VTimer extends Widget {

	//public static final String CLASSNAME = "timer";
	
	private Timer timer;

	public VTimer() {
		timer = new Timer();
	}
	
	/*public Timer getTimer() {
		return timer;
	}*/
	
	public void start(long time) {
		timer.start(time);
	}

	public void stop() {
		timer.stop();
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

	public void removeUpdateEventHandler(UpdateEventHandler handler) {
		timer.removeUpdateEventHandler(handler);
	}

}