/**
 * 
 */
package com.tilioteo.hypothesis.evaluation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.tilioteo.hypothesis.interfaces.ExchangeVariable;
import com.tilioteo.hypothesis.interfaces.Variable;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class Nick implements Serializable {

	private Long slideId;
	private Expression expression;

	public Nick(Long slideId) {
		this.slideId = slideId;
	}

	public Expression getExpression() {
		return expression;
	}

	public Long getSlideId() {
		return slideId;
	}

	public boolean pass(Map<Integer, ExchangeVariable> inputs) {
		if (inputs != null && expression != null) {
			HashMap<String, Variable<?>> variables = new HashMap<>();

			for (Integer index : inputs.keySet()) {
				ExchangeVariable exchangeVariable = inputs.get(index);
				Variable<?> variable = com.tilioteo.hypothesis.evaluation.Variable.createVariable("output" + index,
						exchangeVariable.getValue());
				variables.put(variable.getName(), variable);
			}

			expression.setVariables(variables);
			return expression.getBoolean();
		}

		return false;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}
}
