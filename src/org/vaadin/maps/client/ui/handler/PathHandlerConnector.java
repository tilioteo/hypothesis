/**
 * 
 */
package org.vaadin.maps.client.ui.handler;

import org.vaadin.maps.client.ui.VPathHandler;
import org.vaadin.maps.shared.ui.handler.PathHandlerState;

import com.vaadin.shared.ui.Connect;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Connect(org.vaadin.maps.ui.handler.PathHandler.class)
public class PathHandlerConnector extends PointHandlerConnector {

	@Override
	protected void init() {
		super.init();
	}

	@Override
	public VPathHandler getWidget() {
		return (VPathHandler) super.getWidget();
	}

	@Override
	public PathHandlerState getState() {
		return (PathHandlerState) super.getState();
	}

}
