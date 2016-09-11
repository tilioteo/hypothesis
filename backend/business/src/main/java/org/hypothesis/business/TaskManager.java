/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.business;

import java.util.Map;

import org.apache.log4j.Logger;
import org.hypothesis.builder.TaskBuilder;
import org.hypothesis.data.DocumentReader;
import org.hypothesis.data.XmlDocumentReader;
import org.hypothesis.data.model.Branch;
import org.hypothesis.data.model.Slide;
import org.hypothesis.data.model.Task;
import org.hypothesis.interfaces.ExchangeVariable;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class TaskManager extends ListManager<Branch, Task> {

	private static Logger log = Logger.getLogger(TaskManager.class);

	private DocumentReader reader = new XmlDocumentReader();

	private Task current = null;
	private TaskController controller = null;

	@Override
	public Task current() {
		Task task = super.current();

		if (current != task) {
			current = task;

			if (current != null) {
				buildTaskController();
			} else {
				controller = null;
			}
		}

		return current;
	}

	@Override
	public Task next() {
		super.next();

		return current();
	}

	private void buildTaskController() {
		log.debug("Building task controller.");

		controller = TaskBuilder.buildTaskController(current, reader);
	}

	/**
	 * Add set of slide output variables
	 * 
	 * @param slide
	 *            the slide as origin of outputs
	 * @param outputValues
	 *            map of indexed output variables
	 */
	public void addSlideOutputs(Slide slide, Map<Integer, ExchangeVariable> outputValues) {
		if (controller != null) {
			controller.addSlideOutputs(slide, outputValues);
		}
	}

	/**
	 * Look for node associated with slide and evaluate conditions to get next
	 * slide index.
	 * 
	 * @param slide
	 *            processed slide which is a child of current task
	 * @return index of next slide - value >= 1 means direct index of slide in
	 *         task, 0 means next slide after currently processed, -1 means go
	 *         to next task
	 */
	public int getNextSlideIndex(Slide slide) {
		if (controller != null) {
			controller.getNextSlideIndex(current, slide);
		}

		return 0;
	}

}
