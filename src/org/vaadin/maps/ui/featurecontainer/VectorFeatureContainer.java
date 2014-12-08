/**
 * 
 */
package org.vaadin.maps.ui.featurecontainer;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.vaadin.maps.shared.ui.featurecontainer.VectorFeatureContainerServerRpc;
import org.vaadin.maps.ui.feature.VectorFeature;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class VectorFeatureContainer extends AbstractFeatureContainer<VectorFeature> {

	private static Logger log = Logger.getLogger(VectorFeatureContainer.class);

	VectorFeatureContainerServerRpc rpc = new VectorFeatureContainerServerRpc() {
		@Override
		public void click(MouseEventDetails mouseDetails) {
			log.debug("VectorFeatureContainerServerRpc: click()");
			fireEvent(new ClickEvent(VectorFeatureContainer.this, mouseDetails));
		}
	};

	public VectorFeatureContainer() {
		registerRpc(rpc);
	}
	
	/**
	 * Click event. This event is thrown, when the vector feature container is clicked.
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
		 * Gets the VectorFeatureContainer where the event occurred.
		 * 
		 * @return the Source of the event.
		 */
		public VectorFeatureContainer getContainer() {
			return (VectorFeatureContainer) getSource();
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
	 * {@link VectorFeatureContainer}.
	 * 
	 */
	public interface ClickListener extends Serializable {

		public static final Method CONTAINER_CLICK_METHOD = ReflectTools
				.findMethod(ClickListener.class, "click",
						ClickEvent.class);

		/**
		 * Called when a {@link VectorFeatureContainer} has been clicked. A reference to the
		 * container is given by {@link ClickEvent#getContainer()}.
		 * 
		 * @param event
		 *            An event containing information about the click.
		 */
		public void click(ClickEvent event);

	}

	/**
	 * Adds the container click listener.
	 * 
	 * @param listener
	 *            the Listener to be added.
	 */
	public void addClickListener(ClickListener listener) {
		addListener(ClickEvent.class, listener,
				ClickListener.CONTAINER_CLICK_METHOD);
	}

	/**
	 * Removes the container click listener.
	 * 
	 * @param listener
	 *            the Listener to be removed.
	 */
	public void removeClickListener(ClickListener listener) {
		removeListener(ClickEvent.class, listener,
				ClickListener.CONTAINER_CLICK_METHOD);
	}

}
