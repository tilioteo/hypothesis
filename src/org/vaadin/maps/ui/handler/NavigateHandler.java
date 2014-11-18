/**
 * 
 */
package org.vaadin.maps.ui.handler;

import org.vaadin.maps.ui.control.Control;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class NavigateHandler extends AbstractHandler implements RequiresLayerLayout {

	protected NavigateHandler(Control control) {
		super(control);
	}

}
