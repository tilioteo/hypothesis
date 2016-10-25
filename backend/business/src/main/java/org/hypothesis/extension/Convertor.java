/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.extension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hypothesis.interfaces.Extension;

import com.tilioteo.common.Strings;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class Convertor implements Extension {

	/**
	 * Safe string to integer conversion
	 * 
	 * @param string
	 * @return parsed integer value or null
	 */
	public Integer stringToInt(String string) {
		return Strings.toInteger(string);
	}

	/**
	 * Safe string to double conversion
	 * 
	 * @param string
	 * @return parsed double value or null
	 */
	public Double stringToFloat(String string) {
		return Strings.toDouble(string);
	}

	/**
	 * Safe string to boolean conversion
	 * 
	 * @param string
	 * @return parsed boolean value or null
	 */
	public Boolean stringToBool(String string) {
		return Strings.toBoolean(string);
	}

	/**
	 * Safe integer to string conversion
	 * 
	 * @param integer
	 * @return string representation of value or null
	 */
	public String intToString(Integer integer) {
		return integer != null ? integer.toString() : "";
	}

	/**
	 * Wraps array to list of object. Items are referenced
	 * 
	 * @param arr
	 * @return list
	 */
	public List<Object> objectToArray(Object arr) {
		ArrayList<Object> array = new ArrayList<>();

		if (arr != null && arr.getClass().isArray()) {
			Arrays.stream((Object[]) arr).forEach(array::add);
		}

		return array;
	}
}
