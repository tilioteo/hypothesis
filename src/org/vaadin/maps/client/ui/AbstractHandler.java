/**
 * 
 */
package org.vaadin.maps.client.ui;

import com.google.gwt.user.client.DOM;
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
		setElement(DOM.createDiv());
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
	
	protected abstract void initialize();
	protected abstract void finalize();
}
