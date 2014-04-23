/**
 * 
 */
package org.vaadin.maps.client.ui;

import java.util.Stack;

import org.vaadin.gwtgraphics.client.shape.Circle;
import org.vaadin.gwtgraphics.client.shape.Path;
import org.vaadin.gwtgraphics.client.shape.path.LineTo;
import org.vaadin.maps.client.drawing.Utils;
import org.vaadin.maps.client.geometry.LineString;
import org.vaadin.maps.shared.ui.Style;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;

/**
 * @author kamil
 *
 */
public class VPathHandler extends VPointHandler {

	public static final String CLASSNAME = "v-pathhandler";
	
	/**
	 * Representation of line start point, 
	 */
	protected Circle startPoint = null;
	protected Style startPointStyle = Style.DEFAULT_DRAW_START_POINT;
	
	/**
	 * Stack of line vertices, excluding first and last vertex
	 */
	protected Stack<Circle> vertices = new Stack<Circle>();
	protected Style drawVertexStyle = Style.DEFAULT_DRAW_VERTEX;
	
	protected Path line = null;
	protected Style drawLineStyle = Style.DEFAULT_DRAW_LINE;
	
	/**
	 * line actually drawn
	 */
	protected LineString lineString = null;
	
	public VPathHandler() {
		super();
		setStyleName(CLASSNAME);
	}

	private void addStartPoint(int x, int y) {
		startPoint = new Circle(x, y, 0);
		updateStartPointStyle();
		container.add(startPoint);
	}

	private void updateStartPointStyle() {
		if (startPoint != null && startPointStyle != null) {
			Utils.updateDrawingStyle(startPoint, startPointStyle);
		}
	}


	private void removeStartPoint() {
		container.remove(startPoint);
		startPoint = null;
	}
	
	private void addLine(int x, int y) {
		line = new Path(x, y);
		line.lineTo(x, y);
		updateDrawLineStyle();
		container.add(line);
	}
	
	private void updateDrawLineStyle() {
		if (line != null && drawLineStyle != null) {
			Utils.updateDrawingStyle(line, drawLineStyle);
		}
	}

	private void removeLine() {
		container.remove(line);
		line = null;
		removeVertices();
	}
	
	private void addVertex(int x, int y) {
		Circle vertex = new Circle(x, y, 0);
		updateDrawVertexStyle(vertex);
		vertices.add(vertex);
		container.add(vertex);
	}

	private void updateDrawVertexStyle(Circle vertex) {
		if (vertex != null && drawVertexStyle != null) {
			Utils.updateDrawingStyle(vertex, drawVertexStyle);
		}
	}
	
	private void updateVerticesStyle() {
		for (Circle vertex : vertices) {
			updateDrawVertexStyle(vertex);
		}
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
	
	private void addLineStringVertex(int[] xy) {
		lineString.getCoordinateSequence().add(createWorldCoordinate(xy));
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
	}
	
	private void prepareLineString(int[] xy) {
		lineString = new LineString(createWorldCoordinate(xy));
	}
	
	private void finishLineString(int[] xy) {
		lineString.getCoordinateSequence().add(createWorldCoordinate(xy));
	}
	
	private void closeLineString() {
		lineString.close();
	}

	private void cleanDrawing() {
		removeVertices();
		removeStartPoint();
		removeLine();
	}
	
	private void cleanLineString() {
		lineString = null;
	}
	
	public Style getStartPointStyle() {
		return startPointStyle;
	}
	
	public void setStartPointStyle(Style style) {
		if (style != null) {
			this.startPointStyle = style;
		} else {
			this.startPointStyle = Style.DEFAULT_DRAW_CURSOR;
		}
		
		updateStartPointStyle();
	}

	public Style getDrawLineStyle() {
		return drawLineStyle;
	}
	
	public void setDrawLineStyle(Style style) {
		if (style != null) {
			this.drawLineStyle = style;
		} else {
			this.drawLineStyle = Style.DEFAULT_DRAW_LINE;
		}
		
		updateDrawLineStyle();
	}

	public Style getDrawVertexStyle() {
		return drawVertexStyle;
	}
	
	public void setDrawVertexStyle(Style style) {
		if (style != null) {
			this.drawVertexStyle = style;
		} else {
			this.drawVertexStyle = Style.DEFAULT_DRAW_VERTEX;
		}
		
		updateVerticesStyle();
	}

	@Override
	public void onClick(ClickEvent event) {
		if (!active) {
			return;
		}

		boolean finish = false;

		int[] xy = getMouseEventXY(event);

		// first click
		// add start point and begin line drawing
		// create and insert start point
		if (null == startPoint) {
			prepareDrawing(xy[0], xy[1]);
			prepareLineString(xy);
		} else {
			if (event.isAltKeyDown()) {
				// finish drawing if alt key has been pressed
				finish = true;
				// append last vertex
				finishLineString(xy);
				
			} else if (event.isShiftKeyDown()) {
				// finish drawing with closing line if shift key has been pressed
				// and the click is in start point's circle
				if (isWithinCircle(startPoint.getX(), startPoint.getY(), startPoint.getRadius(),
						xy[0], xy[1])) {
					finish = true;
					
					// close line
					closeLineString();
				}
			}
			
			if (finish) {
				fireEvent(new GeometryEvent(VPathHandler.this, lineString));
				cleanDrawing();
				cleanLineString();
			} else {
				addLineSegment(xy[0], xy[1]);
				// append vertex
				addLineStringVertex(xy);
			}
		}
	}

	private boolean isWithinCircle(int circleX, int circleY, int radius, int pointX, int pointY) {
		return Math.sqrt(
				Math.pow(pointX - circleX, 2) +
				Math.pow(pointY - circleY, 2)
				) <= radius;
	}
	
	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if (!active) {
			return;
		}
		
		super.onMouseMove(event);
		
		int[] xy = getMouseEventXY(event);
		
		// update line segment to current position
		updateLineSegment(xy[0], xy[1]);
	}

}
