/**
 * 
 */
package org.vaadin.maps.ui.control;

import org.vaadin.maps.shared.ui.control.PanControlState;
import org.vaadin.maps.ui.LayerLayout;
import org.vaadin.maps.ui.handler.PanHandler;

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
}
