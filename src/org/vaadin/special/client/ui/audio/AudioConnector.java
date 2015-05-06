/**
 * 
 */
package org.vaadin.special.client.ui.audio;

import org.vaadin.special.client.DateUtility;
import org.vaadin.special.client.MediaEvents.PauseEvent;
import org.vaadin.special.client.MediaEvents.PauseHandler;
import org.vaadin.special.client.MediaEvents.PlayEvent;
import org.vaadin.special.client.MediaEvents.PlayHandler;
import org.vaadin.special.client.ui.VAudio;
import org.vaadin.special.shared.ui.audio.AudioServerRpc;
import org.vaadin.special.shared.ui.audio.AudioState;

import com.google.gwt.event.dom.client.CanPlayThroughEvent;
import com.google.gwt.event.dom.client.CanPlayThroughHandler;
import com.google.gwt.event.dom.client.EndedEvent;
import com.google.gwt.event.dom.client.EndedHandler;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(org.vaadin.special.ui.Audio.class)
public class AudioConnector extends com.vaadin.client.ui.audio.AudioConnector
		implements CanPlayThroughHandler, PlayHandler, EndedHandler, PauseHandler {

	private boolean started = false;

	@Override
	protected void init() {
		super.init();

		getWidget().addCanPlayThroughHandler(this);
		getWidget().addEndedHandler(this);
		getWidget().addPlayHandler(this);
		getWidget().addPauseHandler(this);
	}

	@Override
	public AudioState getState() {
		return (AudioState) super.getState();
	}

	@Override
	public VAudio getWidget() {
		return (VAudio) super.getWidget();
	}

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);

	}

	@Override
	public void onCanPlayThrough(CanPlayThroughEvent event) {
		getRpcProxy(AudioServerRpc.class).canPlayThrough(DateUtility.getTimestamp());
	}

	@Override
	public void onEnded(EndedEvent event) {
		getRpcProxy(AudioServerRpc.class).stop(DateUtility.getTimestamp(), getWidget().getCurrentTime(), false);
	}

	@Override
	public void onPause(PauseEvent event) {
		getRpcProxy(AudioServerRpc.class).stop(DateUtility.getTimestamp(), getWidget().getCurrentTime(), true);
	}

	@Override
	public void onPlay(PlayEvent event) {
		getRpcProxy(AudioServerRpc.class).start(DateUtility.getTimestamp(), started ? getWidget().getCurrentTime() : 0.0, started);

		if (!started) {
			started = true;
		}
	}

}
