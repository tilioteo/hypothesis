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
public enum Status implements Serializable {
	CREATED(1), STARTED(2), FINISHED(3), BROKEN_BY_CLIENT(4), BROKEN_BY_ERROR(5);

	private static final Map<Integer, Status> lookup = new HashMap<>();

	static {
		EnumSet.allOf(Status.class).forEach(e -> lookup.put(e.getCode(), e));
	}

	public static Status get(int code) {
		return lookup.get(code);
	}

	private final Integer code;

	Status(Integer code) {
		this.code = code;
	}

	public Integer getCode() {
		return code;
	}
}
