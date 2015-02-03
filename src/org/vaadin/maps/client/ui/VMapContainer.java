/**
 * 
 */
package org.vaadin.maps.client.ui;

import java.util.HashMap;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author kamil
 *
 */
public class VMapContainer extends VLayerLayout {

	private HashMap<PanEventHandler, HandlerRegistration> panEventHandlerMap = new HashMap<PanEventHandler, HandlerRegistration>();
	private HashMap<ZoomEventHandler, HandlerRegistration> zoomEventHandlerMap = new HashMap<ZoomEventHandler, HandlerRegistration>();

	@Override
	public void onPanEnd(int totalX, int totalY) {
		fireEvent(new PanEvent(this, totalX, totalY));
		
		super.onPanEnd(totalX, totalY);
	}

	
	@Override
	public void onZoom(double zoom) {
		fireEvent(new ZoomEvent(this, zoom));
		
		super.onZoom(zoom);
	}


	public interface PanEventHandler extends EventHandler {
		public void onPanEnd(PanEvent event);
	}
	
	public static class PanEvent extends GwtEvent<PanEventHandler> {

		public static final Type<PanEventHandler> TYPE = new Type<PanEventHandler>();
		
		private int shiftX = 0;
		private int shiftY = 0;
		
		public PanEvent(VMapContainer source, int shiftX, int shiftY) {
			setSource(source);
			this.shiftX = shiftX;
			this.shiftY = shiftY;
		}
		
		public int getShiftX() {
			return shiftX;
		}

		public int getShiftY() {
			return shiftY;
		}

		@Override
		public Type<PanEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(PanEventHandler handler) {
			handler.onPanEnd(this);
		}

	}

	public interface ZoomEventHandler extends EventHandler {
		public void onZoom(ZoomEvent event);
	}
	
	public static class ZoomEvent extends GwtEvent<ZoomEventHandler> {

		public static final Type<ZoomEventHandler> TYPE = new Type<ZoomEventHandler>();
		
		private double zoom = 1.0;
		
		public ZoomEvent(VMapContainer source, double zoom) {
			setSource(source);
			this.zoom = zoom;
		}
		
		public double getZoom() {
			return zoom;
		}

		@Override
		public Type<ZoomEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(ZoomEventHandler handler) {
			handler.onZoom(this);
		}

	}

	public void addPanEventHandler(PanEventHandler handler) {
		panEventHandlerMap.put(handler, addHandler(handler, PanEvent.TYPE));
	}

	public void removePanEventHandler(PanEventHandler handler) {
		if (panEventHandlerMap.containsKey(handler)) {
			removeHandler(panEventHandlerMap.get(handler));
			panEventHandlerMap.remove(handler);
		}
	}

	public void addZoomEventHandler(ZoomEventHandler handler) {
		zoomEventHandlerMap.put(handler, addHandler(handler, ZoomEvent.TYPE));
	}

	public void removeZoomEventHandler(ZoomEventHandler handler) {
		if (zoomEventHandlerMap.containsKey(handler)) {
			removeHandler(zoomEventHandlerMap.get(handler));
			zoomEventHandlerMap.remove(handler);
		}
	}

	protected final void removeHandler(HandlerRegistration handler) {
		if (handler != null) {
			handler.removeHandler();
			handler = null;
		}
	}
	
}
