/**
 * 
 */
package org.vaadin.maps.client.ui;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author kamil
 *
 */
public class VZoomControl extends VNavigateControl {

	public static final String CLASSNAME = "v-zoomcontrol";

	public VZoomControl() {
		super();
		setStyleName(CLASSNAME);
	}

	@Override
	public void setWidget(Widget widget) {
		if (widget != null && widget instanceof VZoomHandler) {
			super.setWidget(widget);
		}
	}
	
	@Override
	public VZoomHandler getHandler() {
		return (VZoomHandler) handler;
	}
	
}
