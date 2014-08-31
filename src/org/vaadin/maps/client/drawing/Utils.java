/**
 * 
 */
package org.vaadin.maps.client.drawing;

import java.util.Iterator;

import org.vaadin.gwtgraphics.client.AbstractDrawing;
import org.vaadin.gwtgraphics.client.Group;
import org.vaadin.gwtgraphics.client.Shape;
import org.vaadin.gwtgraphics.client.Strokeable;
import org.vaadin.gwtgraphics.client.shape.Circle;
import org.vaadin.gwtgraphics.client.shape.Path;
import org.vaadin.gwtgraphics.client.shape.Text;
import org.vaadin.gwtgraphics.client.shape.path.Arc;
import org.vaadin.maps.client.geometry.Coordinate;
import org.vaadin.maps.client.geometry.Geometry;
import org.vaadin.maps.client.geometry.GeometryCollection;
import org.vaadin.maps.client.geometry.LineString;
import org.vaadin.maps.client.geometry.LinearRing;
import org.vaadin.maps.client.geometry.MultiLineString;
import org.vaadin.maps.client.geometry.MultiPoint;
import org.vaadin.maps.client.geometry.MultiPolygon;
import org.vaadin.maps.client.geometry.Point;
import org.vaadin.maps.client.geometry.Polygon;
import org.vaadin.maps.client.geometry.wkb.WKBReader;
import org.vaadin.maps.client.geometry.wkb.WKBWriter;
import org.vaadin.maps.client.io.ParseException;
import org.vaadin.maps.shared.ui.Style;

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

		for (Iterator<Geometry> iterator = multiPoint.iterator(); iterator.hasNext();) {
			Point point = (Point) iterator.next();
			group.add(drawPoint(point));
		}

		return group;
	}

	public static Group drawMultiLineString(MultiLineString multiLineString) {
		Group group = new Group();

		for (Iterator<Geometry> iterator = multiLineString.iterator(); iterator.hasNext();) {
			LineString lineString = (LineString) iterator.next();
			group.add(drawLineString(lineString));
		}

		return group;
	}

	public static Group drawMultiPolygon(MultiPolygon multiPolygon) {
		Group group = new Group();

		for (Iterator<Geometry> iterator = multiPolygon.iterator(); iterator.hasNext();) {
			Polygon polygon = (Polygon) iterator.next();
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
	
	public static Path drawDonut(double x, double y, double r1, double r2) {
		Path path = new Path(Math.round((float)x), Math.round((float)(y-r1)));
		path.setFillEventOdd();
		path.addStep(new Arc(false, Math.round((float)r1), Math.round((float)r1), 0, false, true, Math.round((float)x), Math.round((float)(y+r1))));
		path.addStep(new Arc(false, Math.round((float)r1), Math.round((float)r1), 0, false, true, Math.round((float)x), Math.round((float)(y-r1))));
		path.close();
		path.moveTo(Math.round((float)x), Math.round((float)(y-r2)));
		path.addStep(new Arc(false, Math.round((float)r2), Math.round((float)r2), 0, false, true, Math.round((float)x), Math.round((float)(y+r2))));
		path.addStep(new Arc(false, Math.round((float)r2), Math.round((float)r2), 0, false, true, Math.round((float)x), Math.round((float)(y-r2))));
		path.close();
		
		return path;
	}

	private static void updateCircleStyle(Circle circle, Style style) {
		updateShapeStyle(circle, style);
		
		circle.setRadius(style.pointRadius);
	}

	private static void updateDonutStyle(Donut donut, Style style) {
		updateShapeStyle(donut, style);
		
		donut.setR1(style.pointRadius > 1 ? style.pointRadius : 1);
		donut.setR2(1);
	}

	private static void updateTextStyle(Text text, Style style) {
		if (!style.textColor.isEmpty())
			text.setFillColor(style.textColor);
		else
			text.setFillColor(null);
		
		if (style.textFillOpacity < 1)
			text.setFillOpacity(style.textFillOpacity);
		else
			text.setFillOpacity(1.0);

		text.setFontFamily(style.fontFamily);
		text.setFontSize(style.fontSize);
		
		if (!style.textStrokeColor.isEmpty())
			text.setStrokeColor(style.textStrokeColor);
		else
			text.setStrokeColor(null);
		
		if (style.textStrokeWidth > 0)
			text.setStrokeWidth(style.textStrokeWidth);
		else
			text.setStrokeWidth(0);

		if (style.textOpacity < 1)
			text.setStrokeOpacity(style.textOpacity);
		else
			text.setStrokeOpacity(1.0);
	}

	private static void updateShapeStyle(Shape shape, Style style) {
		updateStrokeableStyle(shape, style);
		
		if (!style.fillColor.isEmpty())
			shape.setFillColor(style.fillColor);
		else
			shape.setFillColor(null);
		
		if (style.fillOpacity < 1)
			shape.setFillOpacity(style.fillOpacity);
		else
			shape.setFillOpacity(1.0);
		
		if (style.opacity < 1)
			shape.setOpacity(style.opacity);
		else
			shape.setOpacity(1.0);
	}

	private static void updateStrokeableStyle(Strokeable strokeable, Style style) {
		if (!style.strokeColor.isEmpty())
			strokeable.setStrokeColor(style.strokeColor);
		else
			strokeable.setStrokeColor(null);
			
		if (style.strokeOpacity < 1)
			strokeable.setStrokeOpacity(style.strokeOpacity);
		else
			strokeable.setStrokeOpacity(1.0);
		if (style.strokeWidth > 0)
			strokeable.setStrokeWidth(style.strokeWidth);
		else
			strokeable.setStrokeWidth(0);
	}

	private static void updateGroupStyle(Group group, Style style) {
		for (Iterator<AbstractDrawing> iterator = group.iterator(); iterator.hasNext();) {
			updateDrawingStyle(iterator.next(), style);
		}
	}

	public static void updateDrawingStyle(AbstractDrawing drawing, Style style) {
		if (drawing instanceof Circle) {
			updateCircleStyle((Circle) drawing, style);
		} else if (drawing instanceof Donut) {
			updateDonutStyle((Donut) drawing, style);
		} else if (drawing instanceof Text) {
			updateTextStyle((Text) drawing, style);
		} else if (drawing instanceof Shape) {
			updateShapeStyle((Shape) drawing, style);
		} else if (drawing instanceof Strokeable) {
			updateStrokeableStyle((Strokeable) drawing, style);
		} else if (drawing instanceof Group) {
			updateGroupStyle((Group) drawing, style);
		}
	}
	
}