/**
 * 
 */
package org.vaadin.maps.client.ui;

import java.util.Stack;

import org.vaadin.gwtgraphics.client.shape.Circle;
import org.vaadin.gwtgraphics.client.shape.Path;
import org.vaadin.gwtgraphics.client.shape.path.LineTo;
import org.vaadin.maps.client.geometry.LineString;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;

/**
 * @author kamil
 *
 */
public class VPathHandler extends VPointHandler {

	public static final String CLASSNAME = "v-pathhandler";
	
	public static final int START_POINT_RADIUS = CURSOR_CIRCLE_RADIUS;
	public static final String START_POINT_STROKE_COLOR = "black";
	public static final String START_POINT_FILL_COLOR = CURSOR_CIRCLE_FILL_COLOR;
	public static final double START_POINT_FILL_OPACITY = CURSOR_CIRCLE_FILL_OPACITY;
	
	public static final int VERTEX_RADIUS = 2;
	public static final String VERTEX_STROKE_COLOR = START_POINT_STROKE_COLOR;
	public static final String VERTEX_FILL_COLOR = START_POINT_FILL_COLOR;
	public static final double VERTEX_FILL_OPACITY = 0.0;

	public static final String LINE_STROKE_COLOR = CURSOR_CIRCLE_FILL_COLOR;
	
	
	
	/**
	 * Representation of line start point, 
	 */
	protected Circle startPoint = null;
	/**
	 * Stack of line vertices, excluding first and last vertex
	 */
	protected Stack<Circle> vertices = new Stack<Circle>();
	protected Path line = null;
	
	/**
	 * line actually drawn
	 */
	protected LineString lineString = null;
	
	public VPathHandler() {
		super();
		setStyleName(CLASSNAME);
	}

	private void addStartPoint(int x, int y) {
		startPoint = new Circle(x, y, START_POINT_RADIUS);
		startPoint.setStrokeColor(START_POINT_STROKE_COLOR);
		startPoint.setFillColor(START_POINT_FILL_COLOR);
		startPoint.setFillOpacity(START_POINT_FILL_OPACITY);
		container.add(startPoint);
	}

	private void removeStartPoint() {
		container.remove(startPoint);
		startPoint = null;
	}
	
	private void addLine(int x, int y) {
		line = new Path(x, y);
		line.lineTo(x, y);
		line.setStrokeColor(LINE_STROKE_COLOR);
		line.setFillOpacity(0.0);
		container.add(line);
	}
	
	private void createLineString(int x, int y) {
		lineString = new LineString(createWorldCoordinate(x, y));
	}

	private void removeLine() {
		container.remove(line);
		line = null;
		removeVertices();
	}
	
	private void addVertex(int x, int y) {
		Circle vertex = new Circle(x, y, VERTEX_RADIUS);
		vertex.setStrokeColor(VERTEX_STROKE_COLOR);
		vertex.setFillColor(VERTEX_FILL_COLOR);
		vertex.setFillOpacity(VERTEX_FILL_OPACITY);
		vertices.add(vertex);
		container.add(vertex);
	}

	private void removeVertices() {
		while (!vertices.isEmpty()) {
			Circle vertex = vertices.pop();
			container.remove(vertex);
		}
	}

	private void addLineSegment(int x, int y) {
		addVertex(x, y);
		
		line.lineTo(x, y);
	}

	private void updateLineSegment(int x, int y) {
		if (line != null) {
			LineTo lastStep = (LineTo) line.getStep(line.getStepCount() - 1);
			lastStep.setX(x);
			lastStep.setY(y);
			line.issueRedraw(true);
		}
	}
	
	private void prepareDrawing(int x, int y) {
		addStartPoint(x, y);
		addLine(x, y);
		createLineString(x, y);
	}

	private void cleanDrawing() {
		removeVertices();
		removeStartPoint();
		removeLine();
		
		lineString = null;
	}

	@Override
	public void onClick(ClickEvent event) {
		if (!active) {
			return;
		}

		boolean finish = false;

		// first click
		// add start point and begin line drawing
		// create and insert start point
		if (null == startPoint) {
			prepareDrawing(event.getX(), event.getY());
		} else {
			if (event.isAltKeyDown()) {
				// finish drawing if alt key has been pressed
				finish = true;
				
				// append last vertex
				lineString.getCoordinateSequence().add(createWorldCoordinate(event.getX(), event.getY()));
			} else if (event.isShiftKeyDown()) {
				// finish drawing with closing line if shift key has been pressed
				// and the click is in start point's circle
				if (Math.sqrt(
						Math.pow(event.getX() - startPoint.getX(), 2) +
						Math.pow(event.getY() - startPoint.getY(), 2)
						) <= START_POINT_RADIUS) {
					finish = true;
					
					// close line
					lineString.close();
				}
			}
			
			if (finish) {
				fireEvent(new GeometryEvent(VPathHandler.this, lineString));
				cleanDrawing();
			} else {
				// append vertex
				lineString.getCoordinateSequence().add(createWorldCoordinate(event.getX(), event.getY()));
				
				addLineSegment(event.getX(), event.getY());
			}
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if (!active) {
			return;
		}
		
		super.onMouseMove(event);
		
		// update line segment to current position
		updateLineSegment(event.getX(), event.getY());
	}

}
