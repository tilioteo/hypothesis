/**
 * 
 */
package org.vaadin.maps.client.ui;

import com.google.gwt.dom.client.Style.Unit;

/**
 * @author kamil
 *
 */
public class VWMSTile extends VImageTile {

    public static final String CLASSNAME = "v-wmstile";
    
    private int left = 0;
    private int top = 0;

    public VWMSTile() {
        setStylePrimaryName(CLASSNAME);
    }

    @Override
    public void setUrl(String url) {
    	
    	// TODO check wms request
    	super.setUrl(url);
    }
    
    public int getLeft() {
    	return left;
    }
    
    public void setLeft(int left) {
    	this.left = left;
    	getElement().getStyle().setLeft(left, Unit.PX);
    }
    
    public int getTop() {
    	return top;
    }
    
    public void setTop(int top) {
    	this.top = top;
    	getElement().getStyle().setTop(top, Unit.PX);
    }
}
