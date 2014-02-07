/**
 * 
 */
package org.vaadin.maps.exception;

import org.vaadin.maps.ui.GridLayout;

/**
 * @author morong
 *
 */
@SuppressWarnings("serial")
public class OutOfBoundsException extends java.lang.RuntimeException {

	private final GridLayout<?>.Area areaOutOfBounds;

	/**
	 * Constructs an <code>OoutOfBoundsException</code> with the specified
	 * detail message.
	 * 
	 * @param areaOutOfBounds
	 */
	public OutOfBoundsException(GridLayout<?>.Area areaOutOfBounds) {
		this.areaOutOfBounds = areaOutOfBounds;
	}

	/**
	 * Gets the area that is out of bounds.
	 * 
	 * @return the area out of Bound.
	 */
	public GridLayout<?>.Area getArea() {
		return areaOutOfBounds;
	}
}
