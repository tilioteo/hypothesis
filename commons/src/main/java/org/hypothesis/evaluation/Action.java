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
	private final List<Evaluable> evaluables = new ArrayList<>();

	/**
	 * Construct
	 * 
	 * @param variables
	 * @param id
	 */
	public Action(HasVariables variables, String id) {
		super(variables, id);
	}

	/**
	 * Add evaluable
	 * 
	 * @param evaluable
	 */
	public void add(Evaluable evaluable) {
		evaluables.add(evaluable);
	}

	@Override
	public void execute() {
		super.execute();

		evaluables.forEach(e -> {
			Map<String, org.hypothesis.interfaces.Variable<?>> variables = getVariables();

			if (variables != null) {
				e.setVariables(variables);
			}
			e.evaluate();
			if (variables != null) {
				e.updateVariables(variables);
			}
		});
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder(getId() + "() {\n");
		evaluables.forEach(e -> {
			builder.append("\t" + e.toString() + ";\n");
		});
		builder.append("}");

		return builder.toString();
	}

	@Override
	public Map<Integer, ExchangeVariable> getOutputs() {
		Map<String, org.hypothesis.interfaces.Variable<?>> variables = getVariables();
		if (variables != null) {
			outputValues.values().forEach(e -> e.setVariables(variables));
		}

		return outputValues;
	}
}
