/**
 * 
 */
package org.vaadin.maps.client.geometry.wkb;

import org.vaadin.maps.client.emul.io.IOException;
import org.vaadin.maps.client.geometry.CoordinateSequence;
import org.vaadin.maps.client.geometry.Geometry;
import org.vaadin.maps.client.geometry.GeometryCollection;
import org.vaadin.maps.client.geometry.LineString;
import org.vaadin.maps.client.geometry.LinearRing;
import org.vaadin.maps.client.geometry.MultiLineString;
import org.vaadin.maps.client.geometry.MultiPoint;
import org.vaadin.maps.client.geometry.MultiPolygon;
import org.vaadin.maps.client.geometry.Point;
import org.vaadin.maps.client.geometry.Polygon;
import org.vaadin.maps.client.io.ByteArrayInStream;
import org.vaadin.maps.client.io.ByteOrderDataInStream;
import org.vaadin.maps.client.io.ByteOrderValues;
import org.vaadin.maps.client.io.InStream;
import org.vaadin.maps.client.io.ParseException;

/**
 * @author Kamil Morong - Hypothesis
 * 
 *         Based on com.vividsolutions.jts.io.WKBReader from The JTS Topology
 *         Suite
 */
public class WKBReader {

	/**
	 * Converts a hexadecimal string to a byte array.
	 * 
	 * @param hex
	 *            a string containing hex digits
	 */
	public static byte[] hexToBytes(String hex) {
		int byteLen = hex.length() / 2;
		byte[] bytes = new byte[byteLen];

		for (int i = 0; i < hex.length() / 2; i++) {
			int i2 = 2 * i;
			if (i2 + 1 > hex.length())
				throw new IllegalArgumentException("Hex string has odd length");

			int nib1 = hexToInt(hex.charAt(i2));
			int nib0 = hexToInt(hex.charAt(i2 + 1));
			byte b = (byte) ((nib1 << 4) + (byte) nib0);
			bytes[i] = b;
		}
		return bytes;
	}

	private static int hexToInt(char hex) {
		int nib = Character.digit(hex, 16);
		if (nib < 0)
			throw new IllegalArgumentException("Invalid hex digit");
		return nib;
	}

	private static final String INVALID_GEOM_TYPE_MSG = "Invalid geometry type encountered in ";

	// private PrecisionModel precisionModel;
	// default dimension - will be set on read
	private int inputDimension = 2;
	private boolean hasSRID = false;
	@SuppressWarnings("unused")
	private int SRID = 0;
	private ByteOrderDataInStream dis = new ByteOrderDataInStream();
	private double[] ordValues;
	private int targetDim = 2;

	public WKBReader() {

	}

	/**
	 * Reads a single {@link VectorObject} from a byte array.
	 * 
	 * @param bytes
	 *            the byte array to read from
	 * @return the VectorObject read
	 * @throws ParseException
	 *             if a parse exception occurs
	 */
	public Geometry read(byte[] bytes) throws ParseException {
		// possibly reuse the ByteArrayInStream?
		// don't throw IOExceptions, since we are not doing any I/O
		try {
			return read(new ByteArrayInStream(bytes));
		} catch (IOException ex) {
			throw new RuntimeException("Unexpected IOException caught: "
					+ ex.getMessage());
		}
	}

/**
	   * Reads a {@link VectorObject} from an {@link InStream).
	   *
	   * @param is the stream to read from
	   * @return the VectorObject read
	   * @throws IOException
	   * @throws ParseException
	   */
	public Geometry read(InStream is) throws IOException, ParseException {
		dis.setInStream(is);
		Geometry g = readGeometry();
		// setSRID(g);
		return g;
	}

