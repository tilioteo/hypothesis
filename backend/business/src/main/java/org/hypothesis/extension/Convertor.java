/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.extension;

import static org.hypothesis.common.utility.StringUtility.toBoolean;
import static org.hypothesis.common.utility.StringUtility.toDouble;
import static org.hypothesis.common.utility.StringUtility.toInteger;

import java.lang.reflect.Array;
import java.util.ArrayList;

import org.hypothesis.interfaces.Extension;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class Convertor implements Extension {

	public Integer stringToInt(String string) {
		return toInteger(string);
	}

	public Double stringToFloat(String string) {
		return toDouble(string);
	}

	public Boolean stringToBool(String string) {
		return toBoolean(string);
	}

	public String intToString(Integer integer) {
		return integer != null ? integer.toString() : "";
	}

	public ArrayList<Object> objectToArray(Object arr) {
		if (arr != null && arr.getClass().isArray()) {
			ArrayList<Object> array = new ArrayList<>();
			int length = Array.getLength(arr);
			for (int i = 0; i < length; ++i) {
				array.add(Array.get(arr, i));
			}

			return array;
		}

		return null;
	}
}
