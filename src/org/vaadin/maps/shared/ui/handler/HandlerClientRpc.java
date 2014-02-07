/**
 * 
 */
package org.vaadin.maps.shared.ui.handler;

import com.vaadin.shared.communication.ClientRpc;

/**
 * @author kamil
 *
 */
public interface HandlerClientRpc extends ClientRpc {

	public void cancel();
	
}
