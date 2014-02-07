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

import java.io.Serializable;

import org.vaadin.maps.client.geometry.wkt.WKTWriter;

/**
 * The base class for all geometric objects.
 * <P>
 * 
 * <H3>Binary Predicates</H3>
 * Because it is not clear at this time what semantics for spatial analysis
 * methods involving <code>GeometryCollection</code>s would be useful,
 * <code>GeometryCollection</code>s are not supported as arguments to binary
 * predicates (other than <code>convexHull</code>) or the <code>relate</code>
 * method.
 * 
 * <H3>Set-Theoretic Methods</H3>
 * 
 * The spatial analysis methods will return the most specific class possible to
 * represent the result. If the result is homogeneous, a <code>Point</code>,
 * <code>LineString</code>, or <code>Polygon</code> will be returned if the
 * result contains a single element; otherwise, a <code>MultiPoint</code>,
 * <code>MultiLineString</code>, or <code>MultiPolygon</code> will be returned.
 * If the result is heterogeneous a <code>GeometryCollection</code> will be
 * returned.
 * <P>
 * 
 * Because it is not clear at this time what semantics for set-theoretic methods
 * involving <code>GeometryCollection</code>s would be useful,
 * <code>GeometryCollections</code> are not supported as arguments to the
 * set-theoretic methods.
 * 
 * <H4>Representation of Computed Geometries</H4>
 * 
 * The SFS states that the result of a set-theoretic method is the "point-set"
 * result of the usual set-theoretic definition of the operation (SFS 3.2.21.1).
 * However, there are sometimes many ways of representing a point set as a
 * <code>Geometry</code>.
 * <P>
 * 
 * The SFS does not specify an unambiguous representation of a given point set
 * returned from a spatial analysis method. One goal of JTS is to make this
 * specification precise and unambiguous. JTS will use a canonical form for
 * <code>Geometry</code>s returned from spatial analysis methods. The canonical
 * form is a <code>Geometry</code> which is simple and noded:
 * <UL>
 * <LI>Simple means that the Geometry returned will be simple according to the
 * JTS definition of <code>isSimple</code>.
 * <LI>Noded applies only to overlays involving <code>LineString</code>s. It
 * means that all intersection points on <code>LineString</code>s will be
 * present as endpoints of <code>LineString</code>s in the result.
 * </UL>
 * This definition implies that non-simple geometries which are arguments to
 * spatial analysis methods must be subjected to a line-dissolve process to
 * ensure that the results are simple.
 * 
 * <H4>Constructed Points And The Precision Model</H4>
 * 
 * The results computed by the set-theoretic methods may contain constructed
 * points which are not present in the input <code>Geometry</code> s. These new
 * points arise from intersections between line segments in the edges of the
 * input <code>Geometry</code>s. In the general case it is not possible to
 * represent constructed points exactly. This is due to the fact that the
 * coordinates of an intersection point may contain twice as many bits of
 * precision as the coordinates of the input line segments. In order to
 * represent these constructed points explicitly, JTS must truncate them to fit
 * the <code>PrecisionModel</code>.
 * <P>
 * 
 * Unfortunately, truncating coordinates moves them slightly. Line segments
 * which would not be coincident in the exact result may become coincident in
 * the truncated representation. This in turn leads to "topology collapses" --
 * situations where a computed element has a lower dimension than it would in
 * the exact result.
 * <P>
 * 
 * When JTS detects topology collapses during the computation of spatial
 * analysis methods, it will throw an exception. If possible the exception will
 * report the location of the collapse.
 * <P>
 * 
 * #equals(Object) and #hashCode are not overridden, so that when two
 * topologically equal Geometries are added to HashMaps and HashSets, they
 * remain distinct. This behaviour is desired in many cases.
 * 
 * @version 1.7
 */
@SuppressWarnings("serial")
public abstract class Geometry implements Serializable {

	/*
	 * @SuppressWarnings("rawtypes") private final static Class[] sortedClasses
	 * = new Class[] { Point.class, MultiPoint.class, LineString.class,
	 * LinearRing.class, MultiLineString.class, Polygon.class,
	 * MultiPolygon.class, GeometryCollection.class };
	 */

