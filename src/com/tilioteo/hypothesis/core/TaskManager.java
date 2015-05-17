/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.tilioteo.hypothesis.dom.SlideXmlConstants;
import com.tilioteo.hypothesis.dom.XmlUtility;
import com.tilioteo.hypothesis.entity.Branch;
import com.tilioteo.hypothesis.entity.Slide;
import com.tilioteo.hypothesis.entity.Task;
import com.tilioteo.hypothesis.interfaces.Action;
import com.tilioteo.hypothesis.interfaces.ExchangeVariable;
import com.tilioteo.hypothesis.interfaces.HasActions;
import com.tilioteo.hypothesis.interfaces.HasVariables;
import com.tilioteo.hypothesis.processing.ActionMap;
import com.tilioteo.hypothesis.processing.Variable;
import com.tilioteo.hypothesis.processing.VariableMap;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class TaskManager extends ListManager<Branch, Task> implements HasVariables, HasActions {

	private static Logger log = Logger.getLogger(TaskManager.class);

	private TaskFactory taskFactory;

	private VariableMap variables = new VariableMap();
	private ActionMap actions = new ActionMap();

	private Document taskXml = null;
	private Task current = null;
	
	private HashMap<Long, Node> nodes = new HashMap<Long, Node>();
	private HashMap<Long, Map<Integer, ExchangeVariable>> slideOutputs = new HashMap<Long, Map<Integer, ExchangeVariable>>();

	public TaskManager() {
		taskFactory = TaskFactory.getInstance(this);
	}

	public void addNode(Long slideId, Node node) {
		nodes.put(slideId, node);
	}
	
	public void addSlideOutputs(Slide slide, Map<Integer, ExchangeVariable> outputValues) {
		if (!nodes.isEmpty() && slide != null && slide.getId() != null && !outputValues.isEmpty()) {
			// copy map of variables because it will be erased at the slide finish
			HashMap<Integer, ExchangeVariable> map = new HashMap<Integer, ExchangeVariable>();
			for (Integer index : outputValues.keySet()) {
				map.put(index, outputValues.get(index));
			}
			
			slideOutputs.put(slide.getId(), map);
		}
	}
	
	public int getNextSlideIndex(Slide currentSlide) {
		if (current != null && !current.isRandomized()) {
			Node node = nodes.get(currentSlide.getId());
			if (node != null) {
				//clear output values
				for (int i = 1; i <= 10; ++i) {
					node.getVariables().remove("output"+i);
				}
				// add current output values
				Map<Integer, ExchangeVariable> outputs = slideOutputs.get(currentSlide.getId());
				for (Integer index : outputs.keySet()) {
					ExchangeVariable exchangeVariable = outputs.get(index);
					Variable<?> variable = Variable.createVariable("output"+index, exchangeVariable.getValue());
					node.getVariables().put(variable.getName(), variable);
				}
				
				// add Navigator object variable
				node.getVariables().put(SlideXmlConstants.NAVIGATOR,
						taskFactory.createNavigatorObject(node));

				node.execute();
				return node.getNextIndex();
			}
		}
		return -1;
	}

	private void buildTask() {
		log.debug("buildTask");
		clearTaskRelatives();
		Task task = super.current();
		if (current != null && !current.isRandomized()) {
			taskXml = XmlUtility.readString(task.getXmlData());
			taskFactory.createTaskControls();
		}
	}

	private void clearTaskRelatives() {
		log.debug("clearTaskRelatives");

		taskXml = null;

		nodes.clear();
		variables.clear();
		slideOutputs.clear();
	}

	@Override
	public Task current() {
		Task task = super.current();
		if (current != task) {
			current = task;
			
			buildTask();
		}
		
		return current;
	}

	public Document getTaskXml() {
		return taskXml;
	}

	@Override
	public final VariableMap getVariables() {
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
