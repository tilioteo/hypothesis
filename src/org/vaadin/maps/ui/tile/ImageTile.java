/**
 * 
 */
package org.vaadin.maps.ui.tile;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.URL;

import org.vaadin.maps.server.ImageResource;
import org.vaadin.maps.shared.ui.tile.ImageTileServerRpc;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;

/**
 * @author morong
 *
 */
@SuppressWarnings("serial")
public class ImageTile extends ProxyTile<ImageResource> {
	
	ImageTileServerRpc rpc = new ImageTileServerRpc() {
		@Override
		public void load() {
			fireEvent(new LoadEvent(ImageTile.this));
		}
		
		@Override
		public void click(MouseEventDetails mouseDetails) {
			fireEvent(new ClickEvent(ImageTile.this, mouseDetails));
		}
	};
	
	public ImageTile() {
		super();
		registerRpc(rpc);
	}
	
	public ImageTile(URL imageURL) {
		super(new ImageResource(imageURL));
		registerRpc(rpc);
	}

	public ImageTile(String imageURL) {
		super(new ImageResource(imageURL));
		registerRpc(rpc);
	}
	
	public void setImageUrl(URL imageURL) {
		setSource(new ImageResource(imageURL));
	}

	public void setImageUrl(String imageURL) {
		setSource(new ImageResource(imageURL));
	}

	/**
	 * Click event. This event is thrown, when the image tile is clicked.
	 * 
	 */
	public class ClickEvent extends Component.Event {

		private final MouseEventDetails details;

		/**
		 * New instance of text change event.
		 * 
		 * @param source
		 *            the Source of the event.
		 */
		public ClickEvent(Component source) {
			super(source);
			details = null;
		}

		/**
		 * Constructor with mouse details
		 * 
		 * @param source
		 *            The source where the click took place
		 * @param details
		 *            Details about the mouse click
		 */
		public ClickEvent(Component source, MouseEventDetails details) {
			super(source);
			this.details = details;
		}

		/**
		 * Returns the mouse position (x coordinate) when the click took place.
		 * The position is relative to the browser client area.
		 * 
		 * @return The mouse cursor x position or -1 if unknown
		 */
		public int getClientX() {
			if (null != details) {
				return details.getClientX();
			} else {
				return -1;
			}
		}

		/**
		 * Returns the mouse position (y coordinate) when the click took place.
		 * The position is relative to the browser client area.
		 * 
		 * @return The mouse cursor y position or -1 if unknown
		 */
		public int getClientY() {
			if (null != details) {
				return details.getClientY();
			} else {
				return -1;
			}
		}

		/**
		 * Gets the ImageTile where the event occurred.
		 * 
		 * @return the Source of the event.
		 */
		public ImageTile getTile() {
			return (ImageTile) getSource();
		}

		/**
		 * Returns the relative mouse position (x coordinate) when the click
		 * took place. The position is relative to the clicked component.
		 * 
		 * @return The mouse cursor x position relative to the clicked layout
		 *         component or -1 if no x coordinate available
		 */
		public int getRelativeX() {
			if (null != details) {
				return details.getRelativeX();
			} else {
				return -1;
			}
		}

		/**
		 * Returns the relative mouse position (y coordinate) when the click
		 * took place. The position is relative to the clicked component.
		 * 
		 * @return The mouse cursor y position relative to the clicked layout
		 *         component or -1 if no y coordinate available
		 */
		public int getRelativeY() {
			if (null != details) {
				return details.getRelativeY();
			} else {
				return -1;
			}
		}

		/**
		 * Checks if the Alt key was down when the mouse event took place.
		 * 
		 * @return true if Alt was down when the event occurred, false otherwise
		 *         or if unknown
		 */
		public boolean isAltKey() {
			if (null != details) {
				return details.isAltKey();
			} else {
				return false;
			}
		}

		/**
		 * Checks if the Ctrl key was down when the mouse event took place.
		 * 
		 * @return true if Ctrl was pressed when the event occurred, false
		 *         otherwise or if unknown
		 */
		public boolean isCtrlKey() {
			if (null != details) {
				return details.isCtrlKey();
			} else {
				return false;
			}
		}

		/**
		 * Checks if the Meta key was down when the mouse event took place.
		 * 
		 * @return true if Meta was pressed when the event occurred, false
		 *         otherwise or if unknown
		 */
		public boolean isMetaKey() {
			if (null != details) {
				return details.isMetaKey();
			} else {
				return false;
			}
		}

		/**
		 * Checks if the Shift key was down when the mouse event took place.
		 * 
		 * @return true if Shift was pressed when the event occurred, false
		 *         otherwise or if unknown
		 */
		public boolean isShiftKey() {
			if (null != details) {
				return details.isShiftKey();
			} else {
				return false;
			}
		}
	}

	/**
	 * Interface for listening for a {@link ClickEvent} fired by a
	 * {@link ImageTile}.
	 * 
	 */
	public interface ClickListener extends Serializable {

		public static final Method TILE_CLICK_METHOD = ReflectTools
				.findMethod(ClickListener.class, "click",
						ClickEvent.class);

		/**
		 * Called when a {@link ImageTile} has been clicked. A reference to the
		 * tile is given by {@link ClickEvent#getTile()}.
		 * 
		 * @param event
		 *            An event containing information about the click.
		 */
		public void click(ClickEvent event);

	}
	
	/**
	 * Adds the tile click listener.
	 * 
	 * @param listener
	 *            the Listener to be added.
	 */
	public void addClickListener(ClickListener listener) {
		addListener(ClickEvent.class, listener,
				ClickListener.TILE_CLICK_METHOD);
	}

	/**
	 * Removes the tile click listener.
	 * 
	 * @param listener
	 *            the Listener to be removed.
	 */
	public void removeClickListener(ClickListener listener) {
		removeListener(ClickEvent.class, listener,
				ClickListener.TILE_CLICK_METHOD);
	}

	/**
	 * Load event. This event is thrown, when the image tile is loaded.
	 * 
	 */
	public class LoadEvent extends Component.Event {
		
		/**
		 * New instance of text change event.
		 * 
		 * @param source
		 *            the Source of the event.
		 */
		public LoadEvent(Component source) {
			super(source);
		}

		/**
		 * Gets the ImageTile where the event occurred.
		 * 
		 * @return the Source of the event.
		 */
		public ImageTile getTile() {
			return (ImageTile) getSource();
		}

	}

	/**
	 * Interface for listening for a {@link LoadEvent} fired by a
	 * {@link ImageTile}.
	 * 
	 */
	public interface LoadListener extends Serializable {

		public static final Method TILE_LOAD_METHOD = ReflectTools
				.findMethod(LoadListener.class, "load",
						LoadEvent.class);

		/**
		 * Called when a {@link ImageTile} has been loaded. A reference to the
		 * tile is given by {@link LoadEvent#getTile()}.
		 * 
		 * @param event
		 *            An event containing information about the click.
		 */
		public void load(LoadEvent event);

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

}
