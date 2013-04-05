/**
 * 
 */
package org.hypothesis.common;

import java.util.HashMap;

import org.hypothesis.common.constants.StringConstants;

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
		return Boolean.parseBoolean(get(key));
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		Boolean value = getBoolean(key);
		return value != null ? value : defaultValue;
	}

	public Integer getInteger(String key) {
		try {
			return Integer.parseInt(get(key));
		} catch (NumberFormatException e) {}
		return null;
	}

	public int getInteger(String key, int defaultValue) {
		Integer value = getInteger(key);
		return value != null ? value : defaultValue;
	}

	public Double getDouble(String key) {
		try {
			return Double.parseDouble(get(key));
		} catch (NumberFormatException e) {}
		return null;
	}

	public double getDouble(String key, double defaultValue) {
		Double value = getDouble(key);
		return value != null ? value : defaultValue;
	}
	
	public String[] getStringArray(String key) {
		return getStringArray(key, false);
	}
	
	public String[] getStringArray(String key, boolean emptyArrayAsDefault) {
		String value = get(key);
		if (value != null) {
			int count = 0;
			String[] parts = value.split(StringConstants.STR_QUOTED_STRING_SPLIT_PATTERN);
			for (int i = 0; i < parts.length; ++i) {
				parts[i] = parts[i].trim();
				if (parts[i].equals(StringConstants.STR_COMMA) || parts[i].length() == 0)
					parts[i] = null;
				else
					++count;
			}
			String[] array = new String[count];
			int j = 0;
			for (int i = 0; i < parts.length; ++i) {
				if (parts[i] != null)
					array[j++] = parts[i];
			}

			return array;
		}
		
		if (emptyArrayAsDefault)
			return new String[] {};
		else
			return null;
	}

}
