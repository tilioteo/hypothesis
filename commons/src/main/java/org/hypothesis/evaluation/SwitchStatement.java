/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hypothesis.interfaces.Evaluable;
import org.hypothesis.interfaces.HasVariables;

import com.tilioteo.expressions.ExpressionFactory;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class SwitchStatement implements Evaluable {

	private final HasVariables variables;
	private final Expression expression;

	private final HashMap<String, List<Evaluable>> caseMap = new HashMap<>();

	public SwitchStatement(HasVariables variables, Expression expression) {
		this.variables = variables;
		this.expression = expression;
	}

	public void addCaseEvaluable(String caseValue, Evaluable evaluable) {
		List<Evaluable> evaluables = caseMap.get(caseValue);
		if (evaluables == null) {
			evaluables = new ArrayList<>();
			caseMap.put(caseValue, evaluables);
		}

		evaluables.add(evaluable);
	}

	public void evaluate() {
		if (expression != null && variables != null) {
			Object result = expression.getValue();
			if (result != null) {
				com.tilioteo.expressions.Expression expression = ExpressionFactory.parseString("a==b");
				expression.setVariableValue("a", result);

				for (String caseValue : caseMap.keySet()) {
					com.tilioteo.expressions.Expression caseExpression = ExpressionFactory.parseString(caseValue);
					expression.setVariableValue("b", caseExpression.getValue());
					Boolean value = expression.getBoolean();

					if (value != null && value) {
						List<Evaluable> evaluables = caseMap.get(caseValue);
						if (evaluables != null) {
							for (Evaluable evaluable : evaluables) {
								evaluable.setVariables(variables.getVariables());
								evaluable.evaluate();
								evaluable.updateVariables(variables.getVariables());
							}
						}
					}
				}
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
		if (expression != null) {
			expression.updateVariables(variables);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("switch (" + expression.toString() + ") {\n");
		for (Object value : caseMap.keySet()) {
			builder.append("\tcase ").append(value.toString()).append(" : {\n");
			List<Evaluable> evaluables = caseMap.get(value);
			for (Evaluable evaluable : evaluables) {
				builder.append("\t\t").append(evaluable.toString()).append(";\n");
			}
			builder.append("\t}\n");
		}
		builder.append("}");

		return builder.toString();
	}

}
