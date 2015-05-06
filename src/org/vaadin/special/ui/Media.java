/**
 * 
 */
package org.vaadin.special.ui;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.vaadin.special.event.ComponentEvent;

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
	public static abstract class MediaEvent extends ComponentEvent {

		private double mediaTime;

		protected MediaEvent(long timestamp, Component source, double mediaTime) {
			super(timestamp, source);
			this.mediaTime = mediaTime;
		}

		public double getMediaTime() {
			return mediaTime;
		}
	}

	@SuppressWarnings("serial")
	public static class CanPlayThroughEvent extends MediaEvent {

		public static final String EVENT_ID = "canPlayThrough";

		public CanPlayThroughEvent(long timestamp, Component source) {
			super(timestamp, source, 0.0);
		}
    	
    }

    /**
     * Interface for listening for a {@link CanPlayThroughEvent} fired by a
     * {@link Media}.
     * 
     */
    public interface CanPlayThroughListener extends ConnectorEventListener {

        public static final Method MEDIA_CAN_PLAY_THROUGH = ReflectTools.findMethod(
        		CanPlayThroughListener.class, CanPlayThroughEvent.EVENT_ID, CanPlayThroughEvent.class);

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

		public StartEvent(long timestamp, Component source, double mediaTime, boolean resumed) {
			super(timestamp, source, mediaTime);
			this.resumed = resumed;
		}

		public boolean isResumed() {
			return resumed;
		}
	}

	public interface StartListener extends Serializable {

		public static final Method MEDIA_START_METHOD = ReflectTools
				.findMethod(StartListener.class, StartEvent.EVENT_ID, StartEvent.class);

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

		public StopEvent(long timestamp, Component source, double mediaTime, boolean paused) {
			super(timestamp, source, mediaTime);
			this.paused = paused;
		}

		public boolean isPaused() {
			return paused;
		}
	}

	public interface StopListener extends Serializable {

		public static final Method MEDIA_STOP_METHOD = ReflectTools.findMethod(
				StopListener.class, StopEvent.EVENT_ID, StopEvent.class);

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
