/**
 * 
 */
package org.vaadin.maps.client.ui;

/**
 * @author morong
 *
 */
public class VImageLayer extends InteractiveLayer {

    /** Class name, prefix in styling */
    public static final String CLASSNAME = "v-imagelayer";
    
    public VImageLayer() {
    	super();
    	setStylePrimaryName(CLASSNAME);
	}

	@Override
	public void setFixed(boolean fixed) {
		super.setFixed(fixed);
	}

	@Override
	public void onSizeChange(int oldWidth, int oldHeight, int newWidth,	int newHeight) {
		// TODO Auto-generated method stub
		
	}

}
