/**
 * 
 */
package org.vaadin.websocket.client.ui;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author kamil
 *
 */
public class WebSocketObject extends JavaScriptObject {
	
	protected WebSocketObject() {
	}
	
	public final native void send(String message) /*-{
		this.send(message);
	}-*/;

	public final native void close() /*-{
		this.close();
	}-*/;
}
