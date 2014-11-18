/**
 * 
 */
package org.vaadin.websocket.client.ui;

/**
 * @author kamil
 *
 */
public interface WebSocketHandler {
	
	public void onMessage(String message);
	public void onError(String message);
	public void onOpen();
	public void onClose();

}
