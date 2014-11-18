package org.vaadin.websocket.ui;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;

import org.vaadin.websocket.shared.ui.websocket.WebSocketClientRpc;
import org.vaadin.websocket.shared.ui.websocket.WebSocketServerRpc;
import org.vaadin.websocket.shared.ui.websocket.WebSocketState;

@SuppressWarnings("serial")
public class WebSocket extends AbstractComponent {

	private WebSocketServerRpc rpc = new WebSocketServerRpc() {

		@Override
		public void message(String message) {
			fireEvent(new MessageEvent(WebSocket.this, message));
		}

		@Override
		public void error(String message) {
			fireEvent(new ErrorEvent(WebSocket.this, message));
		}

		@Override
		public void open() {
			WebSocket.this.connected = true;
			fireEvent(new OpenEvent(WebSocket.this));
		}

		@Override
		public void close() {
			WebSocket.this.connected = false;
			fireEvent(new CloseEvent(WebSocket.this));
		}
	};
	
	private WebSocketClientRpc clientRpc;
	private boolean connected = false;

	public WebSocket() {
		registerRpc(rpc);
		clientRpc = getRpcProxy(WebSocketClientRpc.class);
	}

	public WebSocket(String url) {
		this();
		setUrl(url);
	}
	
	public void setUrl(String url) {
		// TODO check url
		getState().url = url;
	}
	
	public String getUrl() {
		return getState().url;
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public void send(String message) {
		if (connected) {
			clientRpc.send(message);
		}
	}

	public void close() {
		getState().url = "";
	}

	@Override
	public WebSocketState getState() {
		return (WebSocketState) super.getState();
	}
	
	public class OpenEvent extends Component.Event {

		public static final String EVENT_ID = "open";

		public OpenEvent(WebSocket source) {
			super(source);
		}
	}

	public interface OpenListener extends Serializable {

		public static final Method OPEN_METHOD = ReflectTools
				.findMethod(OpenListener.class, OpenEvent.EVENT_ID, OpenEvent.class);

		/**
		 * Called when a {@link WebSocket} has been opened. A reference to the
		 * component is given by {@link OpenEvent#getComponent()}.
		 * 
		 * @param event
		 *            An event containing information about the web socket.
		 */
		public void open(OpenEvent event);
	}

	public class CloseEvent extends Component.Event {

		public static final String EVENT_ID = "close";

		public CloseEvent(WebSocket source) {
			super(source);
		}
	}

	public interface CloseListener extends Serializable {

		public static final Method CLOSE_METHOD = ReflectTools
				.findMethod(CloseListener.class, CloseEvent.EVENT_ID, CloseEvent.class);

		/**
		 * Called when a {@link WebSocket} has been closed. A reference to the
		 * component is given by {@link CloseEvent#getComponent()}.
		 * 
		 * @param event
		 *            An event containing information about the web socket.
		 */
		public void close(CloseEvent event);
	}

	public class MessageEvent extends Component.Event {

		public static final String EVENT_ID = "message";
		
		private String message;

		public MessageEvent(WebSocket source, String message) {
			super(source);
			this.message = message;
		}
		
		public String getMessage() {
			return message;
		}
	}

	public interface MessageListener extends Serializable {

		public static final Method MESSAGE_METHOD = ReflectTools
				.findMethod(MessageListener.class, MessageEvent.EVENT_ID, MessageEvent.class);

		/**
		 * Called when a {@link WebSocket} recieved a message. A reference to the
		 * component is given by {@link MessageEvent#getComponent()}.
		 * 
		 * @param event
		 *            An event containing information about the web socket.
		 */
		public void message(MessageEvent event);
	}

	public class ErrorEvent extends Component.Event {

		public static final String EVENT_ID = "error";
		
		private String message;

		public ErrorEvent(WebSocket source, String message) {
			super(source);
			this.message = message;
		}
		
		public String getMessage() {
			return message;
		}
	}

	public interface ErrorListener extends Serializable {

		public static final Method ERROR_METHOD = ReflectTools
				.findMethod(ErrorListener.class, ErrorEvent.EVENT_ID, ErrorEvent.class);

		/**
		 * Called when an error occurred on {@link WebSocket} connection. A reference to the
		 * component is given by {@link ErrorEvent#getComponent()}.
		 * 
		 * @param event
		 *            An event containing information about the web socket.
		 */
		public void error(ErrorEvent event);
	}

	public void addOpenListener(OpenListener listener) {
		addListener(OpenEvent.EVENT_ID, OpenEvent.class, listener, OpenListener.OPEN_METHOD);
	}

	public void removeOpenListener(OpenListener listener) {
		removeListener(OpenEvent.EVENT_ID, OpenEvent.class, listener);
	}

	public void addCloseListener(CloseListener listener) {
		addListener(CloseEvent.EVENT_ID, CloseEvent.class, listener, CloseListener.CLOSE_METHOD);
	}

	public void removeCloseListener(CloseListener listener) {
		removeListener(CloseEvent.EVENT_ID, CloseEvent.class, listener);
	}

	public void addMessageListener(MessageListener listener) {
		addListener(MessageEvent.EVENT_ID, MessageEvent.class, listener, MessageListener.MESSAGE_METHOD);
	}

	public void removeMessageListener(MessageListener listener) {
		removeListener(MessageEvent.EVENT_ID, MessageEvent.class, listener);
	}

	public void addErrorListener(ErrorListener listener) {
		addListener(ErrorEvent.EVENT_ID, ErrorEvent.class, listener, ErrorListener.ERROR_METHOD);
	}

	public void removeErrorListener(ErrorListener listener) {
		removeListener(ErrorEvent.EVENT_ID, ErrorEvent.class, listener);
	}

}
