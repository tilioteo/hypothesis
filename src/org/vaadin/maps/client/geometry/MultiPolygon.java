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
 * Basic implementation of <code>MultiPolygon</code>.
 * 
 * @version 1.7
 */
@SuppressWarnings("serial")
public class MultiPolygon extends GeometryCollection {

	/**
	 * @param polygons
	 *            the <code>Polygon</code>s for this <code>MultiPolygon</code>,
	 *            or <code>null</code> or an empty array to create the empty
	 *            geometry. Elements may be empty <code>Polygon</code>s, but not
	 *            <code>null</code>s. The polygons must conform to the
	 *            assertions specified in the <A
	 *            HREF="http://www.opengis.org/techno/specs.htm">OpenGIS Simple
	 *            Features Specification for SQL</A>.
	 */
	public MultiPolygon(Polygon[] polygons) {
		this(polygons, 0);
	}

	/**
	 * Constructs a <code>MultiPolygon</code>.
	 * 
	 * @param polygons
	 *            the <code>Polygon</code>s for this <code>MultiPolygon</code> ,
	 *            or <code>null</code> or an empty array to create the empty
	 *            geometry. Elements may be empty <code>Polygon</code>s, but not
	 *            <code>null</code> s. The polygons must conform to the
	 *            assertions specified in the <A
	 *            HREF="http://www.opengis.org/techno/specs.htm">OpenGIS Simple
	 *            Features Specification for SQL</A> .
	 * @param precisionModel
	 *            the specification of the grid of allowable points for this
	 *            <code>MultiPolygon</code>
	 * @param SRID
	 *            the ID of the Spatial Reference System used by this
	 *            <code>MultiPolygon</code>
	 */
	public MultiPolygon(Polygon[] polygons, int SRID) {
		super(polygons, SRID);
	}

	public MultiPolygon(MultiPolygon multiPolygon) {
		super(multiPolygon.SRID);
		for (Geometry geometry : multiPolygon.geometries) {
			geometries.add(Geometry.clone(geometry));
		}
	}

	@Override
	public String getGeometryType() {
		return "MultiPolygon";
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MultiPolygon)) {
			return false;
		}
		
		return super.equals(other);
	}
	
}
