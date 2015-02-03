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
import org.vaadin.gwtgraphics.client.shape.Rectangle;
import org.vaadin.gwtgraphics.client.shape.Text;
import org.vaadin.gwtgraphics.client.shape.Path.RedrawType;
import org.vaadin.gwtgraphics.client.shape.path.Arc;
import org.vaadin.gwtgraphics.client.shape.path.LineTo;
import org.vaadin.gwtgraphics.client.shape.path.MoveTo;
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
	
	public enum PointShape {
		Circle,
		Square,
		Cross,
		XCross,
		Asterisk,
		TriangleUp,
		TriangleDown,
		Diamond//,
		//Star
	}
	
	public static PointShape pointShapeFromString(String string) {
		if (string != null) {
			for (PointShape pointShape : PointShape.values()) {
				if (pointShape.name().equalsIgnoreCase(string)) {
					return pointShape;
				}
			}
		}
		return null;
	}
	
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

	private static Circle drawPoint(Point point, int shiftX, int shiftY) {
		Circle circle = new Circle(Math.round((float) point.getX() + shiftX),
				Math.round((float) point.getY() + shiftY), 5);   // TODO radius from
		circle.setFillOpacity(0.3);						// feature styling
		return circle;
	}

	public static Shape drawPoint(Point point, PointShape pointShape, double scale, int shiftX, int shiftY) {
		switch (pointShape) {
		case Circle:
			return drawPoint(point, shiftX, shiftY);
		case Square:
			return drawSquare(point, scale, shiftX, shiftY);
		case Cross:
			return drawCross(point, scale, shiftX, shiftY);
		case XCross:
			return drawXCross(point, scale, shiftX, shiftY);
		case Asterisk:
			return drawAsterisk(point, scale, shiftX, shiftY);
		case TriangleUp:
			return drawTriangle(point, false, scale, shiftX, shiftY);
		case TriangleDown:
			return drawTriangle(point, true, scale, shiftX, shiftY);
		case Diamond:
			return drawDiamond(point, scale, shiftX, shiftY);

		default:
			break;
		}
		return null;
	}

	private static Shape drawDiamond(Point point, double scale, int shiftX, int shiftY) {
		int d = Math.round((float)(1 * scale));
		if (d < 1) {
			d = 1;
		}
		
		Path path = new Path(Math.round((float)point.getX()+shiftX)-d, Math.round((float)point.getY()+shiftY));
		path.setRedrawingType(RedrawType.MANUAL);
		path.addStep(new LineTo(true, d, d));
		path.addStep(new LineTo(true, d, -d));
		path.addStep(new LineTo(true, -d, -d));
		path.addStep(new LineTo(true, -d, d));
		path.close();
		path.issueRedraw(true);
		return path;
	}

	private static Shape drawTriangle(Point point, boolean down, double scale, int shiftX, int shiftY) {
		int a = Math.round((float)(1 * scale));
		if (a < 1) {
			a = 1;
		}
		int b = Math.round((float)(2 * scale));
		if (b < 2) {
			b = 2;
		}
		int c = Math.round((float)((a+1)*Math.sqrt(3))) - 1;
		if (c < 2) {
			c = 2;
		}
		
		Path path = new Path(Math.round((float)point.getX()+shiftX), Math.round((float)point.getY()+shiftY)+ (down ? b : -b));
		path.setRedrawingType(RedrawType.MANUAL);
		path.addStep(new LineTo(true, c, down ? -a-b: a+b));
		path.addStep(new LineTo(true, -2*c, 0));
		path.addStep(new LineTo(true, c, down ? a+b: -a-b));
		path.close();
		path.issueRedraw(true);
		return path;
	}

	private static Shape drawAsterisk(Point point, double scale, int shiftX, int shiftY) {
		int d = Math.round((float)(1 * scale));
		if (d < 1) {
			d = 1;
		}
		int e = Math.round((float)Math.sqrt((2*Math.pow(1*scale + 1, 2))))-1;
		if (e < 1) {
			e = 1;
		}
		
		Path path = new Path(Math.round((float)point.getX()+shiftX)-d, Math.round((float)point.getY()+shiftY)-d);
		path.setFillAllowed(false);
		path.setRedrawingType(RedrawType.MANUAL);
		path.addStep(new LineTo(true, 2*d, 2*d));
		path.addStep(new MoveTo(true, -2*d, 0));
		path.addStep(new LineTo(true, 2*d, -2*d));
		
		path.addStep(new MoveTo(false, Math.round((float)point.getX()+shiftX)-e, Math.round((float)point.getY()+shiftY)));
		path.addStep(new LineTo(true, 2*e, 0));
		path.addStep(new MoveTo(true, -e, e));
		path.addStep(new LineTo(true, 0, -2*e));

		path.issueRedraw(true);
		return path;
	}

	private static Shape drawXCross(Point point, double scale, int shiftX, int shiftY) {
		int d = Math.round((float)(1 * scale));
		if (d < 1) {
			d = 1;
		}
		
		Path path = new Path(Math.round((float)point.getX()+shiftX)-d, Math.round((float)point.getY()+shiftY)-d);
		path.setFillAllowed(false);
		path.setRedrawingType(RedrawType.MANUAL);
		path.addStep(new LineTo(true, 2*d, 2*d));
		path.addStep(new MoveTo(true, -2*d, 0));
		path.addStep(new LineTo(true, 2*d, -2*d));
		path.issueRedraw(true);
		return path;
	}

	private static Shape drawCross(Point point, double scale, int shiftX, int shiftY) {
		int d = Math.round((float)(1 * scale));
		if (d < 1) {
			d = 1;
		}
		
		Path path = new Path(Math.round((float)point.getX()+shiftX)-d, Math.round((float)point.getY()+shiftY));
		path.setFillAllowed(false);
		path.setRedrawingType(RedrawType.MANUAL);
		path.addStep(new LineTo(true, 2*d, 0));
		path.addStep(new MoveTo(true, -d, d));
		path.addStep(new LineTo(true, 0, -2*d));
		path.issueRedraw(true);
		return path;
	}

	private static Rectangle drawSquare(Point point, double scale, int shiftX, int shiftY) {
		int d = Math.round((float)(1 * scale));
		if (d < 1) {
			d = 1;
		}
		return new Rectangle(Math.round((float)point.getX()+shiftX)-d, Math.round((float)point.getY()+shiftY)-d, 2*d+1, 2*d+1);
	}

	public static Path drawLineString(LineString lineString, int shiftX, int shiftY) {
		Path path = null;

		for (Coordinate coordinate : lineString.getCoordinateSequence()) {
			if (path != null) {
				path.lineTo(Math.round((float) coordinate.x + shiftX),
						Math.round((float) coordinate.y + shiftY));
			} else {
				path = new Path(Math.round((float) coordinate.x + shiftX), Math.round((float) coordinate.y + shiftY));
				path.setFillAllowed(false);
			}
		}

		return path;
	}

	public static Path drawLinearRing(LinearRing linearRing, boolean filled, int shiftX, int shiftY) {
		Path path = null;

		if (linearRing.getNumPoints() > 3) {
			path = drawLineString(linearRing, shiftX, shiftY);
			path.setFillAllowed(true);

			if (filled) {
				path.setFillEvenOdd();
			}

			path.close();
		}

		return path;
	}

	public static Path drawPolygon(Polygon polygon, int shiftX, int shiftY) {
		return drawPolygonInternal(polygon, shiftX, shiftY);
	}

	private static Path drawPolygonInternal(Polygon polygon, int shiftX, int shiftY) {
		Path path = drawLinearRing(polygon.getShell(), true, shiftX, shiftY);
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

	public static Group drawMultiPoint(MultiPoint multiPoint, PointShape pointShape, double scale, int shiftX, int shiftY) {
		Group group = new Group();

		for (Iterator<Geometry> iterator = multiPoint.iterator(); iterator.hasNext();) {
			Point point = (Point) iterator.next();
			group.add(drawPoint(point, pointShape, scale, shiftX, shiftY));
		}

		return group;
	}

	public static Group drawMultiLineString(MultiLineString multiLineString, int shiftX, int shiftY) {
		Group group = new Group();

		for (Iterator<Geometry> iterator = multiLineString.iterator(); iterator.hasNext();) {
			LineString lineString = (LineString) iterator.next();
			group.add(drawLineString(lineString, shiftX, shiftY));
		}

		return group;
	}

	public static Group drawMultiPolygon(MultiPolygon multiPolygon, int shiftX, int shiftY) {
		Group group = new Group();

		for (Iterator<Geometry> iterator = multiPolygon.iterator(); iterator.hasNext();) {
			Polygon polygon = (Polygon) iterator.next();
			group.add(drawPolygonInternal(polygon, shiftX, shiftY));
		}

		return group;
	}

	public static Group drawGeometryCollection(GeometryCollection geometryCollection, PointShape pointShape, double scale, int shiftX, int shiftY) {
		Group group = new Group();
		for (Iterator<Geometry> iterator = geometryCollection.iterator(); iterator.hasNext();) {
			Geometry geometry = iterator.next();
			group.add(drawGeometry(geometry, pointShape, scale, shiftX, shiftY));
		}

		return group;
	}

	public static AbstractDrawing drawGeometry(Geometry geometry, PointShape pointShape, double scale, int shiftX, int shiftY) {
		if (geometry instanceof MultiPolygon) {
			return drawMultiPolygon((MultiPolygon) geometry, shiftX, shiftY);
		} else if (geometry instanceof MultiLineString) {
			return drawMultiLineString((MultiLineString) geometry, shiftX, shiftY);
		} else if (geometry instanceof MultiPoint) {
			return drawMultiPoint((MultiPoint) geometry, pointShape, scale, shiftX, shiftY);
		} else if (geometry instanceof GeometryCollection) {
			return drawGeometryCollection((GeometryCollection) geometry, pointShape, scale, shiftX, shiftY);
		} else if (geometry instanceof Polygon) {
			return drawPolygon((Polygon) geometry, shiftX, shiftY);
		} else if (geometry instanceof LinearRing) {
			return drawLinearRing((LinearRing) geometry, false, shiftX, shiftY);
		} else if (geometry instanceof LineString) {
			return drawLineString((LineString) geometry, shiftX, shiftY);
		} else if (geometry instanceof Point) {
			return drawPoint((Point) geometry, pointShape, scale, shiftX, shiftY);
		}

		return null;
	}
	
	public static Path drawDonut(double x, double y, double r1, double r2) {
		Path path = new Path(Math.round((float)x), Math.round((float)(y-r1)));
		path.setFillEvenOdd();
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

	/*public static void updateDrawingPosition(AbstractDrawing drawing, int shiftX, int shiftY) {
		if (drawing instanceof Group) {
			Iterator<AbstractDrawing> iterator;
			for (iterator = ((Group)drawing).iterator(); iterator.hasNext();) {
				updateDrawingPosition(iterator.next(), shiftX, shiftY);
			}
		} else if (drawing instanceof Positionable) {
			Positionable positionable = (Positionable)drawing;
			positionable.setX(positi);
		}
	}*/
	
	public static void moveGeometry(Geometry geometry, int dX, int dY) {
		if (geometry != null && (dX != 0 || dY != 0)) {
			Coordinate[] coordinates = geometry.getCoordinates();
			for (Coordinate coordinate : coordinates) {
				coordinate.x += dX;
				coordinate.y += dY;
			}
		}
	}
}
