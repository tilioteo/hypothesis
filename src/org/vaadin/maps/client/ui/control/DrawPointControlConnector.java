/**
 * 
 */
package org.vaadin.maps.client.ui.control;

import org.vaadin.maps.client.ui.VDrawPointControl;
import org.vaadin.maps.shared.ui.control.DrawPointControlState;

import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(org.vaadin.maps.ui.control.DrawPointControl.class)
public class DrawPointControlConnector extends DrawFeatureControlConnector {

	@Override
	public VDrawPointControl getWidget() {
		return (VDrawPointControl) super.getWidget();
	}

	@Override
	public DrawPointControlState getState() {
		return (DrawPointControlState) super.getState();
	}

}
