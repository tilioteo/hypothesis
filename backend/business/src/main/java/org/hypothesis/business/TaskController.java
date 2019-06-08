/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.hypothesis.data.dto.SlideDto;
import org.hypothesis.data.dto.TaskDto;
import org.hypothesis.evaluation.Node;
import org.hypothesis.interfaces.Action;
import org.hypothesis.interfaces.Evaluator;
import org.hypothesis.interfaces.ExchangeVariable;
import org.hypothesis.interfaces.Variable;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class TaskController implements Serializable, Evaluator {

	private final HashMap<Long, Node> nodes = new HashMap<>();

	private final HashMap<String, Variable<?>> variables = new HashMap<>();
	private final HashMap<String, Action> actions = new HashMap<>();

	private final HashMap<Long, Map<Integer, ExchangeVariable>> slideOutputs = new HashMap<>();

	public TaskController() {

	}

	public void addNode(Long slideId, Node node) {
		nodes.put(slideId, node);
	}

	public void addSlideOutputs(SlideDto slide, Map<Integer, ExchangeVariable> outputValues) {
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

	public int getNextSlideIndex(TaskDto task, SlideDto slide) {
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
						Variable<?> variable = org.hypothesis.evaluation.Variable.createVariable("output" + index,
								exchangeVariable.getValue());
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
