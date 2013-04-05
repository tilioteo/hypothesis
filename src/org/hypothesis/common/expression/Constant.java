/**
 * 
 */
package org.hypothesis.common.expression;

import org.hypothesis.common.Strings;
import org.hypothesis.common.constants.StringConstants;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
public class Constant extends Primitive {

	public Constant(String value) {
		assert (!Strings.isNullOrEmpty(value));
		
		boolean parsed = false;
		
		if (value.startsWith(StringConstants.STR_DOUBLE_QUOTE) &&
				value.endsWith(StringConstants.STR_DOUBLE_QUOTE)) {
			setValue(value.substring(1, value.length()-1));
		} else if (value.indexOf(StringConstants.STR_DOT) >= 0) {
			try {
				Double val = Double.parseDouble(value);
				setValue(val);
				parsed = true;
			} catch (NumberFormatException e) {}
			
		}
		if (!parsed) {
			try {
				Integer val = Integer.parseInt(value);
				setValue(val);
				parsed = true;
			} catch (NumberFormatException e) {}
		}
		if (!parsed) {
			if (value.equalsIgnoreCase(StringConstants.STR_BOOL_TRUE)) {
				setValue(Boolean.TRUE);
			} else if (value.equalsIgnoreCase(StringConstants.STR_BOOL_FALSE)) {
				setValue(Boolean.FALSE);
			}
		}
	}
}
