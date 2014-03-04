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

	public void click(double x, double y, String buttonName, boolean altKey, boolean ctrlKey, boolean metaKey, boolean shiftKey, boolean doubleClick);
	
	public void geometry(String wkb);

}
