/**
 * 
 */
package org.vaadin.maps.client.ui;

import org.vaadin.maps.client.geometry.LinearRing;
import org.vaadin.maps.client.geometry.Polygon;

import com.google.gwt.event.dom.client.ClickEvent;

/**
 * @author kamil
 *
 */
public class VPolygonHandler extends VPathHandler {

	public static final String CLASSNAME = "v-polygonhandler";
	
	public VPolygonHandler() {
		super();
		setStyleName(CLASSNAME);
	}
	
	@Override
	public void onClick(ClickEvent event) {
		if (!active) {
			return;
		}

		int[] xy = getMouseEventXY(event);

		// first click
		// add start point and begin line drawing
		// create and insert start point
		if (null == startPoint) {
			prepareDrawing(xy[0], xy[1]);
			prepareLineString(xy);
		} else {
			if (event.isAltKeyDown() && canCloseLineString() &&
					isWithinCircle(startPoint.getX(), startPoint.getY(),
							startPoint.getRadius() > 2 ? startPoint.getRadius() : 2,
									xy[0], xy[1])) {
				// close line
				closeLineString();
				
				Polygon polygon = new Polygon(new LinearRing(lineString));

				fireEvent(new GeometryEvent(VPolygonHandler.this, polygon));
				cleanDrawing();
				cleanLineString();
			} else {
				addLineSegment(xy[0], xy[1]);
				// append vertex
				addLineStringVertex(xy);
			}
		}
	}

	protected boolean canCloseLineString() {
		return lineString != null && lineString.getNumPoints() > 2;
	}

}
