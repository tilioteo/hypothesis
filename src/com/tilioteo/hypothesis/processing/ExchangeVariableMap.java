/**
 * 
 */
package com.tilioteo.hypothesis.processing;

import java.util.LinkedHashMap;

import com.tilioteo.hypothesis.interfaces.ExchangeVariable;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class ExchangeVariableMap extends LinkedHashMap<Integer, ExchangeVariable> {

	public void setVariables(VariableMap variables) {
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
