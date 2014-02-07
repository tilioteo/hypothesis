/**
 * 
 */
package org.vaadin.maps.ui.control;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
public enum ControlType {
	BUTTON(1), TOGGLE(2), TOOL(3);
	
	private static final Map<Integer, ControlType> lookup = new HashMap<Integer, ControlType>();

	static {
		for (ControlType controlType : EnumSet.allOf(ControlType.class))
			lookup.put(controlType.getCode(), controlType);
	}

	public static ControlType get(int code) {
		return lookup.get(code);
	}

	private int code;

	private ControlType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
