/**
 * 
 */
package org.vaadin.maps.client.ui.gridlayout;

import org.vaadin.maps.client.ui.AbstractLayoutConnector;
import org.vaadin.maps.client.ui.VGridLayout;
import org.vaadin.maps.shared.ui.gridlayout.GridLayoutState;
import org.vaadin.maps.shared.ui.gridlayout.GridLayoutState.ChildComponentData;
import org.vaadin.maps.ui.GridLayout;

import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.communication.StateChangeEvent;
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
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        for (ComponentConnector child : getChildComponents()) {
            setChildWidgetPosition(child);
        }
    }

    private void setChildWidgetPosition(ComponentConnector child) {
    	ChildComponentData childComponentData = getState().childData.get(child);
        getWidget().setWidgetPosition(child.getWidget(), childComponentData.left, childComponentData.top);
    };

	@Override
	public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
        for (ComponentConnector child : getChildComponents()) {
            if (!getWidget().contains(child.getWidget())) {
                getWidget().add(child.getWidget());
                //child.addStateChangeHandler(childStateChangeHandler);
                setChildWidgetPosition(child);
            }
        }
        for (ComponentConnector oldChild : event.getOldChildren()) {
            if (oldChild.getParent() != this) {
                getWidget().remove(oldChild.getWidget());
                //oldChild.removeStateChangeHandler(childStateChangeHandler);
            }
        }

        getWidget().cleanupWrappers();
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
