/**
 * 
 */
package org.vaadin.maps.ui.controlcontainer;

import org.vaadin.maps.ui.control.AbstractControl;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class GeneralControlContainer extends AbstractControlContainer<AbstractControl> {

	public void deactivateAll() {
		for (AbstractControl control : components) {
			if (control.isActive()) {
				control.deactivate();
			}
		}
	}
}
