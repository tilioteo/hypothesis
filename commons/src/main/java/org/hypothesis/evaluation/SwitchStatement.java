/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

	private HasVariables variables;
	private Expression expression;

	private HashMap<String, List<Evaluable>> caseMap = new HashMap<>();

	public SwitchStatement(HasVariables variables, Expression expression) {
		this.variables = variables;
		this.expression = expression;
	}

	public void addCaseEvaluable(String caseValue, Evaluable evaluable) {
		List<Evaluable> evaluables = caseMap.get(caseValue);
		if (evaluables == null) {
			evaluables = new ArrayList<Evaluable>();
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

				for (Entry<String, List<Evaluable>> entry : caseMap.entrySet()) {
					com.tilioteo.expressions.Expression caseExpression = ExpressionFactory.parseString(entry.getKey());
					expression.setVariableValue("b", caseExpression.getValue());
					Boolean value = expression.getBoolean();

					if (value != null && value.booleanValue()) {
						List<Evaluable> evaluables = caseMap.get(entry.getValue());
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
		for (Entry<String, List<Evaluable>> entry : caseMap.entrySet()) {
			builder.append("\tcase " + entry.getKey() + " : {\n");
			List<Evaluable> evaluables = entry.getValue();
			for (Evaluable evaluable : evaluables) {
				builder.append("\t\t" + evaluable.toString() + ";\n");
			}
			builder.append("\t}\n");
		}
		builder.append("}");

		return builder.toString();
	}

}
