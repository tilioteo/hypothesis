/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.data.model;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
public enum Gender implements Serializable {
	MALE("M", "Caption.Gender.Male"), FEMALE("F", "Caption.Gender.Female");

	private static final Map<String, Gender> lookup = new HashMap<>();

	static {
		for (Gender g : EnumSet.allOf(Gender.class))
			lookup.put(g.getCode(), g);
	}

	public static Gender get(String code) {
		return lookup.get(code);
	}

	private final String code;
	private final String messageCode;

	Gender(String code, String messageCode) {
		this.code = code;
		this.messageCode = messageCode;
	}

	public String getCode() {
		return code;
	}

	public String getMessageCode() {
		return messageCode;
	}
}
