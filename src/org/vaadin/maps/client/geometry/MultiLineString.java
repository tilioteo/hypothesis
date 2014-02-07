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
 * Basic implementation of <code>MultiLineString</code>.
 * 
 * @version 1.7
 */
@SuppressWarnings("serial")
public class MultiLineString extends GeometryCollection {

	/**
	 * @param lineStrings
	 *            the <code>LineString</code>s for this
	 *            <code>MultiLineString</code>, or <code>null</code> or an empty
	 *            array to create the empty geometry. Elements may be empty
	 *            <code>LineString</code>s, but not <code>null</code>s.
	 */
	public MultiLineString(LineString[] lineStrings) {
		this(lineStrings, 0);
	}

	/**
	 * Constructs a <code>MultiLineString</code>.
	 * 
	 * @param lineStrings
	 *            the <code>LineString</code>s for this
	 *            <code>MultiLineString</code> , or <code>null</code> or an
	 *            empty array to create the empty geometry. Elements may be
	 *            empty <code>LineString</code>s, but not <code>null</code> s.
	 * @param precisionModel
	 *            the specification of the grid of allowable points for this
	 *            <code>MultiLineString</code>
	 * @param SRID
	 *            the ID of the Spatial Reference System used by this
	 *            <code>MultiLineString</code>
	 */
	public MultiLineString(LineString[] lineStrings, int SRID) {
		super(lineStrings, SRID);
	}

	public MultiLineString(MultiLineString multiLineString) {
		super(multiLineString.SRID);
		for (Geometry geometry : multiLineString.geometries) {
			geometries.add(Geometry.clone(geometry));
		}
	}

	public String getGeometryType() {
		return "MultiLineString";
	}

	public boolean isClosed() {
		if (isEmpty()) {
			return false;
		}
		for (Geometry geometry : geometries) {
			if (!((LineString) geometry).isClosed()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Creates a {@link MultiLineString} in the reverse order to this object.
	 * Both the order of the component LineStrings and the order of their
	 * coordinate sequences are reversed.
	 * 
	 * @return a {@link MultiLineString} in the reverse order
	 */
	public MultiLineString reverse() {
		int numLineStrings = getNumGeometries();
		LineString[] lineStrings = new LineString[numLineStrings];

		for (int i = 0; i < geometries.size(); i++) {
			lineStrings[numLineStrings - 1 - i] = ((LineString) getGeometry(i)).reverse();
		}
		return new MultiLineString(lineStrings, SRID);
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MultiLineString)) {
			return false;
		}

		return super.equals(other);
	}

}
