/**
 * 
 */
package org.vaadin.maps.client.ui.gridlayout;

import org.vaadin.maps.client.ui.AbstractLayoutConnector;
import org.vaadin.maps.client.ui.VGridLayout;
import org.vaadin.maps.shared.ui.gridlayout.GridLayoutState;
import org.vaadin.maps.ui.GridLayout;

import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.shared.ui.Connect;

/**
 * @author morong
 *
 */
@SuppressWarnings("serial")
@Connect(GridLayout.class)
public class GridLayoutConnector extends AbstractLayoutConnector {

	@Override
	public void updateCaption(ComponentConnector connector) {
		// nop
		
	}

	@Override
	public void onConnectorHierarchyChange(
			ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {
		// TODO Auto-generated method stub
		
	}

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.ui.AbstractComponentConnector#getWidget()
     */
    @Override
    public VGridLayout getWidget() {
        return (VGridLayout) super.getWidget();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.ui.AbstractComponentConnector#getState()
     */
    @Override
    public GridLayoutState getState() {
        return (GridLayoutState) super.getState();
    }

}
