/**
 * 
 */
package org.vaadin.maps.client.ui;

/**
 * @author kamil
 *
 */
public class VWMSLayer extends VAbstractLayer implements PanHandler {

    /** Class name, prefix in styling */
    public static final String CLASSNAME = "v-wmslayer";
    
    protected boolean base = true; 

    public VWMSLayer() {
    	super();
    	setStylePrimaryName(CLASSNAME);
	}

	public boolean isBase() {
		return base;
	}

	public void setBase(boolean base) {
		this.base = base;
	}

	@Override
	public void onPanStep(int dX, int dY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPanEnd(int totalX, int totalY) {
		// TODO Auto-generated method stub
		
	}
    
}
