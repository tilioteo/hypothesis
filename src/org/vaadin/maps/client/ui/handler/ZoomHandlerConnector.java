/**
 * 
 */
package org.vaadin.maps.client.ui.handler;

import org.vaadin.maps.client.ui.VZoomHandler;
import org.vaadin.maps.client.ui.VZoomHandler.ZoomEvent;
import org.vaadin.maps.client.ui.VZoomHandler.ZoomEventHandler;
import org.vaadin.maps.shared.ui.handler.ZoomHandlerRpc;
import org.vaadin.maps.shared.ui.handler.ZoomHandlerState;

import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(org.vaadin.maps.ui.handler.ZoomHandler.class)
public class ZoomHandlerConnector extends LayerLayoutHandlerConnector implements ZoomEventHandler {

	@Override
	protected void init() {
		super.init();
		
		getWidget().addZoomEventHandler(this);
	}

	@Override
	public VZoomHandler getWidget() {
		return (VZoomHandler) super.getWidget();
	}

	@Override
	public ZoomHandlerState getState() {
		return (ZoomHandlerState) super.getState();
	}

	@Override
	public void zoom(ZoomEvent event) {
		getRpcProxy(ZoomHandlerRpc.class).zoomChange(event.getZoom());
	}

}
