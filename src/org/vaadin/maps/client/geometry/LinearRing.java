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
 * Models an OGC SFS <code>LinearRing</code>. A LinearRing is a LineString which
 * is both closed and simple. In other words, the first and last coordinate in
 * the ring must be equal, and the interior of the ring must not self-intersect.
 * Either orientation of the ring is allowed.
 * 
 * @version 1.7
 */
@SuppressWarnings("serial")
public class LinearRing extends LineString {

	public LinearRing(Coordinate... coordinates) {
		this(coordinates, 0);
	}
	
	public LinearRing(Coordinate[] coordinates, int SRID) {
		this(new CoordinateSequence(coordinates), SRID);
	}
	
	/**
	 * @param coordinates
	 *            contains the single coordinate on which to base this
	 *            <code>Point</code> , or <code>null</code> to create the empty
	 *            geometry.
	 */
	public LinearRing(CoordinateSequence coordinates) {
		this(coordinates, 0);
	}

	public LinearRing(CoordinateSequence coordinates, int SRID) {
		super(coordinates, SRID);
		validateConstruction();
	}

	public LinearRing(LineString lineString) {
		this(lineString.getClosed().coordinates, lineString.SRID);
	}
	
	public LinearRing(LinearRing linearRing) {
		this(linearRing.coordinates, linearRing.SRID);
	}

	private void validateConstruction() {
		if (!isEmpty() && !super.isClosed()) {
			throw new IllegalArgumentException(
					"points must form a closed linestring");
		}
		if (getCoordinateSequence().size() >= 1
				&& getCoordinateSequence().size() <= 3) {
			throw new IllegalArgumentException(
					"Number of points must be 0 or >3");
		}
	}

	@Override
	public String getGeometryType() {
		return "LinearRing";
	}

}
