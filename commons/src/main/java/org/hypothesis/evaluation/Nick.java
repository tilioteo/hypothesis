/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.evaluation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.hypothesis.interfaces.ExchangeVariable;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class Nick implements Serializable {

	private final Long slideId;
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
			HashMap<String, org.hypothesis.interfaces.Variable<?>> variables = new HashMap<>();

			for (Entry<Integer, ExchangeVariable> entry : inputs.entrySet()) {
				ExchangeVariable exchangeVariable = entry.getValue();
				org.hypothesis.interfaces.Variable<?> variable = Variable.createVariable("output" + entry.getKey(),
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
