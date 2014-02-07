/**
 * 
 */
package org.vaadin.maps.client.ui.handler;

import org.vaadin.maps.client.geometry.Utils;
import org.vaadin.maps.client.ui.VPointHandler;
import org.vaadin.maps.client.ui.VPointHandler.GeometryEvent;
import org.vaadin.maps.client.ui.VPointHandler.GeometryEventHandler;
import org.vaadin.maps.client.ui.layer.VectorFeatureLayerConnector;
import org.vaadin.maps.shared.ui.handler.PointHandlerServerRpc;
import org.vaadin.maps.shared.ui.handler.PointHandlerState;

import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.shared.Connector;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(org.vaadin.maps.ui.handler.PointHandler.class)
public class PointHandlerConnector extends AbstractHandlerConnector implements GeometryEventHandler {

	@Override
	protected void init() {
		super.init();
		
		getWidget().addGeometryEventHandler(this);
	}

	@Override
	public VPointHandler getWidget() {
		return (VPointHandler) super.getWidget();
	}

	@Override
	public PointHandlerState getState() {
		return (PointHandlerState) super.getState();
	}

	@Override
	public void geometry(GeometryEvent event) {
		getRpcProxy(PointHandlerServerRpc.class).geometry(Utils.GeometryToWKBHex(event.getGeometry()));
	}

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);
		
		if (stateChangeEvent.hasPropertyChanged("layer")) {
			Connector connector = getState().layer;
			if (connector != null) {
				if (connector instanceof VectorFeatureLayerConnector)
					getWidget().setLayer(((VectorFeatureLayerConnector)connector).getWidget());
			} else
				getWidget().setLayer(null);
		}
	}

}