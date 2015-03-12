/**
 * 
 */
package com.tilioteo.hypothesis.data;

import java.util.Locale;

import com.tilioteo.hypothesis.common.Strings;
import com.tilioteo.hypothesis.core.Messages;
import com.vaadin.data.util.converter.AbstractStringToNumberConverter;

/**
 * @author kamil
 * 
 */
@SuppressWarnings("serial")
public class StringToDoubleConverter extends
		AbstractStringToNumberConverter<Double> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vaadin.data.util.converter.Converter#convertToModel(java.lang.Object,
	 * java.lang.Class, java.util.Locale)
	 */
	@Override
	public Double convertToModel(String value,
			Class<? extends Double> targetType, Locale locale)
			throws ConversionException {

		if (Strings.isNullOrEmpty(value)) {
			return null;
		}

		Double result = null;
		try {
			result = Double.parseDouble(value.replace(",", "."));
		} catch (NumberFormatException e) {
			throw new ConversionException(Messages.getString("Error.Convertion", value, getModelType().getName()));
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.data.util.converter.Converter#getModelType()
	 */
	@Override
	public Class<Double> getModelType() {
		return Double.class;
	}

}
