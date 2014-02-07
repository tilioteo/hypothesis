/**
 * 
 */
package org.vaadin.maps.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author kamil
 *
 */
public class VGeneralControlContainer extends ComplexPanel {

	public static final String CLASSNAME = "v-controlcontainer";
	
	Element container = DOM.createDiv();

	public VGeneralControlContainer() {
		super();
		setStyleName(CLASSNAME);
		setVisible(false);
		
		getElement().appendChild(container);
	}
	
	@Override
	public void add(Widget widget) {
		if (widget instanceof AbstractControl) {
			addControl((AbstractControl)widget);
		}
	}
	
	protected void addControl(AbstractControl control) {
			add(control, container);
	}
	
}
