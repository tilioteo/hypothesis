/**
 * 
 */
package org.vaadin.maps.client.ui.controlcontainer;

import org.vaadin.maps.shared.ui.controlcontainer.AbstractControlContainerState;

import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ui.AbstractComponentContainerConnector;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractControlContainerConnector extends
		AbstractComponentContainerConnector {


	@Override
	public AbstractControlContainerState getState() {
		// TODO Auto-generated method stub
		return (AbstractControlContainerState) super.getState();
	}

	/* (non-Javadoc)
	 * @see com.vaadin.client.HasComponentsConnector#updateCaption(com.vaadin.client.ComponentConnector)
	 */
	@Override
	public void updateCaption(ComponentConnector connector) {
		// nop
	}

}
