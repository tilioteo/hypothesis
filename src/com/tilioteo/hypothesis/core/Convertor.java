/**
 * 
 */
package com.tilioteo.hypothesis.core;

import com.tilioteo.hypothesis.common.Strings;

/**
 * @author kamil
 *
 */
public class Convertor implements CoreObject {
	
	public Integer stringToInt(String string) {
		return Strings.toInteger(string);
	}
	
	public Double stringToFloat(String string) {
		return Strings.toDouble(string);
	}
	
	public Boolean stringToBool(String string) {
		return Strings.toBoolean(string);
	}
}
