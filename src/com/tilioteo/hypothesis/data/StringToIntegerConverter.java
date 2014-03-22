/**
 * 
 */
package com.tilioteo.hypothesis.data;

import java.text.NumberFormat;
import java.util.Locale;

import com.tilioteo.hypothesis.common.Strings;
import com.vaadin.data.util.converter.AbstractStringToNumberConverter;

/**
 * @author kamil
 * 
 */
@SuppressWarnings("serial")
public class StringToIntegerConverter extends
		AbstractStringToNumberConverter<Integer> {

	/**
	 * Returns the format used by
	 * {@link #convertToPresentation(Integer, Class, Locale)} and
	 * {@link #convertToModel(String, Class, Locale)}
	 * 
	 * @param locale
	 *            The locale to use
	 * @return A NumberFormat instance
	 */
	@Override
	protected NumberFormat getFormat(Locale locale) {
		if (locale == null) {
			locale = Locale.getDefault();
		}
		return NumberFormat.getIntegerInstance(locale);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vaadin.data.util.converter.Converter#convertToModel(java.lang.Object,
	 * java.lang.Class, java.util.Locale)
	 */
	@Override
	public Integer convertToModel(String value,
			Class<? extends Integer> targetType, Locale locale)
			throws ConversionException {

		if (Strings.isNullOrEmpty(value)) {
			return null;
		}

		Integer result = null;
		try {
			result = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new ConversionException("Could not convert '" + value
					+ "' to " + getModelType().getName());
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.data.util.converter.Converter#getModelType()
	 */
	@Override
	public Class<Integer> getModelType() {
		return Integer.class;
	}

}
