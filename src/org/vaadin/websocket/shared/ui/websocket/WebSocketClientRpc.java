package org.vaadin.websocket.shared.ui.websocket;

import com.vaadin.shared.communication.ClientRpc;

public interface WebSocketClientRpc extends ClientRpc {

	public void send(String message);

}