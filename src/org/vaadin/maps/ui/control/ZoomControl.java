/**
 * 
 */
package org.vaadin.maps.ui.control;

import org.vaadin.maps.shared.ui.control.ZoomControlState;
import org.vaadin.maps.ui.LayerLayout;
import org.vaadin.maps.ui.handler.ZoomHandler;
import org.vaadin.maps.ui.handler.ZoomHandler.ZoomChangeListener;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class ZoomControl extends NavigateControl<ZoomHandler> {

	public ZoomControl(LayerLayout layout) {
		super(layout);
	}

	@Override
	protected ZoomControlState getState() {
		return (ZoomControlState) super.getState();
	}

	public void addZoomChangeListener(ZoomChangeListener listener) {
		if (handlerInstance != null) {
			handlerInstance.addZoomChangeListener(listener);
		}
	}

	public void removeZoomChangeListener(ZoomChangeListener listener) {
		if (handlerInstance != null) {
			handlerInstance.removeZoomChangeListener(listener);
		}
	}

}
