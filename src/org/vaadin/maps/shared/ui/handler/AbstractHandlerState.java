/**
 * 
 */
package org.vaadin.maps.shared.ui.handler;

import com.vaadin.shared.AbstractComponentState;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractHandlerState extends AbstractComponentState {
    {
        primaryStyleName = "v-handler";
    }

    public boolean active = false;

}
