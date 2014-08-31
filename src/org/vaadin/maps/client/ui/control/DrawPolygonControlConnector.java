/**
 * 
 */
package org.vaadin.maps.client.ui.control;

import org.vaadin.maps.client.ui.VDrawPolygonControl;
import org.vaadin.maps.shared.ui.control.DrawPolygonControlState;

import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(org.vaadin.maps.ui.control.DrawPolygonControl.class)
public class DrawPolygonControlConnector extends DrawPathControlConnector {

	@Override
	public VDrawPolygonControl getWidget() {
		return (VDrawPolygonControl) super.getWidget();
	}

	@Override
	public DrawPolygonControlState getState() {
		return (DrawPolygonControlState) super.getState();
	}

}
