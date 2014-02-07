/**
 * 
 */
package org.vaadin.maps.shared.ui.control;

import com.vaadin.shared.communication.ClientRpc;

/**
 * @author kamil
 *
 */
public interface ControlClientRpc extends ClientRpc {
	
	public void activate();
	public void deactivate();

}
