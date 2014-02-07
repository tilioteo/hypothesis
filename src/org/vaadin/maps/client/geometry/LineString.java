/*
 * The JTS Topology Suite is a collection of Java classes that
 * implement the fundamental operations required to validate a given
 * geo-spatial data set to a known topological specification.
 *
 * Copyright (C) 2001 Vivid Solutions
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * For more information, contact:
 *
 *     Vivid Solutions
 *     Suite #1A
 *     2328 Government Street
 *     Victoria BC  V8T 5G5
 *     Canada
 *
 *     (250)385-6040
 *     www.vividsolutions.com
 */
package org.vaadin.maps.client.geometry;

/**
 * Basic implementation of <code>LineString</code>.
 * 
 * @version 1.7
 */
@SuppressWarnings("serial")
public class LineString extends Point {

	public LineString(Coordinate... coordinates) {
		this(coordinates, 0);
	}
	
	public LineString(Coordinate[] coordinates, int SRID) {
		this(new CoordinateSequence(coordinates), SRID);
	}
	
	public LineString(CoordinateSequence coordinates) {
		this(coordinates, 0);
	}

	public LineString(CoordinateSequence coordinates, int SRID) {
		super(SRID);
		init(coordinates);
	}

	private void init(CoordinateSequence coordinates) {
		if (coordinates != null) {
			this.coordinates = new CoordinateSequence(coordinates);
		}
	}

public LineString(LineString lineString) {
		this(lineString.coordinates, lineString.SRID);
	}

	public Coordinate getCoordinate(int index) {
		return coordinates.get(index);
	}

	public Point getPoint(int index) {
		return new Point(new Coordinate(coordinates.get(index)));
	}

	public Point getStartPoint() {
		if (isEmpty()) {
			return null;
		}
		return getPoint(0);
	}

	public Point getEndPoint() {
		if (isEmpty()) {
			return null;
		}
		return getPoint(getNumPoints() - 1);
	}

	public boolean isClosed() {
		if (isEmpty()) {
			return false;
		}
		return getCoordinate(0).equals(getCoordinate(getNumPoints() - 1));
	}

	public String getGeometryType() {
		return "LineString";
	}

	/**
	 * Creates a {@link LineString} whose coordinates are in the reverse order
	 * of this objects
	 * 
	 * @return a {@link LineString} with coordinates in the reverse order
	 */
	public LineString reverse() {
		LineString lineString = new LineString(this);
		CoordinateSequence.reverse(lineString.coordinates);
		return lineString;
	}

	public LineString getClosed() {
		if (!isEmpty()) {
			LineString lineString = new LineString(this);

			if (!lineString.isClosed()) {
				lineString.coordinates.add(new Coordinate(getStartPoint().getCoordinate()));
			}

			return lineString;
		}
		return null;
	}

	public void close() {
		if (!isEmpty() && !isClosed()) {
			coordinates.add(new Coordinate(getStartPoint().getCoordinate()));
		}
	}

	/**
	 * Returns true if the given point is a vertex of this
	 * <code>LineString</code>.
	 * 
	 * @param pt
	 *            the <code>Coordinate</code> to check
	 * @return <code>true</code> if <code>pt</code> is one of this
	 *         <code>LineString</code> 's vertices
	 */
	public boolean isVertex(Coordinate coordinate) {
		return coordinates.coordinates.contains(coordinate);
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof LineString)) {
			return false;
		}
		LineString otherLineString = (LineString) other;

		return coordinates.equals(otherLineString.coordinates);
	}

}
