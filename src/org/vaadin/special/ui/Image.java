/**
 * 
 */
package org.vaadin.special.ui;

import java.lang.reflect.Method;

import org.vaadin.special.event.ComponentEvent;
import org.vaadin.special.event.MouseEvents.ClickEvent;
import org.vaadin.special.event.MouseEvents.ClickListener;
import org.vaadin.special.shared.EventId;
import org.vaadin.special.shared.ui.image.ImageServerRpc;

import com.vaadin.event.ConnectorEventListener;
import com.vaadin.server.Resource;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.image.ImageState;
import com.vaadin.ui.AbstractEmbedded;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class Image extends AbstractEmbedded {

	protected ImageServerRpc rpc = new ImageServerRpc() {
		@Override
		public void click(long timestamp, MouseEventDetails mouseDetails) {
			fireEvent(new ClickEvent(timestamp, Image.this, mouseDetails));
		}

		@Override
		public void load(long timestamp) {
			fireEvent(new LoadEvent(timestamp, Image.this));
		}

		@Override
		public void error(long timestamp) {
			fireEvent(new ErrorEvent(timestamp, Image.this));
		}
	};

	/**
	 * Creates a new empty Image.
	 */
	public Image() {
		registerRpc(rpc);
	}

	/**
	 * Creates a new empty Image with caption.
	 * 
	 * @param caption
	 */
	public Image(String caption) {
		this();
		setCaption(caption);
	}

	/**
	 * Creates a new Image whose contents is loaded from given resource. The
	 * dimensions are assumed if possible. The type is guessed from resource.
	 * 
	 * @param caption
	 * @param source
	 *            the Source of the embedded object.
	 */
	public Image(String caption, Resource source) {
		this(caption);
		setSource(source);
	}

	@Override
	protected ImageState getState() {
		return (ImageState) super.getState();
	}

	/**
	 * Class for holding information about a image load event. A
	 * {@link LoadEvent} is fired when the <code>Image</code> is successfully
	 * loaded.
	 * 
	 * @author kamil.
	 * @see LoadListener
	 */
	public static class LoadEvent extends ComponentEvent {

		public LoadEvent(long timestamp, Component source) {
			super(timestamp, source);
		}

	}

	/**
	 * Interface for listening for a {@link LoadEvent} fired by a {@link Image}.
	 * 
	 * @see LoadEvent
	 * @author kamil
	 */
	public interface LoadListener extends ConnectorEventListener {

		public static final Method loadMethod = ReflectTools.findMethod(
				LoadListener.class, EventId.LOAD_EVENT_IDENTIFIER, LoadEvent.class);

		/**
		 * Called when a {@link Image} has been successfully loaded. A reference
		 * to the component is given by {@link LoadEvent#getComponent()}.
		 * 
		 * @param event
		 *            An event containing information about the image.
		 */
		public void load(LoadEvent event);
	}

	/**
	 * Class for holding information about a image error event. An
	 * {@link ErrorEvent} is fired when the <code>Image</code> loading fails.
	 * 
	 * @author kamil
	 * @see ErrorListener
	 */
	public static class ErrorEvent extends ComponentEvent {

		public ErrorEvent(long timestamp, Component source) {
			super(timestamp, source);
		}

	}

	/**
	 * Interface for listening for a {@link ErrorEvent} fired by a {@link Image}
	 * 
	 * @see ErrorEvent
	 * @author kamil
	 */
	public interface ErrorListener extends ConnectorEventListener {

		public static final Method errorMethod = ReflectTools.findMethod(
				ErrorListener.class, EventId.ERROR_EVENT_IDENTIFIER, ErrorEvent.class);

		/**
		 * Called when a {@link Image} loading failed. A reference to the
		 * component is given by {@link ErrorEvent#getComponent()}.
		 * 
		 * @param event
		 *            An event containing information about the image.
		 */
		public void error(ErrorEvent event);
	}

	/**
	 * Add a load listener to the component. The listener is called when the
	 * image is successfully loaded.
	 * 
	 * Use {@link #removeLoadListener(LoadListener)} to remove the listener.
	 * 
	 * @param listener
	 *            The listener to add
	 */
	public void addLoadListener(LoadListener listener) {
		addListener(EventId.LOAD_EVENT_IDENTIFIER, LoadEvent.class, listener, LoadListener.loadMethod);
	}

	/**
	 * Remove a load listener from the component. The listener should earlier
	 * have been added using {@link #addLoadListener(LoadListener)}.
	 * 
	 * @param listener
	 *            The listener to remove
	 */
	public void removeLoadListener(LoadListener listener) {
		removeListener(EventId.LOAD_EVENT_IDENTIFIER, LoadEvent.class, listener);
	}

	/**
	 * Add an error listener to the component. The listener is called when the
	 * image loading failed.
	 * 
	 * Use {@link #removeErrorListener(ErrorListener)} to remove the listener.
	 * 
	 * @param listener
	 *            The listener to add
	 */
	public void addErrorListener(ErrorListener listener) {
		addListener(EventId.ERROR_EVENT_IDENTIFIER, ErrorEvent.class, listener, ErrorListener.errorMethod);
	}

	/**
	 * Remove an error listener from the component. The listener should earlier
	 * have been added using {@link #addErrorListener(ErrorListener)}.
	 * 
	 * @param listener
	 *            The listener to remove
	 */
	public void removeErrorListener(ErrorListener listener) {
		removeListener(EventId.ERROR_EVENT_IDENTIFIER, ErrorEvent.class, listener);
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
	}
}
