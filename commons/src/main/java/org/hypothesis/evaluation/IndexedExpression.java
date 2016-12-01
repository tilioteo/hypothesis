/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.evaluation;

import org.hypothesis.interfaces.ExchangeVariable;

import java.util.Map;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class IndexedExpression implements ExchangeVariable {

	private final int index;
	private final Expression expression;

	/**
	 * Construct
	 * 
	 * @param index
	 * @param expression
	 */
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
	public void setVariables(Map<String, org.hypothesis.interfaces.Variable<?>> variables) {
		if (expression != null) {
			expression.setVariables(variables);
		}
	}

}
