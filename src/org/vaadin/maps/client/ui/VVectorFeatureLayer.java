/**
 * 
 */
package org.vaadin.maps.client.ui;

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
	public void onSizeChange(int oldWidth, int oldHeight, int newWidth,	int newHeight) {
		// TODO
		
	}

}
