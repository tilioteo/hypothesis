/**
 * 
 */
package org.vaadin.maps.client.ui.featurecontainer;

import org.vaadin.maps.shared.ui.featurecontainer.AbstractFeatureContainerState;

import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ui.AbstractComponentContainerConnector;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractFeatureContainerConnector extends
		AbstractComponentContainerConnector {

	@Override
	public AbstractFeatureContainerState getState() {
		// TODO Auto-generated method stub
		return (AbstractFeatureContainerState) super.getState();
	}

	/* (non-Javadoc)
	 * @see com.vaadin.client.HasComponentsConnector#updateCaption(com.vaadin.client.ComponentConnector)
	 */
	@Override
	public void updateCaption(ComponentConnector connector) {
		// nop
	}

}
