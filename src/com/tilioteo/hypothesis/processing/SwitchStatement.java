/**
 * 
 */
package com.tilioteo.hypothesis.processing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tilioteo.hypothesis.evaluable.ExpressionFactory;
import com.tilioteo.hypothesis.interfaces.Evaluable;
import com.tilioteo.hypothesis.interfaces.HasVariables;
import com.tilioteo.hypothesis.interfaces.Variable;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class SwitchStatement implements Evaluable {

	private HasVariables variables;
	private Expression expression;

	private HashMap<String, List<Evaluable>> caseMap = new HashMap<String, List<Evaluable>>();

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
				com.tilioteo.hypothesis.evaluable.Expression expression = ExpressionFactory.parseString("a==b");
				expression.setVariableValue("a", result);
				
				for (String caseValue : caseMap.keySet()) {
					com.tilioteo.hypothesis.evaluable.Expression caseExpression = ExpressionFactory.parseString(caseValue);
					expression.setVariableValue("b", caseExpression.getValue());
					Boolean value = expression.getBoolean();
					
					if (value != null && value.booleanValue()) {
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
	public void setVariables(Map<String, Variable<?>> variables) {
		if (expression != null) {
			expression.setVariables(variables);
		}
	}

	@Override
	public void updateVariables(Map<String, Variable<?>> variables) {
		if (expression != null) {
			expression.updateVariables(variables);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("switch (" + expression.toString() + ") {\n");
		for (Object value : caseMap.keySet()) {
			builder.append("\tcase " + value.toString() + " : {\n");
			List<Evaluable> evaluables = caseMap.get(value);
			for (Evaluable evaluable : evaluables) {
				builder.append("\t\t" + evaluable.toString() + ";\n");
			}
			builder.append("\t}\n");
		}
		builder.append("}");
		return builder.toString();
	}

}
