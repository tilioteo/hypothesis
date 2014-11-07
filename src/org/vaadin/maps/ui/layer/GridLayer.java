/**
 * 
 */
package org.vaadin.maps.ui.layer;

import org.vaadin.maps.ui.GridLayout;
import org.vaadin.maps.ui.MeasuredSizeHandler;

import com.vaadin.ui.Component;

/**
 * @author morong
 *
 */
@SuppressWarnings("serial")
public abstract class GridLayer<C extends Component> extends AbstractLayer<GridLayout<C>> implements MeasuredSizeHandler {

	private GridLayout<C> grid = new GridLayout<C>();
	
	@Override
	public boolean isBase() {
		return false;
	}
	
	public GridLayer() {
		setContent(grid);
	}
	
	protected GridLayout<C> getGrid() {
		return grid;
	}

}
