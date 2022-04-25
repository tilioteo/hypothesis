package org.hypothesis.common.utility;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

public class StringUtility {

	private static final String STR_UNIT_PX = "px";
	private static final String STR_UNIT_PT = "pt";
	private static final String STR_UNIT_EM = "em";
	private static final String STR_UNIT_EX = "ex";
	private static final String STR_UNIT_MM = "mm";
	private static final String STR_UNIT_CM = "cm";
	private static final String STR_UNIT_PERCENT = "%";

	private static final String STR_COMMA = ",";
	private static final String STR_QUOTED_STRING_SPLIT_PATTERN = "['']";

	public static Integer toInteger(String string) {
		if (isNotEmpty(string)) {
			try {
				return Integer.parseInt(string);
			} catch (Exception e) {
			}
		}
		return null;
	}

	public static int toInteger(String string, int defaultValue) {
		return Optional.ofNullable(toInteger(string)).orElse(defaultValue);
	}

	public static Double toDouble(String string) {
		if (isNotEmpty(string)) {
			try {
				return Double.parseDouble(string);
			} catch (Exception e) {
			}
		}
		return null;
	}

	public static double toDouble(String string, double defaultValue) {
		return Optional.ofNullable(toDouble(string)).orElse(defaultValue);
	}

	public static Date toDate(String string, String format) {
		if (isNotEmpty(string) && isNotEmpty(format)) {
			try {
				DateFormat formatter = new SimpleDateFormat(format);
				return formatter.parse(string);
			} catch (Exception e) {
			}
		}
		return null;
	}

	public static Boolean toBoolean(String string) {
		if (isNotEmpty(string)) {
			string = string.trim();
			if ("true".equalsIgnoreCase(string) || "1".equals(string)) {
				return Boolean.TRUE;
			} else if ("false".equalsIgnoreCase(string) || "0".equals(string)) {
				return Boolean.FALSE;
			}
		}
		return null;
	}

	public static boolean toBoolean(String string, boolean defaultValue) {
		return Optional.ofNullable(toBoolean(string)).orElse(defaultValue);
	}

	public static String[] toStringArray(String string, String separator, String surroundPattern) {
		if (string != null) {
			int count = 0;
			String[] parts = null;
			if (isNotEmpty(surroundPattern)) {
				parts = string.split(surroundPattern);
			} else if (isNotEmpty(separator)) {
				parts = string.split(separator);
			}
			if (parts != null) {
				for (int i = 0; i < parts.length; ++i) {
					parts[i] = parts[i].trim();
					if (parts[i].equals(separator) || parts[i].length() == 0)
						parts[i] = null;
					else
						++count;
				}
				String[] array = new String[count];
				int j = 0;
				for (String part : parts) {
					if (part != null)
						array[j++] = part;
				}

				return array;
			}
		}
		return null;
	}

	public static String[] toStringArray(String string, String separator) {
		return toStringArray(string, separator, null);
	}

	public static Integer[] toIntegerArray(String string, String separator) {
		String[] strings = toStringArray(string, separator);
		if (strings != null) {
			Integer[] array = new Integer[strings.length];
			for (int i = 0; i < strings.length; ++i) {
				array[i] = toInteger(strings[i]);
			}
			return array;
		}
		return null;
	}

	public static Double[] toDoubleArray(String string, String separator) {
		String[] strings = toStringArray(string, separator);
		if (strings != null) {
			Double[] array = new Double[strings.length];
			for (int i = 0; i < strings.length; ++i) {
				array[i] = toDouble(strings[i]);
			}
			return array;
		}
		return null;
	}

	public static String getDimension(Map<String, String> map, String key) {
		String value = map.get(key);
		if (value != null //
				&& !(value.endsWith(STR_UNIT_PX) //
						|| value.endsWith(STR_UNIT_PT) //
						|| value.endsWith(STR_UNIT_EM) //
						|| value.endsWith(STR_UNIT_EX) //
						|| value.endsWith(STR_UNIT_MM) //
						|| value.endsWith(STR_UNIT_CM) //
						|| value.endsWith(STR_UNIT_PERCENT))) {
			try {
				Double doubleValue = Double.parseDouble(value);
				value = doubleValue + STR_UNIT_PX;
			} catch (NumberFormatException e) {
				value = null;
			}
		}

		return value;
	}

	public static Boolean getBoolean(Map<String, String> map, String key) {
		String value = map.get(key);
		if (isNotEmpty(value)) {
			return Boolean.parseBoolean(value);
		} else {
			return null;
		}
	}

	public static boolean getBoolean(Map<String, String> map, String key, boolean defaultValue) {
		Boolean value = getBoolean(map, key);
		return value != null ? value : defaultValue;
	}

	public static Integer getInteger(Map<String, String> map, String key) {
		return toInteger(map.get(key));
	}

	public static int getInteger(Map<String, String> map, String key, int defaultValue) {
		Integer value = getInteger(map, key);
		return value != null ? value : defaultValue;
	}

	public static Double getDouble(Map<String, String> map, String key) {
		return toDouble(map.get(key));
	}

	public static double getDouble(Map<String, String> map, String key, double defaultValue) {
		Double value = getDouble(map, key);
		return value != null ? value : defaultValue;
	}

	public static String[] getStringArray(Map<String, String> map, String key) {
		return getStringArray(map, key, false);
	}

	public static String[] getStringArray(Map<String, String> map, String key, boolean emptyArrayAsDefault) {
		String[] array = toStringArray(map.get(key), STR_COMMA, STR_QUOTED_STRING_SPLIT_PATTERN);

		if (array != null) {
			return array;
		} else if (emptyArrayAsDefault)
			return new String[] {};
		else
			return null;
	}
}
