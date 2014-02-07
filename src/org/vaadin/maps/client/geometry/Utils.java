/**
 * 
 */
package org.vaadin.maps.client.geometry;

import org.vaadin.gwtgraphics.client.AbstractDrawing;
import org.vaadin.gwtgraphics.client.Group;
import org.vaadin.gwtgraphics.client.shape.Circle;
import org.vaadin.gwtgraphics.client.shape.Path;
import org.vaadin.maps.client.geometry.wkb.WKBReader;
import org.vaadin.maps.client.geometry.wkb.WKBWriter;
import org.vaadin.maps.client.io.ParseException;

/**
 * @author kamil
 *
 */
public class Utils {
	
	public static Geometry hexWKBToGeometry(String wkb) throws ParseException {
		if ( wkb != null) {
			WKBReader wkbReader = new WKBReader();
		
			return wkbReader.read(WKBReader.hexToBytes(wkb));
		}

		return null;
	}
	
	public static String GeometryToWKBHex(Geometry geometry) {
		if (geometry != null) {
			WKBWriter wkbWriter = new WKBWriter();
			
			return WKBWriter.bytesToHex(wkbWriter.write(geometry));
		}
		
		return null;
	}

	public static Circle drawPoint(Point point) {
		Circle circle = new Circle(Math.round((float) point.getX()),
				Math.round((float) point.getY()), 5);   // TODO radius from
		circle.setFillOpacity(0.3);						// feature styling
		return circle;
	}

	public static Path drawLineString(LineString lineString) {
		Path path = null;

		for (Coordinate coordinate : lineString.getCoordinateSequence()) {
			if (path != null) {
				path.lineTo(Math.round((float) coordinate.x),
						Math.round((float) coordinate.y));
			} else {
				path = new Path(Math.round((float) coordinate.x),
						Math.round((float) coordinate.y));
				path.setFillOpacity(0);
			}
		}

		return path;
	}

	public static Path drawLinearRing(LinearRing linearRing, boolean filled) {
		Path path = null;

		if (linearRing.getNumPoints() > 3) {
			path = drawLineString(linearRing);

			if (filled) {
				path.setFillEventOdd();
			}

			path.close();
		}

		return path;
	}

	public static Path drawPolygon(Polygon polygon) {
		/*
		 * public static Group drawPolygon(Polygon polygon) { Group group = new
		 * Group();
		 * 
		 * group.add(drawPolygonInternal(polygon));
		 * 
		 * return group;
		 */
		return drawPolygonInternal(polygon);
	}

	private static Path drawPolygonInternal(Polygon polygon) {
		Path path = drawLinearRing(polygon.getShell(), true);
		path.setFillOpacity(0.3);

		if (polygon.getNumHoles() > 0) {
			addPolygonHoles(path, polygon);
		}

		return path;
	}

	private static void addPolygonHoles(Path path, Polygon polygon) {
		for (LinearRing hole : polygon.getHoles()) {
			addPolygonHole(path, hole);
		}
	}

	private static void addPolygonHole(Path path, LinearRing hole) {
		boolean moved = false;

		for (Coordinate coordinate : hole.getCoordinateSequence()) {
			if (!moved) {
				moved = true;
				path.moveTo(Math.round((float) coordinate.x),
						Math.round((float) coordinate.y));
			} else {
				path.lineTo(Math.round((float) coordinate.x),
						Math.round((float) coordinate.y));
			}
		}
	}

	public static Group drawMultiPoint(MultiPoint multiPoint) {
		Group group = new Group();

		for (Point point = (Point) multiPoint.iterator(); multiPoint.iterator()
				.hasNext();) {

			group.add(drawPoint(point));
		}

		return group;
	}

	public static Group drawMultiLineString(MultiLineString multiLineString) {
		Group group = new Group();

		for (LineString lineString = (LineString) multiLineString.iterator(); multiLineString
				.iterator().hasNext();) {

			group.add(drawLineString(lineString));
		}

		return group;
	}

	public static Group drawMultiPolygon(MultiPolygon multiPolygon) {
		Group group = new Group();

		for (Polygon polygon = (Polygon) multiPolygon.iterator(); multiPolygon
				.iterator().hasNext();) {

			group.add(drawPolygonInternal(polygon));
		}

		return group;
	}

	public static Group drawGeometryCollection(
			GeometryCollection geometryCollection) {
		Group group = new Group();

		for (Geometry geometry = (Geometry) geometryCollection.iterator(); geometryCollection
				.iterator().hasNext();) {

			group.add(drawGeometry(geometry));
		}

		return group;
	}

	public static AbstractDrawing drawGeometry(Geometry geometry) {
		if (geometry instanceof MultiPolygon) {
			return drawMultiPolygon((MultiPolygon) geometry);
		} else if (geometry instanceof MultiLineString) {
			return drawMultiLineString((MultiLineString) geometry);
		} else if (geometry instanceof MultiPoint) {
			return drawMultiPoint((MultiPoint) geometry);
		} else if (geometry instanceof GeometryCollection) {
			return drawGeometryCollection((GeometryCollection) geometry);
		} else if (geometry instanceof Polygon) {
			return drawPolygon((Polygon) geometry);
		} else if (geometry instanceof LinearRing) {
			return drawLinearRing((LinearRing) geometry, false);
		} else if (geometry instanceof LineString) {
			return drawLineString((LineString) geometry);
		} else if (geometry instanceof Point) {
			return drawPoint((Point) geometry);
		}

		return null;
	}

}
