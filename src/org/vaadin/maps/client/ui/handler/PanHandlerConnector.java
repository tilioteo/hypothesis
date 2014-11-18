/**
 * 
 */
package org.vaadin.maps.client.ui.handler;

import org.vaadin.maps.client.ui.VPanHandler;
import org.vaadin.maps.client.ui.layerlayout.LayerLayoutConnector;
import org.vaadin.maps.shared.ui.handler.PanHandlerState;

import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.shared.Connector;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(org.vaadin.maps.ui.handler.PanHandler.class)
public class PanHandlerConnector extends AbstractHandlerConnector {

	@Override
	protected void init() {
		super.init();
	}

	@Override
	public VPanHandler getWidget() {
		return (VPanHandler) super.getWidget();
	}

	@Override
	public PanHandlerState getState() {
		return (PanHandlerState) super.getState();
	}

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);
		
		if (stateChangeEvent.hasPropertyChanged("layout")) {
			Connector connector = getState().layout;
			if (connector != null) {
				if (connector instanceof LayerLayoutConnector)
					getWidget().setLayout(((LayerLayoutConnector)connector).getWidget());
			} else
				getWidget().setLayout(null);
		}
	}


}
