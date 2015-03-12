/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.vaadin.event.ConnectorEventListener;
import com.vaadin.ui.AbstractMedia;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;

/**
 * @author kamil
 *
 */
public abstract class Media {

    @SuppressWarnings("serial")
	public static abstract class MediaEvent extends Component.Event {

		private double time;

		protected MediaEvent(Component source, double time) {
			super(source);
			this.time = time;
		}

		public double getTime() {
			return time;
		}
	}

	@SuppressWarnings("serial")
	public static class CanPlayThroughEvent extends MediaEvent {

		public static final String EVENT_ID = "canplaythrough";

		public CanPlayThroughEvent(Component source) {
			super(source, 0.0);
		}
    	
    }

    /**
     * Interface for listening for a {@link CanPlayThroughEvent} fired by a
     * {@link Audio}.
     * 
     */
    public interface CanPlayThroughListener extends ConnectorEventListener {

        public static final Method MEDIA_CAN_PLAY_THROUGH = ReflectTools.findMethod(
        		CanPlayThroughListener.class, "canPlayThrough", CanPlayThroughEvent.class);

        /**
         * Called when a {@link AbstractMedia} can play through without having to stop for buffering.
         * A reference to the component is given by {@link ClickEvent#getComponent()}.
         * 
         * @param event
         *            An event containing information about the component.
         */
        public void canPlayThrough(CanPlayThroughEvent event);
    }

	@SuppressWarnings("serial")
	public static class StartEvent extends MediaEvent {

		public static final String EVENT_ID = "start";

		private boolean resumed;

		public StartEvent(Component source, double time, boolean resumed) {
			super(source, time);
			this.resumed = resumed;
		}

		public boolean isResumed() {
			return resumed;
		}
	}

	public interface StartListener extends Serializable {

		public static final Method MEDIA_START_METHOD = ReflectTools
				.findMethod(StartListener.class, "start", StartEvent.class);

		/**
		 * Called when a {@link AbstractMedia} has been started. A reference to the
		 * component is given by {@link StartEvent#getComponent()}.
		 * 
		 * @param event
		 *            An event containing information about the timer.
		 */
		public void start(StartEvent event);

	}

	@SuppressWarnings("serial")
	public static class StopEvent extends MediaEvent {

		public static final String EVENT_ID = "stop";

		private boolean paused;

		public StopEvent(Component source, double time, boolean paused) {
			super(source, time);
			this.paused = paused;
		}

		public boolean isPaused() {
			return paused;
		}
	}

	public interface StopListener extends Serializable {

		public static final Method MEDIA_STOP_METHOD = ReflectTools.findMethod(
				StopListener.class, "stop", StopEvent.class);

		/**
		 * Called when a {@link AbstractMedia} has been stopped. A reference to the
		 * component is given by {@link StopEvent#getComponent()}.
		 * 
		 * @param event
		 *            An event containing information about the timer.
		 */
		public void stop(StopEvent event);

	}

}
