/**
 * 
 */
package org.vaadin.maps.client.ui;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author kamil
 *
 */
public abstract class AbstractControl extends SimplePanel {

	public static final String CLASSNAME = "v-control";
	
	protected boolean active = false;
	protected AbstractHandler handler = null;

	public AbstractControl() {
		super();
		setStyleName(CLASSNAME);
		setVisible(false);
	}
	
	public AbstractHandler getHandler() {
		return handler;
	}
	
	@Override
	public void setWidget(Widget widget) {
		if (widget instanceof AbstractHandler) {
			setHandler((AbstractHandler)widget);
		}
	}
	
	protected void setHandler(AbstractHandler handler) {
		if (this.handler == handler) {
			return;
		}
		
		if (this.handler != null) {
			remove(this.handler);
			this.handler.clear();
			this.handler.setControl(null);
		}
		
		if (handler != null) {
			handler.setControl(this);
			super.setWidget(handler);
		}
		
		this.handler = handler;
	}

	public boolean isActive() {
		return active;
	}

	public void activate() {
		if (handler != null && !handler.isActive()) {
			handler.activate();

			active = true;
		}
	}
	
	public void deactivate() {
		if (handler != null && handler.isActive()) {
			handler.deactivate();
			
			active = false;
		}
	}
}
