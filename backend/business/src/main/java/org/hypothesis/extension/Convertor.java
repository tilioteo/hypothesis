/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.extension;

import java.lang.reflect.Array;
import java.util.ArrayList;

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

	public Integer stringToInt(String string) {
		return Strings.toInteger(string);
	}

	public Double stringToFloat(String string) {
		return Strings.toDouble(string);
	}

	public Boolean stringToBool(String string) {
		return Strings.toBoolean(string);
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
