/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business.impl;

import java.util.HashMap;
import java.util.Map;

import org.hypothesis.business.ObjectConstants;
import org.hypothesis.business.TaskController;
import org.hypothesis.business.TaskNavigator;
import org.hypothesis.data.model.Slide;
import org.hypothesis.data.model.Task;
import org.hypothesis.evaluation.Node;
import org.hypothesis.interfaces.Action;
import org.hypothesis.interfaces.ExchangeVariable;
import org.hypothesis.interfaces.Variable;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class TaskControllerImpl implements TaskController {

	private Map<Long, Node> nodes = new HashMap<>();

	private Map<String, Variable<?>> variables = new HashMap<>();
	private Map<String, Action> actions = new HashMap<>();

	private Map<Long, Map<Integer, ExchangeVariable>> slideOutputs = new HashMap<>();

	@Override
	public void addNode(Long slideId, Node node) {
		nodes.put(slideId, node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.business.TaskController#addSlideOutputs(org.hypothesis.
	 * data.model.Slide, java.util.Map)
	 */
	@Override
	public void addSlideOutputs(Slide slide, Map<Integer, ExchangeVariable> outputValues) {
		if (!nodes.isEmpty() && slide != null && slide.getId() != null && !outputValues.isEmpty()) {
			// copy map of variables because it will be erased at the slide
			// finish
			HashMap<Integer, ExchangeVariable> map = new HashMap<>();
			outputValues.entrySet().forEach(e -> map.put(e.getKey(), e.getValue()));

			slideOutputs.put(slide.getId(), map);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hypothesis.business.TaskController#getNextSlideIndex(org.hypothesis.
	 * data.model.Task, org.hypothesis.data.model.Slide)
	 */
	@Override
	public int getNextSlideIndex(Task task, Slide slide) {
		if (task != null && !task.isRandomized() && slide != null) {
			Node node = nodes.get(slide.getId());

			if (node != null) {
				// clear output values
				for (int i = 1; i <= 10; ++i) {
					node.getVariables().remove("output" + i);
				}
				// add current output values
				Map<Integer, ExchangeVariable> outputs = slideOutputs.get(slide.getId());
				if (outputs != null) {
					outputs.entrySet().forEach(e -> {
						ExchangeVariable exchangeVariable = e.getValue();
						Variable<?> variable = org.hypothesis.evaluation.Variable.createVariable("output" + e.getKey(),
								exchangeVariable.getValue());
						node.getVariables().put(variable.getName(), variable);
					});
				}

				// add Navigator object variable
				addNavigatorVariable(node);

				node.execute();
				return node.getNextIndex();
			}
		}

		return 0; // 0 for next slide in task
	}

	private void addNavigatorVariable(Node node) {
		Variable<Object> variable = new org.hypothesis.evaluation.Variable<Object>(ObjectConstants.NAVIGATOR,
				new TaskNavigator(node));
		node.getVariables().put(variable.getName(), variable);
	}

	@Override
	public final Map<String, Variable<?>> getVariables() {
		return variables;
	}

	@Override
	public void setAction(String id, Action action) {
		actions.put(id, action);
	}

	@Override
	public Action getAction(String id) {
		return actions.get(id);
	}

}
