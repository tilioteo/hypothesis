/**
 * 
 */
package org.vaadin.maps.client.ui;

import java.util.HashMap;

import org.vaadin.gwtgraphics.client.shape.Circle;
import org.vaadin.maps.client.drawing.Utils;
import org.vaadin.maps.client.geometry.Coordinate;
import org.vaadin.maps.client.geometry.Geometry;
import org.vaadin.maps.client.geometry.Point;
import org.vaadin.maps.shared.ui.Style;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseEvent;
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
	
	public ClickHandler clickHandlerSlave; 
	
	protected VVectorFeatureLayer layer = null;
	protected VVectorFeatureContainer container = null;
	
	protected Style cursorStyle = Style.DEFAULT_DRAW_CURSOR;
	
	/**
	 * point of mouse cursor position
	 * TODO make implementation independent
	 */
	private Circle cursor = null;
	
	protected HandlerRegistration clickHandler = null;
	protected HandlerRegistration mouseMoveHandler = null;
	
	private HashMap<GeometryEventHandler, HandlerRegistration> geometryHandlerMap = new HashMap<GeometryEventHandler, HandlerRegistration>();

	public static int[] getMouseEventXY(MouseEvent<?> event) {
		// Firefox encounters some problems with relative position to SVG element
		// correct xy position is obtained using the parent DIV element of vector layer 
		Element relative = event.getRelativeElement();
		if (relative != null) {
			Element parent = relative.getParentElement();
			if (parent != null) {
				return new int[] { event.getRelativeX(parent), event.getRelativeY(parent) };
			}
		}
		return new int[] { event.getX(), event.getY() };
	}
	

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
	
	private void addCursor() {
		//cursor = new Donut(0, 0, 0, 0);
		cursor = new Circle(0, 0, 0);
		updateCursorStyle();
		container.add(cursor);
	}
	
	private void updateCursorStyle() {
		if (cursor != null && cursorStyle != null) {
			Utils.updateDrawingStyle(cursor, cursorStyle);
		}
	}

	private void removeCursor() {
		container.remove(cursor);
		cursor = null;
	}
	
	private void updateCursorPosition(int[] xy) {
		cursor.setX(xy[0]);
		cursor.setY(xy[1]);
	}
	
	@Override
	public void activate() {
		super.activate();
		
		addCursor();
	}
	
	/**
	 * Create a coordinate recalculated from display view units to world units
	 * @param x
	 * @param y
	 * @return  new {@link Coordinate} 
	 */
	public Coordinate createWorldCoordinate(int[] xy) {
		// TODO implement
		return new Coordinate(xy[0], xy[1]);
	}
	
	@Override
	public void deactivate() {
		removeCursor();
		
		super.deactivate();
	}
	
	public Style getCursorStyle() {
		return cursorStyle;
	}

	public void setCursorStyle(Style style) {
		if (style != null) {
			this.cursorStyle = style;
		} else {
			this.cursorStyle = Style.DEFAULT_DRAW_CURSOR;
		}
		
		updateCursorStyle();
	}

	@Override
	public void onClick(ClickEvent event) {
		if (!active) {
			return;
		}
		
		if (clickHandlerSlave != null) {
			clickHandlerSlave.onClick(event);
		}
		
		int[] xy = getMouseEventXY(event);
		Point point = new Point(createWorldCoordinate(xy));
		fireEvent(new GeometryEvent(VPointHandler.this, point));
  	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if (!active) {
			return;
		}
		
		int[] xy = getMouseEventXY(event);

		// redraw cursor point
		// TODO make implementation independent
		updateCursorPosition(xy);
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
