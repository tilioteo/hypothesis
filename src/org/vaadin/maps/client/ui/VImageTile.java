/**
 * 
 */
package org.vaadin.maps.client.ui;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Image;

/**
 * @author kamil
 *
 */
public class VImageTile extends Image implements Tile {

    public static final String CLASSNAME = "v-imagetile";

	private int shiftX = 0;
	private int shiftY = 0;

	public VImageTile() {
        setStylePrimaryName(CLASSNAME);
        
        setAltText("");
        getElement().getStyle().setPosition(Position.ABSOLUTE);
    }
    
    @Override
    public void setUrl(String url) {
    	setVisible(false);
    	super.setUrl(url != null ? url : "");
    }

    
	public int getShiftX() {
		return shiftX;
	}

	public int getShiftY() {
		return shiftY;
	}

	@Override
	public void setShift(int x, int y) {
		shiftX = x;
		shiftY = y;
		
		Style style = getElement().getStyle();
		style.setLeft(x, Unit.PX);
		style.setTop(y, Unit.PX);
	}
}
