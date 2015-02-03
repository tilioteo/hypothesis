/**
 * 
 */
package org.vaadin.maps.ui.layer;

import java.util.ArrayList;

import org.vaadin.maps.event.LayoutEvents;
import org.vaadin.maps.event.LayoutEvents.LayoutClickEvent;
import org.vaadin.maps.event.LayoutEvents.LayoutClickListener;
import org.vaadin.maps.ui.GridLayout;
import org.vaadin.maps.ui.MeasuredSizeHandler;

import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.shared.EventId;
import com.vaadin.ui.Component;

/**
 * @author morong
 *
 */
@SuppressWarnings("serial")
public abstract class GridLayer<C extends Component> extends AbstractLayer<GridLayout<C>> implements MeasuredSizeHandler {

	private GridLayout<C> grid = new GridLayout<C>();
	
	private ArrayList<ClickListener> clickListeners = new ArrayList<ClickListener>();
	
	private LayoutClickListener<C> layoutClickListener = new LayoutClickListener<C>() {
		@Override
		public void layoutClick(LayoutClickEvent<C> event) {
			fireEvent(LayoutEvents.LayoutClickEvent.createClickEvent(GridLayer.this, event));
		}
	};
	
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

	/**
	 * Add a click listener to the component. The listener is called whenever
	 * the user clicks inside the component. Depending on the content the event
	 * may be blocked and in that case no event is fired.
	 * 
	 * Use {@link #removeClickListener(ClickListener)} to remove the listener.
	 * 
	 * @param listener
	 *            The listener to add
	 */
	public void addClickListener(ClickListener listener) {
		addListener(EventId.CLICK_EVENT_IDENTIFIER, ClickEvent.class, listener, ClickListener.clickMethod);
		if (clickListeners.size() == 0) {
			grid.addLayoutClickListener(layoutClickListener);
		}
		clickListeners.add(listener);
	}

	/**
	 * Remove a click listener from the component. The listener should earlier
	 * have been added using {@link #addClickListener(ClickListener)}.
	 * 
	 * @param listener
	 *            The listener to remove
	 */
	public void removeClickListener(ClickListener listener) {
		removeListener(EventId.CLICK_EVENT_IDENTIFIER, ClickEvent.class, listener);
		clickListeners.remove(listener);
		if (clickListeners.size() == 0) {
			grid.removeLayoutClickListener(layoutClickListener);
		}
	}


}
