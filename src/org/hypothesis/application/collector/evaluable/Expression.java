/**
 * 
 */
package org.hypothesis.application.collector.evaluable;

import org.hypothesis.application.collector.slide.Variable;
import org.hypothesis.application.collector.slide.VariableMap;
import org.hypothesis.common.expression.UnaryExpression;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class Expression implements Evaluable {

	private org.hypothesis.common.expression.Expression internalExpression;

	public Expression(org.hypothesis.common.expression.Expression expression) {
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
					&& expression.getRightSide() instanceof org.hypothesis.common.expression.Variable) {
				return ((org.hypothesis.common.expression.Variable) expression
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
