/**
 * 
 */
package org.vaadin.maps.client.ui.featurecontainer;

import java.util.List;

import org.vaadin.gwtgraphics.client.AbstractDrawing;
import org.vaadin.maps.client.ui.VVectorFeatureContainer;
import org.vaadin.maps.shared.ui.featurecontainer.VectorFeatureContainerServerRpc;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.MouseEventDetailsBuilder;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(org.vaadin.maps.ui.featurecontainer.VectorFeatureContainer.class)
public class VectorFeatureContainerConnector extends
		AbstractFeatureContainerConnector {
	
	@Override
	protected void init() {
		super.init();
		getWidget().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				MouseEventDetails mouseDetails = MouseEventDetailsBuilder.buildMouseEventDetails(event.getNativeEvent(), getWidget().getElement());
				getRpcProxy(VectorFeatureContainerServerRpc.class).click(mouseDetails);
			}
		});
	}
	
	@Override
	public VVectorFeatureContainer getWidget() {
		return (VVectorFeatureContainer) super.getWidget();
	}
	
	/* (non-Javadoc)
	 * @see com.vaadin.client.ConnectorHierarchyChangeEvent.ConnectorHierarchyChangeHandler#onConnectorHierarchyChange(com.vaadin.client.ConnectorHierarchyChangeEvent)
	 */
	@Override
	public void onConnectorHierarchyChange(
			ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {
		List<ComponentConnector> previousChildren = connectorHierarchyChangeEvent.getOldChildren();
		List<ComponentConnector> children = getChildComponents();
		VVectorFeatureContainer container = getWidget();

		for (ComponentConnector previousChild : previousChildren) {
			if (!children.contains(previousChild)) {
				container.remove((AbstractDrawing) previousChild.getWidget());
			}
		}
		
		for (ComponentConnector child : children) {
			if (!previousChildren.contains(child)) {
				container.add((AbstractDrawing) child.getWidget());
			}
		}
	}

}
