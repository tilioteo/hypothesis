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
import com.google.gwt.user.client.Timer;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public abstract class Counter {
	public enum Direction {
		Up, Down
	};

	public interface UpdateEventHandler extends EventHandler {
		void update(UpdateEvent event);
	}

	public static class UpdateEvent extends GwtEvent<UpdateEventHandler> {

		public static final Type<UpdateEventHandler> TYPE = new Type<UpdateEventHandler>();

		private long time;
		private Direction direction;
		private long timeSlice;

		public UpdateEvent(Counter counter, long time, Direction direction,
				long timeSlice) {
			setSource(counter);
			this.time = time;
			this.direction = direction;
			this.timeSlice = timeSlice;
		}

		public Counter getCounter() {
			return (Counter) getSource();
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

		public StartEvent(Counter counter, long time, Direction direction,
				boolean resumed) {
			setSource(counter);
			this.time = time;
			this.direction = direction;
			this.resumed = resumed;
		}

		public Counter getCounter() {
			return (Counter) getSource();
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

		public StopEvent(Counter counter, long time, Direction direction,
				boolean paused) {
			setSource(counter);
			this.time = time;
			this.direction = direction;
			this.paused = paused;
		}

		public Counter getCounter() {
			return (Counter) getSource();
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
	 * for used timeSlice (key) is created its own handler manager
	 */
	HashMap<Long, HandlerManager> handlerManagerMap = new HashMap<Long, HandlerManager>();
	
	

	private Timer timer = new Timer() {
		@Override
		public void run() {
			if (running) {
				setElapsed();
				handlerManager.fireEvent(new UpdateEvent(Counter.this, counter,
						direction, 0L));
				// onUpdate(counter);

				if (!running) {
					handlerManager.fireEvent(new StopEvent(Counter.this,
							counter, direction, false));
					// onEnd(false);
				} else {
					startTime = new Date();
					schedule(10);
				}
			}
		}
	};

	public long getCounter() {
		return counter;
	}

	public Direction getDirection() {
		return direction;
	}

	public boolean isRunning() {
		return running;
	}

	// public abstract void onEnd(final boolean stopped);
	// public abstract void onUpdate(final long estimatedTime);

	public void pause() {
		if (running) {
			running = false;
			setElapsed();
			handlerManager.fireEvent(new StopEvent(Counter.this, counter,
					direction, true));
		}
	}

	public void resume() {
		if (!running) {
			handlerManager.fireEvent(new StartEvent(Counter.this, counter,
					direction, true));
			running = true;
			startTime = new Date();
			timer.schedule(10);
		}
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
		resume();
	}

	public void stop() {
		if (running) {
			running = false;
			updateCounter(startCounter);
			handlerManager.fireEvent(new StopEvent(Counter.this, counter,
					direction, false));
			// onEnd(true);
		}
	}

	private void updateCounter(long elapsed) {
		if (Direction.Up.equals(direction)) {
			counter = elapsed;
		} else {
			counter = startCounter - elapsed;
		}
	}
	
	public void addUpdateHandler(UpdateEventHandler handler, long timeSlice) {
		HandlerManager handlerManager = handlerManagerMap.get(timeSlice);
		// timeSlice has not registered handler manager
		if (null == handlerManager) {
			handlerManager = new HandlerManager(this);
			handlerManagerMap.put(timeSlice, handlerManager);
		}
		
		handlerManager.addHandler(UpdateEvent.TYPE, handler);
	}

	public void removeUpdateHandler(UpdateEventHandler handler) {
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

	public void addStartHandler(StartEventHandler handler) {
		handlerManager.addHandler(StartEvent.TYPE, handler);
	}

	public void removeStartHandler(StartEventHandler handler) {
		handlerManager.removeHandler(StartEvent.TYPE, handler);
	}

	public void addStopHandler(StopEventHandler handler) {
		handlerManager.addHandler(StopEvent.TYPE, handler);
	}

	public void removeStopHandler(StopEventHandler handler) {
		handlerManager.removeHandler(StopEvent.TYPE, handler);
	}

}
