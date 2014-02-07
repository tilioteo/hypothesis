/**
 * 
 */
package org.vaadin.maps.ui.handler;

import org.vaadin.maps.shared.ui.handler.AbstractHandlerState;
import org.vaadin.maps.shared.ui.handler.HandlerClientRpc;
import org.vaadin.maps.ui.control.Control;

import com.vaadin.ui.AbstractComponent;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractHandler extends AbstractComponent implements Handler {
	
	private HandlerClientRpc clientRpc;
	
	protected Control control = null;
	
	protected AbstractHandler(Control control) {
		setControl(control);
		
		clientRpc = getRpcProxy(HandlerClientRpc.class);
	}
	
	@Override
	protected AbstractHandlerState getState() {
		return (AbstractHandlerState) super.getState();
	}
	
	private void setControl(Control control) {
		this.control = control;
	}

	@Override
	public boolean activate() {
		if (isActive())
			return false;
		
		setActive(true);
		return true;
	}
	
	@Override
	public boolean deactivate() {
		if (!isActive())
			return false;
		
		setActive(false);
		return false;
	}
	
	@Override
	public void cancel() {
		clientRpc.cancel();
	}
	
	protected boolean isActive() {
		return getState().active;
	}
	
	protected void setActive(boolean active) {
		getState().active = active;
	}
	
}
