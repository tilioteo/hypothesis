/**
 * 
 */
package org.vaadin.maps.ui.tile;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.vaadin.maps.shared.ui.tile.ImageSequenceTileServerRpc;
import org.vaadin.maps.shared.ui.tile.ImageSequenceTileState;

import com.vaadin.event.ConnectorEventListener;
import com.vaadin.event.MouseEvents;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.server.ResourceReference;
import com.vaadin.shared.EventId;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.URLReference;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class ImageSequenceTile extends AbstractTile {
	
	protected ImageSequenceTileServerRpc rpc = new ImageSequenceTileServerRpc() {
		@Override
		public void load() {
			fireEvent(new LoadEvent(ImageSequenceTile.this));
		}
		
		@Override
		public void error() {
			fireEvent(new ErrorEvent(ImageSequenceTile.this));
		}
		
		@Override
		public void changed(int index) {
			fireEvent(new ChangeEvent(ImageSequenceTile.this, index));
		}

		@Override
		public void click(MouseEventDetails mouseDetails, int index) {
			fireEvent(new ClickEvent(ImageSequenceTile.this, mouseDetails, index));
		}
	};

	private LinkedList<Resource> resources = new LinkedList<Resource>();
	
	public ImageSequenceTile() {
		registerRpc(rpc);
	}

	@Override
	protected ImageSequenceTileState getState() {
		return (ImageSequenceTileState) super.getState();
	}
	
	public void addResource(String url) {
		ExternalResource source = new ExternalResource(url);
		addSource(source);
	}
	
	public void addResource(URL url) {
		ExternalResource source = new ExternalResource(url);
		addSource(source);
	}
	
	protected void addSource(Resource source) {
		resources.add(source);
		
		List<URLReference> sources = getState().sources;
        sources.add(new ResourceReference(source, this, Integer.toString(sources.size())));
        getState().sourceTypes.add(source.getMIMEType());
	}
	
	public void clear() {
		resources.clear();

		getState().sources.clear();
        getState().sourceTypes.clear();
	}
	
	public int getIndex() {
		return getState().index;
	}
	
	public void setIndex(int index) {
		getState().index = index;
	}

	/**
	 * Load event. This event is thrown, when the image sequence is loaded.
	 * 
	 */
	public static class LoadEvent extends Component.Event {
		
		/**
		 * New instance of tile load event.
		 * 
		 * @param source
		 *            the Source of the event.
		 */
		public LoadEvent(ImageSequenceTile source) {
			super(source);
		}

		/**
		 * Gets the ImageSequenceTile where the event occurred.
		 * 
		 * @return the Source of the event.
		 */
		public ImageSequenceTile getTile() {
			return (ImageSequenceTile) getSource();
		}

	}

	/**
	 * Interface for listening for a {@link LoadEvent} fired by a
	 * {@link ImageSequenceTile}.
	 * 
	 */
	public interface LoadListener extends Serializable {

		public static final Method TILE_LOAD_METHOD = ReflectTools
				.findMethod(LoadListener.class, "load",	LoadEvent.class);

		/**
		 * Called when a {@link ImageSequenceTile} has been loaded. A reference to the
		 * tile is given by {@link LoadEvent#getTile()}.
		 * 
		 * @param event
		 *            An event containing information about the click.
		 */
		public void load(LoadEvent event);

	}
	
	/**
	 * Error event. This event is thrown, when the image sequence loading failed.
	 * 
	 */
	public static class ErrorEvent extends Component.Event {
		
		/**
		 * New instance of tile error event.
		 * 
		 * @param source
		 *            the Source of the event.
		 */
		public ErrorEvent(ImageSequenceTile source) {
			super(source);
		}

		/**
		 * Gets the ImageSequenceTile where the event occurred.
		 * 
		 * @return the Source of the event.
		 */
		public ImageSequenceTile getTile() {
			return (ImageSequenceTile) getSource();
		}

	}

	/**
	 * Interface for listening for a {@link ErrorEvent} fired by a
	 * {@link ImageSequenceTile}.
	 * 
	 */
	public interface ErrorListener extends Serializable {

		public static final Method TILE_ERROR_METHOD = ReflectTools
				.findMethod(ErrorListener.class, "error", ErrorEvent.class);

		/**
		 * Called when a {@link ImageSequenceTile} loading failed. A reference to the
		 * tile is given by {@link ErrorEvent#getTile()}.
		 * 
		 * @param event
		 *            An event containing information about the error.
		 */
		public void error(ErrorEvent event);

	}
	
	/**
	 * Change event. This event is thrown, when the image has changed.
	 * 
	 */
	public static class ChangeEvent extends Component.Event {
		
		private int index;
		
		/**
		 * New instance of tile change event.
		 * 
		 * @param source
		 *            the Source of the event.
		 */
		public ChangeEvent(ImageSequenceTile source, int index) {
			super(source);
			this.index = index;
		}

		/**
		 * Gets the ImageSequenceTile where the event occurred.
		 * 
		 * @return the Source of the event.
		 */
		public ImageSequenceTile getTile() {
			return (ImageSequenceTile) getSource();
		}
		
		public int getIndex() {
			return index;
		}

	}

	/**
	 * Interface for listening for a {@link ChangeEvent} fired by a
	 * {@link ImageSequenceTile}.
	 * 
	 */
	public interface ChangeListener extends Serializable {

		public static final Method TILE_CHANGE_METHOD = ReflectTools
				.findMethod(ChangeListener.class, "change", ChangeEvent.class);

		/**
		 * Called when an image of {@link ImageSequenceTile} has been changed. A reference to the
		 * tile is given by {@link ChangeEvent#getTile()}.
		 * 
		 * @param event
		 *            An event containing information about the click.
		 */
		public void change(ChangeEvent event);

	}
	
	public static class ClickEvent extends MouseEvents.ClickEvent {
		
		private int index;

		public ClickEvent(ImageSequenceTile source, MouseEventDetails mouseEventDetails, int index) {
			super(source, mouseEventDetails);
			this.index = index;
		}

		/**
		 * Gets the ImageSequenceTile where the event occurred.
		 * 
		 * @return the Source of the event.
		 */
		public ImageSequenceTile getTile() {
			return (ImageSequenceTile) getSource();
		}
		
		public int getIndex() {
			return index;
		}

	}
	
	public interface ClickListener extends ConnectorEventListener {

        public static final Method clickMethod = ReflectTools.findMethod(
                ClickListener.class, "click", ClickEvent.class);

        /**
         * Called when a {@link Component} has been clicked. A reference to the
         * component is given by {@link ClickEvent#getComponent()}.
         * 
         * @param event
         *            An event containing information about the click.
         */
        public void click(ClickEvent event);
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
	 * Adds the tile change listener.
	 * 
	 * @param listener
	 *            the Listener to be added.
	 */
	public void addChangeListener(ChangeListener listener) {
		addListener(ChangeEvent.class, listener,
				ChangeListener.TILE_CHANGE_METHOD);
	}

	/**
	 * Removes the tile change listener.
	 * 
	 * @param listener
	 *            the Listener to be removed.
	 */
	public void removeChangeListener(ChangeListener listener) {
		removeListener(ChangeEvent.class, listener,
				ChangeListener.TILE_CHANGE_METHOD);
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
