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

	@Override
	public void evaluate() {
		if (expression != null && variables != null) {
			Object result = expression.getValue();
			if (result != null) {
				com.tilioteo.expressions.Expression expression = ExpressionFactory.parseString("a==b");
				expression.setVariableValue("a", result);

				caseMap.entrySet().stream().filter(f -> {
					expression.setVariableValue("b", ExpressionFactory.parseString(f.getKey()).getValue());
					Boolean value = expression.getBoolean();
					return value != null && value.booleanValue() && f.getValue() != null;
				}).forEach(e -> e.getValue().forEach(i -> {
					i.setVariables(variables.getVariables());
					i.evaluate();
					i.updateVariables(variables.getVariables());
				}));
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
		final StringBuilder builder = new StringBuilder("switch (" + expression.toString() + ") {\n");
		caseMap.entrySet().forEach(e -> {
			builder.append("\tcase " + e.getKey() + " : {\n");
			e.getValue().forEach(i -> builder.append("\t\t" + i.toString() + ";\n"));
			builder.append("\t}\n");
		});
		builder.append("}");

		return builder.toString();
	}

}
