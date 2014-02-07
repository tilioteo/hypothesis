/**
 * 
 */
package org.vaadin.maps.ui.layer;

import org.vaadin.maps.ui.tile.WMSTile;

/**
 * @author morong
 *
 */
@SuppressWarnings("serial")
public class WMSLayer extends GridLayer<WMSTile> {

	@Override
	public boolean isBase() {
		return true;
	}

}
