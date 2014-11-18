/**
 * 
 */
package org.vaadin.maps.client.ui.control;

import org.vaadin.maps.client.ui.VNavigateControl;
import org.vaadin.maps.client.ui.layerlayout.LayerLayoutConnector;
import org.vaadin.maps.shared.ui.control.NavigateControlState;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.shared.Connector;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class NavigateControlConnector extends AbstractControlConnector {

	@Override
	protected void init() {
		super.init();
		
	}

	@Override
	public VNavigateControl getWidget() {
		return (VNavigateControl) super.getWidget();
	}

	@Override
	public NavigateControlState getState() {
		return (NavigateControlState) super.getState();
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

	@Override
	public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {
        // We always have 1 child, unless the child is hidden
        Widget content = getContentWidget();
        if (content != null) {
        	getWidget().setWidget(content);
        }
	}

}
