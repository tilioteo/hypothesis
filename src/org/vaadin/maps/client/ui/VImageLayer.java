/**
 * 
 */
package org.vaadin.maps.client.ui;

/**
 * @author morong
 *
 */
public class VImageLayer extends VAbstractLayer implements PanHandler {

    /** Class name, prefix in styling */
    public static final String CLASSNAME = "v-imagelayer";
    
    protected boolean fixed = false; 

    public VImageLayer() {
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
