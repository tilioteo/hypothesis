/**
 * 
 */
package org.vaadin.maps.ui.tile;

import org.vaadin.maps.server.TileResource;
import org.vaadin.maps.shared.ui.tile.ProxyTileServerRpc;
import org.vaadin.maps.shared.ui.tile.ProxyTileState;

import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.shared.EventId;
import com.vaadin.shared.MouseEventDetails;

/**
 * @author morong
 * 
 */
@SuppressWarnings("serial")
public class ProxyTile<T extends TileResource> extends AbstractTile {

	protected ProxyTileServerRpc rpc = new ProxyTileServerRpc() {
		@Override
		public void click(MouseEventDetails mouseDetails) {
			fireEvent(new ClickEvent(ProxyTile.this, mouseDetails));
		}
	};

	/**
	 * Creates a new empty Tile.
	 */
	public ProxyTile() {
		registerRpc(rpc);
	}

	/**
	 * Creates a new Image whose contents is loaded from given resource. The
	 * dimensions are assumed if possible. The type is guessed from resource.
	 * 
	 * @param source
	 *            the Source of the embedded object.
	 */
	public ProxyTile(T source) {
		this();
		setSource(source);
	}

	@Override
	protected ProxyTileState getState() {
		return (ProxyTileState) super.getState();
	}

	/**
	 * Sets the object source resource. The dimensions are assumed if possible.
	 * The type is guessed from resource.
	 * 
	 * @param source
	 *            the source to set.
	 */
	public void setSource(T source) {
		setTileResource(source);
	}

	/**
	 * Get the object source resource.
	 * 
	 * @return the source
	 */
	@SuppressWarnings("unchecked")
	public T getSource() {
		return (T) getTileResource();
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
		addListener(EventId.CLICK_EVENT_IDENTIFIER, ClickEvent.class, listener,
				ClickListener.clickMethod);
	}

	/**
	 * Remove a click listener from the component. The listener should earlier
	 * have been added using {@link #addClickListener(ClickListener)}.
	 * 
	 * @param listener
	 *            The listener to remove
	 */
	public void removeClickListener(ClickListener listener) {
		removeListener(EventId.CLICK_EVENT_IDENTIFIER, ClickEvent.class,
				listener);
	}

}
