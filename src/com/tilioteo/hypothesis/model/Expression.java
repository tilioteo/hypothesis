/**
 * 
 */
package com.tilioteo.hypothesis.model;

import com.tilioteo.hypothesis.evaluable.UnaryExpression;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class Expression implements Evaluable {

	private com.tilioteo.hypothesis.evaluable.Expression internalExpression;

	public Expression(com.tilioteo.hypothesis.evaluable.Expression expression) {
		this.internalExpression = expression;
	}

	public void evaluate() {
		getValue();
	}

	public Boolean getBoolean() {
		if (internalExpression != null) {
			return internalExpression.getBoolean();
		}
		return null;
	}

	public String getSimpleVariableName() {
		if (internalExpression != null
				&& internalExpression instanceof UnaryExpression) {
			UnaryExpression expression = (UnaryExpression) internalExpression;
			if (expression.getRightSide() != null
					&& expression.getRightSide() instanceof com.tilioteo.hypothesis.evaluable.Variable) {
				return ((com.tilioteo.hypothesis.evaluable.Variable) expression
						.getRightSide()).getName();
			}
		}
		return null;
	}

	public Object getValue() {
		if (internalExpression != null) {
			return internalExpression.getValue();
		}
		return null;
	}

	public void setVariables(VariableMap variables) {
		if (internalExpression != null && variables != null) {
			for (String key : variables.keySet()) {
				Variable<?> variable = variables.get(key);
				internalExpression.setVariableValue(key, variable.getValue());
			}
		}
	}

	public void updateVariables(VariableMap variables) {
		if (internalExpression != null && variables != null) {
			for (String key : variables.keySet()) {
				Variable<?> variable = variables.get(key);
				Object value = internalExpression.getVariableValue(key);
				if (value != null) {
					variable.setRawValue(value);
				}
			}
		}
	}

}
