/**
 * 
 */
package org.vaadin.maps.ui.handler;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.vaadin.maps.geometry.Utils;
import org.vaadin.maps.shared.ui.handler.PointHandlerServerRpc;
import org.vaadin.maps.shared.ui.handler.PointHandlerState;
import org.vaadin.maps.ui.control.Control;
import org.vaadin.maps.ui.feature.VectorFeature;
import org.vaadin.maps.ui.layer.VectorFeatureLayer;

import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
@SuppressWarnings("serial")
public class PointHandler extends FeatureHandler {

	private static Logger log = Logger.getLogger(PointHandler.class);

	private PointHandlerServerRpc rpc = new PointHandlerServerRpc() {

		@Override
		public void click(double x, double y, String buttonName, boolean altKey, boolean ctrlKey,
				boolean metaKey, boolean shiftKey, boolean doubleClick) {
			log.debug("PointHandlerServerRpc: click()");
			fireEvent(new ClickEvent(PointHandler.this, new Coordinate(x, y), buttonName, altKey,
					ctrlKey, metaKey, shiftKey, doubleClick));
		}

		@Override
		public void geometry(String wkb) {
			try {
				Geometry geometry = Utils.wkbHexToGeometry(wkb);
				if (layer != null && layer.getForLayer() != null) {
					Utils.transformViewToWorld(geometry, layer.getForLayer().getViewWorldTransform());
				}
				VectorFeature feature = addNewFeature(geometry);
				fireEvent(new DrawFeatureEvent(PointHandler.this, feature));
				
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	};
	
	/**
	 * The last drawn feature
	 */
	protected VectorFeature feature = null;
	
	/**
	 * The drawing layer
	 */
	protected VectorFeatureLayer layer = null;
	
	public PointHandler(Control control) {
		super(control);
		registerRpc(rpc);
	}
	
	protected VectorFeature addNewFeature(Geometry geometry) {
		if (geometry != null) {
			feature = new VectorFeature(geometry);
			feature.setStyle(featureStyle);
			layer.addComponent(feature);
			return feature;
		}
		return null;
	}

	@Override
	protected PointHandlerState getState() {
		return (PointHandlerState) super.getState();
	}

	@Override
	public void setLayer(VectorFeatureLayer layer) {
		this.layer = layer;
		getState().layer = layer;
	}
	
	@Override
	public boolean deactivate() {
		if (!super.deactivate())
			return false;
		
		cancel();
		
		return true;
	}
	
	@Override
	public void cancel() {
		super.cancel();
	}
	
	/**
	 * Click event. This event is thrown, when the drawing layer is clicked.
	 * 
	 */
	public class ClickEvent extends Component.Event {
		
		private Coordinate coordinate;
		private String buttonName;
		private boolean altKey;
		private boolean ctrlKey;
		private boolean metaKey;
		private boolean shiftKey;
		private boolean doubleClick;

		/**
		 * Constructor with details
		 * 
		 * @param source
		 *            The source where the click took place
		 */
		public ClickEvent(Component source, Coordinate coordinate, String buttonName, boolean altKey, boolean ctrlKey, boolean metaKey, boolean shiftKey, boolean doubleClick) {
			super(source);
			this.coordinate = coordinate;
			this.buttonName = buttonName;
			this.altKey = altKey;
			this.ctrlKey = ctrlKey;
			this.metaKey = metaKey;
			this.shiftKey = shiftKey;
			this.doubleClick = doubleClick;
		}

		/**
		 * Returns the coordinate when the click took place.
		 * The position is in coordinating system of layer.
		 * 
		 * @return The position coordinate
		 */
		public Coordinate getCoordinate() {
			return coordinate;
		}
		
		public String getButtonName() {
			return buttonName;
		}

		/**
		 * Checks if the Alt key was down when the mouse event took place.
		 * 
		 * @return true if Alt was down when the event occurred, false otherwise
		 *         or if unknown
		 */
		public boolean isAltKey() {
			return altKey;
		}

		/**
		 * Checks if the Ctrl key was down when the mouse event took place.
		 * 
		 * @return true if Ctrl was pressed when the event occurred, false
		 *         otherwise or if unknown
		 */
		public boolean isCtrlKey() {
			return ctrlKey;
		}

		/**
		 * Checks if the Meta key was down when the mouse event took place.
		 * 
		 * @return true if Meta was pressed when the event occurred, false
		 *         otherwise or if unknown
		 */
		public boolean isMetaKey() {
			return metaKey;
		}

		/**
		 * Checks if the Shift key was down when the mouse event took place.
		 * 
		 * @return true if Shift was pressed when the event occurred, false
		 *         otherwise or if unknown
		 */
		public boolean isShiftKey() {
			return shiftKey;
		}
		
		public boolean isDoubleClick() {
			return doubleClick;
		}
	}

	/**
	 * Interface for listening for a {@link ClickEvent} fired by a
	 * {@link PointHandler}.
	 * 
	 */
	public interface ClickListener extends Serializable {

		public static final Method CLICK_METHOD = ReflectTools
				.findMethod(ClickListener.class, "click",
						ClickEvent.class);

		/**
		 * Called when a drawing layer has been clicked.
		 * 
		 * @param event
		 *            An event containing information about the click.
		 */
		public void click(ClickEvent event);

	}
	
	/**
	 * Adds the click listener.
	 * 
	 * @param listener
	 *            the Listener to be added.
	 */
	public void addClickListener(ClickListener listener) {
		addListener(ClickEvent.class, listener,
				ClickListener.CLICK_METHOD);
	}

	/**
	 * Removes the click listener.
	 * 
	 * @param listener
	 *            the Listener to be removed.
	 */
	public void removeClickListener(ClickListener listener) {
		removeListener(ClickEvent.class, listener,
				ClickListener.CLICK_METHOD);
	}

}
                