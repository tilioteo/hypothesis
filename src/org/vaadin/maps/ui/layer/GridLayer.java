/**
 * 
 */
package org.vaadin.maps.ui.layer;

import org.vaadin.maps.ui.GridLayout;

import com.vaadin.ui.Component;

/**
 * @author morong
 *
 */
@SuppressWarnings("serial")
public class GridLayer<C extends Component> extends
		AbstractLayer<GridLayout<C>> {

	@Override
	public boolean isBase() {
		return false;
	}

}
