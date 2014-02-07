/**
 * 
 */
package org.vaadin.maps.client.ui.layer;

import org.vaadin.maps.client.ui.AbstractLayerConnector;
import org.vaadin.maps.client.ui.VImageLayer;
import org.vaadin.maps.shared.ui.layer.ImageLayerState;

import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.shared.ui.Connect;

/**
 * @author morong
 *
 */
@SuppressWarnings("serial")
@Connect(org.vaadin.maps.ui.layer.ImageLayer.class)
public class ImageLayerConnector extends AbstractLayerConnector {

	@Override
	protected void init() {
		super.init();
	}
	
	@Override
	public VImageLayer getWidget() {
		return (VImageLayer) super.getWidget();
	}

	@Override
	public ImageLayerState getState() {
		return (ImageLayerState) super.getState();
	}

	@Override
	public void onConnectorHierarchyChange(
			ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {
        // We always have 1 child, unless the child is hidden
        getWidget().setWidget(getContentWidget());
	}

}
