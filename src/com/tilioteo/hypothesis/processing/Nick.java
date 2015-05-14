/**
 * 
 */
package com.tilioteo.hypothesis.processing;

import java.io.Serializable;
import java.util.Map;

import com.tilioteo.hypothesis.interfaces.ExchangeVariable;

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
			VariableMap variables = new VariableMap();
			
			for (Integer index : inputs.keySet()) {
				ExchangeVariable exchangeVariable = inputs.get(index);
				Variable<?> variable = Variable.createVariable("output"+index, exchangeVariable.getValue());
				variables.put(variable);
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
