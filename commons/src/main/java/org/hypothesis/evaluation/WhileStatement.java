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
import org.hypothesis.interfaces.Variable;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class WhileStatement implements Evaluable {

	private HasVariables variables;
	private Expression expression;
	private List<Evaluable> evaluables = new ArrayList<>();

	public WhileStatement(HasVariables variables, Expression expression) {
		this.variables = variables;
		this.expression = expression;
	}

	public void addEvaluable(Evaluable evaluable) {
		evaluables.add(evaluable);
	}

	@Override
	public void evaluate() {
		if (expression != null && variables != null) {
			Boolean result = expression.getBoolean();
			expression.updateVariables(variables.getVariables());

			int watchdog = 0;

			while (Boolean.TRUE.equals(result)) {
				for (Evaluable evaluable : evaluables) {
					evaluable.setVariables(variables.getVariables());
					evaluable.evaluate();
					evaluable.updateVariables(variables.getVariables());
				}

				if (++watchdog >= 1000) { // security issue to prevent infinite
											// cycle
					break;
				}

				result = expression.getBoolean();
				// expression.updateVariables(variables.getVariables());
			}
		}
	}

	@Override
	public void setVariables(Map<String, Variable<?>> variables) {
		if (expression != null) {
			expression.setVariables(variables);
		}
	}

	@Override
	public void updateVariables(Map<String, Variable<?>> variables) {
		// NOTE While statement cannot update variables after block evaluation
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("while (" + expression.toString() + ") {\n");
		for (Evaluable evaluable : evaluables) {
			builder.append("\t" + evaluable.toString() + ";\n");
		}

		builder.append("}");

		return builder.toString();
	}

}
