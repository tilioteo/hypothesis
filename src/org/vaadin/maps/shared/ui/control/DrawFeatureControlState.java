/**
 * 
 */
package org.vaadin.maps.shared.ui.control;

import com.vaadin.shared.Connector;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class DrawFeatureControlState extends AbstractControlState {
    {
        primaryStyleName = "v-drawfeaturecontrol";
    }

	public Connector layer = null;
}
