/**
 * 
 */
package org.vaadin.maps.client.ui.layer;

import org.vaadin.maps.client.ui.AbstractLayerConnector;
import org.vaadin.maps.client.ui.VControlLayer;
import org.vaadin.maps.shared.ui.layer.ControlLayerState;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(org.vaadin.maps.ui.layer.ControlLayer.class)
public class ControlLayerConnector extends AbstractLayerConnector {

	@Override
	public VControlLayer getWidget() {
		return (VControlLayer) super.getWidget();
	}

	@Override
	public ControlLayerState getState() {
		return (ControlLayerState) super.getState();
	}

	/* (non-Javadoc)
	 * @see com.vaadin.client.ConnectorHierarchyChangeEvent.ConnectorHierarchyChangeHandler#onConnectorHierarchyChange(com.vaadin.client.ConnectorHierarchyChangeEvent)
	 */
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
