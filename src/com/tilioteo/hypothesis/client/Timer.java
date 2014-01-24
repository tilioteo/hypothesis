/**
 * 
 */
package com.tilioteo.hypothesis.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class Timer {
	public enum Direction {
		Up, Down
	};
	
	/**
	 * tick of internal timer
	 */
	public static final int TIMER_TICK = 10;

	public interface UpdateEventHandler extends EventHandler {
		void update(UpdateEvent event);
	}

	public static class UpdateEvent extends GwtEvent<UpdateEventHandler> {

		public static final Type<UpdateEventHandler> TYPE = new Type<UpdateEventHandler>();

		private long time;
		private Direction direction;
		private long timeSlice;

		public UpdateEvent(Timer timer, long time, Direction direction,
				long timeSlice) {
			setSource(timer);
			this.time = time;
			this.direction = direction;
			this.timeSlice = timeSlice;
		}

		public Timer getTimer() {
			return (Timer) getSource();
		}

		public long getTime() {
			return time;
		}

		public Direction getDirection() {
			return direction;
		}

		public long getTimeSlice() {
			return timeSlice;
		}

		@Override
		public Type<UpdateEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(UpdateEventHandler handler) {
			handler.update(this);

		}

	}

	public interface StartEventHandler extends EventHandler {
		void start(StartEvent event);
	}

	public static class StartEvent extends GwtEvent<StartEventHandler> {

		public static final Type<StartEventHandler> TYPE = new Type<StartEventHandler>();

		private long time;
		private Direction direction;
		boolean resumed;

		public StartEvent(Timer timer, long time, Direction direction,
				boolean resumed) {
			setSource(timer);
			this.time = time;
			this.direction = direction;
			this.resumed = resumed;
		}

		public Timer getTimer() {
			return (Timer) getSource();
		}

		public long getTime() {
			return time;
		}

		public Direction getDirection() {
			return direction;
		}

		public boolean isResumed() {
			return resumed;
		}

		@Override
		public Type<StartEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(StartEventHandler handler) {
			handler.start(this);

		}

	}

	public interface StopEventHandler extends EventHandler {
		void stop(StopEvent event);
	}

	public static class StopEvent extends GwtEvent<StopEventHandler> {

		public static final Type<StopEventHandler> TYPE = new Type<StopEventHandler>();

		private long time;
		private Direction direction;
		private boolean paused;

		public StopEvent(Timer timer, long time, Direction direction,
				boolean paused) {
			setSource(timer);
			this.time = time;
			this.direction = direction;
			this.paused = paused;
		}

		public Timer getTimer() {
			return (Timer) getSource();
		}

		public long getTime() {
			return time;
		}

		public Direction getDirection() {
			return direction;
		}

		public boolean isPaused() {
			return paused;
		}

		@Override
		public Type<StopEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(StopEventHandler handler) {
			handler.stop(this);

		}

	}

	private long startCounter = 0;
	private long counter = 0;
	private boolean running = false;
	private long elapsed = 0;
	private Date startTime;
	private Direction direction = Direction.Up;

	/**
	 * handle manager for StartEvent and StopEvent
	 */
	HandlerManager handlerManager = new HandlerManager(this);
	
	/**
	 * map of handler managers
	 * for registered timeSlice (key) is created its own handler manager
	 */
	HashMap<Long, HandlerManager> handlerManagerMap = new HashMap<Long, HandlerManager>();
	
	/**
	 * internal timer runs for 1 period of 10 ms
	 */
	private com.google.gwt.user.client.Timer internalTimer = new com.google.gwt.user.client.Timer() {
		@Override
		public void run() {
			if (running) {
				setElapsed();

				if (!running) {
					onStop(counter, direction, false);
					handlerManager.fireEvent(new StopEvent(Timer.this,
							counter, direction, false));
				} else {
					startTime = new Date();
					schedule(TIMER_TICK);
				}
			}
		}
	};
	
	protected void onStart(long time, Direction direction, boolean resumed) {};

	protected void onStop(long time, Direction direction, boolean paused) {};

	protected void onUpdate(long time, Direction direction, long timeSlice) {};

	public long getCounter() {
		return counter;
	}

	public Direction getDirection() {
		return direction;
	}

	public boolean isRunning() {
		return running;
	}

	public void pause() {
		if (running) {
			running = false;
			setElapsed();
			onStop(counter, direction, true);
			handlerManager.fireEvent(new StopEvent(Timer.this, counter,
					direction, true));
		}
	}
	
	protected void resume(boolean started) {
		if (!running) {
			onStart(counter, direction, started);
			handlerManager.fireEvent(new StartEvent(Timer.this, counter,
					direction, started));
			running = true;
			startTime = new Date();
			internalTimer.schedule(TIMER_TICK);
		}
	}

	public void resume() {
		resume(true);
	}

	public void setDirection(Direction direction) throws Exception {
		if (!running)
			this.direction = direction;
		else
			throw new Exception("Timer cannot change direction until running");
	}

	protected void setElapsed() {
		elapsed += new Date().getTime() - startTime.getTime();

		if (elapsed >= startCounter) {
			running = false;
			elapsed = startCounter;
		}
		updateCounter(elapsed);
	}

	public void start(long miliSeconds) {
		running = false;
		startCounter = miliSeconds;
		if (Direction.Up.equals(direction))
			counter = 0;
		else
			counter = startCounter;

		elapsed = 0;
		resume(false);
	}

	public void stop() {
		if (running) {
			running = false;
			updateCounter(startCounter);
			onStop(counter, direction, false);
			handlerManager.fireEvent(new StopEvent(Timer.this, counter,
					direction, false));
		}
	}

	private void updateCounter(long elapsed) {
		if (Direction.Up.equals(direction)) {
			counter = elapsed;
		} else {
			counter = startCounter - elapsed;
		}
		signalTimeSlices(elapsed);
	}
	
	private void signalTimeSlices(long elapsed) {
		for (Long timeSlice : handlerManagerMap.keySet()) {
			// modulo for time slices passed in elapsed time
			long rest = elapsed % timeSlice; 
			// if rest passes into first timer tick interval then fire update event
			// for this time slice
			if (rest >= 0 && rest < TIMER_TICK) {
				onUpdate(counter, direction, timeSlice);
				handlerManagerMap.get(timeSlice).fireEvent(new UpdateEvent(Timer.this, counter, direction, timeSlice));
			}
		}
		
	}

	public void addUpdateEventHandler(long interval, UpdateEventHandler handler) {
		HandlerManager handlerManager = handlerManagerMap.get(interval);
		
		// timeSlice has not registered handler manager
		// create and register them
		if (null == handlerManager) {
			handlerManager = new HandlerManager(this);
			handlerManagerMap.put(interval, handlerManager);
		}
		
		handlerManager.addHandler(UpdateEvent.TYPE, handler);
	}

	public void removeUpdateEventHandler(UpdateEventHandler handler) {
		ArrayList<Long> pruneList = new ArrayList<Long>();
		
		for (Long key : handlerManagerMap.keySet()) {
			HandlerManager handlerManager = handlerManagerMap.get(key);
			handlerManager.removeHandler(UpdateEvent.TYPE, handler);
			
			if (handlerManager.getHandlerCount(UpdateEvent.TYPE) == 0)
				pruneList.add(key);
		}
		
		// clean unused handler managers
		for (Long key : pruneList) {
			handlerManagerMap.remove(key);
		}
		
		handlerManager.removeHandler(UpdateEvent.TYPE, handler);
	}

	public void addStartEventHandler(StartEventHandler handler) {
		handlerManager.addHandler(StartEvent.TYPE, handler);
	}

	public void removeStartEventHandler(StartEventHandler handler) {
		handlerManager.removeHandler(StartEvent.TYPE, handler);
	}

	public void addStopEventHandler(StopEventHandler handler) {
		handlerManager.addHandler(StopEvent.TYPE, handler);
	}

	public void removeStopEventHandler(StopEventHandler handler) {
		handlerManager.removeHandler(StopEvent.TYPE, handler);
	}

}
