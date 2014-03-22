/**
 * 
 */
package org.vaadin.maps.ui.handler;

import org.vaadin.maps.shared.ui.handler.PolygonHandlerState;
import org.vaadin.maps.ui.control.Control;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class PolygonHandler extends PathHandler {

	public PolygonHandler(Control control) {
		super(control);
	}

	@Override
	protected PolygonHandlerState getState() {
		return (PolygonHandlerState) super.getState();
	}

}
