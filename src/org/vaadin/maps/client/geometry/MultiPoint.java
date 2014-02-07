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
 * Models a collection of <code>Point</code>s.
 * 
 * @version 1.7
 */
@SuppressWarnings("serial")
public class MultiPoint extends GeometryCollection {

	/**
	 * @param points
	 *            the <code>Point</code>s for this <code>MultiPoint</code> , or
	 *            <code>null</code> or an empty array to create the empty
	 *            geometry. Elements may be empty <code>Point</code>s, but not
	 *            <code>null</code>s.
	 */
	public MultiPoint(Point[] points) {
		this(points, 0);
	}

	/**
	 * Constructs a <code>MultiPoint</code>.
	 * 
	 * @param points
	 *            the <code>Point</code>s for this <code>MultiPoint</code> , or
	 *            <code>null</code> or an empty array to create the empty
	 *            geometry. Elements may be empty <code>Point</code>s, but not
	 *            <code>null</code>s.
	 * @param precisionModel
	 *            the specification of the grid of allowable points for this
	 *            <code>MultiPoint</code>
	 * @param SRID
	 *            the ID of the Spatial Reference System used by this
	 *            <code>MultiPoint</code>
	 */
	public MultiPoint(Point[] points, int SRID) {
		super(points, SRID);
	}
	
	public MultiPoint(MultiPoint multiPoint) {
		super(multiPoint.SRID);
		for (Geometry geometry : multiPoint.geometries) {
			geometries.add(Geometry.clone(geometry));
		}
	}

	@Override
	public String getGeometryType() {
		return "MultiPoint";
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MultiPoint)) {
			return false;
		}

		return super.equals(other);
	}
	
}
