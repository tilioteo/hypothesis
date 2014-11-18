/**
 * 
 */
package org.vaadin.maps.client.ui;

/**
 * @author kamil
 *
 */
public interface PanHandler {
	
	public void onPanStep(int dX, int dY);
	public void onPanEnd(int totalX, int totalY);

}
