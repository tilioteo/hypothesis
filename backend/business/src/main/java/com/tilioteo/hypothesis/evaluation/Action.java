/**
 * 
 */
package com.tilioteo.hypothesis.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tilioteo.hypothesis.interfaces.Evaluable;
import com.tilioteo.hypothesis.interfaces.ExchangeVariable;
import com.tilioteo.hypothesis.interfaces.HasVariables;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class Action extends AbstractBaseAction {

	private HashMap<Integer, ExchangeVariable> outputValues = new HashMap<>();
	private List<Evaluable> evaluables = new ArrayList<>();

	public Action(HasVariables variables, String id) {
		super(variables, id);
	}

	public void add(Evaluable evaluable) {
		evaluables.add(evaluable);
	}

	@Override
	public void execute() {
		super.execute();

		for (Evaluable evaluable : evaluables) {
			Map<String, com.tilioteo.hypothesis.interfaces.Variable<?>> variables = null;
			if (getVariables() != null)
				variables = getVariables();

			if (variables != null)
				evaluable.setVariables(variables);
			evaluable.evaluate();
			if (variables != null)
				evaluable.updateVariables(variables);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(getId() + "() {\n");
		for (Evaluable evaluable : evaluables) {
			builder.append("\t" + evaluable.toString() + ";\n");
		}
		builder.append("}");

		return builder.toString();
	}

	public Map<Integer, ExchangeVariable> getOutputs() {
		Map<String, com.tilioteo.hypothesis.interfaces.Variable<?>> variables = getVariables();
		if (variables != null) {
			for (ExchangeVariable outputValue : outputValues.values()) {
				outputValue.setVariables(variables);
			}
		}

		return outputValues;
	}
}
