/**
 * 
 */
package com.tilioteo.hypothesis.business;

import java.util.Map;

import org.apache.log4j.Logger;

import com.tilioteo.hypothesis.builder.TaskBuilder;
import com.tilioteo.hypothesis.data.DocumentReader;
import com.tilioteo.hypothesis.data.XmlDocumentReader;
import com.tilioteo.hypothesis.data.model.Branch;
import com.tilioteo.hypothesis.data.model.Slide;
import com.tilioteo.hypothesis.data.model.Task;
import com.tilioteo.hypothesis.interfaces.ExchangeVariable;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class TaskManager extends ListManager<Branch, Task> {

	private static Logger log = Logger.getLogger(TaskManager.class);

	private DocumentReader reader = new XmlDocumentReader();

	private Task current = null;
	private TaskController controller = null;

	public TaskManager() {
		super();
	}

	@Override
	public Task current() {
		Task task = super.current();

		if (current != task) {
			current = task;

			if (current != null) {
				buildTaskController();
			}
		}

		return current;
	}

	private void buildTaskController() {
		log.debug("Building task controller.");

		controller = TaskBuilder.buildTaskController(current, reader);
	}

	public void addSlideOutputs(Slide slide, Map<Integer, ExchangeVariable> outputValues) {
		if (controller != null) {
			controller.addSlideOutputs(slide, outputValues);
		}
	}

	public int getNextSlideIndex(Slide slide) {
		if (controller != null) {
			controller.getNextSlideIndex(current, slide);
		}
		return 0;
	}

}
