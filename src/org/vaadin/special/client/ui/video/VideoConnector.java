/**
 * 
 */
package org.vaadin.special.client.ui.video;

import org.vaadin.special.client.DateUtility;
import org.vaadin.special.client.MediaEvents.PauseEvent;
import org.vaadin.special.client.MediaEvents.PauseHandler;
import org.vaadin.special.client.MediaEvents.PlayEvent;
import org.vaadin.special.client.MediaEvents.PlayHandler;
import org.vaadin.special.client.ui.VVideo;
import org.vaadin.special.shared.ui.video.VideoServerRpc;
import org.vaadin.special.shared.ui.video.VideoState;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.CanPlayThroughEvent;
import com.google.gwt.event.dom.client.CanPlayThroughHandler;
import com.google.gwt.event.dom.client.EndedEvent;
import com.google.gwt.event.dom.client.EndedHandler;
import com.vaadin.client.Util;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.ClickEventHandler;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.URLReference;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.MediaControl;
import com.vaadin.shared.ui.video.VideoConstants;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(org.vaadin.special.ui.Video.class)
public class VideoConnector extends AbstractComponentConnector implements CanPlayThroughHandler, PlayHandler, EndedHandler, PauseHandler {

    private boolean started = false;
    
	@Override
    protected void init() {
        super.init();
        
        super.init();

        registerRpc(MediaControl.class, new MediaControl() {
            @Override
            public void play() {
                getWidget().play();
            }

            @Override
            public void pause() {
                getWidget().pause();
            }
        });

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
    public void onStateChanged(StateChangeEvent event) {
        super.onStateChanged(event);

        final VVideo widget = getWidget();
        final VideoState state = getState();

        setAltText(state.altText); // must do before loading sources
        widget.setAutoplay(state.autoplay);
        widget.setMuted(state.muted);
        widget.setControls(state.showControls);

        if (event.hasPropertyChanged("sources")) {
            widget.removeAllSources();
            for (int i = 0; i < state.sources.size(); i++) {
                URLReference source = state.sources.get(i);
                String sourceType = state.sourceTypes.get(i);
                widget.addSource(source.getURL(), sourceType);
            }
            widget.load();
        }
        
        getWidget().setPoster(getResourceUrl(VideoConstants.POSTER_RESOURCE));
        
        clickEventHandler.handleEventHandlerRegistration();
    }

    private void setAltText(String altText) {

        if (altText == null || "".equals(altText)) {
            altText = getDefaultAltHtml();
        } else if (!getState().htmlContentAllowed) {
            altText = Util.escapeHTML(altText);
        }
        getWidget().setAltText(altText);
    }

    protected String getDefaultAltHtml() {
        return "Your browser does not support the <code>video</code> element.";
    }

    protected final ClickEventHandler clickEventHandler = new ClickEventHandler(this) {
        @Override
        protected void fireClick(NativeEvent event, MouseEventDetails mouseDetails) {
            getRpcProxy(VideoServerRpc.class).click(DateUtility.getTimestamp(), mouseDetails, getWidget().getCurrentTime());
        }
    };

	@Override
	public void onCanPlayThrough(CanPlayThroughEvent event) {
		getRpcProxy(VideoServerRpc.class).canPlayThrough(DateUtility.getTimestamp());
	}

	@Override
	public void onEnded(EndedEvent event) {
		getRpcProxy(VideoServerRpc.class).stop(DateUtility.getTimestamp(), getWidget().getCurrentTime(), false);
	}

	@Override
	public void onPause(PauseEvent event) {
		getRpcProxy(VideoServerRpc.class).stop(DateUtility.getTimestamp(), getWidget().getCurrentTime(), true);
	}

	@Override
	public void onPlay(PlayEvent event) {
		getRpcProxy(VideoServerRpc.class).start(DateUtility.getTimestamp(), started ? getWidget().getCurrentTime() : 0.0, started);
		
		if (!started) {
			started = true;
		}
	}

}
