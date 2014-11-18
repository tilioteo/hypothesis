/**
 * 
 */
package org.vaadin.maps.client.ui;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author kamil
 *
 */
public class VNavigateControl extends AbstractControl {

	public static final String CLASSNAME = "v-navigatecontrol";
	
	private VLayerLayout layout = null;

	public VNavigateControl() {
		super();
		setStyleName(CLASSNAME);
	}

	@Override
	public void setWidget(Widget widget) {
		if (widget != null && widget instanceof AbstractNavigateHandler) {
			setHandler((AbstractNavigateHandler)widget);
		}
	}

	public VLayerLayout getLayout() {
		return layout;
	}

	public void setLayout(VLayerLayout layout) {
		if (this.layout == layout) {
			return;
		}
		
		this.layout = layout;
	}
	

}
