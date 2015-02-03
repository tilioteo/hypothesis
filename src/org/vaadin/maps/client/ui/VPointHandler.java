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
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.vaadin.client.MouseEventDetailsBuilder;
import com.vaadin.shared.MouseEventDetails;

/**
 * @author kamil
 *
 */
public class VPointHandler extends AbstractDrawFeatureHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler, CanShift {

	public static final String CLASSNAME = "v-pointhandler";
	
	public SyntheticClickHandler clickHandlerSlave; 
	
	protected VVectorFeatureLayer layer = null;
	protected VVectorFeatureContainer container = null;
	
	private int lastShiftX = 0;
	private int lastShiftY = 0;
	
	/**
	 * point of mouse cursor position
	 * TODO make implementation independent
	 */
	private Circle cursor = null;
	
	private boolean mouseDown = false;
	private boolean mouseMoved = false;
	protected MouseEventDetails mouseEventDetails = null;
	protected Element eventElement = null;
	
	//protected HandlerRegistration clickHandler = null;
	protected HandlerRegistration mouseDownHandler = null;
	protected HandlerRegistration mouseUpHandler = null;
	protected HandlerRegistration mouseMoveHandler = null;
	
	private HashMap<GeometryEventHandler, HandlerRegistration> geometryHandlerMap = new HashMap<GeometryEventHandler, HandlerRegistration>();

	public static int[] getMouseEventXY(MouseEvent<?> event) {
		return getMouseEventXY(MouseEventDetailsBuilder.buildMouseEventDetails(event.getNativeEvent(), event.getRelativeElement()), event.getRelativeElement());
	}
	
	public static int[] getMouseEventXY(MouseEventDetails details, Element relativeElement) {
		// Firefox encounters some problems with relative position to SVG element
		// correct xy position is obtained using the parent DIV element of vector layer 
		if (relativeElement != null) {
			Element parent = relativeElement.getParentElement();
			if (parent != null) {
				return new int[] {
						details.getClientX() - parent.getAbsoluteLeft() + parent.getScrollLeft() + parent.getOwnerDocument().getScrollLeft(),
						details.getClientY() - parent.getAbsoluteTop() + parent.getScrollTop() + parent.getOwnerDocument().getScrollTop() };
			}
		}
		return new int[] { details.getClientX(), details.getClientY() };
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
			container.setCanShiftSlave(this);
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
			container.setCanShiftSlave(null);
			removeContainerHandlers();
			container = null;
		}
	}

	protected void ensureContainerHandlers() {
		//clickHandler = container.addClickHandler(this);
		mouseDownHandler = container.addMouseDownHandler(this);
		mouseUpHandler = container.addMouseUpHandler(this);
		mouseMoveHandler = container.addMouseMoveHandler(this);
	}
	
	protected void removeContainerHandlers() {
		//removeEventHandler(clickHandler);
		removeEventHandler(mouseDownHandler);
		removeEventHandler(mouseUpHandler);
		removeEventHandler(mouseMoveHandler);
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
	 * Create a coordinate from array.
	 * <strong>Note:</strong> World coordinates are recalculated on server side.
	 * @param xy
	 * @return  new {@link Coordinate} 
	 */
	public Coordinate createCoordinate(int[] xy) {
		return new Coordinate(xy[0], xy[1]);
	}
	
	@Override
	public void deactivate() {
		removeCursor();
		
		super.deactivate();
	}
	
	@Override
	public void setCursorStyle(Style style) {
		super.setCursorStyle(style);
		
		updateCursorStyle();
	}

	/*@Override
	public void onClick(ClickEvent event) {
		if (!active) {
			return;
		}
		
		if (clickHandlerSlave != null) {
			clickHandlerSlave.onClick(event);
		}
		
		int[] xy = getMouseEventXY(event);
		Point point = new Point(createCoordinate(xy));
		fireEvent(new GeometryEvent(VPointHandler.this, point));
  	}*/
	
	protected void syntheticClick(MouseEventDetails details, Element relativeElement) {
		if (clickHandlerSlave != null) {
			clickHandlerSlave.syntheticClick(details, relativeElement);
		}
		
		int[] xy = getMouseEventXY(details, relativeElement);
		Point point = new Point(createCoordinate(xy));
		fireEvent(new GeometryEvent(VPointHandler.this, point));
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		if (!active) {
			return;
		}
		
		mouseDown = true;
		mouseEventDetails = MouseEventDetailsBuilder.buildMouseEventDetails(event.getNativeEvent(), event.getRelativeElement());
		mouseEventDetails.setType(Event.getTypeInt("click"));
		eventElement = event.getRelativeElement();
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		if (!active) {
			return;
		}
		
		if (!mouseMoved) {
			syntheticClick(mouseEventDetails, eventElement);
		} else {
			mouseMoved = false;
		}
		mouseDown = false;
		mouseEventDetails = null;
		eventElement = null;
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if (!active) {
			return;
		}
		
		if (mouseDown) {
			mouseMoved = true;
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
			removeEventHandler(geometryHandlerMap.get(handler));
			geometryHandlerMap.remove(handler);
		}
	}

	public interface SyntheticClickHandler {
		public void syntheticClick(MouseEventDetails details, Element relativeElement);
	}

	@Override
	public void setShift(int x, int y) {
		int deltaX = x - lastShiftX;
		int deltaY = y - lastShiftY;
		lastShiftX = x;
		lastShiftY = y;
		
		updateDrawings(deltaX, deltaY);
	}

	@Override
	public int getShiftX() {
		return lastShiftX;
	}

	@Override
	public int getShiftY() {
		return lastShiftY;
	}

	protected void updateDrawings(int deltaX, int deltaY) {
	}

}
