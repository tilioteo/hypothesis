/**
 * 
 */
package com.tilioteo.hypothesis.data;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.data.validator.DoubleRangeValidator;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class NumberRangeValidator extends DoubleRangeValidator implements Validator {
	
	private StringToDoubleConverter converter;

	public NumberRangeValidator(String message, Double minValue, Double maxValue) {
		super(message, minValue, maxValue);
		converter = new StringToDoubleConverter();
	}

	@Override
	public void validate(Object value) throws InvalidValueException {
		Double doubleValue = null;
		if (value != null && Double.class.isAssignableFrom(value.getClass())) {
			doubleValue = (Double) value;
		} else if (value instanceof String) {
			try {
				doubleValue = converter.convertToModel((String) value, Double.class, Locale.ENGLISH);
			} catch (ConversionException e) {
			}
		}
		
		super.validate(doubleValue);
	}
	
}
