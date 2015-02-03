/**
 * 
 */
package org.vaadin.maps.ui.handler;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.vaadin.maps.shared.ui.Style;
import org.vaadin.maps.ui.control.Control;
import org.vaadin.maps.ui.feature.VectorFeature;

import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class FeatureHandler extends AbstractHandler implements
		RequiresVectorFeatureLayer {
	
	protected Style featureStyle = null;

	protected FeatureHandler(Control control) {
		super(control);
	}

	public Style getFeatureStyle() {
		return featureStyle;
	}

	public void setFeatureStyle(Style featureStyle) {
		this.featureStyle = featureStyle;
	}

	/**
	 * This event is thrown, when the geometry is drawn.
	 * 
	 */
	public class DrawFeatureEvent extends Component.Event {
		
		public DrawFeatureEvent(PointHandler source, VectorFeature feature) {
			super(source);
			this.feature = feature;
		}

		private VectorFeature feature;
		
		public VectorFeature getFeature() {
			return feature;
		}
	}
	
	/**
	 * Interface for listening for a {@link DrawFeatureEvent} fired by a
	 * {@link PointHandler}.
	 * 
	 */
	public interface DrawFeatureListener extends Serializable {

		public static final Method DRAW_FEATURE_METHOD = ReflectTools
				.findMethod(DrawFeatureListener.class, "drawFeature",
						DrawFeatureEvent.class);

		/**
		 * Called when a feature has been drawn.
		 * 
		 * @param event
		 *            An event containing information about the geometry.
		 */
		public void drawFeature(DrawFeatureEvent event);

	}
	
	/**
	 * Adds the draw feature listener.
	 * 
	 * @param listener
	 *            the Listener to be added.
	 */
	public void addDrawFeatureListener(DrawFeatureListener listener) {
		addListener(DrawFeatureEvent.class, listener,
				DrawFeatureListener.DRAW_FEATURE_METHOD);
	}

	/**
	 * Removes the draw feature listener.
	 * 
	 * @param listener
	 *            the Listener to be removed.
	 */
	public void removeDrawFeatureListener(DrawFeatureListener listener) {
		removeListener(DrawFeatureEvent.class, listener,
				DrawFeatureListener.DRAW_FEATURE_METHOD);
	}

}
