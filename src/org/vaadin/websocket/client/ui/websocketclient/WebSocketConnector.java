package org.vaadin.websocket.client.ui.websocketclient;

import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;

import org.vaadin.websocket.shared.ui.websocket.WebSocketClientRpc;
import org.vaadin.websocket.shared.ui.websocket.WebSocketServerRpc;
import org.vaadin.websocket.shared.ui.websocket.WebSocketState;
import org.vaadin.websocket.client.ui.VWebSocket;
import org.vaadin.websocket.client.ui.WebSocketHandler;

@SuppressWarnings("serial")
@Connect(org.vaadin.websocket.ui.WebSocket.class)
public class WebSocketConnector extends AbstractComponentConnector implements WebSocketHandler {

	
	@Override
	protected void init() {
		super.init();
		
		getWidget().setHandler(this);
	}

	public WebSocketConnector() {
		registerRpc(WebSocketClientRpc.class, new WebSocketClientRpc() {
			public void send(String message) {
				getWidget().send(message);
			}
		});
	}

	@Override
	public VWebSocket getWidget() {
		return (VWebSocket) super.getWidget();
	}

	@Override
	public WebSocketState getState() {
		return (WebSocketState) super.getState();
	}

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);

        if (stateChangeEvent.hasPropertyChanged("url")) {
			getWidget().setUrl(getState().url);
		}
	}

	@Override
	public void onMessage(String message) {
		getRpcProxy(WebSocketServerRpc.class).message(message);
	}

	@Override
	public void onError(String message) {
		getRpcProxy(WebSocketServerRpc.class).error(message);
	}

	@Override
	public void onOpen() {
		getRpcProxy(WebSocketServerRpc.class).open();
	}

	@Override
	public void onClose() {
		getRpcProxy(WebSocketServerRpc.class).close();
	}

}

