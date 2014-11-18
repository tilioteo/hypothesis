package org.vaadin.websocket.shared.ui.websocket;

import com.vaadin.shared.communication.ServerRpc;

public interface WebSocketServerRpc extends ServerRpc {

	public void message(String message);
	public void error(String message);
	public void open();
	public void close();

}
