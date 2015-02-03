/**
 * 
 */
package org.vaadin.maps.client.ui;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author kamil
 * 
 */
public class VVectorFeatureLayer extends InteractiveLayer {

	/** Class name, prefix in styling */
	public static final String CLASSNAME = "v-vectorfeaturelayer";

	public VVectorFeatureLayer() {
		super();
		setStylePrimaryName(CLASSNAME);
	}

	@Override
	public void setFixed(boolean fixed) {
		super.setFixed(fixed);
	}

	@Override
	public void onZoom(double zoom) {
		super.onZoom(zoom);
		
		if (!fixed) {
			clearShift();
			
			Widget content = getWidget();
			if (content instanceof CanShift) {
				((CanShift)content).setShift(0, 0);
			}
		}
	}

}
