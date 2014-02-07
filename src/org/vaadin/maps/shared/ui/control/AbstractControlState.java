/**
 * 
 */
package org.vaadin.maps.shared.ui.control;

import org.vaadin.maps.shared.ui.AbstractComponentContainerState;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractControlState extends AbstractComponentContainerState {
    {
        primaryStyleName = "v-control";
    }

	public boolean active = false;

}
