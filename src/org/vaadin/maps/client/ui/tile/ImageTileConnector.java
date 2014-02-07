/**
 * 
 */
package org.vaadin.maps.client.ui.tile;

import org.vaadin.maps.client.ui.VImageTile;
import org.vaadin.maps.shared.ui.tile.ImageTileServerRpc;
import org.vaadin.maps.shared.ui.tile.ImageTileState;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.vaadin.client.MouseEventDetailsBuilder;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.ClickEventHandler;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.AbstractEmbeddedState;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(org.vaadin.maps.ui.tile.ImageTile.class)
public class ImageTileConnector extends AbstractComponentConnector {
    
	
	@Override
    protected void init() {
        super.init();
        
        getWidget().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				MouseEventDetails mouseDetails = MouseEventDetailsBuilder.buildMouseEventDetails(event.getNativeEvent(), getWidget().getElement());
				getRpcProxy(ImageTileServerRpc.class).click(mouseDetails);
			}
		});
        
        getWidget().addHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                getLayoutManager().setNeedsMeasure(ImageTileConnector.this);
                getRpcProxy(ImageTileServerRpc.class).load();
            }
        }, LoadEvent.getType());
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

        clickEventHandler.handleEventHandlerRegistration();

        String url = getResourceUrl(AbstractEmbeddedState.SOURCE_RESOURCE);
        getWidget().setUrl(url != null ? url : "");

        String alt = null;//getState().alternateText;
        // Some browsers turn a null alt text into a literal "null"
        getWidget().setAltText(alt != null ? alt : "");
    }

    protected final ClickEventHandler clickEventHandler = new ClickEventHandler(
            this) {
        @Override
        protected void fireClick(NativeEvent event,
                MouseEventDetails mouseDetails) {
            getRpcProxy(ImageTileServerRpc.class).click(mouseDetails);
        }

    };
}
