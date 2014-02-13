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
public class Action extends AbstractBaseAction {
	private List<Evaluable> evaluables = new ArrayList<Evaluable>();

	public Action(HasVariables variables, String id) {
		super(variables, id);
	}

	public void add(Evaluable evaluable) {
		evaluables.add(evaluable);
	}

	@Override
	public void execute() {
		for (Evaluable evaluable : evaluables) {
			VariableMap variables = null;
			if (getVariables() != null)
				variables = getVariables().getVariables();

			if (variables != null)
				evaluable.setVariables(variables);
			evaluable.evaluate();
			if (variables != null)
				evaluable.updateVariables(variables);
		}
	}
}
