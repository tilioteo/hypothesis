/**
 * 
 */
package com.tilioteo.hypothesis.client.ui.audio;

import com.google.gwt.event.dom.client.CanPlayThroughEvent;
import com.google.gwt.event.dom.client.CanPlayThroughHandler;
import com.google.gwt.event.dom.client.EndedEvent;
import com.google.gwt.event.dom.client.EndedHandler;
import com.tilioteo.hypothesis.client.MediaEvents.PauseEvent;
import com.tilioteo.hypothesis.client.MediaEvents.PauseHandler;
import com.tilioteo.hypothesis.client.MediaEvents.PlayEvent;
import com.tilioteo.hypothesis.client.MediaEvents.PlayHandler;
import com.tilioteo.hypothesis.client.ui.VAudio;
import com.tilioteo.hypothesis.shared.ui.audio.AudioServerRpc;
import com.tilioteo.hypothesis.shared.ui.audio.AudioState;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(com.tilioteo.hypothesis.ui.Audio.class)
public class AudioConnector extends com.vaadin.client.ui.audio.AudioConnector implements CanPlayThroughHandler, PlayHandler, EndedHandler, PauseHandler {

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
		getRpcProxy(AudioServerRpc.class).canPlayThrough();
	}

	@Override
	public void onEnded(EndedEvent event) {
		getRpcProxy(AudioServerRpc.class).stop(getWidget().getCurrentTime(), false);
	}

	@Override
	public void onPause(PauseEvent event) {
		getRpcProxy(AudioServerRpc.class).stop(getWidget().getCurrentTime(), true);
	}

	@Override
	public void onPlay(PlayEvent event) {
		getRpcProxy(AudioServerRpc.class).start(started ? getWidget().getCurrentTime() : 0.0, started);
		
		if (!started) {
			started = true;
		}
	}

}
