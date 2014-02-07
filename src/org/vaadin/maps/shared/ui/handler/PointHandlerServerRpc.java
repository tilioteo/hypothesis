/**
 * 
 */
package org.vaadin.maps.shared.ui.handler;

import com.vaadin.shared.communication.ServerRpc;

/**
 * @author kamil
 *
 */
public interface PointHandlerServerRpc extends ServerRpc {

	public void geometry(String wkb);

}
