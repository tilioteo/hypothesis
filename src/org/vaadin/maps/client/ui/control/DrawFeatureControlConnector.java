/**
 * 
 */
package org.vaadin.maps.client.ui.control;

import org.vaadin.maps.client.ui.MapUtility;
import org.vaadin.maps.client.ui.VDrawFeatureControl;
import org.vaadin.maps.client.ui.layer.VectorFeatureLayerConnector;
import org.vaadin.maps.shared.ui.control.DrawFeatureControlState;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.shared.Connector;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class DrawFeatureControlConnector extends AbstractControlConnector {

	@Override
	protected void init() {
		super.init();
		
	}

	@Override
	public VDrawFeatureControl getWidget() {
		return (VDrawFeatureControl) super.getWidget();
	}

	@Override
	public DrawFeatureControlState getState() {
		return (DrawFeatureControlState) super.getState();
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
		if (stateChangeEvent.hasPropertyChanged("cursorStyle")) {
			getWidget().setCursorStyle(MapUtility.getStyleFromMap(getState().cursorStyle, null));
		}
	}

	@Override
	public void onConnectorHierarchyChange(
			ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {
        // We always have 1 child, unless the child is hidden
        Widget content = getContentWidget();
        if (content != null) {
        	getWidget().setWidget(content);
        }
	}

}
