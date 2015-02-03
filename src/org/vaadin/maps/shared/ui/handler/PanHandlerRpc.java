/**
 * 
 */
package org.vaadin.maps.shared.ui.handler;

import com.vaadin.shared.communication.ServerRpc;

/**
 * @author kamil
 *
 */
public interface PanHandlerRpc extends ServerRpc {
	
	public void panStart(int x, int y);
	public void panEnd(int deltaX, int deltaY);

}
