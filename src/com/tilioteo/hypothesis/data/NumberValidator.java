/**
 * 
 */
package com.tilioteo.hypothesis.data;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter.ConversionException;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class NumberValidator extends AbstractValidator {
	
	private StringToDoubleConverter converter;

	public NumberValidator(String message) {
		super(message);
		converter = new StringToDoubleConverter();
	}

	@Override
	public boolean isValid(Object value) {
		if (null == value || Number.class.isAssignableFrom(value.getClass())) {
			return true;
		}
		
		if (!(value instanceof String)) {
			return false;
		}

		try {
			@SuppressWarnings("unused")
			Number numberValue = converter.convertToModel((String) value, Double.class, Locale.ENGLISH);
			return true;
		} catch (ConversionException e) {
		}
		return false;
	}

}
