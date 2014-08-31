/**
 * 
 */
package org.vaadin.maps.client.ui.control;

import org.vaadin.maps.client.ui.MapUtility;
import org.vaadin.maps.client.ui.VDrawPathControl;
import org.vaadin.maps.shared.ui.control.DrawPathControlState;

import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(org.vaadin.maps.ui.control.DrawPathControl.class)
public class DrawPathControlConnector extends DrawFeatureControlConnector {

	@Override
	public VDrawPathControl getWidget() {
		return (VDrawPathControl) super.getWidget();
	}

	@Override
	public DrawPathControlState getState() {
		return (DrawPathControlState) super.getState();
	}

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);
		
		if (stateChangeEvent.hasPropertyChanged("startPointStyle")) {
			getWidget().setStartPointStyle(MapUtility.getStyleFromMap(getState().startPointStyle, null));
		}
		if (stateChangeEvent.hasPropertyChanged("lineStyle")) {
			getWidget().setLineStyle(MapUtility.getStyleFromMap(getState().lineStyle, null));
		}
		if (stateChangeEvent.hasPropertyChanged("vertexStyle")) {
			getWidget().setVertexStyle(MapUtility.getStyleFromMap(getState().vertexStyle, null));
		}
	}
		
}
