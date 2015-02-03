/**
 * 
 */
package org.vaadin.maps.server;

/**
 * @author kamil
 *
 */
public class Bounds {
	
	private static final int DEFAULT_DECIMAL = 6;

	private double left = 0.0;
	private double bottom = 0.0;
	private double right = 0.0;
	private double top = 0.0;

	public Bounds() {
	}
	
	public Bounds(double left, double bottom, double right, double top) {
		if (left <= right) {
			this.left = left;
			this.right = right;
		} else {
			this.left = right;
			this.right = left;
		}

		if (bottom < top) {
			this.bottom = bottom;
			this.top = top;
		} else {
			this.bottom = top;
			this.top = bottom;
		}
	}

	public double getLeft() {
		return left;
	}

	public double getBottom() {
		return bottom;
	}

	public double getRight() {
		return right;
	}

	public double getTop() {
		return top;
	}
	
	public LonLat getTopLeft() {
		return new LonLat(left, top);
	}

	public LonLat getTopRight() {
		return new LonLat(right, top);
	}

	public LonLat getBottomLeft() {
		return new LonLat(left, bottom);
	}

	public LonLat getBottomRight() {
		return new LonLat(right, bottom);
	}
	
	public double getWidth() {
		return right - left;
	}
	
	public void setWidth(double width) {
		if (width > 0) {
			right = left + width;
		}
	}
	
	public void setWidthCentered(double width) {
		if (width > 0) {
			double add = (width - getWidth()) / 2;
			left -= add;
			right += add;
		}
	}
	
	public double getHeight() {
		return top - bottom;
	}
	
	public void setHeight(double height) {
		if (height > 0) {
			bottom = top - height;
		}
	}
	
	public void setHeightCentered(double height) {
		if (height > 0) {
			double add = (height - getHeight()) / 2;
			bottom -= add;
			top += add;
		}
	}
	
	public LonLat getCenter() {
		return new LonLat((left + right) / 2, (bottom + top) / 2);
	}
	
	public Bounds scale(double ratio, LonLat origin) {
		if (null == origin) {
			origin = getCenter();
		}
		
		return new Bounds(
				(left - origin.getLon()) * ratio + origin.getLon(),
				(bottom - origin.getLat()) * ratio + origin.getLat(),
				(right - origin.getLon()) * ratio + origin.getLon(),
				(top - origin.getLat()) * ratio + origin.getLat());
	}
	
	public Bounds scale(double ratio) {
		return scale(ratio, null);
	}
	
	public Bounds add(double x, double y) {
		return new Bounds(left + x, bottom + y, right + x, top + y);
	}
	
	public boolean isValid() {
		return getWidth() > 0 && getHeight() > 0;
	}
	
	public void shift(double x, double y) {
		left += x;
		bottom += y;
		right += x;
		top += y;
	}
	
	public void extend(LonLat lonLat) {
		if (lonLat != null) {
			if (lonLat.getLon() < left) {
				left = lonLat.getLon();
			}
			if (lonLat.getLat() < bottom) {
				bottom = lonLat.getLat();
			}
			if (lonLat.getLon() > right) {
				right = lonLat.getLon();
			}
			if (lonLat.getLat() > top) {
				top = lonLat.getLat();
			}
		}
	}
	
	public void expand(double x, double y) {
		left -= x;
		bottom -= y;
		right += x;
		top += y;
	}
	
	public void extend(Bounds bounds) {
		if (bounds != null) {
			if (bounds.left < left) {
				left = bounds.left;
			}
			if (bounds.bottom < bottom) {
				bottom = bounds.bottom;
			}
			if (bounds.right > right) {
				right = bounds.right;
			}
			if (bounds.top > top) {
				top = bounds.top;
			}
		}
	}
	
	public String toBBOX(int decimal, boolean reverseAxisOrder) {
		if (decimal >= 0) {
			double multi = Math.pow(10, decimal);
			double xmin = Math.round(left * multi) / multi;
			double ymin = Math.round(bottom * multi) / multi;
			double xmax = Math.round(right * multi) / multi;
			double ymax = Math.round(top * multi) / multi;

			if (reverseAxisOrder) {
				return ymin + "," + xmin + "," + ymax + "," + xmax;
			} else {
				return xmin + "," + ymin + "," + xmax + "," + ymax;
			}
		} else {
			if (reverseAxisOrder) {
				return bottom + "," + left + "," + top + "," + right;
			} else {
				return left + "," + bottom + "," + right + "," + top;
			}
		}
	}

	public String toBBOX(int decimal) {
		return toBBOX(decimal, false);
	}

	public String toBBOX(boolean reverseAxisOrder) {
		return toBBOX(DEFAULT_DECIMAL, reverseAxisOrder);
	}

	public String toBBOX() {
		return toBBOX(DEFAULT_DECIMAL, false);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Bounds)) {
			return false;
		}
		
		Bounds other = (Bounds) obj;
		
		return (left == other.left && bottom == other.bottom && right == other.right && top == other.top);
	}
	
	@Override
	public Bounds clone() {
		return new Bounds(left, bottom, right, top);
	}

	@Override
	public String toString() {
		return "left=" + left + ",bottom=" + bottom + ",right=" + right + ",top=" + top + "\nwidth=" + getWidth() + ",height=" + getHeight();
	}
	
	public static Bounds fromBBOX(String bbox, boolean reverseAxisOrder) {
		if (bbox != null && !bbox.trim().isEmpty()) {
			String[] parts = bbox.split(",");
			if (parts.length >= 4) {
				double[] box = new double[4];
				for (int i = 0; i < 4; ++i) {
					try {
						box[i] = Double.parseDouble(parts[i].trim());
					} catch (Exception e) {
						return null;
					}
				}
				
				return reverseAxisOrder ?
						new Bounds(box[1], box[0], box[3], box[2]) :
							new Bounds(box[0], box[1], box[2], box[3]);
			}
		}
		return null;
	}

	public static Bounds fromBBOX(String bbox) {
		return fromBBOX(bbox, false);
	}
}
