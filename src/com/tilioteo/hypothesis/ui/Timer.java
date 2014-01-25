package com.tilioteo.hypothesis.ui;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import com.tilioteo.hypothesis.shared.ui.timer.TimerClientRpc;
import com.tilioteo.hypothesis.shared.ui.timer.TimerServerRpc;
import com.tilioteo.hypothesis.shared.ui.timer.TimerState;
import com.tilioteo.hypothesis.shared.ui.timer.TimerState.Direction;
import com.vaadin.event.EventRouter;
import com.vaadin.shared.ui.ComponentStateUtil;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.util.ReflectTools;

@SuppressWarnings("serial")
public class Timer extends AbstractComponent {

	private TimerServerRpc rpc = new TimerServerRpc() {

		@Override
		public void start(long time, String direction, boolean resumed) {
			fireEvent(new StartEvent(Timer.this, time, Direction.valueOf(direction), resumed));
		}

		@Override
		public void stop(long time, String direction, boolean paused) {
			fireEvent(new StopEvent(Timer.this, time, Direction.valueOf(direction), paused));
		}

		@Override
		public void update(long time, String direction, long interval) {
			EventRouter eventRouter = eventRouterMap.get(interval);
			if (eventRouter != null) {
				eventRouter.fireEvent(new UpdateEvent(Timer.this, time, Direction.valueOf(direction), interval));
			}
		}

	};
	
	private TimerClientRpc clientRpc;
	
	private HashMap<Long, EventRouter> eventRouterMap = new HashMap<Long, EventRouter>();

	public abstract class TimerEvent extends Component.Event {

		private long time;
		private Direction direction;

		protected TimerEvent(Component source, long time, Direction direction) {
			super(source);
			this.time = time;
			this.direction = direction;
		}

		public long getTime() {
			return time;
		}

		public Direction getDirection() {
			return direction;
		}
	}

	public class StartEvent extends TimerEvent {

		public static final String EVENT_ID = "start";

		private boolean resumed;

		public StartEvent(Component source, long time, Direction direction,
				boolean resumed) {
			super(source, time, direction);
			this.resumed = resumed;
		}

		public boolean isResumed() {
			return resumed;
		}
	}

	public interface StartListener extends Serializable {

		public static final Method TIMER_START_METHOD = ReflectTools
				.findMethod(StartListener.class, "start", StartEvent.class);

		/**
		 * Called when a {@link Timer} has been started. A reference to the
		 * component is given by {@link StartEvent#getComponent()}.
		 * 
		 * @param event
		 *            An event containing information about the timer.
		 */
		public void start(StartEvent event);

	}

	public class StopEvent extends TimerEvent {

		public static final String EVENT_ID = "stop";

		private boolean paused;

		public StopEvent(Component source, long time, Direction direction,
				boolean paused) {
			super(source, time, direction);
			this.paused = paused;
		}

		public boolean isPaused() {
			return paused;
		}
	}

	public interface StopListener extends Serializable {

		public static final Method TIMER_STOP_METHOD = ReflectTools.findMethod(
				StopListener.class, "stop", StopEvent.class);

		/**
		 * Called when a {@link Timer} has been stopped. A reference to the
		 * component is given by {@link StopEvent#getComponent()}.
		 * 
		 * @param event
		 *            An event containing information about the timer.
		 */
		public void stop(StopEvent event);

	}

	public class UpdateEvent extends TimerEvent {

		public static final String EVENT_ID = "update";
		
		private long interval;

		public UpdateEvent(Component source, long time, Direction direction, long interval) {
			super(source, time, direction);
			this.interval = interval;
		}
		
		public long getInterval() {
			return interval;
		}
	}

	public interface UpdateListener extends Serializable {

		public static final Method TIMER_UPDATE_METHOD = ReflectTools
				.findMethod(UpdateListener.class, "update", UpdateEvent.class);

		/**
		 * Called when a {@link Timer} has been updated. A reference to the
		 * component is given by {@link StartEvent#getComponent()}.
		 * 
		 * @param event
		 *            An event containing information about the timer.
		 */
		public void update(UpdateEvent event);

	}

	public Timer() {
		registerRpc(rpc);
		clientRpc = getRpcProxy(TimerClientRpc.class);
	}

	@Override
	public TimerState getState() {
		return (TimerState) super.getState();
	}

	public void start(long time) {
		clientRpc.start(time);
	}

	public void stop() {
		clientRpc.stop();
	}

	public boolean isRunning() {
		clientRpc.getRunning();
		return getState().running;
	}

	public Direction getDirection() {
		return getState().direction;
	}

	public void setDirection(Direction direction) {
		getState().direction = direction;
	}

	public void addStartListener(StartListener listener) {
		addListener(StartEvent.EVENT_ID, StartEvent.class, listener,
				StartListener.TIMER_START_METHOD);
	}

	public void removeStartListener(StartListener listener) {
		removeListener(StartEvent.EVENT_ID, StartedEvent.class, listener);
	}

	public void addStopListener(StopListener listener) {
		addListener(StopEvent.EVENT_ID, StopEvent.class, listener,
				StopListener.TIMER_STOP_METHOD);
	}

	public void removeStopListener(StopListener listener) {
		removeListener(StopEvent.EVENT_ID, StopEvent.class, listener);
	}

	public void addUpdateListener(long interval, UpdateListener listener) {
        boolean needRepaint = eventRouterMap.isEmpty();

		EventRouter eventRouter = eventRouterMap.get(interval);
		if (null == eventRouter) {
			eventRouter = new EventRouter();
			eventRouterMap.put(interval, eventRouter);
        	getState().intervals.add(interval);
		}
		
        eventRouter.addListener(UpdateEvent.class, listener, UpdateListener.TIMER_UPDATE_METHOD);

        if (needRepaint) {
            ComponentStateUtil.addRegisteredEventListener(getState(), UpdateEvent.EVENT_ID);
        }
	}

	public void removeUpdateListener(long interval, UpdateListener listener) {
		EventRouter eventRouter = eventRouterMap.get(interval);

		if (eventRouter != null) {
            eventRouter.removeListener(UpdateEvent.class, listener);
            if (!eventRouter.hasListeners(UpdateEvent.class)) {
            	eventRouterMap.remove(interval);
            	getState().intervals.remove(interval);
            	
                if (eventRouterMap.isEmpty())
                	ComponentStateUtil.removeRegisteredEventListener(getState(), UpdateEvent.EVENT_ID);
            }
        }
	}
	
	public void removeUpdateListener(UpdateListener listener) {
        boolean needRepaint = !eventRouterMap.isEmpty();

		ArrayList<Long> pruneList = new ArrayList<Long>();
		
		for (Long interval : eventRouterMap.keySet()) {
			EventRouter eventRouter = eventRouterMap.get(interval);
			eventRouter.removeListener(UpdateEvent.class, listener);
			
			if (!eventRouter.hasListeners(UpdateEvent.class))
				pruneList.add(interval);
		}

		for (Long interval : pruneList) {
			eventRouterMap.remove(interval);
			getState().intervals.remove(interval);
		}
		
		if (needRepaint && eventRouterMap.isEmpty())
        	ComponentStateUtil.removeRegisteredEventListener(getState(), UpdateEvent.EVENT_ID);
	}

}
