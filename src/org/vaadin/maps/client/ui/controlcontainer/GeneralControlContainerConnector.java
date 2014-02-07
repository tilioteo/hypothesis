/**
 * 
 */
package org.vaadin.maps.client.ui.controlcontainer;

import java.util.List;

import org.vaadin.maps.client.ui.AbstractControl;
import org.vaadin.maps.client.ui.VGeneralControlContainer;

import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(org.vaadin.maps.ui.controlcontainer.GeneralControlContainer.class)
public class GeneralControlContainerConnector extends
		AbstractControlContainerConnector {

	@Override
	public VGeneralControlContainer getWidget() {
		return (VGeneralControlContainer) super.getWidget();
	}
	
	/* (non-Javadoc)
	 * @see com.vaadin.client.ConnectorHierarchyChangeEvent.ConnectorHierarchyChangeHandler#onConnectorHierarchyChange(com.vaadin.client.ConnectorHierarchyChangeEvent)
	 */
	@Override
	public void onConnectorHierarchyChange(
			ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {
		List<ComponentConnector> previousChildren = connectorHierarchyChangeEvent.getOldChildren();
		List<ComponentConnector> children = getChildComponents();
		VGeneralControlContainer container = getWidget();

		for (ComponentConnector previousChild : previousChildren) {
			if (!children.contains(previousChild)) {
				container.remove((AbstractControl) previousChild.getWidget());
			}
		}
		
		for (ComponentConnector child : children) {
			if (!previousChildren.contains(child)) {
				container.add((AbstractControl) child.getWidget());
			}
		}
	}

}
