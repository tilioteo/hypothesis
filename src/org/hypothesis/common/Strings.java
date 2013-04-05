/**
 * 
 */
package org.hypothesis.common;

import org.hypothesis.common.constants.StringConstants;

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
}
