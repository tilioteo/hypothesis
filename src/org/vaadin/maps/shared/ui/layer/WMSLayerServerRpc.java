/**
 * 
 */
package org.vaadin.maps.shared.ui.layer;

import com.vaadin.shared.communication.ServerRpc;

/**
 * @author kamil
 *
 */
public interface WMSLayerServerRpc extends ServerRpc {
	
	public void requestSingleTile(int width, int height, int shiftX, int shiftY);

}
