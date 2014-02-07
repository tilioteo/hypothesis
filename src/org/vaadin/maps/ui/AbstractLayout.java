/**
 * 
 */
package org.vaadin.maps.ui;

import org.vaadin.maps.shared.ui.AbstractLayoutState;

import com.vaadin.ui.Component;


/**
 * @author morong
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractLayout<C extends Component> extends AbstractComponentContainer<C> implements Layout<C> {

	@Override
	protected AbstractLayoutState getState() {
		return (AbstractLayoutState) super.getState();
	}

}

