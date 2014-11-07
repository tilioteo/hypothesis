package org.vaadin.maps.shared.ui.layerlayout;

import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.shared.ui.LayoutClickRpc;

public interface LayerLayoutServerRpc extends LayoutClickRpc, ServerRpc {
	
	public void updateMeasuredSize(int newWidth, int newHeight);

}
