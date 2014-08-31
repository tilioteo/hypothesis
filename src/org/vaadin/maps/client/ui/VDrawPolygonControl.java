/**
 * 
 */
package org.vaadin.maps.client.ui;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author kamil
 *
 */
public class VDrawPolygonControl extends VDrawPathControl {

	public static final String CLASSNAME = "v-drawpolygoncontrol";
	
	public VDrawPolygonControl() {
		super();
		setStyleName(CLASSNAME);
	}

	@Override
	public void setWidget(Widget widget) {
		if (widget != null && widget instanceof VPolygonHandler) {
			super.setWidget(widget);
		}
	}
	
	@Override
	public VPolygonHandler getHandler() {
		return (VPolygonHandler) handler;
	}
	
}
