/**
 * 
 */
package com.tilioteo.hypothesis.common;

import java.util.HashMap;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
public class StringMap extends HashMap<String, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8700318881002231798L;
	
	public String get(String key, String defaultValue) {
		String value = get(key);
		return value != null ? value : defaultValue;
	}
	
	public String getDimension(String key) {
		String value = get(key);
		if (value != null &&
				!(value.endsWith(StringConstants.STR_UNIT_PX) ||
				value.endsWith(StringConstants.STR_UNIT_PT) ||
				value.endsWith(StringConstants.STR_UNIT_EM) ||
				value.endsWith(StringConstants.STR_UNIT_EX) ||
				value.endsWith(StringConstants.STR_UNIT_MM) ||
				value.endsWith(StringConstants.STR_UNIT_CM) ||
				value.endsWith(StringConstants.STR_UNIT_PERCENT))) {
			try {
				Double doubleValue = Double.parseDouble(value);
				value = doubleValue + StringConstants.STR_UNIT_PX;
			} catch (NumberFormatException e) {
				value = null;
			}
		}
		
		return value;
	}

	public Boolean getBoolean(String key) {
		String value = get(key);
		if (!Strings.isNullOrEmpty(value)) {
			return Boolean.parseBoolean(value);
		} else {
			return null;
		}
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		Boolean value = getBoolean(key);
		return value != null ? value : defaultValue;
	}

	public Integer getInteger(String key) {
		return Strings.toInteger(get(key));
	}

	public int getInteger(String key, int defaultValue) {
		Integer value = getInteger(key);
		return value != null ? value : defaultValue;
	}

	public Double getDouble(String key) {
		return Strings.toDouble(get(key));
	}

	public double getDouble(String key, double defaultValue) {
		Double value = getDouble(key);
		return value != null ? value : defaultValue;
	}
	
	public String[] getStringArray(String key) {
		return getStringArray(key, false);
	}
	
	public String[] getStringArray(String key, boolean emptyArrayAsDefault) {
		String[] array = Strings.toStringArray(get(key), StringConstants.STR_COMMA, StringConstants.STR_QUOTED_STRING_SPLIT_PATTERN);
		
		if (array != null) {
			return array;
		} else if (emptyArrayAsDefault)
			return new String[] {};
		else
			return null;
	}

}
