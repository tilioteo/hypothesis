/**
 * 
 */
package org.vaadin.special.ui;

import org.vaadin.special.shared.ui.audio.AudioServerRpc;
import org.vaadin.special.shared.ui.audio.AudioState;
import org.vaadin.special.ui.Media.CanPlayThroughEvent;
import org.vaadin.special.ui.Media.CanPlayThroughListener;
import org.vaadin.special.ui.Media.StartEvent;
import org.vaadin.special.ui.Media.StartListener;
import org.vaadin.special.ui.Media.StopEvent;
import org.vaadin.special.ui.Media.StopListener;

import com.vaadin.server.Resource;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class Audio extends com.vaadin.ui.Audio {

	protected AudioServerRpc rpc = new AudioServerRpc() {

		@Override
		public void stop(long timestamp, double time, boolean paused) {
			fireEvent(new StopEvent(timestamp, Audio.this, time, paused));
		}

		@Override
		public void start(long timestamp, double time, boolean resumed) {
			fireEvent(new StartEvent(timestamp, Audio.this, time, resumed));
		}

		@Override
		public void canPlayThrough(long timestamp) {
			fireEvent(new CanPlayThroughEvent(timestamp, Audio.this));
		}
	};

	public Audio() {
        this("", null);
	}

	/**
	 * @param caption
	 *            The caption of the audio component.
	 */
	public Audio(String caption) {
        this(caption, null);
	}

	/**
	 * @param caption
	 *            The caption of the audio component
	 * @param source
	 *            The audio file to play.
	 */
	public Audio(String caption, Resource source) {
		super(caption, source);

    	registerRpc(rpc);
	}

    @Override
    protected AudioState getState() {
        return (AudioState) super.getState();
    }

	public void addCanPlayThroughListener(CanPlayThroughListener listener) {
		addListener(CanPlayThroughEvent.EVENT_ID, CanPlayThroughEvent.class, listener,
				CanPlayThroughListener.MEDIA_CAN_PLAY_THROUGH);
	}

	public void removeCanPlayThroughListener(CanPlayThroughListener listener) {
		removeListener(CanPlayThroughEvent.EVENT_ID, CanPlayThroughEvent.class, listener);
	}

	public void addStartListener(StartListener listener) {
		addListener(StartEvent.EVENT_ID, StartEvent.class, listener,
				StartListener.MEDIA_START_METHOD);
	}

	public void removeStartListener(StartListener listener) {
		removeListener(StartEvent.EVENT_ID, StartEvent.class, listener);
	}

	public void addStopListener(StopListener listener) {
		addListener(StopEvent.EVENT_ID, StopEvent.class, listener,
				StopListener.MEDIA_STOP_METHOD);
	}

	public void removeStopListener(StopListener listener) {
		removeListener(StopEvent.EVENT_ID, StopEvent.class, listener);
	}

}
