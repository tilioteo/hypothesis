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

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Basic implementation of <code>GeometryCollection</code>.
 * 
 * @version 1.7
 */
@SuppressWarnings("serial")
public class GeometryCollection extends Geometry implements Iterable<Geometry> {

	/**
	 * Internal representation of this <code>GeometryCollection</code>.
	 */
	protected LinkedList<Geometry> geometries = new LinkedList<Geometry>();
	
	protected GeometryCollection(int SRID) {
		this.SRID = SRID;
	}

	public GeometryCollection(Geometry[] geometries) {
		this(geometries, 0);
	}

	/**
	 * @param geometries
	 *            the <code>Geometry</code>s for this
	 *            <code>GeometryCollection</code>, or <code>null</code> or an
	 *            empty array to create the empty geometry. Elements may be
	 *            empty <code>Geometry</code>s, but not <code>null</code>s.
	 */
	public GeometryCollection(Geometry[] geometries, int SRID) {
		this(SRID);
		
		if (geometries != null) {

			if (hasNullElements(geometries)) {
				throw new IllegalArgumentException(
						"geometries must not contain null elements");
			}
			for (Geometry geometry : geometries) {
				this.geometries.add(Geometry.clone(geometry));
			}
		}
	}

	public static final GeometryCollection clone(GeometryCollection geometryCollection) {
		if (geometryCollection instanceof MultiPoint)
			return new MultiPoint((MultiPoint)geometryCollection);
		else if (geometryCollection instanceof MultiLineString)
			return new MultiLineString((MultiLineString)geometryCollection);
		else if (geometryCollection instanceof MultiPolygon)
			return new MultiPolygon((MultiPolygon)geometryCollection);
		else
			return new GeometryCollection(geometryCollection.geometries.toArray(new Geometry[0]), geometryCollection.SRID);
	}
	
	@Override
	public Iterator<Geometry> iterator() {
		return geometries.iterator();
	}

	@Override
	public Coordinate getCoordinate() {
		if (isEmpty()) {
			return null;
		}

		return geometries.get(0).getCoordinate();
	}

	/**
	 * Collects all coordinates of all subgeometries into an Array.
	 * 
	 * Note that while changes to the coordinate objects themselves may modify
	 * the Geometries in place, the returned Array as such is only a temporary
	 * container which is not synchronized back.
	 * 
	 * @return the collected coordinates
	 * */
	@Override
	public Coordinate[] getCoordinates() {
		Coordinate[] coordinates = new Coordinate[getNumPoints()];
		int k = -1;
		for (Geometry geometry : geometries) {
			for (Coordinate coordinate : geometry.getCoordinates()) {
				coordinates[++k] = coordinate;//new Coordinate(coordinate);
			}
		}
		return coordinates;
	}

	@Override
	public boolean isEmpty() {
		for (Geometry geometry : geometries) {
			if (!geometry.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int getNumGeometries() {
		return geometries.size();
	}

	@Override
	public Geometry getGeometry(int index) {
		return geometries.get(index);
	}

	@Override
	public int getNumPoints() {
		int numPoints = 0;
		for (Geometry geometry : geometries) {
			numPoints += geometry.getNumPoints();
		}
		return numPoints;
	}

	@Override
	public String getGeometryType() {
		return "GeometryCollection";
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof GeometryCollection)) {
			return false;
		}

		GeometryCollection otherCollection = (GeometryCollection) other;
		if (geometries.size() != otherCollection.geometries.size()) {
			return false;
		}

		for (int i = 0; i < geometries.size(); i++) {
			if (!geometries.get(i).equals(otherCollection.geometries.get(i))) {
				return false;
			}
		}
		return true;
	}

}
