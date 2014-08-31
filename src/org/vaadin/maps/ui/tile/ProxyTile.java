/**
 * 
 */
package org.vaadin.maps.ui.tile;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.vaadin.maps.server.TileResource;
import org.vaadin.maps.shared.ui.tile.ProxyTileServerRpc;
import org.vaadin.maps.shared.ui.tile.ProxyTileState;

import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.shared.EventId;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;

/**
 * @author morong
 * 
 */
@SuppressWarnings("serial")
public class ProxyTile<T extends TileResource> extends AbstractTile {

	protected ProxyTileServerRpc rpc = new ProxyTileServerRpc() {
		@Override
		public void load() {
			fireEvent(new LoadEvent(ProxyTile.this));
		}

		@Override
		public void error() {
			fireEvent(new ErrorEvent(ProxyTile.this));
		}

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
	 * Load event. This event is thrown, when the tile is loaded.
	 * 
	 */
	public static class LoadEvent extends Component.Event {
		
		/**
		 * New instance of tile load event.
		 * 
		 * @param source
		 *            the Source of the event.
		 */
		public LoadEvent(ProxyTile<?> source) {
			super(source);
		}

		/**
		 * Gets the ProxyTile where the event occurred.
		 * 
		 * @return the Source of the event.
		 */
		public ProxyTile<?> getTile() {
			return (ProxyTile<?>) getSource();
		}

	}

	/**
	 * Interface for listening for a {@link LoadEvent} fired by a
	 * {@link ProxyTile}.
	 * 
	 */
	public interface LoadListener extends Serializable {

		public static final Method TILE_LOAD_METHOD = ReflectTools
				.findMethod(LoadListener.class, "load",	LoadEvent.class);

		/**
		 * Called when a {@link AbstractTile} has been loaded. A reference to the
		 * tile is given by {@link LoadEvent#getTile()}.
		 * 
		 * @param event
		 *            An event containing information about the click.
		 */
		public void load(LoadEvent event);

	}
	
	/**
	 * Error event. This event is thrown, when the tile loading failed.
	 * 
	 */
	public static class ErrorEvent extends Component.Event {
		
		/**
		 * New instance of tile error event.
		 * 
		 * @param source
		 *            the Source of the event.
		 */
		public ErrorEvent(ProxyTile<?> source) {
			super(source);
		}

		/**
		 * Gets the ProxyTile where the event occurred.
		 * 
		 * @return the Source of the event.
		 */
		public ProxyTile<?> getTile() {
			return (ProxyTile<?>) getSource();
		}

	}

	/**
	 * Interface for listening for a {@link ErrorEvent} fired by a
	 * {@link ProxyTile}.
	 * 
	 */
	public interface ErrorListener extends Serializable {

		public static final Method TILE_ERROR_METHOD = ReflectTools
				.findMethod(ErrorListener.class, "error", ErrorEvent.class);

		/**
		 * Called when a {@link ProxyTile} loading failed. A reference to the
		 * tile is given by {@link ErrorEvent#getTile()}.
		 * 
		 * @param event
		 *            An event containing information about the error.
		 */
		public void error(ErrorEvent event);

	}
	
	/**
	 * Adds the tile load listener.
	 * 
	 * @param listener
	 *            the Listener to be added.
	 */
	public void addLoadListener(LoadListener listener) {
		addListener(LoadEvent.class, listener,
				LoadListener.TILE_LOAD_METHOD);
	}

	/**
	 * Removes the tile load listener.
	 * 
	 * @param listener
	 *            the Listener to be removed.
	 */
	public void removeLoadListener(LoadListener listener) {
		removeListener(LoadEvent.class, listener,
				LoadListener.TILE_LOAD_METHOD);
	}

	/**
	 * Adds the tile error listener.
	 * 
	 * @param listener
	 *            the Listener to be added.
	 */
	public void addErrorListener(ErrorListener listener) {
		addListener(ErrorEvent.class, listener,
				ErrorListener.TILE_ERROR_METHOD);
	}

	/**
	 * Removes the tile error listener.
	 * 
	 * @param listener
	 *            the Listener to be removed.
	 */
	public void removeErrorListener(ErrorListener listener) {
		removeListener(ErrorEvent.class, listener,
				ErrorListener.TILE_ERROR_METHOD);
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
