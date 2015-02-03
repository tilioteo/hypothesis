/**
 * 
 */
package org.vaadin.maps.ui.control;

import org.vaadin.maps.shared.ui.control.PanControlState;
import org.vaadin.maps.ui.LayerLayout;
import org.vaadin.maps.ui.handler.PanHandler;
import org.vaadin.maps.ui.handler.PanHandler.PanEndListener;
import org.vaadin.maps.ui.handler.PanHandler.PanStartListener;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class PanControl extends NavigateControl<PanHandler> {

	public PanControl(LayerLayout layout) {
		super(layout);
	}

	@Override
	protected PanControlState getState() {
		return (PanControlState) super.getState();
	}
	
	public void addPanStartListener(PanStartListener listener) {
		if (handlerInstance != null) {
			handlerInstance.addPanStartListener(listener);
		}
	}

	public void removePanStartListener(PanStartListener listener) {
		if (handlerInstance != null) {
			handlerInstance.removePanStartListener(listener);
		}
	}

	public void addPanEndListener(PanEndListener listener) {
		if (handlerInstance != null) {
			handlerInstance.addPanEndListener(listener);
		}
	}

	public void removePanEndListener(PanEndListener listener) {
		if (handlerInstance != null) {
			handlerInstance.removePanEndListener(listener);
		}
	}

}
