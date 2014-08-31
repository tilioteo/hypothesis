/**
 * 
 */
package org.vaadin.maps.shared.ui.control;

import java.util.Map;

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
	public Map<String, String> cursorStyle = null;

}
