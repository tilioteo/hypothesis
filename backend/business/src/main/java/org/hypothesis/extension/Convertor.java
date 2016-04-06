/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.extension;

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

}
