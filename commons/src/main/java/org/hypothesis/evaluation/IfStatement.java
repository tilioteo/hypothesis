/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hypothesis.interfaces.Evaluable;
import org.hypothesis.interfaces.HasVariables;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class IfStatement implements Evaluable {

	private final HasVariables variables;
	private final Expression expression;
	private final List<Evaluable> trueBlock = new ArrayList<>();
	private final List<Evaluable> falseBlock = new ArrayList<>();

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

	@Override
	public void evaluate() {
		if (expression != null && variables != null) {
			Boolean result = expression.getBoolean();
			expression.updateVariables(variables.getVariables());

			if (result != null) {
				(result ? trueBlock : falseBlock).forEach(e -> {
					e.setVariables(variables.getVariables());
					e.evaluate();
					e.updateVariables(variables.getVariables());
				});
			}
		}
	}

	@Override
	public void setVariables(Map<String, org.hypothesis.interfaces.Variable<?>> variables) {
		if (expression != null) {
			expression.setVariables(variables);
		}
	}

	@Override
	public void updateVariables(Map<String, org.hypothesis.interfaces.Variable<?>> variables) {
		// NOTE If statement cannot update variables after block evaluation
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("if (" + expression.toString() + ") {\n");
		trueBlock.forEach(e -> builder.append("\t" + e.toString() + ";\n"));
		builder.append("}");

		if (!falseBlock.isEmpty()) {
			builder.append(" else {\n");
			falseBlock.forEach(e -> builder.append("\t" + e.toString() + ";\n"));
			builder.append("}");
		}

		return builder.toString();
	}

}
