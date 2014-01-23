/**
 * 
 */
package com.tilioteo.hypothesis.client;

import java.util.Date;

import com.google.gwt.user.client.Timer;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public abstract class Counter {
	public enum Direction {
		Up, Down
	};

	private long startCounter = 0;
	private long counter = 0;
	private boolean running = false;
	private long elapsed = 0;
	private Date startTime;
	private Direction direction = Direction.Up;

	private Timer timer = new Timer() {
		@Override
		public void run() {
			if (running) {
				setElapsed();
				onUpdate(counter);

				if (!running)
					onEnd(false);
				else {
					startTime = new Date();
					schedule(20);
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

	/*
	 * private final long getCounter() { return counter; }
	 */

	public boolean isRunning() {
		return running;
	}

	public abstract void onEnd(final boolean stopped);

	public abstract void onUpdate(final long estimatedTime);

	public void pause() {
		if (running) {
			running = false;
			setElapsed();
		}
	}

	public void resume() {
		if (!running /* && counter > 0 */) {
			running = true;
			startTime = new Date();
			timer.schedule(20);
		}
	}

	public void setDirection(Direction direction) throws Exception {
		if (!running)
			this.direction = direction;
		else
			throw new Exception("Timer cannot change direction when running");
	}

	protected void setElapsed() {
		elapsed += new Date().getTime() - startTime.getTime();

		if (elapsed >= startCounter) {
			running = false;
			elapsed = startCounter;
		}
		updateCounter(elapsed);
	}

	public void start(long seconds) {
		running = false;
		startCounter = seconds * 1000;
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
			onEnd(true);
		}
	}

	private void updateCounter(long elapsed) {
		if (Direction.Up.equals(direction)) {
			counter = elapsed;
		} else {
			counter = startCounter - elapsed;
		}
	}
}
