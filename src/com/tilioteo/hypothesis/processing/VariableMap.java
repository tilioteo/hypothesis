/**
 * 
 */
package com.tilioteo.hypothesis.processing;

import com.tilioteo.hypothesis.interfaces.Variable;

import java.util.HashMap;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class VariableMap extends HashMap<String, Variable<?>> {

	public Variable<?> put(Variable<?> variable) {
		if (variable != null)
			return put(variable.getName(), variable);
		else
			return null;
	}

}
