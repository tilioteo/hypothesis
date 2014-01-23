/**
 * 
 */
package com.tilioteo.hypothesis.model;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class Nick {

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

	public boolean pass(Object value) {
		if (value != null && expression != null) {
			VariableMap variables = new VariableMap();
			// TODO upravit pro ruzne typy
			Variable<Integer> slideResult = new Variable<Integer>("result");
			slideResult.setRawValue(value);
			variables.put(slideResult);
			expression.setVariables(variables);
			return expression.getBoolean();
		}
		return false;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}
}
