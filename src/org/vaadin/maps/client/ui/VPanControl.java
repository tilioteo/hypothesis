/**
 * 
 */
package org.vaadin.maps.client.ui;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author kamil
 *
 */
public class VPanControl extends VNavigateControl {
	
	public static final String CLASSNAME = "v-pancontrol";

	public VPanControl() {
		super();
		setStyleName(CLASSNAME);
	}

	@Override
	public void setWidget(Widget widget) {
		if (widget != null && widget instanceof VPanHandler) {
			super.setWidget(widget);
		}
	}
	
	@Override
	public VPanHandler getHandler() {
		return (VPanHandler) handler;
	}
	
}
