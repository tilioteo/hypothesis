/**
 * 
 */
package com.tilioteo.hypothesis.processing;

import java.util.LinkedHashMap;
import java.util.Map;

import com.tilioteo.hypothesis.interfaces.ExchangeVariable;
import com.tilioteo.hypothesis.interfaces.Variable;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class ExchangeVariableMap extends LinkedHashMap<Integer, ExchangeVariable> {

	public void setVariables(Map<String, Variable<?>> variables) {
		for (ExchangeVariable variable : this.values()) {
			variable.setVariables(variables);
		}
	}

	public boolean add(ExchangeVariable variable) {
		if (variable != null && !this.containsKey(variable.getIndex())) {
			put(variable.getIndex(), variable);
		}
		return false;
    }

}
