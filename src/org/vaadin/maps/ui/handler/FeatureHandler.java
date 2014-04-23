/**
 * 
 */
package org.vaadin.maps.ui.handler;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.vaadin.maps.ui.control.Control;

import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;
import com.vividsolutions.jts.geom.Geometry;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class FeatureHandler extends AbstractHandler implements
		RequiresVectorFeatureLayer {

	protected FeatureHandler(Control control) {
		super(control);
	}

	/**
	 * This event is thrown, when the geometry is drawn.
	 * 
	 */
	public class GeometryEvent extends Component.Event {
		
		public GeometryEvent(PointHandler source, Geometry geometry) {
			super(source);
			this.geometry = geometry;
		}

		private Geometry geometry;
		
		public Geometry getGeometry() {
			return geometry;
		}
	}
	
	/**
	 * Interface for listening for a {@link GeometryEvent} fired by a
	 * {@link PointHandler}.
	 * 
	 */
	public interface GeometryListener extends Serializable {

		public static final Method GEOMETRY_METHOD = ReflectTools
				.findMethod(GeometryListener.class, "geometry",
						GeometryEvent.class);

		/**
		 * Called when a geometry has been drawn.
		 * 
		 * @param event
		 *            An event containing information about the geometry.
		 */
		public void geometry(GeometryEvent event);

	}
	
	/**
	 * Adds the geometry listener.
	 * 
	 * @param listener
	 *            the Listener to be added.
	 */
	public void addGeometryListener(GeometryListener listener) {
		addListener(GeometryEvent.class, listener,
				GeometryListener.GEOMETRY_METHOD);
	}

	/**
	 * Removes the geometry listener.
	 * 
	 * @param listener
	 *            the Listener to be removed.
	 */
	public void removeGeometryListener(GeometryListener listener) {
		removeListener(GeometryEvent.class, listener,
				GeometryListener.GEOMETRY_METHOD);
	}

}
