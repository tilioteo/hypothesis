/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.evaluation;

import java.util.Map;

import org.hypothesis.interfaces.Evaluable;

import com.tilioteo.expressions.UnaryExpression;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class Expression implements Evaluable {

	private final com.tilioteo.expressions.Expression internalExpression;

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
	public void setVariables(Map<String, org.hypothesis.interfaces.Variable<?>> variables) {
		if (internalExpression != null && variables != null) {
			for (String key : variables.keySet()) {
				org.hypothesis.interfaces.Variable<?> variable = variables.get(key);
				internalExpression.setVariableValue(key, variable.getValue());
			}
		}
	}

	@Override
	public void updateVariables(Map<String, org.hypothesis.interfaces.Variable<?>> variables) {
		if (internalExpression != null && variables != null) {
			for (String key : variables.keySet()) {
				org.hypothesis.interfaces.Variable<?> variable = variables.get(key);
				if (internalExpression.hasVariable(key)) {
					Object value = internalExpression.getVariableValue(key);
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
