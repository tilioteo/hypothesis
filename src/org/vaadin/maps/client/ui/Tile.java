/**
 * 
 */
package org.vaadin.maps.client.ui;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author kamil
 *
 */
public interface Tile extends IsWidget, CanShift {
	
	public int getWidth();
	public int getHeight();

	public static interface SizeChangeHandler {
		public void onSizeChange(Tile tile, int oldWidth, int oldHeight, int newWidth, int newHeight);
	}
	
	public static interface TileLoadHandler {
		public void onLoad(Tile tile);
	}
}
