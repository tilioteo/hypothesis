/**
 * 
 */
package org.hypothesis.application.collector.evaluable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.hypothesis.application.collector.slide.HasVariables;
import org.hypothesis.application.collector.slide.VariableMap;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
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
