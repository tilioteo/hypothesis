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

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a linear polygon, which may include holes. The shell and holes of
 * the polygon are represented by {@link LinearRing}s. In a valid polygon, holes
 * may touch the shell or other holes at a single point. However, no sequence of
 * touching holes may split the polygon into two pieces. The orientation of the
 * rings in the polygon does not matter.
 * <p>
 * The shell and holes must conform to the assertions specified in the <A
 * HREF="http://www.opengis.org/techno/specs.htm">OpenGIS Simple Features
 * Specification for SQL</A> .
 * 
 * @version 1.7
 */
@SuppressWarnings("serial")
public class Polygon extends Geometry {

	/**
	 * The exterior boundary, or <code>null</code> if this <code>Polygon</code>
	 * is empty.
	 */
	protected LinearRing shell = null;

	/**
	 * The interior boundaries, if any. This instance var is never null. If
	 * there are no holes, the array is of zero length.
	 */
	protected List<LinearRing> holes = new LinkedList<LinearRing>();

	public Polygon(LinearRing shell) {
		this(shell, 0);
	}

	/**
	 * Constructs a <code>Polygon</code> with the given exterior boundary.
	 * 
	 * @param shell
	 *            the outer boundary of the new <code>Polygon</code>, or
	 *            <code>null</code> or an empty <code>LinearRing</code> if the
	 *            empty geometry is to be created.
	 * @param precisionModel
	 *            the specification of the grid of allowable points for this
	 *            <code>Polygon</code>
	 * @param SRID
	 *            the ID of the Spatial Reference System used by this
	 *            <code>Polygon</code>
	 */
	public Polygon(LinearRing shell, int SRID) {
		this(shell, null, SRID);
	}

	/**
	 * Constructs a <code>Polygon</code> with the given exterior boundary and
	 * interior boundaries.
	 * 
	 * @param shell
	 *            the outer boundary of the new <code>Polygon</code>, or
	 *            <code>null</code> or an empty <code>LinearRing</code> if the
	 *            empty geometry is to be created.
	 * @param holes
	 *            the inner boundaries of the new <code>Polygon</code> , or
	 *            <code>null</code> or empty <code>LinearRing</code>s if the
	 *            empty geometry is to be created.
	 * @param precisionModel
	 *            the specification of the grid of allowable points for this
	 *            <code>Polygon</code>
	 * @param SRID
	 *            the ID of the Spatial Reference System used by this
	 *            <code>Polygon</code>
	 */
	public Polygon(LinearRing shell, LinearRing[] holes) {
		this(shell, holes, 0);
	}

	/**
	 * Constructs a <code>Polygon</code> with the given exterior boundary and
	 * interior boundaries.
	 * 
	 * @param shell
	 *            the outer boundary of the new <code>Polygon</code>, or
	 *            <code>null</code> or an empty <code>LinearRing</code> if the
	 *            empty geometry is to be created.
	 * @param holes
	 *            the inner boundaries of the new <code>Polygon</code> , or
	 *            <code>null</code> or empty <code>LinearRing</code>s if the
	 *            empty geometry is to be created.
	 */
	public Polygon(LinearRing shell, LinearRing[] holes, int SRID) {

		if (shell == null) {
			throw new IllegalArgumentException("shell cannot be null");
		}

		if (holes != null && holes.length > 0) {
			if (hasNullElements(holes)) {
				throw new IllegalArgumentException(
						"holes must not contain null elements");
			}
			if (shell.isEmpty() && hasNonEmptyElements(holes)) {
				throw new IllegalArgumentException(
						"shell is empty but holes are not");
			}
		}
		this.shell = new LinearRing(shell);
		if (holes != null) {
			for (LinearRing hole : holes) {
				this.holes.add(new LinearRing(hole));
			}
		}
	}
	
	public Polygon(Polygon polygon) {
		this(polygon.shell, polygon.holes != null ? polygon.holes.toArray(new LinearRing[0]) : null, polygon.SRID);
	}

	@Override
	public Coordinate getCoordinate() {
		return shell.getCoordinate();
	}

	@Override
	public Coordinate[] getCoordinates() {
		if (isEmpty()) {
			return new Coordinate[] {};
		}
		Coordinate[] coordinates = new Coordinate[getNumPoints()];

		int k = -1;
		for (Coordinate coordinate : shell.coordinates) {
			coordinates[++k] = coordinate;//new Coordinate(coordinate);
		}
		for (LinearRing hole : holes) {
			for (Coordinate coordinate : hole.coordinates) {
				coordinates[++k] = coordinate;//new Coordinate(coordinate);
			}
		}

		return coordinates;
	}

	@Override
	public int getNumPoints() {
		int numPoints = shell.getNumPoints();
		for (LinearRing hole : holes) {
			numPoints += hole.getNumPoints();
		}
		return numPoints;
	}

	@Override
	public boolean isEmpty() {
		return shell.isEmpty();
	}

	public LinearRing getShell() {
		return shell;
	}

	public int getNumHoles() {
		return holes.size();
	}

	public LinearRing getHole(int index) {
		if (index < 0 || index >= holes.size()) {
			throw new OutOfBoundsException();
		}

		return holes.get(index);
	}
	
	public List<LinearRing> getHoles() {
		return holes;
	}

	@Override
	public String getGeometryType() {
		return "Polygon";
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Polygon)) {
			return false;
		}

		Polygon otherPolygon = (Polygon) other;

		if (!shell.equals(otherPolygon.shell)) {
			return false;
		}

		if (holes.size() != otherPolygon.holes.size()) {
			return false;
		}

		for (int i = 0; i < holes.size(); i++) {
			if (!holes.get(i).equals(otherPolygon.holes.get(i))) {
				return false;
			}
		}
		return true;
	}

}
