/**
 * 
 */
package com.tilioteo.hypothesis.evaluable;

import com.tilioteo.hypothesis.common.Strings;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
public class Variable extends Primitive {

	private int refCount;
	private String name;
	
	protected int decRefCount() {
		return (refCount > 1 ? --refCount : 0);
	}
	
	protected void incRefCount() {
		++refCount;
	}

	public Variable(String name) {
		assert (!Strings.isNullOrEmpty(name));
		
		this.refCount = 0;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

}
