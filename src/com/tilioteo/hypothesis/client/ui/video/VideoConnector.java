/**
 * 
 */
package com.tilioteo.hypothesis.client.ui.video;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.CanPlayThroughEvent;
import com.google.gwt.event.dom.client.CanPlayThroughHandler;
import com.google.gwt.event.dom.client.EndedEvent;
import com.google.gwt.event.dom.client.EndedHandler;
import com.tilioteo.hypothesis.client.MediaEvents.PauseEvent;
import com.tilioteo.hypothesis.client.MediaEvents.PauseHandler;
import com.tilioteo.hypothesis.client.MediaEvents.PlayEvent;
import com.tilioteo.hypothesis.client.MediaEvents.PlayHandler;
import com.tilioteo.hypothesis.client.ui.VVideo;
import com.tilioteo.hypothesis.shared.ui.video.VideoServerRpc;
import com.tilioteo.hypothesis.shared.ui.video.VideoState;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.ClickEventHandler;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(com.tilioteo.hypothesis.ui.Video.class)
public class VideoConnector extends com.vaadin.client.ui.video.VideoConnector implements CanPlayThroughHandler, PlayHandler, EndedHandler, PauseHandler {

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
    public VideoState getState() {
        return (VideoState) super.getState();
    }

    @Override
    public VVideo getWidget() {
        return (VVideo) super.getWidget();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        clickEventHandler.handleEventHandlerRegistration();
        
    }

    protected final ClickEventHandler clickEventHandler = new ClickEventHandler(this) {
        @Override
        protected void fireClick(NativeEvent event, MouseEventDetails mouseDetails) {
            getRpcProxy(VideoServerRpc.class).click(mouseDetails, getWidget().getCurrentTime());
        }
    };

	@Override
	public void onCanPlayThrough(CanPlayThroughEvent event) {
		getRpcProxy(VideoServerRpc.class).canPlayThrough();
	}

	@Override
	public void onEnded(EndedEvent event) {
		getRpcProxy(VideoServerRpc.class).stop(getWidget().getCurrentTime(), false);
	}

	@Override
	public void onPause(PauseEvent event) {
		getRpcProxy(VideoServerRpc.class).stop(getWidget().getCurrentTime(), true);
	}

	@Override
	public void onPlay(PlayEvent event) {
		getRpcProxy(VideoServerRpc.class).start(started ? getWidget().getCurrentTime() : 0.0, started);
		
		if (!started) {
			started = true;
		}
	}

}
