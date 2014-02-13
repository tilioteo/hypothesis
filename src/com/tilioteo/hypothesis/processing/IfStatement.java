/**
 * 
 */
package com.tilioteo.hypothesis.processing;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class IfStatement implements Evaluable {

	private HasVariables variables;
	private Expression expression;
	private List<Evaluable> trueBlock = new ArrayList<Evaluable>();
	private List<Evaluable> falseBlock = new ArrayList<Evaluable>();

	public IfStatement(HasVariables variables, Expression expression) {
		this.variables = variables;
		this.expression = expression;
	}

	public void addFalseEvaluable(Evaluable evaluable) {
		falseBlock.add(evaluable);
	}

	public void addTrueEvaluable(Evaluable evaluable) {
		trueBlock.add(evaluable);
	}

	public void evaluate() {
		if (expression != null && variables != null) {
			Boolean result = expression.getBoolean();
			if (result != null) {
				List<Evaluable> evaluables = result ? trueBlock : falseBlock;

				for (Evaluable evaluable : evaluables) {
					evaluable.setVariables(variables.getVariables());
					evaluable.evaluate();
					evaluable.updateVariables(variables.getVariables());
				}
			}
		}
	}

	public void setVariables(VariableMap variables) {
		if (expression != null) {
			expression.setVariables(variables);
		}
	}

	public void updateVariables(VariableMap variables) {
		if (expression != null) {
			expression.updateVariables(variables);
		}
	}

}
