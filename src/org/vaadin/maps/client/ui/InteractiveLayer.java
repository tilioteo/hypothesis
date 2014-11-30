/**
 * 
 */
package org.vaadin.maps.client.ui;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author kamil
 *
 */
public abstract class InteractiveLayer extends AbstractLayer implements PanHandler, SizeChangeHandler {

    private int shiftX = 0;
    private int shiftY = 0;

    protected boolean fixed = false; 

	public boolean isFixed() {
		return fixed;
	}

	protected void setFixed(boolean fixed) {
		this.fixed = fixed;
	}

	@Override
	public void onPanStep(int dX, int dY) {
		if (!fixed) {
			Widget content = getWidget();
			if (content instanceof CanShift) {
				((CanShift)content).setShift(shiftX + dX, shiftY + dY);
			}
		}
	}

	@Override
	public void onPanEnd(int totalX, int totalY) {
		if (!fixed) {
			shiftX += totalX;
			shiftY += totalY;
		}
	}
	
	public void clearShift() {
		shiftX = shiftY = 0;
	}
}
