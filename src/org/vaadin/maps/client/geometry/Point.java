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

import org.vaadin.maps.client.geometry.util.Assert;

/**
 * Basic implementation of <code>Point</code>.
 * 
 * @version 1.7
 */
@SuppressWarnings("serial")
public class Point extends Geometry {
	/**
	 * The <code>Coordinate</code> wrapped by this <code>Point</code>.
	 */
	protected CoordinateSequence coordinates;
	
	protected Point(int SRID) {
		this.SRID = SRID;
	}

	/**
	 * Constructs a <code>Point</code> with the given coordinate without SRID
	 * 
	 * @param coordinate
	 *            the coordinate on which to base this <code>Point</code> , or
	 *            <code>null</code> to create the empty geometry.
	 */
	public Point(Coordinate coordinate) {
		this(coordinate, 0);
	}

	/**
	 * Constructs a <code>Point</code> with the given coordinate.
	 * 
	 * @param coordinate
	 *            the coordinate on which to base this <code>Point</code> , or
	 *            <code>null</code> to create the empty geometry.
	 * @param SRID
	 *            the ID of the Spatial Reference System used by this
	 *            <code>Point</code>
	 */
	public Point(Coordinate coordinate, int SRID) {
		this(SRID);
		
		if (coordinate != null) {
			coordinates = new CoordinateSequence(coordinate);
		}
	}

	/**
	 * @param coordinates
	 *            contains the single coordinate on which to base this
	 *            <code>Point</code> , or <code>null</code> to create the empty
	 *            geometry.
	 */
	public Point(CoordinateSequence coordinates) {
		this(coordinates, 0);
	}

	public Point(CoordinateSequence coordinates, int SRID) {
		init(coordinates);

		this.SRID = SRID;
	}

	public Point(Point point) {
		this(point.coordinates, point.SRID);
	}

	private void init(CoordinateSequence coordinates) {
		if (coordinates != null) {
		    Assert.isTrue(coordinates.size() <= 1);
			this.coordinates = new CoordinateSequence(coordinates);
		}
	}

	public Coordinate[] getCoordinates() {
		return isEmpty() ? new Coordinate[] {} : coordinates.toArray();
	}

	public int getNumPoints() {
		return coordinates.size();
	}

	public boolean isEmpty() {
		return coordinates.size() == 0;
	}

	public double getX() {
		if (getCoordinate() == null) {
			throw new IllegalStateException("getX called on empty Point");
		}
		return getCoordinate().x;
	}

	public double getY() {
		if (getCoordinate() == null) {
			throw new IllegalStateException("getY called on empty Point");
		}
		return getCoordinate().y;
	}

	public Coordinate getCoordinate() {
		if (isEmpty()) {
			return null;
		}

		return coordinates.get(0);
	}

	public String getGeometryType() {
		return "Point";
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Point))
			return false;

		Point otherPoint = (Point) other;
		return coordinates.equals(otherPoint.coordinates);
	}

	public CoordinateSequence getCoordinateSequence() {
		return coordinates;
	}
}
