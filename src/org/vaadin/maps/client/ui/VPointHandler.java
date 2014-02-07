/**
 * 
 */
package org.vaadin.maps.client.ui;

import java.util.HashMap;

import org.vaadin.gwtgraphics.client.shape.Circle;
import org.vaadin.maps.client.geometry.Coordinate;
import org.vaadin.maps.client.geometry.Geometry;
import org.vaadin.maps.client.geometry.Point;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author kamil
 *
 */
public class VPointHandler extends AbstractHandler implements ClickHandler, MouseMoveHandler {

	public static final String CLASSNAME = "v-pointhandler";
	
	public static final int CURSOR_CIRCLE_RADIUS = 5;
	public static final String CURSOR_CIRCLE_STROKE_COLOR = "blue";
	public static final String CURSOR_CIRCLE_FILL_COLOR = "cyan";
	public static final double CURSOR_CIRCLE_FILL_OPACITY = 0.3;
	
	protected VVectorFeatureLayer layer = null;
	protected VVectorFeatureContainer container = null;
	
	/**
	 * point of mouse cursor position
	 * TODO make implementation independent
	 */
	private Circle cursorPoint = null;
	
	protected HandlerRegistration clickHandler = null;
	protected HandlerRegistration mouseMoveHandler = null;
	
	private HashMap<GeometryEventHandler, HandlerRegistration> geometryHandlerMap = new HashMap<GeometryEventHandler, HandlerRegistration>();
	
	public VPointHandler() {
		super();
		setStyleName(CLASSNAME);
	}
	
	public void setLayer(VVectorFeatureLayer layer) {
		if (this.layer == layer) {
			return;
		}
		
		finalize();
		this.layer = layer;
		initialize();
	}
	
	/* (non-Javadoc)
	 * @see org.vaadin.maps.client.ui.AbstractHandler#initialize()
	 */
	@Override
	protected void initialize() {
		if (layer != null && layer.getWidget() != null && layer.getWidget() instanceof VVectorFeatureContainer) {
			container = (VVectorFeatureContainer) layer.getWidget();
			ensureContainerHandlers();
		} else {
			container = null;
		}
	}

	/* (non-Javadoc)
	 * @see org.vaadin.maps.client.ui.AbstractHandler#finalize()
	 */
	@Override
	protected void finalize() {
		if (container != null) {
			removeContainerHandlers();
			container = null;
		}
	}

	protected void ensureContainerHandlers() {
		clickHandler = container.addClickHandler(this);
		mouseMoveHandler = container.addMouseMoveHandler(this);
	}
	
	protected final void removeHandler(HandlerRegistration handler) {
		if (handler != null) {
			handler.removeHandler();
			handler = null;
		}
	}

	protected void removeContainerHandlers() {
		removeHandler(clickHandler);
		removeHandler(mouseMoveHandler);
	}
	
	private void addCursorPoint() {
		cursorPoint = new Circle(0, 0, CURSOR_CIRCLE_RADIUS);
		cursorPoint.setStrokeColor(CURSOR_CIRCLE_STROKE_COLOR);
		cursorPoint.setFillColor(CURSOR_CIRCLE_FILL_COLOR);
		cursorPoint.setFillOpacity(CURSOR_CIRCLE_FILL_OPACITY);
		container.add(cursorPoint);
	}
	
	private void removeCursorPoint() {
		container.remove(cursorPoint);
		cursorPoint = null;
	}
	
	@Override
	public void activate() {
		super.activate();
		
		addCursorPoint();
	}
	
	/**
	 * Create a coordinate recalculated from display view units to world units
	 * @param x
	 * @param y
	 * @return  new {@link Coordinate} 
	 */
	protected Coordinate createWorldCoordinate(int x, int y) {
		// TODO implement
		return new Coordinate(x, y);
	}
	
	@Override
	public void deactivate() {
		removeCursorPoint();
		
		super.deactivate();
	}
	
	@Override
	public void onClick(ClickEvent event) {
		if (!active) {
			return;
		}
		
		Point point = new Point(createWorldCoordinate(event.getX(), event.getY()));
		fireEvent(new GeometryEvent(VPointHandler.this, point));
  	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if (!active) {
			return;
		}
		
		// redraw cursor point
		// TODO make implementation independent
		cursorPoint.setX(Math.round((float)event.getX()));
		cursorPoint.setY(Math.round((float)event.getY()));
	}
	
	public interface GeometryEventHandler extends EventHandler {
		void geometry(GeometryEvent event);
	}

	public static class GeometryEvent extends GwtEvent<GeometryEventHandler> {

		public static final Type<GeometryEventHandler> TYPE = new Type<GeometryEventHandler>();
		
		private Geometry geometry;

		public GeometryEvent(VPointHandler source, Geometry geometry) {
			setSource(source);
			this.geometry = geometry;
		}
		
		public Geometry getGeometry() {
			return geometry;
		}


		@Override
		public Type<GeometryEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GeometryEventHandler handler) {
			handler.geometry(this);
		}
	}

	public void addGeometryEventHandler(GeometryEventHandler handler) {
		geometryHandlerMap.put(handler, addHandler(handler, GeometryEvent.TYPE));
	}

	public void removeGeometryEventHandler(GeometryEventHandler handler) {
		if (geometryHandlerMap.containsKey(handler)) {
			removeHandler(geometryHandlerMap.get(handler));
			geometryHandlerMap.remove(handler);
		}
	}

}
