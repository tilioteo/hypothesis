/**
 * 
 */
package com.tilioteo.hypothesis.entity;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kamil
 *
 */
public enum Status {
	CREATED(1), STARTED(2), FINISHED(3), BROKEN_BY_CLIENT(4), BROKEN_BY_ERROR(5);

	private static final Map<Integer, Status> lookup = new HashMap<Integer, Status>();

	static {
		for (Status s : EnumSet.allOf(Status.class))
			lookup.put(s.getCode(), s);
	}

	public static Status get(int code) {
		return lookup.get(code);
	}

	private Integer code;

	private Status(Integer code) {
		this.code = code;
	}

	public Integer getCode() {
		return code;
	}
}
