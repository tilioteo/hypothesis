/**
 * 
 */
package org.vaadin.maps.client.ui;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author kamil
 *
 */
public class VWMSLayer extends InteractiveLayer {

    /** Class name, prefix in styling */
    public static final String CLASSNAME = "v-wmslayer";
    
    protected boolean base = true;
    protected boolean singleTile = true;
    
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

	public boolean isSingleTile() {
		return singleTile;
	}

	public void setSingleTile(boolean singleTile) {
		this.singleTile = singleTile;
	}

	@Override
	public void onSizeChange(int oldWidth, int oldHeight, int newWidth,	int newHeight) {
		Widget child = getWidget();
		if (child instanceof VGridLayout) {
			VGridLayout gridLayout = (VGridLayout)child;
			gridLayout.setMeasuredSize(newWidth, newHeight);
			
			if (singleTile) {
				// center tile
				Widget tileWidget = gridLayout.getWidget(0);
				if (tileWidget instanceof Tile) {
					Tile tile = (Tile)tileWidget;
					int dx = (newWidth - tile.getWidth()) / 2;
					int dy = (newHeight - tile.getHeight()) / 2;
					gridLayout.setWidgetPosition(tileWidget, dx, dy);
				}
			} else {
				
			}
		}
	}

}
