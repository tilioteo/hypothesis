/**
 * 
 */
package org.vaadin.maps.shared.ui.layerlayout;

import java.util.HashMap;
import java.util.Map;

import org.vaadin.maps.shared.ui.AbstractLayoutState;

/**
 * @author morong
 *
 */
@SuppressWarnings("serial")
public class LayerLayoutState extends AbstractLayoutState {
    {
        primaryStyleName = "v-layerlayout";
    }

    // Maps each component to a position
    public Map<String, String> connectorToCssPosition = new HashMap<String, String>();
}