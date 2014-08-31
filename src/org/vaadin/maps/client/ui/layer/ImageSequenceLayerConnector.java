/**
 * 
 */
package org.vaadin.maps.client.ui.layer;

import org.vaadin.maps.client.ui.AbstractLayerConnector;
import org.vaadin.maps.client.ui.VImageSequenceLayer;
import org.vaadin.maps.shared.ui.layer.ImageSequenceLayerState;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(org.vaadin.maps.ui.layer.ImageSequenceLayer.class)
public class ImageSequenceLayerConnector extends AbstractLayerConnector {

	@Override
	protected void init() {
		super.init();
	}
	
	@Override
	public VImageSequenceLayer getWidget() {
		return (VImageSequenceLayer) super.getWidget();
	}

	@Override
	public ImageSequenceLayerState getState() {
		return (ImageSequenceLayerState) super.getState();
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
