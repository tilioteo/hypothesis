/**
 * 
 */
package org.vaadin.maps.server;

import java.util.EventListener;
import java.util.EventObject;

import javax.swing.event.EventListenerList;

/**
 * @author kamil
 *
 */
public class ViewWorldTransform {
	
	private EventListenerList eventListeners = new EventListenerList();
	
	private Size view = new Size();
	private Bounds world = new Bounds();
	
	private double viewWorldRatio = 0.0;
	
	public ViewWorldTransform() {
		
	}
	
	public ViewWorldTransform(Size view) {
		if (view != null) {
			this.view = view.clone();
		}
	}
	
	public ViewWorldTransform(int width, int height) {
		view.setWidth(width);
		view.setHeight(height);
	}
	
	/**
	 * Fit world bounds to view size.
	 * World bounds width and/or height is recalculated to aspect view width/height ratio.
	 * 
	 * @param world world bounding box to fit to view size
	 */
	public void fitWorldToView(Bounds world) {
		if (view.isValid()) {
			if (world != null && world.isValid()) {
				this.world = world.clone();

				double xRatio = view.getWidth() / world.getWidth();
				double yRatio = view.getHeight() / world.getHeight();
			
				// new transform ratio
				double newRatio = Math.min(xRatio, yRatio);
				
				// recalculate width and height
				this.world.setWidth(view.getWidth() / newRatio);
				this.world.setHeight(view.getHeight() / newRatio);

				setViewWorldRatio(newRatio);

			} else {
				this.world = new Bounds();
				setViewWorldRatio(0.0);
			}
		}
	}
	
	private void setViewWorldRatio(double value) {
		if (viewWorldRatio != value) {
			viewWorldRatio = value;
			
			fireChangeEvent(new TransformChangeEvent(this));
		}
	}
	
	/**
	 * Change view size and recalculate world bounds from top left corner with current transform ratio.
	 * 
	 * @param width new width
	 * @param height new height
	 */
	public void resizeView(int width, int height) {
		view.setWidth(width);
		view.setHeight(height);

		if (view.isValid()) {
			if (viewWorldRatio != 0) {
				LonLat topLeft = world.getTopLeft();
				LonLat bottomRight = topLeft.add(width / viewWorldRatio, - height / viewWorldRatio);
				world = new Bounds(topLeft.getLon(), bottomRight.getLat(), bottomRight.getLon(), topLeft.getLat());
				/*world.extend(topLeft);
				world.extend(bottomRight);*/
			}
		} else {
			world = new Bounds();
			setViewWorldRatio(0.0);
		}
	}
	
	/**
	 * Recalculate world bounds according to shift from top left view corner.
	 * 
	 * @param shiftX x shift from top left corner in view coordinates
	 * @param shiftY y shift from top left corner in view coordinates 
	 */
	public void shiftWorld(int shiftX, int shiftY) {
		if (world.isValid() && viewWorldRatio != 0) {
			world.shift(shiftX / viewWorldRatio, shiftY / viewWorldRatio);
		}
	}
	
	/**
	 * Recalculate world bounds to change width and height relative to center.
	 * Scale greater than 1 means greater world extent, scale between 0 and 1 (exclusive) causes smaller world extent. 
	 * 
	 * @param scale
	 */
	public void scaleWorld(double scale) {
		if (world.isValid() && view.isValid() && viewWorldRatio != 0) {
			double newRatio = viewWorldRatio / scale;

			world.setWidthCentered(view.getWidth() / newRatio);
			world.setHeightCentered(view.getHeight() / newRatio);

			setViewWorldRatio(newRatio);
		}
	}
	
	/**
	 * Get world coordinate transformed to view coordinate.
	 * 
	 * @param lon world longitude (x) coordinate
	 * @param lat world latitude (y) coordinate
	 * @return view coordinate from top left corner
	 */
	public Pixel worldToView(double lon, double lat) {
		if (world.isValid() && viewWorldRatio != 0) {
			LonLat topLeft = world.getTopLeft();
			return new Pixel(
					(int)Math.round((lon - topLeft.getLon()) * viewWorldRatio),
					(int)Math.round((topLeft.getLat() - lat) * viewWorldRatio));
		}
		return null;
	}

	/**
	 * Get world coordinate transformed to view coordinate.
	 * 
	 * @param lonLat world coordinate
	 * @return view coordinates from top left corner
	 */
	public Pixel worldToView(LonLat lonLat) {
		if (lonLat != null) {
			return worldToView(lonLat.getLon(), lonLat.getLat());
		}
		return null;
	}

	/**
	 * Get view coordinate transformed to world coordinate.
	 * 
	 * @param x view x coordinate from top left corner
	 * @param y view y coordinate from top left corner
	 * @return world coordinate
	 */
	public LonLat viewToWorld(double x, double y) {
		if (world.isValid() && viewWorldRatio != 0) {
			return world.getTopLeft().add(x / viewWorldRatio, - y / viewWorldRatio);
		}
		return null;
	}
	
	/**
	 * Get view coordinate transformed to world coordinate.
	 * 
	 * @param pixel view coordinates from top left corner
	 * @return world coordinate
	 */
	public LonLat viewToWorld(Pixel pixel) {
		if (pixel != null) {
			return viewToWorld(pixel.getX(), pixel.getY());
		}
		return null;
	}
	
	public Size getView() {
		return view;
	}
	
	public Bounds getWorld() {
		return world;
	}
	
	public double getViewWorldRatio() {
		return viewWorldRatio;
	}

	@SuppressWarnings("serial")
	public static class TransformChangeEvent extends EventObject {
		
		public TransformChangeEvent(ViewWorldTransform source) {
			super(source);
		}
		
		public ViewWorldTransform getViewWorldTransform() {
			return (ViewWorldTransform) getSource();
		}
		
		public double getViewWorldRatio() {
			return getViewWorldTransform().getViewWorldRatio();
		}
	}
	
	public interface TransformChangeListener extends EventListener {
		void onTransformChange(TransformChangeEvent event);
	}

	public void addTransformChangeListener(TransformChangeListener listener) {
		eventListeners.add(TransformChangeListener.class, listener);
	}

	public void removeTransformChangeListener(TransformChangeListener listener) {
		eventListeners.remove(TransformChangeListener.class, listener);
	}
	
	protected void fireChangeEvent(TransformChangeEvent event) {
		Object[] listeners = eventListeners.getListenerList();

		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == TransformChangeListener.class) {
				((TransformChangeListener) listeners[i + 1]).onTransformChange(event);
			}
		}
	}
	
}
