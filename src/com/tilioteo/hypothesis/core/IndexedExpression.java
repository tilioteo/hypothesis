/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.util.Map;

import com.tilioteo.hypothesis.interfaces.ExchangeVariable;
import com.tilioteo.hypothesis.interfaces.Variable;
import com.tilioteo.hypothesis.processing.Expression;

/**
 * @author kamil
 *
 */
public class IndexedExpression implements ExchangeVariable {
	
	private int index;
	private Expression expression;
	
	public IndexedExpression(int index, Expression expression) {
		this.index = index;
		this.expression = expression;
	}
	
	@Override
	public int getIndex() {
		return index;
	}
	
	public Expression getExpression() {
		return expression;
	}
	
	@Override
	public Object getValue() {
		if (expression != null) {
			return expression.getValue();
		}
		return null;
	}
	
	@Override
	public void setVariables(Map<String, Variable<?>> variables) {
		if (expression != null) {
			expression.setVariables(variables);
		}
	}

}
