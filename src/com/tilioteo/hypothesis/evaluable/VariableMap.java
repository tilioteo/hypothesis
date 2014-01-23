/**
 * 
 */
package com.tilioteo.hypothesis.evaluable;

import java.util.HashMap;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
@SuppressWarnings("serial")
public final class VariableMap extends HashMap<String, Variable> {

	public Object getValue(String name) {
		Variable variable = get(name);
		if (variable != null) {
			return variable.getValue();
		}
		
		return null;
	}
	
	public void setValue(String name, Object value) {
		Variable variable = get(name);
		if (variable != null) {
			variable.setValue(value);
		}
	}
	
	public void add(Variable variable) {
		if (variable != null) {
			variable.incRefCount();
			if (!this.containsValue(variable))
				super.put(variable.getName(), variable);
		}
	}
	
	public void remove(String name) {
		super.remove(get(name));
	}
}
