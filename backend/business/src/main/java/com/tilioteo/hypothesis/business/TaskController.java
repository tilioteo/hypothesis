/**
 * 
 */
package com.tilioteo.hypothesis.business;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.tilioteo.hypothesis.data.model.Slide;
import com.tilioteo.hypothesis.data.model.Task;
import com.tilioteo.hypothesis.evaluation.Node;
import com.tilioteo.hypothesis.interfaces.Action;
import com.tilioteo.hypothesis.interfaces.Evaluator;
import com.tilioteo.hypothesis.interfaces.ExchangeVariable;
import com.tilioteo.hypothesis.interfaces.Variable;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class TaskController implements Serializable, Evaluator {

	private HashMap<Long, Node> nodes = new HashMap<>();

	private HashMap<String, Variable<?>> variables = new HashMap<>();
	private HashMap<String, Action> actions = new HashMap<>();

	private HashMap<Long, Map<Integer, ExchangeVariable>> slideOutputs = new HashMap<>();

	public TaskController() {

	}

	public void addNode(Long slideId, Node node) {
		nodes.put(slideId, node);
	}

	public void addSlideOutputs(Slide slide, Map<Integer, ExchangeVariable> outputValues) {
		if (!nodes.isEmpty() && slide != null && slide.getId() != null && !outputValues.isEmpty()) {
			// copy map of variables because it will be erased at the slide
			// finish
			HashMap<Integer, ExchangeVariable> map = new HashMap<>();
			for (Integer index : outputValues.keySet()) {
				map.put(index, outputValues.get(index));
			}

			slideOutputs.put(slide.getId(), map);
		}
	}

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
					for (Integer index : outputs.keySet()) {
						ExchangeVariable exchangeVariable = outputs.get(index);
						Variable<?> variable = com.tilioteo.hypothesis.evaluation.Variable
								.createVariable("output" + index, exchangeVariable.getValue());
						node.getVariables().put(variable.getName(), variable);
					}
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
		Variable<Object> variable = new com.tilioteo.hypothesis.evaluation.Variable<Object>(ObjectConstants.NAVIGATOR,
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
