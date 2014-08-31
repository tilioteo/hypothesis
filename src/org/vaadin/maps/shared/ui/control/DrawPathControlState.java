/**
 * 
 */
package org.vaadin.maps.shared.ui.control;

import java.util.Map;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class DrawPathControlState extends DrawPointControlState {
    {
        primaryStyleName = "v-drawpathcontrol";
    }

	public Map<String, String> startPointStyle = null;
	public Map<String, String> lineStyle = null;
	public Map<String, String> vertexStyle = null;

}
