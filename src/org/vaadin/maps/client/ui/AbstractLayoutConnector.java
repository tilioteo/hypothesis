/**
 * 
 */
package org.vaadin.maps.client.ui;

import org.vaadin.maps.shared.ui.AbstractLayoutState;

import com.vaadin.client.ui.AbstractComponentContainerConnector;


/**
 * @author morong
 * 
 */
@SuppressWarnings("serial")
public abstract class AbstractLayoutConnector extends
		AbstractComponentContainerConnector {

	@Override
	public AbstractLayoutState getState() {
		return (AbstractLayoutState) super.getState();
	}
}
