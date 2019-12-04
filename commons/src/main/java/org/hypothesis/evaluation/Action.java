/**
 * 
 */
package org.hypothesis.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hypothesis.interfaces.Evaluable;
import org.hypothesis.interfaces.ExchangeVariable;
import org.hypothesis.interfaces.HasVariables;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class Action extends AbstractBaseAction {

	private final HashMap<Integer, ExchangeVariable> outputValues = new HashMap<>();
	private final HashMap<Integer, ExchangeVariable> scores = new HashMap<>();
	private final List<Evaluable> evaluables = new ArrayList<>();

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
			Map<String, org.hypothesis.interfaces.Variable<?>> variables = null;
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
			builder.append("\t").append(evaluable.toString()).append(";\n");
		}
		builder.append("}");

		return builder.toString();
	}

	@Override
	public Map<Integer, ExchangeVariable> getOutputs() {
		Map<String, org.hypothesis.interfaces.Variable<?>> variables = getVariables();
		if (variables != null) {
			for (ExchangeVariable outputValue : outputValues.values()) {
				outputValue.setVariables(variables);
			}
		}

		return outputValues;
	}

	@Override
	public Map<Integer, ExchangeVariable> getScores() {
		Map<String, org.hypothesis.interfaces.Variable<?>> variables = getVariables();
		if (variables != null) {
			for (ExchangeVariable score : scores.values()) {
				score.setVariables(variables);
			}
		}

		return scores;
	}
}
