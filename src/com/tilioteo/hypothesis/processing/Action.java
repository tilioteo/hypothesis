/**
 * 
 */
package com.tilioteo.hypothesis.processing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tilioteo.hypothesis.interfaces.Evaluable;
import com.tilioteo.hypothesis.interfaces.ExchangeVariable;
import com.tilioteo.hypothesis.interfaces.HasVariables;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class Action extends AbstractBaseAction {
	
	private ExchangeVariableMap outputValues = new ExchangeVariableMap();
	private List<Evaluable> evaluables = new ArrayList<Evaluable>();

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
		if (getVariables() != null) {
			outputValues.setVariables(getVariables());
		}
		return outputValues;
	}
}
