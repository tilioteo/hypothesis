/**
 * 
 */
package org.vaadin.maps.shared.ui.mapcontainer;

import com.vaadin.shared.communication.ServerRpc;

/**
 * @author kamil
 *
 */
public interface MapContainerRpc extends ServerRpc {

	public void panEnd(int shiftX, int shiftY);
	public void zoom(double zoom);
	
}
