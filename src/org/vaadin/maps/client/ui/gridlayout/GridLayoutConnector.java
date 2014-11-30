/**
 * 
 */
package org.vaadin.maps.client.ui.gridlayout;

import org.vaadin.maps.client.ui.AbstractLayoutConnector;
import org.vaadin.maps.client.ui.Tile;
import org.vaadin.maps.client.ui.Tile.SizeChangeHandler;
import org.vaadin.maps.client.ui.VGridLayout;
import org.vaadin.maps.client.ui.tile.ImageTileConnector;
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
	
	private SizeChangeHandler sizeChangeHandler = new SizeChangeHandler() {
		@Override
		public void onSizeChange(Tile tile, int oldWidth, int oldHeight, int newWidth, int newHeight) {
			VGridLayout gridLayout = getWidget();
			
			if (gridLayout.getWidgetCount() == 1) {
				// center tile
				int dx = (getWidget().getMeasuredWidth() - newWidth) / 2;
				int dy = (getWidget().getMeasuredHeight() - newHeight) / 2;
				gridLayout.setWidgetPosition(tile.asWidget(), dx, dy);
			} else {
				
			}
		}
	};
	
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
            	if (child instanceof ImageTileConnector) {
            		((ImageTileConnector)child).setSizeChangeHandler(sizeChangeHandler);
            	}
                getWidget().add(child.getWidget());
                //child.addStateChangeHandler(childStateChangeHandler);
                setChildWidgetPosition(child);
            }
        }
        for (ComponentConnector oldChild : event.getOldChildren()) {
            if (oldChild.getParent() != this) {
                getWidget().remove(oldChild.getWidget());
                //oldChild.removeStateChangeHandler(childStateChangeHandler);
            	if (oldChild instanceof ImageTileConnector) {
            		((ImageTileConnector)oldChild).setSizeChangeHandler(null);
            	}
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
