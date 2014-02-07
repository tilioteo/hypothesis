/**
 * 
 */
package org.vaadin.maps.client.geometry;

/**
 * @author morong
 * 
 */
public class Coordinate {

	/**
	 * Standard ordinate index values
	 */
	static final int X = 0;
	static final int Y = 1;
	/*
	 * int Z = 2; int M = 3;
	 */

	/**
	 * The x-coordinate.
	 */
	public double x;
	/**
	 * The y-coordinate.
	 */
	public double y;

	/**
	 * The z-coordinate.
	 */
	// public double z;

	/**
	 * Constructs a <code>Coordinate</code> at (x,y,z).
	 * 
	 * @param x
	 *            the x-value
	 * @param y
	 *            the y-value
	 * @param z
	 *            the z-value
	 */
	public Coordinate(double x, double y/* , double z */) {
		this.x = x;
		this.y = y;
		// this.z = z;
	}

	/**
	 * Constructs a <code>Coordinate</code> at (0,0,NaN).
	 */
	public Coordinate() {
		this(0.0, 0.0);
	}

	/**
	 * Constructs a <code>Coordinate</code> having the same (x,y,z) values as
	 * <code>other</code>.
	 * 
	 * @param c
	 *            the <code>Coordinate</code> to copy.
	 */
	public Coordinate(Coordinate c) {
		this(c.x, c.y/* , c.z */);
	}

	/**
	 * Constructs a <code>Coordinate</code> at (x,y,NaN).
	 * 
	 * @param x
	 *            the x-value
	 * @param y
	 *            the y-value
	 */
	/*
	 * public Coordinate(double x, double y) { this(x, y, Double.NaN); }
	 */

	/**
	 * Sets this <code>Coordinate</code>s (x,y,z) values to that of
	 * <code>other</code> .
	 * 
	 * @param other
	 *            the <code>Coordinate</code> to copy
	 */
	public void setCoordinate(Coordinate other) {
		x = other.x;
		y = other.y;
		// z = other.z;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getOrdinate(int ordinateIndex) {
		switch (ordinateIndex) {
		case Coordinate.X:
			return x;
		case Coordinate.Y:
			return y;
			// case Coordinate.Z: return z;
		}
		return Double.NaN;
	}

	protected void setOrdinate(int ordinateIndex, double value) {
		switch (ordinateIndex) {
		case Coordinate.X:
			x = value;
		case Coordinate.Y:
			y = value;
			// case Coordinate.Z: z = value;
		}
	}

	public void setXY(double x, double y) {
		this.x = x;
		this.y = y;		
	}

	/**
	 * Returns whether the planar projections of the two <code>Coordinate</code>
	 * s are equal.
	 * 
	 * @param other
	 *            a <code>Coordinate</code> with which to do the 2D comparison.
	 * @return <code>true</code> if the x- and y-coordinates are equal; the
	 *         z-coordinates do not have to be equal.
	 */
	protected/* public */boolean equals2D(Coordinate other) {
		if (x != other.x) {
			return false;
		}

		if (y != other.y) {
			return false;
		}

		return true;
	}

	/**
	 * Returns <code>true</code> if <code>other</code> has the same values for
	 * the x and y ordinates. Since Coordinates are 2.5D, this routine ignores
	 * the z value when making the comparison.
	 * 
	 * @param other
	 *            a <code>Coordinate</code> with which to do the comparison.
	 * @return <code>true</code> if <code>other</code> is a
	 *         <code>Coordinate</code> with the same values for the x and y
	 *         ordinates.
	 */
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Coordinate)) {
			return false;
		}
		return equals2D((Coordinate) other);
	}

	/**
	 * Compares this {@link Coordinate} with the specified {@link Coordinate}
	 * for order. This method ignores the z value when making the comparison.
	 * Returns:
	 * <UL>
	 * <LI>-1 : this.x < other.x || ((this.x == other.x) && (this.y < other.y))
	 * <LI>0 : this.x == other.x && this.y = other.y
	 * <LI>1 : this.x > other.x || ((this.x == other.x) && (this.y > other.y))
	 * 
	 * </UL>
	 * Note: This method assumes that ordinate values are valid numbers. NaN
	 * values are not handled correctly.
	 * 
	 * @param o
	 *            the <code>Coordinate</code> with which this
	 *            <code>Coordinate</code> is being compared
	 * @return -1, zero, or 1 as this <code>Coordinate</code> is less than,
	 *         equal to, or greater than the specified <code>Coordinate</code>
	 */
	public int compareTo(Object o) {
		Coordinate other = (Coordinate) o;

		if (x < other.x)
			return -1;
		if (x > other.x)
			return 1;
		if (y < other.y)
			return -1;
		if (y > other.y)
			return 1;
		return 0;
	}

	/**
	 * Returns <code>true</code> if <code>other</code> has the same values for
	 * x, y and z.
	 * 
	 * @param other
	 *            a <code>Coordinate</code> with which to do the 3D comparison.
	 * @return <code>true</code> if <code>other</code> is a
	 *         <code>Coordinate</code> with the same values for x, y and z.
	 */
	/*
	 * public boolean equals3D(Coordinate other) { return (x == other.x) && (y
	 * == other.y) && ((z == other.z) || (Double.isNaN(z) &&
	 * Double.isNaN(other.z))); }
	 */

	/**
	 * Returns a <code>String</code> of the form <I>(x,y,z)</I> .
	 * 
	 * @return a <code>String</code> of the form <I>(x,y,z)</I>
	 */
	@Override
	public String toString() {
		return "(" + x + ", " + y + /* ", " + z + */")";
	}

	public double distance(Coordinate p) {
		double dx = x - p.x;
		double dy = y - p.y;

		return Math.sqrt(dx * dx + dy * dy);
	}

	public static void swap(Coordinate a, Coordinate b) {
		double tmp = a.x;
		a.x = b.x;
		b.x = tmp;

		tmp = a.y;
		a.y = b.y;
		b.y = tmp;
	}

}
