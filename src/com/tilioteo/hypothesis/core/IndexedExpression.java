/**
 * 
 */
package com.tilioteo.hypothesis.core;

import com.tilioteo.hypothesis.processing.Expression;
import com.tilioteo.hypothesis.processing.VariableMap;

/**
 * @author kamil
 *
 */
public class IndexedExpression {
	
	private int index;
	private Expression expression;
	
	public IndexedExpression(int index, Expression expression) {
		this.index = index;
		this.expression = expression;
	}
	
	public int getIndex() {
		return index;
	}
	
	public Expression getExpression() {
		return expression;
	}
	
	public Object getValue() {
		if (expression != null) {
			return expression.getValue();
		}
		return null;
	}
	
	public void setVariables(VariableMap variables) {
		if (expression != null) {
			expression.setVariables(variables);
		}
	}

}
