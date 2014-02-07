/**
 * 
 */
package org.vaadin.maps.ui.feature;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.vaadin.maps.geometry.Utils;
import org.vaadin.maps.shared.ui.feature.FeatureServerRpc;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;
import com.vividsolutions.jts.geom.Geometry;


/**
 * @author morong
 *
 */
@SuppressWarnings("serial")
public class VectorFeature extends AbstractFeature {

	private FeatureServerRpc rpc = new FeatureServerRpc() {
		@Override
		public void click(MouseEventDetails mouseDetails) {
			fireEvent(new ClickEvent(VectorFeature.this, mouseDetails));
		}

		@Override
		public void doubleClick(MouseEventDetails mouseDetails) {
			fireEvent(new DoubleClickEvent(VectorFeature.this, mouseDetails));
		}
	};
	
	private Geometry geometry = null;
	
	public VectorFeature() {
		super();
		registerRpc(rpc);
	}
	
	public VectorFeature(Geometry geometry) {
		this();
		setGeometry(geometry);
	}
	
	@Override
	public Geometry getGeometry() {
		return geometry;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
		
		getState().wkb = Utils.geometryToWKBHex(geometry);
	}

	/**
	 * Click event. This event is thrown, when the vector feature is clicked.
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
		 * Gets the VectorFeature where the event occurred.
		 * 
		 * @return the Source of the event.
		 */
		public VectorFeature getFeature() {
			return (VectorFeature) getSource();
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
	 * {@link VectorFeature}.
	 * 
	 */
	public interface ClickListener extends Serializable {

		public static final Method FEATURE_CLICK_METHOD = ReflectTools
				.findMethod(ClickListener.class, "click",
						ClickEvent.class);

		/**
		 * Called when a {@link VectorFeature} has been clicked. A reference to the
		 * feature is given by {@link ClickEvent#getFeature()}.
		 * 
		 * @param event
		 *            An event containing information about the click.
		 */
		public void click(ClickEvent event);

	}
	
	/**
	 * Adds the feature click listener.
	 * 
	 * @param listener
	 *            the Listener to be added.
	 */
	public void addClickListener(ClickListener listener) {
		addListener(ClickEvent.class, listener,
				ClickListener.FEATURE_CLICK_METHOD);
	}

	/**
	 * Removes the feature click listener.
	 * 
	 * @param listener
	 *            the Listener to be removed.
	 */
	public void removeClickListener(ClickListener listener) {
		removeListener(ClickEvent.class, listener,
				ClickListener.FEATURE_CLICK_METHOD);
	}

	public class DoubleClickEvent extends ClickEvent {

		public DoubleClickEvent(Component source) {
			super(source);
		}

		public DoubleClickEvent(Component source, MouseEventDetails details) {
			super(source, details);
		}
		
	}

	/**
	 * Interface for listening for a {@link DoubleClickEvent} fired by a
	 * {@link VectorFeature}.
	 * 
	 */
	public interface DoubleClickListener extends Serializable {

		public static final Method FEATURE_DOUBLE_CLICK_METHOD = ReflectTools
				.findMethod(DoubleClickListener.class, "doubleClick",
						DoubleClickEvent.class);

		/**
		 * Called when a {@link VectorFeature} has been double-clicked. A reference to the
		 * feature is given by {@link DoubleClickEvent#getFeature()}.
		 * 
		 * @param event
		 *            An event containing information about the click.
		 */
		public void doubleClick(DoubleClickEvent event);

	}

	/**
	 * Adds the feature double-click listener.
	 * 
	 * @param listener
	 *            the Listener to be added.
	 */
	public void addDoubleClickListener(DoubleClickListener listener) {
		addListener(DoubleClickEvent.class, listener,
				DoubleClickListener.FEATURE_DOUBLE_CLICK_METHOD);
	}

	/**
	 * Removes the feature double-click listener.
	 * 
	 * @param listener
	 *            the Listener to be removed.
	 */
	public void removeDoubleClickListener(DoubleClickListener listener) {
		removeListener(DoubleClickEvent.class, listener,
				DoubleClickListener.FEATURE_DOUBLE_CLICK_METHOD);
	}

}