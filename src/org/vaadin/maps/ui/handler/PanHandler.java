/**
 * 
 */
package org.vaadin.maps.ui.handler;

import org.vaadin.maps.shared.ui.handler.PanHandlerState;
import org.vaadin.maps.ui.LayerLayout;
import org.vaadin.maps.ui.control.Control;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class PanHandler extends NavigateHandler {

	protected LayerLayout layout = null;
	
	public PanHandler(Control control) {
		super(control);
	}

	@Override
	public void setLayout(LayerLayout layout) {
		this.layout = layout;
		getState().layout = layout;
	}

	@Override
	protected PanHandlerState getState() {
		return (PanHandlerState) super.getState();
	}
}
