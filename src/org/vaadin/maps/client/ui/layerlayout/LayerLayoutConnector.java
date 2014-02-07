/**
 * 
 */
package org.vaadin.maps.client.ui.layerlayout;

import java.util.List;

import org.vaadin.maps.client.ui.AbstractLayoutConnector;
import org.vaadin.maps.client.ui.VLayerLayout;
import org.vaadin.maps.shared.ui.layerlayout.LayerLayoutServerRpc;
import org.vaadin.maps.shared.ui.layerlayout.LayerLayoutState;
import org.vaadin.maps.ui.LayerLayout;

import com.google.gwt.user.client.Element;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.Util;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.communication.StateChangeEvent.StateChangeHandler;
import com.vaadin.client.ui.LayoutClickEventHandler;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.LayoutClickRpc;

/**
 * @author morong
 * 
 */
@SuppressWarnings("serial")
@Connect(LayerLayout.class)
public class LayerLayoutConnector extends AbstractLayoutConnector {

    private LayoutClickEventHandler clickEventHandler = new LayoutClickEventHandler(
            this) {

        @Override
        protected ComponentConnector getChildComponent(Element element) {
            return getConnectorForElement(element);
        }

        @Override
        protected LayoutClickRpc getLayoutClickRPC() {
            return getRpcProxy(LayerLayoutServerRpc.class);
        };
    };

    private StateChangeHandler childStateChangeHandler = new StateChangeHandler() {
        @Override
        public void onStateChanged(StateChangeEvent stateChangeEvent) {
            ComponentConnector child = (ComponentConnector) stateChangeEvent.getConnector();
            List<String> childStyles = child.getState().styles;
            if (childStyles == null) {
                getWidget().setWidgetWrapperStyleNames(child.getWidget(), (String[]) null);
            } else {
                getWidget().setWidgetWrapperStyleNames(child.getWidget(),
                        childStyles.toArray(new String[childStyles.size()]));
            }
        }
    };

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.ui.AbstractComponentConnector#init()
     */
    @Override
    protected void init() {
        super.init();
    }

    /**
     * Returns the deepest nested child component which contains "element". The
     * child component is also returned if "element" is part of its caption.
     * 
     * @param element
     *            An element that is a nested sub element of the root element in
     *            this layout
     * @return The Paintable which the element is a part of. Null if the element
     *         belongs to the layout and not to a child.
     */
    protected ComponentConnector getConnectorForElement(Element element) {
        return Util.getConnectorForElement(getConnection(), getWidget(),
                element);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.HasComponentsConnector#updateCaption(com.vaadin
     * .client.ComponentConnector)
     */
    @Override
    public void updateCaption(ComponentConnector component) {
    	// nop
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.ui.AbstractComponentConnector#getWidget()
     */
    @Override
    public VLayerLayout getWidget() {
        return (VLayerLayout) super.getWidget();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.ui.AbstractComponentConnector#getState()
     */
    @Override
    public LayerLayoutState getState() {
        return (LayerLayoutState) super.getState();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.client.ui.AbstractComponentConnector#onStateChanged(com.vaadin
     * .client.communication.StateChangeEvent)
     */
    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        clickEventHandler.handleEventHandlerRegistration();

        for (ComponentConnector child : getChildComponents()) {
            setChildWidgetPosition(child);
        }
    }

    private void setChildWidgetPosition(ComponentConnector child) {
        getWidget().setWidgetOrder(child.getWidget(),
                getState().connectorToCssPosition.get(child.getConnectorId()));
    };

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.ui.AbstractComponentContainerConnector#
     * onConnectorHierarchyChange
     * (com.vaadin.client.ConnectorHierarchyChangeEvent)
     */
    @Override
    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
        for (ComponentConnector child : getChildComponents()) {
            if (!getWidget().contains(child.getWidget())) {
                getWidget().add(child.getWidget());
                child.addStateChangeHandler(childStateChangeHandler);
                setChildWidgetPosition(child);
            }
        }
        for (ComponentConnector oldChild : event.getOldChildren()) {
            if (oldChild.getParent() != this) {
                getWidget().remove(oldChild.getWidget());
                oldChild.removeStateChangeHandler(childStateChangeHandler);
            }
        }

        getWidget().cleanupWrappers();
    }

}
