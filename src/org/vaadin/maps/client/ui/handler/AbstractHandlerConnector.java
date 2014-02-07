/**
 * 
 */
package org.vaadin.maps.client.ui.handler;

import org.vaadin.maps.client.ui.AbstractHandler;
import org.vaadin.maps.shared.ui.handler.AbstractHandlerState;
import org.vaadin.maps.shared.ui.handler.HandlerClientRpc;

import com.vaadin.client.ui.AbstractComponentConnector;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractHandlerConnector extends AbstractComponentConnector {

	@Override
	protected void init() {
		super.init();
		
		registerRpc(HandlerClientRpc.class, new HandlerClientRpc() {
			@Override
			public void cancel() {
				getWidget().cancel();
			}
		}); 
	}

	@Override
	public AbstractHandler getWidget() {
		return (AbstractHandler) super.getWidget();
	}

	@Override
	public AbstractHandlerState getState() {
		return (AbstractHandlerState) super.getState();
	}

}
