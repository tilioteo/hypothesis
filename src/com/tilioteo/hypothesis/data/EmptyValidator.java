/**
 * 
 */
package com.tilioteo.hypothesis.data;

import com.tilioteo.hypothesis.common.Strings;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class EmptyValidator extends AbstractValidator {
	
	public EmptyValidator(String message) {
		super(message);
	}

	@Override
	public boolean isValid(Object value) {
		if (null == value || !(value instanceof String)) {
			return false;
		}
		
		return !Strings.isNullOrEmpty(((String)value).trim());
	}
	
}
