/**
 * 
 */
package org.vaadin.maps.client.ui.tile;

import java.util.LinkedList;

import org.vaadin.maps.client.ui.VImageSequenceTile;
import org.vaadin.maps.client.ui.VImageSequenceTile.SequenceErrorEvent;
import org.vaadin.maps.client.ui.VImageSequenceTile.SequenceErrorHandler;
import org.vaadin.maps.client.ui.VImageSequenceTile.SequenceLoadedEvent;
import org.vaadin.maps.client.ui.VImageSequenceTile.SequenceLoadedHandler;
import org.vaadin.maps.shared.ui.tile.ImageSequenceTileServerRpc;
import org.vaadin.maps.shared.ui.tile.ImageSequenceTileState;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.ClickEventHandler;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.URLReference;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(org.vaadin.maps.ui.tile.ImageSequenceTile.class)
public class ImageSequenceTileConnector extends AbstractComponentConnector implements SequenceLoadedHandler, SequenceErrorHandler, LoadHandler {

	@Override
    protected void init() {
        super.init();
        
        getWidget().addSequenceLoadedHandler(this);
        getWidget().addSequenceErrorHandler(this);
        getWidget().addHandler(this, LoadEvent.getType());
    }

    protected final ClickEventHandler clickEventHandler = new ClickEventHandler(
            this) {
        @Override
        protected void fireClick(NativeEvent event,
                MouseEventDetails mouseDetails) {
            getRpcProxy(ImageSequenceTileServerRpc.class).click(mouseDetails, getWidget().getIndex());
        }

    };
	
	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);

        clickEventHandler.handleEventHandlerRegistration();
        
        if (stateChangeEvent.hasPropertyChanged("sources")) {
        	LinkedList<String> urls = new LinkedList<String>();
        	for (URLReference urlRef : getState().sources) {
        		if (urlRef != null && urlRef.getURL() != null) {
        			urls.add(urlRef.getURL());
        		}
        	}
        	getWidget().setUrls(urls);
        }
        
        if (stateChangeEvent.hasPropertyChanged("index")) {
        	getWidget().setIndex(getState().index);
        }
	}

	@Override
	public VImageSequenceTile getWidget() {
		return (VImageSequenceTile) super.getWidget();
	}

	@Override
	public ImageSequenceTileState getState() {
		return (ImageSequenceTileState) super.getState();
	}

	@Override
	public void onLoad(LoadEvent event) {
        getLayoutManager().setNeedsMeasure(ImageSequenceTileConnector.this);
        getRpcProxy(ImageSequenceTileServerRpc.class).changed(getWidget().getIndex());
	}

	@Override
	public void error(SequenceErrorEvent event) {
        getRpcProxy(ImageSequenceTileServerRpc.class).error();
	}

	@Override
	public void loaded(SequenceLoadedEvent event) {
        getRpcProxy(ImageSequenceTileServerRpc.class).load();
	}
	
}
