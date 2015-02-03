/**
 * 
 */
package org.vaadin.maps.client.ui;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author kamil
 *
 */
public abstract class InteractiveLayer extends AbstractLayer implements PanHandler, ZoomHandler, SizeChangeHandler {

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
	public void onSizeChange(int oldWidth, int oldHeight, int newWidth,	int newHeight) {
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
	
	@Override
	public void onZoom(double zoom) {
		if (!fixed) {
			Widget content = getWidget();
		}
	}

	public void clearShift() {
		shiftX = shiftY = 0;
	}

	protected final void removeHandler(HandlerRegistration handler) {
		if (handler != null) {
			handler.removeHandler();
			handler = null;
		}
	}
	
}
