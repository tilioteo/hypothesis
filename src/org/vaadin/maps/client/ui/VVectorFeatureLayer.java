/**
 * 
 */
package org.vaadin.maps.client.ui;

/**
 * @author kamil
 * 
 */
public class VVectorFeatureLayer extends VAbstractLayer implements PanHandler {

	/** Class name, prefix in styling */
	public static final String CLASSNAME = "v-vectorfeaturelayer";

    protected boolean fixed = false; 

	public VVectorFeatureLayer() {
		super();
		setStylePrimaryName(CLASSNAME);
	}

	public boolean isFixed() {
		return fixed;
	}

	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}

	@Override
	public void onPanStep(int dX, int dY) {
		if (!fixed) {
			
		}
	}

	@Override
	public void onPanEnd(int totalX, int totalY) {
		if (!fixed) {
			
		}
	}

}
