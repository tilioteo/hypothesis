/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.common.utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public class ConversionUtility {

	private static final String STR_UNIT_PX = "px";
	private static final String STR_UNIT_PT = "pt";
	private static final String STR_UNIT_EM = "em";
	private static final String STR_UNIT_EX = "ex";
	private static final String STR_UNIT_MM = "mm";
	private static final String STR_UNIT_CM = "cm";
	private static final String STR_UNIT_PERCENT = "%";

	private static final String ERROR_INPUT_STRING_NUM_CHARS = "Input string must contain an even number of characters";
	private static final String STR_HEX_PREFIX = "0x";
	private static final String STR_0 = "0";

	private ConversionUtility() {
	}

	public static String getStringOrDefault(String value, String defaultValue) {
		return value != null ? value : defaultValue;
	}

	public static String getDimension(String string) {
		if (string != null && !(string.endsWith(STR_UNIT_PX) || string.endsWith(STR_UNIT_PT)
				|| string.endsWith(STR_UNIT_EM) || string.endsWith(STR_UNIT_EX) || string.endsWith(STR_UNIT_MM)
				|| string.endsWith(STR_UNIT_CM) || string.endsWith(STR_UNIT_PERCENT))) {
			try {
				Double doubleValue = Double.parseDouble(string);
				string = doubleValue + STR_UNIT_PX;
			} catch (NumberFormatException e) {
				string = null;
			}
		}

		return string;
	}

	public static Boolean getBoolean(String string) {
		if (StringUtils.isNotBlank(string)) {
			string = string.trim();
			if ("true".equalsIgnoreCase(string) || "1".equals(string)) {
				return Boolean.TRUE;
			} else if ("false".equalsIgnoreCase(string) || "0".equals(string)) {
				return Boolean.FALSE;
			}
		}
		return null;
	}

	public static boolean getBooleanOrDefault(String string, boolean defaultValue) {
		Boolean value = getBoolean(string);
		return value != null ? value : defaultValue;
	}

	public static Integer getInteger(String string) {
		if (StringUtils.isNotBlank(string)) {
			try {
				Integer value = Integer.parseInt(string);
				return value;
			} catch (Exception e) {
			}
		}
		return null;
	}

	public static int getIntegerOrDefault(String string, int defaultValue) {
		Integer value = getInteger(string);
		return value != null ? value : defaultValue;
	}

	public static Double getDouble(String string) {
		if (StringUtils.isNotBlank(string)) {
			try {
				Double value = Double.parseDouble(string);
				return value;
			} catch (Exception e) {
			}
		}
		return null;
	}

	public static double getDoubleOrDefault(String string, double defaultValue) {
		Double value = getDouble(string);
		return value != null ? value : defaultValue;
	}

	public static Date getDate(String string, String format) {
		if (StringUtils.isNotBlank(string) && StringUtils.isNotBlank(format)) {
			try {
				DateFormat formatter = new SimpleDateFormat(format);
				Date value = formatter.parse(string);
				return value;
			} catch (Exception e) {
			}
		}
		return null;
	}

	public static Date getDateOrDefault(String string, String format, Date defaultValue) {
		Date value = getDate(string, format);
		return value != null ? value : defaultValue;
	}

	public static String toHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		int i;

		for (i = 0; i < bytes.length; i++) {
			if ((bytes[i] & 0xff) < 0x10)
				sb.append(STR_0);

			sb.append(Long.toString(bytes[i] & 0xff, 16));
		}

		return sb.toString();
	}

	public static byte[] fromHexString(final String str) {
		if ((str.length() % 2) != 0)
			throw new IllegalArgumentException(ERROR_INPUT_STRING_NUM_CHARS);

		byte[] result = new byte[str.length() / 2];
		for (int i = 0; i < str.length() / 2; i++) {
			result[i] = (Integer.decode(STR_HEX_PREFIX + str.substring(i * 2, (i + 1) * 2))).byteValue();
		}
		return result;
	}

}
