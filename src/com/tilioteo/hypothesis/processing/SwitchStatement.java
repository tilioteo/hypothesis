/**
 * 
 */
package com.tilioteo.hypothesis.processing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	private HashMap<Object, List<Evaluable>> caseMap = new HashMap<Object, List<Evaluable>>();

	public SwitchStatement(HasVariables variables, Expression expression) {
		this.variables = variables;
		this.expression = expression;
	}

	public void addCaseEvaluable(Object caseValue, Evaluable evaluable) {
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
				List<Evaluable> evaluables = caseMap.get(result);
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
