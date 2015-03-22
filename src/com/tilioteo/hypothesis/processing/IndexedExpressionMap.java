/**
 * 
 */
package com.tilioteo.hypothesis.processing;

import java.util.LinkedHashMap;

import com.tilioteo.hypothesis.core.IndexedExpression;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class IndexedExpressionMap extends LinkedHashMap<Integer, IndexedExpression> {

	public void setVariables(VariableMap variables) {
		for (IndexedExpression outputValue : this.values()) {
			outputValue.setVariables(variables);
		}
	}

	public boolean add(IndexedExpression outputValue) {
		if (outputValue != null && !this.containsKey(outputValue.getIndex())) {
			put(outputValue.getIndex(), outputValue);
		}
		return false;
    }

}
