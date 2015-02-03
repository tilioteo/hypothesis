/**
 * 
 */
package org.vaadin.maps.client.ui;

import java.util.HashMap;

import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author kamil
 *
 */
public class VZoomHandler extends AbstractNavigateHandler implements HasLayerLayout, MouseWheelHandler {

	public static final String CLASSNAME = "v-zoomhandler";
	
	protected VLayerLayout layout = null;
	
	protected HandlerRegistration mouseWheelHandler = null;

	private HashMap<ZoomEventHandler, HandlerRegistration> zoomHandlerMap = new HashMap<ZoomEventHandler, HandlerRegistration>();

	public VZoomHandler() {
		super();
		setStyleName(CLASSNAME);
	}
	
	@Override
	public void setLayout(VLayerLayout layout) {
		if (this.layout == layout) {
			return;
		}
		
		finalize();
		this.layout = layout;
		initialize();
	}
	
	@Override
	public void onMouseWheel(MouseWheelEvent event) {
		if (!active) {
			return;
		}
		
		double zoom = 1.0;
		int deltaY = event.getDeltaY();
		if (deltaY < 0) {
			zoom = 0.8;
		} else if (deltaY > 0) {
			zoom = 1.25;
		}
		
		if (zoom != 1 && layout != null) {
			layout.onZoom(zoom);
		}
		
		event.preventDefault();
	}

	@Override
	protected void initialize() {
		if (layout != null) {
			mouseWheelHandler = layout.addMouseWheelHandler(this);
		}
	}

	@Override
	protected void finalize() {
		if (layout != null && mouseWheelHandler != null) {
			mouseWheelHandler.removeHandler();
			mouseWheelHandler = null;
		}
	}

	public interface ZoomEventHandler extends EventHandler {
		void zoom(ZoomEvent event);
	}

	public static class ZoomEvent extends GwtEvent<ZoomEventHandler> {

		public static final Type<ZoomEventHandler> TYPE = new Type<ZoomEventHandler>();
		
		private double zoom;
		
		public ZoomEvent(VZoomHandler source, double zoom) {
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
			handler.zoom(this);
		}
	}

	public void addZoomEventHandler(ZoomEventHandler handler) {
		zoomHandlerMap.put(handler, addHandler(handler, ZoomEvent.TYPE));
	}

	public void removeZoomEventHandler(ZoomEventHandler handler) {
		if (zoomHandlerMap.containsKey(handler)) {
			removeEventHandler(zoomHandlerMap.get(handler));
			zoomHandlerMap.remove(handler);
		}
	}

}
