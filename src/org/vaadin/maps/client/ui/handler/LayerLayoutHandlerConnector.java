/**
 * 
 */
package org.vaadin.maps.client.ui.handler;

import org.vaadin.maps.client.ui.HasLayerLayout;
import org.vaadin.maps.client.ui.layerlayout.LayerLayoutConnector;
import org.vaadin.maps.shared.ui.handler.LayerLayoutHandlerState;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.shared.Connector;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class LayerLayoutHandlerConnector extends AbstractHandlerConnector {

	@Override
	public LayerLayoutHandlerState getState() {
		return (LayerLayoutHandlerState) super.getState();
	}

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);
		
		if (stateChangeEvent.hasPropertyChanged("layout")) {
			Widget widget = getWidget();
			if (widget instanceof HasLayerLayout) {
				HasLayerLayout hasLayerLayout = (HasLayerLayout) widget;
			
				Connector connector = getState().layout;
				if (connector != null) {
					if (connector instanceof LayerLayoutConnector) {
						hasLayerLayout.setLayout(((LayerLayoutConnector)connector).getWidget());
					}
				} else {
					hasLayerLayout.setLayout(null);
				}
			}
		}
	}

}