	/**
	 * The ID of the Spatial Reference System used by this <code>Geometry</code>
	 */
	protected int SRID;

	/**
	 * Creates a new <tt>Geometry</tt> via the specified GeometryFactory.
	 * 
	 * @param factory
	 */
	public Geometry() {
	}
	
	public static final Geometry clone(Geometry geometry) {
		// respect this serie of class checking LinearRing->LineString->Point
		if (geometry instanceof LinearRing)
			return new LinearRing((LinearRing)geometry);
		else if (geometry instanceof LineString)
			return new LineString((LineString)geometry);
		else if (geometry instanceof Point)
			return new Point((Point)geometry);
		else if (geometry instanceof Polygon)
			return new Polygon((Polygon)geometry);
		else if (geometry instanceof GeometryCollection)
			return GeometryCollection.clone((GeometryCollection)geometry);
		else
			return null; // should not occur
	}

	/**
	 * Returns the name of this object's <code>com.vivid.jts.geom</code>
	 * interface.
	 * 
	 * @return the name of this <code>Geometry</code>s most specific
	 *         <code>com.vividsolutions.jts.geom</code> interface
	 */
	public abstract String getGeometryType();

	/**
	 * Returns true if the array contains any non-empty <code>Geometry</code>s.
	 * 
	 * @param geometries
	 *            an array of <code>Geometry</code>s; no elements may be
	 *            <code>null</code>
	 * @return <code>true</code> if any of the <code>Geometry</code>s
	 *         <code>isEmpty</code> methods return <code>false</code>
	 */
	protected static boolean hasNonEmptyElements(Geometry[] geometries) {
		for (int i = 0; i < geometries.length; i++) {
			if (!geometries[i].isEmpty()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if the array contains any <code>null</code> elements.
	 * 
	 * @param array
	 *            an array to validate
	 * @return <code>true</code> if any of <code>array</code>s elements are
	 *         <code>null</code>
	 */
	protected static boolean hasNullElements(Object[] array) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the ID of the Spatial Reference System used by the
	 * <code>Geometry</code>.
	 * <P>
	 * 
	 * JTS supports Spatial Reference System information in the simple way
	 * defined in the SFS. A Spatial Reference System ID (SRID) is present in
	 * each <code>Geometry</code> object. <code>Geometry</code> provides basic
	 * accessor operations for this field, but no others. The SRID is
	 * represented as an integer.
	 * 
	 * @return the ID of the coordinate space in which the <code>Geometry</code>
	 *         is defined.
	 * 
	 */
	public int getSRID() {
		return SRID;
	}

	/**
	 * Sets the ID of the Spatial Reference System used by the
	 * <code>Geometry</code>.
	 */
	public void setSRID(int SRID) {
		this.SRID = SRID;
	}

	/**
	 * Returns the number of {@link Geometry}s in a {@link GeometryCollection}
	 * (or 1, if the geometry is not a collection).
	 * 
	 * @return the number of geometries contained in this geometry
	 */
	public int getNumGeometries() {
		return 1;
	}

	/**
	 * Returns an element {@link Geometry} from a {@link GeometryCollection} (or
	 * <code>this</code>, if the geometry is not a collection).
	 * 
	 * @param n
	 *            the index of the geometry element
	 * @return the n'th geometry contained in this geometry
	 */
	public Geometry getGeometry(int index) {
		return this;
	}

	/**
	 * Returns a vertex of this <code>Geometry</code>.
	 * 
	 * @return a {@link Coordinate} which is a vertex of this
	 *         <code>Geometry</code>. Returns <code>null</code> if this Geometry
	 *         is empty
	 */
	public abstract Coordinate getCoordinate();

	/**
	 * Returns this <code>Geometry</code> s vertices. If you modify the
	 * coordinates in this array, be sure to call #geometryChanged afterwards.
	 * The <code>Geometry</code>s contained by composite <code>Geometry</code>s
	 * must be Geometry's; that is, they must implement
	 * <code>getCoordinates</code>.
	 * 
	 * @return the vertices of this <code>Geometry</code>
	 */
	public abstract Coordinate[] getCoordinates();

	/**
	 * Returns the count of this <code>Geometry</code>s vertices. The
	 * <code>Geometry</code> s contained by composite <code>Geometry</code>s
	 * must be Geometry's; that is, they must implement
	 * <code>getNumPoints</code>
	 * 
	 * @return the number of vertices in this <code>Geometry</code>
	 */
	public abstract int getNumPoints();

	/**
	 * Returns whether or not the set of points in this <code>Geometry</code> is
	 * empty.
	 * 
	 * @return <code>true</code> if this <code>Geometry</code> equals the empty
	 *         geometry
	 */
	public abstract boolean isEmpty();

	/**
	 * Returns <code>true</code> if this geometry is equal to the specified
	 * geometry.
	 * <p>
	 * The <code>equals</code> predicate has the following equivalent
	 * definitions:
	 * <ul>
	 * <li>The two geometries have at least one point in common, and no point of
	 * either geometry lies in the exterior of the other geometry.
	 * <li>The DE-9IM Intersection Matrix for the two geometries is T*F**FFF*
	 * </ul>
	 * 
	 * @param other
	 *            the <code>Geometry</code> with which to compare this
	 *            <code>Geometry</code>
	 * @return <code>true</code> if the two <code>Geometry</code>s are equal
	 */
	@Override
	public abstract boolean equals(Object other);

	@Override
	public String toString() {
		return toText();
	}

	/**
	 * Returns the Well-known Text representation of this <code>Geometry</code>.
	 * For a definition of the Well-known Text format, see the OpenGIS Simple
	 * Features Specification.
	 * 
	 * @return the Well-known Text representation of this <code>Geometry</code>
	 */
	public String toText() {
		WKTWriter writer = new WKTWriter();
		return writer.write(this);
	}

	/**
	 * Throws an exception if <code>g</code>'s class is
	 * <code>GeometryCollection</code> . (Its subclasses do not trigger an
	 * exception).
	 * 
	 * @param g
	 *            the <code>Geometry</code> to check
	 * @throws IllegalArgumentException
	 *             if <code>g</code> is a <code>GeometryCollection</code> but
	 *             not one of its subclasses
	 */
	protected void checkNotGeometryCollection(Geometry g) {
		// Don't use instanceof because we want to allow subclasses
		if (g.getClass()
				.getName()
				.equals(GeometryCollection.class.getName() /* "com.vividsolutions.jts.geom.GeometryCollection" */)) {
			throw new IllegalArgumentException(
					"This method does not support GeometryCollection arguments");
		}
	}

	/**
	 * Returns the first non-zero result of <code>compareTo</code> encountered
	 * as the two <code>Collection</code>s are iterated over. If, by the time
	 * one of the iterations is complete, no non-zero result has been
	 * encountered, returns 0 if the other iteration is also complete. If
	 * <code>b</code> completes before <code>a</code>, a positive number is
	 * returned; if a before b, a negative number.
	 * 
	 * @param a
	 *            a <code>Collection</code> of <code>Comparable</code>s
	 * @param b
	 *            a <code>Collection</code> of <code>Comparable</code>s
	 * @return the first non-zero <code>compareTo</code> result, if any;
	 *         otherwise, zero
	 */
	/*
	 * protected int compare(Collection a, Collection b) { Iterator i =
	 * a.iterator(); Iterator j = b.iterator(); while (i.hasNext() &&
	 * j.hasNext()) { Comparable aElement = (Comparable) i.next(); Comparable
	 * bElement = (Comparable) j.next(); int comparison =
	 * aElement.compareTo(bElement); if (comparison != 0) { return comparison; }
	 * } if (i.hasNext()) { return 1; } if (j.hasNext()) { return -1; } return
	 * 0; }
	 */

	/*
	 * private int getClassSortIndex() { for (int i = 0; i <
	 * sortedClasses.length; i++) { if (sortedClasses[i].isInstance(this)) {
	 * return i; } } // Assert.shouldNeverReachHere("Class not supported: " + //
	 * this.getClass()); return -1; }
	 */

}
