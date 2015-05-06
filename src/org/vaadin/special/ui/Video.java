/**
 * 
 */
package org.vaadin.special.ui;

import java.lang.reflect.Method;

import org.vaadin.special.event.MouseEvents;
import org.vaadin.special.shared.ui.video.VideoServerRpc;
import org.vaadin.special.shared.ui.video.VideoState;
import org.vaadin.special.ui.Media.CanPlayThroughEvent;
import org.vaadin.special.ui.Media.CanPlayThroughListener;
import org.vaadin.special.ui.Media.StartEvent;
import org.vaadin.special.ui.Media.StartListener;
import org.vaadin.special.ui.Media.StopEvent;
import org.vaadin.special.ui.Media.StopListener;

import com.vaadin.event.ConnectorEventListener;
import com.vaadin.server.Resource;
import com.vaadin.shared.EventId;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class Video extends com.vaadin.ui.Video {

	protected VideoServerRpc rpc = new VideoServerRpc() {
		@Override
		public void click(long timestamp, MouseEventDetails mouseDetails, double mediaTime) {
			fireEvent(new ClickEvent(timestamp, Video.this, mouseDetails, mediaTime));
		}

		@Override
		public void start(long timestamp, double mediaTime, boolean resumed) {
			fireEvent(new StartEvent(timestamp, Video.this, mediaTime, resumed));
		}

		@Override
		public void stop(long timestamp, double mediaTime, boolean paused) {
			fireEvent(new StopEvent(timestamp, Video.this, mediaTime, paused));
		}

		@Override
		public void canPlayThrough(long timestamp) {
			fireEvent(new CanPlayThroughEvent(timestamp, Video.this));
		}
	};

	public Video() {
		this("", null);
	}

	/**
	 * @param caption
	 *            The caption for this video.
	 */
	public Video(String caption) {
		this(caption, null);
	}

	/**
	 * @param caption
	 *            The caption for this video.
	 * @param source
	 *            The Resource containing the video to play.
	 */
	public Video(String caption, Resource source) {
		super(caption, source);
		registerRpc(rpc);
		setShowControls(false);
	}

	@Override
	protected VideoState getState() {
		return (VideoState) super.getState();
	}

	public class ClickEvent extends MouseEvents.ClickEvent {

		private double mediaTime;

		public ClickEvent(long timestamp, Component source, MouseEventDetails mouseEventDetails, double mediaTime) {
			super(timestamp, source, mouseEventDetails);

			this.mediaTime = mediaTime;
		}

		public double getMediaTime() {
			return mediaTime;
		}
	}

	/**
	 * Interface for listening for a {@link ClickEvent} fired by a
	 * {@link Component}.
	 * 
	 */
	public interface ClickListener extends ConnectorEventListener {

		public static final Method clickMethod = ReflectTools.findMethod(
				ClickListener.class, EventId.CLICK_EVENT_IDENTIFIER, ClickEvent.class);

		/**
		 * Called when a {@link Component} has been clicked. A reference to the
		 * component is given by {@link ClickEvent#getComponent()}.
		 * 
		 * @param event
		 *            An event containing information about the click.
		 */
		public void click(ClickEvent event);
	}

	/**
	 * Add a click listener to the component. The listener is called whenever
	 * the user clicks inside the component. Depending on the content the event
	 * may be blocked and in that case no event is fired.
	 * 
	 * Use {@link #removeClickListener(ClickListener)} to remove the listener.
	 * 
	 * @param listener
	 *            The listener to add
	 */
	public void addClickListener(ClickListener listener) {
		addListener(EventId.CLICK_EVENT_IDENTIFIER, ClickEvent.class, listener, ClickListener.clickMethod);
	}

	/**
	 * Remove a click listener from the component. The listener should earlier
	 * have been added using {@link #addClickListener(ClickListener)}.
	 * 
	 * @param listener
	 *            The listener to remove
	 */
	public void removeClickListener(ClickListener listener) {
		removeListener(EventId.CLICK_EVENT_IDENTIFIER, ClickEvent.class, listener);
	}

	public void addCanPlayThroughListener(CanPlayThroughListener listener) {
		addListener(CanPlayThroughEvent.EVENT_ID, CanPlayThroughEvent.class, listener,
				CanPlayThroughListener.MEDIA_CAN_PLAY_THROUGH);
	}

	public void removeCanPlayThroughListener(CanPlayThroughListener listener) {
		removeListener(CanPlayThroughEvent.EVENT_ID, CanPlayThroughEvent.class, listener);
	}

	public void addStartListener(StartListener listener) {
		addListener(StartEvent.EVENT_ID, StartEvent.class, listener, StartListener.MEDIA_START_METHOD);
	}

	public void removeStartListener(StartListener listener) {
		removeListener(StartEvent.EVENT_ID, StartEvent.class, listener);
	}

	public void addStopListener(StopListener listener) {
		addListener(StopEvent.EVENT_ID, StopEvent.class, listener, StopListener.MEDIA_STOP_METHOD);
	}

	public void removeStopListener(StopListener listener) {
		removeListener(StopEvent.EVENT_ID, StopEvent.class, listener);
	}

}
