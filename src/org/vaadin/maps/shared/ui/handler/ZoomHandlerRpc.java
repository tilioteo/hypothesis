/**
 * 
 */
package org.vaadin.maps.shared.ui.handler;

import com.vaadin.shared.communication.ServerRpc;

/**
 * @author kamil
 *
 */
public interface ZoomHandlerRpc extends ServerRpc {

	public void zoomChange(double zoomStep);
	
}
