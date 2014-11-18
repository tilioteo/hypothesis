/**
 * 
 */
package org.vaadin.maps.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author kamil
 *
 */
public class VGeneralControlContainer extends ComplexPanel {

	public static final String CLASSNAME = "v-controlcontainer";
	
	public VGeneralControlContainer() {
		super();
		setElement(Document.get().createDivElement());
		setStyleName(CLASSNAME);
		setVisible(false);
		//setSize("0px", "0px");
	}
	
	@Override
	public void add(Widget widget) {
		if (widget instanceof AbstractControl) {
			addControl((AbstractControl)widget);
		}
	}
	
	protected void addControl(AbstractControl control) {
		add(control, getElement());
	}
	
}
