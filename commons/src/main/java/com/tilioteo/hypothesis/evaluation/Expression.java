/**
 * 
 */
package com.tilioteo.hypothesis.evaluation;

import java.util.Map;

import com.tilioteo.expressions.UnaryExpression;
import com.tilioteo.hypothesis.interfaces.Evaluable;
import com.tilioteo.hypothesis.interfaces.Variable;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class Expression implements Evaluable {

	private com.tilioteo.expressions.Expression internalExpression;

	public Expression(com.tilioteo.expressions.Expression expression) {
		this.internalExpression = expression;
	}

	@Override
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
		if (internalExpression != null && internalExpression instanceof UnaryExpression) {
			UnaryExpression expression = (UnaryExpression) internalExpression;
			if (expression.getRightSide() != null
					&& expression.getRightSide() instanceof com.tilioteo.expressions.Variable) {
				return ((com.tilioteo.expressions.Variable) expression.getRightSide()).getName();
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

	@Override
	public void setVariables(Map<String, Variable<?>> variables) {
		if (internalExpression != null && variables != null) {
			for (String key : variables.keySet()) {
				Variable<?> variable = variables.get(key);
				internalExpression.setVariableValue(key, variable.getValue());
			}
		}
	}

	@Override
	public void updateVariables(Map<String, Variable<?>> variables) {
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

	@Override
	public String toString() {
		return internalExpression != null ? internalExpression.toString() : "<null>";
	}

}