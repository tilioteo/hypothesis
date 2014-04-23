/**
 * 
 */
package org.vaadin.maps.client.ui.handler;

import org.vaadin.maps.client.drawing.Utils;
import org.vaadin.maps.client.geometry.Coordinate;
import org.vaadin.maps.client.ui.VPointHandler;
import org.vaadin.maps.client.ui.VPointHandler.GeometryEvent;
import org.vaadin.maps.client.ui.VPointHandler.GeometryEventHandler;
import org.vaadin.maps.client.ui.layer.VectorFeatureLayerConnector;
import org.vaadin.maps.shared.ui.handler.PointHandlerServerRpc;
import org.vaadin.maps.shared.ui.handler.PointHandlerState;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.vaadin.client.MouseEventDetailsBuilder;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.shared.Connector;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(org.vaadin.maps.ui.handler.PointHandler.class)
public class PointHandlerConnector extends AbstractHandlerConnector implements ClickHandler, GeometryEventHandler {

	@Override
	protected void init() {
		super.init();
		
		getWidget().clickHandlerSlave = this;
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

	@Override
	public void onClick(ClickEvent event) {
		Coordinate coordinate = getWidget().createWorldCoordinate(VPointHandler.getMouseEventXY(event));
		MouseEventDetails mouseDetails = MouseEventDetailsBuilder.buildMouseEventDetails(event.getNativeEvent(), getWidget().getElement());
		getRpcProxy(PointHandlerServerRpc.class).click(coordinate.x, coordinate.y,
				mouseDetails.getButtonName(), mouseDetails.isAltKey(), mouseDetails.isCtrlKey(),
				mouseDetails.isMetaKey(), mouseDetails.isShiftKey(), mouseDetails.isDoubleClick());
	}

	@Override
	public void geometry(GeometryEvent event) {
		getRpcProxy(PointHandlerServerRpc.class).geometry(Utils.GeometryToWKBHex(event.getGeometry()));
	}

}
