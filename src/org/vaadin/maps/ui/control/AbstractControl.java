/**
 * 
 */
package org.vaadin.maps.ui.control;

import org.vaadin.maps.shared.ui.control.AbstractControlState;
import org.vaadin.maps.shared.ui.control.ControlClientRpc;
import org.vaadin.maps.ui.AbstractSingleComponentContainer;
import org.vaadin.maps.ui.handler.Handler;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractControl extends AbstractSingleComponentContainer<Handler> implements Control {
	
	private ControlClientRpc clientRpc;
	
	protected ControlType controlType;
	
	protected Handler handler = null;
	
	protected AbstractControl() {
		super();
		clientRpc = getRpcProxy(ControlClientRpc.class);
	}
	
	@Override
	public boolean activate() {
		if (isActive())
			return false;
		
		if (handler != null)
			handler.activate();
		
		setActive(true);
		clientRpc.activate();

		return true;
	}
	
	@Override
	public boolean deactivate() {
		if (!isActive())
			return false;

		if (handler != null)
			handler.deactivate();
			
		setActive(false);
		clientRpc.deactivate();
		
		return true;
	}

	@Override
	protected AbstractControlState getState() {
		return (AbstractControlState) super.getState();
	}
	
	public boolean isActive() {
		return getState().active;
	}
	
	protected void setActive(boolean active) {
		getState().active = active;
	}
	
	protected void setHandler(Handler handler) {
		this.handler = handler;
		setContent(handler);
		//getState().handler = handler;
	}
}
