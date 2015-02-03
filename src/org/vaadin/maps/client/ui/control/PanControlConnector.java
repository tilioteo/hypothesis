/**
 * 
 */
package org.vaadin.maps.client.ui.control;

import org.vaadin.maps.client.ui.VPanControl;
import org.vaadin.maps.shared.ui.control.PanControlState;

import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(org.vaadin.maps.ui.control.PanControl.class)
public class PanControlConnector extends NavigateControlConnector {

	@Override
	public VPanControl getWidget() {
		return (VPanControl) super.getWidget();
	}

	@Override
	public PanControlState getState() {
		return (PanControlState) super.getState();
	}

}
