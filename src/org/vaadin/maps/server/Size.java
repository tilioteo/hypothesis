/**
 * 
 */
package org.vaadin.maps.server;

/**
 * @author kamil
 *
 */
public class Size {

	int width = 0;
	int height = 0;
	
	public Size() {
		
	}
	
	public Size(int width, int height) {
		this.width = Math.abs(width);
		this.height = Math.abs(height);
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		if (width > 0) {
			this.width = width;
		} else {
			this.width = 0;
		}
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		if (height > 0) {
			this.height = height;
		} else {
			this.height = 0;
		}
	}
	
	public boolean isValid() {
		return width > 0 && height > 0;
	}
	
	public void expand(int x, int y) {
		width += x;
		height += y;
		
		if (width < 0) {
			width = 0;
		}
		if (height < 0) {
			height = 0;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Size)) {
			return false;
		}
		
		Size other = (Size) obj;
		
		return (width == other.width && height == other.height);
	}
	
	@Override
	public Size clone() {
		return new Size(width, height);
	}

	@Override
	public String toString() {
		return "width=" + width + ",height=" + height;
	}
}
