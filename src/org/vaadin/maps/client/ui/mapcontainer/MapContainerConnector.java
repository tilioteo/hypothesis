/**
 * 
 */
package org.vaadin.maps.client.ui.mapcontainer;

import org.vaadin.maps.client.ui.VMapContainer;
import org.vaadin.maps.client.ui.VMapContainer.PanEvent;
import org.vaadin.maps.client.ui.VMapContainer.PanEventHandler;
import org.vaadin.maps.client.ui.VMapContainer.ZoomEvent;
import org.vaadin.maps.client.ui.VMapContainer.ZoomEventHandler;
import org.vaadin.maps.client.ui.layerlayout.LayerLayoutConnector;
import org.vaadin.maps.shared.ui.mapcontainer.MapContainerRpc;

import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(org.vaadin.maps.ui.MapContainer.class)
public class MapContainerConnector extends LayerLayoutConnector implements PanEventHandler, ZoomEventHandler {

	@Override
	public VMapContainer getWidget() {
		return (VMapContainer)super.getWidget();
	}

	@Override
	protected void init() {
		super.init();
		
		getWidget().addPanEventHandler(this);
		getWidget().addZoomEventHandler(this);
	}

	@Override
	public void onPanEnd(PanEvent event) {
		getRpcProxy(MapContainerRpc.class).panEnd(event.getShiftX(), event.getShiftY());
	}

	@Override
	public void onZoom(ZoomEvent event) {
		getRpcProxy(MapContainerRpc.class).zoom(event.getZoom());
	}

}
