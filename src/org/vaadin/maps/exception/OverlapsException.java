/**
 * 
 */
package org.vaadin.maps.exception;

import org.vaadin.maps.ui.GridLayout;

import com.vaadin.ui.Component;

/**
 * @author morong
 *
 */
@SuppressWarnings("serial")
public class OverlapsException extends java.lang.RuntimeException {

	private final GridLayout<?>.Area existingArea;

	/**
	 * Constructs an <code>OverlapsException</code>.
	 * 
	 * @param existingArea
	 */
	public OverlapsException(GridLayout<?>.Area existingArea) {
		this.existingArea = existingArea;
	}

	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder();
		Component component = existingArea.getComponent();
		sb.append(component);
		sb.append("( type = ");
		sb.append(component.getClass().getName());
		if (component.getCaption() != null) {
			sb.append(", caption = \"");
			sb.append(component.getCaption());
			sb.append("\"");
		}
		sb.append(")");
		sb.append(" is already added to ");
		sb.append(existingArea.getChildData().column1);
		sb.append(",");
		sb.append(existingArea.getChildData().column1);
		sb.append(",");
		sb.append(existingArea.getChildData().row1);
		sb.append(",");
		sb.append(existingArea.getChildData().row2);
		sb.append("(column1, column2, row1, row2).");

		return sb.toString();
	}

	/**
	 * Gets the area .
	 * 
	 * @return the existing area.
	 */
	public GridLayout<?>.Area getArea() {
		return existingArea;
	}
}

