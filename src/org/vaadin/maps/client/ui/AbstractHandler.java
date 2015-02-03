/**
 * 
 */
package org.vaadin.maps.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author kamil
 *
 */
public abstract class AbstractHandler extends Widget {

	public static final String CLASSNAME = "v-handler";
	
	protected boolean active = false;
	
	// parent control
	protected AbstractControl control = null;

	public AbstractHandler() {
		super();
		setElement(Document.get().createDivElement());
		setStyleName(CLASSNAME);
		setVisible(false);
	}
	
	public AbstractControl getControl() {
		return control;
	}

	public void setControl(AbstractControl control) {
		if (this.control == control) {
			return;
		}
		
		this.control = control;		
	}

	public boolean isActive() {
		return active;
	}

	public void activate() {
		active = true;
	}
	
	public void deactivate() {
		active = false;
	}
	
	public void cancel() {
		
	}
	
	public void clear() {
		finalize();
	}
	
	protected final void removeEventHandler(HandlerRegistration handler) {
		if (handler != null) {
			handler.removeHandler();
			handler = null;
		}
	}

	protected abstract void initialize();
	protected abstract void finalize();
}
