/**
 * 
 */
package org.vaadin.maps.ui;

import java.util.HashMap;

import org.vaadin.maps.server.Bounds;
import org.vaadin.maps.server.ViewWorldTransform;
import org.vaadin.maps.server.ViewWorldTransform.TransformChangeListener;
import org.vaadin.maps.shared.ui.Style;
import org.vaadin.maps.shared.ui.mapcontainer.MapContainerRpc;
import org.vaadin.maps.ui.control.AbstractControl;
import org.vaadin.maps.ui.layer.AbstractLayer;
import org.vaadin.maps.ui.layer.ControlLayer;
import org.vaadin.maps.ui.layer.ForLayer;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class MapContainer extends LayerLayout implements ForLayer {

	private HashMap<String, Style> styles = new HashMap<String, Style>();
	
	private ControlLayer controlLayer = null;
	
	private String crs = null;
	
	/**
	 * initial bounds
	 */
	private Bounds bounds = new Bounds();
	
	private ViewWorldTransform transform = new ViewWorldTransform();
	
	private MapContainerRpc rpc = new MapContainerRpc() {
		@Override
		public void panEnd(int shiftX, int shiftY) {
			transform.shiftWorld(-shiftX, shiftY);
		}

		@Override
		public void zoom(double zoom) {
			transform.scaleWorld(zoom);
		}
	};
	
	public MapContainer() {
		super();
		
		registerRpc(rpc);
		// Add default control layer
		controlLayer = new ControlLayer();
		addComponent(controlLayer);
	}
	
	public void addStyle(String id, Style style) {
		if (id != null && style != null) {
			styles.put(id, style);
		}
	}
	
	public void removeStyle(Style style) {
		styles.remove(style);
		//TODO remove styling from objects
	}
	
	public Style getStyle(String id) {
		return styles.get(id);
	}
	
	public void addLayer(AbstractLayer<?> layer) {
		if (layer != null) {
			addComponent(layer);
			layer.setForLayer(this);
		}
	}

	public void removeLayer(AbstractLayer<?> layer) {
		if (layer != null) {
			removeComponent(layer);
			layer.setForLayer(null);
		}
	}

	public void addControl(AbstractControl control) {
		controlLayer.addComponent(control);
	}
	
	public void removeControl(AbstractControl control) {
		controlLayer.removeComponent(control);
	}


	@Override
	public String getCRS() {
		return crs;
	}

	public void setCRS(String crs) {
		if (crs != null && !crs.trim().isEmpty() && CRSUtility.checkCRS(crs.trim())) {
			this.crs = crs;
		} else {
			this.crs = null;
		}
	}

	public Bounds getBounds() {
		return bounds;
	}

	public void setBounds(Bounds bounds) {
		if (bounds != null) {
			this.bounds = bounds;
		} else {
			this.bounds = new Bounds(0, -getMeasuredHeight(), getMeasuredWidth(), 0);
		}
		
		transform.fitWorldToView(this.bounds);
	}

	@Override
	public Bounds getExtent() {
		return transform.getWorld();
	}

	@Override
	protected void updateMeasuredSize(int newWidth, int newHeight) {
		boolean fit = false;
		if (!transform.getView().isValid()) {
			fit = true;
		}
		transform.resizeView(newWidth, newHeight);

		if (!bounds.isValid()) {
			bounds = new Bounds(0, -newHeight, newWidth, 0);
			fit = true;
		}
		
		if (fit) {
			transform.fitWorldToView(bounds);
		}
		
		super.updateMeasuredSize(newWidth, newHeight);
	}

	@Override
	public ViewWorldTransform getViewWorldTransform() {
		return transform;
	}

	@Override
	public void addTransformChangeListener(TransformChangeListener listener) {
		transform.addTransformChangeListener(listener);
	}

	@Override
	public void removeTransformChangeListener(TransformChangeListener listener) {
		transform.removeTransformChangeListener(listener);
	}

}