	private Geometry readGeometry() throws IOException, ParseException {
		// determine byte order
		byte byteOrder = dis.readByte();
		// default is big endian
		if (byteOrder == WKBConstants.wkbNDR)
			dis.setOrder(ByteOrderValues.LITTLE_ENDIAN);

		int typeInt = dis.readInt();
		int geometryType = typeInt & 0xff;
		// determine if Z values are present
		boolean hasZ = (typeInt & 0x80000000) != 0;
		inputDimension = hasZ ? 3 : 2;
		// determine if SRIDs are present
		hasSRID = (typeInt & 0x20000000) != 0;

		if (hasSRID) {
			SRID = dis.readInt();
		}

		// only allocate ordValues buffer if necessary
		if (ordValues == null || ordValues.length < inputDimension)
			ordValues = new double[inputDimension];

		switch (geometryType) {
		case WKBConstants.wkbPoint:
			return readPoint();
		case WKBConstants.wkbLineString:
			return readLineString();
		case WKBConstants.wkbPolygon:
			return readPolygon();
		case WKBConstants.wkbMultiPoint:
			return readMultiPoint();
		case WKBConstants.wkbMultiLineString:
			return readMultiLineString();
		case WKBConstants.wkbMultiPolygon:
			return readMultiPolygon();
		case WKBConstants.wkbGeometryCollection:
			return readGeometryCollection();
		}
		throw new ParseException("Unknown WKB type " + geometryType);
		// return null;
	}

	private Point readPoint() throws IOException {
		CoordinateSequence pts = readCoordinateSequence(1);
		return new Point(pts);
	}

	private LineString readLineString() throws IOException {
		int size = dis.readInt();
		CoordinateSequence pts = readCoordinateSequence(size);
		return new LineString(pts);
	}

	private LinearRing readLinearRing() throws IOException {
		int size = dis.readInt();
		CoordinateSequence pts = readCoordinateSequence(size);
		return new LinearRing(pts);
	}

	private Polygon readPolygon() throws IOException {
		int numRings = dis.readInt();
		LinearRing[] holes = null;
		if (numRings > 1)
			holes = new LinearRing[numRings - 1];

		LinearRing shell = readLinearRing();
		for (int i = 0; i < numRings - 1; i++) {
			holes[i] = readLinearRing();
		}
		return new Polygon(shell, holes);
	}

	private MultiPoint readMultiPoint() throws IOException, ParseException {
		int numGeom = dis.readInt();
		Point[] geoms = new Point[numGeom];
		for (int i = 0; i < numGeom; i++) {
			Geometry g = readGeometry();
			if (!(g instanceof Point))
				throw new ParseException(INVALID_GEOM_TYPE_MSG + "MultiPoint");
			geoms[i] = (Point) g;
		}
		return new MultiPoint(geoms);
	}

	private MultiLineString readMultiLineString() throws IOException,
			ParseException {
		int numGeom = dis.readInt();
		LineString[] geoms = new LineString[numGeom];
		for (int i = 0; i < numGeom; i++) {
			Geometry g = readGeometry();
			if (!(g instanceof LineString))
				throw new ParseException(INVALID_GEOM_TYPE_MSG
						+ "MultiLineString");
			geoms[i] = (LineString) g;
		}
		return new MultiLineString(geoms);
	}

	private MultiPolygon readMultiPolygon() throws IOException, ParseException {
		int numGeom = dis.readInt();
		Polygon[] geoms = new Polygon[numGeom];
		for (int i = 0; i < numGeom; i++) {
			Geometry g = readGeometry();
			if (!(g instanceof Polygon))
				throw new ParseException(INVALID_GEOM_TYPE_MSG + "MultiPolygon");
			geoms[i] = (Polygon) g;
		}
		return new MultiPolygon(geoms);
	}

	private GeometryCollection readGeometryCollection() throws IOException,
			ParseException {
		int numGeom = dis.readInt();
		Geometry[] geoms = new Geometry[numGeom];
		for (int i = 0; i < numGeom; i++) {
			geoms[i] = readGeometry();
		}
		return new GeometryCollection(geoms);
	}

	private CoordinateSequence readCoordinateSequence(int size)
			throws IOException {
		CoordinateSequence seq = new CoordinateSequence(size);

		for (int i = 0; i < size; i++) {
			readCoordinate();
			for (int j = 0; j < targetDim; j++) {
				seq.setOrdinate(i, j, ordValues[j]);
			}
		}
		return seq;
	}

	/**
	 * Reads a coordinate value with the specified dimensionality. Makes the X
	 * and Y ordinates precise according to the precision model in use.
	 */
	private void readCoordinate() throws IOException {
		for (int i = 0; i < inputDimension; i++) {
			ordValues[i] = dis.readDouble();
		}
	}

}
