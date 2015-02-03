/**
 * 
 */
package org.vaadin.maps.server;

/**
 * @author kamil
 *
 */
public class Pixel {

	private int x = 0;
	private int y = 0;
	
	public Pixel(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public Pixel add(int x, int y) {
		return new Pixel(this.x + x, this.y + y);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Pixel)) {
			return false;
		}
		
		Pixel other = (Pixel) obj;
		
		return (x == other.x && y == other.y);
	}
	
	@Override
	public Pixel clone() {
		return new Pixel(x, y);
	}
	
	@Override
	public String toString() {
		return "x=" + x + ",y=" + y;
	}

}
