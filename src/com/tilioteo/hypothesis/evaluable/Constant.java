/**
 * 
 */
package com.tilioteo.hypothesis.evaluable;

import com.tilioteo.common.Strings;
import com.tilioteo.hypothesis.common.StringConstants;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
@SuppressWarnings("serial")
public class Constant extends Primitive {
	
	private Class<?> type;

	public Constant(String value) {
		assert (!Strings.isNullOrEmpty(value));
		
		boolean parsed = false;
		
		if (value.startsWith(StringConstants.STR_DOUBLE_QUOTE) &&
				value.endsWith(StringConstants.STR_DOUBLE_QUOTE)) {
			setValue(value.substring(1, value.length()-1));
			type = String.class;
			parsed = true;
			
		} else if (value.indexOf(StringConstants.STR_DOT) >= 0) {
			try {
				Double val = Double.parseDouble(value);
				setValue(val);
				type = Double.class;
				parsed = true;
			} catch (NumberFormatException e) {}
			
		}
		if (!parsed) {
			try {
				Integer val = Integer.parseInt(value);
				setValue(val);
				type = Integer.class;
				parsed = true;
			} catch (NumberFormatException e) {}
		}
		if (!parsed) {
			if (value.equalsIgnoreCase(StringConstants.STR_BOOL_TRUE)) {
				setValue(Boolean.TRUE);
			} else if (value.equalsIgnoreCase(StringConstants.STR_BOOL_FALSE)) {
				setValue(Boolean.FALSE);
			}
			type = Boolean.class;
		}
	}
	
	@Override
	public String toString() {
		Object value = getValue();
		if (type == String.class) {
			return value != null ? StringConstants.STR_DOUBLE_QUOTE + value + StringConstants.STR_DOUBLE_QUOTE : "<null>";
		} else if (Number.class.isAssignableFrom(type)) {
			return value != null ? value.toString() : "<NaN>"; 
		} else {
			return getValue() != null ? value.toString() : "<null>";
		}
	}
}
