/**
 * 
 */
package org.vaadin.maps.client.ui.tile;

import org.vaadin.maps.client.ui.Tile.SizeChangeHandler;
import org.vaadin.maps.client.ui.Tile.TileLoadHandler;
import org.vaadin.maps.client.ui.VImageTile;
import org.vaadin.maps.shared.ui.tile.ImageTileState;
import org.vaadin.maps.shared.ui.tile.ProxyTileServerRpc;

import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.shared.ui.AbstractEmbeddedState;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(org.vaadin.maps.ui.tile.ImageTile.class)
public class ImageTileConnector extends ProxyTileConnector implements LoadHandler, ErrorHandler {
	
	private SizeChangeHandler sizeChangeHandler;
	private TileLoadHandler loadHandler;
	int width = 0;
	int height = 0;
    
	@Override
    protected void init() {
        super.init();
        
        getWidget().addHandler(this, LoadEvent.getType());
        getWidget().addHandler(this, ErrorEvent.getType());
    }

    @Override
    public VImageTile getWidget() {
        return (VImageTile) super.getWidget();
    }

    @Override
    public ImageTileState getState() {
        return (ImageTileState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        getWidget().setUrl(getResourceUrl(AbstractEmbeddedState.SOURCE_RESOURCE));
    }

	@Override
	public void onError(ErrorEvent event) {
        getWidget().setVisible(true);
        getRpcProxy(ProxyTileServerRpc.class).error();
	}

	public void setSizeChangeHandler(SizeChangeHandler sizeChangeHandler) {
		this.sizeChangeHandler = sizeChangeHandler;
	}

	public void setTileLoadHandler(TileLoadHandler loadHandler) {
		this.loadHandler = loadHandler;
	}

	@Override
	public void onLoad(LoadEvent event) {
        getLayoutManager().setNeedsMeasure(ImageTileConnector.this);
        VImageTile widget = getWidget();
        if (loadHandler != null) {
        	loadHandler.onLoad(getWidget());
        }
        
        int newWidth = widget.getWidth();
        int newHeight = widget.getHeight();
        if (sizeChangeHandler != null) {
        	sizeChangeHandler.onSizeChange(getWidget(), width, height, newWidth, newHeight);
        }
        
        getWidget().setVisible(true);
        getRpcProxy(ProxyTileServerRpc.class).updateClippedSize(newWidth, newHeight);
        getRpcProxy(ProxyTileServerRpc.class).load();
	}
	
}
