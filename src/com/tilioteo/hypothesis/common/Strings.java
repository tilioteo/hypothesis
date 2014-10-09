/**
 * 
 */
package com.tilioteo.hypothesis.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
public class Strings {
	
	public static boolean isNullOrEmpty(String string) {
		return string == null || string.isEmpty();
	}
	
	public static String toHexString(byte[] bytes) {
		StringBuffer strbuf = new StringBuffer(bytes.length * 2);
		int i;

		for (i = 0; i < bytes.length; i++) {
			if ((bytes[i] & 0xff) < 0x10)
				strbuf.append(StringConstants.STR_0);

			strbuf.append(Long.toString(bytes[i] & 0xff, 16));
		}

		return strbuf.toString();
	}

	public static byte[] fromHexString(final String str) {
		if ((str.length() % 2) != 0)
			throw new IllegalArgumentException(StringConstants.ERROR_INPUT_STRING_NUM_CHARS);

		byte[] result = new byte[str.length() / 2];
		for (int i = 0; i < str.length() / 2; i++) {
			result[i] = (Integer.decode(StringConstants.STR_HEX_PREFIX
					+ str.substring(i * 2, (i + 1) * 2))).byteValue();
		}
		return result;
	}
	
	public static Integer toInteger(String string) {
		if (!Strings.isNullOrEmpty(string)) {
			try {
				Integer value = Integer.parseInt(string);
				return value;
			} catch (Exception e) {
			}
		}
		return null;
	}

	public static Double toDouble(String string) {
		if (!Strings.isNullOrEmpty(string)) {
			try {
				Double value = Double.parseDouble(string);
				return value;
			} catch (Exception e) {
			}
		}
		return null;
	}

	public static Date toDate(String string, String format) {
		if (!Strings.isNullOrEmpty(string) && !Strings.isNullOrEmpty(format)) {
			try {
				DateFormat formatter = new SimpleDateFormat(format);
				Date value = formatter.parse(string);
				return value;
			} catch (Exception e) {
			}
		}
		return null;
	}

	public static String[] toStringArray(String string, String separator, String surroundPattern) {
		if (string != null) {
			int count = 0;
			String[] parts = null;
			if (!isNullOrEmpty(surroundPattern)) {
				parts = string.split(surroundPattern);
			} else if (!isNullOrEmpty(separator)) {
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
				for (int i = 0; i < parts.length; ++i) {
					if (parts[i] != null)
						array[j++] = parts[i];
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
}
